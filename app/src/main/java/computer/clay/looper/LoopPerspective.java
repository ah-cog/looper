package computer.clay.looper;

import android.graphics.Point;

public class LoopPerspective {

    public static int DEFAULT_RADIUS = 350;
    public static int DEFAULT_RADIUS_EXTENSION = 100;
    public static int DEFAULT_START_ANGLE = 15; // i.e., -75
    public static int DEFAULT_ANGLE_SPAN = 330;

    private int radius = DEFAULT_RADIUS;

    public int getRadius () {
        return this.radius + (DEFAULT_RADIUS_EXTENSION - (int) ((this.getSpan () / 360.0) * DEFAULT_RADIUS_EXTENSION));
    }

    public Point loopCutPoint = null;
    public Point loopCutSpanPoint = null;
    public int loopCutStartAngle = 0;
    public int loopCutSpan = 0;

    private Perspective perspective = null;

    private LoopConstruct loopConstruct = null;

    private BehaviorConstruct firstBehaviorConstruct = null; // The first behavior construct displayed in the perspective's span

//    double angle = 0.0; // The starting angle in degrees for this perspective.
//    double span = 0.0; // The arc length in degrees of this perspective.

    LoopPerspective (LoopConstruct loopConstruct) { // TODO: LoopPerspective (Perspective perspective, Loop loop) {
        // TODO: this.perspective = perspective;
        this.loopConstruct = loopConstruct;
    }

    /**
     * Returns the perspective associated with this loop perspective.
     */
    Perspective getPerspective () {
        return this.perspective;
    }

    /**
     * Returns the loop for which this perspective applies.
     */
    LoopConstruct getLoopConstruct () {
        return this.loopConstruct;
    }

//    void set (double angle, double span) {
//        this.angle = angle;
//        this.span = span;
//    }

    public int getStartAngle () {
        return this.loopCutStartAngle;
    }

    public void setStartAngle (int angle) {
        this.loopCutStartAngle = angle;
    }

    public int getSpan () {
        return this.loopCutSpan;
    }

    public void setSpan (int span) {
        this.loopCutSpan = loopCutSpan;
    }

    public int getStopAngle () {
        return this.loopCutStartAngle + this.loopCutSpan;
    }

//    void setAngle (double angle) {
//        this.angle = angle % 360;
//    }
//
//    double getAngle () {
//        return this.angle;
//    }

//    void setSpan (double span) {
//        this.span = span % 360;
//    }
//
//    double getSpan () {
//        return this.span;
//    }
}
