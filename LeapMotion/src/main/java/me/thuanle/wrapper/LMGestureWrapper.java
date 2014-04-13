package me.thuanle.wrapper;


import com.leapmotion.leap.Gesture;
import me.thuanle.connector.connector.ASTRequest;

/**
 * Created by thuanle on 4/11/14.
 */
public class LMGestureWrapper extends ASTRequest.Data{

    public String state;

    public LMGestureWrapper(Gesture g) {
        super(g.id(),g.frame().id());
        state = g.state().name();
    }
}
