
using LitJson;
using System;

namespace Networking
{
		public class MsgPeer
		{
				public int id { get; set; }

				public string role { get; set; }

				public string status { get; set; }
		}

		public class MsgVector3
		{
				public float x { get; set; }

				public float y { get; set; }

				public float z { get; set; }

				public MsgVector3 (float x, float y, float z)
				{
						this.x = x;
						this.y = y;
						this.z = z;
				}
		}

		public class MsgLeapGesture
		{
				public MsgVector3 position { get; set; }

				public MsgVector3 direction { get; set; }

				public int id { get; set; }

				public float speed { get; set; }

				public string state { get; set; }

				public int frame_id { get; set; }

				public static MsgLeapGesture Unmarshal (JsonData data)
				{
						var leap = new MsgLeapGesture ();
						leap.position = new MsgVector3 (
				(float)data ["position"] ["x"], (float)data ["position"] ["y"], (float)data ["position"] ["z"]);
			
						leap.direction = new MsgVector3 (
				(float)data ["direction"] ["x"], (float)data ["direction"] ["y"], (float)data ["direction"] ["z"]);
			
						leap.id = data ["id"] != null ? (int)data ["id"] : 0;
						leap.speed = data ["speed"] != null ? (float)data ["speed"] : 0.0f;
						leap.state = data ["state"] != null ? (string)data ["state"] : Const.kStateInvalid;
						leap.frame_id = data ["frame_id"] != null ? (int)data ["frame_id"] : 0;

						return leap;
				}

				public JsonData Marshal ()
				{
						var data = new JsonData ();
						data ["position"] = new JsonData ();
						data ["position"] ["x"] = this.position.x;
						data ["position"] ["y"] = this.position.y;
						data ["position"] ["z"] = this.position.z;

						data ["direction"] = new JsonData ();
						data ["direction"] ["x"] = this.direction.x;
						data ["direction"] ["y"] = this.direction.y;
						data ["direction"] ["z"] = this.direction.z;

						data ["id"] = this.id;
						data ["speed"] = this.speed;
						data ["state"] = this.state;
						data ["frame_id"] = this.frame_id;

						return data;
				}
		}

		public class MsgLeapHand
		{
				// TODO
		}

		public class Const
		{
				public static readonly string kHandshake = "handshake";
				public static readonly string kHandshakeConnect = "handshake/connect";
				public static readonly string kHandshakeReconnect = "handshake/reconnect";
				public static readonly string kHandshakeClose = "handshake/close";
				public static readonly string kHandshakeError = "handshake/error";
				public static readonly string kHandshakeAccept = "handshake/accept";
		public static readonly string kGraphSwitch = "graph/switch";
		public static readonly string kEarthMoveTo = "earth/move_to";
		public static readonly string kEarthRotate = "earth/rotate";
		public static readonly string kEarthTimeAnimation = "earth/time_animation";
				public static readonly string kAck = "ack";
				public static readonly string kRoleApp = "app";
				public static readonly string kRoleServer = "server";
				public static readonly string kRoleLeap = "leap";
				public static readonly string kRoleController = "controller";
				public static readonly string kLeapGesture = "leap/gesture";
				public static readonly string kLeapGestureSwipe = "leap/gesture/swipe";
				public static readonly string kLeapGestureKeyTap = "leap/gesture/keytap";
				public static readonly string kLeapGestureScreenTap = "leap/gesture/screen_tap"; // (?)
				public static readonly string kLeapHand = "leap/hand";
				public static readonly string kStateInvalid = "STATE_INVALID";
				public static readonly string kStateStart = "STATE_START";
				public static readonly string kStateUpdate = "STATE_UPDATE";
				public static readonly string kStateStop = "STATE_STOP";
		}

		public class Message
		{		
				public string type { get; set; }

				public string role { get; set; }

				public string channel_name { get; set; }

				public string description { get; set; }

				public int from_id { get; set; }

				public int peer_id { get; set; }

				public int to_id { get; set; }

				public string to { get; set; }

				public MsgPeer[] peers { get; set; }

				public JsonData data { get; set; }

				private MsgLeapGesture data_leap_gesture;
				private MsgLeapHand data_leap_hand;

				public Message ()
				{

				}

				public Message (string type, string role)
				{
						this.type = type;
						this.role = role;
				}

				public override string ToString ()
				{
						return Marshal ();
				}
		
				public static Message Unmarshal (string json)
				{
			UnityEngine.Debug.Log("abc");
			try {
								Message msg = JsonMapper.ToObject<Message> (json);
								return msg;
						} catch (Exception e) {
				UnityEngine.Debug.Log(e);
						}

						return null;
				}
		
				public string Marshal ()
				{
						try {
								return JsonMapper.ToJson (this);
						} catch (Exception e) {
								Console.WriteLine (e);
						}
						return null;
				}

				public MsgLeapGesture GetLeapGestureData ()
				{
						if (this.data_leap_gesture != null) {
								return this.data_leap_gesture;
						}

						if (this.type.StartsWith (Const.kLeapGesture)) {
								try {										
										this.data_leap_gesture = MsgLeapGesture.Unmarshal (this.data);
										return this.data_leap_gesture;

								} catch (Exception e) {
										Console.Error.WriteLine (e);
								}
						}

						return null;
				}

				public void SetLeapGestureData (MsgLeapGesture leap)
				{
						if (this.type.StartsWith (Const.kLeapGesture)) {
								this.data_leap_gesture = leap;

						} else {
								Console.Error.WriteLine ("Can not SetLeapGestureData to type:" + this.type);
						}
				}

				public MsgLeapHand GetLeapHandData ()
				{
						if (this.type == Const.kLeapHand) {
							// TODO
						}

						return null;
				}
		}
	
}


