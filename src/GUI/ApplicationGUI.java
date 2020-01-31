package GUI;

import algorithms.*;
import javafx.util.Pair;
import petrinet.Marking;
import petrinet.PetriNet;
import petrinet.PnmlOwnParser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ApplicationGUI extends JFrame {

    private static final int GREEDY_ALG = 0;
    private static final int EXHAUSTIVE_ALG = 1;
    private static final int PROCESS_HEUR_ALG = 2;
    private static final int ASTAR_ALG = 3;

    private static final int TWO_MODELS = 4;
    private static final int MODEL_AND_DIR = 5;
    private static final int DIRECTORY = 6;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu aboutMenu;
    private JMenuItem addNetMenuItem;
    private JMenuItem addDirectoryMenuItem;
    private JMenuItem howToUseMenuItem;
    private JMenuItem developersMenuItem;
    private JPanel panel1;
    private JTable filesTable;
    private JLabel filesTableLabel;
    private JTable selectedFilesTable;
    private JButton selectItemButton;
    private JButton nextButton;
    private JButton removeButton;
    private JTabbedPane tabbedPane;
    private JButton nextButton1;
    private JTextArea textArea1;
    private JTextArea textArea2;
    private JRadioButton greedyAlgorithmRadioButton;
    private JRadioButton exhaustiveAlgorithmWithPruningRadioButton;
    private JRadioButton processHeuristicAlgorithmRadioButton;
    private JRadioButton aStarAlgorithmRadioButton;
    private JButton nextButton2;
    private JTextField wskipnTextField;
    private JTextField wskipeTextField;
    private JTextField wsubnTextField;
    private JTextField param1TextField;
    private JTextField param2TextField;
    private JButton nextButton3;
    private JLabel algNameLabel;
    private JLabel param1;
    private JLabel param2;
    private JLabel param1decr;
    private JLabel param2decr;
    private JProgressBar progressBar1;
    private JTextArea textArea3;
    private JButton startOverButton;
    private JButton chooseDirectoryButton;
    private DisabledEditingJTable filesTableContent;
    private DisabledEditingJTable selectedFilesTableContent;
    private JFileChooser jfc;
    private ArrayList<File> allFiles;
    private ArrayList<File> selectedFiles;
    private List<Pair<File, PetriNet>> nets;
    private int alg;
    private int comparingOption;
    private double wskipn;
    private double wskipe;
    private double wsubn;
    private int pruneat;
    private int pruneto;
    private double ledcutoff;
    private List<ComparisonResult> results;

    public ApplicationGUI() {

        alg = -1;

        allFiles = new ArrayList<>();
        selectedFiles = new ArrayList<>();

        add(panel1);
        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setEnabledAt(2, false);
        tabbedPane.setEnabledAt(3, false);
        tabbedPane.setEnabledAt(4, false);
        tabbedPane.setEnabledAt(5, false);

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        aboutMenu = new JMenu("About");
        menuBar.add(aboutMenu);

        addNetMenuItem = new JMenuItem("Add net");
        fileMenu.add(addNetMenuItem);
        addNetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jfc.setDialogTitle("Select a Petri net filesTableContent");
                jfc.setMultiSelectionEnabled(true);
                jfc.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter(".pnml models", "pnml");
                jfc.addChoosableFileFilter(filter);

                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File[] files = jfc.getSelectedFiles();
                    System.out.println("Directories found\n");
                    Arrays.asList(files).forEach(x -> {
                        if (x.isDirectory()) {
                            System.out.println(x.getName());
                        }
                    });
                    System.out.println("\n- - - - - - - - - - -\n");
                    System.out.println("Files Found\n");
                    Arrays.asList(files).forEach(x -> {
                        if (x.isFile()) {
                            System.out.println(x.getName());
                            addFileToTable(x);
                        }
                    });

                }
            }
        });

        addDirectoryMenuItem = new JMenuItem("Add directory");
        fileMenu.add(addDirectoryMenuItem);
        addDirectoryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jfc.setDialogTitle("Choose a directory to open: ");
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    if (jfc.getSelectedFile().isDirectory()) {
                        System.out.println("You selected the directory: " + jfc.getSelectedFile());
                        addFileToTable(jfc.getSelectedFile());
                    }
                }
            }
        });

        howToUseMenuItem = new JMenuItem("How to use");
        aboutMenu.add(howToUseMenuItem);
        howToUseMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = "This is a tool designed for structural comparison of Petri net models.\r\n";
                str = str + "There are three comparison options available:\r\n";
                str = str + "1. Compare two .pnml models;\r\n";
                str = str + "2. Compare a .pnml model with all .pnml models from the directory of choice;\r\n";
                str = str + "3. Compare all pairs of .pnml models in the directory of choice.\r\n\r\n";
                str = str + "To start, please select .pnml files or directories via \"File\" menu button.\r\n";
                str = str + "Then, select only those files that you need to compare.\r\nIt can either be a .pnml model, or a directory containing ones.\r\n";
                str = str + "In the \"Algorithm\" tab you need to choose a structural comparison algorithm.\r\n";
                str = str + "Tab \"Parameters\" allows you to change params for the chosen algorithm.\r\n";
                str = str + "After the comparison you can save results on your device as .txt file.";

                JOptionPane.showMessageDialog(new JFrame(), str, "", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        developersMenuItem = new JMenuItem("Developers");
        aboutMenu.add(developersMenuItem);
        developersMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = "Main developer: Semyon Tikhonov,\r\n";
                str = str + "HSE PAIS Lab.\r\n";
                str = str + "setikhonov@edu.hse.ru";
                JOptionPane.showMessageDialog(new JFrame(), str, "", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        selectItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = filesTable.getSelectedRow();
                if (row >= 0)
                    if (selectedFilesTableContent.getRowCount() < 2) {
                        if (selectedFiles.size() == 0 || !(selectedFiles.size() > 0 &&
                                selectedFiles.get(0).isDirectory() && allFiles.get(row).isDirectory())) {
                            selectedFiles.add(allFiles.get(row));
                            selectedFilesTableContent.addRow(new Object[]{allFiles.get(row).getName()});
                        } else createError("Cannot compare two directories.");
                    } else
                        createError("Please remove at least one item\nto add this one.");
            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = selectedFilesTable.getSelectedRow();
                if (row >= 0) {
                    selectedFiles.remove(row);
                    selectedFilesTableContent.removeRow(row);
                }
            }
        });
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFiles.size() > 0 && selectedFiles.size() < 3) {
                    if (selectedFiles.get(0).isDirectory()) {
                        if (selectedFiles.size() == 1) {
                            comparingOption = DIRECTORY;
                            openTab2();
                        } else if (!selectedFiles.get(1).isDirectory()) {
                            comparingOption = MODEL_AND_DIR;
                            openTab2();
                        } else
                            createError("Please make a proper choice of items.");
                    } else {
                        if (selectedFiles.get(1).isDirectory()) {
                            comparingOption = MODEL_AND_DIR;
                            openTab2();
                        } else if (selectedFiles.size() == 2) {
                            comparingOption = TWO_MODELS;
                            openTab2();
                        } else createError("Please make a proper choice of items.");
                    }
                } else {
                    createError("Please make a proper choice of items.");
                }
            }
        });
        nextButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openTab3();
            }
        });
        greedyAlgorithmRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exhaustiveAlgorithmWithPruningRadioButton.setSelected(false);
                processHeuristicAlgorithmRadioButton.setSelected(false);
                aStarAlgorithmRadioButton.setSelected(false);
                nextButton2.setEnabled(true);
                if (!greedyAlgorithmRadioButton.isSelected())
                    nextButton2.setEnabled(false);
                alg = GREEDY_ALG;
            }
        });
        exhaustiveAlgorithmWithPruningRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                greedyAlgorithmRadioButton.setSelected(false);
                processHeuristicAlgorithmRadioButton.setSelected(false);
                aStarAlgorithmRadioButton.setSelected(false);
                nextButton2.setEnabled(true);
                if (!exhaustiveAlgorithmWithPruningRadioButton.isSelected())
                    nextButton2.setEnabled(false);
                alg = EXHAUSTIVE_ALG;
            }
        });
        processHeuristicAlgorithmRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                greedyAlgorithmRadioButton.setSelected(false);
                exhaustiveAlgorithmWithPruningRadioButton.setSelected(false);
                aStarAlgorithmRadioButton.setSelected(false);
                nextButton2.setEnabled(true);
                if (!processHeuristicAlgorithmRadioButton.isSelected())
                    nextButton2.setEnabled(false);
                alg = PROCESS_HEUR_ALG;
            }
        });
        aStarAlgorithmRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                greedyAlgorithmRadioButton.setSelected(false);
                exhaustiveAlgorithmWithPruningRadioButton.setSelected(false);
                processHeuristicAlgorithmRadioButton.setSelected(false);
                nextButton2.setEnabled(true);
                if (!aStarAlgorithmRadioButton.isSelected())
                    nextButton2.setEnabled(false);
                alg = ASTAR_ALG;
            }
        });
        nextButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (greedyAlgorithmRadioButton.isSelected() || exhaustiveAlgorithmWithPruningRadioButton.isSelected() ||
                        processHeuristicAlgorithmRadioButton.isSelected() || aStarAlgorithmRadioButton.isSelected())
                    openTab4();
            }
        });
        wskipnTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    double val = Double.parseDouble(wskipnTextField.getText());
                    if (val <= 0.)
                        throw new IllegalArgumentException();
                    wskipnTextField.setForeground(Color.BLACK);
                } catch (Exception ex) {
                    wskipnTextField.setForeground(Color.RED);
                }
            }
        });
        wskipeTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    double val = Double.parseDouble(wskipeTextField.getText());
                    if (val <= 0.)
                        throw new IllegalArgumentException();
                    wskipeTextField.setForeground(Color.BLACK);
                } catch (Exception ex) {
                    wskipeTextField.setForeground(Color.RED);
                }
            }
        });
        wsubnTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    double val = Double.parseDouble(wsubnTextField.getText());
                    if (val <= 0.)
                        throw new IllegalArgumentException();
                    wsubnTextField.setForeground(Color.BLACK);
                } catch (Exception ex) {
                    wsubnTextField.setForeground(Color.RED);
                }
            }
        });
        nextButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (alg) {
                    case GREEDY_ALG: {
                        if (!parseWeights())
                            createError("Please check your parameters.");
                        else openTab5();
                        break;
                    }
                    case EXHAUSTIVE_ALG: {
                        if (!(parseWeights() && parseExhaustiveParams()))
                            createError("Please check your parameters.");
                        else openTab5();
                        break;
                    }
                    case PROCESS_HEUR_ALG: {
                        if (!(parseWeights() && parseExhaustiveParams()))
                            createError("Please check your parameters.");
                        else openTab5();
                        break;
                    }
                    case ASTAR_ALG: {
                        if (!(parseWeights() && parseAStarParams()))
                            createError("Please check your parameters.");
                        else openTab5();
                        break;
                    }
                    default: {
                        if (!parseWeights())
                            createError("Please check your parameters.");
                        else openTab5();
                        break;
                    }
                }
            }
        });
        chooseDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int retrival = chooser.showSaveDialog(null);
                if (retrival == JFileChooser.APPROVE_OPTION) {
                    ArrayList<String> resultsArray = new ArrayList<>();
                    for (ComparisonResult res : results)
                        resultsArray.add(res.buildString());

                    Path path = Paths.get(chooser.getSelectedFile() + ".txt");
                    try {
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
                        JOptionPane.showMessageDialog(new JFrame(), "File was successfully saved.", "", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        createError("Could not save file.");
                    }
                }
            }
        });
        startOverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setEnabledAt(0, true);
                tabbedPane.setEnabledAt(1, false);
                tabbedPane.setEnabledAt(2, false);
                tabbedPane.setEnabledAt(3, false);
                tabbedPane.setEnabledAt(4, false);
                tabbedPane.setEnabledAt(5, false);
                tabbedPane.setSelectedIndex(0);
                fileMenu.setEnabled(true);
                textArea1.setText("");
                textArea2.setText("");
                textArea3.setText("");
                greedyAlgorithmRadioButton.setSelected(false);
                exhaustiveAlgorithmWithPruningRadioButton.setSelected(false);
                processHeuristicAlgorithmRadioButton.setSelected(false);
                aStarAlgorithmRadioButton.setSelected(false);
            }
        });
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                switch (tabbedPane.getSelectedIndex()) {
                    case 0: {
                        tabbedPane.setEnabledAt(1, false);
                        tabbedPane.setEnabledAt(2, false);
                        tabbedPane.setEnabledAt(3, false);
                        tabbedPane.setEnabledAt(4, false);
                        tabbedPane.setEnabledAt(5, false);
                        break;
                    }
                    case 1:
                        break;
                    case 2: {
                        tabbedPane.setEnabledAt(3, false);
                        tabbedPane.setEnabledAt(4, false);
                        tabbedPane.setEnabledAt(5, false);
                        break;
                    }
                    case 3: {
                        tabbedPane.setEnabledAt(4, false);
                        tabbedPane.setEnabledAt(5, false);
                        break;
                    }
                    case 4: {
                        tabbedPane.setEnabledAt(0, false);
                        tabbedPane.setEnabledAt(1, false);
                        tabbedPane.setEnabledAt(2, false);
                        tabbedPane.setEnabledAt(3, false);
                        tabbedPane.setEnabledAt(4, true);
                        tabbedPane.setEnabledAt(5, false);
                        break;
                    }
                    case 5: {
                        tabbedPane.setEnabledAt(0, false);
                        tabbedPane.setEnabledAt(1, false);
                        tabbedPane.setEnabledAt(2, false);
                        tabbedPane.setEnabledAt(3, false);
                        tabbedPane.setEnabledAt(4, false);
                        tabbedPane.setEnabledAt(5, true);
                        break;
                    }
                }
            }
        });
    }

    private void openTab2() {
        fileMenu.setEnabled(false);
        tabbedPane.setSelectedIndex(1);
        tabbedPane.setEnabledAt(1, true);

        textArea1.setEditable(false);
        textArea2.setEditable(false);
        nextButton1.setEnabled(false);
        parseFiles();
    }

    private void openTab3() {
        tabbedPane.setSelectedIndex(2);
        tabbedPane.setEnabledAt(2, true);
        nextButton2.setEnabled(false);
    }

    private void openTab4() {
        tabbedPane.setSelectedIndex(3);
        tabbedPane.setEnabledAt(3, true);

        switch (alg) {

            case GREEDY_ALG: {
                algNameLabel.setText("Greedy algorithm:");
                wskipnTextField.setText("0.1");
                wskipeTextField.setText("0.4");
                wsubnTextField.setText("0.9");
                param1.setVisible(false);
                param2.setVisible(false);
                param1decr.setVisible(false);
                param2decr.setVisible(false);
                param1TextField.setVisible(false);
                param2TextField.setVisible(false);
                break;
            }
            case EXHAUSTIVE_ALG: {
                algNameLabel.setText("Exhaustive algorithm with pruning:");
                wskipnTextField.setText("0.1");
                wskipeTextField.setText("0.2");
                wsubnTextField.setText("0.8");
                param1.setVisible(true);
                param2.setVisible(true);
                param1TextField.setVisible(true);
                param2TextField.setVisible(true);
                param1decr.setVisible(true);
                param2decr.setVisible(true);
                param1.setText("prune at");
                param2.setText("prune to");
                param1TextField.setText("100");
                param1TextField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        try {
                            int val = Integer.parseInt(param1TextField.getText());
                            if (val <= 0)
                                throw new IllegalArgumentException();
                            param1TextField.setForeground(Color.BLACK);
                        } catch (Exception ex) {
                            param1TextField.setForeground(Color.RED);
                        }
                    }
                });
                param2TextField.setText("10");
                param2TextField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        try {
                            int val = Integer.parseInt(param2TextField.getText());
                            if (val <= 0)
                                throw new IllegalArgumentException();
                            param2TextField.setForeground(Color.BLACK);
                        } catch (Exception ex) {
                            param2TextField.setForeground(Color.RED);
                        }
                    }
                });
                param1decr.setText("positive integer value");
                param2decr.setText("positive integer value");
                break;
            }
            case PROCESS_HEUR_ALG: {
                algNameLabel.setText("Process heuristic algorithm:");
                wskipnTextField.setText("0.1");
                wskipeTextField.setText("0.2");
                wsubnTextField.setText("0.8");
                param1.setVisible(true);
                param2.setVisible(true);
                param1TextField.setVisible(true);
                param2TextField.setVisible(true);
                param1decr.setVisible(true);
                param2decr.setVisible(true);
                param1.setText("prune at");
                param2.setText("prune to");
                param1TextField.setText("100");
                param1TextField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        try {
                            int val = Integer.parseInt(param1TextField.getText());
                            if (val <= 0)
                                throw new IllegalArgumentException();
                            param1TextField.setForeground(Color.BLACK);
                        } catch (Exception ex) {
                            param1TextField.setForeground(Color.RED);
                        }
                    }
                });
                param2TextField.setText("10");
                param2TextField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        try {
                            int val = Integer.parseInt(param2TextField.getText());
                            if (val <= 0)
                                throw new IllegalArgumentException();
                            param2TextField.setForeground(Color.BLACK);
                        } catch (Exception ex) {
                            param2TextField.setForeground(Color.RED);
                        }
                    }
                });
                param1decr.setText("positive integer value");
                param2decr.setText("positive integer value");
                break;
            }
            case ASTAR_ALG: {
                algNameLabel.setText("A-star algorithm:");
                wskipnTextField.setText("0.2");
                wskipeTextField.setText("0.7");
                wsubnTextField.setText("0.1");
                param1.setVisible(true);
                param1TextField.setVisible(true);
                param1decr.setVisible(true);
                param1.setText("ledcutoff");
                param1TextField.setText("0.7");
                param1TextField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        try {
                            double val = Double.parseDouble(param1TextField.getText());
                            if (val < 0 || val > 1)
                                throw new IllegalArgumentException();
                            param1TextField.setForeground(Color.BLACK);
                        } catch (Exception ex) {
                            param1TextField.setForeground(Color.RED);
                        }
                    }
                });
                param1decr.setText("double value in [0.; 1.]");
                param2.setVisible(false);
                param2TextField.setVisible(false);
                param2decr.setVisible(false);
                break;
            }
            default: {
                alg = GREEDY_ALG;
                algNameLabel.setText("Greedy algorithm:");
                param1.setVisible(false);
                param2.setVisible(false);
                param1TextField.setVisible(false);
                param2TextField.setVisible(false);
                param1decr.setVisible(false);
                param2decr.setVisible(false);
                break;
            }
        }
    }

    private void openTab5() {
        tabbedPane.setSelectedIndex(4);
        tabbedPane.setEnabledAt(0, false);
        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setEnabledAt(2, false);
        tabbedPane.setEnabledAt(3, false);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                compareNets();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        openTab6();
                    }
                });
            }
        });
    }

    private void openTab6() {
        tabbedPane.setSelectedIndex(5);
        textArea3.setEditable(false);
        showResults();
    }

    private void showResults() {
        textArea3.append("Selected algorithm: ");
        switch (alg) {
            case GREEDY_ALG:
                textArea3.append("Greedy algorithm with params:\r\nwskipn = " + Double.toString(wskipn) +
                        ", wskipe = " + Double.toString(wskipe) + ", wsubn = " + Double.toString(wsubn) + ".\r\n\r\n");
                break;
            case EXHAUSTIVE_ALG:
                textArea3.append("Exhaustive algorithm with pruning with params:\r\nwskipn = " + Double.toString(wskipn) +
                        ", wskipe = " + Double.toString(wskipe) + ", wsubn = " + Double.toString(wsubn) +
                        ", pruneat = " + Integer.toString(pruneat) + ", pruneto = " + Integer.toString(pruneto) + ".\r\n\r\n");
                break;
            case PROCESS_HEUR_ALG:
                textArea3.append("Process heuristic algorithm with params:\r\nwskipn = " + Double.toString(wskipn) +
                        ", wskipe = " + Double.toString(wskipe) + ", wsubn = " + Double.toString(wsubn) +
                        ", pruneat = " + Integer.toString(pruneat) + ", pruneto = " + Integer.toString(pruneto) + ".\r\n\r\n");
                break;
            case ASTAR_ALG:
                textArea3.append("A-star algorithm with params:\r\nwskipn = " + Double.toString(wskipn) +
                        ", wskipe = " + Double.toString(wskipe) + ", wsubn = " + Double.toString(wsubn) +
                        ", ledcutoff = " + Double.toString(ledcutoff) + ".\r\n\r\n");
            default:
                textArea3.append("Greedy algorithm with params:\r\nwskipn = " + Double.toString(wskipn) +
                        ", wskipe = " + Double.toString(wskipe) + ", wsubn = " + Double.toString(wsubn) + ".\r\n\r\n");
                break;
        }

        for (ComparisonResult res : results) {
            textArea3.append(res.buildString() + "\r\n==========================\r\n\r\n");
        }
    }

    private boolean parseWeights() {
        try {
            double wskipnval = Double.parseDouble(wskipnTextField.getText());
            if (wskipnval <= 0) throw new IllegalArgumentException();
            double wskipeval = Double.parseDouble(wskipeTextField.getText());
            if (wskipeval <= 0) throw new IllegalArgumentException();
            double wsubnval = Double.parseDouble(wsubnTextField.getText());
            if (wsubnval <= 0) throw new IllegalArgumentException();
            wskipn = wskipnval;
            wskipe = wskipeval;
            wsubn = wsubnval;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean parseExhaustiveParams() {
        try {
            int pruneatval = Integer.parseInt(param1TextField.getText());
            if (pruneatval <= 0) throw new IllegalArgumentException();
            int prunetoval = Integer.parseInt(param2TextField.getText());
            if (prunetoval <= 0) throw new IllegalArgumentException();
            pruneat = pruneatval;
            pruneto = prunetoval;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean parseAStarParams() {
        try {
            double ledcutoffval = Double.parseDouble(param1TextField.getText());
            if (ledcutoffval < 0 || ledcutoffval > 1) throw new IllegalArgumentException();
            ledcutoff = ledcutoffval;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void compareNets() {

        System.out.println("Comparing nets.");

        results = new ArrayList<>();

        if (nets.size() <= 1)
            createError("Not enough models to compare.");
        else {
            switch (comparingOption) {
                case TWO_MODELS:
                    results.add(compareTwoNets(nets.get(0), nets.get(1)));
                    break;
                case MODEL_AND_DIR: {
                    Pair<File, PetriNet> model = nets.get(0);
                    nets.remove(0);
                    for (Pair<File, PetriNet> p : nets)
                        results.add(compareTwoNets(model, p));
                    break;
                }
                case DIRECTORY: {
                    for (int i = 0; i < nets.size() - 1; i++)
                        for (int j = i + 1; j < nets.size(); j++)
                            results.add(compareTwoNets(nets.get(i), nets.get(j)));
                }
            }

            Collections.sort(results);

            System.out.println("Nets compared.");
        }
    }

    private ComparisonResult compareTwoNets(Pair<File, PetriNet> p1, Pair<File, PetriNet> p2) {

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
                algorithm = new GreedyAlg(p1.getValue(), p2.getValue(), wskipn, wsubn, wskipe);
                break;
        }

        double GES = 0.;
        double GED = 0.;

        long start = System.nanoTime();
        try {
            algorithm.initializeMapping();
            GES = algorithm.getGES(algorithm.getMapping());
            GED = GraphSimilarity.getGraphEditDistance(p1.getValue(), p2.getValue(), algorithm.getMapping());
        } catch (Exception e) {
            createError("Something went wrong. Please restart the program");
        }

        long end = System.nanoTime();

        return new ComparisonResult(p1.getKey().getName(), p2.getKey().getName(), p1.getValue().getNodes().size(),
                p2.getValue().getNodes().size(), GES, GED, end - start);
    }

    private void parseFiles() {

        List<File> files = new ArrayList<>();

        for (File f : selectedFiles)
            if (!f.isDirectory())
                files.add(f);
        for (File f : selectedFiles)
            if (f.isDirectory())
                for (File model : f.listFiles(new PNMLFileFilter()))
                    files.add(model);

        textArea1.setText("");
        textArea2.setText("");

        nets = new ArrayList<>();
        PnmlOwnParser parser = new PnmlOwnParser();

        for (File file : files) {
            kotlin.Pair kp;
            try {
                kp = parser.parseFromFile(file);
                Pair<PetriNet, Marking> p = new Pair<PetriNet, Marking>((PetriNet) kp.component1(), (Marking) kp.component2());
                if (p.getKey().getNodes().size() == 0)
                    throw new IllegalArgumentException();
                nets.add(new Pair<>(file, p.getKey()));
                textArea1.append(file.getName() + "\n");
            } catch (Exception e) {
                textArea2.append(file.getName() + "\n");
            }
        }

        if (nets.size() > 1)
            nextButton1.setEnabled(true);
    }

    private void createError(String message) {
        JOptionPane.showMessageDialog(new JFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void addFileToTable(File file) {

        String name, type, descr;
        name = file.getName();
        type = file.isDirectory() ? "Directory" : "Petri net";
        if (file.isDirectory()) {
            int numOfModels = file.listFiles(new PNMLFileFilter()).length;
            if (numOfModels == 0) {
                createError("No .pnml files found in directory.\nPlease repeat your choice.");
                return;
            }
            descr = "Contains " + Integer.toString(file.listFiles(new PNMLFileFilter()).length) + " .pnml models";
        } else {
            descr = ".pnml model";
        }
        filesTableContent.addRow(new Object[]{name, type, descr});

        allFiles.add(file);
    }

    public static void main(String... args) {

        ApplicationGUI gui = new ApplicationGUI();

        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
        gui.setSize(640, 480);
        gui.setResizable(false);
        gui.setTitle("Compare Petri nets");
    }

    private void createUIComponents() {

        filesTableContent = new DisabledEditingJTable();
        filesTable = new JTable(filesTableContent);

        filesTableContent.addColumn("File");
        filesTableContent.addColumn("Type");
        filesTableContent.addColumn("Description");

        filesTableLabel = new JLabel("Please add files to the list below:");

        selectedFilesTableContent = new DisabledEditingJTable();
        selectedFilesTable = new JTable(selectedFilesTableContent);

        selectedFilesTableContent.addColumn("File");
    }
}