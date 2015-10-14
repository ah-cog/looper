package computer.clay.looper;

import java.util.ArrayList;

public class Substrate {

    ArrayList<Action> actions = new ArrayList<Action>();
    ArrayList<Loop> loops = new ArrayList<Loop>();

    // TODO: Include Operator/People/Agents/Actors/Robots/Intelligence

    public Substrate() {
        super();
    }

    public void addAction (Action action) {
        this.actions.add(action);
    }

    public ArrayList<Action> getActions () {
        return this.actions;
    }

    public void addLoop (Loop loop) {
        this.loops.add(loop);
    }

    public ArrayList<Loop> getLoops () {
        return this.loops;
    }

    // TODO: getNearestLoop (int x, int y)
}
