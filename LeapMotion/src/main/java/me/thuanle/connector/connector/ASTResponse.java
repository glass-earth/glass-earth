package me.thuanle.connector.connector;

import com.google.gson.Gson;

/**
 * Created by thuanle on 4/10/14.
 */
public class ASTResponse extends ASTProtocol {

    private static final ASTResponse _DUMMY_RESPONSE = new ASTResponse("");
    public String raw_request;

    public ASTResponse(String type) {
        super(type);
    }

    public static ASTResponse getDummyResponse() {
        return _DUMMY_RESPONSE;
    }

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
