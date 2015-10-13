package computer.clay.looper;

public class MyGameThread extends Thread {

    static final long FPS = 10;

    computer.clay.looper.MyGameSurfaceView view;
//    long sleepTime;

    volatile boolean running = false;

    MyGameThread (computer.clay.looper.MyGameSurfaceView sv) {
        super ();
        this.view = sv;
//        this.sleepTime = st;
    }

    public void setRunning (boolean r) {
        running = r;
    }

    @Override
    public void run () {

        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;

        while (running) {

//            Canvas c = null;
            startTime = System.currentTimeMillis ();

//            try {
//                sleep (sleepTime);
                view.updateSurfaceView ();
//            } catch (InterruptedException e) {
//                e.printStackTrace ();
//            }

            // Sleep until the time remaining in the frame's allocated draw time (for the specified FPS) is reached.
            sleepTime = ticksPS - (System.currentTimeMillis () - startTime);
            try {
                if (sleepTime > 0) {
                    sleep(sleepTime);
                } else {
                    sleep(10);
                }
            } catch (Exception e) {
            }

        }
    }

}
