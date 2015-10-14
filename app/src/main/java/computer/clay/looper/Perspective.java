package computer.clay.looper;

import android.graphics.Point;

public class Perspective {

    private Point position = new Point();

    private Substrate substrate = null;

    public Perspective (Substrate substrate) {
        super();

        this.substrate = substrate;

        this.position.set (0, 0);
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
}
