package computer.clay.looper;

import java.util.ArrayList;

public class Substrate {

    ArrayList<Loop> loops = new ArrayList<Loop>();
    ArrayList<BehaviorPlaceholder> behaviors = new ArrayList<BehaviorPlaceholder>();

    // TODO: Include Operator/People/Agents/Actors/Robots/Intelligence

    public Substrate() {
        super();
    }

    public void addLoop (Loop loop) {
        this.loops.add(loop);
    }

    public ArrayList<Loop> getLoops () {
        return this.loops;
    }

    public void addBehavior (BehaviorPlaceholder behaviorPlaceholder) {
        this.behaviors.add (behaviorPlaceholder);
    }

    public ArrayList<BehaviorPlaceholder> getBehaviors() {
        return this.behaviors;
    }

    // TODO: getNearestLoop (int x, int y)
}
