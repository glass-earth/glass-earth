# glass-earth center-server

> Center Server for Glass Earth

## Introduction

**Control server** connects other devices, manage state of whole system and forwards messages between **EarthModelUnity**, **Leap Motion**, **controller** and **web application**.

**Web server** serves home page, **web application** and **api**.

**Web application** for students to read lessons. This application connects to server via websocket and will display the current lesson. It will update content as soon as teacher changes current lesson by using **controller**.

**API server** at /wmapi/

## Usage

* Start the server

  ```
  ./server
  ```

* Homepage

  Open http://localhost:8080/

* WMAPI

  Open http://localhost:8080/wmapi/v1/land_temp?day=2013-12-02&level=1

## Getting Started

Follow the instruction in [getting-started.md](https://github.com/glass-earth/glass-earth/blob/master/getting-started.md)

## Protocol

The protocol is documented in [protocol.md](https://github.com/glass-earth/glass-earth/blob/master/protocol.md)
