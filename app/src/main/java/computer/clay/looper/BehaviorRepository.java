package computer.clay.looper;

import java.util.ArrayList;

public class BehaviorRepository {

    private Clay clay;

    private ArrayList<String> repositoryUris = new ArrayList<String>();
    private ArrayList<Behavior> cachedBehaviors = new ArrayList<Behavior>();

    BehaviorRepository (Clay clay) {
        this.clay = clay;
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
