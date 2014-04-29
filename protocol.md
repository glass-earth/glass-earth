# Network protocol

## Usecases

- connect
- reconnect
- channel / channel_name (define group of devices and controllers)
- control information between LeapMotion and application (JSON over socket)
- control information between controller and application (JSON over socket)
- state information about application (config, current graph, velocity, rotate angel, camera position, etc.) for exchanging between application and controller

## Protocol

### Handshake

#### Connect

- Default Port: 50505
- Role: "server", "controller", "leap", "app"
- Client:  one among "app", "controller" or "leap"

Application connects to server:
```js
connect(192.168.1.100, 50505)
send({
  type: "handshake/connect",
  role: "app",
  channel_name: "foo_channel"
})
```

Controller connects to server:
```js
connect(192.168.1.100, 50505)
send({
  type: "handshake/connect",
  role: "controller",
  channel_name: "foo_channel"
})
```

Server responses:
```js
send({
  type: "handshake/accept",
  role: "server",
  channel_name: "foo_channel",
  peer_id: 12345                // Server assigns an different id for each client in a channel.
               // Server does not reuse id for new client.
               // A channel is closed when there is no client in it.
})
```

Server rejects (disconnects):
```js
send({
  type: "handshake/error",
  role: "server",
  description: "Server is full"
})
```

#### Reconnect

Client sends to server:
```js
send({
  type: "handshake/reconnect",
  role: "application",
  peer_id: 12345         // Client use `peer_id` that server sent before.
})
```

Server may accept or reject.

#### Close

Client send **close** message to server to indicate that it want to close. Otherwise, server should expect a dropped connection and wait for client to reconnect (wait for maximum 10 minutes).

```js
send({
  type: "handshake/close",
  description: "User exit"
})
```

#### Connected peers

Server broadcasts all connected peers in a channel, when a client connects to or disconnects from server (includes the received peer):
```sh
send({
  type: "handshake/peers",
  role: "server",
  peers: [
    { id: 12345, role: "controller", ... },
    ...
  ]
})
```

### Forward messages

Client sends message to another peer. In this case, server simply forward the message.
Client can use any value in type (except `handshake` message) and must specific received peers (`to`).

There are some kinds of received peer:
- Peer id
- All peers in a specific role
- All peers in the current channel

Controller (12367) send message to application (12345):
```js
// controller to server
send({
  type: "graph/switch",
  role: "controller",
  to_id: 12345,   // must specific "to_id" or "to" field
  // to: "app"
  // to: "controller"
  // to: "leap"
  // to: "channel"
  data: {
    graph_name: "polution"
  }
})

// server to application, note *role* is "controller", *from_id* is *12367*
send({
  type: "graph/switch",
  role: "controller",
  from_id: 12367,
  to: "id_12345",
  data: {
    ...
  }
})

// application to server
send({
  type: "graph/switch",
  role: "application",
  to_id: 12367
  data: {
    status: "ok"
  }
})

// server to controller
send({
  type: "graph/switch",
  role: "application",
  from_id: 12345,
  to_id: 12367,
  data: {
    status: "ok"
  }
})
```

### Controlling using LeapMotion

#### Gesture

##### Swipe Gesture

```js
send({
  type: "leap/gesture/swipe",
  role: "leap",
  to: "app",
  "direction": "up" || "down" || "left" || "right"
})
```
TODO: @ng-vu  Based on the gesture, sent the correct action to other:
- Right: Cho the next graph and broadcast switch
- Left: start/stop self rotation
- Up: Open the full map
- Down: back to the normal map.

### Controlling using controller

#### Usecases

- Select story
- Get story info
- Select graph
- Get graph info
- Stop / move Earth
- Open / close WorldMap
- Turn on/off earth rotation
- Point to a country / region ("named" lat,long)
- Time animation (controller only)

#### Select graph / story

