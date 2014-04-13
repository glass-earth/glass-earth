using UnityEngine;
using System.Collections;

public class CameraController : MonoBehaviour {
	public float rotateSpeed = 50f;
	public float resetSpeed = 10f;
	private bool isReset = false;
	public float zoomSpeed = 5f;

	public GameObject earth;

	public GameObject mainCamera;
	public GameObject leftCamera;
	public GameObject rightCamera;
	public GameObject backCamera;
	public GameObject forwardCamera;

	private Quaternion camRot0;
	private Vector3 camPos0;

	private Quaternion rotChina;
	private Quaternion rotVietnam;
	private Quaternion rotUS;
	private Quaternion rotBienDong;
	private Quaternion rotNorthm;
	private Quaternion rotSouthm;

	private Quaternion resetRot;

	void Start()
	{
		camRot0 = transform.rotation;
		camPos0 = new Vector3(0,0,-2);

		rotChina = Quaternion.Euler (28.95647f, 3.429099f, 1.6617f);
		rotVietnam = Quaternion.Euler (1.966047f, 2.674344f, 1.454777f);
		rotBienDong = Quaternion.Euler (357.007f, 354.8107f, 358.8406f);
		rotUS = Quaternion.Euler (51.05462f, 208.6768f, 10.96412f);
		rotNorthm = Quaternion.Euler (73.17585f, 355.1109f, 3.637074f);
		rotSouthm = Quaternion.Euler (284.8241f, 175.1109f, 0f);


	}

	// Update is called once per frame
	void FixedUpdate () 
	{
		OnInput();
	}

	void OnInput()
	{
		if (Input.GetKey (KeyCode.Escape)) {
			Reset ();
		} else if (Input.GetKey (KeyCode.LeftArrow)) {
			RotateUp (1);
			isReset = false;
		} else if (Input.GetKey (KeyCode.RightArrow)) {
			RotateDown (1);
			isReset = false;
		} else if (Input.GetKey (KeyCode.UpArrow)) {
			RotateLeft (1);
			isReset = false;
		} else if (Input.GetKey (KeyCode.DownArrow)) {
			RotateRight (1);
			isReset = false;
		} else if (Input.GetKey (KeyCode.Z)) {
			ZoomIn (1);
			isReset = false;
		} else if (Input.GetKey (KeyCode.X)) {
			ZoomOut (1);
			isReset = false;
		} else if (Input.GetKey (KeyCode.A)) {
			PointToChina ();
		} else if (Input.GetKey (KeyCode.S)) {
			PointToVietnam ();
		} else if (Input.GetKey (KeyCode.D)) {
			PointToBienDong ();
		} else if (Input.GetKey (KeyCode.F)) {
			PointToUS ();
		} else if (Input.GetKey (KeyCode.G)) {
			PointToNorthem ();
		} else if (Input.GetKey (KeyCode.H)) {
			PointToSouthem ();
		}




		if(isReset){
			DoResetRotation();
		}
//		print(isReset);
	}

	public void RotateLeft(float strength)
	{
		transform.Rotate(Vector3.left, rotateSpeed * Time.deltaTime * strength);
	}

    public void RotateLeft(){
        RotateLeft(1.0f);
    }

	public void RotateRight(float strength)
	{
		transform.Rotate(Vector3.right, rotateSpeed * Time.deltaTime * strength);
	}

    public void RotateRight(){
        RotateRight(1.0f);
    }

	public void RotateUp(float strength)
	{
		transform.Rotate(Vector3.up, rotateSpeed * Time.deltaTime * strength);
	}

    public void RotateUp(){
        RotateUp(1.0f);
    }

	public void RotateDown(float strength)
	{
		transform.Rotate(Vector3.down, rotateSpeed * Time.deltaTime * strength);
	}

    public void RotateDown(){
        RotateDown(1.0f);
    }

	public void ZoomIn(float strength)
	{
		Vector3 v = Vector3.forward * zoomSpeed * Time.deltaTime * strength;
		if((mainCamera.transform.position + v).magnitude > 0.9){
			mainCamera.transform.Translate(v);
            //leftCamera.transform.Translate (v);
            //rightCamera.transform.Translate (v);
            //backCamera.transform.Translate (v);
            //forwardCamera.transform.Translate (v);
		}
	}

	public void ZoomOut(float strength)
	{
		Vector3 v = -Vector3.forward * zoomSpeed * Time.deltaTime * strength;
		if((mainCamera.transform.position + v).magnitude < 4){
			mainCamera.transform.Translate(v);
            //leftCamera.transform.Translate (v);
            //rightCamera.transform.Translate (v);
            //backCamera.transform.Translate (v);
            //forwardCamera.transform.Translate (v);

		}
	}



	public void Reset()
	{
		PointTo (camRot0);
	}

	public void PointToChina(){
		PointTo (rotChina);
	}

	public void PointToVietnam(){
		PointTo (rotVietnam);
	}

	public void PointToUS(){
		PointTo (rotUS);
	}

	public void PointToBienDong(){
		PointTo (rotBienDong);
	}

	public void PointToNorthem(){
		PointTo (rotNorthm);
	}

	public void PointToSouthem(){
		PointTo (rotSouthm);
	}

	private void PointTo(Quaternion rot){
		isReset = true;
		resetRot = rot;
		earth.GetComponent<EarthRotator>().SetRotateOff ();
	}



	private void DoResetRotation(){
		transform.rotation = Quaternion.Slerp( transform.rotation, resetRot,resetSpeed * Time.deltaTime );
//		camera.transform.position = Vector3.Lerp(camera.transform.position, camPos0, resetSpeed * Time.deltaTime);
//		camera.transform.position = camPos0;
//		transform.rotation = camRot0;
	}


}
