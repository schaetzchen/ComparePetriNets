package petrinet;

public class NodeID extends ObjectID {

    public NodeID(PetrinetNode nd) {

        ID = nd.getPnmlId().hashCode() * nd.getNetID().ID;
    }

}
