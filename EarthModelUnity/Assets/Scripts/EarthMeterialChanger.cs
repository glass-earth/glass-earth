using UnityEngine;
using System.Collections;
using System;

public class EarthMeterialChanger : MonoBehaviour {

	public GameObject earth;
	public GameObject earthL;





	public float duration = .5f;
	public bool stopAtFloor = false;

	public float counter = 0f;
	private int intCounter = -1;
	private int from;
	private int to;

	public bool triggerChange = true;

	public Texture[] texEarths;
	private Texture[] texLandTemps;
	private Texture[] texSnowCovers;
	private Texture []textures;
	private Texture[] texturesL;

	public Light light;
	public Light lightL;
	public Light lightR;
	public Light lightF;
	public Light lightB;

    private int matState = -1;
    private const int kMatStateNone = -1;
    private const int kMatStateLandTemp = 1;
    private const int kMatStateSnowCover = 2;
    private const int kMatStateEarth = 3;

    private const int kMatStateRunTimeline = 100;
    private const int kMatStateStopTimeline = 101;


	private DateTime date852012 = new DateTime (2012, 5, 8);
	// Use this for initialization
	void Start () {
		Blend(0f);
		from = 0;
		to = textures.Length;

		texLandTemps = Resources.LoadAll<Texture> ("land_temp") ;
		texSnowCovers = Resources.LoadAll<Texture> ("snow_cover");
		print ("number of land: " + texLandTemps.Length);
		print ("number of snow: " + texSnowCovers.Length);

		textures = texEarths;

	}

	void Awake()
	{
		textures = texEarths;
		texturesL = texEarths;
	}
	
	// Update is called once per frame
	void Update () {
//        print("state = " + matState);
		if (Input.GetKey (KeyCode.C) || matState == kMatStateRunTimeline) {
			RunTimeline ();
            matState = kMatStateNone;
		} else if (Input.GetKey (KeyCode.V) || matState == kMatStateStopTimeline) {
			StopTimeline ();
		} else if (Input.GetKey (KeyCode.B) || matState == kMatStateLandTemp) {
			DoShowLandTemp ();
			print ("Land temp");
			print ("texture length = " + textures.Length);
            matState = kMatStateNone;
		} else if (Input.GetKey (KeyCode.N) || matState == kMatStateEarth) {
			DoShowEarth ();
			print ("Earth temp");
			print ("texture length = " + textures.Length);
            matState = kMatStateNone;
		} 

		if(triggerChange){
			counter += Time.deltaTime / duration;
			if(stopAtFloor && counter - intCounter >= 1){
				triggerChange = false;
				counter = intCounter + 1;
			}
			Blend(counter);
			if (counter >= textures.Length) {
				counter = 0;
			}
		}


//		Blend(counter);
	}

	public void SetTexIndex(int index)
	{
		if (index >= 0 && index < textures.Length) {
			Blend (index);
		}
	}

    public void ShowLandTemp(){
        matState = kMatStateLandTemp;
        print("Con heo: state = " + matState);
    }

    public void ShowSnowCover(){
        matState = kMatStateSnowCover;
    }

    public void ShowEarth(){
        matState = kMatStateEarth;
    }

	private void DoShowLandTemp()
	{
        print("con heo");
		ShowTex (texLandTemps, texSnowCovers);
		light.intensity = 0f;
		lightL.intensity = 0f;
		lightR.intensity = 0f;
		lightF.intensity = 0f;
		lightB.intensity = 0f;
	}


	public void DoShowSnowCover()
	{
		ShowTex (texSnowCovers, texLandTemps);
		light.intensity = 0f;
	}

	public void DoShowEarth()
	{
		ShowTex (texEarths, texEarths);
		light.intensity = 1.15f;
		lightL.intensity = 1.15f;
		lightR.intensity = 1.15f;
		lightF.intensity = 1.15f;
		lightB.intensity = 1.15f;

	}

	private void ShowTex(Texture[] texes, Texture[] texesL)
	{
		textures = texes;
		texturesL = texesL;
		counter = 0;
		intCounter = -1;
	    triggerChange = true;
	}

	public void RunTimeline()
	{
		triggerChange = true;
	}

	public void StopTimeline()
	{
		triggerChange = false;
	}

	void Blend(float blend)
	{
		int floor = (int)blend;
		float percent = blend - floor;
//		print ("Tex 1 = " + (floor % textures.Length) + "   tex 2 = " + ((floor + 1) % textures.Length));
		if(floor != intCounter){
			earth.renderer.material.SetTexture("_MainTex", textures[floor % textures.Length]);
			earth.renderer.material.SetTexture("_Texture2", textures[(floor + 1) % textures.Length]);

			earthL.renderer.material.SetTexture("_MainTex", texturesL[floor % textures.Length]);
			earthL.renderer.material.SetTexture("_Texture2", texturesL[(floor + 1) % textures.Length]);
			intCounter = floor;

		}
		earth.renderer.material.SetFloat("_Blend", percent);
		earthL.renderer.material.SetFloat("_Blend", percent);
	}


    public int getCurIndex(){
        return ((int)counter) % textures.Length;
    }
}
