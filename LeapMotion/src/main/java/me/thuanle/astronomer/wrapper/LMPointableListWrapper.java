package me.thuanle.astronomer.wrapper;

import java.util.ArrayList;

import com.leapmotion.leap.PointableList;
import me.thuanle.astronomer.connector.ASTRequest;

/**
 * Created by thuanle on 4/13/14.
 */
public class LMPointableListWrapper extends ASTRequest.Data{
    public ArrayList<LMPointableWrapper> list;

    public LMPointableListWrapper(PointableList plist) {
        list = new ArrayList<LMPointableWrapper>(plist.count());

        for (int i = 0; i<plist.count(); i++){
            list.add(new LMPointableWrapper(plist.get(i)));
        }
    }
}
