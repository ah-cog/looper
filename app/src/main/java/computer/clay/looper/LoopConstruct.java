package computer.clay.looper;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

// TODO: loopConcept/loopBody/loopSubject/loopFrame, loopPerspective, loopBehavior/loopOperation

public class LoopConstruct { // TODO: Possibly renamed to LoopScaffold, LoopScaffold, LoopStructure, LoopMachine, LoopEngine, LoopUnit, LoopOperator

    public static int DEFAULT_RADIUS = 350;
    public static int DEFAULT_START_ANGLE = 15; // i.e., -75
    public static int DEFAULT_ANGLE_SPAN = 330;

    private Point position = new Point ();

    private int radius = DEFAULT_RADIUS;

    private int startAngle = DEFAULT_START_ANGLE;
    private int angleSpan = DEFAULT_ANGLE_SPAN;

    private Loop loop = null;

    private ArrayList<LoopPerspective> candidateLoopPerspectives = new ArrayList<LoopPerspective>();
    private ArrayList<LoopPerspective> loopPerspectives = new ArrayList<LoopPerspective>();

    public ArrayList<LoopPerspective> getLoopPerspectives() {
        return loopPerspectives;
    }

    // TODO: Point position

    LoopConstruct (Loop loop) {

        this.loop = loop;

        // TODO: Create a default loop and perspective for the placeholder.
//        Loop defaultLoop = new Loop(this.system);
//        LoopPerspective defaultLoopPerspective = new LoopPerspective(defaultLoop);

    }

    public Loop getLoop () {
        return this.loop;
    }

    // TODO: double getRadius (double angle);

    public boolean hasCandidatePerspective (Loop loop) {
        for (LoopPerspective loopPerspective : this.candidateLoopPerspectives) {
            if (loop == loopPerspective.getLoopConstruct ().getLoop ()) {
                return true;
            }
        }
        return false;
    }

    public void setCandidatePerspective(LoopPerspective loopPerspective) {
        if (!this.candidateLoopPerspectives.contains(loopPerspective)) {
            this.candidateLoopPerspectives.add(loopPerspective);
        }
    }

    public LoopPerspective getCandidatePerspective (Loop loop) {
        for (LoopPerspective loopPerspective : this.candidateLoopPerspectives) {
            if (loop == loopPerspective.getLoopConstruct ().getLoop ()) {
                return loopPerspective;
            }
        }
        return null;
    }

    public void removeCandidatePerspective(Loop loop) {

        for (int i = 0; i < this.candidateLoopPerspectives.size(); i++) {
            LoopPerspective loopPerspective = this.candidateLoopPerspectives.get(i);
            if (loop == loopPerspective.getLoopConstruct ().getLoop ()) {
                this.candidateLoopPerspectives.remove(i);
            }
        }
    }

