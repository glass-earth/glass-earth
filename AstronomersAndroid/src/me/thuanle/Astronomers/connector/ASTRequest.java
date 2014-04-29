package me.thuanle.astronomers.connector;

import com.google.gson.Gson;

/**
 * Created by thuanle on 4/10/14.
 */
public class ASTRequest extends ASTProtocol {

    public static interface Data{
    }

    public Data data;
    public String to;

    public ASTRequest(String type) {
        super(type);

        role = Role.ROLE_CONTROLLER;
        to =ASTConnector.DEFAULT_TO;
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
