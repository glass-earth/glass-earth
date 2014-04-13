package me.thuanle.wrapper;


import com.leapmotion.leap.ScreenTapGesture;

/**
 * Created by thuanle on 4/9/14.
 */
public class LMScreenTapGestureWrapper extends LMGestureWrapper {

    public final LMVectorWrapper position;
    public final LMVectorWrapper direction;
    public String speed;

    public LMScreenTapGestureWrapper(ScreenTapGesture g) {
        super(g);
        position = new LMVectorWrapper(g.position());
        direction = new LMVectorWrapper(g.direction());
    }
}
