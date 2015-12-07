package computer.clay.looper;

public class Clay {

    // Clay
    // - Placeholder
    //   - LoopConstruct
    //   - BehaviorConstruct

//    private ArrayList<Unit> units = new ArrayList<Unit> (); // TODO: Move this to the System class!

    private BehaviorRepository behaviorRepository = new BehaviorRepository (this);

    private System system = new System (this); // i.e., like the "model"
    private Perspective perspective = new Perspective (system); // ie., like the "view"
    private Person person = new Person (this); // i.e., like the "controller"

    Clay () {

        // HACK: Uses fake test units to prototype Looper!
//        this.setupTestUnits(); // TODO: Replace this with the actual discovered units!
    }

    public System getSystem () {
        return this.system;
    }

    public Perspective getPerspective () {
        return this.perspective;
    }

    public Person getPerson () {
        return this.person;
    }

//    private void setupTestUnits () {
//        Unit unit = new Unit (UUID.randomUUID());
//        this.units.add(unit);
//
//        // TODO: Make a "core loop" for each unit that is the "main" loop of the unit!
//    }

    // TODO: discoverUnits() : Discover devices via UDP (maybe TCP).
    // TODO: discoverNetwork() : Discover and model the communications network between the units.
    // TODO: requestModuleBehaviors(module) : Request the available behaviors that Clay modules can do. These are the basic behaviors.
    // TODO: requestModuleBehavior(module) : Request the currently programmed behavior of a specific Clay module.

    // TODO: Implement incoming and outgoing message queues for communicating with Clay modules.
}
