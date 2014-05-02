# glass-earth leap motion

> Leap Motion App for controlling Glass Earth

## Introduction

Leap Motion App allow teacher to control the Unity App by hand gesture.

Leap Motion App connects to center-server via TCP Port (default 50505). It implements Glass Earth controlling protocol based on JSON.

## Usage

* Connect hardward and Leap Motion service

  Use must connect Leap Motion device and start Leap Motion service. Make sure the device works correctly.

* Join channel

  Leap Motion App must join the same room with Unity App and others.

* Control

  Move hand left to toggle time animation. Move hand right to switch graph. Move hand up and down to open and close 2D World Map mode.

## Getting Started

Follow the instruction in [getting-started.md](https://github.com/glass-earth/glass-earth/blob/master/getting-started.md)

## Protocol

The protocol is documented in [protocol.md](https://github.com/glass-earth/glass-earth/blob/master/protocol.md)
