package algorithms;

import javafx.util.Pair;
import petrinet.*;

import java.util.HashSet;
import java.util.Set;

public class AStarAlg extends AlgorithmAbstract {

    // label edit cut-off
    private double ledcutoff;

    public AStarAlg(PetriNet n1, PetriNet n2) {
        super(n1, n2);
        wskipn = 0.2;
        wsubn = 0.1;
        wskipe = 0.7;
        ledcutoff = .5;
    }

    public AStarAlg(PetriNet n1, PetriNet n2, double ledcutoff) {
        super(n1, n2);
        wskipn = 0.2;
        wsubn = 0.1;
        wskipe = 0.7;
        this.ledcutoff = ledcutoff;
    }

    public AStarAlg(PetriNet n1, PetriNet n2, double wskipn, double wsubn, double wskipe) {
        super(n1, n2);
        this.wskipn = wskipn;
        this.wsubn = wsubn;
        this.wskipe = wskipe;
        ledcutoff = .5;
    }

    public AStarAlg(PetriNet n1, PetriNet n2, double wskipn, double wsubn, double wskipe, double ledcutoff) {
        super(n1, n2);
        this.wskipn = wskipn;
        this.wsubn = wsubn;
        this.wskipe = wskipe;
        this.ledcutoff = ledcutoff;
    }

    @Override
    public void initializeMapping() {

        Set<Set<Pair<PetrinetNode, PetrinetNode>>> open = createOpenSet();

        while (!open.isEmpty()) {

            Set<Pair<PetrinetNode, PetrinetNode>> map = selectMappingWithMaxGES(open);
            Set<PetrinetNode> mapDomain = getDomain(map);
            open.remove(map);

            if (mapDomain.equals(N1.getNodes())) {
                this.mapping = new Mapping(map);
                return;
            }

            Set<PetrinetNode> mapCodomain = getCodomain(map);
            PetrinetNode n1 = selectNodeNotBelongingToSet(N1.getNodes(), mapDomain);
            for (PetrinetNode n2 : N2.getNodes())
                if (!mapCodomain.contains(n2) && NodeSimilarity.getSim(n1, n2) >= ledcutoff) {
                    Set<Pair<PetrinetNode, PetrinetNode>> newMapping = new HashSet<>(map);
                    newMapping.add(new Pair<>(n1, n2));
                    open.add(newMapping);
                }
            Set<Pair<PetrinetNode, PetrinetNode>> newMapping = new HashSet<>(map);
            newMapping.add(new Pair<>(n1, new DummyNode()));
            open.add(newMapping);
        }
    }

    private Set<Set<Pair<PetrinetNode, PetrinetNode>>> createOpenSet() {

        Set<Set<Pair<PetrinetNode, PetrinetNode>>> open = new HashSet<>();
        PetrinetNode n1 = N1.getNodes().iterator().next();

        for (PetrinetNode n2 : N2.getNodes())
            if ((n1 instanceof Place && n2 instanceof Place) || (n1 instanceof Transition && n2 instanceof Transition) &&
                    NodeSimilarity.getSim(n1, n2) > ledcutoff) {
                Set<Pair<PetrinetNode, PetrinetNode>> newSet = new HashSet<>();
                newSet.add(new Pair<>(n1, n2));
                open.add(newSet);
            }

        Set<Pair<PetrinetNode, PetrinetNode>> newSet = new HashSet<>();
        newSet.add(new Pair<>(n1, new DummyNode()));
        open.add(newSet);

        return open;
    }

    private Set<Pair<PetrinetNode, PetrinetNode>> selectMappingWithMaxGES(Set<Set<Pair<PetrinetNode, PetrinetNode>>> mappings) {

        if (mappings.size() == 0)
            return null;

        Set<Pair<PetrinetNode, PetrinetNode>> bestMapping = mappings.iterator().next();
        double bestGES = GraphSimilarity.getGraphEditSimilarity(N1, N2, new Mapping(bestMapping), wskipn, wskipe, wsubn);

        for (Set<Pair<PetrinetNode, PetrinetNode>> mapping : mappings) {
            double newGES = GraphSimilarity.getGraphEditSimilarity(N1, N2, new Mapping(mapping), wskipn, wskipe, wsubn);
            if (newGES > bestGES) {
                bestGES = newGES;
                bestMapping = mapping;
            }
        }

        return bestMapping;
    }

    Set<PetrinetNode> getDomain(Set<Pair<PetrinetNode, PetrinetNode>> mapping) {

        Set<PetrinetNode> domain = new HashSet<>();
        for (Pair<PetrinetNode, PetrinetNode> pair : mapping)
            domain.add(pair.getKey());

        return domain;
    }

    Set<PetrinetNode> getCodomain(Set<Pair<PetrinetNode, PetrinetNode>> mapping) {

        Set<PetrinetNode> codomain = new HashSet<>();
        for (Pair<PetrinetNode, PetrinetNode> pair : mapping)
            codomain.add(pair.getValue());

        return codomain;
    }

    PetrinetNode selectNodeNotBelongingToSet(Set<PetrinetNode> nodes, Set<PetrinetNode> set) {

        for (PetrinetNode node : nodes)
            if (!set.contains(node))
                return node;

        return null;
    }
}