package computer.clay.looper;

import android.graphics.Point;

public class LoopPerspective {

    public Point loopCutPoint = null;
    public Point loopCutSpanPoint = null;
    public int loopCutStartAngle = 0;
    public int loopCutSpan = 0;

    private Perspective perspective = null;

    private Loop loop = null;

    double angle = 0.0; // The starting angle in degrees for this perspective.
//    double span = 0.0; // The arc length in degrees of this perspective.

    LoopPerspective (Loop loop) { // TODO: LoopPerspective (Perspective perspective, Loop loop) {
        // TODO: this.perspective = perspective;
        this.loop = loop;
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
    Loop getLoop () {
        return this.loop;
    }

//    void set (double angle, double span) {
//        this.angle = angle;
//        this.span = span;
//    }

    void setAngle (double angle) {
        this.angle = angle % 360;
    }

    double getAngle () {
        return this.angle;
    }

//    void setSpan (double span) {
//        this.span = span % 360;
//    }
//
//    double getSpan () {
//        return this.span;
//    }
}
