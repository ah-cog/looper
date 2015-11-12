package computer.clay.looper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
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
    private Gesture gesture = new Gesture (this.substrate, this.perspective);

    public AppSurfaceView (Context context) {
        super (context);

//        this.substrate = new Substrate ();
//        this.perspective = new Perspective (this.substrate);
//        this.gesture = new Gesture (this.substrate, this.perspective);
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
            paint.setColor (Color.BLACK);

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
            paint.setStyle (Paint.Style.STROKE);
            paint.setStrokeWidth (2);
            paint.setColor (Color.BLACK);

            // Draw the arrowhead
            myCanvas.drawLine(-20, -20, 0, 0, paint);
            myCanvas.drawLine(-20, 20, 0, 0, paint);

            myCanvas.restore();

            // Draw the sections of the loop

            myCanvas.save();

            if (this.perspective.loopCutPoint != null && this.perspective.loopCutSpanPoint != null) {

                int radiusExtension = 70;
                int innerLoopRadius = 30;

                double cutStartAngle = loop.getStartAngle() + loop.getAngle (this.perspective.loopCutPoint);
                Point cutStartPoint = loop.getPoint(cutStartAngle, loop.getRadius() + radiusExtension);
                double cutStopAngle = loop.getStartAngle() + this.perspective.loopCutStartAngle + this.perspective.loopCutSpan;
                Point cutStopPoint = loop.getPoint(cutStopAngle, loop.getRadius() + radiusExtension);

                // Draw the filled arc highlighting the perspective's area

                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(2);
                paint.setColor(Color.WHITE);

                myCanvas.drawArc(loopLeft - radiusExtension, loopTop - radiusExtension, loopRight + radiusExtension, loopBottom + radiusExtension, (float) cutStartAngle, (float) cutStopAngle - this.perspective.loopCutStartAngle, true, paint);

                // Draw the loop in the cut

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                paint.setColor(Color.BLACK);

                myCanvas.drawArc(loopLeft - innerLoopRadius, loopTop - innerLoopRadius, loopRight + innerLoopRadius, loopBottom + innerLoopRadius, (float) cutStartAngle, (float) cutStopAngle - this.perspective.loopCutStartAngle, false, paint);

                // Draw the line indicating the start of the cut.

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                paint.setColor(Color.RED);

                //myCanvas.drawLine (loop.getPosition().x, loop.getPosition().y, this.perspective.loopCutPoint.x, this.perspective.loopCutPoint.y, paint);
                myCanvas.drawLine (loop.getPosition().x, loop.getPosition().y, cutStartPoint.x, cutStartPoint.y, paint);
//                myCanvas.drawLine (loop.getPosition().x, loop.getPosition().y, cutStartPoint.x, cutStartPoint.y, paint);

                // Draw the line indicating the end of the cut.

                if (this.perspective.loopCutSpan < 0) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(2);
                    paint.setColor(Color.BLUE);
                } else {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(2);
                    paint.setColor(Color.GREEN);
                }

                myCanvas.drawLine(loop.getPosition().x, loop.getPosition().y, cutStopPoint.x, cutStopPoint.y, paint);
                // myCanvas.drawLine (loop.getPosition().x, loop.getPosition().y, this.loopCutSpanPoint.x, this.loopCutSpanPoint.y, paint);

            }

            myCanvas.restore();

            // TODO: Draw the behaviors for each loop

            myCanvas.restore();
        }

        // Draw behaviors that represent Clay's current behavior.
        // TODO: Draw the behaviors here that are NOT on a loop. Those are drawn above.
        for (BehaviorPlaceholder behaviorPlaceholder : this.substrate.getBehaviors()) {

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
            String name = "I/O";
            Rect textBounds = new Rect();
//            paint.setTextAlign(Paint.Align.CENTER);
            paint.getTextBounds(name, 0, name.length(), textBounds);
            paint.setTextSize (25);
            myCanvas.drawText (name, behaviorPlaceholder.getPosition().x - textBounds.exactCenterX(), behaviorPlaceholder.getPosition().y - textBounds.exactCenterY(), paint);

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

        // Paint the bitmap to the "primary" canvas
        canvas.drawBitmap (canvasBitmap, identityMatrix, null);

        // Restore the perspective translation.
        myCanvas.restore();

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
//                        touchingAction = true;  // TODO: Set state of finger
////                        action.state = BehaviorPlaceholder.State.MOVING; // Set state of touched action
//                    }
//                }

                // Update the state of the touched object based on the current touch interaction state.
                if (touchAction == MotionEvent.ACTION_DOWN) {

                    gesture.touch (pointerId, xTouches[pointerId], yTouches[pointerId]);
                    gesture.classify (pointerId);

                } else if (touchAction == MotionEvent.ACTION_POINTER_DOWN) {

//                    isTouch[pointerId] = true;

                } else if (touchAction == MotionEvent.ACTION_MOVE) {

                    gesture.touch (pointerId, xTouches[pointerId], yTouches[pointerId]);
                    gesture.classify (pointerId);

                } else if (touchAction == MotionEvent.ACTION_UP) {

                    gesture.untouch (pointerId, xTouches[pointerId], yTouches[pointerId]);
                    gesture.classify (pointerId);

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