package me.thuanle.wrapper;


import com.leapmotion.leap.KeyTapGesture;

/**
 * Created by thuanle on 4/9/14.
 */
public class LMKeyTapGestureWrapper extends LMGestureWrapper {

    public final LMVectorWrapper position;
    public final LMVectorWrapper direction;

    public LMKeyTapGestureWrapper(KeyTapGesture g) {
        super(g);
        position = new LMVectorWrapper(g.position());
        direction = new LMVectorWrapper(g.direction());
    }
}
