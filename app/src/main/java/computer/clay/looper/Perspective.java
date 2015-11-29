package computer.clay.looper;

import android.graphics.Point;

import java.util.ArrayList;

public class Perspective {

    private Substrate substrate = null;

    public static float DEFAULT_SCALE_FACTOR = 1.0f;

    private Point position = new Point ();
    private float scaleFactor = DEFAULT_SCALE_FACTOR;

    ArrayList<LoopPerspective> loopPerspectives = new ArrayList<LoopPerspective>();

//    public Point loopCutPoint = null;
//    public Point loopCutSpanPoint = null;
//    public int loopCutStartAngle = 0;
//    public int loopCutSpan = 0;

    public Perspective (Substrate substrate) {
        super();

        this.substrate = substrate;

        this.position.set (0, 0);
        this.scaleFactor = DEFAULT_SCALE_FACTOR;
    }

    /**
     * Checks if there's a perspective for the specified loop. Returns true if so, and returns
     * false if not.
     *
     * @param loop The loop for which to search for a perspective.
     * @return True if there is a perspective on the specified loop and false otherwise.
     */
    public boolean hasPerspective (Loop loop) {
        for (LoopPerspective loopPerspective : this.loopPerspectives) {
            if (loop == loopPerspective.getLoop()) {
                return true;
            }
        }
        return false;
    }

    public void setPosition (int x, int y) {
        position.set (x, y);
    }

    public Point getPosition () {
        return this.position;
    }

    public void moveBy (int xOffset, int yOffset) {
        position.offset (xOffset, yOffset);
    }

    public void setScaleFactor (float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public float getScaleFactor () {
        return this.scaleFactor;
    }

    public void addPerspective(LoopPerspective loopPerspective) {
        this.loopPerspectives.add(loopPerspective);
    }

    public LoopPerspective getPerspective (Loop loop) {
        for (LoopPerspective loopPerspective : this.loopPerspectives) {
            if (loop == loopPerspective.getLoop()) {
                return loopPerspective;
            }
        }
        return null;
    }

    public void removePerspective(Loop loop) {

        for (int i = 0; i < this.loopPerspectives.size(); i++) {
            LoopPerspective loopPerspective = this.loopPerspectives.get(i);
            if (loop == loopPerspective.getLoop()) {
                this.loopPerspectives.remove(i);
            }
        }
    }
}
