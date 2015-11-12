package computer.clay.looper;

import android.graphics.Point;

import java.util.ArrayList;

public class Loop {

    public static int DEFAULT_RADIUS = 300;
    public static int DEFAULT_START_ANGLE = 15; // i.e., -75
    public static int DEFAULT_ANGLE_SPAN = 330;

    private Substrate substrate = null;

    private ArrayList<BehaviorPlaceholder> behaviors = new ArrayList<BehaviorPlaceholder> ();

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

    public void addBehavior (BehaviorPlaceholder behaviorPlaceholder) {
        this.behaviors.add (behaviorPlaceholder);
    }

    public ArrayList<BehaviorPlaceholder> getBehaviors() {
        return this.behaviors;
    }

    public BehaviorPlaceholder getAction (int index) {
        if (0 < index && index < this.behaviors.size()) {
            return this.behaviors.get(index);
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

    /**
     * Calculates the distance between the center of the loop and the specified point.
     * @param x
     * @param y
     * @return
     */
    public double getDistance (int x, int y) {
        double distanceSquare = Math.pow (x - this.position.x, 2) + Math.pow (y - this.position.y, 2);
        double distance = Math.sqrt(distanceSquare);
        return distance;
    }

    /**
     * Get the angle at which the specified point falls with respect to the center of the loop.
     */
    public double getAngle (int x, int y) {
        Point startAngle = this.getPoint (this.startAngle);
        Point stopAngle = new Point (x, y);
        double angle = this.getAngle(startAngle, stopAngle);
        return angle;
    }

    /**
     * Get the angle at which the specified point falls with respect to the center of the loop.
     */
    public double getAngle (Point point) {
        Point startAngle = this.getPoint (this.startAngle);
        double angle = this.getAngle (startAngle, point);
        return angle;
    }

    /**
     * Calculates and returns the angle (in degrees) between the specified points and the center
     * point of the loop.
     *
     * @param startingPoint
     * @param endingPoint
     * @return
     */
    public double getAngle (Point startingPoint, Point endingPoint) {
        Point p1 = this.getPosition (); // The center point is p1.

        double a = startingPoint.x - p1.x;
        double b = startingPoint.y - p1.y;
        double c = endingPoint.x - p1.x;
        double d = endingPoint.y - p1.y;

        double atanA = Math.atan2 (a, b);
        double atanB = Math.atan2(c, d);

        double result = Math.toDegrees(atanA - atanB);

        return result;
    }

    /**
     * Calculates the point on the circumference of the circle at the specified angle (in degrees).
     */
    public Point getPoint (double angle) {
        Point point = new Point ();
        double angleInRadians = Math.toRadians(this.startAngle + angle); // ((90.0 - angle) + angle);
        double x = this.getPosition ().x + this.getRadius () * Math.cos (angleInRadians);
        double y = this.getPosition ().y + this.getRadius () * Math.sin (angleInRadians);
        point.set ((int) x, (int) y);
        return point;
    }

    /**
     * Calculates the point on the circumference of the circle at the specified angle (in degrees).
     */
    public Point getPoint (double angle, double radius) {
        Point point = new Point ();
        double angleInRadians = Math.toRadians(this.startAngle + angle); // ((90.0 - angle) + angle);
        double x = this.getPosition ().x + radius * Math.cos (angleInRadians);
        double y = this.getPosition ().y + radius * Math.sin (angleInRadians);
        point.set ((int) x, (int) y);
        return point;
    }
}
