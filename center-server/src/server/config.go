package main

var (
	configDefaultGraph = "default"
	configListGraph    []string
	configGraphInfo    = map[string]*MsgGraphInfo{

		"land_temp": &MsgGraphInfo{
			GraphLabel: "Land Temperature",
		},

		"snow_cover": &MsgGraphInfo{
			GraphLabel: "Snow Cover",
		},

		"default": &MsgGraphInfo{
			GraphLabel: "The Earth",
			Events:     nil,
		},
	}

	configPathChannel     = "/channel/"
	configPathGraph       = "/graph/"
	configPathCtrlChannel = "/ctrl_channel/"
	configPathCtrlGraph   = "/ctrl_graph/"
)

func configGraph() {
	configListGraph = make([]string, len(configGraphInfo))[:0]
	for graphName, graphInfo := range configGraphInfo {
		configListGraph = append(configListGraph, graphName)
		graphInfo.GraphName = graphName
	}
}

// Location
// us, china, vietnam, biendong, southem, northem
