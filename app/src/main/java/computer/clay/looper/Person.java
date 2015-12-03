package computer.clay.looper;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

public class Person {

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
    private boolean[] isDragging = new boolean[MAXIMUM_TOUCH_COUNT];
    private double[] dragDistance = new double[MAXIMUM_TOUCH_COUNT];

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

    private boolean[] isTouchingBehavior = new boolean[MAXIMUM_TOUCH_COUNT];

    private boolean isPerformingLoopGesture = false;
    private Loop selectedLoop = null; // TODO: Implement this for each finger.

    private boolean isPerformingLoopPerspectiveGesture = false;
    private LoopPerspective selectedLoopPerspective = null; // TODO: Implement this for each finger.
    private boolean selectedLoopPerspectiveStartBoundary = false;

    private boolean isPerformingConditionGesture = false;

    private boolean isPerformingSubstrateGesture = false;

    double previousDistanceToSelectedLoopCenter = 0.0;
    double distanceToSelectedLoopCenter = 0.0;

    BehaviorConstruct touchedBehaviorConstruct = null;

    boolean isPerformingBehaviorGesture = false; // True if touching _any_ action.
    ArrayList<BehaviorConstruct> touchedBehaviors = new ArrayList<BehaviorConstruct> (); // List of the behaviors that are currently being touched.

    BehaviorCondition touchedCondition = null;

    boolean isPerformingPerspectiveGesture = false;
    boolean isMovingPerspective = false; // True if not touching an action, but dragging (not just touching) the canvas.
    double maxDragDistance = 0;
    boolean isCreatingLoopPerspective = false;

    private System system = null;
    private Perspective perspective = null;

