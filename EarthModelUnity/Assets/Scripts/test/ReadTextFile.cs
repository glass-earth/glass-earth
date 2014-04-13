using UnityEngine;
using System.Collections;
using System.IO;
using SimpleJSON;
using Utilities;

public class ReadTextFile : MonoBehaviour {
	Vector3 [][]countries;
	// Use this for initialization
	void Start () {
		print(Utils.ReadTextFile("test.txt"));

		JSONNode json = Utils.ReadJSonFile("geoData.json");
		print(json["type"]);

		JSONArray coords = (JSONArray)json["geometry"]["coordinates"];
		countries = new Vector3[coords.Count][];
		for(int i = coords.Count; i >= 0; --i){
			JSONArray nodes = (JSONArray)coords[i][0];
			countries[i] = new Vector3[nodes.Count];

			for(var j = nodes.Count; j >= 0; --j){
				JSONArray n = (JSONArray)nodes[j];
				countries[i][j] = MathUtils.LatLong2Vector3(n[0].AsFloat, n[1].AsFloat);
			}
		}

	}
	
	// Update is called once per frame
	void Update () {
		
	}

	private string getAsset(string filename){
		return System.IO.Path.Combine(Application.streamingAssetsPath, filename);
	}
}
