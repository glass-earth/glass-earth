package main

import (
	"fmt"
	"net/http"

	"code.google.com/p/go.net/websocket"
)

type WSConn struct {
	id          int
	role        string
	channelName string
	status      string

	C  *websocket.Conn
	ch chan *Message
}

func (w *WSConn) Id() int {
	return w.id
}

func (w *WSConn) Role() string {
	return w.role
}

func (w *WSConn) ChannelName() string {
	return w.channelName
}
func (w *WSConn) Status() string {
	return w.status
}

func (w *WSConn) Channel() *Channel {
	return channelManager.GetChannel(w.channelName)
}

func (w *WSConn) Close() {
	defer catch()

	w.C.Close()
	close(w.ch)
	w.status = statusDisconnected
}

func (w *WSConn) Send(msg *Message) {
	if w.status != statusReady {
		logE("SendMessage: status not ready", w, msg)
		return
	}
	w.ch <- msg
}

func (w *WSConn) String() string {
	return fmt.Sprint("WSConn:", w.channelName, "/", w.id, ".", w.status)
}

func (w *WSConn) StatusReady(id int, role string, channelName string) {
	if w.status == statusConnecting {
		w.id = id
		w.channelName = channelName
		w.status = statusReady
		w.role = role

	} else {
		logE("WSConn: Unexpected", MsgTypeHandshakeAccept)
	}
}

func (w *WSConn) HandleMessage(msg *Message) {
	// TODO: handle some special message
}

func (w *WSConn) reader() {

	logI("WS New Connection", w)
	defer func() {
		catch()
		logI("WS Connection closed", w)
		w.Close()
		w.Channel().unregister(w)
	}()

	for {
		w.status = statusConnecting
		var s string
		err := websocket.Message.Receive(w.C, &s)
		if err != nil {
			logE("WSError:", err)
			break
		}

		logV("WSRead:", s)
		msg, err := UnmarshalMessage([]byte(s))
		if err != nil {
			logE("WSRead Marshal", err)

		} else {
			HandleMessage(w, msg)
		}
	}
}

func (w *WSConn) writer() {
	defer catch()

	for msg := range w.ch {
		logV("WSSend:", msg)

		data, err := msg.Marshal()
		if err != nil {
			logE("WSSend:", err)
			continue
		}
		err = websocket.Message.Send(w.C, string(data))
		if err != nil {
			logE("SendMessage:", err)
		}
	}
}

func wsHandler(ws *websocket.Conn) {
	conn := new(WSConn)
	conn.C = ws
	conn.status = statusConnecting
	conn.ch = make(chan *Message)
	go conn.reader()
	conn.writer()
}

func setupWebSocket() {
	http.Handle("/ws/", websocket.Handler(wsHandler))
}
