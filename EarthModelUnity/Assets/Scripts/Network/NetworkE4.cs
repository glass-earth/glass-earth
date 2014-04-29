
using System;
using UnityEngine;

namespace Networking
{
    public class NetworkE4 : MonoBehaviour {
        public GameObject world;
        ControlE4 ctrl;

        public NetworkE4() {
        }

        void Start ()
        {
            ctrl = new ControlE4 ();
            ctrl.world = world;

            Debug.Log ("Set CtrlE4");
            NetworkManager.setController (ctrl);
        }
        
        void OnGUI ()
        {

        }
    }

    public class ControlE4 : ControlInterface
    {
        public GameObject world;

        public ControlE4 ()
        {
        }

        public override CtrlAction handleCtrlMsg(Message msg) {
            var t = msg.type;
            if (t == Const.kGraphSwitch) {
                var name = (msg.data ["graph_name"]).ToString ();
                var index = 0;
                if (name == "land_temp") {
                    index = 1;
                    
                } else if (name == "snow_cover") {
                    index = 2;
                    
                } else if (name =="precipitation") {
                    index = 3;

                } else {
                    index = 0;
                }
                return delegate{
                    SwapEarth(index);
                };
                
            } else if (t == Const.kGraphWordmap) {
                var enabled = (bool)msg.data ["enabled"];
                if (enabled) {
                    return new CtrlAction (OpenWorldmap);
                    
                } else {
                    return new CtrlAction (CloseWorldmap);
                }
                
            } else if (t == Const.kEarthRotate) {
                
                var velocity = (float)msg.data ["velocity"];
                if (velocity > 0.01) {
                    return new CtrlAction (StartRotation);
                    
                } else {
                    return new CtrlAction (StopRotation);
                }
                
            } else if (t == Const.kEarthTimeAnimation) {

                
            } else {
                Debug.Log ("Unsupported " + t);
            }

            return null;
        }

        void SwapEarth(int index)
        {
            Debug.Log ("swap earth " + index);
            world.GetComponent<E4SwapEarth> ().SetEarth (index);
        }
        
        void OpenWorldmap ()
        {
            Debug.Log ("open world map");
            world.GetComponent<E4MaterialChanger> ().SetMap (true);
        }
        
        void CloseWorldmap ()
        {
            Debug.Log ("close world map");
            world.GetComponent<E4MaterialChanger> ().SetMap (false);
        }
        
        void StartRotation ()
        {
            Debug.Log ("start rotation");
            world.GetComponent<E4SwapEarth> ().SetAutoSpin (true);
        }
        
        void StopRotation ()
        {
            Debug.Log ("stop rotation");
            world.GetComponent<E4SwapEarth> ().SetAutoSpin (false);
        }
    }
}

