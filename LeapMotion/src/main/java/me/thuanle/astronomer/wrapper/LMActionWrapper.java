package me.thuanle.astronomer.wrapper;

import com.leapmotion.leap.Vector;
import me.thuanle.astronomer.connector.ASTRequest;

/**
 * Created by thuanle on 4/18/14.
 */
public class LMActionWrapper extends ASTRequest.Data {

    public String direction;

    public LMActionWrapper(Vector v) {
        if (v == Vector.up()) {
            direction = "up";
        } else if (v == Vector.down()) {
            direction = "down";
        } else if (v == Vector.left()) {
            direction = "left";
        } else if (v == Vector.right()) {
            direction = "right";
        }
    }
}
