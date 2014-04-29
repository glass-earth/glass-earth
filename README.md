glass-earth
===========

> NASA Space Apps Challenge

**Note:** This repository is source code we wrote during NASA Space App Challenge Event. We will split each directory to separate repository under [github.com/glass-earth](http://github.com/glass-earth) namespace.

**This repository is used for archival purpose only. Visit [github.com/glass-earth](http://github.com/glass-earth) for up-to-date projects**

## Introduction

Glass Earth is solving the challenge "A Picture Is Worth A Thousand Words" whose requirement is creating a "mashup" to combine data from various sources including satellite imagery from NASA’s Global Imagery Browse Services ([GIBS](https://wiki.earthdata.nasa.gov/display/GIBS/GIBS+Available+Imagery+Products)) and other reliable sources, finding the connection among them. Combining information like this can put data into context to tell a compelling story or unearth an interesting insight.

For more information, visit [2014.spaceappschallenge.org/project/glass-earth/](https://2014.spaceappschallenge.org/project/glass-earth/)

### Prerequisites

Glass Earth uses these tools & SDKs:

1. **[Unity Game Engine](https://unity3d.com/)**
* **[Android SDK](http://developer.android.com/sdk)**
* **[Leap Motion SDK](https://www.leapmotion.com/developers)**
* **[Go](http://golang.org)**

### Structure

1. **EarthModelUnity**

  Render graph from [GIBS](https://wiki.earthdata.nasa.gov/display/GIBS/GIBS+Available+Imagery+Products) onto an Earth model. There are two scenes. One scene renders 4 viewports of Earth for displaying the 3D model inside prism. The other renders 4 different graphs for displaying on projector. The application connects to **center-server** for receiving controlling messages from Leap Motion and controller.

* **LeapMotion**

  Control the graphs using Leap Motion.

* **AstronomersAndroid (controller)**

  Control the graphs using Android tablet. Supports NFC and gesture. This application allows teacher to select current lesson and control whole system. Students may tap their smartphone to open **web application** via NFC.

* **center-server**

  **Control server** connects other devices, manage state of whole system and forwards messages between **EarthModelUnity**, **Leap Motion**, **controller** and **web application**.

  **Web server** serves home page, **web application** and **api**.

  **Web application** for students to read lessons. This application connects to server via websocket and will display the current lesson. It will updated content as soon as teacher change using **controller**.

  **API server** at /wmapi/

## Getting Started

Follow the instruction in [getting-started.md](https://github.com/glass-earth/glass-earth/blob/master/README.md)

## Credits

This project is created by:

* Anh Minh
* Anh Nguyen
* Thuan Le
* Tuan Chau
* Vu Nguyen
