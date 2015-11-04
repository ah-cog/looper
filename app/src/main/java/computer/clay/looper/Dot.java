package computer.clay.looper;

import android.graphics.Point;
import android.util.Log;

public class Dot {

    public static int DEFAULT_RADIUS = 60;

    private Point position = new Point ();
    private int radius;

    public enum State {
        FREE, // The action is not on a loop.
        MOVING, // The action is being moved by touch.
        COUPLED, // The action is near enough to a loop to snap onto it.
        SEQUENCED // The action is in a sequence (i.e., on a loop).
    }

    public State state;

    private Substrate substrate = null;

    // TODO: Title
    // TODO: Graphical representation and layout
    // TODO: Associate with a particular Clay by address
    // TODO: Associate with command (action's behavior tree/graph structure)
    // TODO: Associate with cloud object

    public Dot(Substrate substrate, int xPosition, int yPosition) {
        super();

        this.state = State.FREE;

        this.substrate = substrate;

        position.set (xPosition, yPosition);
        radius = DEFAULT_RADIUS;
    }

    public void setPosition (int x, int y) {
        position.set (x, y);

        // TODO: Update the state based on the position (i.e., settleState).
    }

    /**
     * "Settling" the position means computing the position based on the state of the action.
     */
    public Point settlePosition () { // TODO: Implement continuous flow-based call: "public Point settlePosition (Point currentActionPosition) {"
        Log.v("Clay", "settlePosition");

        Point resolvedPoint = new Point ();

        // TODO: If the action is entangled with the nearest loop, then snap it onto the nearest position on that loop.

        /* Check if the action is entangled. */

        // Search for the nearest loop and snap to that one (ongoing).
        Loop nearestLoop = null;
        double nearestLoopDistance = Double.MAX_VALUE;
        for (Loop loop : this.substrate.getLoops ()) {
            if (this.getDistanceToLoop (loop) < nearestLoopDistance) {
                nearestLoop = loop; // Update the nearest loop.
                nearestLoopDistance = this.getDistanceToLoop (loop); // Update the nearest loop distance.
            }
        }

        // Snap to the loop if within snapping range
        if (nearestLoop != null) {
            if (nearestLoopDistance < 250) {
                Point nearestPoint = this.getNearestPoint(nearestLoop);
                this.setPosition(nearestPoint.x, nearestPoint.y);
            }
        }

        return resolvedPoint;
    }

//    public void snapToLoop (Loop loop) {
//        this.getNearestPoint (loop);
//    }

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

    public double getDistanceToLoop (Loop loop) {
        Point nearestPoint = this.getNearestPoint(loop);
        return this.getDistance (nearestPoint.x, nearestPoint.y);
    }
}
