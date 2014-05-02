# Getting Started

> Step by step to build and run glass-earth

## Prerequisites

Glass Earth uses these tools & SDKs:

1. **[Unity Game Engine](https://unity3d.com/)**
* **[Android SDK](http://developer.android.com/sdk)**
* **[Leap Motion SDK](https://www.leapmotion.com/developers)**
* **[JDK 8](http://www.oracle.com/technetwork/java/javase)**
* **[Go 1.2](http://golang.org)**

## EarthModelUnity (Unity App)

TBU. Read more [glassearth.net/unity](http://glassearth.net/unity)

## LeapMotion

TBU. Read more [glassearth.net/leap](http://glassearth.net/leap)

## AstronomersAndroid (Controller)

TBU. Read more [glassearth.net/controller](http://glassearth.net/controller)

## center-server (Controller Server, API Server and Web App)

### Download and install golang. Make sure the version is 1.2 or above.

* Windows

  Download installer and follow instruction [here](https://code.google.com/p/go/downloads/list)

* Mac

  ```
  brew install golang
  ```

* Linux

  ```
  // Install golang 1.0
  sudo apt-get install golang

  // Download godeb
  mkdir ~/gotmp
  export GOPATH=~/gotmp
  cd $GOPATH
  go get launchpad.net/godeb

  // Check godeb exists
  $GOPATH/bin/godeb --help

  // Uninstall golang 1.0
  sudo apt-get remove golang

  // Uninstall related packages
  sudo apt-get autoremove

  // Install golang 1.2
  $GOPATH/bin/godeb install

  // If everything is ok, you do not this step.
  // If the above step reported error, you need to force dpkg to install.
  // It happens because conflict version of some packages between 1.0 and 1.2.
  sudo dpkg -i --force-overwrite go_1.2-godeb1_amd64.deb
  ```

### Install websocket for go

**Make sure you have `git` and `hg` available in PATH. If not, please install and add them to the system PATH**

* Windows

  ```
  cd glass-earth\center-server
  set GOPATH=%CD%
  go get code.google.com/p/go.net/websocket
  ```

* Mac and Linux

  ```
  cd glass-earth/center-server
  export GOPATH=$PWD
  go get code.google.com/p/go.net/websocket
  ```

### Build and start server

* Windows

  ```
  cd glass-earth\center-server
  set GOPATH=%CD%
  go build server
  server
  ```

* Mac and Linux

  ```
  cd glass-earth\center-server
  set GOPATH=%CD%
  go build server
  chmod +x server
  ./server
  ```

### Open browser

* Homepage: http://localhost:8080/
* WMAPI: http://localhost:8080/wmapi/v1/land_temp?day=2013-12-02&level=1
