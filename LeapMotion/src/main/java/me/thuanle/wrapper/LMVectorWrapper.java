package me.thuanle.wrapper;

import com.leapmotion.leap.Vector;

/**
 * Created by thuanle on 4/11/14.
 */
public class LMVectorWrapper {

    public final float x;
    public final float y;
    public final float z;

    public LMVectorWrapper(Vector v) {
        x = v.getX();
        y = v.getY();
        z = v.getZ();
    }
}
