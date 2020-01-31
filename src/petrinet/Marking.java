package petrinet;

import java.util.Collection;
import java.util.HashSet;

public class Marking {
    Collection<Place> marking;

    public Marking() {
        marking = new HashSet<>();
    }

    public Marking(Collection<Place> collection) {
        this.marking = collection;
    }

    public void add(Place p) {
        marking.add(p);
    }
}
