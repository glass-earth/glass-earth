package me.thuanle.astronomer.connector;

/**
 * Created by thuanle on 4/10/14.
 */
public abstract class ASTProtocol {

    public static class Type {

        public static class Handshake {

            public static final String TYPE_CONNECT = "handshake/connect";
            public static final String TYPE_ACCEPT = "handshake/accept";
            public static final String TYPE_ERROR = "handshake/error";
            public static final String TYPE_RECONNECT = "handshake/reconnect";
            public static final String TYPE_CLOSE = "handshake/close";
            public static final String TYPE_PEERS = "handshake/peers";
        }

        public static class Leap {

            public static class Gesture {

                public static final String TYPE_SWIPE = "leap/gesture/swipe";
                public static final String TYPE_SCREEN_TAP="leap/gesture/screentap";
                public static final String TYPE_KEY_TAP = "leap/gesture/keytap";
            }

            public static class Pointable{

                public static final String TYPE_POINTABLE = "leap/pointable";
            }
        }

    }

    public static class Role {

        public static final String ROLE_LEAP = "leap";
        public static final String ROLE_CONTROLLER = "leap";
    }


    public String type;
    public String role;
    public String channel_name;
    public String peer_id;
    public String description;
    public String from_id;

    public ASTProtocol(String type) {
        this.type = type;
    }

    public abstract String toJson();
}
