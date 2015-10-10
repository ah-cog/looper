package computer.clay.looper;

import android.graphics.Point;

import java.util.ArrayList;

public class Loop {

    public static int DEFAULT_RADIUS = 300;
    public static int DEFAULT_START_ANGLE = 15; // i.e., -75
    public static int DEFAULT_ANGLE_SPAN = 330;

    private Substrate substrate = null;

    private ArrayList<Action> actions = new ArrayList<Action> ();

    private Point position = new Point ();

    private int radius = DEFAULT_RADIUS;

    private int startAngle = DEFAULT_START_ANGLE;
    private int angleSpan = DEFAULT_ANGLE_SPAN;

    public Loop (Substrate substrate) {
        super();

        this.substrate = substrate;
    }

    public Substrate getSubstrate () {
        return this.substrate;
    }

    public void addAction (Action action) {
        this.actions.add(action);
    }

    public ArrayList<Action> getActions () {
        return this.actions;
    }

    public Action getAction (int index) {
        if (0 < index && index < this.actions.size()) {
            return this.actions.get(index);
        } else {
            return null;
        }
    }

    public Point getPosition () {
        return this.position;
    }

    public int getRadius () {
        return this.radius;
    }

    public int getStartAngle () {
        return this.startAngle;
    }

    public int getAngleSpan () {
        return this.angleSpan;
    }

    // TODO: getActionNearestToPoint (int x, int y)

    // TODO: getActionNearestToAngle (int angle)
}
