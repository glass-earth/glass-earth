
using System;
using UnityEngine;

namespace Networking
{
    public class NetworkE1 : MonoBehaviour
    {
        public GameObject earthController;
        public GameObject cameraController;
        public GameObject networkManager;
        ControlE1 ctrl;

        public NetworkE1 ()
        {

        }

        void Start ()
        {
            ctrl = new ControlE1 ();
            ctrl.earthController = earthController.GetComponent<EarthMeterialChanger> ();
            ctrl.cameraController = cameraController.GetComponent<CameraController> ();
        }
        
        void OnGUI ()
        {
            Debug.Log ("Set CtrlE1");
            NetworkManager.setController (ctrl);
        }
        
        void Update ()
        {

        }
        
        void FixedUpdate ()
        {

        }
    }

    public class ControlE1 : ControlInterface
    {
        public EarthMeterialChanger earthController;
        public CameraController cameraController;

        public ControlE1 ()
        {
        }

        public override CtrlAction handleCtrlMsg(Message msg) {
            // Do nothing
            return null;
        }

        public  CtrlAction handleCtrlMsg_backup (Message msg)
        {

                var t = msg.type;
                if (t == Const.kGraphSwitch) {
                    var name = (msg.data ["graph_name"]).ToString ();
                    if (name == "land_temp") {
                        return new CtrlAction (ShowLandTemp);
                        
                    } else if (name == "snow_cover") {
                        return new CtrlAction (ShowSnowCover);
                        
                    } else {
                        return new CtrlAction (ShowEarth);                    
                    }
                    
                } else if (t == Const.kGraphWordmap) {
                    var enabled = (bool)msg.data ["enabled"];
                    if (enabled) {
                        return new CtrlAction (OpenWorldmap);
                        
                    } else {
                        return new CtrlAction (CloseWorldmap);
                    }
                    
                } else if (t == Const.kEarthMoveTo) {
                    string location = (string)msg.data ["location"];
                    string time = (string)msg.data ["time"];
                    
                    CtrlAction moveTo = null;
                    if (location != "") {
                        
                        // us, china, vietnam, biendong, southem, northem
                        if (location == "us") {
                            moveTo = delegate {
                                cameraController.PointToUS ();
                            };
                            
                        } else if (location == "china") {
                            moveTo = delegate {
                                cameraController.PointToChina ();
                            };
                            
                        } else if (location == "vietnam") {
                            moveTo = delegate {
                                cameraController.PointToVietnam ();
                            };
                            
                        } else if (location == "biendong") {
                            moveTo = delegate {
                                cameraController.PointToBienDong ();
                            };
                            
                        } else if (location == "southem") {
                            moveTo = delegate {
                                cameraController.PointToSouthem ();
                            };
                            
                        } else if (location == "northem") {
                            moveTo = delegate {
                                cameraController.PointToNorthem ();
                            };
                            
                        } else {
                            Debug.Log ("Not support location " + location);
                        }
                    }
                    if (moveTo != null) {
                        return moveTo;
                    }
                    
                    if (time != "") {
                        var day = int.Parse (time.Substring (0, 2));
                        var month = int.Parse (time.Substring (3, 5));
                        var year = int.Parse (time.Substring (6, 10));
                        DateTime date = new DateTime (year, month, day);
                        var d = date.Subtract (baseDate).Days;
                        
                        Debug.Log ("date " + d / 5);
                        var d2 = (int)d / 5;
                        d2 = d2 < 0 ? 0 : d2;
                        d2 = d2 >= maxTime ? maxTime : d2;
                        earthController.SetTexIndex (d2);
                    }
                    
                    float delta_time = (float)msg.data;
                    if (delta_time > 0) {
                        return new CtrlAction (TimeForward);
                        
                    } else if (delta_time < 0) {
                        return new CtrlAction (TimeBackward);
                        
                    }
                    
                } else if (t == Const.kEarthRotate) {
                    
                    var velocity = (float)msg.data ["velocity"];
                    if (velocity > 0.01) {
                        return new CtrlAction (StartRotation);
                        
                    } else {
                        return new CtrlAction (StopRotation);
                    }
                    
//                var delta = (float)msg.data ["delta"];
//                int deltaInt = (int)delta;
//
//                if (deltaInt > 0) {
//                    for (int i=0; i<deltaInt && i < 20; i++) {
//                        returnnew CtrlAction(RotateLeft));
//                    }
//
//                } else {
//                    for (int i=0; i > deltaInt && i > -20; i--) {
//                        returnnew CtrlAction(RotateLeft));
//                    }
//                }
                    
                } else if (t == Const.kEarthTimeAnimation) {
                    // TODO
                    
                } else {
                    Debug.Log ("Unsupported " + t);
                }

            return null;
        }

        const int maxTime = 141;
        DateTime baseDate = new DateTime (2012, 05, 08);
        
        void ShowLandTemp ()
        {
            Debug.Log ("show land temp");
            earthController.ShowLandTemp ();
        }
        
        void ShowEarth ()
        {
            Debug.Log ("show earth");
            earthController.ShowEarth ();
        }
        
        void ShowSnowCover ()
        {
            Debug.Log ("show snow cover");
            earthController.ShowSnowCover ();
        }
        
        void OpenWorldmap ()
        {
            Debug.Log ("open world map");
            // TODO
        }
        
        void CloseWorldmap ()
        {
            Debug.Log ("close world map");
            // TODO
        }
        
        void StartRotation ()
        {
            Debug.Log ("start rotation");
        }
        
        void StopRotation ()
        {
            Debug.Log ("stop rotation");
        }
        
        void StartAnimation ()
        {
            Debug.Log ("start animation");
        }
        
        void StopAnimation ()
        {
            Debug.Log ("stop animation");
        }
        
        void RotateLeft ()
        {
            cameraController.RotateLeft (0);
        }
        
        void RotateRight ()
        {
            cameraController.RotateRight (0);
        }
        
        void TimeForward ()
        {
            var d = earthController.getCurIndex ();
            var d2 = d + 1;
            d2 = d2 < 0 ? 0 : d2;
            d2 = d2 >= maxTime ? maxTime : d2;
            earthController.SetTexIndex (d2);
        }
        
        void TimeBackward ()
        {
            var d = earthController.getCurIndex ();
            var d2 = d - 1;
            d2 = d2 < 0 ? 0 : d2;
            d2 = d2 >= maxTime ? maxTime : d2;
            earthController.SetTexIndex (d2);
        }
    }
}

