package main

import (
	"html/template"
	"net/http"
	"strings"
)

func makeHandler(name, filepath string) http.HandlerFunc {

	tpl := template.Must(template.ParseFiles(*flPublic + filepath))

	return func(rw http.ResponseWriter, req *http.Request) {
		err := tpl.Execute(rw, nil)
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

func makeRedirectHandler(redirectTo string) http.HandlerFunc {
	return func(rw http.ResponseWriter, req *http.Request) {
		http.Redirect(rw, req, redirectTo, http.StatusTemporaryRedirect)
	}
}

// var rootTpl   = template.Must(template.ParseFiles(*flPublic + "/index.html"))

func rootHandler(rw http.ResponseWriter, req *http.Request) {
	if req.URL.Path == "/" {
		// rootTpl.Execute(rw, nil)
		indexPath := *flPublic + "/index.html"
		http.ServeFile(rw, req, indexPath)
		return
	}

	http.Redirect(rw, req, "/", http.StatusTemporaryRedirect)
}

func startWebServer() {

	http.HandleFunc("/", rootHandler)

	http.HandleFunc("/channel/", makeHandler("ChannelHandler", "/channel.html"))
	http.HandleFunc("/graph/", makeGraphHandler("GraphHandler", "/graph/", *flPublic+"/graph/"))
	http.HandleFunc("/ctrl_channel/", makeHandler("CtrlChannelHandler", "/ctrl_channel.html"))
	http.HandleFunc("/ctrl_graph/", makeGraphHandler("CtrlGraphHandler", "/ctrl_graph/", *flPublic+"/ctrl_graph/"))
	http.HandleFunc("/"+configWMApi+"/", handleApi)

	http.Handle("/app/", http.StripPrefix("/app/", http.FileServer(http.Dir("app"))))

	// http.HandleFunc("/app", makeRedirectHandler("/channel/"))
	http.HandleFunc("/unity", makeRedirectHandler("https://github.com/glass-earth/glass-earth/tree/master/EarthModelUnity"))
	http.HandleFunc("/controller", makeRedirectHandler("https://github.com/glass-earth/glass-earth/tree/master/AstronomersAndroid"))
	http.HandleFunc("/leap", makeRedirectHandler("https://github.com/glass-earth/glass-earth/tree/master/LeapMotion"))
	http.HandleFunc("/lesson/colorado-wildfires", makeRedirectHandler("/app/#colorado-wildfires"))
	http.HandleFunc("/lesson/whale-migration", makeRedirectHandler("/app/#whale-migration"))
	http.HandleFunc("/lesson/", makeRedirectHandler("/app/"))

	logI("HTTP Server is listening on", *flHttpPort)

	err := http.ListenAndServe(":8080", nil)
	if err != nil {
		logE("HTTP Server:", err)
	}
}
