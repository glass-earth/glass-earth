
using System;
using UnityEngine;

namespace Networking
{
    public delegate void CtrlAction ();

    public abstract class ControlInterface
    {
        public ControlInterface ()
        {
     
        }

        public abstract CtrlAction handleCtrlMsg (Message msg);
    }
}

