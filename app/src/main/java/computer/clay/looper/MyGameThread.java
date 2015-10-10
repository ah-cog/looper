package computer.clay.looper;

public class MyGameThread extends Thread {

    volatile boolean running = false;

    computer.clay.looper.MyGameSurfaceView parent;
    long sleepTime;

    MyGameThread(computer.clay.looper.MyGameSurfaceView sv, long st){
        super();
        parent = sv;
        sleepTime = st;
    }

    public void setRunning (boolean r) {
        running = r;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while(running){

            try {
                sleep(sleepTime);
                parent.updateSurfaceView();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

}
