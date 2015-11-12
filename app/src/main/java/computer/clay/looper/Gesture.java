package computer.clay.looper;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

public class Gesture {

    // public enum loopGesture = { };

    /* Touch Interaction Dynamics for Clay */

//    public static int DEFAULT_TOUCH_COUNT = 5;
    public final int MAXIMUM_TOUCH_COUNT = 5;

//    public static int DEFAULT_DRAG_DISTANCE = 15;
    public final int MINIMUM_DRAG_DISTANCE = 15;

    private boolean hasTouches = false; // i.e., a touch is detected
    private int touchCount = 0; // i.e., the number of touch points detected

//    private Point[] touch = new Point[MAXIMUM_TOUCH_COUNT];
    private double[] xTouch = new double[MAXIMUM_TOUCH_COUNT];
    private double[] yTouch = new double[MAXIMUM_TOUCH_COUNT];
    private boolean[] isTouch = new boolean[MAXIMUM_TOUCH_COUNT];
    private boolean[] isTouchingAction = new boolean[MAXIMUM_TOUCH_COUNT];
    private boolean[] isDragging = new boolean[MAXIMUM_TOUCH_COUNT];
    private double[] dragDistance = new double[MAXIMUM_TOUCH_COUNT];

    private boolean isPerformingLoopGesture = false;
    private Loop selectedLoop = null; // TODO: Implement this for each finger.

    private double[] xTouchPrevious = new double[MAXIMUM_TOUCH_COUNT];
    private double[] yTouchPrevious = new double[MAXIMUM_TOUCH_COUNT];
    private boolean[] isTouchPrevious = new boolean[MAXIMUM_TOUCH_COUNT];
    private boolean[] isTouchingActionPrevious = new boolean[MAXIMUM_TOUCH_COUNT];

    // Point where the touch started.
    private double[] xTouchStart = new double[MAXIMUM_TOUCH_COUNT];
    private double[] yTouchStart = new double[MAXIMUM_TOUCH_COUNT];

    // Point where the touch ended.
    private double[] xTouchStop = new double[MAXIMUM_TOUCH_COUNT];
    private double[] yTouchStop = new double[MAXIMUM_TOUCH_COUNT];

    double previousDistanceToSelectedLoopCenter = 0.0;
    double distanceToSelectedLoopCenter = 0.0;

    BehaviorPlaceholder touchedBehaviorPlaceholder = null;

    boolean touchingAction = false; // True if touching _any_ action.
    ArrayList<BehaviorPlaceholder> touchedBehaviors = new ArrayList<BehaviorPlaceholder> (); // List of the behaviors that are currently being touched.

    boolean isPerformingPerspectiveGesture = false;
    boolean isMovingPerspective = false; // True if not touching an action, but dragging (not just touching) the canvas.
    double maxDragDistance = 0;
    boolean isCreatingLoopPerspective = false;

    private Substrate substrate = null;
    private Perspective perspective = null;

    Gesture (Substrate substrate, Perspective perspective) {

        this.substrate = substrate;
        this.perspective = perspective;

    }

//    public void setTouching (int finger, boolean touching) {
//        this.isTouch[finger] = touching;
//    }

    public void touch (int finger, double x, double y) {

        // Set the previous touch point to the current touch point before updating the current one.
        this.updatePreviousTouch (finger);

        // Update the current touch point.
        this.isTouch[finger] = true;
        this.xTouch[finger] = x;
        this.yTouch[finger] = y;

//        // Check if this is the start of a touch gesture (i.e., the first touch in a sequence of touch events for the given finger)
//        if (this.isTouch[finger] == true && this.isTouchPrevious[finger] == false) {
//            this.xTouchStart[finger] = this.xTouch[finger];
//            this.yTouchStart[finger] = this.yTouch[finger];
//
//            isTouchingAction[pointerId] = false;
//
//            isDragging[pointerId] = false;
//            dragDistance[pointerId] = 0;
//        }

        // TODO: Classify ongoing gesture.
        this.classify (finger);
    }

    public boolean isTouching (int finger) {
        return this.isTouch[finger];
    }

    public Point getTouch (int finger) {
        if (this.isTouching(finger)) {
            return new Point((int) this.xTouch[finger], (int) this.yTouch[finger]);
        } else {
            return null;
        }
    }

