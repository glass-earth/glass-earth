package me.thuanle.wrapper;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;

/**
 * Created by thuanle on 4/9/14.
 */
public class LMHandWrapper {

    private final Vector palmPosition;
    private final Vector palmVelocity;
    private final Vector palmNormal;
    private final Vector direction;
    private final float sphereRadius;
    private final Vector sphereCenter;

    public LMHandWrapper(Hand h) {
        palmPosition = h.palmPosition();
        palmVelocity = h.palmVelocity();
        palmNormal = h.palmNormal();
        direction = h.direction();
        sphereCenter = h.sphereCenter();
        sphereRadius = h.sphereRadius();
    }
}
