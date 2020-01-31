package petrinet;

public class Arc<S extends PetrinetNode, T extends PetrinetNode> {
    private PetrinetNode source;
    private PetrinetNode target;
    private int weight;
    private String label;
    private String pnmlId;

    public Arc(S source, T target, int w) {
        this.source = source;
        this.target = target;
        this.weight = w;
        label = "";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || obj.getClass() != this.getClass())
            return false;

        Arc arc = (Arc) obj;

        return (this.source.equals(arc.source) && this.target.equals(arc.target)
                && this.weight == arc.weight && this.label.equals(arc.label));
    }

    @Override
    public int hashCode() {
        return source.getID().ID * target.getID().ID * weight;
    }

    public String getPnmlId() {
        return pnmlId;
    }

    public String getLabel() {
        return label;
    }

    public PetrinetNode getSource() {
        return source;
    }

    public PetrinetNode getTarget() {
        return target;
    }

    public void setPnmlId(String s) {
        this.pnmlId = s;
    }

    public void setLabel(String s) {
        this.label = s;
    }
}