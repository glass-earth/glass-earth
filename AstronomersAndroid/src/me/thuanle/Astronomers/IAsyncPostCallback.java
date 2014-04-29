package me.thuanle.astronomers;

import me.thuanle.astronomers.connector.ASTRequest;
import me.thuanle.astronomers.connector.ASTResponse;

/**
* Created by thuanle on 4/13/14.
*/
public interface IAsyncPostCallback {

    boolean onReceiveResponse(ASTRequest request, ASTResponse response);
}
