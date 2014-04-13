package me.thuanle.wrapper;

import com.leapmotion.leap.Pointable;
import me.thuanle.connector.connector.ASTRequest;

/**
 * Created by thuanle on 4/11/14.
 */
public class LMPointableWrapper extends ASTRequest.Data {

    public static class Type {

        public static final String TYPE_TOOL = "tool";
        public static final String TYPE_FINGER = "finger";
    }

    private final LMVectorWrapper tipPosition;
    private final float length;
    private final float width;
    private final LMVectorWrapper direction;
    private final LMVectorWrapper tipVelocity;
    private final String type;

    public LMPointableWrapper(Pointable pointable) {
        super(pointable.id(), pointable.frame().id());

        length = pointable.length();
        width = pointable.width();
        direction = new LMVectorWrapper(pointable.direction());
        tipPosition = new LMVectorWrapper(pointable.tipPosition());
        tipVelocity = new LMVectorWrapper(pointable.tipVelocity())
        ;

        if (pointable.isFinger()) {
            type = Type.TYPE_FINGER;
        } else {
            type = Type.TYPE_TOOL;
        }

    }
}
