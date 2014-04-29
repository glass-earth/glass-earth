package main

import (
	"fmt"
	"log"
	"net"
	"reflect"
	// "runtime/debug"
	"sync"
	"testing"
	"time"
)

type ConnMock struct {
	C net.Conn

	id          int
	role        string
	channelName string
	status      string

	recvMsgs []*Message
	sendMsgs []*Message
	ch       chan *Message
	mutex    sync.Mutex
}

func logT(msg ...interface{}) {
	log.Print("[_] " + fmt.Sprintln(msg...))
}

func NewConnMock() *ConnMock {
	c := new(ConnMock)
	c.status = statusNotInit
	c.recvMsgs = make([]*Message, 10)[:0]
	c.sendMsgs = make([]*Message, 10)[:0]
	c.ch = make(chan *Message, 5)

	return c
}

func (c *ConnMock) Id() int {
	return c.id
}

func (c *ConnMock) Role() string {
	return c.role
}

func (c *ConnMock) ChannelName() string {
	return c.channelName
}

func (c *ConnMock) Status() string {
	return c.status
}

func (c *ConnMock) String() string {
	return fmt.Sprint("Conn:", c.channelName, "/", c.role, ".", c.id, ".", c.status)
}

func (c *ConnMock) Start() {
	c.status = statusConnecting
}

func (c *ConnMock) Channel() *Channel {
	return channelManager.GetChannel(c.channelName)
}

func (c *ConnMock) Close() {
	logT("Close")
}

func (c *ConnMock) Send(msg *Message) {
	c.mutex.Lock()
	c.sendMsgs = append(c.sendMsgs, msg)
	c.mutex.Unlock()
	logT("Send", msg)
	c.ch <- msg
}

func (c *ConnMock) StatusReady(id int, role string, channelName string) {
	c.id = id
	c.role = role
	c.channelName = channelName
	c.status = statusReady
	logT("StatusReady", id, role, channelName, c.String())
	// debug.PrintStack()
}

func (c *ConnMock) HandleMessage(msg *Message) {
	logT("HandleMessage", msg)
}

func (c *ConnMock) lastRecvMsg(n int) *Message {
	if n < 0 || n >= len(c.recvMsgs) {
		panic("Invalid position")
	}
	return c.recvMsgs[n]
}

func (c *ConnMock) lastSendMsg(n int) *Message {
	if n < 0 || n >= len(c.recvMsgs) {
		panic("Invalid position")
	}
	return c.sendMsgs[n]
}

func (c *ConnMock) receive(msg *Message) {
	c.recvMsgs = append(c.recvMsgs, msg)

	HandleMessage(c, msg)
}

func (c *ConnMock) wait(fn func(*Message)) {
	timeout := time.After(time.Second)
	select {
	case msg := <-c.ch:
		fn(msg)
	case _ = <-timeout:
		panic("Timeout " + c.String())
	}
}

func (c *ConnMock) waitN(n int, fn func([]*Message)) {
	timeout := time.After(time.Second)
	msgs := make([]*Message, n)
	for i := 0; i < n; i++ {
		select {
		case msgs[i] = <-c.ch:

		case _ = <-timeout:
			panic("Timeout " + c.String())
		}
	}
	fn(msgs)
}

func exp(T *testing.T, condition bool, msg ...interface{}) {
	if !condition {
		T.Error(msg...)
	}
}

func expEq(T *testing.T, value, expected interface{}, msg ...interface{}) {
	if !reflect.DeepEqual(value, expected) {
		T.Errorf("%vGot `%v`, expect `%v`", fmt.Sprintln(msg...), value, expected)
	}
}

