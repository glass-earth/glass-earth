package me.thuanle.connector.connector;

import com.google.gson.Gson;

/**
 * Created by thuanle on 4/10/14.
 */
public class ASTRequest extends ASTProtocol {

    public static class Data{

        public int id;
        public long frame_id;

        public Data(int id, long frame_id) {
            this.id = id;
            this.frame_id = frame_id;
        }
    }

    public Data data;
    public String to;

    public ASTRequest(String type) {
        super(type);

        role = Role.ROLE_LEAP;
    }

    @Override
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this,ASTRequest.class);
    }

    public static final ASTRequest fromJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json,ASTRequest.class);
    }
}
