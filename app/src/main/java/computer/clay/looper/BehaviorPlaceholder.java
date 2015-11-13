package computer.clay.looper;

import android.graphics.Point;
import android.util.Log;

public class BehaviorPlaceholder { // TODO: Consdier renaming to BehaviorScaffold or BehaviorForm

    public static int DEFAULT_RADIUS = 80; // 60

    private Behavior behavior = null; // TODO: private ArrayList<Behavior> behaviors = new ArrayList<Behavior>();

    private BehaviorCondition condition = null;

    private Point position = new Point ();
    private int radius;

    private Loop loop = null; // The loop associated with this behavior placeholder, if any.

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

    public BehaviorPlaceholder (Substrate substrate, int xPosition, int yPosition) {
        super();

        this.state = State.FREE;

        this.substrate = substrate;

        position.set (xPosition, yPosition);
        radius = DEFAULT_RADIUS;

        // Create and associate a behavior with this placeholder.
        this.behavior = new Behavior(this); // TODO: Remove this! Assign this through the behavior selection interface.
        this.behavior.setTitle(String.valueOf(Behavior.BEHAVIOR_COUNT));
        Behavior.BEHAVIOR_COUNT++;
    }

    /**
     * Adds this behavior placeholder to the specified loop sequence and updates the state
     * accordingly.
     *
     * @param loop
     */
    public void setLoop (Loop loop) {

        // Add this placeholder to the loop.
        this.loop = loop;
        this.loop.addBehavior(this);

        // Update state of this placeholder
        this.state = State.SEQUENCED;
    }

    /**
     * Returns the loop associated with this behavior placeholder. Returns null if there is no loop
     * associated with this behavior placeholder.
     *
     * @return
     */
    public Loop getLoop () {
        return this.loop;
    }

    public boolean hasLoop () {
        return (this.loop != null);
    }

    /**
     * Removes this behavior placeholder from the loop it's associated with, if any, and update
     * the state accordingly.
     */
    public void unsetLoop () {
        if (this.loop != null) {
            // Remove this placeholder from the loop.
            this.loop.removeBehavior(this);
            this.loop = null;

            // Update state of the this placeholder
            this.state = State.FREE;
        }
    }

    public void setBehavior (Behavior behavior) {
        this.behavior = behavior;
    }

    public Behavior getBehavior () {
        return this.behavior;
    }

    public boolean hasBehavior () {
        return (this.behavior != null);
    }

    public void setCondition (BehaviorCondition condition) {
        this.condition = condition;
    }

    public BehaviorCondition getCondition () {
        return this.condition;
    }

    public boolean hasCondition () {
        return (this.condition != null);
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
            if (nearestLoopDistance < 250) { // TODO: Replace magic number with a static class variable.

                Point nearestPoint = this.getNearestPoint (nearestLoop);
                setPosition (nearestPoint.x, nearestPoint.y);
                setLoop (nearestLoop);

            } else { // The behavior was positioned outside the snapping boundary of the loop.

                if (this.hasLoop ()) { // Check if this behavior placeholder is in a loop sequence.
                    unsetLoop();
                } else {
                    // NOTE: This happens when a free behavior is moved, but not onto a loop (it remains free after being moved).
                    Log.v ("Clay", "UNHANGLED CONDITION MET. HANDLE THIS CONDITION!");
                }
            }
        }

        return resolvedPoint;
    }

//    public void updateState () {
//
//    }

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
