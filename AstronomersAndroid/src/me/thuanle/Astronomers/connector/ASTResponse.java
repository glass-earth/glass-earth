package me.thuanle.astronomers.connector;

import java.util.ArrayList;

import com.google.gson.Gson;

/**
 * Created by thuanle on 4/10/14.
 */
public class ASTResponse extends ASTProtocol {

    private static final ASTResponse _DUMMY_RESPONSE = new ASTResponse("");

    public static class Data {
        public String[] graph_names;
        public String graph_label;
        public String graph_name;
        public String guest_url;
        public String guest_content_url;
        public String controller_url;
        public String controller_content_url;
        public String events;
    }

    public ASTResponse(String type) {
        super(type);
    }

    public static ASTResponse getDummyResponse() {
        return _DUMMY_RESPONSE;
    }

    public Data data;

    @Override
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this,ASTResponse.class);

    }

    public static final ASTResponse fromJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json,ASTResponse.class);
    }

}
