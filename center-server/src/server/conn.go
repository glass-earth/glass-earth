package main

import (
	"bufio"
	"fmt"
	"net"
)

const (
	statusNotInit      = "NotInit"
	statusConnecting   = "Connecting"
	statusReady        = "Ready"
	statusDisconnected = "Disconnected"
	statusDrop         = "Drop"
)

type Conn struct {
	C net.Conn

	id          int
	role        string
	channelName string
	status      string

	scanner *bufio.Scanner
	ch      chan *Message
}

func NewConn(conn net.Conn) *Conn {
	c := new(Conn)
	c.C = conn
	c.status = statusNotInit

	return c
}

func (c *Conn) Id() int {
	return c.id
}

func (c *Conn) Role() string {
	return c.role
}

func (c *Conn) ChannelName() string {
	return c.channelName
}

func (c *Conn) Status() string {
	return c.status
}

func (c *Conn) String() string {
	return fmt.Sprint("Conn:", c.channelName, "/", c.id, ".", c.status)
}

func (c *Conn) Start() {
	c.status = statusConnecting
	c.ch = make(chan *Message)

	go c.reader()
	c.writer()
}

func (c *Conn) Channel() *Channel {
	return channelManager.GetChannel(c.channelName)
}

func (c *Conn) Close() {
	defer catch()

	close(c.ch)
	// TODO: Drop
	if c.status != statusDrop {
		c.status = statusDisconnected
	}
}

func (c *Conn) Send(msg *Message) {
	if c.status != statusReady {
		logE("SendMessage: status not ready", c.status, msg)
		return
	}

	c.ch <- msg
}

func (c *Conn) StatusReady(id int, role string, channelName string) {
	if c.status == statusConnecting {
		c.id = id
		c.role = role
		c.channelName = channelName
		c.status = statusReady

	} else {
		logE("Conn: Unexpect ready", id, role, channelName)
	}
}

func (c *Conn) HandleMessage(msg *Message) {
	// TODO
}

func (c *Conn) reader() {

	logI("New connection", c)
	scanner := bufio.NewScanner(c.C)
	defer func() {
		catch()
		logI("Connection closed", c)
		c.Close()
		c.Channel().unregister(c)
	}()

	for scanner.Scan() {

		msg, err := UnmarshalMessage(scanner.Bytes())

		logV("Text:", scanner.Text())
		logV("Msg:", msg)

		if err != nil {
			logE("Read:", err)
			continue
		}

		HandleMessage(c, msg)
	}

	if err := scanner.Err(); err != nil {
		logE("Scanner:", err)
	}
}

func (c *Conn) writer() {

	defer catch()
	defer c.C.Close()

	for msg := range c.ch {
		logV("SendMessage:", msg)

		data, err := msg.Marshal()
		if err != nil {
			logE("SendMessage:", err)
			continue
		}

		_, err = c.C.Write(data)
		c.C.Write([]byte("\r\n"))
		if err != nil {
			logE("SendMessage:", err)
		}
	}
}
