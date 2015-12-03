package computer.clay.looper;

import java.util.ArrayList;

/**
 * Loop
 */
public class Loop { // TODO: Possibly rename to LoopOperation

    private System system = null;

    private ArrayList<BehaviorConstruct> behaviors = new ArrayList<BehaviorConstruct> ();

    public Loop (System system) {
        super();

        this.system = system;
    }

    public System getSystem () {
        return this.system;
    }

    public void addBehavior (BehaviorConstruct behaviorConstruct) {

        // Add behavior to the list of behaviors in the loop sequence
        if (!this.behaviors.contains(behaviorConstruct)) {
            this.behaviors.add(behaviorConstruct);
        }

        // Re-order the behaviors based on their position along the loop
//        this.reorderBehaviors();

    }

    public boolean hasBehavior (BehaviorConstruct behaviorConstruct) {
        return this.behaviors.contains (behaviorConstruct);
    }

    // TODO: Remove behavior from a sequence, not from by the specified angle. Do that in LoopConstruct.
    public void removeBehavior (BehaviorConstruct behaviorConstruct) {
        if (behaviorConstruct != null) {

            // Remove the specified behavior from the loop (if it is present)
            if (this.behaviors.contains (behaviorConstruct)) {
                this.behaviors.remove(behaviorConstruct);
            }

            // Re-order the behaviors based on their position along the loop
//            this.reorderBehaviors();
        }
    }

    // TODO: Intelligently compute adjustments to behavior position on loop and update the position, showing it clearly as automatically being updated by Clay.

    public ArrayList<BehaviorConstruct> getBehaviors() {
        return this.behaviors;
    }

    public BehaviorConstruct getAction (int index) {
        if (0 < index && index < this.behaviors.size()) {
            return this.behaviors.get(index);
        } else {
            return null;
        }
    }
}
