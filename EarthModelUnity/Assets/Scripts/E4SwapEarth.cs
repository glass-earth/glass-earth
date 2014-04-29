using UnityEngine;
using System.Collections;

public class E4SwapEarth : MonoBehaviour {
    public static int kStateNone = -1;
    public static int kStateRotateLeft = 1;
    public static int kStateRotateRight = 2;
    public static int kStateRotateUp = 3;
    public static int kStateRotateDown = 4;
    public static int kStateResetRotate = 5;
    public static int kStateSwap0 = 6;
    public static int kStateSwap1 = 7;
    public static int kStateSwap2 = 8;
    public static int kStateSet0 = 9;
    public static int kStateSet1 = 10;
    public static int kStateSet2 = 11;
    public static int kStateSet3 = 12;



    public GameObject []earths;
    private int[] mapEarth = {1,2,3};
    private int curIndex = 0;
    public GameObject curEarth;
    private GameObject curSwap;
    private Transform curTE;
    private Transform curTS;


    private bool swaping = false;
    public float swapTime = .3f;
    private Vector3 swapCurPos;
    private Vector3 swapSwapPos;
    private Vector3 swapCurDirection;
    private Vector3 swapSwapDirection;

    private float swapCurScale;
    private float swapSwapScale;
    private float swapCurScaleSpeed;
    private float swapSwapScaleSpeed;


    public float rotateSpeed = 30f;

    private int state = kStateNone;

	
	// Update is called once per frame
	void Update () {

	    if(Input.GetKey(KeyCode.Q) || state == kStateSwap0){
            print("Q: " + swaping + "  " + (curEarth == earths[0]));
            if(!swaping){
                int tmp = mapEarth[0];
                initSwap(earths[tmp]);
                mapEarth[0] = curIndex;
                curIndex = tmp;
                GetComponent<E4MaterialChanger>().SetCurIndex(curIndex);
            }

            state = kStateNone;
        }
        else if(Input.GetKey(KeyCode.W) || state == kStateSwap1){
            print("W: " + swaping + "  " + (curEarth == earths[1]));
            if(!swaping){
                int tmp = mapEarth[1];
                initSwap(earths[tmp]);
                mapEarth[1] = curIndex;
                curIndex = tmp;
                GetComponent<E4MaterialChanger>().SetCurIndex(curIndex);
            }

            state = kStateNone;
        }
        else if(Input.GetKey(KeyCode.E) || state == kStateSwap2){
            print("E: " + swaping + "  " + (curEarth == earths[2]));
            if(!swaping){
                int tmp = mapEarth[2];
                initSwap(earths[tmp]);
                mapEarth[2] = curIndex;
                curIndex = tmp;
                GetComponent<E4MaterialChanger>().SetCurIndex(curIndex);
            }
            state = kStateNone;
        }
        else if(state == kStateSet0){
            if(!swaping){
                if(curIndex != 0){
                    for(int i = 0; i < mapEarth.Length; i++){
                        if(mapEarth[i] == 0){
                            mapEarth[i] = curIndex;
                        }
                    }
                    initSwap(earths[0]);
                    curIndex = 0;
                    GetComponent<E4MaterialChanger>().SetCurIndex(curIndex);
                }
            }

            state = kStateNone;
        }
        else if(state == kStateSet1){
            if(!swaping){
                if(curIndex != 1){
                    for(int i = 0; i < mapEarth.Length; i++){
                        if(mapEarth[i] == 1){
                            mapEarth[i] = curIndex;
                        }
                    }
                    initSwap(earths[1]);
                    curIndex = 1;
                    GetComponent<E4MaterialChanger>().SetCurIndex(curIndex);
                }
            }
            
            state = kStateNone;
        }
        else if(state == kStateSet2){
            if(!swaping){
                if(curIndex != 2){
                    for(int i = 0; i < mapEarth.Length; i++){
                        if(mapEarth[i] == 2){
                            mapEarth[i] = curIndex;
                        }
                    }
                    initSwap(earths[2]);
                    curIndex = 2;
                    GetComponent<E4MaterialChanger>().SetCurIndex(curIndex);
                }
            }
            
            state = kStateNone;
        }
        else if(state == kStateSet3){
            if(!swaping){
                if(curIndex != 3){
                    for(int i = 0; i < mapEarth.Length; i++){
                        if(mapEarth[i] == 3){
                            mapEarth[i] = curIndex;
                        }
                    }
                    initSwap(earths[3]);
                    curIndex = 3;
                    GetComponent<E4MaterialChanger>().SetCurIndex(curIndex);
                }
            }
            
            state = kStateNone;
        }
        else if(Input.GetKey(KeyCode.LeftArrow) || state == kStateRotateLeft){
            rotateLeft();

            state = kStateNone;
        }
        else if(Input.GetKey(KeyCode.RightArrow) || state == kStateRotateRight){
            rotateRight();

            state = kStateNone;
        }
        else if(Input.GetKey(KeyCode.UpArrow) || state == kStateRotateUp){
            rotateUp();

            state = kStateNone;
        }
        else if(Input.GetKey(KeyCode.DownArrow) || state == kStateRotateDown){
            rotateDown();

            state = kStateNone;
        }
        else if(Input.GetKey(KeyCode.Escape) || state == kStateResetRotate){
            ResetRotate();

            state = kStateNone;
        }

        if(swaping){
            swap();
        }
	}

