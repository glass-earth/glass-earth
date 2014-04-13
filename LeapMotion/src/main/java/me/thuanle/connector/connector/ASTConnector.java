package me.thuanle.connector.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * This class is used for connection between LeapMotion and central Server
 * Created by thuanle on 4/10/14.
 */
public class ASTConnector {

    public static final Logger LOGGER = Logger.getLogger(ASTConnector.class.getName());

    public static final int SLEEP_TIMEOUT = 5000;

    public static final String DEFAULT_SERVER_IP = "192.168.1.102";
    public static final int DEFAULT_SERVER_PORT = 50505;
    private static final String DEFAULT_CHANNEL = "channel-1";
    public static final String DEFAULT_TO_ROLE = "app";
    private static final ASTConnector DEFAULT_CONNECTOR;

    static {
        ASTConnector cn = null;
        try {
            cn = new ASTConnector();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DEFAULT_CONNECTOR = cn;
    }

    private static int rID;
    private final String host;
    private final int port;
    private final String channel;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String peerId;
    public String to;

    public ASTConnector(String serverIp, int serverPort) throws IOException {
        this(serverIp, serverPort, DEFAULT_CHANNEL);
    }

    private ASTConnector() throws IOException {
        this(DEFAULT_SERVER_IP, DEFAULT_SERVER_PORT);
    }

    public ASTConnector(String host, int port, String channel) throws IOException {
        this.host = host;
        this.port = port;
        this.channel = channel;
        this.to = DEFAULT_TO_ROLE;

        initSocket();
    }

    public static final ASTConnector getDefaultConnector() {
        return DEFAULT_CONNECTOR;
    }

    public String getPeerId() {
        return peerId;
    }

    private void initSocket() throws IOException {
        LOGGER.info("Initializing connection...");
        int count = 0;
        while (count != -1) {
            count++;
            try {
                LOGGER.info("#" + count + " Connect to " + host + ":" + port + "...");
                socket = new Socket(host, port);
                out = new PrintWriter(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //handshake
                ASTRequest message = new ASTRequest(ASTProtocol.Type.Handshake.TYPE_CONNECT);

                ASTResponse result = post(message);
                if (ASTProtocol.Type.Handshake.TYPE_ACCEPT.equals(result.type)) {
                    this.peerId = result.peer_id;
                    LOGGER.info("Server connected. PeerId=" + peerId);
                    count=-1;
                    break;
                } else {
                    throw new Exception(result.toJson());
                }
            } catch (Exception e) {
                try {
                    LOGGER.warning("Connection failed. Reconnect after 5 seconds. Error: " + e.getMessage());
                    Thread.sleep(SLEEP_TIMEOUT);
                } catch (Exception ee) {
                }
            }
        }
        LOGGER.info("Connection initialized.");
    }

    public ASTResponse post(ASTRequest message) {
        //message.channel_name = channel;
        //message.from_id = this.peerId;
        String result = post(message.toJson());
        return ASTResponse.fromJson(result);
    }


    protected String post(String json) {
       try {
            int id = rID++;
            LOGGER.info("SENT #" + id + ": " + json);
            out.println(json);
            out.flush();

            String res = in.readLine();
            LOGGER.info("RECV #" + id + ": " + res);
            return res;
        } catch (IOException e) {
            return "";
        }
    }


}
