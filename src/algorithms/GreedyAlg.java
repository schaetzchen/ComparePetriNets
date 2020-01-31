package algorithms;

import javafx.util.Pair;
import petrinet.PetriNet;
import petrinet.PetrinetNode;
import petrinet.Place;
import petrinet.Transition;

import java.util.HashSet;
import java.util.Set;

public class GreedyAlg extends AlgorithmAbstract {

    public GreedyAlg(PetriNet n1, PetriNet n2) {
        super(n1, n2);
        wskipn = 0.1;
        wsubn = 0.9;
        wskipe = 0.4;
    }

    public GreedyAlg(PetriNet n1, PetriNet n2, double wskipn, double wsubn, double wskipe) {
        super(n1, n2);
        this.wskipn = wskipn;
        this.wsubn = wsubn;
        this.wskipe = wskipe;
    }

    @Override
    public void initializeMapping() {

        Mapping M = new Mapping();
        Set<Pair<PetrinetNode, PetrinetNode>> openpairs = new HashSet<>();

        for (Place p1 : N1.getPlaces())
            for (Place p2 : N2.getPlaces())
                openpairs.add(new Pair<>(p1, p2));

        for (Transition t1 : N1.getTransitions())
            for (Transition t2 : N2.getTransitions())
                openpairs.add(new Pair<>(t1, t2));

        if (openpairs.size() == 0)
            mapping = M;

        else {
            Pair<PetrinetNode, PetrinetNode> bestPair;
            do {
                bestPair = null;
                double bestSimilarity = getGES(M);

                for (Pair<PetrinetNode, PetrinetNode> p : openpairs) {

                    Mapping newMapping = new Mapping(M.getMapping());
                    newMapping.add(p);

                    double newSimilarity = getGES(newMapping);
                    if (newSimilarity > bestSimilarity) {
                        bestPair = p;
                        bestSimilarity = newSimilarity;
                    }
                }

                if (bestPair != null) {
                    M.add(bestPair);
                    openpairs = removeElementsFromSet(bestPair.getKey(), bestPair.getValue(), openpairs);
                }

            } while (bestPair != null);
        }

        this.mapping = M;
    }

    private Set<Pair<PetrinetNode, PetrinetNode>> removeElementsFromSet(PetrinetNode n1, PetrinetNode n2,
                                                                        Set<Pair<PetrinetNode, PetrinetNode>> set) {
        Set<Pair<PetrinetNode, PetrinetNode>> newSet = new HashSet<>();

        for (Pair<PetrinetNode, PetrinetNode> p : set)
            if (!p.getKey().equals(n1) && !p.getKey().equals(n2) &&
                    !p.getValue().equals(n1) && !p.getValue().equals(n2))
                newSet.add(p);

        return newSet;
    }
}