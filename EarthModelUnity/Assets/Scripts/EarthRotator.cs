using UnityEngine;
using System.Collections;


public class EarthRotator : MonoBehaviour {
	public float rotateSpeed = 1f;
	public float resetSpeed = 2f;



    public bool isRotating = true;
	private Quaternion rot0;




	void Start()
	{
		rot0 = transform.rotation;
	}

	void FixedUpdate ()
	{
		ToggleRotate ();
		DoRotate ();
	}

	void Reset()
	{
		isRotating = false;
//		ToggleRotate ();
//		DoRotate ();
	}

	void ToggleRotate()
	{
		if (Input.GetKeyDown (KeyCode.Space)) 
		{
			Debug.Log("isRotating = " + isRotating);
			isRotating = !isRotating;
//			if(!isRotating)
//			{
//			
//			}
		}

	}

	public void SetRotateOn()
	{
		isRotating = true;
	}

	public void SetRotateOff()
	{
		isRotating = false;
	}

	void DoRotate()
	{
		if(isRotating)
		{
            transform.Rotate (0f, -rotateSpeed * Time.deltaTime, 0);
		}
		else
		{
//			Quaternion target = Quaternion.Euler(12.0872f, 216.9453f, 8.949478f); 
			Quaternion target = rot0;
			transform.rotation = Quaternion.Slerp( transform.rotation, target,resetSpeed * Time.deltaTime );
		}
	}


}