    Person (System system, Perspective perspective) {

        this.system = system;
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
//            isTouchingBehavior[pointerId] = false;
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
        // isPerformingBehaviorGesture
        // touchedBehaviors
        // TODO: touchedBehaviorConstruct[]

        // Check if this is the start of a touch gesture (i.e., the first touch in a sequence of touch events for the given finger)

        if (this.isTouch[finger] == true && this.isTouchPrevious[finger] == false) { // touch...

            Log.v ("Clay", "touch");

            // TODO: Determine what was touched: behavior? condition? system?

            this.xTouchStart[finger] = this.xTouch[finger];
            this.yTouchStart[finger] = this.yTouch[finger];

            this.isTouchingBehavior[finger] = false;
            this.isDragging[finger] = false;
            this.dragDistance[finger] = 0;

            // Check if touching _any_ behaviors (or loops, or canvas, or perspective). If so, keep the canvas locked, and find the action that's being touched.
            for (BehaviorConstruct behaviorConstruct : this.system.getBehaviors()) {
                double distanceToTouch = behaviorConstruct.getDistance ((int) xTouch[finger], (int) yTouch[finger]);
                if (distanceToTouch < behaviorConstruct.getRadius () + 20) {

                    // A behavior gesture is being performed.
                    if (isPerformingBehaviorGesture != true) {
                        Log.v("Clay", "starting behavior gesture");
                        isPerformingBehaviorGesture = true;
                    }

                    // Update the state of the touched behavior.
                    isTouchingBehavior[finger] = true; // TODO: Set state of finger
                    behaviorConstruct.state = BehaviorConstruct.State.MOVING;

                    // Add the touched behavior to the list of touched behaviors.
                    if (!this.touchedBehaviors.contains(behaviorConstruct)) {
                        touchedBehaviors.add (behaviorConstruct);
                    }
                }
            }

//            // Check if touching an action and set isTouchingBehavior accordingly.
//            for (BehaviorConstruct behaviorPlaceholder : this.system.getBehaviors()) {
//                double distanceToAction = behaviorPlaceholder.getDistance ((int) xTouch[finger], (int) yTouch[finger]);
//                if (distanceToAction < behaviorPlaceholder.getRadius ()) {
//                    behaviorPlaceholder.state = BehaviorConstruct.State.MOVING;
//                    isTouchingBehavior[finger] = true; // TODO: Set state of finger
////                            behaviorPlaceholder.state = BehaviorConstruct.State.MOVING; // Set state of touched behaviorPlaceholder
//                    break;
//                }
//            }

            if (!isPerformingBehaviorGesture) {
                // Check if touching in a loop.
                for (Loop loop : this.system.getLoops()) {

                    LoopConstruct loopConstruct = this.perspective.getConstruct (loop);

//                    Point touchPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
//                    LoopPerspective loopPerspective = loopConstruct.getPerspective (touchPoint);

                    double distanceToTouch = loopConstruct.getDistance((int) xTouch[finger], (int) yTouch[finger]);
                    Log.v("Clay", "distanceToTouch = " + distanceToTouch);
                    if (distanceToTouch < 0.50 * loopConstruct.getRadius()) {

                        Log.v("Clay", "starting loop gesture");
                        isPerformingLoopGesture = true;
                        selectedLoop = loop;

//                            isTouchingBehavior[pointerId] = true; // TODO: Set state of finger

//                            if (!this.touchedLoops.contains(loop)) {
//                                touchedLoops.add(loop);
//                            }

//                        isPerformingBehaviorGesture = true;  // TODO: Set state of finger
//                        action.state = BehaviorConstruct.State.MOVING; // Set state of touched action
                    }
                }
            }

            // Check if a loop perspective gesture is being performed
            if (!isPerformingBehaviorGesture && !isPerformingLoopGesture) {

//                if (this.perspective.hasCandidatePerspective(selectedLoop)) {
////                    if (this.currentLoopPerspective != null) {
//
//                    LoopPerspective candidateLoopPerspective = this.perspective.getCandidatePerspective(selectedLoop);
//
//                    Point currentTouchPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
//                    // TODO: Calculate the end angle between the three points (loop center, loopCutPoint, and the current touch point)
//                    candidateLoopPerspective.loopCutSpan = (int) selectedLoop.getAngle(candidateLoopPerspective.loopCutPoint, currentTouchPoint);
//                    // double loopCutSpanPselectedLoop.getAngle (currentTouchPoint.x, currentTouchPoint.y);
//                    Log.v ("Clay", "loopCutStartAngle = " + candidateLoopPerspective.loopCutStartAngle);
//                    candidateLoopPerspective.loopCutSpanPoint = selectedLoop.getPoint (candidateLoopPerspective.loopCutSpan); // (loopCutStartAngle + loopCutSpan);
//                    Log.v ("Clay", "angle = " + candidateLoopPerspective.loopCutSpan);
//
//                }

//                if (selectedLoop != null) {

                    // TODO: Determine the nearest perspective on the selected loop for the finger (if any).

                    // Declare the distance around arc that will respond to touch
                    float conditionTouchProximity = 100;

                    // Get the nearest loop to the touch
                    // TODO: Move this into a new function Perspective.getNearestLoopConstruct().
                    LoopConstruct nearestLoopConstruct = null;
                    float nearestLoopDistance = Float.MAX_VALUE;
                    for (Loop loop : this.system.getLoops()) {

                        LoopConstruct loopConstruct = this.perspective.getConstruct (loop);

                        double distanceToTouch = loopConstruct.getDistance((int) xTouch[finger], (int) yTouch[finger]);
                        if (distanceToTouch < nearestLoopDistance) {
                            nearestLoopDistance = (float) distanceToTouch;
                            nearestLoopConstruct = loopConstruct;
                        }
                    }

                    // Calculate the angle of the touch point relative to the nearest loop.
                    double touchAngle = nearestLoopConstruct.getAngle ((int) xTouch[finger], (int) yTouch[finger]);
                    Log.v("Clay_Loop_Perspective", "touchAngle = " + touchAngle);

                    // Get the start angle and span of the perspective

                LoopPerspective nearestLoopPerspectiveBoundary = null;
                double distanceToNearestLoopPerspectiveBoundary = Double.MAX_VALUE;
                ArrayList<LoopPerspective> nearestLoopPerspectives = nearestLoopConstruct.getPerspectives (nearestLoopConstruct.getLoop ());
//                ArrayList<LoopPerspective> nearestLoopPerspectives = this.perspective.getConstruct (nearestLoop).getPerspectives (nearestLoop);
                for (LoopPerspective loopPerspective : nearestLoopPerspectives) {
                    double startAngle = loopPerspective.loopCutStartAngle;
                    double stopAngle = loopPerspective.loopCutStartAngle + loopPerspective.loopCutSpan;
                    Log.v("Clay_Loop_Perspective", "startAngle = " + startAngle);
                    Log.v("Clay_Loop_Perspective", "stopAngle = " + stopAngle);

                    double distanceToStart = Math.max (touchAngle, startAngle) - Math.min (touchAngle, startAngle);
                    double distanceToStop = Math.max (touchAngle, stopAngle) - Math.min (touchAngle, stopAngle);
                    if (distanceToStart < distanceToNearestLoopPerspectiveBoundary) {
                        distanceToNearestLoopPerspectiveBoundary = distanceToStart;
                        nearestLoopPerspectiveBoundary = loopPerspective;
                        selectedLoopPerspectiveStartBoundary = true;
                        Log.v("Clay_Loop_Perspective", "START boundary is nearest");
                    }

                    if (distanceToStop < distanceToNearestLoopPerspectiveBoundary) {
                        distanceToNearestLoopPerspectiveBoundary = distanceToStop;
                        nearestLoopPerspectiveBoundary = loopPerspective;
                        selectedLoopPerspectiveStartBoundary = false; // "false" flag means the stop boundary is selected, not the start boundary
                        Log.v("Clay_Loop_Perspective", "STOP boundary is nearest");
                    }

//                    if (startAngle < touchAngle && touchAngle < stopAngle) {
//                        Log.v("Clay_Loop_Perspective", "nearestPerspective FOUND");
//                        // Select the loop perspective since.
//                        selectedLoopPerspective = loopPerspective;
//                        break;
//                    }
                }

                int loopPerspectiveBoundaryTouchThreshold = 5;
                if (distanceToNearestLoopPerspectiveBoundary < loopPerspectiveBoundaryTouchThreshold) {
                    Log.v("Clay_Loop_Perspective", "PERFORMING loop perspective gesture");
                    isPerformingLoopPerspectiveGesture = true;
                    selectedLoopPerspective = nearestLoopPerspectiveBoundary;
                }

                    // NOTE: This is useful for placing behaviors (when dragging and dropping them).
//                    LoopPerspective selectedLoopPerspective = null;
//                    ArrayList<LoopPerspective> nearestLoopPerspectives = this.perspective.getPerspectives (nearestLoop);
//                    for (LoopPerspective loopPerspective : nearestLoopPerspectives) {
//                        double startAngle = loopPerspective.loopCutStartAngle;
//                        double stopAngle = loopPerspective.loopCutStartAngle + loopPerspective.loopCutSpan;
//                        Log.v("Clay_Loop_Perspective", "startAngle = " + startAngle);
//                        Log.v("Clay_Loop_Perspective", "stopAngle = " + stopAngle);
//                        if (startAngle < touchAngle && touchAngle < stopAngle) {
//                            Log.v("Clay_Loop_Perspective", "nearestPerspective FOUND");
//                            // Select the loop perspective since.
//                            selectedLoopPerspective = loopPerspective;
//                            break;
//                        }
//                    }

                    // TODO: Calculate positions of endpoints of a perspective, which will be the target points for touch gestures.

                    // TODO: Get the position of a touch.

                    // TODO: Calculate the distance between the endpoints of the perspective and the touch.

                    // TODO: If the distance is small enough, consider the endpoint touched and update the position of the touched endpoint to the position of the touch.

//                }

            }

            // Check if a condition gesture is being performed on a behavior.
            if (!isPerformingBehaviorGesture && !isPerformingLoopGesture && !isPerformingLoopPerspectiveGesture) {
                // TODO: Check if performing a condition gesture.

                // Declare the distance around arc that will respond to touch
                float conditionTouchProximity = 100;

                // Get the nearest loop to the touch
                // TODO: Move this into a new function Perspective.getNearestLoopConstruct().
                LoopConstruct nearestLoopConstruct = null;
                float nearestLoopDistance = Float.MAX_VALUE;
                for (Loop loop : this.system.getLoops()) {

                    LoopConstruct loopConstruct = this.perspective.getConstruct (loop);

                    double distanceToTouch = loopConstruct.getDistance((int) xTouch[finger], (int) yTouch[finger]);
                    if (distanceToTouch < nearestLoopDistance) {
                        nearestLoopDistance = (float) distanceToTouch;
                        nearestLoopConstruct = loopConstruct;
                    }
                }

//                Log.v("Condition", "nearestLoop = " + nearestLoop);
//                Log.v("Condition", "nearestLoopDistance = " + nearestLoopDistance);
//                Log.v("Condition", "conditionTouchProximity = " + conditionTouchProximity);
//                Log.v("Condition", "nearestLoop.getRadius()  = " + nearestLoop.getRadius());
//                Log.v("Condition", "distanceThreshold = " + (nearestLoop.getRadius() + conditionTouchProximity));

//                LoopConstruct loopConstruct = this.perspective.getConstruct (loop);
                Point touchPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
                LoopPerspective nearestLoopPerspective = nearestLoopConstruct.getPerspective (touchPoint);

                if (nearestLoopDistance < (nearestLoopPerspective.getRadius() + conditionTouchProximity)) {
                    double touchAngle = nearestLoopConstruct.getAngle ((int) xTouch[finger], (int) yTouch[finger]); // i.e., the touch angle relative to the nearest loop
//                    Log.v ("Condition", "touchAngle = " + touchAngle);
//                    BehaviorConstruct behaviorBeforeAngle = nearestLoop.getBehaviorBeforeAngle(touchAngle);
//                    Log.v ("Condition", "nearestLoop.getBehaviorBeforeAngle = " + behaviorBeforeAngle);
//                    if (behaviorBeforeAngle != null) {
//                        Log.v ("Condition", "nearestLoop.getBehaviorBeforeAngle = " + behaviorBeforeAngle.getBehavior().getTitle());
//                    }
//
//                    BehaviorConstruct behaviorAfterAngle = nearestLoop.getBehaviorAfterAngle(touchAngle);
//                    Log.v ("Condition", "nearestLoop.behaviorAfterAngle = " + behaviorAfterAngle);
//                    if (behaviorAfterAngle != null) {
//                        Log.v ("Condition", "nearestLoop.behaviorAfterAngle = " + behaviorAfterAngle.getBehavior().getTitle());
//                    }

                    BehaviorCondition behaviorCondition = nearestLoopConstruct.getBehaviorConditionAtAngle(touchAngle);
                    if (behaviorCondition != null) {
                        isPerformingConditionGesture = true;
//                        Log.v ("Condition", "behaviorCondition.behavior = " + behaviorCondition.getBehaviorConstruct().getBehavior().getTitle());
                        Log.v("Condition", "starting condition gesture");
                        this.touchedCondition = behaviorCondition;
                    }

                    // TODO: Loop.getBehaviorBeforeAngle(float angle)
                    // TODO: Loop.getBehaviorAfterAngle(float angle)
                    // TODO: Loop.getConditionAtAngle(float angle)
                }

                // TODO:
                // - Get the nearest loop (if there's one "near enough" to consider)
                //   - Get the angle of the touch relative to the nearest loop got in the previous step (if any)
                //   - Check if the touch angle falls in the sweep range of a condition (i.e., on the space between two behaviors).
                //     - If so, get the condition.
                //     - If so, get the set "isPerformingConditionGesture = true".
                //     - (?) If the touch falls in the sweep range between behaviors, get the behavior associated with the condition.
            }

            // Check if a system gesture is being performed.
            if (!isPerformingBehaviorGesture && !isPerformingLoopGesture && !isPerformingLoopPerspectiveGesture && !isPerformingConditionGesture) {
                // TODO: Check if performing a system/perspective gesture.
                Log.v("Condition", "starting perspective gesture");
                isPerformingPerspectiveGesture = true;
            }

        } else if (this.isTouch[finger] == true && this.isTouchPrevious[finger] == true) { // ...continue touching...

            Log.v ("Clay", "continuing touch");

            // TODO: behaviorPlaceholder.state = BehaviorConstruct.State.MOVING;

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
                if (isPerformingBehaviorGesture) {

                    Log.v("Condition", "continuing behavior gesture (response)");

                } else if (isPerformingConditionGesture) {

                    Log.v("Condition", "continuing condition gesture (response)");

                } else if (isPerformingLoopGesture) {

                    Log.v("Condition", "continuing loop gesture (response)");

                    // Get the distance from the center of the loop "selected" with the gesture.
                    LoopConstruct selectedLoopConstruct = this.perspective.getConstruct (selectedLoop);
                    previousDistanceToSelectedLoopCenter = selectedLoopConstruct.getDistance ((int) xTouchPrevious[finger], (int) yTouchPrevious[finger]);
                    distanceToSelectedLoopCenter = selectedLoopConstruct.getDistance ((int) xTouch[finger], (int) yTouch[finger]);

                    Point touchPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
                    LoopPerspective selectedLoopPerspective = selectedLoopConstruct.getPerspective (touchPoint);

//                            Log.v ("Clay", "distanceToLoopCenter = " + distanceToLoopCenter);
                    if (previousDistanceToSelectedLoopCenter < selectedLoopPerspective.getRadius() && distanceToSelectedLoopCenter > selectedLoopPerspective.getRadius ()) {
                        Log.v("Clay", "Cut the loop.");

                        isCreatingLoopPerspective = true;

                        // TODO: Get the angle and (x,y) coordinate at which the loop was crossed (exited).
                        if (!this.perspective.getConstruct (selectedLoop).hasCandidatePerspective (selectedLoop)) {
//                        if (this.currentLoopPerspective == null) {
                        // TODO: Replace the above conditional with a check for proximity (so can adjust existing loop)?
                            LoopPerspective candidateLoopPerspective = new LoopPerspective(selectedLoopConstruct);
                            candidateLoopPerspective.loopCutPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
                            candidateLoopPerspective.loopCutStartAngle = (int) selectedLoopConstruct.getAngle((int) xTouch[finger], (int) yTouch[finger]);
                            Log.v("Clay", "loopCutStartAngle = " + candidateLoopPerspective.loopCutStartAngle);
                            this.perspective.getConstruct (selectedLoop).setCandidatePerspective(candidateLoopPerspective);
                        }

//                        if (this.perspective.loopCutPoint == null) {
//                            this.perspective.loopCutPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
//                            this.perspective.loopCutStartAngle = (int) selectedLoop.getAngle((int) xTouch[finger], (int) yTouch[finger]);
//                            Log.v ("Clay", "loopCutStartAngle = " + this.perspective.loopCutStartAngle);
//                        }

                        // TODO: Calculate loopCutStartAngle

                    } else if (previousDistanceToSelectedLoopCenter > selectedLoopPerspective.getRadius () && distanceToSelectedLoopCenter < selectedLoopPerspective.getRadius ()) {
                        Log.v ("Clay", "Uncut the loop.");

                        // A finger was dragged from the outside of a loop back to the inside of a
                        // loop (after starting, originally, in inside the loop and going outside
                        // the loop).
                        //
                        // The effect of doing this is "reversing" the gesture, thereby "undoing"
                        // the result of the gesture (i.e., the result of crossing to the outside
                        // of the loop from the inside of the loop in a single drag gesture).

                        if (this.perspective.getConstruct (selectedLoop).hasCandidatePerspective(selectedLoop)) {
                            this.perspective.getConstruct (selectedLoop).removeCandidatePerspective (selectedLoop);
                        }

//                        if (this.perspective.hasPerspectives(selectedLoop)) {
//                            this.perspective.removePerspective(selectedLoop);
//                        }

//                        // Clear the angle and (x,y) coordinate at which the loop was crossed (entered).
//                        if (this.perspective.loopCutPoint != null) {
//                            this.perspective.loopCutPoint = null;
//                            this.perspective.loopCutSpanPoint = null;
//                            this.perspective.loopCutStartAngle = 0;
//                            this.perspective.loopCutSpan = 0;
//                        }

                    }

                    // If started cutting the loop, then calculate the angle offset of the cut in degrees.
                    if (this.perspective.getConstruct (selectedLoop).hasCandidatePerspective(selectedLoop)) {
//                    if (this.currentLoopPerspective != null) {

                        LoopPerspective candidateLoopPerspective = this.perspective.getConstruct (selectedLoop).getCandidatePerspective(selectedLoop);

                        Point currentTouchPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
                        // TODO: Calculate the end angle between the three points (loop center, loopCutPoint, and the current touch point)
                        candidateLoopPerspective.loopCutSpan = (int) selectedLoopConstruct.getAngle(candidateLoopPerspective.loopCutPoint, currentTouchPoint);
                        // double loopCutSpanPselectedLoop.getAngle (currentTouchPoint.x, currentTouchPoint.y);
                        Log.v ("Clay", "loopCutStartAngle = " + candidateLoopPerspective.loopCutStartAngle);
                        candidateLoopPerspective.loopCutSpanPoint = selectedLoopConstruct.getPoint (candidateLoopPerspective.loopCutSpan); // (loopCutStartAngle + loopCutSpan);
                        Log.v ("Clay", "angle = " + candidateLoopPerspective.loopCutSpan);

                    }

//                    // If started cutting the loop, then calculate the angle offset of the cut in degrees.
//                    if (this.perspective.loopCutPoint != null) {
//
//                        Point currentTouchPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
//                        // TODO: Calculate the end angle between the three points (loop center, loopCutPoint, and the current touch point)
//                        this.perspective.loopCutSpan = (int) selectedLoop.getAngle(this.perspective.loopCutPoint, currentTouchPoint);
//                        // double loopCutSpanPselectedLoop.getAngle (currentTouchPoint.x, currentTouchPoint.y);
//                        Log.v ("Clay", "loopCutStartAngle = " + this.perspective.loopCutStartAngle);
//                        this.perspective.loopCutSpanPoint = selectedLoop.getPoint (this.perspective.loopCutSpan); // (loopCutStartAngle + loopCutSpan);
//                        Log.v ("Clay", "angle = " + this.perspective.loopCutSpan);
//                    }

                } else if (isPerformingLoopPerspectiveGesture) {

                    Log.v("Clay_Loop_Perspective", "long continuing loop perspective gesture (response)");

                } else if (isPerformingPerspectiveGesture) {

                    Log.v("Condition", "continuing perspective gesture (response)");

                    // If a loop gesture is not being performed, then it must be the case that the perspective is being moved.
                    if (isTouchingBehavior[finger] == false) {
//                        isPerformingPerspectiveGesture = true;
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

            } else if (isPerformingLoopPerspectiveGesture) {

                Log.v("Clay_Loop_Perspective", "continuing loop perspective gesture (response)");

                if (selectedLoopPerspective != null) {

                    // TODO: Replace the above conditional with a check for proximity (so can adjust existing loop)?
//                    LoopPerspective candidateLoopPerspective = new LoopPerspective(selectedLoop);
//                    candidateLoopPerspective = new LoopPerspective(selectedLoop);
                    if (selectedLoopPerspectiveStartBoundary == true) {
                        selectedLoopPerspective.loopCutPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
                        selectedLoopPerspective.loopCutStartAngle = (int) selectedLoopPerspective.getLoopConstruct ().getAngle ((int) xTouch[finger], (int) yTouch[finger]);
                    } else if (selectedLoopPerspectiveStartBoundary == false) {
//                        selectedLoopPerspective.loopCutPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
//                        selectedLoopPerspective.loopCutStartAngle = (int) selectedLoop.getAngle ((int) xTouch[finger], (int) yTouch[finger]);

                        selectedLoopPerspective.loopCutSpanPoint = new Point ((int) xTouch[finger], (int) yTouch[finger]);
                        int loopCutAngle = (int) selectedLoopPerspective.getLoopConstruct ().getAngle ((int) xTouch[finger], (int) yTouch[finger]);

                        int loopSpanStartAngle = selectedLoopPerspective.getLoopConstruct ().getStartAngle () + selectedLoopPerspective.getLoopConstruct ().getAngleSpan ();

                        selectedLoopPerspective.loopCutSpan = loopCutAngle - selectedLoopPerspective.loopCutStartAngle;

                    }
//                    Log.v("Clay", "loopCutStartAngle = " + selectedLoopPerspective.loopCutStartAngle);
//                    this.perspective.setCandidatePerspective(selectedLoopPerspective);

                }

            } else if (isTouchingBehavior[finger]) {

                // Declare the distance around arc that will respond to touch
                float conditionTouchProximity = 100;

                // Get the nearest loop placeholder to the touch
                // TODO: Move this into a new function Perspective.getNearestLoopConstruct().
                LoopConstruct nearestLoopConstruct = null;
                float nearestLoopDistance = Float.MAX_VALUE;
                for (Loop loop : this.system.getLoops ()) {

                    LoopConstruct loopConstruct = this.perspective.getConstruct (loop);

                    double distanceToTouch = loopConstruct.getDistance((int) xTouch[finger], (int) yTouch[finger]);
                    if (distanceToTouch < nearestLoopDistance) {
                        nearestLoopDistance = (float) distanceToTouch;
                        nearestLoopConstruct = loopConstruct;
                    }
                }

                Log.v("Clay_Loop_Perspective", "NEAREST LOOP = " + nearestLoopConstruct);


                // TODO: Get nearest loop to the touch point (where the behavior is being dragged)

                // TODO: Get nearest loop perspective

                // Calculate the angle of the touch point relative to the nearest loop.
                double touchAngle = nearestLoopConstruct.getAngle ((int) xTouch[finger], (int) yTouch[finger]);

                // NOTE: This is useful for placing behaviors (when dragging and dropping them).
                LoopPerspective nearestLoopPerspective = null;
                Log.v ("Clay_Loop_Perspective", "# PERSPECTIVES FOR NEAREST LOOP = " + nearestLoopConstruct.getPerspectives (nearestLoopConstruct.getLoop ()).size ());
                for (LoopPerspective loopPerspective : nearestLoopConstruct.getPerspectives (nearestLoopConstruct.getLoop ())) {
                    double startAngle = loopPerspective.loopCutStartAngle;
                    double stopAngle = loopPerspective.loopCutStartAngle + loopPerspective.loopCutSpan;
                    Log.v("Clay_Loop_Perspective", "startAngle = " + startAngle);
                    Log.v("Clay_Loop_Perspective", "stopAngle = " + stopAngle);

                    // Check which perspective the behavior is in range of.
                    if (startAngle < touchAngle && touchAngle < stopAngle) {
                        Log.v("Clay_Loop_Perspective", "nearestPerspective FOUND");

                        // Select the loop perspective since.
                        nearestLoopPerspective = loopPerspective;
                        break;
                    }
                }

                // TODO: Get the loop placeholder associated with the nearest perspective

                Log.v("Clay_Loop_Perspective", "NEAREST PERSPECTIVE = " + nearestLoopPerspective);
                LoopConstruct nearestLoopConstructPerspective = nearestLoopPerspective.getLoopConstruct ();
                Log.v("Clay_Loop_Perspective", "NEAREST PERSPECTIVE LOOP = " + nearestLoopConstructPerspective);

//                // TODO: Update the behavior's loop (based on the loop in the placeholder). (TODO: Shift removed behaviors into place in perspectives observing the segment where the removed behavior was.)
//
//                for (BehaviorConstruct behaviorPlaceholder : touchedBehaviors) {
//
//                    // Change the behavior's loop to the one in nearest perspective.
//                    behaviorPlaceholder.detach ();
//                    behaviorPlaceholder.attach (nearestPerspectiveLoop);
//
//                    // TODO: Update position based on inferred position relative to the nearest loop in snapping range.
////                    behaviorPlaceholder.setPosition ((int) xTouch[finger],(int)  yTouch[finger]);
//                }
//
//                // TODO: Get the segment of the loop placeholder exposed by the perspective
//
//                // TODO: Calculate the relative angle of the touched behavior on the loop segment
//
//                // TODO: Calculate the relative point of the touched behavior on the loop segment






                for (BehaviorConstruct behaviorConstruct : touchedBehaviors) {
                    behaviorConstruct.setPosition ((int) xTouch[finger],(int)  yTouch[finger]);
                }
            }

        } else if (this.isTouch[finger] == false && this.isTouchPrevious[finger] == true) { // ...untouch.

            Log.v ("Clay", "untouch");

            // TODO?: behaviorPlaceholder.state = BehaviorConstruct.State.FREE;

            // Move the canvas if this is a drag event!
            if (isPerformingBehaviorGesture) {

                Log.v("Condition", "stopping behavior gesture");

                // TODO: If moving an action, upon release, call "searchForPosition()" to check the "logical state" of the action in the system WRT the other loops, and find it's final position and update its state (e.g., if it's near enough to snap to a loop, to be deleted, etc.).

                Log.v("Clay", "before isTouchingBehavior[pointerId]");
                if (touchedBehaviors.size() > 0) { // if (isTouchingBehavior[pointerId]) {
                    Log.v("Clay", "touchedBehaviors.size() = " + touchedBehaviors.size());

                    // Settle position of action.
                    for (BehaviorConstruct behaviorConstruct : touchedBehaviors) {
                        behaviorConstruct.settlePosition();
                    }

                    // HACK: This hack removes _all_ touched behaviors when _any_ finger is lifted up.
                    touchedBehaviors.clear();
                    // TODO: Remove specific finger from the list of fingers touching down.

                    // HACK: This hack updates the touch flag that indicates if _any_ finger is touching to false.
                    isPerformingBehaviorGesture = false;
                    // TODO: Set state of finger

//                            // Update the gesture state
//                            isPerformingLoopGesture = false;
//                            selectedLoop = null;
                }

            } else if (isPerformingConditionGesture) {

                Log.v ("Condition", "stopping condition gesture");
                Log.v ("Condition", "touchedCondition = " + touchedCondition);

                if (this.touchedCondition != null) {

                    Log.v ("Condition", "touchedCondition.type = " + this.touchedCondition.getType());

                    if (touchedCondition.getType() == BehaviorCondition.Type.NONE) {
                        touchedCondition.setType(BehaviorCondition.Type.SWITCH);
                    } else if (touchedCondition.getType() == BehaviorCondition.Type.SWITCH) {
                        touchedCondition.setType(BehaviorCondition.Type.THRESHOLD);
                    } else if (touchedCondition.getType() == BehaviorCondition.Type.THRESHOLD) {
                        touchedCondition.setType(BehaviorCondition.Type.GESTURE);
                    } else if (touchedCondition.getType() == BehaviorCondition.Type.GESTURE) {
                        touchedCondition.setType(BehaviorCondition.Type.MESSAGE);
                    } else if (touchedCondition.getType() == BehaviorCondition.Type.MESSAGE) {
                        touchedCondition.setType(BehaviorCondition.Type.NONE);
                    }

                    touchedCondition = null;
                }

                // TODO:
                // - Get the condition associated with this gesture
                // - Change the condition to the next condition type in the list
                // - TODO: (in continuing condition gesture) As dragging toward and away from the loop center, cycle through the condition types!

            } else if (isPerformingLoopGesture) {

                Log.v ("Condition", "stopping loop gesture");

                if (isCreatingLoopPerspective) {

                    // Create the loop perspective and associate with the system perspective.
                    LoopPerspective candidateLoopPerspective = this.perspective.getConstruct (selectedLoop).getCandidatePerspective (this.selectedLoop);
                    this.perspective.getConstruct (selectedLoop).addPerspective (candidateLoopPerspective);

                    // Reset the "live action" state in the current perspective to prepare for
                    // future "live action" gestures.
//                    this.currentLoopPerspective = null;
                    this.perspective.getConstruct (selectedLoop).removeCandidatePerspective (this.selectedLoop);
                    candidateLoopPerspective = this.perspective.getConstruct (selectedLoop).getCandidatePerspective(this.selectedLoop);
                    Log.v("Clay", "candidatePerspective = " + candidateLoopPerspective);

//                    this.perspective.loopCutPoint = null;
//                    this.perspective.loopCutSpanPoint = null;
//
//                    this.perspective.loopCutStartAngle = 0;
//                    this.perspective.loopCutSpan = 0;

                    // Update the gesture state
                    isPerformingLoopGesture = false;
                    selectedLoop = null;
                }

            } else if (isPerformingLoopPerspectiveGesture) {

                Log.v ("Clay_Loop_Perspective", "stopping loop perspective gesture");

                // Check if the perspective is small enough to delete.
                if (selectedLoopPerspective != null) {
                    if (selectedLoopPerspective.loopCutSpan < 20) {
                        this.perspective.getConstruct (selectedLoop).removePerspective (selectedLoopPerspective);
                    }
                }

                isPerformingLoopPerspectiveGesture = false;
                selectedLoopPerspective = null;
                selectedLoopPerspectiveStartBoundary = false;

            } if (isPerformingPerspectiveGesture) {

                    Log.v ("Condition", "stopping perspecting gesture");

                    if (isMovingPerspective) {
                        perspective.moveBy ((int) (xTouch[finger] - xTouchStart[finger]), (int) (yTouch[finger] - yTouchStart[finger]));
                    } else {

                        // Add an action to the system.
                        BehaviorConstruct newBehaviorConstruct = new BehaviorConstruct (this.perspective, (int) xTouch[finger], (int) yTouch[finger]);
                        this.system.addBehavior (newBehaviorConstruct);
                        newBehaviorConstruct.settlePosition ();
                    }

//                        if (isTouchingBehavior[pointerId]) {
//                            // Settle the action.
//                            // Check if touching an action and set isTouchingBehavior accordingly.
//                            for (BehaviorConstruct action : this.system.getBehaviors()) {
//                                double distanceToAction = action.getDistance ((int) xTouch[pointerId], (int) yTouch[pointerId]);
//                                if (distanceToAction < action.getRadius ()) {
////                                    action.settlePosition ();
//                                    break;
//                                }
//                            }
//                        } else {
//                            // Create a new action
//                            this.system.addBehavior(new BehaviorConstruct(this.system, (int) xTouch[pointerId], (int) yTouch[pointerId]));
//                        }
            }

            /* Reset touch state for the finger. */

            isTouchingBehavior[finger] = false;

            isDragging[finger] = false;
            dragDistance[finger] = 0;

            // TODO: In processTouchInteractions, compute isMovingPerspective and if it is true, move the perspective.
            isPerformingBehaviorGesture = false;
            isPerformingLoopGesture = false;
            isCreatingLoopPerspective = false;
            isPerformingLoopPerspectiveGesture = false;
            isPerformingConditionGesture = false;
            isPerformingPerspectiveGesture = false;
            isMovingPerspective = false;

        }

    }

//    public void classifyUntouch (int finger) {
//
//
//
//    }

}