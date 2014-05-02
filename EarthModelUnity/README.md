# glass-earth unity app

> Unity App for displaying Glass Earth inside prism or on projector

## Introduction

Unity App render graph from [GIBS](https://wiki.earthdata.nasa.gov/display/GIBS/GIBS+Available+Imagery+Products) onto an Earth model.

It has two scenes (modes). One scene renders 4 viewports of Earth for displaying the 3D model inside prism. The other renders 4 different graphs for displaying on projector. In a graph, it has 3D Sprere Mode and 2D World Map Mode. You can see the Unity App in action here: [youtube.com/watch?v=QeqrtSaIRg0#t=17](https://www.youtube.com/watch?v=QeqrtSaIRg0#t=17)

Unity App connects to center-server via TCP Port (default 50505). It implements Glass Earth controlling protocol based on JSON.

## Usage

* Join channel

  Click button to connect to server and join channel. Default is 50505, channel-1. You can change these values in config.

* Controlled by Keyboard

  TBU.

* Controlled by Controller

  Controller must join the same room. Use controller to select lesson and switch graph.

* Controlled by Leap Motion

  Leap Motion must join the same room. Move hand left to toggle time animation. Move hand right to switch graph. Move hand up and down to open and close 2D World Map mode.

## Getting Started

Follow the instruction in [getting-started.md](https://github.com/glass-earth/glass-earth/blob/master/getting-started.md)

## Protocol

The protocol is documented in [protocol.md](https://github.com/glass-earth/glass-earth/blob/master/protocol.md)
