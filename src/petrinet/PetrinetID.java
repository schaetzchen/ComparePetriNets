package petrinet;

import java.util.Random;

public class PetrinetID extends ObjectID {

    public PetrinetID() {
        ID = new Random().nextInt();
    }

    public PetrinetID(String string) {
        ID = string.hashCode();
    }
}