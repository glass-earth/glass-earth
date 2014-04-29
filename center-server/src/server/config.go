package main

import (
	"encoding/json"
	"io/ioutil"
	"os"
	"path/filepath"
	"regexp"
	"sort"
)

var (
	configDefaultStory = "all"
	configListStory    = make([]string, 8)[:0]
	configStoryInfo    = make(map[string]*StoryInfo)

	configDefaultGraph = "default"
	configListGraph    = make([]string, 8)[:0]
	configGraphInfo    = make(map[string]*MsgGraphInfo)

	configListApi = make([]string, 8)[:0]
	configApiInfo = make(map[string]*ApiInfo)

	configPathChannel     = "/channel/"
	configPathGraph       = "/graph/"
	configPathCtrlChannel = "/ctrl_channel/"
	configPathCtrlGraph   = "/ctrl_graph/"

	configDefaultState = MsgState{
		EarthRotation:      1,
		EarthVelocity:      1,
		EarthTimeAnimating: true,
		EarthTimeStart:     "",
		EarthTimeEnd:       "",
	}

	configWMApi = "wmapi"
)

type configJson struct {
	Graphs  map[string]configElem
	Api     map[string]configElem
	Stories map[string]configElem
}

type configElem struct {
	Label     string   `json:"label"`
	Graphs    []string `json:"graphs"`
	TimeStart MsgDate  `json:"time_start"`
	TimeEnd   MsgDate  `json:"time_end"`
}

func registerGraph(name, label string) {
	configListGraph = append(configListGraph, name)
	configGraphInfo[name] = &MsgGraphInfo{
		GraphName:  name,
		GraphLabel: label,
	}
}

func registerStory(name, label string, graphs []string) {
	configListStory = append(configListStory, name)
	configStoryInfo[name] = &StoryInfo{
		StoryName:  name,
		StoryLabel: label,
		Graphs:     graphs,
	}
}

func initApi(config configJson) {
	for name, elem := range config.Api {
		configListApi = append(configListApi, name)
		info := &ApiInfo{
			name:  name,
			label: elem.Label,
			dir:   *flData + "/" + name,
			days:  nil,
			files: make(map[string]string),
		}
		configApiInfo[name] = info

		scanDataDir(info)

		logI("Scan graph", name, "found", len(info.days), "days")
		if len(info.days) > 0 {
			logI("  From", info.days[0], "to", info.days[len(info.days)-1])
		}
	}
}

var dayRe = regexp.MustCompile("201[0-9]-[0-1][0-9]-[0-3][0-9]")

func scanDataDir(info *ApiInfo) {
	days := make([]string, 128)[:0]
	walkFn := func(path string, fileInfo os.FileInfo, err error) error {

		day := dayRe.FindString(path)
		// logV("scan", day, path, err)

		if day != "" {
			days = append(days, day)
			info.files[day] = path
		}

		return nil
	}
	err := filepath.Walk(info.dir, walkFn)
	if err != nil {
		logE("scanDataDir", err)
		os.Exit(1)
	}

	sort.Strings(days)
	info.days = days

	// logV("days", info.name, len(info.days), info.days)
}

func initGraph(config configJson) {
	for name, elem := range config.Graphs {
		registerGraph(name, elem.Label)
	}
}

func initStory(config configJson) {

	registerStory("all", "All Graphs", configListGraph)

	for name, elem := range config.Stories {
		registerStory(name, elem.Label, elem.Graphs)
	}
}

func configData() {

	defer func() {
		err := recover()
		if err != nil {
			logE("Error loading config:", err)
			os.Exit(1)
		}
	}()

	cfg, err := ioutil.ReadFile(*flConfig)
	if err != nil {
		panic(err)
	}

	var config configJson
	err = json.Unmarshal(cfg, &config)
	if err != nil {
		panic(err)
	}

	initGraph(config)
	initStory(config)
	initApi(config)
}
