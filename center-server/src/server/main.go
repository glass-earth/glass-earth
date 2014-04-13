package main

import (
	"errors"
	"flag"
	"fmt"
	"log"
	"net"
	"runtime/debug"
)

var (
	flTcpPort  = flag.Int("tcp-port", 50505, "TCP Port")
	flHttpPort = flag.Int("http-port", 8080, "HTTP Port")
	flError    = flag.Bool("error", false, "Enable random error")
	flPublic   = flag.String("public", "public", "Public directory")
	flHost     = flag.String("host", "192.168.1.102", "Server IP")
)

func logIE(msg string, err error) {
	if err != nil {
		logE(msg, err)
	}
}

func logE(msg ...interface{}) {
	log.Print("[Error] " + fmt.Sprintln(msg...))
	debug.PrintStack()
}

func logI(msg ...interface{}) {
	log.Print("[I] " + fmt.Sprintln(msg...))
}

func logW(msg ...interface{}) {
	log.Print("[Warn] " + fmt.Sprintln(msg...))
}

func logV(msg ...interface{}) {
	log.Print("[V] " + fmt.Sprintln(msg...))
}

func expect(condition bool, text string) {
	if !condition {
		panic(errors.New(text))
	}
}

func catch() {
	err := recover()
	if err != nil {
		logE("Panic", err)
		debug.PrintStack()
	}
}

func xor(a, b bool) bool {
	return a && !b || !a && b
}

func config() {
	flag.Parse()
	if (*flPublic)[len(*flPublic)-1] == '/' {
		*flPublic = (*flPublic)[:len(*flPublic)-1]
	}

	configGraph()
}

func startTCPServer() {
	ln, err := net.Listen("tcp", fmt.Sprint(":", *flTcpPort))
	if err != nil {
		logE("Server", err)
		return
	}

	logI("TCP Server is listening on", *flTcpPort)

	for {
		conn, err := ln.Accept()
		if err != nil {
			logE("Connection", err)
			continue
		}

		c := NewConn(conn)
		go c.Start()
	}
}

func main() {
	config()
	setupWebSocket()
	go startWebServer()
	startTCPServer()
}
