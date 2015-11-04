package computer.clay.looper;

import java.util.ArrayList;

public class Substrate {

    ArrayList<Dot> activities = new ArrayList<Dot>();
    ArrayList<Loop> loops = new ArrayList<Loop>();

    // TODO: Include Operator/People/Agents/Actors/Robots/Intelligence

    public Substrate() {
        super();
    }

    public void addAction (Dot dot) {
        this.activities.add(dot);
    }

    public ArrayList<Dot> getActivities() {
        return this.activities;
    }

    public void addLoop (Loop loop) {
        this.loops.add(loop);
    }

    public ArrayList<Loop> getLoops () {
        return this.loops;
    }

    // TODO: getNearestLoop (int x, int y)
}
