package computer.clay.looper;

import java.util.ArrayList;
import java.util.UUID;

public class Clay {

    private ArrayList<Unit> units = new ArrayList<Unit> ();

    private BehaviorRepository behaviorRepository = new BehaviorRepository (this);

    private Substrate substrate = new Substrate();

    Clay () {

        // HACK: Uses fake test units to prototype Looper!
        this.setupTestUnits(); // TODO: Replace this with the actual discovered units!
    }

    private void setupTestUnits () {
        Unit unit = new Unit (UUID.randomUUID());
        this.units.add(unit);

        // TODO: Make a "core loop" for each unit that is the "main" loop of the unit!
    }

    // TODO: discoverUnits() : Discover devices via UDP (maybe TCP).
    // TODO: discoverNetwork() : Discover and model the communications network between the units.
    // TODO: requestModuleBehaviors(module) : Request the available behaviors that Clay modules can do. These are the basic behaviors.
    // TODO: requestModuleBehavior(module) : Request the currently programmed behavior of a specific Clay module.

    // TODO: Implement incoming and outgoing message queues for communicating with Clay modules.
}
