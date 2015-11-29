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

    private int canvasWidth, canvasHeight;
    private Bitmap canvasBitmap = null;
    private Canvas myCanvas = null;
    private Matrix identityMatrix;

    private Paint paint = new Paint (Paint.ANTI_ALIAS_FLAG);

    private Substrate substrate = new Substrate ();
    private Perspective perspective = new Perspective (this.substrate);
    private Interface touchInterface = new Interface(this.substrate, this.perspective);

    public AppSurfaceView (Context context) {
        super (context);

//        this.substrate = new Substrate ();
//        this.perspective = new Perspective (this.substrate);
//        this.touchInterface = new Interface (this.substrate, this.perspective);
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

        canvasWidth = getWidth();
        canvasHeight = getHeight();
        canvasBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas();
        myCanvas.setBitmap (canvasBitmap);

        // TODO: Move setPosition to a better location!
        perspective.setPosition (myCanvas.getWidth() / 2, myCanvas.getHeight() / 2);

        identityMatrix = new Matrix();
    }

    @Override
    public void surfaceDestroyed (SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    public void AppSurfaceView_OnResume() {

        surfaceHolder = getHolder ();
        getHolder ().addCallback (this);

        // Create and start background Thread
        appRenderingThread = new AppRenderingThread (this);
        appRenderingThread.setRunning (true);
        appRenderingThread.start();

    }

    public void AppSurfaceView_OnPause() {

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

        // Move the perspective
        myCanvas.save();
        myCanvas.translate(perspective.getPosition().x, perspective.getPosition().y);
        myCanvas.scale(perspective.getScaleFactor(), perspective.getScaleFactor());

        // Draw the background
        myCanvas.drawColor(Color.WHITE);

        // Define base coordinate system
        float xOrigin = 0;
        float yOrigin = 0;

        // Create the default, empty loop if none exist.
        if (this.substrate.loops.size () == 0) {

            // Create a loop on the substrate if one doesn't exist.
            Loop defaultLoop = new Loop (this.substrate);
            this.substrate.addLoop(defaultLoop);
        }

        // Draw the loops
        for (Loop loop : this.substrate.getLoops ()) {

            myCanvas.save();

            // Set the loop's style
            paint.setStyle (Paint.Style.STROKE);
            paint.setStrokeWidth (2);
            paint.setColor (Color.LTGRAY);

            // Draw the loop
            float loopLeft   = xOrigin +      loop.getPosition ().x - loop.getRadius ();
            float loopTop    = yOrigin + -1 * loop.getPosition ().y - loop.getRadius ();
            float loopRight  = xOrigin +      loop.getPosition ().x + loop.getRadius ();
            float loopBottom = yOrigin + -1 * loop.getPosition ().y + loop.getRadius ();
            myCanvas.drawArc(loopLeft, loopTop, loopRight, loopBottom, -90 + loop.getStartAngle(), loop.getAngleSpan(), false, paint);

            // Draw arrowhead on loop
            myCanvas.save();
//            myCanvas.translate(perspective.getPosition().x, perspective.getPosition().y);
            myCanvas.rotate(-1 * (360 - (loop.getStartAngle() + loop.getAngleSpan())));
            myCanvas.translate(0, -1 * loop.getRadius());

            // Set the arrowhead's style
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.LTGRAY);

            // Draw the arrowhead
            myCanvas.drawLine(-20, -20, 0, 0, paint);
            myCanvas.drawLine(-20, 20, 0, 0, paint);

            myCanvas.restore();

            // TODO: Draw the behaviors for each loop

            /* Draw conditions for behaviors */

            myCanvas.save ();

            float previousBehaviorAngle = -90 + loop.getStartAngle (); // Initialize behavior condition to be the start of the loop.
            for (BehaviorPlaceholder behaviorPlaceholder : loop.getBehaviors()) {

                // TODO: Get condition type and render the condition according to its type (e.g., for "switch" type, draw an arrowhead).

                // Draw the behavior conditions
                float behaviorAngle = (float) loop.getAngle (behaviorPlaceholder.getPosition()) + (2 * loop.getStartAngle ());
//                Log.v("Clay", "behaviorAngle = " + behaviorAngle);
//                Log.v("Clay", "loop.getStartAngle() = " + loop.getStartAngle());

                if (behaviorPlaceholder.hasCondition()) { // TODO: Move this to out to a larger scope...
                    if (behaviorPlaceholder.getCondition().getType() == BehaviorCondition.Type.NONE) {

                        // Set the behavior condition's style
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(2);
                        paint.setColor(Color.BLACK);

                        // Draw the condition
                        myCanvas.drawArc(loopLeft, loopTop, loopRight, loopBottom, (float) previousBehaviorAngle, (float) behaviorAngle - previousBehaviorAngle, false, paint);

                    } else if (behaviorPlaceholder.getCondition().getType() == BehaviorCondition.Type.SWITCH) {

                        float spacingBeforeArc = 20.0f;
                        float spacingAfterArc = 20.0f;

                        // Set the behavior condition's style
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(2);
                        paint.setColor(Color.CYAN);

                        // Draw the condition
                        myCanvas.drawArc(loopLeft, loopTop, loopRight, loopBottom, ((float) previousBehaviorAngle + spacingBeforeArc), (float) behaviorAngle - previousBehaviorAngle - (spacingBeforeArc + spacingAfterArc), false, paint);

                        // Draw arrowhead on loop
                        myCanvas.save();
                        myCanvas.rotate(((float) behaviorAngle + 90.0f - spacingAfterArc));
                        myCanvas.translate(0, -1 * loop.getRadius());

                        // Set the arrowhead's style
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(2);
                        paint.setColor(Color.CYAN);

                        // Draw the arrowhead
                        myCanvas.drawLine(-20, -20, 0, 0, paint);
                        myCanvas.drawLine(-20, 20, 0, 0, paint);

                        myCanvas.restore();

                    } else if (behaviorPlaceholder.getCondition().getType() == BehaviorCondition.Type.THRESHOLD) {

                        // Set the behavior condition's style
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(2);
                        paint.setColor(Color.GREEN);

                        // Draw the condition
                        myCanvas.drawArc(loopLeft, loopTop, loopRight, loopBottom, (float) previousBehaviorAngle, (float) behaviorAngle - previousBehaviorAngle, false, paint);

                    } else if (behaviorPlaceholder.getCondition().getType() == BehaviorCondition.Type.GESTURE) {

                        // Set the behavior condition's style
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(2);
                        paint.setColor(Color.MAGENTA);

                        // Draw the condition
                        myCanvas.drawArc(loopLeft, loopTop, loopRight, loopBottom, (float) previousBehaviorAngle, (float) behaviorAngle - previousBehaviorAngle, false, paint);

                    } else if (behaviorPlaceholder.getCondition().getType() == BehaviorCondition.Type.MESSAGE) {

                        // Set the behavior condition's style
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(2);
                        paint.setColor(Color.YELLOW);
                        paint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));

                        // Draw the condition
                        myCanvas.drawArc(loopLeft, loopTop, loopRight, loopBottom, (float) previousBehaviorAngle, (float) behaviorAngle - previousBehaviorAngle, false, paint);

                        paint.setPathEffect(new PathEffect()); // HACK: This undoes the dash effect! Better way would be to give individual control to drawn elements over their dash stroke (or otherwise).

                    }
                }

                // Store the current behavior's angle for calculating the geometry for the subsequent behavior's condition.
                previousBehaviorAngle = behaviorAngle;

            }

            myCanvas.restore ();


            // TODO: Draw the behaviors ON the loop.
            myCanvas.save();

            for (BehaviorPlaceholder behaviorPlaceholder : loop.getBehaviors()) {
//            behaviorStates = behaviorStates + " " + behaviorPlaceholder.state.toString();

                // Set style for behaviorPlaceholder node interior
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setStrokeWidth(2);
                paint.setColor(Color.WHITE);

                // Draw behaviorPlaceholder node interior
                myCanvas.drawCircle(behaviorPlaceholder.getPosition().x, behaviorPlaceholder.getPosition().y, behaviorPlaceholder.getRadius(), paint);

                // Set style for behaviorPlaceholder node border
                paint.setStyle (Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                paint.setColor(Color.BLACK);

                // Draw behaviorPlaceholder node border
                myCanvas.drawCircle(behaviorPlaceholder.getPosition().x, behaviorPlaceholder.getPosition().y, behaviorPlaceholder.getRadius(), paint);

                // Draw behavior's label
                //Typeface plain = Typeface.createFromAsset(assetManager, pathToFont);
//            Typeface plain = Typeface.createFromAsset(getContext().getAssets(), "fonts/comic.TTF");
//            Typeface bold = Typeface.create(plain, Typeface.DEFAULT_BOLD);
//            paint.setTypeface(bold);

                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(0);
                paint.setColor(Color.BLACK);

                String name = "None";
                if (behaviorPlaceholder.hasBehavior ()) {
                    name = behaviorPlaceholder.getBehavior().getTitle();
                }
                Rect textBounds = new Rect();
                paint.getTextBounds(name, 0, name.length(), textBounds);
                paint.setTextSize(35);
                myCanvas.drawText(name, behaviorPlaceholder.getPosition().x - textBounds.exactCenterX(), behaviorPlaceholder.getPosition().y - textBounds.exactCenterY(), paint);

                /* Draw snapping path to nearest loop. */

                // Set the nearest loop (the one in which this behavior is contained) and snap to that one (ongoing).
                Loop nearestLoop = loop;

                // Draw snapping path to nearest loop
                if (behaviorPlaceholder.getDistanceToLoop (nearestLoop) < 250) {
                    Point nearestPoint = behaviorPlaceholder.getNearestPoint (nearestLoop);
                    myCanvas.drawLine(behaviorPlaceholder.getPosition().x, behaviorPlaceholder.getPosition().y, nearestPoint.x, nearestPoint.y, paint);
                }
            }

            myCanvas.restore();



            // Draw the perspectives of the loop

            myCanvas.save();

            if (this.perspective.hasPerspectives(loop)) {

                // TODO: Support multiple perspectives per "loop" (loop concept/placeholder)

                for (LoopPerspective loopPerspective : this.perspective.getPerspectives(loop)) {

//                LoopPerspective loopPerspective = this.perspective.getPerspective(loop);

                    if (loopPerspective.loopCutPoint != null && loopPerspective.loopCutSpanPoint != null) {

                        int radiusExtension = 100;
                        int innerLoopRadius = 40; // TODO: Change this dynamically, based on the angular sweep size.

                        double cutStartAngle = loop.getStartAngle() + loop.getAngle(loopPerspective.loopCutPoint);
                        Point cutStartPoint = loop.getPoint(cutStartAngle, loop.getRadius() + radiusExtension);
                        double cutStopAngle = loop.getStartAngle() + loop.getAngle(loopPerspective.loopCutPoint) + loopPerspective.loopCutSpan;
                        Point cutStopPoint = loop.getPoint(cutStopAngle, loop.getRadius() + radiusExtension);

                        // Draw the filled arc highlighting the perspective's area

                        paint.setStyle(Paint.Style.FILL);
                        paint.setStrokeWidth(2);
                        paint.setColor(Color.WHITE);

                        myCanvas.drawArc(loopLeft - radiusExtension, loopTop - radiusExtension, loopRight + radiusExtension, loopBottom + radiusExtension, (float) cutStartAngle + loop.getStartAngle(), (float) cutStopAngle - (float) cutStartAngle, true, paint);

                        // Draw the loop in the cut

                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(2);
                        paint.setColor(Color.BLACK);

                        myCanvas.drawArc(loopLeft - innerLoopRadius, loopTop - innerLoopRadius, loopRight + innerLoopRadius, loopBottom + innerLoopRadius, (float) cutStartAngle + loop.getStartAngle(), (float) cutStopAngle - (float) cutStartAngle, false, paint);

                        // Draw the line indicating the start of the cut.

                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(2);
                        paint.setColor(Color.parseColor("#008080"));

                        myCanvas.drawLine(loop.getPosition().x, loop.getPosition().y, cutStartPoint.x, cutStartPoint.y, paint);

                        // Draw the line indicating the end of the cut.

                        if (loopPerspective.loopCutSpan < 0) {
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeWidth(2);
                            paint.setColor(Color.BLUE);
                        } else {
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeWidth(2);
                            paint.setColor(Color.GREEN);
                        }

                        myCanvas.drawLine(loop.getPosition().x, loop.getPosition().y, cutStopPoint.x, cutStopPoint.y, paint);

                    }
                }

            }

            myCanvas.restore();

            // Draw the candidate perspective(s) of the loop (if any)

            myCanvas.save();

            if (this.perspective.hasCandidatePerspective(loop)) {

                LoopPerspective candidateLoopPerspective = this.perspective.getCandidatePerspective(loop);

                if (candidateLoopPerspective.loopCutPoint != null && candidateLoopPerspective.loopCutSpanPoint != null) {

                    int radiusExtension = 100;
                    int innerLoopRadius = 40; // TODO: Change this dynamically, based on the angular sweep size.

                    double cutStartAngle = loop.getStartAngle() + loop.getAngle(candidateLoopPerspective.loopCutPoint);
                    Point cutStartPoint = loop.getPoint(cutStartAngle, loop.getRadius() + radiusExtension);
                    double cutStopAngle = loop.getStartAngle() + loop.getAngle(candidateLoopPerspective.loopCutPoint) + candidateLoopPerspective.loopCutSpan;
                    Point cutStopPoint = loop.getPoint(cutStopAngle, loop.getRadius() + radiusExtension);

                    // Draw the filled arc highlighting the perspective's area

                    paint.setStyle(Paint.Style.FILL);
                    paint.setStrokeWidth(2);
                    paint.setColor(Color.WHITE);

                    myCanvas.drawArc(loopLeft - radiusExtension, loopTop - radiusExtension, loopRight + radiusExtension, loopBottom + radiusExtension, (float) cutStartAngle + loop.getStartAngle(), (float) cutStopAngle - (float) cutStartAngle, true, paint);

                    // Draw the loop in the cut

                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(2);
                    paint.setColor(Color.BLACK);

                    myCanvas.drawArc(loopLeft - innerLoopRadius, loopTop - innerLoopRadius, loopRight + innerLoopRadius, loopBottom + innerLoopRadius, (float) cutStartAngle + loop.getStartAngle(), (float) cutStopAngle - (float) cutStartAngle, false, paint);

                    // Draw the line indicating the start of the cut.

                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(2);
                    paint.setColor(Color.parseColor("#008080"));

                    myCanvas.drawLine(loop.getPosition().x, loop.getPosition().y, cutStartPoint.x, cutStartPoint.y, paint);

                    // Draw the line indicating the end of the cut.

                    if (candidateLoopPerspective.loopCutSpan < 0) {
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(2);
                        paint.setColor(Color.BLUE);
                    } else {
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(2);
                        paint.setColor(Color.GREEN);
                    }

                    myCanvas.drawLine(loop.getPosition().x, loop.getPosition().y, cutStopPoint.x, cutStopPoint.y, paint);

                }
            }

//            if (this.perspective.loopCutPoint != null && this.perspective.loopCutSpanPoint != null) {
//
//                int radiusExtension = 100;
//                int innerLoopRadius = 40; // TODO: Change this dynamically, based on the angular sweep size.
//
//                double cutStartAngle = loop.getStartAngle() + loop.getAngle (this.perspective.loopCutPoint);
//                Point cutStartPoint = loop.getPoint(cutStartAngle, loop.getRadius() + radiusExtension);
//                double cutStopAngle = loop.getStartAngle() + loop.getAngle (this.perspective.loopCutPoint) + this.perspective.loopCutSpan;
//                Point cutStopPoint = loop.getPoint(cutStopAngle, loop.getRadius() + radiusExtension);
//
//                // Draw the filled arc highlighting the perspective's area
//
//                paint.setStyle(Paint.Style.FILL);
//                paint.setStrokeWidth(2);
//                paint.setColor(Color.WHITE);
//
//                myCanvas.drawArc(loopLeft - radiusExtension, loopTop - radiusExtension, loopRight + radiusExtension, loopBottom + radiusExtension, (float) cutStartAngle + loop.getStartAngle(), (float) cutStopAngle - (float) cutStartAngle, true, paint);
//
//                // Draw the loop in the cut
//
//                paint.setStyle(Paint.Style.STROKE);
//                paint.setStrokeWidth(2);
//                paint.setColor(Color.BLACK);
//
//                myCanvas.drawArc(loopLeft - innerLoopRadius, loopTop - innerLoopRadius, loopRight + innerLoopRadius, loopBottom + innerLoopRadius, (float) cutStartAngle + loop.getStartAngle(), (float) cutStopAngle - (float) cutStartAngle, false, paint);
//
//                // Draw the line indicating the start of the cut.
//
//                paint.setStyle(Paint.Style.STROKE);
//                paint.setStrokeWidth(2);
//                paint.setColor(Color.parseColor("#008080"));
//
//                myCanvas.drawLine (loop.getPosition().x, loop.getPosition().y, cutStartPoint.x, cutStartPoint.y, paint);
//
//                // Draw the line indicating the end of the cut.
//
//                if (this.perspective.loopCutSpan < 0) {
//                    paint.setStyle(Paint.Style.STROKE);
//                    paint.setStrokeWidth(2);
//                    paint.setColor(Color.BLUE);
//                } else {
//                    paint.setStyle(Paint.Style.STROKE);
//                    paint.setStrokeWidth(2);
//                    paint.setColor(Color.GREEN);
//                }
//
//                myCanvas.drawLine(loop.getPosition().x, loop.getPosition().y, cutStopPoint.x, cutStopPoint.y, paint);
//
//            }

            myCanvas.restore();

            myCanvas.restore();
        }

        // Draw behaviors that represent Clay's current behavior.
        // TODO: Draw the behaviors here that are NOT on a loop. Those are drawn above.
        String behaviorStates = "";
        //for (BehaviorPlaceholder behaviorPlaceholder : this.substrate.getBehaviors()) {
        for (BehaviorPlaceholder behaviorPlaceholder : this.substrate.getBehaviors()) {
//            behaviorStates = behaviorStates + " " + behaviorPlaceholder.state.toString();

            // Only draw loops that are NOT on a loop!
            if (behaviorPlaceholder.hasLoop()) {
                continue;
            }

            // Set style for behaviorPlaceholder node interior
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.WHITE);

            // Draw behaviorPlaceholder node interior
            myCanvas.drawCircle(behaviorPlaceholder.getPosition().x, behaviorPlaceholder.getPosition().y, behaviorPlaceholder.getRadius(), paint);

            // Set style for behaviorPlaceholder node border
            paint.setStyle (Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);

            // Draw behaviorPlaceholder node border
            myCanvas.drawCircle(behaviorPlaceholder.getPosition().x, behaviorPlaceholder.getPosition().y, behaviorPlaceholder.getRadius(), paint);

            // Draw behavior's label

            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(0);
            paint.setColor(Color.BLACK);

            String name = "None";
            if (behaviorPlaceholder.hasBehavior ()) {
                name = behaviorPlaceholder.getBehavior().getTitle();
            }
            Rect textBounds = new Rect();
            paint.getTextBounds(name, 0, name.length(), textBounds);
            paint.setTextSize(35);
            myCanvas.drawText(name, behaviorPlaceholder.getPosition().x - textBounds.exactCenterX(), behaviorPlaceholder.getPosition().y - textBounds.exactCenterY(), paint);

            /* Draw snapping path to nearest loop. */

            // Search for the nearest loop and snap to that one (ongoing).
            Loop nearestLoop = null;
            double nearestLoopDistance = Double.MAX_VALUE;
            for (Loop loop : this.substrate.getLoops()) {
                if (behaviorPlaceholder.getDistanceToLoop(loop) < nearestLoopDistance) {
                    nearestLoop = loop;
                }
            }

            // Draw snapping path to nearest loop
            if (behaviorPlaceholder.getDistanceToLoop (nearestLoop) < 250) {
                Point nearestPoint = behaviorPlaceholder.getNearestPoint (nearestLoop);
                myCanvas.drawLine(behaviorPlaceholder.getPosition().x, behaviorPlaceholder.getPosition().y, nearestPoint.x, nearestPoint.y, paint);
            }
        }
        Log.v("Clay", behaviorStates);

        // Paint the bitmap to the "primary" canvas
        canvas.drawBitmap (canvasBitmap, identityMatrix, null);

        // Restore the perspective translation.
        myCanvas.restore();


        // HACK: TODO: Move this to a separate "behavior execution thread"
        for (Loop loop : this.substrate.getLoops()) {
            for (BehaviorPlaceholder behaviorPlaceholder : loop.getBehaviors()) {
                Behavior behavior = behaviorPlaceholder.getBehavior();
                behavior.perform();
            }
        }

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
                    xTouches[id] = (motionEvent.getX (i) - perspective.getPosition ().x) / perspective.getScaleFactor (); // HACK: TODO: Get x position directly!
                    yTouches[id] = (motionEvent.getY (i) - perspective.getPosition ().y) / perspective.getScaleFactor (); // HACK: TODO: Get y position directly!
                }

