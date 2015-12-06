package computer.clay.looper;

import android.graphics.Point;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Perspective {

    private System system = null;

    public static float DEFAULT_SCALE_FACTOR = 1.0f;

    private Point position = new Point ();
    private float scaleFactor = DEFAULT_SCALE_FACTOR;

    private ArrayList<LoopConstruct> loopConstructs = new ArrayList<LoopConstruct> ();
    private ArrayList<BehaviorConstruct> behaviorConstructs = new ArrayList<BehaviorConstruct> ();

//    public Point startAnglePoint = null;
//    public Point spanPoint = null;
//    public int startAngle = 0;
//    public int span = 0;

    public Perspective (System system) {
        super();

        this.system = system;

        this.position.set (0, 0);
        this.scaleFactor = DEFAULT_SCALE_FACTOR;
    }

    public void setPosition (int x, int y) {
        position.set (x, y);
    }

    public Point getPosition () {
        return this.position;
    }

    public void moveBy (int xOffset, int yOffset) {
        position.offset(xOffset, yOffset);
    }

    public void setScaleFactor (float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public float getScaleFactor () {
        return this.scaleFactor;
    }

    /**
     * Checks if the perspective has a construct corresponding to the specified loop.
     *
     * @param loop
     * @return
     */
    public boolean hasConstruct (Loop loop) {
        for (LoopConstruct loopConstruct : this.loopConstructs) {
            if (loopConstruct.getLoop () == loop) {
                return true;
            }

        }
        return false;
    }

    public void addConstruct (Loop loop) {
        LoopConstruct loopConstruct = new LoopConstruct (loop);
        this.loopConstructs.add (loopConstruct);
    }

    public ArrayList<LoopConstruct> getLoopConstructs () {
        return this.loopConstructs;
    }

    public void addBehaviorConstruct (BehaviorConstruct behaviorConstruct) {
        this.behaviorConstructs.add (behaviorConstruct);
    }

    public ArrayList<BehaviorConstruct> getBehaviorConstructs () {
        return this.behaviorConstructs;
    }

    // TODO: createConstruct to hide the creation of constructs.

    // TODO: hasConstruct (Behavior behavior)

    // TODO: hasConstruct (Loop loop, Behavior behavior)
    // TODO: (...) Loop.hasConstruct (Behavior behavior)

    public LoopConstruct getConstruct (Loop loop) {
        for (LoopConstruct loopConstruct : this.loopConstructs) {
            if (loopConstruct.getLoop () == loop) {
                return loopConstruct;
            }

        }
        return null;
    }

    // TODO: getConstruct (Behavior behavior)

    // TODO: getConstruct (Loop loop, Behavior behavior)
    // TODO: (...) Loop.getConstruct (Behavior behavior)

    public LoopConstruct getNearestLoopConstruct (BehaviorConstruct behaviorConstruct) {
        LoopConstruct nearestLoop = null;
        double nearestLoopDistance = Double.POSITIVE_INFINITY;
        for (Loop loop : this.system.getLoops ()) {
            LoopConstruct loopConstruct = this.getConstruct (loop);
            if (behaviorConstruct.getDistanceToLoop (loopConstruct) < nearestLoopDistance) {
                nearestLoop = loopConstruct;
            }
        }
        return nearestLoop;
    }
}
