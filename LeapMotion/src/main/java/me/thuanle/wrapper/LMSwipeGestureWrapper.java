package me.thuanle.wrapper;


import com.leapmotion.leap.SwipeGesture;

/**
 * Created by thuanle on 4/9/14.
 */
public class LMSwipeGestureWrapper extends LMGestureWrapper {

    public final LMVectorWrapper position;
    public final LMVectorWrapper direction;
    public String speed;

    public LMSwipeGestureWrapper(SwipeGesture g) {
        super(g);
        speed = String.valueOf(g.speed());
        position = new LMVectorWrapper(g.position());
        direction = new LMVectorWrapper(g.direction());
    }
}
