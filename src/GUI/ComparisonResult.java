package GUI;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class ComparisonResult implements Comparable<ComparisonResult> {
    private double GES;
    private double GED;

    private String name1;
    private String name2;

    private int nodes1;
    private int nodes2;

    private long time;

    public ComparisonResult(String name1, String name2, int nodes1, int nodes2, double GES, double GED, long time) {
        this.name1 = name1;
        this.name2 = name2;
        this.nodes1 = nodes1;
        this.nodes2 = nodes2;
        this.GES = GES;
        this.GED = GED;
        this.time = time;
    }

    public String buildString() {
        String res = "Comparing " + name1 + " and " + name2 + ".\r\n";
        res = res + "Model " + name1 + " contains " + nodes1 + " nodes.\r\n";
        res = res + "Model " + name2 + " contains " + nodes2 + " nodes.\r\n";
        res = res + "Graph edit distance: " + Double.toString(GED) + "\r\n";
        res = res + "Graph edit similarity: " + Double.toString(GES) + "\r\n";
        res = res + "Took time: " + Long.toString(time / 1000000) + " ms.\r\n";

        return res;
    }

    @Override
    public int compareTo(@NotNull ComparisonResult o) {
        if (this.GES < o.GES)
            return -1;
        else if (this.GES > o.GES)
            return 1;
        return 0;
    }
}
