using UnityEngine;
using System.Collections;

public class E4MaterialChanger : MonoBehaviour {
    public static int kStateNone = -1;
    public static int kStateAnimationOn = 1;
    public static int kStateAnimationOff = 2;
    public static int kStateMapOn = 3;
    public static int kStateMapOff = 4;

    public GameObject [] earths;
    public GameObject rectangle;
    public Texture[][] textures;
    public Texture baseEarthTexture;

    public float duration = 1f;
    public bool stopAtFloor = false;
    public float counter = 0f;
    private int intCounter = -1;
    public bool triggerChange = true;

    private int curIndex = 0;

    private int numTex = 141;

    private bool showRect = false;
    private float rectColor = 0;
    public float changeRectColorSpeed = 01f;

    private int state = -1;


    void Start(){
        rectangle.SetActive(false);

        textures = new Texture[4][];
       
        textures[0] = new Texture[numTex];
        for(int i = 0; i < numTex; i++){
            textures[0][i] = baseEarthTexture;
        }

        textures[1] = Resources.LoadAll<Texture>("land_temp");
        textures[2] = Resources.LoadAll<Texture>("snow_cover");
        textures[3] = Resources.LoadAll<Texture>("sea_temp");

        print ("tex length = " + textures[1].Length);

        Blend(0);
    }
	
	// Update is called once per frame
	void Update () {
        if(Input.GetKey(KeyCode.Z) || state == kStateAnimationOn){
            triggerChange = true;
            state = kStateNone;
        }
        else if(Input.GetKey(KeyCode.X) || state == kStateAnimationOff){
            triggerChange = false;
            state = kStateNone;
        }
        else if(Input.GetKey(KeyCode.A) || state == kStateMapOff){
            showRect = false;
            state = kStateNone;
        }
        else if(Input.GetKey(KeyCode.S) || state == kStateMapOn){
            showRect = true;
            rectangle.SetActive(showRect);
            state = kStateNone;
        }

        if(triggerChange){
            counter += Time.deltaTime / duration;
            if(stopAtFloor && counter - intCounter >= 1){
                triggerChange = false;
                counter = intCounter + 1;
            }
            Blend(counter);
            if (counter >= numTex) {
                counter = 0;
            }
        }

        SetRectColor();
        
        //      Blend(counter);
	}




    void Blend(float blend)
    {
        int floor = (int)blend;
        float percent = blend - floor;
        if(floor != intCounter){
//            print ("floor = " + floor);
            for(int i = 1; i < earths.Length; i++){
                Renderer r = GetRenderer(earths[i]);
                r.material.SetTexture("_MainTex", textures[i][floor % numTex]);
                r.material.SetTexture("_Texture2", textures[i][(floor + 1) % numTex]);
            }

            rectangle.renderer.material.SetTexture("_MainTex", textures[curIndex][floor % numTex]);
            rectangle.renderer.material.SetTexture("_Texture2", textures[curIndex][(floor + 1) % numTex]);

            intCounter = floor;
        }

        for(int i = 1; i < earths.Length; i++){
            GetRenderer(earths[i]).material.SetFloat("_Blend", percent);
        }

        rectangle.renderer.material.SetFloat("_Blend", percent);
    }

    void SetRectColor(){
        if(showRect){
            if(rectColor < 1){
                rectColor += Time.deltaTime/changeRectColorSpeed;

            }
            else{
                rectColor = 1f;
                //rectangle.renderer.material.color = new Color(rectColor, rectColor, rectColor);
            }
        }
        else{
            if(rectColor > 0){
                rectColor -= Time.deltaTime/changeRectColorSpeed;

            }

            else{
                rectColor = 0;
                rectangle.SetActive(showRect);
            }
        }

        rectangle.renderer.material.color = new Color(rectColor, rectColor, rectColor, rectColor);
    }

    Renderer GetRenderer(GameObject o){
        return o.renderer;
    }

    public void SetCurIndex(int curIndex){
        this.curIndex = curIndex;
    }

    public int GetCurTexIndex(){
        return ((int)counter) % textures.Length;
    }

    public void SetCurTexIndex(int index){
        if (index >= 0 && index < numTex) {
            Blend (index);
        }
    }

    public void SetMap(bool on){
        state = on?kStateMapOn:kStateMapOff;
    }

    public void SetAnimation(bool on){
        state = on?kStateAnimationOn:kStateAnimationOff;
    }
}
