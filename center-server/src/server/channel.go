package main

import (
	"errors"
	"fmt"
	"math/rand"
	"strings"
	"sync"
	"time"
)

// ChannelManager
type ChannelManager struct {
	channels map[string]*Channel
	mutex    sync.Mutex
}

var channelManager = ChannelManager{
	channels: make(map[string]*Channel),
}

func (cm *ChannelManager) GetChannel(name string) *Channel {

	channel, ok := cm.channels[name]
	if !ok {
		logV("Create new channel:", name)

		// We must synchronize creating channel
		cm.mutex.Lock()
		defer cm.mutex.Unlock()

		channel = NewChannel(name)
		cm.channels[name] = channel

		go channel.Start()
	}

	return channel
}

func (cm *ChannelManager) RemoveChannel(name string) {
	cm.mutex.Lock()
	defer cm.mutex.Unlock()

	delete(cm.channels, name)
}

type Channel struct {
	name      string
	conns     map[int]IConn
	state     MsgState
	graphName string

	ch chan ChannelMessage
	// regCh   chan IConn
	// unregCh chan IConn
	closeCh chan bool

	isClosed bool
	mutex    sync.Mutex
}

type IConn interface {
	Id() int
	Role() string
	ChannelName() string
	Channel() *Channel
	Status() string
	Send(msg *Message)

	// Start()                                 // status: NotInit -> Connecting
	Close()                                              // status: Ready -> Disconnected
	StatusReady(id int, role string, channelName string) // status: Connecting -> Ready
	HandleMessage(msg *Message)
}

type ChannelMessage struct {
	conn IConn
	msg  *Message
}

func NewChannel(name string) *Channel {
	c := new(Channel)
	c.name = name
	c.conns = make(map[int]IConn)
	c.ch = make(chan ChannelMessage)
	// c.regCh = make(chan IConn)
	// c.unregCh = make(chan IConn)
	c.closeCh = make(chan bool)

	return c
}

// Handle first message handshake/accept to get channelName
// Then delegate to channel.HandleMessage()
func HandleMessage(conn IConn, msg *Message) {

	defer func() {
		err := recover()
		if err != nil {
			logE("HandleMessage:", err)
		}
	}()

	if conn.Status() == statusConnecting {
		if msg.Type == MsgTypeHandshakeConnect || msg.Type == MsgTypeHandshakeReconnect {
			channelName := msg.ChannelName
			if channelName == "" {
				logE("HandleMessage: ChannelName empty", msg)
				conn.Send(&Message{
					Type:        MsgTypeHandshakeError,
					Role:        MsgRoleServer,
					Description: "HandleMessage: ChannelName empty " + msg.String(),
				})
				conn.Close()
				return
			}

			logV("HandleMessage", conn, msg)
			channel := channelManager.GetChannel(channelName)
			channel.HandleMessage(conn, msg)

		} else {
			logE("HandleMessage: Expect", MsgTypeHandshakeConnect, "or", MsgTypeHandshakeReconnect)
			conn.Close()
			return
		}

	} else if conn.Status() == statusReady {
		conn.Channel().HandleMessage(conn, msg)

	} else {
		logE("HandleMessage: Status not ok", conn.Status())
	}
}

func (c *Channel) String() string {
	return "Channel:" + c.name
}

func (c *Channel) getNewId() int {

	var id int
	for {
		id = 10000 + rand.Intn(90000)
		_, ok := c.conns[id]
		if !ok {
			break
		}
	}

	return id
}

func (c *Channel) Start() {

	defer catch()
	logV("Channel is running")

	for {
		// logV("channel waiting")
		select {
		// case conn := <-c.regCh:
		// 	logV("Accept connection:", conn.Id(), conn.Role(), conn.ChannelName())
		// 	c.conns[conn.Id()] = conn
		// 	c.broadcastPeers()

		// case conn := <-c.unregCh:
		// 	logV("Close connection")
		// 	delete(c.conns, conn.Id())
		// 	c.broadcastPeers()

		case _ = <-c.closeCh:
			logV("Close channel")
			close(c.closeCh)
			// close(c.unregCh)
			// close(c.regCh)
			close(c.ch)

			c.isClosed = true
			channelManager.RemoveChannel(c.name)
			return

		case cMsg := <-c.ch:

			logV("Handling Message", cMsg.conn, cMsg.msg)

			if strings.HasPrefix(cMsg.msg.Type, "handshake") {
				c.handleHandshake(cMsg.conn, cMsg.msg)

			} else {
				sent := c.handleControl(cMsg.conn, cMsg.msg)
				if !sent {
					ackMsg := &Message{
						Type: MsgTypeAck,
					}
					cMsg.conn.Send(ackMsg)
				}
			}

		default:
			time.Sleep(100 * time.Millisecond)
		}
	}
}