    public void untouch (int finger, double x, double y) {

//        // Set the previous touch state to the current touch state before updating the current one.
//        this.isTouchPrevious[finger] = this.isTouch[finger];
//
//        // Update the current touch state.
//        this.isTouch[finger] = false;

        // Set the previous touch point to the current touch point before updating the current one.
        this.updatePreviousTouch (finger);

        // Update the current touch point.
        this.isTouch[finger] = false;
        this.xTouch[finger] = x;
        this.yTouch[finger] = y;

        // Check if this is the start of a touch gesture (i.e., the first touch in a sequence of touch events for the given finger)
        if (this.isTouch[finger] == false && this.isTouchPrevious[finger] == true) {
            this.xTouchStop[finger] = this.xTouch[finger];
            this.yTouchStop[finger] = this.yTouch[finger];
        }

        // TODO: Classify completed gesture.
    }

    private void updatePreviousTouch (int finger) {

        // TODO: Store the current value of previousTouch in a local timeline/database.

        this.isTouchPrevious[finger] = this.isTouch[finger];
        this.xTouchPrevious[finger] = this.xTouch[finger];
        this.yTouchPrevious[finger] = this.yTouch[finger];
    }

    public void unsetPreviousTouch (int finger) {
        this.isTouchPrevious[finger] = false;
    }

