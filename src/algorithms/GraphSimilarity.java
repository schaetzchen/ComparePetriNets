package algorithms;

import javafx.util.Pair;
import petrinet.*;

import java.util.HashSet;
import java.util.Set;

public abstract class GraphSimilarity {

    private static Pair<Set<PetrinetNode>, Set<PetrinetNode>> getDomainAndCodomain(Mapping M) {

        Set<PetrinetNode> domain = new HashSet<>();
        Set<PetrinetNode> codomain = new HashSet<>();

        for (Pair<PetrinetNode, PetrinetNode> p : M.getMapping()) {
            domain.add(p.getKey());
            codomain.add(p.getValue());
        }

        return new Pair<>(domain, codomain);
    }

    private static void updateSubnAndSkipn(PetrinetNode n, Set<PetrinetNode> subn, Set<PetrinetNode> skipn,
                                           Set<PetrinetNode> domain, Set<PetrinetNode> codomain) {
        if (domain.contains(n) || codomain.contains(n))
            subn.add(n);
        else
            skipn.add(n);
    }

    private static void updateSkipe(Arc arc, Set<Arc> skipe, Mapping M) {

        // TODO: Notion?

        Pair<PetrinetNode, PetrinetNode> sourcePair = M.searchPairByNode(arc.getSource());
        Pair<PetrinetNode, PetrinetNode> targetPair = M.searchPairByNode(arc.getTarget());

        if (sourcePair == null &&
                targetPair == null)
            skipe.add(arc);
    }

    private static double getSimilaritySum(Mapping M) {

        double sum = 0.;

        for (Pair<PetrinetNode, PetrinetNode> p : M.getMapping())
            sum += 1. - NodeSimilarity.getSim(p.getKey(), p.getValue());

        return sum;
    }

    public static double getGraphEditDistance(PetriNet N1, PetriNet N2, Mapping M) {

        Pair<Set<PetrinetNode>, Set<PetrinetNode>> domPair = getDomainAndCodomain(M);
        Set<PetrinetNode> domain = domPair.getKey();
        Set<PetrinetNode> codomain = domPair.getValue();

        Set<PetrinetNode> subn = new HashSet<>();
        Set<PetrinetNode> skipn = new HashSet<>();
        Set<Arc> skipe = new HashSet<>();

        for (Place p : N1.getPlaces())
            updateSubnAndSkipn(p, subn, skipn, domain, codomain);
        for (Transition t : N1.getTransitions())
            updateSubnAndSkipn(t, subn, skipn, domain, codomain);
        for (Arc a : N1.getArcs())
            updateSkipe(a, skipe, M);
        for (Place p : N2.getPlaces())
            updateSubnAndSkipn(p, subn, skipn, domain, codomain);
        for (Transition t : N2.getTransitions())
            updateSubnAndSkipn(t, subn, skipn, domain, codomain);
        for (Arc a : N2.getArcs())
            updateSkipe(a, skipe, M);

        double GED = skipn.size() + skipe.size() + 2. * getSimilaritySum(M);

        return GED;
    }

    public static double getGraphEditSimilarity(PetriNet N1, PetriNet N2, Mapping M,
                                                double wskipn, double wskipe, double wsubn) {
        Pair<Set<PetrinetNode>, Set<PetrinetNode>> domPair = getDomainAndCodomain(M);
        Set<PetrinetNode> domain = domPair.getKey();
        Set<PetrinetNode> codomain = domPair.getValue();

        Set<PetrinetNode> subn = new HashSet<>();
        Set<PetrinetNode> skipn = new HashSet<>();
        Set<Arc> skipe = new HashSet<>();

        for (Place p : N1.getPlaces())
            updateSubnAndSkipn(p, subn, skipn, domain, codomain);
        for (Transition t : N1.getTransitions())
            updateSubnAndSkipn(t, subn, skipn, domain, codomain);
        for (Arc a : N1.getArcs())
            updateSkipe(a, skipe, M);
        for (Place p : N2.getPlaces())
            updateSubnAndSkipn(p, subn, skipn, domain, codomain);
        for (Transition t : N2.getTransitions())
            updateSubnAndSkipn(t, subn, skipn, domain, codomain);
        for (Arc a : N2.getArcs())
            updateSkipe(a, skipe, M);

        double fskipn = (double) skipn.size() / (double) (N1.getPlaces().size() + N1.getTransitions().size() +
                N2.getPlaces().size() + N2.getTransitions().size());

        double fskipe = (double) skipe.size() / (double) (N1.getArcs().size() + N2.getArcs().size());

        // When subn set is empty we assume that fsubn equals 0.0
        // Though such case was not explicitly mentioned in the article
        // "Graph Matching Algorithms for Business Process Model Similarity Search"
        double fsubn = subn.isEmpty() ? 0 : 2. * getSimilaritySum(M) / (double) subn.size();

        double GES = 1. - (wskipn * fskipn + wskipe * fskipe + wsubn * fsubn) / (wskipn + wskipe + wsubn);

        return GES;
    }

}