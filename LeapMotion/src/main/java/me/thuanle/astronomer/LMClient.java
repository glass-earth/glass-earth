package me.thuanle.astronomer;

import java.io.IOException;
import java.util.HashMap;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;
import me.thuanle.astronomer.connector.ASTConnector;
import me.thuanle.astronomer.connector.ASTProtocol;
import me.thuanle.astronomer.connector.ASTRequest;
import me.thuanle.astronomer.connector.ASTResponse;
import me.thuanle.astronomer.wrapper.LMActionWrapper;
import me.thuanle.astronomer.wrapper.LMGestureWrapper;
import me.thuanle.astronomer.wrapper.LMSwipeGestureWrapper;

class LMClient {

    public static void main(String[] args) {
        //ASTConnector.getDefaultConnector();

        // Create a sample listener and controller
        SampleListener listener = new SampleListener();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);


        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }
}

class SampleListener extends Listener {

    public static final float GESTURE_MIN_LENGTH = 100f;
    public static final float GESTURE_MIN_VELOCITY = 500f;

    public void onConnect(Controller controller) {
        System.out.println("Connected");
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        if(controller.config().setFloat("Gesture.Swipe.MinLength", GESTURE_MIN_LENGTH) &&
                controller.config().setFloat("Gesture.Swipe.MinVelocity", GESTURE_MIN_VELOCITY)) {
            controller.config().save();
        }
        //controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
//        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
//        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
    }

    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.

    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        final Frame frame = controller.frame();

        if (frame.hands().count() + frame.tools().count() + frame.gestures().count() > 0) {

            //System.out.println("Receive:" + post(frame).toJson());

            Vector dir = null;
            GestureList gestures = frame.gestures();
            for (int i = 0; i < gestures.count(); i++) {
                Gesture gesture = gestures.get(i);
                switch (gesture.type()) {
                    case TYPE_SWIPE:
                        SwipeGesture swipe = new SwipeGesture(gesture);
                        if (swipe.state() == Gesture.State.STATE_STOP) {
                            dir =  detectDirection(swipe);
                            System.out.println(
                                    "  Swipe id: " + swipe.id()
                                    + " direction: " + dir
//                                    + ", state: " + swipe.state()
//                                    + ", start: " + swipe.startPosition()
//                                    + ", position: " + swipe.position()
//                                    + ", direction: " + swipe.direction()
//                                    + ", speed: " + swipe.speed()
                            );

                        }
                        //post(swipe);
                        break;
                }
            }

            if (dir != null) {
                System.out.println("Frame id: " + frame.id()
                        + ", timestamp: " + frame.timestamp()
                        + ", fingers: " + frame.fingers().count()
                        + ", gestures " + frame.gestures().count());
            }
            post(dir);
        }
    }

    private ASTResponse post(Vector dir) {
            LMActionWrapper gestureWrapper = new LMActionWrapper(dir);

            ASTRequest message = new ASTRequest(ASTProtocol.Type.Leap.Gesture.TYPE_SWIPE);
            message.to = ASTConnector.DEFAULT_TO_ROLE;
            message.data = gestureWrapper;

            return ASTConnector.getDefaultConnector().post(message);
    }

    private Vector detectDirection(SwipeGesture swipe) {
        Vector dir = swipe.direction();
        dir.setZ(0);
        dir = dir.normalized();
        float disLeft = Vector.left().distanceTo(dir);
        float disRight = Vector.right().distanceTo(dir);
        float disUp = Vector.up().distanceTo(dir);
        float disDown = Vector.down().distanceTo(dir);
        float min = Math.min(Math.min(disLeft,disRight),Math.min(disUp,disDown));
        if (min == disUp){
            return Vector.up();
        }
        if (min == disDown){
            return Vector.down();
        }
        if (min == disLeft){
            return Vector.left();
        }
        if (min == disRight){
            return Vector.right();
        }
        return Vector.zero();
    }

//    private ASTResponse post(PointableList pointables) {
//        LMPointableListWrapper pointableWrapper = new LMPointableListWrapper(pointables);
//
//        ASTRequest message = new ASTRequest(ASTProtocol.Type.Leap.Pointable.TYPE_POINTABLE);
//        message.to = ASTConnector.DEFAULT_TO_ROLE;
//        message.data = pointableWrapper;
//
//        return ASTConnector.getDefaultConnector().post(message);
//    }

//    private ASTResponse post(Pointable pointable) {
//        if (pointable.isValid()) {
//            LMPointableWrapper pointableWrapper = new LMPointableWrapper(pointable);
//
//            ASTRequest message = new ASTRequest(ASTProtocol.Type.Leap.Pointable.TYPE_POINTABLE);
//            message.to = ASTConnector.DEFAULT_TO_ROLE;
//            message.data = pointableWrapper;
//
//            return ASTConnector.getDefaultConnector().post(message);
//        } else {
//            return ASTResponse.getDummyResponse();
//        }
//    }

    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

//    public ASTResponse post(SwipeGesture gesture) {
//        if (gesture.isValid()) {
//            LMSwipeGestureWrapper gestureWrapper = new LMSwipeGestureWrapper(gesture);
//
//            ASTRequest message = new ASTRequest(ASTProtocol.Type.Leap.Gesture.TYPE_SWIPE);
//            message.to = ASTConnector.DEFAULT_TO_ROLE;
//            message.data = gestureWrapper;
//
//            return ASTConnector.getDefaultConnector().post(message);
//        } else {
//            return ASTResponse.getDummyResponse();
//        }
//    }

//    public ASTResponse post(ScreenTapGesture gesture) {
//        if (gesture.isValid()) {
//            LMScreenTapGestureWrapper gestureWrapper = new LMScreenTapGestureWrapper(gesture);
//
//            ASTRequest message = new ASTRequest(ASTProtocol.Type.Leap.Gesture.TYPE_SCREEN_TAP);
//            message.to = ASTConnector.DEFAULT_TO_ROLE;
//            message.data = gestureWrapper;
//
//            return ASTConnector.getDefaultConnector().post(message);
//        } else {
//            return ASTResponse.getDummyResponse();
//        }
//    }

//    public ASTResponse post(KeyTapGesture gesture) {
//        if (gesture.isValid()) {
//            LMKeyTapGestureWrapper gestureWrapper = new LMKeyTapGestureWrapper(gesture);
//
//            ASTRequest message = new ASTRequest(ASTProtocol.Type.Leap.Gesture.TYPE_KEY_TAP);
//            message.to = ASTConnector.DEFAULT_TO_ROLE;
//            message.data = gestureWrapper;
//
//            return ASTConnector.getDefaultConnector().post(message);
//        } else {
//            return ASTResponse.getDummyResponse();
//        }
//    }
}
