package main

import (
	"net/http"
	"sort"
	"strings"
)

type ApiInfo struct {
	name  string
	label string
	dir   string
	days  []string
	files map[string]string
}

func DataFilePath(name string, day string, level int) string {

	if level != 1 {
		return ""
	}

	info := configApiInfo[name]
	if info == nil || len(info.days) == 0 {
		logI("Request error, graph not found", name)
		return ""
	}

	if day < info.days[0] || day > info.days[len(info.days)-1] {
		logI("Request error, day invalid", day)
		return ""
	}

	exactDay := sort.SearchStrings(info.days, day)

	if exactDay < 0 || exactDay >= len(info.days) {
		logI("Request error, day not found", day, exactDay)
		return ""
	}

	logV("ExactDay", exactDay, day, info.days[exactDay], info.files[info.days[exactDay]])

	return info.files[info.days[exactDay]]
}

func BadRequest(w http.ResponseWriter, r *http.Request) {
	w.WriteHeader(http.StatusBadRequest)
	logI("API BadRequest", r.URL.RequestURI())
}

func handleApi(w http.ResponseWriter, r *http.Request) {

	// /wmapi/v1/land_temp?day=2013-12-02&level=1
	logV("requestUrl", r.URL.RequestURI())
	parts := strings.Split(r.URL.Path, "/")
	if !(len(parts) == 4 && parts[1] == configWMApi && parts[2] == "v1") {
		logV("parts", parts)
		BadRequest(w, r)
		return
	}

	name := parts[3]
	level := r.URL.Query().Get("level")
	day := string(r.URL.Query().Get("day"))

	// We currently only support level 1
	if level != "" && level != "1" {
		logV("level", level)
		BadRequest(w, r)
		return
	}

	path := DataFilePath(name, day, 1)
	if path == "" {
		logV("path", name, day, 1)
		BadRequest(w, r)
		return
	}

	logV("ServeFile", r.URL.RequestURI(), path)
	http.ServeFile(w, r, path)
}
