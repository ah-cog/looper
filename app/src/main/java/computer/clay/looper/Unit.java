package computer.clay.looper;

import java.util.UUID;

/**
 * Created by mrgubbels on 11/12/15.
 */
public class Unit {

    private UUID uuid; // The unit's static, unchanging, UUID

    private String internetAddress; // The unit's IP address

    private String meshAddress; // The unit's IP address

    // TODO: Cache/model the unit's state and behavior

    Unit (UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public void setInternetAddress (String address) {
        this.internetAddress = address;
    }

    public String getInternetAddress () {
        return this.internetAddress;
    }

    public void setMeshAddress (String address) {
        this.meshAddress = address;
    }

    public String getMeshAddress () {
        return this.meshAddress;
    }

    // TODO: Simulate the unit's state change based on its behavior
}
