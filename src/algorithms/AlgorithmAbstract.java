package algorithms;

import petrinet.PetriNet;

public abstract class AlgorithmAbstract {
    protected Mapping mapping;
    protected PetriNet N1;
    protected PetriNet N2;
    protected double wskipn;
    protected double wsubn;
    protected double wskipe;

    public AlgorithmAbstract(PetriNet n1, PetriNet n2) {
        N1 = n1;
        N2 = n2;
        mapping = new Mapping();
    }

    public abstract void initializeMapping();

    public Mapping getMapping() {
        return mapping;
    }

    public double getWskipn() {
        return wskipn;
    }

    public double getWskipe() {
        return wskipe;
    }

    public double getWsubn() {
        return wsubn;
    }

    public double getGES(Mapping m) {
        return GraphSimilarity.getGraphEditSimilarity(N1, N2, m, wskipn, wskipe, wsubn);
    }
}