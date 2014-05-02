# glass-earth controller

> Controller App for Glass Earth on Android

![Android Controller][https://raw.githubusercontent.com/glass-earth/glass-earth/master/artwork/android-controller.png]

## Introduction

The controller connected to center server via TCP Port (default 50505). It implements Glass Earth controlling protocol based on JSON.

**The protocol is used in hackathon event only and is supposed to change in future.**

## Usage

* Join channel

  First time open the controller, teacher enters the channel name. It serves as a virtual classroom for students and other devices to join, include Unity App, Leap Motion and Web App.

* Select lesson and switch graph

  Teacher can browse and select lesson using navigation menu. Each lesson may contain many graphs. They can easily switch current graph by dragging on screen. Unity App will update current graph and student Web App will update content immediately.

* Use NFC tag to activate story

  Instead of browsing and selecting story on screen, teacher may prepare their own NFC tag. Each NFC tag stores a short description about graph or lesson (story). The controller uses this information to select the current graph or lesson. It helps teacher quickly switching to the content they want.

* Tap student smartphones to the controller to open web app using NFC

  The controller enables NFC access for student smartphones to quickly join the classroom and access the lesson. Behind the scene, it will make an URL for each channel `http://localhost/channel/[channel-name]`

## Getting Started

Follow the instruction in [getting-started.md](https://github.com/glass-earth/glass-earth/blob/master/getting-started.md)

## Protocol

The protocol is documented in [protocol.md](https://github.com/glass-earth/glass-earth/blob/master/protocol.md)