func (c *Channel) HandleMessage(conn IConn, msg *Message) {
	c.ch <- ChannelMessage{conn, msg}
}

func (c *Channel) Close() {
	c.closeCh <- true
}

func (c *Channel) register(conn IConn) {
	logV("Accept connection:", conn)
	c.conns[conn.Id()] = conn
	c.broadcastPeers()
}

// Do not call in channel.go, must be called by Conn
func (c *Channel) unregister(conn IConn) {
	if c == nil {
		return
	}

	logV("Remove connection:", conn)
	delete(c.conns, conn.Id())
	c.broadcastPeers()
}

func (c *Channel) handleHandshake(conn IConn, msg *Message) {

	defer func() {
		err := recover()
		if err != nil {
			logE("HandleHandshake:", err)

			conn.Send(&Message{
				Type:        MsgTypeHandshakeError,
				Role:        MsgRoleServer,
				Description: fmt.Sprint(err),
			})
		}
	}()

	switch msg.Type {
	case MsgTypeHandshakeConnect, MsgTypeHandshakeReconnect:
		logV("Receive:", MsgTypeHandshakeConnect)

		expect(conn.Status() == statusConnecting, conn.Status()+" Expect handshake/connect is first message (status must be "+statusConnecting+")")

		peerId := msg.PeerId
		if msg.Type == MsgTypeHandshakeConnect {
			peerId = c.getNewId()
		}

		if msg.Type == MsgTypeHandshakeReconnect {
			oldConn := c.conns[msg.PeerId]
			expect(oldConn != nil, "Expect handshake/reconnect has a valid id")
			expect(oldConn.Status() == statusDrop || oldConn.Status() == statusDisconnected, "Expect handshake/reconnect resume from dropped connection")
		}

		conn.StatusReady(peerId, msg.Role, msg.ChannelName)

		var resMsg = &Message{
			Type:   MsgTypeHandshakeAccept,
			Role:   MsgRoleServer,
			PeerId: peerId,
		}

		if *flError && rand.Intn(2) == 1 {
			resMsg = &Message{
				Type:        MsgTypeHandshakeError,
				Role:        MsgRoleServer,
				Description: "Random error",
			}
		}

		conn.Send(resMsg)
		c.register(conn)
		logV("Role", conn.Role(), msg.Role)
		if conn.Role() == MsgRoleGuest {
			logV("Send graph/info")
			c.sendGraphInfo(conn)
		}
		logV("Send to register", conn)

	case MsgTypeHandshakeClose:
		logV("Receive:", MsgTypeHandshakeClose)
		conn.Close()

	default:
		logE("Receive invalid message type:", msg.Type)
		panic("Receive invalid message type: " + msg.Type)

		// c.CloseConn(conn)
	}
}

func (c *Channel) handleControl(conn IConn, msg *Message) bool {

	msg.FromId = conn.Id()
	msg.Role = conn.Role()

	// Handle some special messages
	switch msg.Type {
	case MsgTypeGraphList:
		resMsg := &Message{
			Type: MsgTypeGraphList,
			Role: MsgRoleServer,
			Data: map[string]interface{}{
				"graph_names": configListGraph,
			},
		}
		conn.Send(resMsg)
		return true

	case MsgTypeGraphInfo:
		c.sendGraphInfo(conn)
		return true

	case MsgTypeAppState:
		// TODO
		// c.state = m
		c.Broadcast(conn.Id(), msg)
		return false

	case MsgTypeGraphSwitch:

		data := msg.Data.(map[string]interface{})
		graphName := data["graph_name"].(string)
		c.graphName = graphName

		c.sendGraphInfo(conn)
		c.handleForward(conn, msg)
		return true

	case MsgTypeLeapPointable:
		fMsg := HandleLeapMessage(msg)
		logV("leap", fMsg)
		if fMsg != nil {
			c.handleForward(conn, fMsg)
		}
		return false

	default:
		c.handleForward(conn, msg)
		return false
	}
}

