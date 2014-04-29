package me.thuanle.astronomer.wrapper;

import java.util.ArrayList;
import java.util.List;

import com.leapmotion.leap.Frame;
import com.leapmotion.leap.GestureList;

/**
 * This class is the wrapper of {@link com.leapmotion.leap.Frame} for using gson
 * Created by thuanle on 4/9/14.
 */
public class LMFrameWrapper {
    private transient final String type = "LMFrameWrapper";
    private final long id;
    private final long timestamp;
    private final int nHand;
    private final int nFigure;
    private final int nTools;
    public List<LMSwipeGestureWrapper> gestures;

    public LMFrameWrapper(Frame frame) {
        id = frame.id();
        timestamp=frame.timestamp();
        nHand=frame.hands().count();
        nFigure = frame.fingers().count();
        nTools = frame.tools().count();
        
        gestures = new ArrayList<LMSwipeGestureWrapper>();
        GestureList fGestures = frame.gestures();
        for (int i = 0 ;i <fGestures.count(); i++){
//            LMSwipeGestureWrapper wrapper = new LMSwipeGestureWrapper(fGestures.get(i));
//            gestures.add(wrapper);
        }
    }


}
