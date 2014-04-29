using UnityEngine;
using System.Collections;

public class E4Rotator : MonoBehaviour {
    public static int kStateNone = -1;
    public static int kStateRotateOn = 1;
    public static int kStateRotateOff = 2;

    private bool isSmall = false;
    public bool isRotate = true;

    public float rotateSpeed = 20f;

    private int state = kStateNone;
	
	// Update is called once per frame
	void FixedUpdate () {
        if(Input.GetKey(KeyCode.C) || state == kStateRotateOff){
            isRotate = false;
            state = kStateNone;
        }
        else if(Input.GetKey(KeyCode.V)){
            isRotate = true;
            state = kStateNone;
        }

        Rotate();
	}

    void Rotate(){
        if(isSmall || isRotate){
            transform.Rotate (0f, -rotateSpeed * Time.deltaTime, 0);
        }

    }

    public void SetRotate(bool on){
        state = on?kStateRotateOn:kStateRotateOff;
    }
}
