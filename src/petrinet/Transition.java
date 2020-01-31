package petrinet;

public class Transition extends PetrinetNode {
    public Transition(String label, PetriNet pn) {

        super(label, pn);
        fixID();
    }
}