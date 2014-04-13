package main

import (
	"encoding/json"
	"strings"
)

const (
	MsgTypeHandshake          = "handshake/"
	MsgTypeHandshakeConnect   = "handshake/connect"
	MsgTypeHandshakeReconnect = "handshake/reconnect"
	MsgTypeHandshakeClose     = "handshake/close"
	MsgTypeHandshakeError     = "handshake/error"
	MsgTypeHandshakeAccept    = "handshake/accept"
	MsgTypeHandshakePeers     = "handshake/peers"

	MsgTypeGraphInfo   = "graph/info"
	MsgTypeGraphList   = "graph/list"
	MsgTypeGraphSwitch = "graph/switch"

	MsgTypeAppState = "app/state"

	MsgTypeEarthTimeAnimation = "earth/time_animation"
	MsgTypeEarthRotate        = "earth/rotate"
	MsgTypeEarthMoveTo        = "earth/move_to"
	MsgTypeEarthReset         = "earth/reset"

	MsgTypeLeapPointable = "leap/pointable"

	MsgTypeAck = "ack"

	MsgRoleServer     = "server"
	MsgRoleApp        = "app"
	MsgRoleController = "controller"
	MsgRoleLeap       = "leap"
	MsgRoleGuest      = "guest"
	MsgToChannel      = "channel"
	MsgToChannelGuest = "channel_guest"
)

type Message struct {
	Type string `json:"type"`
	Role string `json:"role"`

	ChannelName string `json:"channel_name,omitempty"`
	Description string `json:"description,omitempty"`
	FromId      int    `json:"from_id,omitempty"`
	PeerId      int    `json:"peer_id,omitempty"`
	To          string `json:"to,omitempty"`
	ToId        int    `json:"to_id,omitempty"`

	Peers []MsgPeer `json:"peers,omitempty"`

	RawData json.RawMessage `json:"data,omitempty"`
	Data    interface{}     `json:"-"`
}

type MsgPeer struct {
	Id     int    `json:"id"`
	Role   string `json:"role"`
	Status string `json:"status"`
}

type MsgState struct {
	GraphName          string  `json:"graph_name"`
	EarthRotation      float64 `json:"earth_rotation"`
	EarthVeocity       float64 `json:"earth_velocity"`
	EarthTimeAnimating bool    `json:"earth_time_animating"`
	EarthTimeStart     MsgDate `json:"earth_time_start"`
	EarthTimeEnd       MsgDate `json:"earth_time_end"`
}

type MsgEvent struct {
	Label string  `json:"label"`
	Name  string  `json:"name"`
	Time  MsgDate `json:"time"`
}

type MsgGraphInfo struct {
	GraphLabel           string `json:"graph_label"`
	GraphName            string `json:"graph_name"`
	GuestUrl             string `json:"guest_url"`
	GuestContentUrl      string `json:"guest_content_url"`
	ControllerUrl        string `json:"controller_url"`
	ControllerContentUrl string `json:"controller_content_url"`

	Events []MsgEvent `json:"events"`
}

type MsgDate string

func (m *Message) Marshal() ([]byte, error) {
	var err error

	if m.Data != nil {
		m.RawData, err = json.Marshal(m.Data)
	}

	if err != nil {
		return nil, err
	}

	return json.Marshal(m)
}

func SplitMessage(msgType string) (string, string) {
	a := strings.Split(msgType, "/")
	if len(a) == 2 {
		return a[0], a[1]
	}

	logE("SplitMessage:", msgType)
	return "", ""
}

func UnmarshalMessage(data []byte) (*Message, error) {
	var m Message
	err := json.Unmarshal(data, &m)

	if len(m.RawData) > 0 {
		err2 := json.Unmarshal(m.RawData, &m.Data)
		if err == nil {
			err = err2
		}
	}

	return &m, err
}

func (m *Message) String() string {
	data, _ := m.Marshal()
	return string(data)
}
