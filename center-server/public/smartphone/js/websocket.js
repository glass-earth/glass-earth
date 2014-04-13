var clientSocket = {

    innerSocket: null,
    peerId: null,
    url: null,

    init: function(url) {
        if ('WebSocket' in window) {
        	this.url = url;
            this.innerSocket = new WebSocket(url);
            this.innerSocket.onopen = this.onopen;
            this.innerSocket.onclose = this.onclose;
            this.innerSocket.onmessage = this.onmessage;
            this.innerSocket.onerror = this.onerror;
        } else {
            console.log('websocket not support');
        }
    },

    send: function(msg) {
        // Send the msg object as a JSON-formatted string.
        console.log('Check if socket is connected');
        if (!this.isConnected()) {
        	this.init(this.url);
            return;
        }
        console.log('Begin send');
        var msg_str = JSON.stringify(msg);
        console.log(msg_str);
        this.innerSocket.send(msg_str);
    },

    close: function() {
        console.log('Send close signal');
        var msg = {
          type: "handshake/close",
          description: "User exit"
        }
        this.innerSocket.send(msg);
        this.innerSocket.close();
    },

    isConnected: function() {
    	return this.innerSocket.readyState == 1;
    },

    onopen: function() {
        console.log('Connected to ' + clientSocket.url);
        var msg = {
            type: "handshake/connect",
            role: "guest",
            channel_name: "channel-1"
        };
        console.log('Begin handshake');
        clientSocket.send(msg);
    },

    onmessage: function(e) {
        
        console.log('Message received:');
        console.log('Raw message:');
        console.log(e.data);

        var msg = JSON.parse(e.data);
        console.log('Parse message:');
        console.log(msg);

        switch (msg.type) {
            case "handshake/accept":
                clientSocket.peerId = msg.peer_id;
                console.log('Handshake accept. Set peerId = ' + clientSocket.peerId);
                break;
            case "handshake/close":
            case "handshake/error":
                console.log(msg.description);
                break;
            case "handshake/peers":
                break;
            case "app/state":
                break;
            case "graph/info":
                console.log('Url:');
                console.log(msg.data.guest_content_url);
                if (msg.data.guest_content_url != 'undefined' && msg.data.guest_content_url != '') {
                    console.log('Load content');
                    clientSocket.oninfo(msg.data.guest_content_url, msg.data.graph_label);    
                }
            	break;
        }
    },

    onerror: function(error) {
        console.log('Error detected: ');
        console.log(error);
    },

    onclose: function() {
        console.log('Websocket disconnected');
    },

    oninfo: function(url, title) {

    }
}
