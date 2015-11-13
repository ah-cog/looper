package computer.clay.looper;

import java.util.ArrayList;

public class BehaviorRepository {

    private Clay clay;

    private ArrayList<String> repositoryUris = new ArrayList<String>();
    private ArrayList<Behavior> cachedBehaviors = new ArrayList<Behavior>();

    BehaviorRepository (Clay clay) {

        // Associate this behavior repository with Clay.
        this.clay = clay;

        // HACK: Set up some sample behaviors
        // TODO: Load these from a server! Or recover them from the local cache.
        this.setupTestRepository();
    }

    private void setupTestRepository () {

        Behavior lightBehavior = new Behavior("light"); // e.g., "turn on lights  3 8 9 10 12"
        this.cachedBehaviors.add(lightBehavior);
        Behavior motionBehavior = new Behavior("motion");
        this.cachedBehaviors.add(motionBehavior);
        Behavior gestureBehavior = new Behavior("gesture");
        this.cachedBehaviors.add(gestureBehavior);
        Behavior timeBehavior = new Behavior("time"); // e.g., "delay 1 second"
        this.cachedBehaviors.add(timeBehavior);
        Behavior communicationBehavior = new Behavior("communication");
        this.cachedBehaviors.add(communicationBehavior);
        Behavior controlBehavior = new Behavior("control"); // e.g., "turn on 3 9 12"
        this.cachedBehaviors.add(controlBehavior);
        Behavior soundBehavior = new Behavior("sound");
        this.cachedBehaviors.add(soundBehavior);
        Behavior speechBehavior = new Behavior("speech");
        this.cachedBehaviors.add(speechBehavior);
        Behavior serviceBehavior = new Behavior("service");
        this.cachedBehaviors.add(serviceBehavior);
    }

    public void addRepositoryUri (String repositoryUri) {
        this.repositoryUris.add (repositoryUri);
    }

    public void hasBehavior (String behaviorUri) {
        // TODO: Search current set of repositories for the specified behavior
    }

    public Behavior getBehavior (String behaviorUri) {
        // TODO: Return the behavior with the specified URI or null.
        return null;
    }
}
