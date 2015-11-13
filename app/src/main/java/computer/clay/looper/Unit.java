package computer.clay.looper;

import java.util.UUID;

/**
 * Created by mrgubbels on 11/12/15.
 */
public class Unit {

    private UUID uuid; // The unit's static, unchanging, UUID
    private String address; // The unit's IP address

    // TODO: Cache/model the unit's state and behavior

    Unit (UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public void setAddress (String address) {
        this.address = address;
    }

    public String getAddress () {
        return this.address;
    }

    // TODO: Simulate the unit's state change based on its behavior
}
