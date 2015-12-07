package computer.clay.looper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AppSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private AppRenderingThread appRenderingThread;

    private SurfaceHolder surfaceHolder;

    // Canvas
    private int canvasWidth, canvasHeight;
    private Bitmap canvasBitmap = null;
    private Canvas myCanvas = null;
    private Matrix identityMatrix;
    private Paint paint = new Paint (Paint.ANTI_ALIAS_FLAG);

    // Clay
    private Clay clay = new Clay ();

    // Define base coordinate system
    private Point origin = new Point ();

    /**
     * Set the origin point.
     *
     * @param point The point of origin.
     */
    public void setOrigin (Point point) {
        this.origin = point;
    }

    /**
     * Returns the origin point of the perspective.
     *
     * @return The origin point of the perspective.
     */
    public Point getOrigin () {
        return this.origin;
    }

    public AppSurfaceView (Context context) {
        super (context);

//        this.system = new System ();
//        this.perspective = new Perspective (this.system);
//        this.touchPerson = new Person (this.system, this.perspective);
    }

    public AppSurfaceView (Context context, AttributeSet attrs) {
        super (context, attrs);
    }

    public AppSurfaceView (Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
    }

    @Override
    public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated (SurfaceHolder holder) {

        canvasWidth = getWidth ();
        canvasHeight = getHeight ();
        canvasBitmap = Bitmap.createBitmap (canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas ();
        myCanvas.setBitmap (canvasBitmap);

        // TODO: Move setPosition to a better location!
        clay.getPerspective ().setPosition (myCanvas.getWidth () / 2, myCanvas.getHeight () / 2);

        identityMatrix = new Matrix ();
    }

    @Override
    public void surfaceDestroyed (SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    public void AppSurfaceView_OnResume () {

        surfaceHolder = getHolder ();
        getHolder ().addCallback (this);

        // Create and start background Thread
        appRenderingThread = new AppRenderingThread (this);
        appRenderingThread.setRunning (true);
        appRenderingThread.start ();

    }

    public void AppSurfaceView_OnPause () {

        // Kill the background Thread
        boolean retry = true;
        appRenderingThread.setRunning (false);

        while (retry) {
            try {
                appRenderingThread.join ();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
        }
    }

    @Override
    protected void onDraw (Canvas canvas) {

        // TODO: Draw the text messaging output area (that shows the most recent message). It should float over the top of the perspective.

        // Move the perspective
        myCanvas.save ();
        myCanvas.translate (clay.getPerspective ().getPosition ().x, clay.getPerspective ().getPosition ().y);
        myCanvas.scale (clay.getPerspective ().getScaleFactor (), clay.getPerspective ().getScaleFactor ());

        // Draw the background
        myCanvas.drawColor (Color.WHITE);

        // Create the default, empty loop if none exist.
        if (this.clay.getSystem ().getLoops().size () == 0) {

            // Create a loop on the system if one doesn't exist.
            Loop defaultLoop = new Loop (this.clay.getSystem ());
            this.clay.getSystem ().addLoop (defaultLoop);

        }

        // Draw the loops
        for (Loop loop : this.clay.getSystem ().getLoops ()) {

            // Prepare for drawing. Check if the specified loop has a perspective. If not, add one for it, so it can be rendered.
            // TODO: Put this in the constructor for the LoopConstruct, so it will create itself as a default construct if there's not already a construct for the specified Loop.
            prepareLoopConstruct (loop);

            // Draw loops and behaviors.
//            drawLoopConstruct (myCanvas, loop); // Draw the loop's construct.
            myCanvas.save ();

            drawLoopConstructPerspectives (myCanvas, loop); // Draw perspectives on the loop
            drawBehaviorConditions (myCanvas, loop); // Draw behavior conditions
            drawBehaviorConstructs (myCanvas, loop); // Draw behaviors on the loop.
//            drawCandidatePerspectives (myCanvas, loop); // Draw the candidate perspective(s) of the loop (if any).

            myCanvas.restore ();

        }

        // Draw behavior constructs that are NOT on a loop. Behavior constructs on a loop are drawn above.
        drawBehaviorConstructs (myCanvas, this.clay.getSystem ());

        // Paint the bitmap to the "primary" canvas
        canvas.drawBitmap (canvasBitmap, identityMatrix, null);

        // Restore the perspective translation.
        myCanvas.restore ();


        // HACK: TODO: Move this to a separate "behavior execution thread" that performs the behavior and updates the behavior/construct states.
//        for (Loop loop : this.clay.getSystem ().getLoops ()) {
//            for (BehaviorConstruct behaviorConstruct : loop.getBehaviors ()) {
//                Behavior behavior = behaviorConstruct.getBehavior ();
//                behavior.perform ();
//            }
//        }

    }

    /**
     * Prepare a loop construct for the specified loop.
     *
     * @param loop
     */
    void prepareLoopConstruct (Loop loop) {

        // Check if the specified loop has a perspective. If not, add one for it, so it can be rendered.
        // TODO: Put this in the constructor for the LoopConstruct, so it will create itself as a default construct if there's not already a construct for the specified Loop.
        if (!this.clay.getPerspective ().hasLoopConstruct (loop)) {

            // Create default loop construct
            // TODO: Move this into the Perspective class (maybe in getLoopConstruct and remove hasLoopConstruct since it will always be true)
            this.clay.getPerspective ().createLoopConstruct (loop);

            if (!this.clay.getPerspective ().getLoopConstruct (loop).hasPerspectives (loop)) {

                LoopConstruct loopConstruct = this.clay.getPerspective ().getLoopConstruct (loop);

                Log.v ("Clay_Loop_Perspective", "A loop has no perspectives. Creating one.");
                LoopPerspective defaultLoopPerspective = new LoopPerspective (loopConstruct);
                defaultLoopPerspective.setStartAngle(-105);
                defaultLoopPerspective.setSpan(330);

                this.clay.getPerspective ().getLoopConstruct (loop).addPerspective (defaultLoopPerspective);

            }

        } else {

            // TODO: Make sure the loop construct's perspectives are set up properly

        }
    }

    /**
     * Draws the specified loop construct.
     *
     * @param canvas
     * @param loop
     */
    void drawLoopConstruct (Canvas canvas, Loop loop) {

        LoopConstruct loopConstruct = this.clay.getPerspective ().getLoopConstruct (loop);

        // Draw the loop
        float loopLeft = this.getOrigin ().x + loopConstruct.getPosition ().x - loopConstruct.getRadius ();
        float loopTop = this.getOrigin ().y + -1 * loopConstruct.getPosition ().y - loopConstruct.getRadius ();
        float loopRight = this.getOrigin ().x + loopConstruct.getPosition ().x + loopConstruct.getRadius ();
        float loopBottom = this.getOrigin ().y + -1 * loopConstruct.getPosition ().y + loopConstruct.getRadius ();

        canvas.save ();

        // Set the loop's style
        paint.setStyle (Paint.Style.STROKE);
        paint.setStrokeWidth (2);
        paint.setColor (Color.LTGRAY);

        // Draw the loop
        canvas.drawArc (loopLeft, loopTop, loopRight, loopBottom, -90 + loopConstruct.getStartAngle (), loopConstruct.getAngleSpan (), false, paint);

        // Draw arrowhead on loop
        canvas.save ();
//            myCanvas.translate(perspective.getPosition().x, perspective.getPosition().y);
        canvas.rotate (-1 * (360 - (loopConstruct.getStartAngle () + loopConstruct.getAngleSpan ())));
        canvas.translate (0, -1 * loopConstruct.getRadius ());

        // Set the arrowhead's style
        paint.setStyle (Paint.Style.STROKE);
        paint.setStrokeWidth (2);
        paint.setColor (Color.LTGRAY);

        // Draw the arrowhead
        canvas.drawLine (-20, -20, 0, 0, paint);
        canvas.drawLine (-20, 20, 0, 0, paint);

        canvas.restore ();
    }

    /**
     * Draw the specified loop construct's perspectives.
     *
     * @param canvas
     * @param loop
     */
    void drawLoopConstructPerspectives (Canvas canvas, Loop loop) {

        LoopConstruct loopConstruct = this.clay.getPerspective ().getLoopConstruct (loop);

        canvas.save ();

        if (this.clay.getPerspective ().hasLoopConstruct (loop)) {

            if (this.clay.getPerspective ().getLoopConstruct (loop).hasPerspectives (loop)) {

                // TODO: Support multiple perspectives per "loop" (loop concept/placeholder)

                for (LoopPerspective loopPerspective : this.clay.getPerspective ().getLoopConstruct (loop).getPerspectives (loop)) {

                    // Draw the loop
                    float loopLeft = this.getOrigin().x + loopConstruct.getPosition ().x - loopPerspective.getRadius ();
                    float loopTop = this.getOrigin().y + -1 * loopConstruct.getPosition ().y - loopPerspective.getRadius ();
                    float loopRight = this.getOrigin().x + loopConstruct.getPosition ().x + loopPerspective.getRadius ();
                    float loopBottom = this.getOrigin().y + -1 * loopConstruct.getPosition ().y + loopPerspective.getRadius ();

                    //                LoopPerspective loopPerspective = this.perspective.getPerspective(loop);

                    if (loopPerspective.startAnglePoint != null && loopPerspective.spanPoint != null) {

//                        paint.setStyle (Paint.Style.FILL);
//                        paint.setStrokeWidth (2);
//                        paint.setColor (Color.RED);
//
//                        canvas.drawCircle (loopPerspective.startAnglePoint.x, loopPerspective.startAnglePoint.y, 10, paint);
//                        canvas.drawCircle (loopPerspective.spanPoint.x, loopPerspective.spanPoint.y, 10, paint);

                        int radiusExtension = 100;
                        int innerLoopRadius = 0; // TODO: Change this dynamically, based on the angular sweep size.

                        double cutStartAngle = loopConstruct.getStartAngle () + loopConstruct.getAngle (loopPerspective.startAnglePoint);
                        Point cutStartPoint = loopConstruct.getPoint (cutStartAngle, loopPerspective.getRadius () + radiusExtension); // Point cutStartPoint = loop.getPoint (cutStartAngle, loop.getRadius () + radiusExtension);
                        double cutStopAngle = loopConstruct.getStartAngle () + loopConstruct.getAngle (loopPerspective.startAnglePoint) + loopPerspective.getSpan ();
                        Point cutStopPoint = loopConstruct.getPoint (cutStopAngle, loopPerspective.getRadius () + radiusExtension); // Point cutStopPoint = loop.getPoint (cutStopAngle, loop.getRadius () + radiusExtension);

                        // Draw the loop
                        float perspectiveLoopLeft = this.getOrigin().x + loopConstruct.getPosition ().x - loopPerspective.getRadius ();
                        float perspectiveLoopTop = this.getOrigin().y + -1 * loopConstruct.getPosition ().y - loopPerspective.getRadius ();
                        float perspectiveLoopRight = this.getOrigin().x + loopConstruct.getPosition ().x + loopPerspective.getRadius ();
                        float perspectiveLoopBottom = this.getOrigin().y + -1 * loopConstruct.getPosition ().y + loopPerspective.getRadius ();

                        // Draw the filled arc highlighting the perspective's area
                        paint.setStyle (Paint.Style.FILL);
                        paint.setStrokeWidth (2);
                        paint.setColor (Color.WHITE);

                        canvas.drawArc (perspectiveLoopLeft - radiusExtension, perspectiveLoopTop - radiusExtension, perspectiveLoopRight + radiusExtension, perspectiveLoopBottom + radiusExtension, (float) cutStartAngle + loopConstruct.getStartAngle (), (float) cutStopAngle - (float) cutStartAngle, true, paint);

                        // Draw the loop in the cut
                        paint.setStyle (Paint.Style.STROKE);
                        paint.setStrokeWidth (2);
                        paint.setColor (Color.LTGRAY);

                        canvas.drawArc (perspectiveLoopLeft - innerLoopRadius, perspectiveLoopTop - innerLoopRadius, perspectiveLoopRight + innerLoopRadius, perspectiveLoopBottom + innerLoopRadius, (float) cutStartAngle + loopConstruct.getStartAngle (), (float) cutStopAngle - (float) cutStartAngle, false, paint);

                        // Draw the line indicating the start of the cut.
                        if (loopPerspective.getPreviousPerspective() != null) {
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeWidth(2);
                            paint.setColor(Color.parseColor("#008080"));

                            canvas.drawLine(loopConstruct.getPosition().x, loopConstruct.getPosition().y, cutStartPoint.x, cutStartPoint.y, paint);
                        }

                        // Draw the line indicating the end of the cut.
                        if (loopPerspective.getNextPerspective() != null) {

                            if (loopPerspective.getSpan() < 0) {
                                paint.setStyle(Paint.Style.STROKE);
                                paint.setStrokeWidth(2);
                                paint.setColor(Color.BLUE);
                            } else {
                                paint.setStyle(Paint.Style.STROKE);
                                paint.setStrokeWidth(2);
                                paint.setColor(Color.GREEN);
                            }

                            canvas.drawLine(loopConstruct.getPosition().x, loopConstruct.getPosition().y, cutStopPoint.x, cutStopPoint.y, paint);
                        }


                        // Draw the arrowhead if this is the final perspective
                        if (loopPerspective.getNextPerspective() == null) {

                            // Draw arrowhead on loop
                            canvas.save();

//                        canvas.rotate (-1 * (360 - (loopPerspective.getStartAngle () + loopPerspective.getSpan())));
                            canvas.rotate ((int) cutStopAngle + 90 + 15);
                            canvas.translate(0, -1 * loopPerspective.getRadius());

                            // Set the arrowhead's style
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeWidth(2);
                            paint.setColor(Color.BLACK);

                            // Draw the arrowhead
                            canvas.drawLine(-20, -20, 0, 0, paint);
                            canvas.drawLine(-20, 20, 0, 0, paint);

                            canvas.restore();

                        }

                    }

                }

            }
        }

        canvas.restore ();
    }

    /**
     * Draw behavior condition constructs for the specified loop.
     *
     * @param canvas
     * @param loop
     */
    void drawBehaviorConditions (Canvas canvas, Loop loop) {
    // TODO: void drawBehaviorConditions (Canvas canvas, LoopPerspective loopPerspective)

        LoopConstruct loopConstruct = this.clay.getPerspective ().getLoopConstruct (loop);

        // Define base coordinate system
        float xOrigin = 0;
        float yOrigin = 0;

        canvas.save ();

//        for (LoopPerspective loopPerpsective : loopConstruct.getLoopPerspectives()) {
//
//            loopPerpsective.get
//
//        }

        // TODO: Start drawing behaviors with the first behavior shown by the perspective, and show the number based on the span of the perspective.

        float previousBehaviorAngle = -90 + loopConstruct.getStartAngle (); // Initialize behavior condition to be the start of the loop.

        for (BehaviorConstruct behaviorConstruct : loopConstruct.getBehaviorConstructs()) { // for (BehaviorConstruct behaviorConstruct : loop.getBehaviors ()) {

            // TODO: Get condition type and render the condition according to its type (e.g., for "switch" type, draw an arrowhead).

            /* Draw the behavior conditions */

            // Calculate the angle of the behavior construct relative to its loop.
            float behaviorAngle = (float) loopConstruct.getAngle (behaviorConstruct.getPosition ()) + (2 * loopConstruct.getStartAngle ());
            LoopPerspective loopPerspective = loopConstruct.getPerspective (behaviorAngle);

            // Calculate the geometry representing the loop's condition and flow.
            float loopLeft = xOrigin + loopConstruct.getPosition ().x - loopPerspective.getRadius ();
            float loopTop = yOrigin + -1 * loopConstruct.getPosition ().y - loopPerspective.getRadius ();
            float loopRight = xOrigin + loopConstruct.getPosition ().x + loopPerspective.getRadius ();
            float loopBottom = yOrigin + -1 * loopConstruct.getPosition ().y + loopPerspective.getRadius ();

            // Update the starting angle of the conditional arc.
//            if (previousBehaviorAngle < loopPerspective.getStartAngle() + 30) {
//                previousBehaviorAngle = loopPerspective.getStartAngle() + 30;
//            }

            if (behaviorConstruct.hasCondition ()) {

                if (behaviorConstruct.getCondition ().getType () == BehaviorCondition.Type.NONE) {

                    // Set the behavior condition's style
                    paint.setStyle (Paint.Style.STROKE);
                    paint.setStrokeWidth(2);
                    paint.setColor(Color.BLACK);

                    // Draw the condition
                    canvas.drawArc (loopLeft, loopTop, loopRight, loopBottom, (float) previousBehaviorAngle, (float) behaviorAngle - previousBehaviorAngle, false, paint);

                } else if (behaviorConstruct.getCondition ().getType () == BehaviorCondition.Type.SWITCH) {

                    float spacingBeforeArc = (loopPerspective.hasPreviousPerspective () ? 0.0f : 20.0f);
                    float spacingAfterArc = (loopPerspective.hasNextPerspective() ? 0.0f : 20.0f);

                    if (loopPerspective.hasPreviousPerspective ()) {
                        spacingBeforeArc = 15.0f;
                        spacingAfterArc = 15.0f;
                    } else {
                        spacingBeforeArc = 0.0f;
                        spacingAfterArc = 15.0f;
                    }

                    // Set the behavior condition's style
                    paint.setStyle (Paint.Style.STROKE);
                    paint.setStrokeWidth (2);
                    paint.setColor (Color.CYAN);

                    // Draw the condition
                    canvas.drawArc (loopLeft, loopTop, loopRight, loopBottom, ((float) previousBehaviorAngle + spacingBeforeArc), (float) behaviorAngle - previousBehaviorAngle - (spacingBeforeArc + spacingAfterArc), false, paint);

                    // Draw arrowhead on loop
                    canvas.save ();
                    canvas.rotate (((float) behaviorAngle + 90.0f - spacingAfterArc));
                    canvas.translate (0, -1 * loopPerspective.getRadius ());

                    // Set the arrowhead's style
                    paint.setStyle (Paint.Style.STROKE);
                    paint.setStrokeWidth (2);
                    paint.setColor (Color.CYAN);

                    // Draw the arrowhead
                    canvas.drawLine (-20, -20, 0, 0, paint);
                    canvas.drawLine (-20, 20, 0, 0, paint);

                    canvas.restore ();

                } else if (behaviorConstruct.getCondition ().getType () == BehaviorCondition.Type.THRESHOLD) {

                    // Set the behavior condition's style
                    paint.setStyle (Paint.Style.STROKE);
                    paint.setStrokeWidth (2);
                    paint.setColor (Color.GREEN);

                    // Draw the condition
                    canvas.drawArc (loopLeft, loopTop, loopRight, loopBottom, (float) previousBehaviorAngle, (float) behaviorAngle - previousBehaviorAngle, false, paint);

                } else if (behaviorConstruct.getCondition ().getType () == BehaviorCondition.Type.GESTURE) {

                    // Set the behavior condition's style
                    paint.setStyle (Paint.Style.STROKE);
                    paint.setStrokeWidth (2);
                    paint.setColor (Color.RED);

                    // Draw the condition
                    canvas.drawArc (loopLeft, loopTop, loopRight, loopBottom, (float) previousBehaviorAngle, (float) behaviorAngle - previousBehaviorAngle, false, paint);

                } else if (behaviorConstruct.getCondition ().getType () == BehaviorCondition.Type.MESSAGE) {

                    // Set the behavior condition's style
                    paint.setStyle (Paint.Style.STROKE);
                    paint.setStrokeWidth (2);
                    paint.setColor (Color.BLUE);
                    paint.setPathEffect (new DashPathEffect (new float[] { 10, 20 }, 0));

                    // Draw the condition
                    canvas.drawArc (loopLeft, loopTop, loopRight, loopBottom, (float) previousBehaviorAngle, (float) behaviorAngle - previousBehaviorAngle, false, paint);

                    paint.setPathEffect (new PathEffect ()); // HACK: This undoes the dash effect! Better way would be to give individual control to drawn elements over their dash stroke (or otherwise).

                }
            }

            // Store the current behavior's angle for calculating the geometry for the subsequent behavior's condition.
            previousBehaviorAngle = behaviorAngle;

        }

        canvas.restore ();
    }

    /**
     * Draw the behavior constructs on the specified loop.
     *
     * @param canvas
     * @param loop
     */
    void drawBehaviorConstructs (Canvas canvas, Loop loop) {

        canvas.save ();

        LoopConstruct loopConstruct = this.clay.getPerspective().getLoopConstruct (loop);

        if (loopConstruct.hasBehaviorConstructs ()) {
            for (BehaviorConstruct behaviorConstruct : loopConstruct.getBehaviorConstructs ()) { // for (BehaviorConstruct behaviorConstruct : loop.getBehaviors ()) {

//                Log.v ("Clay_Loop_Construct", behaviorConstruct.getBehavior ().getTitle () + ": " + behaviorConstruct.state);

                // Set style for behaviorConstruct node interior
                paint.setStyle (Paint.Style.FILL_AND_STROKE);
                paint.setStrokeWidth (2);
                paint.setColor (Color.WHITE);

                // Draw behaviorConstruct node interior
                canvas.drawCircle (behaviorConstruct.getPosition ().x, behaviorConstruct.getPosition ().y, behaviorConstruct.getRadius (), paint);

                // Set style for behaviorConstruct node border
                paint.setStyle (Paint.Style.STROKE);
                paint.setStrokeWidth (2);
                paint.setColor (Color.BLACK);

                // Draw behaviorConstruct node border
                canvas.drawCircle (behaviorConstruct.getPosition ().x, behaviorConstruct.getPosition ().y, behaviorConstruct.getRadius (), paint);

                // Set style for behavior's label
                paint.setStyle (Paint.Style.FILL);
                paint.setStrokeWidth (0);
                paint.setColor (Color.BLACK);

                // Set style for behavior's label
                String name = "None";
                if (behaviorConstruct.hasBehavior ()) {
                    name = behaviorConstruct.getBehavior ().getTitle ();
                }
                Rect textBounds = new Rect ();
                paint.getTextBounds (name, 0, name.length (), textBounds);
                paint.setTextSize (35);
                canvas.drawText (name, behaviorConstruct.getPosition ().x - textBounds.exactCenterX (), behaviorConstruct.getPosition ().y - textBounds.exactCenterY (), paint);

                /* Draw snapping path to nearest loop. */

                // Set the nearest loop (the one in which this behavior is contained) and snap to that one (ongoing).
                Loop nearestLoop = loop; // TODO: Replace with nearestLoopConstruct

                // Get the loop construct associated with the loop.
                LoopConstruct nearestLoopConstruct = this.clay.getPerspective ().getLoopConstruct (loop);

                // TODO: behaviorConstruct.isTouched
                // TODO: behaviorConstruct.getTouchAngle -OR- behaviorConstruct.getTouchPoint

                // TODO: get nearestLoopPerspective
//                LoopPerspective nearestLoopPerspective = nearestLoopConstruct.getPerspective (this.clay.getPerson ().getTouch ());


                // TODO: Get the angle of the behavior WRT the nearest loop construct (may not be this loop construct, if it is moved!)
                double behaviorConstructAngle = nearestLoopConstruct.getAngle (behaviorConstruct.getPosition ());

                // TODO: Get the perspective at the behavior's angle

                LoopPerspective nearestLoopConstructPerspective = nearestLoopConstruct.getPerspective (behaviorConstructAngle);

                // TODO: Get the radius of the perspective

                // Draw snapping path to nearest loop
                if (behaviorConstruct.getDistanceToLoopPerspective (nearestLoopConstructPerspective) < nearestLoopConstructPerspective.getSnapDistance ()) { // if (behaviorConstruct.getDistanceToLoopPerspective (nearestLoopConstructPerspective) < nearestLoopConstructPerspective.getRadius ()) {
                    Point nearestPoint = behaviorConstruct.getNearestPoint (nearestLoopConstructPerspective);
                    canvas.drawLine (behaviorConstruct.getPosition ().x, behaviorConstruct.getPosition ().y, nearestPoint.x, nearestPoint.y, paint);
                }
            }
        }

        canvas.restore ();
    }

    /**
     * Draw behavior constructs in the specified system that are not on a loop.
     *
     * @param canvas
     * @param system
     */
    void drawBehaviorConstructs (Canvas canvas, System system) {
        //        String behaviorStates = "";

        for (BehaviorConstruct behaviorConstruct : this.clay.getPerspective ().getBehaviorConstructs ()) {

            // Only draw loops that are NOT on a loop!
            if (behaviorConstruct.hasLoopConstruct ()) { // if (behaviorConstruct.hasLoop ()) {
                continue;
            }

            // Set style for behaviorConstruct node interior
            paint.setStyle (Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth (2);
            paint.setColor (Color.WHITE);

            // Draw behaviorConstruct node interior
            myCanvas.drawCircle (behaviorConstruct.getPosition ().x, behaviorConstruct.getPosition ().y, behaviorConstruct.getRadius (), paint);

            // Set style for behaviorConstruct node border
            paint.setStyle (Paint.Style.STROKE);
            paint.setStrokeWidth (2);
            paint.setColor (Color.BLACK);

            // Draw behaviorConstruct node border
            myCanvas.drawCircle (behaviorConstruct.getPosition ().x, behaviorConstruct.getPosition ().y, behaviorConstruct.getRadius (), paint);

            // Draw behavior's label

            paint.setStyle (Paint.Style.FILL);
            paint.setStrokeWidth (0);
            paint.setColor (Color.BLACK);

            String name = "None";
            if (behaviorConstruct.hasBehavior ()) {
                name = behaviorConstruct.getBehavior ().getTitle ();
            }
            Rect textBounds = new Rect ();
            paint.getTextBounds (name, 0, name.length (), textBounds);
            paint.setTextSize (35);
            myCanvas.drawText (name, behaviorConstruct.getPosition ().x - textBounds.exactCenterX (), behaviorConstruct.getPosition ().y - textBounds.exactCenterY (), paint);

            /* Draw snapping path to nearest loop. */

            // Search for the nearest loop and snap to that one (ongoing).
            LoopConstruct nearestLoopConstruct = this.clay.getPerspective ().getNearestLoopConstruct (behaviorConstruct);
            //            double nearestLoopDistance = Double.POSITIVE_INFINITY;
            //            for (Loop loop : this.system.getLoops()) {
            //                if (behaviorConstruct.getDistanceToLoop(loop) < nearestLoopDistance) {
            //                    nearestLoop = loop;
            //                }
            //            }


            // TODO: Get nearest perspective.
            //            this.touchPerson.getTouch (i)
            // TODO: Iterate through gesture's touches. Check if any touch is holding a behavior. For those, check if they are near enough to the perspective's radius and then snap it on!

            // TODO: Get relative position in nearest perspective based on start angle, first behavior visible, stop angle, and last behavior visible.

            // TODO: Get radius of nearest perspective.

            // Get the loop construct associated with the loop.
//            LoopConstruct nearestLoopConstruct = this.clay.getPerspective ().getLoopConstruct (loop);

            // TODO: behaviorConstruct.isTouched
            // TODO: behaviorConstruct.getTouchAngle -OR- behaviorConstruct.getTouchPoint

            // TODO: get nearestLoopPerspective
//                LoopPerspective nearestLoopPerspective = nearestLoopConstruct.getPerspective (this.clay.getPerson ().getTouch ());




            // TODO: Get the angle of the behavior WRT the nearest loop construct (may not be this loop construct, if it is moved!)
            double behaviorConstructAngle = nearestLoopConstruct.getAngle (behaviorConstruct.getPosition ());

            // TODO: Get the perspective at the behavior's angle

            LoopPerspective nearestLoopConstructPerspective = nearestLoopConstruct.getPerspective (behaviorConstructAngle);

            // TODO: Get the radius of the perspective

            // Draw snapping path to nearest loop
//                if (behaviorConstruct.getDistanceToLoop (nearestLoop) < nearestLoopPerspective.getRadius ()) {
            if (behaviorConstruct.getDistanceToLoopPerspective (nearestLoopConstructPerspective) < 200) {
                Point nearestPoint = behaviorConstruct.getNearestPoint (nearestLoopConstructPerspective);
                //Point nearestPoint = nearestLoopConstructPerspective.getNearestPoint (nearestLoopConstruct);
                canvas.drawLine (behaviorConstruct.getPosition ().x, behaviorConstruct.getPosition ().y, nearestPoint.x, nearestPoint.y, paint);
            }

            // Draw snapping path to nearest loop
//            if (behaviorConstruct.getDistanceToLoop (nearestLoopConstruct) < 250) {
//                Point nearestPoint = behaviorConstruct.getNearestPoint (nearestLoopConstruct);
//                myCanvas.drawLine (behaviorConstruct.getPosition ().x, behaviorConstruct.getPosition ().y, nearestPoint.x, nearestPoint.y, paint);
//            }
        }
        //        Log.v("Clay", behaviorStates);
    }

    void drawCandidatePerspectives (Canvas canvas, Loop loop) {

        LoopConstruct loopConstruct = this.clay.getPerspective ().getLoopConstruct (loop);

        myCanvas.save ();

        if (this.clay.getPerspective ().hasLoopConstruct (loop)) {

            if (this.clay.getPerspective ().getLoopConstruct (loop).hasCandidatePerspective (loop)) {

                LoopPerspective candidateLoopPerspective = this.clay.getPerspective ().getLoopConstruct (loop).getCandidatePerspective (loop);

                if (candidateLoopPerspective.startAnglePoint != null && candidateLoopPerspective.spanPoint != null) {

                    int radiusExtension = candidateLoopPerspective.getRadius ();
                    int innerLoopRadius = 40; // TODO: Change this dynamically, based on the angular sweep size.

                    double cutStartAngle = loopConstruct.getStartAngle () + loopConstruct.getAngle (candidateLoopPerspective.startAnglePoint);
                    Point cutStartPoint = loopConstruct.getPoint (cutStartAngle, candidateLoopPerspective.getRadius ()); // Point cutStartPoint = loop.getPoint (cutStartAngle, loop.getRadius () + radiusExtension);
                    double cutStopAngle = loopConstruct.getStartAngle () + loopConstruct.getAngle (candidateLoopPerspective.startAnglePoint) + candidateLoopPerspective.getSpan ();
                    Point cutStopPoint = loopConstruct.getPoint (cutStopAngle, candidateLoopPerspective.getRadius ()); // Point cutStopPoint = loop.getPoint (cutStopAngle, loop.getRadius () + radiusExtension);

                    // Define base coordinate system
                    float xOrigin = 0;
                    float yOrigin = 0;

                    // Draw the loop
                    float perspectiveLoopLeft = xOrigin + loopConstruct.getPosition ().x - candidateLoopPerspective.getRadius ();
                    float perspectiveLoopTop = yOrigin + -1 * loopConstruct.getPosition ().y - candidateLoopPerspective.getRadius ();
                    float perspectiveLoopRight = xOrigin + loopConstruct.getPosition ().x + candidateLoopPerspective.getRadius ();
                    float perspectiveLoopBottom = yOrigin + -1 * loopConstruct.getPosition ().y + candidateLoopPerspective.getRadius ();

                    // Draw the filled arc highlighting the perspective's area

                    paint.setStyle (Paint.Style.FILL);
                    paint.setStrokeWidth (2);
                    paint.setColor (Color.WHITE);

                    myCanvas.drawArc (perspectiveLoopLeft - radiusExtension, perspectiveLoopTop - radiusExtension, perspectiveLoopRight + radiusExtension, perspectiveLoopBottom + radiusExtension, (float) cutStartAngle + loopConstruct.getStartAngle (), (float) cutStopAngle - (float) cutStartAngle, true, paint);

                    // Draw the loop in the cut

                    paint.setStyle (Paint.Style.STROKE);
                    paint.setStrokeWidth (2);
                    paint.setColor (Color.BLACK);

                    // TODO: Calculate loopLeft, etc.

                    myCanvas.drawArc (perspectiveLoopLeft - innerLoopRadius, perspectiveLoopTop - innerLoopRadius, perspectiveLoopRight + innerLoopRadius, perspectiveLoopBottom + innerLoopRadius, (float) cutStartAngle + loopConstruct.getStartAngle (), (float) cutStopAngle - (float) cutStartAngle, false, paint);

                    // Draw the line indicating the start of the cut.

                    paint.setStyle (Paint.Style.STROKE);
                    paint.setStrokeWidth (2);
                    paint.setColor (Color.parseColor ("#008080"));

                    myCanvas.drawLine (loopConstruct.getPosition ().x, loopConstruct.getPosition ().y, cutStartPoint.x, cutStartPoint.y, paint);

                    // Draw the line indicating the end of the cut.

                    if (candidateLoopPerspective.getSpan () < 0) {
                        paint.setStyle (Paint.Style.STROKE);
                        paint.setStrokeWidth (2);
                        paint.setColor (Color.BLUE);
                    } else {
                        paint.setStyle (Paint.Style.STROKE);
                        paint.setStrokeWidth (2);
                        paint.setColor (Color.GREEN);
                    }

                    myCanvas.drawLine (loopConstruct.getPosition ().x, loopConstruct.getPosition ().y, cutStopPoint.x, cutStopPoint.y, paint);

                }
            }
        }

        myCanvas.restore ();
    }

    public void updateStates () {
        // Dummy method() to handle the States
    }

    public void updateSurfaceView () {
        // The function run in background thread, not UI thread.

        Canvas canvas = null;

        try {
            canvas = surfaceHolder.lockCanvas ();

            synchronized (surfaceHolder) {
                updateStates ();
                if (canvas != null) {
                    onDraw(canvas);
                }
            }
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost (canvas);
            }
        }
    }

    // TODO: Attach this onTouchEvent handler to the "canvas" that captures continuous motion.
    @Override
    public boolean onTouchEvent (MotionEvent motionEvent) {
        int pointerIndex = ((motionEvent.getAction () & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
        int pointerId = motionEvent.getPointerId (pointerIndex);
        int touchAction = (motionEvent.getAction () & MotionEvent.ACTION_MASK);
        int pointCount = motionEvent.getPointerCount ();

        int MAXIMUM_TOUCH_COUNT = 5;

        double[] xTouches = new double[MAXIMUM_TOUCH_COUNT];
        double[] yTouches = new double[MAXIMUM_TOUCH_COUNT];

        if (pointCount <= MAXIMUM_TOUCH_COUNT) {
            if (pointerIndex <= MAXIMUM_TOUCH_COUNT - 1) {

                // Update touch action state with the raw touch information provided by the operating platform (i.e., Android).
                for (int i = 0; i < pointCount; i++) {
                    int id = motionEvent.getPointerId (i);
                    xTouches[id] = (motionEvent.getX (i) - clay.getPerspective ().getPosition ().x) / clay.getPerspective ().getScaleFactor (); // HACK: TODO: Get x position directly!
                    yTouches[id] = (motionEvent.getY (i) - clay.getPerspective ().getPosition ().y) / clay.getPerspective ().getScaleFactor (); // HACK: TODO: Get y position directly!
                }

//                // Check if touching _any_ behaviors (or loops, or canvas, or perspective). If so, keep the canvas locked, and find the action that's being touched.
//                for (BehaviorConstruct action : this.system.getBehaviors ()) {
//                    double distanceToTouch = action.getDistance ((int) xTouch[pointerId], (int) yTouch[pointerId]);
//                    if (distanceToTouch < action.getRadius () + 20) {
//                        touchedBehaviors.add (action);
//                        isPerformingBehaviorGesture = true;  // TODO: Set state of finger
////                        action.state = BehaviorConstruct.State.MOVING; // Set state of touched action
//                    }
//                }

                // Update the state of the touched object based on the current touch interaction state.
                if (touchAction == MotionEvent.ACTION_DOWN) {

                    clay.getPerson ().touch (pointerId, xTouches[pointerId], yTouches[pointerId]);
                    clay.getPerson ().classify (pointerId);

                } else if (touchAction == MotionEvent.ACTION_POINTER_DOWN) {

//                    isTouch[pointerId] = true;

                } else if (touchAction == MotionEvent.ACTION_MOVE) {

                    clay.getPerson ().touch (pointerId, xTouches[pointerId], yTouches[pointerId]);
                    clay.getPerson ().classify (pointerId);

                } else if (touchAction == MotionEvent.ACTION_UP) {

                    clay.getPerson ().untouch (pointerId, xTouches[pointerId], yTouches[pointerId]);
                    clay.getPerson ().classify(pointerId);

                } else if (touchAction == MotionEvent.ACTION_POINTER_UP) {

//                    isTouch[pointerId] = false;
//                    isTouchPrevious[pointerId] = false;

                } else if (touchAction == MotionEvent.ACTION_CANCEL) {

//                    isTouch[pointerId] = false;
//                    isTouchPrevious[pointerId] = false;

                } else {

//                    isTouch[pointerId] = false;
//                    isTouchPrevious[pointerId] = false;

                }
            }
        }

        return true;
    }

}