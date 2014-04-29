using UnityEngine;
using System.Collections;

public class E4OnOff : MonoBehaviour {
    const int mNone = -1;
    const int mHide = 0;
    const int mShow = 1;
    const int mFlash = 2;
    const int mFlashIn = 3;
    const int mFlashOut = 4;

    public float flashSpeed = 0.6f;

    public GameObject []earths;

    int state = -1;

    bool active = true;

	// Use this for initialization
	void Start () {
	    
	}
	
	// Update is called once per frame
	void Update () {
	    if(Input.GetKey(KeyCode.B)){
            hide();
        }
        else if(Input.GetKey(KeyCode.N)){
            show ();
        }
        else if(Input.GetKeyDown(KeyCode.M)){
            hide();
        }
        else if(Input.GetKeyUp(KeyCode.M)){
            show ();
        }
	}

    void hide(){
        setActive(false);

    }

    void show(){
        setActive(true);
    }

    void toggle(){
        setActive(!active);
    }

    void setActive(bool active){
        this.active = active;

        for(int i = 0; i < earths.Length; i++){
            earths[i].SetActive(active);
        }
    }

    float timeCounter = 0;
    int count = 0;
    void flash(){
        timeCounter += Time.deltaTime;



        switch(state){
        case mFlash:

            break;
        }
    }

}
