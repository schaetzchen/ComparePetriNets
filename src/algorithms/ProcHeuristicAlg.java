package algorithms;

import javafx.util.Pair;
import petrinet.*;

import java.util.HashSet;
import java.util.Set;

public class ProcHeuristicAlg extends ExhAlgAbstract {

    public ProcHeuristicAlg(PetriNet n1, PetriNet n2) {
        super(n1, n2);
    }

    public ProcHeuristicAlg(PetriNet n1, PetriNet n2, int pruneat, int pruneto) {
        super(n1, n2, pruneat, pruneto);
    }

    public ProcHeuristicAlg(PetriNet n1, PetriNet n2,
                            double wskipn, double wsubn, double wskipe) {
        super(n1, n2, wskipn, wsubn, wskipe);
    }

    public ProcHeuristicAlg(PetriNet n1, PetriNet n2, int pruneat, int pruneto,
                            double wskipn, double wsubn, double wskipe) {
        super(n1, n2, pruneat, pruneto, wskipn, wsubn, wskipe);
    }

    @Override
    protected Set<PNNodeTuple> step(Set<PNNodeTuple> unfinished) {

        Set<PNNodeTuple> newUnfinished = new HashSet<>();

        for (PNNodeTuple tuple : unfinished) {

            if (tuple.getCurr1().isEmpty() || tuple.getCurr2().isEmpty())
                finished.add(new Mapping(tuple.getMap()));

            else {

                for (PetrinetNode c1 : tuple.getCurr1()) {

                    Set<PetrinetNode> curr1WithPostset = new HashSet<>(tuple.getCurr1());
                    curr1WithPostset.addAll(getPostset(c1, N1.getArcs()));
                    Set<PetrinetNode> free1WithoutC1 = removeElement(tuple.getFree1(), c1);
                    Set<PetrinetNode> intersection1 =
                            getSetsIntersection(curr1WithPostset, free1WithoutC1);

                    newUnfinished.add(new PNNodeTuple(tuple.getMap(), free1WithoutC1, tuple.getFree2(),
                            intersection1, tuple.getCurr2()));

                    for (PetrinetNode c2 : tuple.getCurr2()) {

                        Set<PetrinetNode> curr2WithPostset = new HashSet<>(tuple.getCurr2());
                        curr2WithPostset.addAll(getPostset(c2, N2.getArcs()));
                        Set<PetrinetNode> free2WithoutC2 = removeElement(tuple.getFree2(), c2);
                        Set<PetrinetNode> intersection2 =
                                getSetsIntersection(curr2WithPostset, free2WithoutC2);

                        if ((c1 instanceof Place && c2 instanceof Place) ||
                                (c1 instanceof Transition && c2 instanceof Transition))
                            newUnfinished.add(new PNNodeTuple(addPairToSet(tuple.getMap(), new Pair<>(c1, c2)),
                                    free1WithoutC1, free2WithoutC2, intersection1, intersection2));
                    }
                }

                for (PetrinetNode c2 : tuple.getCurr2()) {

                    Set<PetrinetNode> curr2WithPostset = new HashSet<>(tuple.getCurr2());
                    curr2WithPostset.addAll(getPostset(c2, N2.getArcs()));
                    Set<PetrinetNode> free2WithoutC2 = removeElement(tuple.getFree2(), c2);
                    Set<PetrinetNode> intersection2 =
                            getSetsIntersection(curr2WithPostset, free2WithoutC2);

                    newUnfinished.add(new PNNodeTuple(tuple.getMap(), tuple.getFree1(), free2WithoutC2,
                            tuple.getCurr1(), intersection2));
                }
            }
        }

        return newUnfinished;
    }

    @Override
    protected Set<PNNodeTuple> initializeUnfinished() {

        Set<PNNodeTuple> unfinished = new HashSet<>();
        unfinished.add(new PNNodeTuple(new HashSet<>(), N1.getNodes(), N2.getNodes(),
                getNodesWithEmptyPreset(N1.getNodes(), N1.getArcs()), getNodesWithEmptyPreset(N2.getNodes(), N2.getArcs())));

        return unfinished;
    }

    private Set<PetrinetNode> getNodesWithEmptyPreset(Set<PetrinetNode> nodes, Set<Arc> arcs) {

        Set<PetrinetNode> nodesWithEmptyPreset = new HashSet<>();

        for (PetrinetNode node : nodes) {
            boolean flag = true;
            for (Arc a : arcs)
                if (a.getTarget().equals(node)) {
                    flag = false;
                    break;
                }
            if (flag)
                nodesWithEmptyPreset.add(node);
        }

        return nodesWithEmptyPreset;
    }

    private Set<PetrinetNode> getPostset(PetrinetNode node, Set<Arc> arcs) {

        Set<PetrinetNode> postset = new HashSet<>();

        for (Arc arc : arcs)
            if (arc.getSource().equals(node))
                postset.add(arc.getTarget());

        return postset;
    }

    private Set<PetrinetNode> getSetsIntersection(Set<PetrinetNode> s1, Set<PetrinetNode> s2) {
        Set<PetrinetNode> intersection = new HashSet<>(s1);
        intersection.retainAll(s2);
        return intersection;
    }
}