//                // Check if touching _any_ behaviors (or loops, or canvas, or perspective). If so, keep the canvas locked, and find the action that's being touched.
//                for (BehaviorPlaceholder action : this.substrate.getBehaviors ()) {
//                    double distanceToTouch = action.getDistance ((int) xTouch[pointerId], (int) yTouch[pointerId]);
//                    if (distanceToTouch < action.getRadius () + 20) {
//                        touchedBehaviors.add (action);
//                        isPerformingBehaviorGesture = true;  // TODO: Set state of finger
////                        action.state = BehaviorPlaceholder.State.MOVING; // Set state of touched action
//                    }
//                }

                // Update the state of the touched object based on the current touch interaction state.
                if (touchAction == MotionEvent.ACTION_DOWN) {

                    touchInterface.touch(pointerId, xTouches[pointerId], yTouches[pointerId]);
                    touchInterface.classify(pointerId);

                } else if (touchAction == MotionEvent.ACTION_POINTER_DOWN) {

//                    isTouch[pointerId] = true;

                } else if (touchAction == MotionEvent.ACTION_MOVE) {

                    touchInterface.touch(pointerId, xTouches[pointerId], yTouches[pointerId]);
                    touchInterface.classify(pointerId);

                } else if (touchAction == MotionEvent.ACTION_UP) {

                    touchInterface.untouch(pointerId, xTouches[pointerId], yTouches[pointerId]);
                    touchInterface.classify(pointerId);

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