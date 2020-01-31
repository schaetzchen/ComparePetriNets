package algorithms;

import javafx.util.Pair;
import petrinet.PetrinetNode;

import java.util.HashSet;
import java.util.Set;

public class Mapping {
    private Set<Pair<PetrinetNode, PetrinetNode>> mapping;

    public Mapping() {
        mapping = new HashSet<>();
    }

    public Mapping(Set<Pair<PetrinetNode, PetrinetNode>> mapping) {
        this.mapping = new HashSet<>(mapping);
    }

    public void add(Pair<PetrinetNode, PetrinetNode> element) {
        mapping.add(element);
    }

    public Set<Pair<PetrinetNode, PetrinetNode>> getMapping() {
        return mapping;
    }

    public Pair<PetrinetNode, PetrinetNode> searchPairByNode(PetrinetNode key) {

        for (Pair<PetrinetNode, PetrinetNode> p : mapping)
            if (p.getKey().equals(key) || p.getValue().equals(key))
                return p;

        return null;
    }
}