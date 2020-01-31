package GUI;

import algorithms.*;
import javafx.util.Pair;
import petrinet.Marking;
import petrinet.PetriNet;
import petrinet.PetrinetNode;
import petrinet.PnmlOwnParser;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConsoleApplication {

    private static final int GREEDY_ALG = 0;
    private static final int EXHAUSTIVE_ALG = 1;
    private static final int PROCESS_HEUR_ALG = 2;
    private static final int ASTAR_ALG = 3;

    private static final int TWO_MODELS = 4;
    private static final int MODEL_AND_DIR = 5;
    private static final int DIRECTORY = 6;

    private static int currArrayIndex = 0;

    private static double wskipn;
    private static double wsubn;
    private static double wskipe;
    private static int pruneat;
    private static int pruneto;
    private static double ledcutoff;

    public static void main(String... args) {

        try {
            if (args.length == 0)
                throw new IllegalArgumentException("No command line arguments were received.");

            int workMode = -1;

            List<File> files = new ArrayList<>();

            String programMode = args[currArrayIndex++];
            switch (programMode) {
                case "TWO_MODELS":
                    workMode = TWO_MODELS;
                    files = initializeFiles(args, false);
                    break;
                case "MODEL_AND_DIR":
                    workMode = MODEL_AND_DIR;
                    files = initializeFiles(args, false);
                    break;
                case "DIRECTORY":
                    workMode = DIRECTORY;
                    files = initializeFiles(args, true);
                    break;
            }

            List<Pair<File, PetriNet>> nets = parseFiles(files);

            String algName = args[currArrayIndex++];
            int alg = getAlgNumber(algName);

            List<ComparisonResult> results = new ArrayList<>();

            initParams(args, alg);

            switch (workMode) {
                case TWO_MODELS:
                    results.add(compareTwoNets(nets.get(0), nets.get(1), alg));
                    break;
                case MODEL_AND_DIR: {
                    Pair<File, PetriNet> model = nets.get(0);
                    nets.remove(0);

                    for (Pair<File, PetriNet> obj : nets)
                        results.add(compareTwoNets(model, obj, alg));

                    break;
                }
                case DIRECTORY: {
                    for (int i = 0; i < nets.size() - 1; i++)
                        for (int j = i; j < nets.size(); j++)
                            results.add(compareTwoNets(nets.get(i), nets.get(j), alg));
                }
            }

            writeToFile(Paths.get(args[currArrayIndex++]), results, alg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initParams(String[] args, int alg) {
        wskipn = getNextDoubleParam(args);
        wsubn = getNextDoubleParam(args);
        wskipe = getNextDoubleParam(args);

        switch (alg) {
            case GREEDY_ALG:
                break;
            case EXHAUSTIVE_ALG:
                pruneat = getNextIntParam(args);
                pruneto = getNextIntParam(args);
                break;
            case PROCESS_HEUR_ALG:
                pruneat = getNextIntParam(args);
                pruneto = getNextIntParam(args);
                break;
            case ASTAR_ALG:
                ledcutoff = getNextDoubleParam(args);
        }
    }

    private static int getAlgNumber(String name) {
        switch (name) {
            case "GREEDY_ALG":
                return GREEDY_ALG;
            case "EXHAUSTIVE_ALG":
                return EXHAUSTIVE_ALG;
            case "PROCESS_HEUR_ALG":
                return PROCESS_HEUR_ALG;
            case "ASTAR_ALG":
                return ASTAR_ALG;
        }

        return -1;
    }

    private static List<File> initializeFiles(String[] args, boolean compareOnlyDir) {

        List<File> files = new ArrayList<>();
        File first = new File(args[currArrayIndex++]);
        if (first.isDirectory()) {
            File[] fileList = first.listFiles(new PNMLFileFilter());
            for (File f : fileList)
                files.add(f);
        } else
            files.add(first);

        if (!compareOnlyDir) {
            File second = new File(args[currArrayIndex++]);
            if (second.isDirectory()) {
                File[] fileList = second.listFiles(new PNMLFileFilter());
                for (File f : fileList)
                    files.add(f);
            } else
                files.add(0, second);
        }

        return files;
    }

    private static List<Pair<File, PetriNet>> parseFiles(List<File> files) {
        List<Pair<File, PetriNet>> nets = new ArrayList<>();
        PnmlOwnParser parser = new PnmlOwnParser();

        for (File file : files) {
            kotlin.Pair kp;
            kp = parser.parseFromFile(file);
            Pair<PetriNet, Marking> p = new Pair<PetriNet, Marking>((PetriNet) kp.component1(), (Marking) kp.component2());
            if (p.getKey().getNodes().size() == 0)
                throw new IllegalArgumentException();
            nets.add(new Pair<>(file, p.getKey()));
        }

        return nets;
    }

    private static ComparisonResult compareTwoNets(Pair<File, PetriNet> p1, Pair<File, PetriNet> p2, int alg) throws Exception {

        AlgorithmAbstract algorithm;

        switch (alg) {
            case GREEDY_ALG:
                algorithm = new GreedyAlg(p1.getValue(), p2.getValue(), wskipn, wsubn, wskipe);
                break;
            case EXHAUSTIVE_ALG:
                algorithm = new ExhAlgWPruning(p1.getValue(), p2.getValue(), pruneat, pruneto, wskipn, wsubn, wskipe);
                break;
            case PROCESS_HEUR_ALG:
                algorithm = new ProcHeuristicAlg(p1.getValue(), p2.getValue(), pruneat, pruneto, wskipn, wsubn, wskipe);
                break;
            case ASTAR_ALG:
                algorithm = new AStarAlg(p1.getValue(), p2.getValue(), wskipn, wsubn, wskipe, ledcutoff);
                break;
            default:
                throw new Exception("Something went wrong. Please start over and check your command line arguments.");
        }

        double GES = 0.;
        double GED = 0.;

        long start = System.nanoTime();
        algorithm.initializeMapping();
        GES = algorithm.getGES(algorithm.getMapping());
        GED = GraphSimilarity.getGraphEditDistance(p1.getValue(), p2.getValue(), algorithm.getMapping());

        long end = System.nanoTime();

        return new ComparisonResult(p1.getKey().getName(), p2.getKey().getName(), p1.getValue().getNodes().size(),
                p2.getValue().getNodes().size(), GES, GED, end - start);
    }

    private static void writeToFile(Path path, List<ComparisonResult> results, int alg) throws Exception {
        ArrayList<String> resultsArray = new ArrayList<>();
        for (ComparisonResult res : results)
            resultsArray.add(res.buildString());

        ArrayList<String> algInfo = new ArrayList<>();
        algInfo.add("Selected algorithm: ");
        switch (alg) {
            case GREEDY_ALG:
                algInfo.add("Greedy algorithm with params:\r\nwskipn = " + Double.toString(wskipn) +
                        ", wskipe = " + Double.toString(wskipe) + ", wsubn = " + Double.toString(wsubn) + ".\r\n\r\n");
                break;
            case EXHAUSTIVE_ALG:
                algInfo.add("Exhaustive algorithm with pruning with params:\r\nwskipn = " + Double.toString(wskipn) +
                        ", wskipe = " + Double.toString(wskipe) + ", wsubn = " + Double.toString(wsubn) +
                        ", pruneat = " + Integer.toString(pruneat) + ", pruneto = " + Integer.toString(pruneto) + ".\r\n\r\n");
                break;
            case PROCESS_HEUR_ALG:
                algInfo.add("Process heuristic algorithm with params:\r\nwskipn = " + Double.toString(wskipn) +
                        ", wskipe = " + Double.toString(wskipe) + ", wsubn = " + Double.toString(wsubn) +
                        ", pruneat = " + Integer.toString(pruneat) + ", pruneto = " + Integer.toString(pruneto) + ".\r\n\r\n");
                break;
            case ASTAR_ALG:
                algInfo.add("A-star algorithm with params:\r\nwskipn = " + Double.toString(wskipn) +
                        ", wskipe = " + Double.toString(wskipe) + ", wsubn = " + Double.toString(wsubn) +
                        ", ledcutoff = " + Double.toString(ledcutoff) + ".\r\n\r\n");
            default:
                algInfo.add("Greedy algorithm with params:\r\nwskipn = " + Double.toString(wskipn) +
                        ", wskipe = " + Double.toString(wskipe) + ", wsubn = " + Double.toString(wsubn) + ".\r\n\r\n");
                break;
        }
        Files.write(path, algInfo, Charset.forName("UTF-8"));
        Files.write(path, resultsArray, Charset.forName("UTF-8"));
        System.out.println("Results saved to " + path.toString() + ".");
    }

    private static double getNextDoubleParam(String[] args) {
        return Double.parseDouble(args[currArrayIndex++]);
    }

    private static int getNextIntParam(String[] args) {
        return Integer.parseInt(args[currArrayIndex++]);
    }
}
