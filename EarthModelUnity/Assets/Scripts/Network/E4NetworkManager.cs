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
    public class E4NetworkManager : MonoBehaviour
    {

        public static readonly string kStatusNotInit = "NotInit";
        public static readonly string kStatusConnecting = "Connecting";
        public static readonly string kStatusReady = "Ready";
        public static readonly string kStatusDisconnected = "Disconnected";
        public static readonly string kStatusDrop = "Drop";
        public GameObject world;
//    public GameObject CameraController;
        public string Host = "192.168.1.199";
        public int Port = 50505;
        public string channelName = "channel-1";
        public int maxConnectingRetries = 3;
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
        float btnX, btnY, btnW, btnH;
        Rect btnStart, btnRefresh;
    
        void Start ()
        {
            btnX = Screen.width * 0.05f;
            btnY = Screen.width * 0.05f;
            btnW = Screen.width * 0.1f;
            btnH = Screen.width * 0.05f;
        
            btnStart = new Rect (btnX, btnY, btnW, btnH);
            btnRefresh = new Rect (btnX, btnY + btnH * 1.2f, btnW, btnH);
        
            // Create new thread
            threadRunning = true;
            ThreadStart ts = new ThreadStart (socketReader);
            theThread = new Thread (ts);
            theThread.Start ();
            print ("Thread done...");
            Console.WriteLine ("This is log");
            Console.Error.WriteLine ("This is error");
        }
    
        void OnGUI ()
        {
            if (GUI.Button (btnStart, "Connect Server")) {
                print ("Connect server");
                Console.WriteLine ("Console");
                startServer ();

            }
        }
    
        void Update ()
        {
        
        }
    
        Queue<string> queue = new Queue<string> ();
    
        void FixedUpdate ()
        {
        
            while (queue.Count > 0) {
                var e = queue.Dequeue ();
                if (e == "time_forward") {
                    TimeForward ();
                
                } else if (e == "time_backward") {
                    TimeBackward ();
                
                } else if (e == "show_earth") {
                    ShowEarth ();
                
                } else if (e == "show_temp") {
                    //                    Debug.Log ("show_temp");
                    ShowLandTemp ();
                
                } else if (e == "left") {
                    RotateLeft ();
                
                } else if (e == "right") {
                    RotateRight ();
                }
            }
        }
    
        void startServer ()
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
                    print ("Connecting...");
                    Message msg = new Message (Const.kHandshakeConnect, Const.kRoleApp);
                    msg.channel_name = channelName;
                    sendMessage (msg);
                
                } else {
                    print ("Reconnecting...");
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
            if (line.Contains ("temp")) {
                queue.Enqueue ("show_temp");
                Debug.Log ("temp");
            
            
            } else if (line.Contains ("snow")) {
                queue.Enqueue ("show_earth");
            
            }
        
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
                this.Invoke ("setupSocket", 500);
            }
        }
    
        public void sendMessage (Message msg)
        {
            string s = msg.Marshal ();
            print ("Send message: " + s);
            writeSocket (s);
        }
    
        public void stopListening ()
        {
            threadRunning = false;
        }
    
        public void handleMessage (Message msg)
        {
            print ("Receive: " + msg.Marshal ());
        
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
                
                    this.Invoke ("setupSocket", 500);
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
            var app = AppState.getInstance ();
            var config = AppState.getConfig ();
            var earth = AppState.getConfig ();
            var leap = AppState.getLeap ();
        
            if (msg.role == Const.kRoleLeap) {
            
            } else if (msg.role == Const.kRoleController) {
                handleCtrllerMsg (msg);
            
            } else {
                Debug.LogWarning ("Unknown control message: " + msg.type);
            }
        }
    
        const int maxTime = 141;
        DateTime baseDate = new DateTime (2012, 05, 08);
    
        void ShowLandTemp ()
        {

        }
    
        void ShowEarth ()
        {
            
        }
    
        void RotateLeft ()
        {
            GetSwaper().SetRotateLeftRight (true);
        }
    
        void RotateRight ()
        {
            GetSwaper().SetRotateLeftRight(false);
        }
    
        void TimeForward ()
        {
            var d = GetMatChanger().GetCurTexIndex ();
            var d2 = d + 1;
            d2 = d2 < 0 ? 0 : d2;
            d2 = d2 >= maxTime ? maxTime : d2;
            GetMatChanger ().SetCurTexIndex (d2);
        }
    
        void TimeBackward ()
        {
            var d = GetMatChanger().GetCurTexIndex ();
            var d2 = d - 1;
            d2 = d2 < 0 ? 0 : d2;
            d2 = d2 >= maxTime ? maxTime : d2;
            GetMatChanger ().SetCurTexIndex (d2);
        }
    
        void handleCtrllerMsg (Message msg)
        {
            var t = msg.type;
            if (t == Const.kGraphSwitch) {
                var name = (msg.data ["graph_name"]).ToString ();
                //                var name = "";
                if (name == "land_temp") {
                    //                                      Invoke ("ShowLandTemp", 0);
                    queue.Enqueue ("show_temp");
                
                } else {
                    //                                      Invoke ("ShowEarth", 0);
                    queue.Enqueue ("show_earth");
                }
            
            } else if (t == Const.kEarthMoveTo) {
                string location = (string)msg.data ["location"];
                string time = (string)msg.data ["time"];
            
                if (location != "") {
                    // us, china, vietnam, biendong, southem, northem
//                    if (location == "us") {
//                        CameraController.GetComponent<CameraController> ().PointToUS ();
//                    
//                    } else if (location == "china") {
//                        CameraController.GetComponent<CameraController> ().PointToChina ();
//                    
//                    } else if (location == "vietnam") {
//                        CameraController.GetComponent<CameraController> ().PointToVietnam ();
//                    
//                    } else if (location == "biendong") {
//                        CameraController.GetComponent<CameraController> ().PointToBienDong ();
//                    
//                    } else if (location == "southem") {
//                        CameraController.GetComponent<CameraController> ().PointToSouthem ();
//                    
//                    } else if (location == "northem") {
//                        CameraController.GetComponent<CameraController> ().PointToNorthem ();
//                    
//                    } else {
//                        print ("Not support location " + location);
//                    }
                } 
            
                if (time != "") {
                    var day = int.Parse (time.Substring (0, 2));
                    var month = int.Parse (time.Substring (3, 5));
                    var year = int.Parse (time.Substring (6, 10));
                    DateTime date = new DateTime (year, month, day);
                    var d = date.Subtract (baseDate).Days;
                
                    print ("date " + d / 5);
                    var d2 = (int)d / 5;
                    d2 = d2 < 0 ? 0 : d2;
                    d2 = d2 >= maxTime ? maxTime : d2;
                    GetMatChanger().SetCurTexIndex (d2);
                }
            
                float delta_time = (float)msg.data;
                if (delta_time > 0) {
                
                    //                                      Invoke ("TimeForward", 0);
                    queue.Enqueue ("time_forward");
                
                } else if (delta_time < 0) {
                
                    //                                      Invoke ("TimeBackward", 0);
                    queue.Enqueue ("time_backward");
                
                }
            
            } else if (t == Const.kEarthRotate) {
                var delta = (float)msg.data ["delta"];
                print ("delta " + delta);
                int deltaInt = (int)delta;
            
                if (deltaInt > 0) {
                    for (int i=0; i<deltaInt; i++) {
                        //                                              Invoke ("RotateLeft", 0);
                    
                        queue.Enqueue ("rotate_left");
                    }
                
                } else {
                    for (int i=0; i > deltaInt; i--) {
                        //                                              Invoke ("RotateRight", 0);
                        //                                              CameraController.GetComponent<CameraController> ().RotateRight (0);
                        queue.Enqueue ("rotate_right");
                    }
                }
            
            } else if (t == Const.kEarthTimeAnimation) {
            
            
            } else {
                print ("Unsupported " + t);
            }
        }
    
        // This function runs on another thread for reading socket
        void socketReader ()
        {
            while (threadRunning) {
                try {
                    var line = readSocket ();
                    if (line != "") {
                        print ("Read: " + line);
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
            if (theThread != null)
                theThread.Join (500);
        }

        E4MaterialChanger GetMatChanger(){
            return world.GetComponent<E4MaterialChanger>();
        }

        E4SwapEarth GetSwaper(){
            return world.GetComponent<E4SwapEarth>();
        }
    }
}