Story is collection of:
- Graphs
- Specific range of time
- Information on controller
- More information on website ( http://192.168.1.199:8080/channel/channel-1 )

User can
- Move Earth around
- Point to a specific location & specific day
- Choose another story

Story have more specific locations and days than graph. In our API, both are called "graph". Graph names are lower case..
Built-in story "all" includes all graphs.

##### Initial maps
```js
send({
  role: "controller",
  to: "channel",
  type: "story/list",
})

send({
  role: "controller",
  to: "channel",
  type: "story/info"
})

// deprecated, use story/info instead
send({
  role: "controller",
  to: "channel",
  type: "graph/list"
})

send({
  role: "controller",
  to: "channel",
  type: "graph/info"
})

// story/list
send({
  role: "server",
  to: "controller",
  type: "story/list",
  data: {
    story_names: [ "all", "colorado-wildfires" ]
  }
})

// story/info, in response for story/info request or broadcast when switching story
// if broadcast, following by graph/info message to set active graph
send({
  role: "server",
  to: "controller", // "channel", "guest"
  type: "story/list",
  data: {
    story_name: "colorado-windfires",
    story_label: "Colorado Windfires",
    graph_names: [ "land_temp", "sea_temp" ],
    active_graph: "land_temp",
  }
})

// deprecated. Response State Information, see below
send({
  role: "app",
  to: "channel",
  type: "graph/list",
  data: {
    graph_names: ["dust_score","vapor",...],
  }
})

// response State Information, see below
send({
  role: "server",
  type: "graph/info",
  data: {
    ...
   }
})
```

------------
```js
send({
  role: "controller",
  to: "channel",
  type: "graph/switch",
  data: {
    graph_name: "dust_score"
  }
})

// response State Information, see below
send({
  role: "app",
  to: "channel",
  type: "app/state",
  data: {
    graph_name: "dust_score",
    ...
  }
})
```

#### Earth

```js
send({
  role: "controller",
  to: "channel",
  type: "earth/time_animation",
  data: {
     animate: true // false
     start: "21-10-2012",
     end: "30-01-2014"
     // start: "", // not change
     // start: "default", // use default graph
     // end: "default"
  }
})

send({
  role: "controller",
  to: "channel",
  type: "earth/rotate",
  data: {
    velocity: 1,
    delta: +0.5  // if "delta" exist, ignore velocity
  }
})

send({
  role: "controller",
  to: "channel",
  type: "earth/move_to",
  data: {
    location: "vietnam",   // either location, time or both exist
    time: "12-09-2013"
    // time: ""
  }
})

send({
  role: "controller",
  to: "channel",
  type: "earth/reset",
  data: null
})

send({
  role: "controller"
})
```

### State Information

Send from application to all peers in a channel

```js
send({
   role: "app",
   to: "channel",
   type: "app/state",
   data: {
    graph_name: "dust_score",
    earth_rotation: 359,  // deg
    earth_velocity: 1,  // default
    earth_time_animating: true,
    earth_time_start: "21-05-2013",
    earth_time_end: "10-06-2014",
    error: {           // null or error, include last message and an error description
      type: "graph/switch",
      role: "controller",
      to: "channel",
      description: "Graph does not exist",
      data: null
    }
  }
})

send({
  role: "app",
  to: "channel",
  type: "graph/info",
  data: {
    graph_name: "dust_score",
    graph_label: "Dust Score",
    guest_url: "http://192.168.1.199:8080/channel/channel-1",
    guest_content_url: "http://192.168.1.199:8080/graph/dust_score",
    controller_url: "http://192.168.1.199:8080/ctrl_channel/channel-1",
    controller_content_url: "http://192.168.1.199:8080/ctrl_graph/dust_score",
    events: [ {
      label: "Viet Nam",
      name: "vietnam",
      time: "21-09-2013"
    }, {
      label: "China",
      name: "china",
      time: "23-10-2013"
    }],
    error: { ... } // see above
  }
})
```

### Websocket

#### Handshake

Connect
```js
open("http://192.168.1.199:8080/ws/")
send({
  type: "handshake/connect",
  role: "guest",
  channel_name: "channel-1"
})

// server response, see above
send({
  type: "handshake/accept",
  role: "server",
  peer_id: 12345,
  channel_name: "channel-1"
})

// or error
send({
  type: "handshake/error",
  role: "server",
  description: "Server is full"
})
```

Disconnect
```js
send({
  type: "handshake/close",
  description: "User exit"
})
```

#### Server messages

Listen to these messages from server, ignore others
```js
// list peers, send when a peer connects or disconnects
send({
  type: "handshake/peers",
  role: "server",
  peers: [{
    id: 12367,
    role: "app"
  }, ... ]
})

// see above, app/state
send({
  type: "app/state",
  role: "app",
  ...
})
```
