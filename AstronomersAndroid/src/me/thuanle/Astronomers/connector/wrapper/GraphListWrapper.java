package me.thuanle.astronomers.connector.wrapper;

import java.util.ArrayList;

import me.thuanle.astronomers.connector.ASTRequest;
import me.thuanle.astronomers.connector.ASTResponse;

/**
 * Created by thuanle on 4/13/14.
 */
public class GraphListWrapper implements ASTRequest.Data{
    public ArrayList<GraphWrapper> list;
}
