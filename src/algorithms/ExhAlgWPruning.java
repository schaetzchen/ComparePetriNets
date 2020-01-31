package algorithms;

import javafx.util.Pair;
import petrinet.PetriNet;
import petrinet.PetrinetNode;
import petrinet.Place;
import petrinet.Transition;

import java.util.*;

public class ExhAlgWPruning extends ExhAlgAbstract {

    public ExhAlgWPruning(PetriNet n1, PetriNet n2) {
        super(n1, n2);
    }

    public ExhAlgWPruning(PetriNet n1, PetriNet n2, int pruneat, int pruneto) {
        super(n1, n2, pruneat, pruneto);
    }

    public ExhAlgWPruning(PetriNet n1, PetriNet n2,
                          double wskipn, double wsubn, double wskipe) {
        super(n1, n2, wskipn, wsubn, wskipe);
    }

    public ExhAlgWPruning(PetriNet n1, PetriNet n2, int pruneat, int pruneto,
                          double wskipn, double wsubn, double wskipe) {
        super(n1, n2, pruneat, pruneto, wskipn, wsubn, wskipe);
    }

    @Override
    protected Set<PNNodeTuple> step(Set<PNNodeTuple> unfinished) {

        Set<PNNodeTuple> newUnfinished = new HashSet<>();

        for (PNNodeTuple tuple : unfinished) {

            if (tuple.getFree1().isEmpty() || tuple.getFree2().isEmpty())
                finished.add(new Mapping(tuple.getMap()));

            else {

                for (PetrinetNode n1 : tuple.getFree1()) {

                    Set<Pair<PetrinetNode, PetrinetNode>> newMapping = new HashSet<>(tuple.getMap());
                    Set<PetrinetNode> newFree2 = new HashSet<>(tuple.getFree2());
                    newUnfinished.add(new PNNodeTuple(newMapping, removeElement(tuple.getFree1(), n1), newFree2));

                    for (PetrinetNode n2 : tuple.getFree2()) {
                        if ((n1 instanceof Place && n2 instanceof Place) ||
                                (n1 instanceof Transition && n2 instanceof Transition))
                            newUnfinished.add(new PNNodeTuple(addPairToSet(tuple.getMap(), new Pair<>(n1, n2)),
                                    removeElement(tuple.getFree1(), n1), removeElement(tuple.getFree2(), n2)));
                    }
                }

                for (PetrinetNode n2 : tuple.getFree2()) {

                    Set<Pair<PetrinetNode, PetrinetNode>> newMapping = new HashSet<>(tuple.getMap());
                    Set<PetrinetNode> newFree1 = new HashSet<>(tuple.getFree1());
                    newUnfinished.add(new PNNodeTuple(newMapping, newFree1, removeElement(tuple.getFree2(), n2)));
                }
            }
        }

        return newUnfinished;
    }

    @Override
    protected Set<PNNodeTuple> initializeUnfinished() {

        Set<PNNodeTuple> unfinished = new HashSet<>();
        unfinished.add(new PNNodeTuple(new HashSet<>(), N1.getNodes(), N2.getNodes()));

        return unfinished;
    }
}