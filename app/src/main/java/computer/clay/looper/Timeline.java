package computer.clay.looper;

import android.content.Context;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Timeline {

    private static String firebaseUri = "https://clay.firebaseio.com/";

    private Firebase rootRef = null;

    private Clay clay;

    public Timeline (Clay clay) {
        this.clay = clay;

        this.enableFirebase ();
        this.startFirebase ();

        testAddUnit();
    }

    private void enableFirebase () {
        Firebase.setAndroidContext(Clay.getPlatformContext());
    }

    private void startFirebase () {
        this.rootRef = new Firebase (firebaseUri);
    }

    public Clay getClay () {
        return this.clay;
    }

    public void addEntry (String entry) {
    }

//    public String getEntry (String entry) {
//        // e.g., rootRef.child ("users/mchen/name");
//        rootRef.child (entry);
//    }

    public void testAddUnit () {
        // TODO: String unitUuid = clay.getUnits().get(0).getUuid();
        String unitUuid = UUID.randomUUID().toString();
        Firebase unitRef = rootRef.child("units").child(unitUuid);

        String behaviorUuid = UUID.randomUUID ().toString ();
        unitRef.child ("behaviorUuid").setValue  (behaviorUuid);

        Firebase unitInteractions = unitRef.child("interactions");
        unitRef.child ("behaviorUuid").setValue  (behaviorUuid);

        // Simulate interactions
        ArrayList<String> interactions = new ArrayList<>();
        interactions.add ("create loop");
        interactions.add ("create behavior");
        interactions.add ("remove behavior");
        interactions.add ("focus on behavior");
        interactions.add ("stop focus on behavior");

        unitInteractions.push().setValue("power on");
        Random random = new Random ();
        for (int i = 0; i < 10; i++) {
            int randomInteractionIndex = random.nextInt(interactions.size());
            unitInteractions.push().setValue(interactions.get(randomInteractionIndex));
        }
        unitInteractions.push().setValue ("power off");
    }

    public void testDeleteUnit (UUID uuid) {
        Firebase unitRef = rootRef.child ("units").child (uuid.toString());
        unitRef.setValue(null);
    }
}
