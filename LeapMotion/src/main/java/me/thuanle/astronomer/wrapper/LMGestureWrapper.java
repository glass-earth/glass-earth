package me.thuanle.astronomer.wrapper;


import com.leapmotion.leap.Gesture;
import me.thuanle.astronomer.connector.ASTRequest;

/**
 * Created by thuanle on 4/11/14.
 */
public class LMGestureWrapper extends ASTRequest.Data{

    public int id;
    public long frame_id;

    public LMGestureWrapper(Gesture g) {
        id = g.id();
        frame_id = g.frame().id();
    }
}
