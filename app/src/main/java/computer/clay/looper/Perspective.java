package computer.clay.looper;

import android.graphics.Point;

public class Perspective {

    public static float DEFAULT_SCALE_FACTOR = 1.0f;

    private Point position = new Point ();
    private float scaleFactor = DEFAULT_SCALE_FACTOR;

    private Substrate substrate = null;

    public Point loopCutPoint = null;
    public Point loopCutSpanPoint = null;
    public int loopCutStartAngle = 0;
    public int loopCutSpan = 0;

    public Perspective (Substrate substrate) {
        super();

        this.substrate = substrate;

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
        position.offset (xOffset, yOffset);
    }

    public void setScaleFactor (float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public float getScaleFactor () {
        return this.scaleFactor;
    }
}