    /**
     * Checks if there's a perspective for the specified loop. Returns true if so, and returns
     * false if not.
     *
     * @param loop The loop for which to search for a perspective.
     * @return True if there is a perspective on the specified loop and false otherwise.
     */
    public boolean hasPerspectives (Loop loop) {
        for (LoopPerspective loopPerspective : this.loopPerspectives) {
            if (loop == loopPerspective.getLoopConstruct ().getLoop ()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPerspective (double angle) {
        // TODO: Check if there is a perspective that starts before and ends after the specified angle

//        LoopPerspective nearestLoopPerspective = null;
//        Log.v ("Clay_Loop_Perspective", "# PERSPECTIVES FOR NEAREST LOOP = " + this.perspective.getConstruct (nearestLoop).getPerspectives (nearestLoop).size ());
        for (LoopPerspective loopPerspective : this.loopPerspectives) {
            double startAngle = loopPerspective.loopCutStartAngle;
            double stopAngle = loopPerspective.loopCutStartAngle + loopPerspective.loopCutSpan;
            Log.v("Clay_Loop_Perspective", "startAngle = " + startAngle);
            Log.v("Clay_Loop_Perspective", "stopAngle = " + stopAngle);

            // Check which perspective the behavior is in range of.
            if (startAngle < angle && angle < stopAngle) {
                Log.v("Clay_Loop_Perspective", "nearestPerspective FOUND");

                // Select the loop perspective since.
                return true;
//                nearestLoopPerspective = loopPerspective;
//                break;
            }
        }
        return false;
    }

    public LoopPerspective getPerspective (double angle) {
        // TODO: Check if there is a perspective that starts before and ends after the specified angle

//        LoopPerspective nearestLoopPerspective = null;
//        Log.v ("Clay_Loop_Perspective", "# PERSPECTIVES FOR NEAREST LOOP = " + this.perspective.getConstruct (nearestLoop).getPerspectives (nearestLoop).size ());
        for (LoopPerspective loopPerspective : this.loopPerspectives) {
            double startAngle = loopPerspective.loopCutStartAngle;
            double stopAngle = loopPerspective.loopCutStartAngle + loopPerspective.loopCutSpan;
            Log.v("Clay_Loop_Perspective", "startAngle = " + startAngle);
            Log.v("Clay_Loop_Perspective", "stopAngle = " + stopAngle);

            // Check which perspective the behavior is in range of.
            if (startAngle < angle && angle < stopAngle) {
                Log.v("Clay_Loop_Perspective", "nearestPerspective FOUND");

                // Select the loop perspective since.
                return loopPerspective;
//                nearestLoopPerspective = loopPerspective;
//                break;
            }
        }
        return null;
    }

    LoopPerspective getPerspective (Point point) {
        double angle = this.getAngle (point);
        return this.getPerspective (angle);
    }

    // TODO: Replace with createPerspective
    public void addPerspective (LoopPerspective loopPerspective) {
        // TODO: Create new perspectives as needed!
        this.loopPerspectives.add(loopPerspective);
    }

    public ArrayList<LoopPerspective> getPerspectives (Loop loop) {
        ArrayList<LoopPerspective> loopPerspectives = new ArrayList<LoopPerspective>();
        for (LoopPerspective loopPerspective : this.loopPerspectives) {
            if (loop == loopPerspective.getLoopConstruct ().getLoop ()) {
                loopPerspectives.add(loopPerspective);
            }
        }
        return loopPerspectives;
    }

    public void removePerspective(Loop loop) {

        for (int i = 0; i < this.loopPerspectives.size(); i++) {
            LoopPerspective loopPerspective = this.loopPerspectives.get(i);
            if (loop == loopPerspective.getLoopConstruct ().getLoop ()) {
                this.loopPerspectives.remove(i);
            }
        }
    }

    public void removePerspective (LoopPerspective loopPerspective) {
        this.loopPerspectives.remove (loopPerspective);
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
        double angle = this.getAngle (startAngle, stopAngle);
        return angle;
    }

    /**
     * Get the angle at which the specified point falls with respect to the center of the loop.
     *
     * @param point The point that defines the line, along with the point at the center of the loop, for which the angle will be determined.
     * @return The angle of the line formed by the specified point and the center point of the loop.
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
     * @param startingPoint The endpoint of the line from which the angle will be measured.
     * @param endingPoint The endpoint of the line forming the stopping angle.
     * @return The angle between the two lines formed by the specified points and the center point of the loop.
     */
    public double getAngle (Point startingPoint, Point endingPoint) {
        Point p1 = this.getPosition (); // The center point is p1.

        double a = startingPoint.x - p1.x;
        double b = startingPoint.y - p1.y;
        double c = endingPoint.x - p1.x;
        double d = endingPoint.y - p1.y;

        double atanA = Math.atan2 (a, b);
        double atanB = Math.atan2 (c, d);

        double result = Math.toDegrees (atanA - atanB);

        return result;
    }

    /**
     * Calculates the point on the circumference of the circle at the specified angle (in degrees).
     */
    public Point getPoint (double angle) {
        Point point = new Point ();
        double angleInRadians = Math.toRadians (this.startAngle + angle); // ((90.0 - angle) + angle);
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

    /**
     * Returns the behavior prior to the specified angle. This method assumes that behaviors are
     * stored in ascending order of their angles on the loop.
     */
    public BehaviorConstruct getBehaviorBeforeAngle (double angle) {

        // Calculate angles along the loop for each behavior
//        ArrayList<Double> behaviorAngles = new ArrayList<Double>();
        String behaviorAngles = "";
        for (BehaviorConstruct behavior : this.getLoop ().getBehaviors ()) {
            Point behaviorPosition = behavior.getPosition();
            double behaviorAngle = this.getAngle(behaviorPosition);
            behaviorAngles += behaviorAngle + ", ";
        }
//        Log.v ("Condition", "behaviorAngles = " + behaviorAngles);


        BehaviorConstruct previousBehavior = null;
        BehaviorConstruct behaviorBeforeAngle = null;
        for (BehaviorConstruct behavior : this.getLoop ().getBehaviors ()) {
            Point behaviorPosition = behavior.getPosition();
            double behaviorAngle = this.getAngle(behaviorPosition);

//            Log.v ("Condition", "angle = " + angle);
//            Log.v ("Condition", "behaviorAngle = " + behaviorAngle);

            if (behaviorAngle < angle) {
//                previousBehavior = behavior;
                behaviorBeforeAngle = behavior;
            } else {
//                behaviorBeforeAngle = previousBehavior;
                break;
            }

//            Log.v("Condition", "behaviorBeforeAngle.angle = " + behaviorAngle);
        }

        return behaviorBeforeAngle;
    }

    /**
     * Returns the behavior prior to the specified angle. This method assumes that behaviors are
     * stored in ascending order of their angles on the loop.
     */
    public BehaviorConstruct getBehaviorAfterAngle (double angle) {

        // Calculate angles along the loop for each behavior
//        ArrayList<Double> behaviorAngles = new ArrayList<Double>();
        String behaviorAngles = "";
        for (BehaviorConstruct behavior : this.getLoop ().getBehaviors ()) {
            Point behaviorPosition = behavior.getPosition();
            double behaviorAngle = this.getAngle(behaviorPosition);
            behaviorAngles += behaviorAngle + ", ";
        }
//        Log.v ("Condition", "behaviorAngles = " + behaviorAngles);


        BehaviorConstruct behaviorAfterAngle = null;
        for (BehaviorConstruct behavior : this.getLoop ().getBehaviors ()) {
            Point behaviorPosition = behavior.getPosition();
            double behaviorAngle = this.getAngle(behaviorPosition);

//            Log.v ("Condition", "angle = " + angle);
//            Log.v ("Condition", "behaviorAngle = " + behaviorAngle);

            if (behaviorAngle < angle) {
//                previousBehavior = behavior;
//                behaviorBeforeAngle = behavior;
            } else {
                behaviorAfterAngle = behavior;
//                behaviorBeforeAngle = previousBehavior;
                break;
            }

//            Log.v("Condition", "behaviorAfterAngle.angle = " + behaviorAngle);
        }

        return behaviorAfterAngle;
    }

    /**
     * Returns the behavior condition at the specified angle. This method assumes that behaviors
     * are stored in ascending order of their angles on the loop.
     */
    public BehaviorCondition getBehaviorConditionAtAngle (double angle) {

        BehaviorCondition behaviorCondition = null;

        BehaviorConstruct behaviorAfterAngle = this.getBehaviorAfterAngle (angle);
        if (behaviorAfterAngle != null) {
            behaviorCondition = behaviorAfterAngle.getCondition();
        }

        return behaviorCondition;
    }

    /**
     * Updates the ordering of the behaviors on the loop based on their position along the loop.
     */
    public void reorderBehaviors () {
        // Re-order the behaviors based on their sequence ordering

        // Calculate angles along the loop for each behavior
        ArrayList<Double> behaviorAngles = new ArrayList<Double>();
        for (BehaviorConstruct behavior : this.getLoop ().getBehaviors ()) {
            Point behaviorPosition = behavior.getPosition();
            double behaviorAngle = this.getAngle(behaviorPosition);
            behaviorAngles.add(behaviorAngle);
            Log.v("Clay", "Behavior " + behaviorAngle + " = " + behaviorAngle);
        }

        // Sort the list of behaviors based on the sort manipulations done to sort the angles in ascending order.
        for (int i = 0; i < this.getLoop ().getBehaviors ().size(); i++) {
            for (int j = 0; j < this.getLoop ().getBehaviors ().size () - 1; j++) {
                if (behaviorAngles.get(j) > behaviorAngles.get(j + 1)) {

                    // Swap angle
                    double angleToSwap = behaviorAngles.get(j);
                    behaviorAngles.set(j, behaviorAngles.get(j + 1));
                    behaviorAngles.set(j + 1, angleToSwap);

                    // Swap behavior
                    BehaviorConstruct behaviorToSwap = this.getLoop ().getBehaviors ().get (j);
                    this.getLoop ().getBehaviors ().set (j, getLoop ().getBehaviors ().get (j + 1));
                    this.getLoop ().getBehaviors ().set (j + 1, behaviorToSwap);
                }
            }
        }

        String loopSequence = "";
        for (BehaviorConstruct behavior : this.getLoop ().getBehaviors ()) {
            loopSequence += behavior.getBehavior().getTitle() + " ";
        }
        Log.v ("Clay", loopSequence);
    }
}
