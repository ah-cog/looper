package computer.clay.looper;

public class BehaviorCondition { // TODO: Consider renaming this to BehaviorPrecursor

    private BehaviorPlaceholder behaviorPlaceholder;

    public enum Type {
        NONE,
        SWITCH, // i.e., boolean
        THRESHOLD, // i.e., sensor
        GESTURE, // i.e., when a specified gesture is detected
        MESSAGE // i.e., when the specified message is received
    };

    private Type type = Type.NONE;

    BehaviorCondition (BehaviorPlaceholder behaviorPlaceholder, Type type) {
        this.behaviorPlaceholder = behaviorPlaceholder;
        this.type = type;
    }

    public void setBehaviorPlaceholder (BehaviorPlaceholder behaviorPlaceholder) {
        this.behaviorPlaceholder = behaviorPlaceholder;
    }

    public BehaviorPlaceholder getBehaviorPlaceholder () {
        return this.behaviorPlaceholder;
    }

    public Type getType () {
        return this.type;
    }
}
