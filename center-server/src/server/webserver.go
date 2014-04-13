package main

import (
	"html/template"
	"net/http"
	"strings"
)

func makeHandler(name, filepath string) http.HandlerFunc {
	return func(rw http.ResponseWriter, req *http.Request) {
		tpl, err := template.ParseFiles(*flPublic + filepath)
		if err != nil {
			logE(name, "Template:", err)
			return
		}

		err = tpl.Execute(rw, nil)
		if err != nil {
			logE(name, err)
		}
	}
}

func makeGraphHandler(name, prefix, dirpath string) http.HandlerFunc {
	return func(rw http.ResponseWriter, req *http.Request) {
		uri := req.URL.RequestURI()
		if strings.HasPrefix(uri, prefix) {
			uri = uri[len(prefix):] + ".html"
			logV("Serve file", dirpath+uri)
			http.ServeFile(rw, req, dirpath+uri)

		} else {
			logE("Expect uri has prefix", prefix, uri)
		}

	}
}

func startWebServer() {

	http.Handle("/", http.FileServer(http.Dir(*flPublic)))
	http.HandleFunc("/channel/", makeHandler("ChannelHandler", "/channel.html"))
	http.HandleFunc("/graph/", makeGraphHandler("GraphHandler", "/graph/", *flPublic+"/graph/"))
	http.HandleFunc("/ctrl_channel/", makeHandler("CtrlChannelHandler", "/ctrl_channel.html"))
	http.HandleFunc("/ctrl_graph/", makeGraphHandler("CtrlGraphHandler", "/ctrl_graph/", *flPublic+"/ctrl_graph/"))

	logI("HTTP Server is listening on", *flHttpPort)

	err := http.ListenAndServe(":8080", nil)
	if err != nil {
		logE("HTTP Server:", err)
	}
}
