package me.thuanle;

import java.io.File;
import java.io.IOException;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.KeyTapGesture;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Pointable;
import com.leapmotion.leap.PointableList;
import com.leapmotion.leap.ScreenTapGesture;
import com.leapmotion.leap.SwipeGesture;
import me.thuanle.connector.connector.ASTConnector;
import me.thuanle.connector.connector.ASTProtocol;
import me.thuanle.connector.connector.ASTRequest;
import me.thuanle.connector.connector.ASTResponse;
import me.thuanle.wrapper.LMKeyTapGestureWrapper;
import me.thuanle.wrapper.LMPointableWrapper;
import me.thuanle.wrapper.LMScreenTapGestureWrapper;
import me.thuanle.wrapper.LMSwipeGestureWrapper;

class LMClient {

    public static void main(String[] args) {
        ASTConnector.getDefaultConnector();

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

    public void onConnect(Controller controller) {
        System.out.println("Connected");
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        //controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
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
            System.out.println("Frame id: " + frame.id()
                    + ", timestamp: " + frame.timestamp()
                    + ", hands: " + frame.hands().count()
                    + ", fingers: " + frame.fingers().count()
                    + ", tools: " + frame.tools().count()
                    + ", gestures " + frame.gestures().count());

            //System.out.println("Receive:" + post(frame).toJson());

            GestureList gestures = frame.gestures();
            for (int i = 0; i < gestures.count(); i++) {
                Gesture gesture = gestures.get(i);
                switch (gesture.type()) {
                    case TYPE_SWIPE:
                        SwipeGesture swipe = new SwipeGesture(gesture);
                        System.out.println("Swipe id: " + swipe.id()
                                + ", " + swipe.state()
                                + ", position: " + swipe.position()
                                + ", direction: " + swipe.direction()
                                + ", speed: " + swipe.speed());
                        post(swipe);
                        break;

                    case TYPE_SCREEN_TAP:
                        ScreenTapGesture screenTap = new ScreenTapGesture(gesture);
                        System.out.println("Screen Tap id: " + screenTap.id()
                                + ", " + screenTap.state()
                                + ", position: " + screenTap.position()
                                + ", direction: " + screenTap.direction());
                        post(screenTap);
                        break;
                    case TYPE_KEY_TAP:
                        KeyTapGesture keyTap = new KeyTapGesture(gesture);
                        System.out.println("Key Tap id: " + keyTap.id()
                                + ", " + keyTap.state()
                                + ", position: " + keyTap.position()
                                + ", direction: " + keyTap.direction());
                        post(keyTap);
                        break;
                    default:
                        System.out.println("Unknown gesture type.");
                        break;
                }
            }

            PointableList pointables = frame.pointables();
            for (int i = 0; i < pointables.count(); i++) {
                Pointable pointable = pointables.get(i);
                post(pointable);
            }
            System.out.println();
        }
    }

    private ASTResponse post(Pointable pointable) {
        if (pointable.isValid()) {
            LMPointableWrapper pointableWrapper = new LMPointableWrapper(pointable);

            ASTRequest message = new ASTRequest(ASTProtocol.Type.Leap.Gesture.TYPE_POINTABLE);
            message.to = ASTConnector.DEFAULT_TO_ROLE;
            message.data = pointableWrapper;

            return ASTConnector.getDefaultConnector().post(message);
        } else {
            return ASTResponse.getDummyResponse();
        }
    }

    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public ASTResponse post(SwipeGesture gesture) {
        if (gesture.isValid()) {
            LMSwipeGestureWrapper gestureWrapper = new LMSwipeGestureWrapper(gesture);

            ASTRequest message = new ASTRequest(ASTProtocol.Type.Leap.Gesture.TYPE_SWIPE);
            message.to = ASTConnector.DEFAULT_TO_ROLE;
            message.data = gestureWrapper;

            return ASTConnector.getDefaultConnector().post(message);
        } else {
            return ASTResponse.getDummyResponse();
        }
    }

    public ASTResponse post(ScreenTapGesture gesture) {
        if (gesture.isValid()) {
            LMScreenTapGestureWrapper gestureWrapper = new LMScreenTapGestureWrapper(gesture);

            ASTRequest message = new ASTRequest(ASTProtocol.Type.Leap.Gesture.TYPE_SCREEN_TAP);
            message.to = ASTConnector.DEFAULT_TO_ROLE;
            message.data = gestureWrapper;

            return ASTConnector.getDefaultConnector().post(message);
        } else {
            return ASTResponse.getDummyResponse();
        }
    }

    public ASTResponse post(KeyTapGesture gesture) {
        if (gesture.isValid()) {
            LMKeyTapGestureWrapper gestureWrapper = new LMKeyTapGestureWrapper(gesture);

            ASTRequest message = new ASTRequest(ASTProtocol.Type.Leap.Gesture.TYPE_KEY_TAP);
            message.to = ASTConnector.DEFAULT_TO_ROLE;
            message.data = gestureWrapper;

            return ASTConnector.getDefaultConnector().post(message);
        } else {
            return ASTResponse.getDummyResponse();
        }
    }
}
