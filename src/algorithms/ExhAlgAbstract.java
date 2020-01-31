package algorithms;

import javafx.util.Pair;
import petrinet.PetriNet;
import petrinet.PetrinetNode;

import java.util.*;

public abstract class ExhAlgAbstract extends AlgorithmAbstract {

    protected int pruneat;
    protected int pruneto;
    protected Set<Mapping> finished;

    protected ExhAlgAbstract(PetriNet n1, PetriNet n2) {
        super(n1, n2);
        finished = new HashSet<>();
        wskipn = 0.1;
        wsubn = 0.8;
        wskipe = 0.2;
        pruneat = 100;
        pruneto = 10;
    }

    protected ExhAlgAbstract(PetriNet n1, PetriNet n2, int pruneat, int pruneto) {
        super(n1, n2);
        finished = new HashSet<>();
        wskipn = 0.1;
        wsubn = 0.8;
        wskipe = 0.2;
        this.pruneat = pruneat;
        this.pruneto = pruneto;
    }

    protected ExhAlgAbstract(PetriNet n1, PetriNet n2,
                             double wskipn, double wsubn, double wskipe) {
        super(n1, n2);
        finished = new HashSet<>();
        this.wskipn = wskipn;
        this.wskipe = wskipe;
        this.wsubn = wsubn;
        pruneat = 100;
        pruneto = 10;
    }

    protected ExhAlgAbstract(PetriNet n1, PetriNet n2, int pruneat, int pruneto,
                             double wskipn, double wsubn, double wskipe) {
        super(n1, n2);
        finished = new HashSet<>();
        this.pruneat = pruneat;
        this.pruneto = pruneto;
        this.wskipn = wskipn;
        this.wskipe = wskipe;
        this.wsubn = wsubn;
    }

    protected abstract Set<PNNodeTuple> step(Set<PNNodeTuple> unfinished);

    protected Set<PNNodeTuple> prune(Set<PNNodeTuple> unfinished) {
        if (unfinished.size() < pruneat)
            return unfinished;

        Set<PNNodeTuple> pruned = new HashSet<>();
        List<PNNodeTuple> sortedList = sortSetByGES(unfinished);
        for (int i = sortedList.size() - 1, j = 0; i >= 0 && j < pruneto; i--, j++)
            pruned.add(sortedList.get(i));

        return pruned;
    }

    @Override
    public void initializeMapping() {

        Set<PNNodeTuple> unfinished = initializeUnfinished();

        do {

            unfinished = prune(unfinished);
            unfinished = step(unfinished);
        } while (!unfinished.isEmpty());

        Mapping bestMapping = new Mapping();
        double bestSimilarity = getGES(bestMapping);
        for (Mapping m : finished) {
            double newSimilarity = getGES(m);
            if (newSimilarity > bestSimilarity) {
                bestSimilarity = newSimilarity;
                bestMapping = m;
            }
        }

        mapping = bestMapping;
    }

    protected Set<PetrinetNode> removeElement(Set<PetrinetNode> set, PetrinetNode element) {

        Set<PetrinetNode> newSet = new HashSet<>();
        for (PetrinetNode node : set)
            if (node != element)
                newSet.add(node);

        return newSet;
    }

    protected List<PNNodeTuple> sortSetByGES(Set<PNNodeTuple> set) {

        List<PNNodeTuple> list = new ArrayList<>(set);

        Collections.sort(list, Comparator.comparingDouble((PNNodeTuple t) ->
                GraphSimilarity.getGraphEditSimilarity(N1, N2, new Mapping(t.getMap()), wskipn, wskipe, wsubn)));

        return list;
    }

    protected Set<Pair<PetrinetNode, PetrinetNode>> addPairToSet(Set<Pair<PetrinetNode, PetrinetNode>> set,
                                                                 Pair<PetrinetNode, PetrinetNode> pair) {

        Set<Pair<PetrinetNode, PetrinetNode>> newSet = new HashSet<>(set);
        newSet.add(pair);
        return newSet;
    }

    protected abstract Set<PNNodeTuple> initializeUnfinished();
}