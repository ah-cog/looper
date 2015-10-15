package computer.clay.looper;

import android.graphics.Point;

public class Action {

    public static int DEFAULT_RADIUS = 60;

    private Point position = new Point ();
    private int radius;

    Substrate substrate = null;

    // TODO: Title
    // TODO: Graphical representation and layout
    // TODO: Associate with a particular Clay by address.
    // TODO: Associate with command
    // TODO: Associate with cloud object

    public Action (Substrate substrate, int xPosition, int yPosition) {
        super();

        this.substrate = substrate;

        position.set (xPosition, yPosition);
        radius = DEFAULT_RADIUS;
    }

    public void setPosition (int x, int y) {
        position.set(x, y);
    }

    public void moveBy (int xOffset, int yOffset) {
        position.offset(xOffset, yOffset);
    }

    public boolean touches (int x, int y) {

        double distanceSquare = Math.pow (x - this.position.x, 2) + Math.pow (y - this.position.y, 2);
        double distance = Math.sqrt(distanceSquare);

        // Check if the screen was touched within the action's radius.
        if (distance <= this.radius) {
            return true;
        } else {
            return false;
        }
    }

    public Point getPosition () {
        return this.position;
    }

    public int getRadius () {
        return this.radius;
    }

    public double getDistance (int x, int y) {
        double distanceSquare = Math.pow (x - this.position.x, 2) + Math.pow (y - this.position.y, 2);
        double distance = Math.sqrt(distanceSquare);
        return distance;
    }

    public Point getNearestPoint (Loop loop) {

        Point nearestPoint = new Point();

        double deltaX = this.position.x - loop.getPosition().x;
        double deltaY = this.position.y - loop.getPosition().y;
        double angleInDegrees = Math.atan2(deltaY, deltaX);

        int nearestX = (int) ((0) + (loop.getRadius()) * Math.cos (angleInDegrees));
        int nearestY = (int) ((0) + (loop.getRadius()) * Math.sin (angleInDegrees));

        nearestPoint.set (nearestX, nearestY);

        return nearestPoint;
    }
}
