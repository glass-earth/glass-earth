using UnityEngine;
using System.Collections;

public class CameraViewPort : MonoBehaviour {
	public Camera camL;
	public Camera camR;
	public Camera camF;
	public Camera camB;


	// Use this for initialization
	void Start () {
//		int size = Mathf.Min (Screen.width, Screen.height);
		print ("before:  " + Screen.width + "  " + Screen.height);
//		Screen.SetResolution (size, size, true); 
		print ("after:  " + Screen.width + "  " + Screen.height);

		print ("cam: " + camF.rect.x + "  " + camF.rect.y + "  " + camF.rect.width + "  " + camF.rect.height);

		float SW = Screen.width;
		float SH = Screen.height;

		float ch = 0.5f;
		float cw = ch * SH / SW;


		float offsetX = 0.0f;
		float offsetY = offsetX * SH / SW;

        camL.rect = new Rect ((0.5f - cw - offsetX), camL.rect.y, cw, ch);
		camR.rect = new Rect (camR.rect.x + offsetX, camR.rect.y, cw, ch);
		camF.rect = new Rect (0.5f - cw / 2f, camF.rect.y + offsetY, cw, ch);
		camB.rect = new Rect (0.5f - cw / 2f, camB.rect.y - offsetY, cw, ch);


	}
	
	// Update is called once per frame
	void Update () {
	
	}
}
