package computer.clay.looper;

import android.graphics.Point;
import android.util.Log;

public class BehaviorConstruct { // TODO: Consdier renaming to BehaviorScaffold or BehaviorForm

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

    // Touch state
    private boolean isTouched = false;
    // TODO: touchStartTime
    // TODO: touchStopTime
    // TODO: startPoint
    // TODO: currentPoint
    // TODO: stopPoint

    public boolean isTouched () {
        return this.isTouched;
    }

    public void setTouched (boolean isTouched) {
        this.isTouched = isTouched;
    }

    public State state;

    private Perspective perspective = null;

    // TODO: Title
    // TODO: Graphical representation and layout
    // TODO: Associate with a particular Clay by address
    // TODO: Associate with command (action's behavior tree/graph structure)
    // TODO: Associate with cloud object

    public BehaviorConstruct (Perspective perspective, int xPosition, int yPosition) {
        super();

        this.state = State.FREE;

        this.perspective = perspective;

        position.set (xPosition, yPosition);
        radius = DEFAULT_RADIUS;

        // Create and associate a behavior with this placeholder.
        this.behavior = new Behavior(this); // TODO: Remove this! Assign this through the behavior selection interface.
        this.behavior.setTitle(String.valueOf(Behavior.BEHAVIOR_COUNT));
        Behavior.BEHAVIOR_COUNT++;

        // Create condition associated with this placeholder
        if (behavior.BEHAVIOR_COUNT % 2 == 0) {
            this.condition = new BehaviorCondition(this, BehaviorCondition.Type.SWITCH);
        } else {
            this.condition = new BehaviorCondition(this, BehaviorCondition.Type.NONE);
        }
    }

    /**
     * Adds this behavior placeholder to the specified loop sequence and updates the state
     * accordingly.
     *
     * @param loop
     */
    public void attach (Loop loop) {

        // Add this placeholder to the loop.
        this.loop = loop;
        this.loop.addBehavior(this);

        // Update state of this placeholder
        this.state = State.SEQUENCED;
    }

    /**
     * Removes this behavior placeholder from the loop it's associated with, if any, and update
     * the state accordingly.
     */
    public void detach () {
        if (this.loop != null) {
            // Remove this placeholder from the loop.
            this.loop.removeBehavior(this);
            this.loop = null;

            // Update state of the this placeholder
            this.state = State.FREE;
        }
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
        LoopConstruct nearestLoopConstruct = this.perspective.getNearestLoopConstruct (this);
//        double nearestLoopConstructDistance = this.getDistanceToLoop (nearestLoopConstruct);
//        double nearestLoopDistance = Double.MAX_VALUE;
//        for (Loop loop : this.perspective.getLoops ()) {
//            if (this.getDistanceToLoop (loop) < nearestLoopDistance) {
//                nearestLoop = loop; // Update the nearest loop.
//                nearestLoopDistance = this.getDistanceToLoop (loop); // Update the nearest loop distance.
//            }
//        }

        double behaviorConstructAngle = nearestLoopConstruct.getAngle (this.getPosition ());

        // TODO: Get the perspective at the behavior's angle

        LoopPerspective nearestLoopConstructPerspective = nearestLoopConstruct.getPerspective (behaviorConstructAngle);
        double nearestLoopConstructPerspectiveDistance = this.getDistanceToLoopPerspective (nearestLoopConstructPerspective);

        // Snap to the loop if within snapping range
        if (nearestLoopConstruct != null) {
            if (nearestLoopConstructPerspectiveDistance < 250) { // TODO: Replace magic number with a static class variable.

                Point nearestPoint = this.getNearestPoint (nearestLoopConstructPerspective);
                setPosition (nearestPoint.x, nearestPoint.y);

                attach (nearestLoopConstruct.getLoop ()); // TODO: Move this into Behavior.attach();

            } else { // The behavior was positioned outside the snapping boundary of the loop.

                if (this.hasLoop ()) { // Check if this behavior placeholder is in a loop sequence.
                    detach ();
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

//    public Point getNearestPoint (LoopConstruct loopConstruct) {
//
//        Point nearestPoint = new Point();
//
//        double deltaX = this.position.x - loopConstruct.getPosition().x;
//        double deltaY = this.position.y - loopConstruct.getPosition().y;
//        double angleInDegrees = Math.atan2(deltaY, deltaX);
//
//        int nearestX = (int) ((0) + (loopConstruct.getRadius()) * Math.cos (angleInDegrees));
//        int nearestY = (int) ((0) + (loopConstruct.getRadius()) * Math.sin (angleInDegrees));
//
//        nearestPoint.set (nearestX, nearestY);
//
//        return nearestPoint;
//    }

    public Point getNearestPoint (LoopPerspective loopPerspective) {

        Point nearestPoint = new Point();

        double deltaX = this.position.x - loopPerspective.getLoopConstruct ().getPosition ().x;
        double deltaY = this.position.y - loopPerspective.getLoopConstruct ().getPosition ().y;
        double angleInDegrees = Math.atan2(deltaY, deltaX);

        int nearestX = (int) ((0) + (loopPerspective.getRadius()) * Math.cos (angleInDegrees));
        int nearestY = (int) ((0) + (loopPerspective.getRadius()) * Math.sin (angleInDegrees));

        nearestPoint.set (nearestX, nearestY);

        return nearestPoint;
    }

    public double getDistanceToLoopPerspective (LoopPerspective loopPerspective) {

        Point nearestPoint = this.getNearestPoint (loopPerspective);

        double distance = this.getDistance (nearestPoint.x, nearestPoint.y);

        return distance;
    }

    public double getDistanceToLoop (LoopConstruct loopConstruct) {
        return this.getDistance (this.position.x, this.position.y);
    }
}
