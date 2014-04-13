using UnityEngine;
using System.Collections;

public class SetScreenResolution : MonoBehaviour {

	// Use this for initialization
	void Start () {
		int size = Mathf.Min (Screen.width, Screen.height);
		print ("before:  " + Screen.width + "  " + Screen.height);
		Screen.SetResolution (size, size, true);

	}
	
	// Update is called once per frame
	void Update () {
	
	}
}
