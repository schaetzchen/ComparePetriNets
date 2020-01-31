package petrinet;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Random;

public class PetrinetNode {

    private String pnmlId;
    private String label;
    private PetrinetID petrinetID;
    private NodeID ID;

    protected PetrinetNode(String label, PetriNet net) {
        this.label = label;
        if (net != null)
            this.petrinetID = net.getID();
        else
            this.petrinetID = new PetrinetID(generateRandomString());
        this.pnmlId = generateRandomString();
        this.ID = new NodeID(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || obj.getClass() != this.getClass())
            return false;

        PetrinetNode node = (PetrinetNode) obj;

        return this.ID.ID == node.ID.ID;
    }

    @Override
    public int hashCode() {
        return ID.ID;
    }

    protected void fixID() {
        ID.ID = ID.ID * (-1);
    }

    public String getPnmlId() {
        return this.pnmlId;
    }

    public String getLabel() {
        return this.label;
    }

    public void setPnmlId(String pnmlId) {
        this.pnmlId = pnmlId;
    }

    public PetrinetID getNetID() {
        return this.petrinetID;
    }

    public NodeID getID() {
        return this.ID;
    }

    private String generateRandomString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890-";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * chars.length());
            salt.append(chars.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
}