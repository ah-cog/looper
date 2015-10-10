package computer.clay.looper;

import android.content.Context;
import android.graphics.Point;

public class Perspective {

    private Point position;

    private Substrate substrate = null;

    public Perspective (Context context) {
        super();

        this.substrate = substrate;
    }

    public void setPosition (int x, int y) {
        position.set(x, y);
    }

    public void moveBy (int xOffset, int yOffset) {
        position.offset (xOffset, yOffset);
    }
}
