package main

const (
	LeapTypeFinger = "finger"
	LeapTypeTool   = "tool"
)

type (
	Vector3 struct {
		x float64
		y float64
		z float64
	}

	LeapPointables struct {
		list []*LeapPointable
	}

	LeapPointable struct {
		tipPosition Vector3
		length      float64
		width       float64
		direction   Vector3
		tipVelocity Vector3
		type_       string
		id          int
		frame_id    int
	}
)

func HandleLeapMessage(msg *Message) *Message {
	leap := parseLeapPointables(msg.Data)
	if leap == nil {
		logE("LeapMessage nil")
		return nil
	}

	var m *Message
	m = gestureTime(msg, leap)
	if m != nil {
		return m
	}

	gestureMove(msg, leap)
	if m != nil {
		return m
	}

	m = gestureEvent(msg, leap)
	return m
}

func parseLeapPointables(data interface{}) *LeapPointables {

	defer catch()

	a := data.([]interface{})
	leap := new(LeapPointables)
	leap.list = make([]*LeapPointable, 5)[:0]

	for _, d := range a {
		l := parseLeapPointable(d)
		if l != nil {
			leap.list = append(leap.list, l)
		}
	}

	return leap
}

func parseLeapPointable(data interface{}) *LeapPointable {

	defer catch()

	a := data.(map[string]interface{})
	leap := new(LeapPointable)

	leap.tipPosition = parseVector3(a["tipPosition"])
	leap.direction = parseVector3(a["direction"])
	leap.tipVelocity = parseVector3(a["tipVelocity"])
	leap.length = a["length"].(float64)
	leap.width = a["width"].(float64)
	leap.type_ = a["type"].(string)
	leap.id = a["id"].(int)
	leap.frame_id = a["frame_id"].(int)

	return leap
}

func parseVector3(data interface{}) Vector3 {

	defer catch()

	if data == nil {
		return Vector3{}
	}

	m := data.(map[string]interface{})
	return Vector3{
		m["x"].(float64),
		m["y"].(float64),
		m["z"].(float64),
	}
}

func handleLeapPointable(data LeapPointable) {

}

func handleLeapSwipe(msg *Message) {

}

func handleLeapKeyTap(msg *Message) {

}

func handleLeapScreenTap(msg *Message) {

}

// Gesture
// - 3-5 fingers
// - position upper right or upper left
func gestureTime(msg *Message, leap *LeapPointables) *Message {

	nfingers := len(leap.list)
	if nfingers < 3 {
		return nil
	}

	m := new(Message)
	m.FromId = msg.FromId
	m.To = "channel"

	for _, finger := range leap.list {
		x := finger.tipPosition.x
		y := finger.tipPosition.y
		// z := finger.tipPosition.z

		if y > 250 && x > 100 {
			m.Type = "earth/move_to"
			data := make(map[string]interface{})
			m.Data = data
			data["delta_time"] = 1
			return m

		} else if y > 250 && x < -100 {
			m.Type = "earth/move_to"
			data := make(map[string]interface{})
			m.Data = data
			data["delta_time"] = -1
			return m

		} else if y < 150 && x > 100 {
			m.Type = "earth/rotate"
			data := make(map[string]interface{})
			m.Data = data
			data["delta"] = 1
			return m

		} else if y < 150 && x < -100 {
			m.Type = "earth/rotate"
			data := make(map[string]interface{})
			m.Data = data
			data["delta"] = -1
			return m
		}
	}

	return nil
}

func gestureMove(msg *Message, leap *LeapPointables) *Message {
	// TODO

	return nil
}

func gestureEvent(msg *Message, leap *LeapPointables) *Message {
	// TODO

	return nil
}