    public void classify (int finger) {

        // isDragging
        // dragDistance
        // isPerformingPerspectiveGesture
        // isMovingPerspective
        // selectedLoop
        // isPerformingLoopGesture
        // isCreatingLoopPerspective
        // touchingAction
        // touchedBehaviors
        // TODO: touchedBehaviorPlaceholder[]

        // Check if this is the start of a touch gesture (i.e., the first touch in a sequence of touch events for the given finger)

        if (this.isTouch[finger] == true && this.isTouchPrevious[finger] == false) { // touch...

            Log.v ("Clay", "touch");

            this.xTouchStart[finger] = this.xTouch[finger];
            this.yTouchStart[finger] = this.yTouch[finger];

            isTouchingAction[finger] = false;
            isDragging[finger] = false;
            dragDistance[finger] = 0;

            // Check if touching _any_ behaviors (or loops, or canvas, or perspective). If so, keep the canvas locked, and find the action that's being touched.
            for (BehaviorPlaceholder behaviorPlaceholder : this.substrate.getBehaviors()) {
                double distanceToTouch = behaviorPlaceholder.getDistance ((int) xTouch[finger], (int) yTouch[finger]);
                if (distanceToTouch < behaviorPlaceholder.getRadius () + 20) {

//                            isTouchingAction[pointerId] = true; // TODO: Set state of finger

                    if (!this.touchedBehaviors.contains(behaviorPlaceholder)) {
                        touchedBehaviors.add (behaviorPlaceholder);
                    }

                    touchingAction = true;  // TODO: Set state of finger
//                        behaviorPlaceholder.state = BehaviorPlaceholder.State.MOVING; // Set state of touched behaviorPlaceholder
                }
            }

            // Check if touching an action and set isTouchingAction accordingly.
            for (BehaviorPlaceholder behaviorPlaceholder : this.substrate.getBehaviors()) {
                double distanceToAction = behaviorPlaceholder.getDistance ((int) xTouch[finger], (int) yTouch[finger]);
                if (distanceToAction < behaviorPlaceholder.getRadius ()) {
                    isTouchingAction[finger] = true; // TODO: Set state of finger
//                            behaviorPlaceholder.state = BehaviorPlaceholder.State.MOVING; // Set state of touched behaviorPlaceholder
                    break;
                }
            }

            // Check if touching in a loop.
            for (Loop loop: this.substrate.getLoops()) {
                double distanceToTouch = loop.getDistance ((int) xTouch[finger], (int) yTouch[finger]);
                Log.v ("Clay", "distanceToTouch = " + distanceToTouch);
                if (distanceToTouch < 0.50 * loop.getRadius ()) {

                    Log.v ("Clay", "starting loop gesture");
                    isPerformingLoopGesture = true;
                    selectedLoop = loop;

//                            isTouchingAction[pointerId] = true; // TODO: Set state of finger

//                            if (!this.touchedLoops.contains(loop)) {
//                                touchedLoops.add(loop);
//                            }

                    touchingAction = true;  // TODO: Set state of finger
//                        action.state = BehaviorPlaceholder.State.MOVING; // Set state of touched action
                }
            }

        } else if (this.isTouch[finger] == true && this.isTouchPrevious[finger] == true) { // ...continue touching...

            Log.v ("Clay", "continuing touch");

            // Calculate the drag distance
            double dragDistanceSquare = Math.pow(xTouch[finger] - xTouchStart[finger], 2) + Math.pow(yTouch[finger] - yTouchStart[finger], 2);
            dragDistance[finger] = (dragDistanceSquare != 0 ? Math.sqrt(dragDistanceSquare) : 0);

//            Log.v ("Clay", "dragDistance = " + dragDistance[finger]);

            // Check if a drag is occurring (defined by continuously touching the screen while deviating from the initail point of touch by more than 15 pixels)
            if (dragDistance[finger] > this.MINIMUM_DRAG_DISTANCE) {
                // TODO: Get distance between down and current touch point. Set isMovingPerspective to true if the drag distance is greater than a specified threshold.
                isDragging[finger] = true;

                // TODO: Move this into a separate processTouchInteraction() function, and in this event listener, only update the touch interaction state.

                // Check if a loop gesture is being performed.
                if (isPerformingLoopGesture) {

                    // Get the distance from the center of the loop "selected" with the gesture.
                    previousDistanceToSelectedLoopCenter = selectedLoop.getDistance ((int) xTouchPrevious[finger], (int) yTouchPrevious[finger]);
                    distanceToSelectedLoopCenter = selectedLoop.getDistance ((int) xTouch[finger], (int) yTouch[finger]);

//                            Log.v ("Clay", "distanceToLoopCenter = " + distanceToLoopCenter);
                    if (previousDistanceToSelectedLoopCenter < selectedLoop.getRadius() && distanceToSelectedLoopCenter > selectedLoop.getRadius ()) {
                        Log.v("Clay", "Cut the loop.");

                        isCreatingLoopPerspective = true;

                        // TODO: Get the angle and (x,y) coordinate at which the loop was crossed (exited).
                        if (this.perspective.loopCutPoint == null) {
                            this.perspective.loopCutPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
                            this.perspective.loopCutStartAngle = (int) selectedLoop.getAngle((int) xTouch[finger], (int) yTouch[finger]);
                            Log.v ("Clay", "loopCutStartAngle = " + this.perspective.loopCutStartAngle);
                        }

                        // TODO: Calculate loopCutStartAngle

                    } else if (previousDistanceToSelectedLoopCenter > selectedLoop.getRadius () && distanceToSelectedLoopCenter < selectedLoop.getRadius ()) {
                        Log.v ("Clay", "Uncut the loop.");

                        // Clear the angle and (x,y) coordinate at which the loop was crossed (entered).
                        if (this.perspective.loopCutPoint != null) {
                            this.perspective.loopCutPoint = null;
                            this.perspective.loopCutSpanPoint = null;
                            this.perspective.loopCutStartAngle = 0;
                            this.perspective.loopCutSpan = 0;
                        }

                    }

                    // If started cutting the loop, then calculate the angle offset of the cut in degrees.
                    if (this.perspective.loopCutPoint != null) {

                        Point currentTouchPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
                        // TODO: Calculate the end angle between the three points (loop center, loopCutPoint, and the current touch point)
                        this.perspective.loopCutSpan = (int) selectedLoop.getAngle(this.perspective.loopCutPoint, currentTouchPoint);
                        // double loopCutSpanPselectedLoop.getAngle (currentTouchPoint.x, currentTouchPoint.y);
                        Log.v ("Clay", "loopCutStartAngle = " + this.perspective.loopCutStartAngle);
                        this.perspective.loopCutSpanPoint = selectedLoop.getPoint (this.perspective.loopCutSpan); // (loopCutStartAngle + loopCutSpan);
                        Log.v ("Clay", "angle = " + this.perspective.loopCutSpan);
                    }

                } else {

                    // If a loop gesture is not being performed, then it must be the case that the perspective is being moved.
                    if (isTouchingAction[finger] == false) {
                        isPerformingPerspectiveGesture = true;
                        isMovingPerspective = true;
                    }
                }
            }


            // Move the perspective over the canvas if this is a drag event!
            if (isPerformingPerspectiveGesture) {

                if (isMovingPerspective) {
                    perspective.moveBy((int) (xTouch[finger] - xTouchStart[finger]), (int) (yTouch[finger] - yTouchStart[finger]));
                }

            } else if (isPerformingLoopGesture) {

                // TODO: Start constructing a viewing angle model to use to construct a viewing angle.

                // TODO: Look for the point on the loop at which the finger crosses the line (i.e., the distance is greater than the loop's radius).

            } else if (isTouchingAction[finger]) {
                for (BehaviorPlaceholder behaviorPlaceholder : touchedBehaviors) {
                    behaviorPlaceholder.setPosition ((int) xTouch[finger],(int)  yTouch[finger]);
                }
            }

        } else if (this.isTouch[finger] == false && this.isTouchPrevious[finger] == true) { // ...untouch.

            Log.v ("Clay", "untouch");

            // Move the canvas if this is a drag event!
            if (isPerformingPerspectiveGesture) {

                if (isMovingPerspective) {
                    perspective.moveBy ((int) (xTouch[finger] - xTouchStart[finger]), (int) (yTouch[finger] - yTouchStart[finger]));
                }

            } else if (isPerformingLoopGesture) {

                if (isCreatingLoopPerspective) {
                    // TODO: Create the loop perspective and associate with the substrate perspective.

                    this.perspective.loopCutPoint = null;
                    this.perspective.loopCutSpanPoint = null;

                    this.perspective.loopCutStartAngle = 0;
                    this.perspective.loopCutSpan = 0;

                    // Update the gesture state
                    isPerformingLoopGesture = false;
                    selectedLoop = null;
                }

            } else {

                // TODO: If moving an action, upon release, call "searchForPosition()" to check the "logical state" of the action in the substrate WRT the other loops, and find it's final position and update its state (e.g., if it's near enough to snap to a loop, to be deleted, etc.).

                Log.v("Clay", "before isTouchingAction[pointerId]");
                if (touchedBehaviors.size() > 0) { // if (isTouchingAction[pointerId]) {
                    Log.v("Clay", "touchedBehaviors.size() = " + touchedBehaviors.size());

                    // Settle position of action.
                    for (BehaviorPlaceholder behaviorPlaceholder : touchedBehaviors) {
                        behaviorPlaceholder.settlePosition();
                    }

                    // HACK: This hack removes _all_ touched behaviors when _any_ finger is lifted up.
                    touchedBehaviors.clear ();
                    // TODO: Remove specific finger from the list of fingers touching down.

                    // HACK: This hack updates the touch flag that indicates if _any_ finger is touching to false.
                    touchingAction = false;
                    // TODO: Set state of finger

//                            // Update the gesture state
//                            isPerformingLoopGesture = false;
//                            selectedLoop = null;

                } else {

                    // Add an action to the substrate.
                    BehaviorPlaceholder newBehaviorPlaceholder = new BehaviorPlaceholder(this.substrate, (int) xTouch[finger], (int) yTouch[finger]);
                    this.substrate.addBehavior (newBehaviorPlaceholder);
                    newBehaviorPlaceholder.settlePosition ();
                }

//                        if (isTouchingAction[pointerId]) {
//                            // Settle the action.
//                            // Check if touching an action and set isTouchingAction accordingly.
//                            for (BehaviorPlaceholder action : this.substrate.getBehaviors()) {
//                                double distanceToAction = action.getDistance ((int) xTouch[pointerId], (int) yTouch[pointerId]);
//                                if (distanceToAction < action.getRadius ()) {
////                                    action.settlePosition ();
//                                    break;
//                                }
//                            }
//                        } else {
//                            // Create a new action
//                            this.substrate.addBehavior(new BehaviorPlaceholder(this.substrate, (int) xTouch[pointerId], (int) yTouch[pointerId]));
//                        }
            }

            /* Reset touch state for the finger. */

            isTouchingAction[finger] = false;

            isDragging[finger] = false;
            dragDistance[finger] = 0;

            // TODO: In processTouchInteractions, compute isMovingPerspective and if it is true, move the perspective.
            isPerformingPerspectiveGesture = false;
            isMovingPerspective = false;
            isPerformingLoopGesture = false;
            isCreatingLoopPerspective = false;

        }

    }

//    public void classifyUntouch (int finger) {
//
//
//
//    }

}