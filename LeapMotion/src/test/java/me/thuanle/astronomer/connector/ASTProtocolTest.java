package me.thuanle.astronomer.connector;

import com.google.gson.Gson;
import me.thuanle.astronomer.connector.ASTProtocol;
import me.thuanle.astronomer.connector.ASTRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by thuanle on 4/10/14.
 */
public class ASTProtocolTest {

    private static ASTProtocol ptc;
    private String ptcJSON;

    @Before
     public void init(){
        ptc=new ASTRequest(ASTProtocol.Type.Handshake.TYPE_CONNECT);
        ptc.channel_name="group-1";
        ptcJSON = "{\"type\":\"handshake/connect\",\"role\":\"app\",\"channel_name\":\"group-1\"}";
    }

    @Test
    public void testJsonMarshall(){
        Gson gson = new Gson();
        String json = gson.toJson(ptc,ASTProtocol.class);
        Assert.assertTrue("json mismatched: " + json + " -> " + ptcJSON, ptcJSON.equals(json));
    }

    @Test
    public void testJsonUnmarshall(){
        Gson gson = new Gson();
        String json = "{}";
        ASTProtocol msg = gson.fromJson(json,ASTProtocol.class);
        Assert.assertNotNull(msg);
        System.out.println(msg.toJson());
    }

}
