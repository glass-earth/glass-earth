using UnityEngine;
using System.Collections;
using System;
using System.IO;
using System.Net.Sockets;
using System.Threading;
using State;
using System.Collections.Generic;

namespace Networking
{
    public class NetworkManagerObj : MonoBehaviour
    {

        public string Host = "192.168.1.199";
        public int Port = 50505;
        public string channelName = "channel-1";
        public int maxConnectingRetries = 3;
        NetworkManager manager;
        float btnX, btnY, btnW, btnH;
        Rect btnStart;
        //        Rect btnRefresh;

        void Start ()
        {
            manager = NetworkManager.getInstance ();
            manager.Host = Host;
            manager.Port = Port;
            manager.channelName = channelName;
            manager.maxConnectingRetries = maxConnectingRetries;

            manager.StartThread ();

            btnX = Screen.width * 0.05f;
            btnY = Screen.width * 0.05f;
            btnW = Screen.width * 0.1f;
            btnH = Screen.width * 0.05f;
            
            btnStart = new Rect (btnX, btnY, btnW, btnH);
        }

        void OnGUI ()
        {
            if (GUI.Button (btnStart, "Connect Server")) {
                Debug.Log ("Connect server");
                Console.WriteLine ("Console");
                manager.startServer ();
            }
        }

        void FixUpdate ()
        {
            manager.FixedUpdate ();
        }
    }

    // Singleton
    public class NetworkManager
    {
        public static readonly string kStatusNotInit = "NotInit";
        public static readonly string kStatusConnecting = "Connecting";
        public static readonly string kStatusReady = "Ready";
        public static readonly string kStatusDisconnected = "Disconnected";
        public static readonly string kStatusDrop = "Drop";
        public string Host;
        public int Port;
        public string channelName;
        public int maxConnectingRetries;
        ControlInterface ctrl;
        int id;
        string status = kStatusNotInit;
        bool socketReady = false;
        bool threadRunning = false;
        int connectingRetries = 0;
        TcpClient mySocket;
        Thread theThread;
        NetworkStream theStream;
        StreamWriter theWriter;
        StreamReader theReader;
        static NetworkManager instance = new NetworkManager ();

        public NetworkManager ()
        {

        }

        public static NetworkManager getInstance ()
        {
            return instance;
        }

        public static void setController (ControlInterface ctrl)
        {
            instance.ctrl = ctrl;
        }

        public void StartThread ()
        {
            // Create new thread
            if (!threadRunning) {
                threadRunning = true;
                ThreadStart ts = new ThreadStart (socketReader);
                theThread = new Thread (ts);
                theThread.Start ();
                Debug.Log ("Thread created");
            }
        }

        Queue<CtrlAction> queue = new Queue<CtrlAction> ();

        public void FixedUpdate ()
        {
            while (queue.Count > 0) {
                var action = queue.Dequeue ();
                if (action != null) {
                    action ();
                }
            }
        }

        public void startServer ()
        {
            if (socketReady) {
                return;
            }
            status = kStatusConnecting;
            setupSocket ();
        }

        public void setupSocket ()
        { 
            try {
                mySocket = new TcpClient (Host, Port);
                theStream = mySocket.GetStream ();
                theWriter = new StreamWriter (theStream);
                theReader = new StreamReader (theStream);
                socketReady = true;

                status = kStatusConnecting;

                // connect or reconnect
                if (id == 0) {
                    Debug.Log ("Connecting...");
                    Message msg = new Message (Const.kHandshakeConnect, Const.kRoleApp);
                    msg.channel_name = channelName;
                    sendMessage (msg);

                } else {
                    Debug.Log ("Reconnecting...");
                    Message msg = new Message (Const.kHandshakeReconnect, Const.kRoleApp);
                    msg.peer_id = id;
                    msg.channel_name = channelName;
                    sendMessage (msg);
                            
                }

            } catch (Exception e) {
                Debug.Log ("Socket error:" + e);
            }
        }
    
        public void writeSocket (string theLine)
        {
            if (!socketReady) {
                return;
            }
            String tmpString = theLine;
            theWriter.Write (tmpString);
            theWriter.WriteLine ();
            theWriter.Flush ();
        }
    
        // This function blocks until it reads something to return
        public String readSocket ()
        {
            if (!socketReady) {
                return "";
            }
                      
            var line = theReader.ReadLine ();
            return line;
        }
    
        public void closeSocket ()
        {
            if (!socketReady) {
                return;
            }
            theWriter.Close ();
            theReader.Close ();
            mySocket.Close ();
            socketReady = false;
        }
    
        public void maintainConnection ()
        {
            if (socketReady && !theStream.CanRead) {
                Debug.Log ("Connection dropped. Reconnecting...");
                status = kStatusDrop;
                connectingRetries = 0;

                // TODO: wait 500ms
                setupSocket();
            }
        }

        public void sendMessage (Message msg)
        {
            string s = msg.Marshal ();
            Debug.Log ("Send message: " + s);
            writeSocket (s);
        }

        public void stopListening ()
        {
            threadRunning = false;
        }

        public void handleMessage (Message msg)
        {
            Debug.Log ("Receive: " + msg.Marshal ());

            if (msg.type == Const.kHandshakeAccept) {
                if (status == kStatusConnecting) {
                    id = msg.peer_id;
                } else {
                    Debug.LogError ("Unexpected handshake accept: " + msg.type);
                }

            } else if (msg.type == Const.kHandshakeError) {
                if (status == kStatusConnecting && connectingRetries < maxConnectingRetries) {
                    connectingRetries += 1;
                    Debug.LogError ("Handshake error. Retry " + connectingRetries + "/" + maxConnectingRetries + ": " + msg.type);
                                
                    // TODO: wait 500ms
                    setupSocket();
                    closeSocket ();
                    status = kStatusDisconnected;

                } else {
                    Debug.LogError ("Handshake error. Disconnected: " + msg.type);
                }

            } else if (msg.type == Const.kHandshakeClose) {
                Debug.Log ("Receive handshake close");
                status = kStatusDisconnected;
                closeSocket ();

            } else if (msg.type.StartsWith (Const.kHandshake)) {
                Debug.LogWarning ("Unknown handshake message: " + msg.type);        

            } else if (msg.type == Const.kAck) {
                // Do nothing

            } else {
                handleControlMessage (msg);
            }
        }

        void handleControlMessage (Message msg)
        {               
            try {
                var action = ctrl.handleCtrlMsg (msg);
                if (action != null) {
                    queue.Enqueue (action);
                }

            } catch (Exception e) {
                Debug.LogError (e);
            }
        }

        // This function runs on another thread for reading socket
        void socketReader ()
        {
            while (threadRunning) {
                try {
                    var line = readSocket ();
                    if (line != "") {
                        Debug.Log ("Read: " + line);
                        var msg = Message.Unmarshal (line);
                        handleMessage (msg);

                    } else {
                        Thread.Sleep (50);
                    }

                } catch (Exception e) {
                    Debug.Log (e);
                }
            }
        }

        void OnApplicationQuit ()
        {
            stopListening ();

            // wait for listening thread to terminate (max. 500ms)
            if (theThread != null) {
                theThread.Join (500);
            }
        }
    }
}