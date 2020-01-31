package petrinet;

import java.util.HashSet;
import java.util.Set;

public class PetriNet {

    private String name;
    private PetrinetID ID;
    private Set<Place> places;
    private Set<Transition> transitions;
    private Set<Arc> arcs;

    public PetriNet(String name) {
        this.name = name;
        places = new HashSet<>();
        transitions = new HashSet<>();
        arcs = new HashSet<>();
        ID = new PetrinetID();
    }

    public Transition addTransition(Transition t) {
        transitions.add(t);
        return t;
    }

    public Transition addTransition(String name) {
        Transition t = new Transition(name, this);
        transitions.add(t);
        return t;
    }

    public Place addPlace(Place p) {
        places.add(p);
        return p;
    }

    public Place addPlace(String name) {
        Place p = new Place(name, this);
        places.add(p);
        return p;
    }

    public Set<Place> getPlaces() {
        return places;
    }

    public Set<Transition> getTransitions() {
        return transitions;
    }

    public Set<Arc> getArcs() {
        return arcs;
    }

    public Arc addArc(PetrinetNode s, PetrinetNode t, int w) {
        Arc a = new Arc(s, t, w);
        arcs.add(a);
        return a;
    }

    public Arc addResetArc(PetrinetNode s, PetrinetNode t, String smt) {
        Arc a = new Arc(s, t, 1);
        arcs.add(a);
        return a;
    }

    public Arc addInhibitorArc(PetrinetNode s, PetrinetNode t, String smt) {
        Arc a = new Arc(s, t, 1);
        arcs.add(a);
        return a;
    }

    public PetrinetID getID() {
        return this.ID;
    }

    public Set<PetrinetNode> getNodes() {
        Set<PetrinetNode> nodes = new HashSet<>();
        nodes.addAll(places);
        nodes.addAll(transitions);
        return nodes;
    }
}