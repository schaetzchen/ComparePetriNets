package algorithms;

import javafx.util.Pair;
import petrinet.PetrinetNode;

import java.util.Set;

public class PNNodeTuple {

    private final Set<Pair<PetrinetNode, PetrinetNode>> first;
    private final Set<PetrinetNode> second;
    private final Set<PetrinetNode> third;
    private final Set<PetrinetNode> fourth;
    private final Set<PetrinetNode> fifth;

    public PNNodeTuple(Set<Pair<PetrinetNode, PetrinetNode>> first, Set<PetrinetNode> second, Set<PetrinetNode> third) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = null;
        this.fifth = null;
    }

    public PNNodeTuple(Set<Pair<PetrinetNode, PetrinetNode>> first, Set<PetrinetNode> second, Set<PetrinetNode> third,
                       Set<PetrinetNode> fourth, Set<PetrinetNode> fifth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
    }

    public Set<Pair<PetrinetNode, PetrinetNode>> getMap() {
        return first;
    }

    public Set<PetrinetNode> getFree1() {
        return second;
    }

    public Set<PetrinetNode> getFree2() {
        return third;
    }

    public Set<PetrinetNode> getCurr1() {
        return fourth;
    }

    public Set<PetrinetNode> getCurr2() {
        return fifth;
    }
}