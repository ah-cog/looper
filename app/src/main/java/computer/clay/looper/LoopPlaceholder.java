package computer.clay.looper;

import java.util.ArrayList;

// TODO: loopConcept/loopBody/loopSubject/loopFrame, loopPerspective, loopBehavior/loopOperation

public class LoopPlaceholder { // TODO: Possibly renamed to LoopScaffold, LoopScaffold, LoopStructure, LoopMachine, LoopEngine, LoopUnit, LoopOperator

    private Substrate substrate = null;

    private ArrayList<LoopPerspective> loopPerspectives = new ArrayList<LoopPerspective>();

    public ArrayList<LoopPerspective> getLoopPerspectives() {
        return loopPerspectives;
    }

    public void setLoopPerspectives(ArrayList<LoopPerspective> loopPerspectives) {
        this.loopPerspectives = loopPerspectives;
    }

    LoopPlaceholder(Substrate substrate) {

        this.substrate = substrate;

        // Create a default loop and perspective for the placeholder.
        Loop defaultLoop = new Loop(this.substrate);
        LoopPerspective defaultLoopPerspective = new LoopPerspective(defaultLoop);

    }

    LoopPlaceholder(Substrate substrate, Loop loop) {
        this.substrate = substrate;

        LoopPerspective loopPerspective = new LoopPerspective(loop);

        this.loopPerspectives.add(loopPerspective);
    }

    LoopPlaceholder(Substrate substrate, LoopPerspective loopPerspective, Loop loop) {

    }


}