    void initSwap(GameObject swap){
        curSwap = swap;
        swaping = true;
        swapCurPos = curEarth.transform.position;
        swapSwapPos = curSwap.transform.position;
        swapSwapDirection = (swapCurPos - swapSwapPos)/swapTime;
        swapCurDirection = (swapSwapPos - swapCurPos)/swapTime;

        curTE = curEarth.transform.Find("ECC/Earth");
        curTS = curSwap.transform.Find("ECC/Earth");
        swapCurScale = curTE.localScale.x;
        swapSwapScale = curTS.localScale.x;
        swapCurScaleSpeed = (swapSwapScale - swapCurScale)/swapTime;
        swapSwapScaleSpeed = (swapCurScale - swapSwapScale)/swapTime;
    }


    float timeSwapCounter = 0;
    void swap(){

        timeSwapCounter += Time.deltaTime;
        if(timeSwapCounter < swapTime){
            curSwap.transform.Translate(swapSwapDirection * Time.deltaTime);
            curEarth.transform.Translate(swapCurDirection * Time.deltaTime);
            curTE.localScale += Vector3.one * swapCurScaleSpeed * Time.deltaTime;
            curTS.localScale += Vector3.one * swapSwapScaleSpeed * Time.deltaTime;
        }
        else{
            swaping = false;
            curSwap.transform.position = swapCurPos;
            curEarth.transform.position = swapSwapPos;
            curTE.localScale = Vector3.one * swapSwapScale;
            curTS.localScale = Vector3.one * swapCurScale;
            curEarth = curSwap;
            curSwap = curEarth;
            timeSwapCounter = 0;
        }
    }

    void rotate(Vector3 v){
        Vector3 rot = v * rotateSpeed * Time.deltaTime;
        for(int i = 0; i < earths.Length; i++){
            Transform t = earths[i].transform.Find("ECC");
            t.Rotate(rot);
        }
    }

    void rotateUp(){
        rotate (Vector3.right);
    }

    void rotateDown(){
        rotate (Vector3.left);
    }

    void rotateLeft(){
        rotate (Vector3.up);
    }

    void rotateRight(){
        rotate (Vector3.down);
    }

    public void ResetRotate(){
        for(int i = 0; i < earths.Length; i++){
            Transform t = earths[i].transform.Find("ECC");
            t.rotation = Quaternion.Euler(0,0,0);
        }
    }

    public int GetCurIndex(){
        return curIndex;
    }

    public void SetRotateLeftRight(bool left){
        this.state = left?kStateRotateLeft:kStateRotateRight;
    }

    public void SetRotateUpDown(bool up){
        this.state = up?kStateRotateUp:kStateRotateDown;
    }

    public void SetAutoSpin(bool on){
        for(int i = 0; i < earths.Length; i++){
            Transform t = earths[i].transform.Find("ECC/Earth");
            t.GetComponent<E4Rotator>().SetRotate(on);
        }
    }

    public void SwapEarth(int index){
        if(index >= 0 && index < mapEarth.Length){

        }
    }

    public void SetEarth(int index){
        if(index >= 0 && index < earths.Length){
            switch(index){
            case 0:
                state = kStateSet0;
                break;
            case 1:
                state = kStateSet1;
                break;
            case 2:
                state = kStateSet2;
                break;
            case 3:
                state = kStateSet3;
                break;
            default:
                print ("Not yet supported");
                break;
            }
        }
    }
}