func TestChannel(T *testing.T) {

	isVerbose = true

	testChannel := "test-channel"
	cm := channelManager
	channel := cm.GetChannel(testChannel)

	// Test creating channel
	exp(T, channel != nil, "Channel must be not nil")
	expEq(T, channel.Name(), testChannel, "Channel name must be "+testChannel)

	// Test creating new id
	ids := make(map[int]bool)
	for i := 0; i < 10; i++ {
		id := channel.getNewId()
		ids[id] = true
		exp(T, id >= 10000, "Expect id greater than 10000")
	}
	expEq(T, len(ids), 10, "getNewId() must return different ids")

	app := NewConnMock()
	ctrl := NewConnMock()
	web := NewConnMock()
	leap := NewConnMock()

	// Test handshake
	app.Start()
	app.receive(&Message{
		Type:        MsgTypeHandshakeConnect,
		Role:        MsgRoleApp,
		ChannelName: testChannel,
	})

	ctrl.Start()
	ctrl.receive(&Message{
		Type:        MsgTypeHandshakeConnect,
		Role:        MsgRoleController,
		ChannelName: testChannel,
	})

	web.Start()
	web.receive(&Message{
		Type:        MsgTypeHandshakeConnect,
		Role:        MsgRoleGuest,
		ChannelName: testChannel,
	})

	leap.Start()
	leap.receive(&Message{
		Type:        MsgTypeHandshakeConnect,
		Role:        MsgRoleLeap,
		ChannelName: testChannel,
	})

	fnAccept := func(conn IConn) func(*Message) {
		return func(msg *Message) {
			expEq(T, msg.Type, MsgTypeHandshakeAccept, "Expect accept")
			exp(T, msg.PeerId >= 10000, "Expect peer_id", msg.PeerId)
			expEq(T, msg.Role, MsgRoleServer, "Expect role")
			expEq(T, msg.ChannelName, testChannel, "Expect channel_name")
		}
	}

	app.wait(fnAccept(app))
	ctrl.wait(fnAccept(ctrl))
	web.wait(fnAccept(web))
	leap.wait(fnAccept(leap))

	/*
		// Test story list
		ctrl.receive(&Message{
			Type: MsgTypeStoryList,
			To:   MsgRoleServer,
		})
		ctrl.wait(func(msg *Message) {

		})

		ctrl.receive(&Message{
			Type: MsgTypeStoryInfo,
			To:   MsgRoleServer,
		})
		ctrl.wait(func(msg *Message) {

		})

		// Switch story

		ctrl.receive(&Message{
			Type: MsgTypeStorySwitch,
			Data: map[string]interface{}{
				story_name: "colorado-windfire",
			},
		})
		ctrl.waitN(2, func(msgs []*Message) {

		})

		// Test story info
		ctrl.receive(&Message{
			Type: MsgTypeStoryInfo,
		})

		// Test graph switch
		ctrl.receive(&Message{
			Type: MsgTypeGraphSwitch,
		})

		// Test graph info
		ctrl.receive(&Message{
			Type: MsgTypeGraphInfo,
		})

		// Test graph state
		app.receive(&Message{
			Type: MsgTypeAppState,
			// TODO
		})

		ctrl.wait(func(msg *Message) {
			// TODO
		})
	*/

	// Test leap
	leap.receive(&Message{
		Type:  MsgTypeLeapGestureSwipe,
		State: "up",
	})

	app.wait(func(msg *Message) {
		expEq(T, msg.Type, MsgTypeGraphWordmap, "Expect translated to graph/worldmap")
		d := msg.Data.(map[string]interface{})
		expEq(T, d["enabled"], true, "Expect worldmap enabled")
	})

	leap.receive(&Message{
		Type:  MsgTypeLeapGestureSwipe,
		State: "down",
	})

	app.wait(func(msg *Message) {
		expEq(T, msg.Type, MsgTypeGraphWordmap, "Expect translated to graph/worldmap")
		d := msg.Data.(map[string]interface{})
		expEq(T, d["enabled"], false, "Expect worldmap disabled")
	})

	expEq(T, channel.story.StoryName, "all", "Expect active story all")
	expEq(T, channel.activeGraph, "default", "Expect active graph default")

	leap.receive(&Message{
		Type:  MsgTypeLeapGestureSwipe,
		State: "right",
	})

	app.wait(func(msg *Message) {
		expEq(T, msg.Type, MsgTypeGraphSwitch, "Expect translated to graph/switch")
		d := msg.Data.(map[string]interface{})
		expEq(T, d["graph_name"], "land_temp", "Expect translated to graph/switch")
	})

	ctrl.wait(func(msg *Message) {
		expEq(T, msg.Type, MsgTypeGraphInfo, "Expect graph/info")
		d := msg.Data.(*MsgGraphInfo)
		expEq(T, d.GraphName, "land_temp", "Expected graph/info.graph_name")
	})

	expEq(T, channel.state.EarthRotation, 1.0, "Expect rotation init to 1.0")

	leap.receive(&Message{
		Type:  MsgTypeLeapGestureSwipe,
		State: "left",
	})

	app.wait(func(msg *Message) {
		expEq(T, msg.Type, MsgTypeEarthRotate, "Expect translated to earth/rotate")
		d := msg.Data.(map[string]interface{})
		expEq(T, d["velocity"], 0.0, "Expect rotation 0")
	})

	leap.receive(&Message{
		Type:  MsgTypeLeapGestureSwipe,
		State: "left",
	})

	app.wait(func(msg *Message) {
		expEq(T, msg.Type, MsgTypeEarthRotate, "Expect translated to earth/rotate")
		d := msg.Data.(map[string]interface{})
		expEq(T, d["velocity"], 1.0, "Expect rotation 1")
	})
}