func (c *Channel) handleForward(conn IConn, msg *Message) {
	logV("Forward:", msg)

	if (msg.To != MsgRoleServer && msg.To != "") || msg.ToId > 0 {
		err := c.Broadcast(conn.Id(), msg)
		if err != nil {
			logE("HandleForward:", err)
			conn.Send(&Message{
				Type:        MsgTypeHandshakeError,
				Role:        MsgRoleServer,
				Description: fmt.Sprint(err),
			})
		}

	} else {
		logE("Forward: Expect to or to_id")
	}
}

// conn nil to broadcast
func (c *Channel) sendGraphInfo(conn IConn) {

	defaultGraphInfo := configGraphInfo[c.graphName]
	if defaultGraphInfo == nil {
		logI("GraphName not exist", c.graphName)
		c.graphName = configDefaultGraph
		defaultGraphInfo = configGraphInfo[c.graphName]
		conn = nil // broadcast
	}

	origin := fmt.Sprint("http://"+*flHost+":", *flHttpPort)

	graphInfo := &MsgGraphInfo{
		GraphName:            c.graphName,
		GraphLabel:           defaultGraphInfo.GraphLabel,
		GuestUrl:             origin + configPathChannel + c.name,
		GuestContentUrl:      origin + configPathGraph + c.graphName,
		ControllerUrl:        origin + configPathCtrlChannel + c.name,
		ControllerContentUrl: origin + configPathCtrlGraph + c.graphName,
	}

	resMsg := &Message{
		Type: MsgTypeGraphInfo,
		Role: MsgRoleServer,
		Data: graphInfo,
	}

	if conn == nil {
		resMsg.To = MsgToChannelGuest
		c.Broadcast(0, resMsg)

	} else {
		conn.Send(resMsg)
	}
}

func (c *Channel) broadcastPeers() {

	// resMsg := &Message{
	// 	Type:        MsgTypeHandshakePeers,
	// 	Role:        MsgRoleServer,
	// 	ChannelName: c.name,
	// }

	// resMsg.Peers = make([]MsgPeer, len(c.conns))[:0]
	// for id, conn := range c.conns {
	// 	resMsg.Peers = append(resMsg.Peers, MsgPeer{
	// 		Id:     id,
	// 		Role:   conn.Role(),
	// 		Status: conn.Status(),
	// 	})
	// }

	// logV("BroadcastPeers:", resMsg.Peers, resMsg)

	// for _, conn := range c.conns {
	// 	if conn != nil {
	// 		conn.Send(resMsg)
	// 	}
	// }
}

func (c *Channel) Broadcast(fromId int, msg *Message) (err interface{}) {

	defer func() {
		err = recover()
		if err != nil {
			logE("Broadcast:", err)
		}
	}()

	if msg.ToId == 0 && msg.To == "" {
		msg.To = MsgToChannel
	}

	if msg.ToId > 0 {
		toConn := c.conns[msg.ToId]
		expect(toConn != nil, "`ToId` does not exist")
		toConn.Send(msg)
		return nil
	}

	switch msg.To {
	case MsgRoleApp, MsgRoleController, MsgRoleGuest:
		for id, conn := range c.conns {
			if id != fromId && msg.To == conn.Role() {
				conn.Send(msg)
			}
		}

		// Never broadcast to leap & controller
	case MsgToChannel:
		for id, conn := range c.conns {
			if id != fromId && (conn.Role() == MsgRoleApp) {
				conn.Send(msg)
			}
		}

		// Never broadcast to leap & controller
	case MsgToChannelGuest:
		for id, conn := range c.conns {
			if id != fromId && (conn.Role() == MsgRoleApp || conn.Role() == MsgRoleGuest) {
				conn.Send(msg)
			}
		}

	default:
		panic(errors.New("Expect `to` has a valid value (controller, app, leap, channel)"))
	}

	return nil
}
