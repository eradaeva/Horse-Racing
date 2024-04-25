package Part2;

import Part1.Horse;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.event.*;

public class RaceGui {
    private static final int roadSideLength = 80;
    private static Horse[] horses;
    private static double balance = 1000.0;
    private static HashMap<Integer, Double> currentBets = new HashMap<>();
    private static final String[] colors = new String[]{"Yellow", "Blue", "Green", "Pink", "Red"};
    private static String[] colorsChosen;
    private static JComboBox<String>[] choiceBoxes;

    private static double[] calculatePayouts(int length){
        double[] payouts = new double[horses.length];
        Double[] confidences = new Double[horses.length];

        for (int i = 0; i < horses.length; i++) {
            confidences[i] = horses[i].getConfidence();
        }

        Double[] confidencesSorted = confidences.clone();

        Arrays.sort(confidencesSorted, new Comparator<Double>() {
            public int compare(Double a, Double b) {
                return Double.compare(b, a);
            }
        });

        int multiplyer = 1;
        double currentResult;
        double chance;
        int index = -1;
        double prob;
        Random random = new Random();

        for (Double confidence : confidencesSorted) {
            currentResult = Math.pow(1 - (Math.pow(confidence, 2) * 0.1), (double)length);
            chance = currentResult * multiplyer;
            multiplyer -= chance;

            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] == confidence) {
                    index = i;
                    break;
                }
            }

            prob = Math.ceil(chance * 10) / 10.0 * 1.05;
            if (prob == 0.0) prob = 0.1;
            while (1.0 / prob > 7.5) {
                prob *= random.nextDouble(1.1, 2.2);
            }
            payouts[index] = Math.round(1.0 / prob * 100.0) / 100.0;
            
        }

        return payouts;
    }

    private static void moveHorse(Horse theHorse){
        if  (!theHorse.hasFallen()) {
            if (Math.random() < theHorse.getConfidence()) {
               theHorse.moveForward();
            }
            if (Math.random() < (0.1*theHorse.getConfidence()*theHorse.getConfidence())) {
                theHorse.fall();
            }
        }
    }

    private static void addStartHorses(JPanel trackPanel, int lanes, int length, JPanel infoPanel) {
        ImageIcon horseIcon;

        JLabel confidenceLabels[] = new JLabel[lanes];

        infoPanel.removeAll();

        for (int i = 0; i < lanes; i++) {
            confidenceLabels[i] = new JLabel("     " + horses[i].getName().toUpperCase() + " (Confidence: " + Math.round(horses[i].getConfidence() * 100) + "%) Status: Ready ");
            confidenceLabels[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            infoPanel.add(confidenceLabels[i]);
        }

        JLabel horseLabel;
        for (int i = 0; i < lanes; i++) {
            horseIcon = new ImageIcon("Part2/Sprites/Horses/" + colorsChosen[i] + "HorseFull.png");
            horseIcon.setImage(horseIcon.getImage().getScaledInstance(roadSideLength, roadSideLength, Image.SCALE_DEFAULT));
            horseLabel = new JLabel(horseIcon);

            trackPanel.remove(lanes * length - 1);
            trackPanel.add(horseLabel, i * length);
            trackPanel.repaint();
            trackPanel.revalidate();
        }
    }

    private static void startRace(JFrame frame, JPanel trackPanel, JPanel infoPanel, int lanes, int length, ImageIcon roadPiece, JSlider linesSlider, JSlider lengthSlider, JButton startButton, JButton resetButton, JButton betButton, JLabel balanceLabel) {
        drawRacetrack(trackPanel, roadPiece, lanes, length);
        addStartHorses(trackPanel, lanes, length, infoPanel);

        for (int i = 0; i < horses.length; i++) {
            horses[i].goBackToStart();
        }

        infoPanel.setMinimumSize(new Dimension(1, roadSideLength*lanes));

        frame.setMinimumSize(null);
        frame.pack();
        frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
        frame.setMaximumSize(new Dimension(frame.getWidth(), frame.getHeight()));

        Timer timer;

        timer = new Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean finished = false;
                boolean anyAlive = true;
                JLabel horseLabel;
                ImageIcon runningImage;
                ImageIcon fallingImage;
                anyAlive = false;
                Random random = new Random();

                drawRacetrack(trackPanel, roadPiece, lanes, length);

                for (int i = 0; i < horses.length; i++) {
                    if(!horses[i].hasFallen()) anyAlive = true;
                    moveHorse(horses[i]);
                }

                for (int i = 0; i < horses.length; i++) {
                    if (horses[i].getDistanceTravelled() >= length-1 && !horses[i].hasFallen()) {
                        finished = true;
                    }
                }

                for (int i = 0; i < horses.length; i++) {
                    if (horses[i].hasFallen()) {
                        fallingImage = new ImageIcon("Part2/Sprites/Horses/" + colorsChosen[i] + "HorseFall.png");
                        fallingImage.setImage(fallingImage.getImage().getScaledInstance(roadSideLength, roadSideLength, Image.SCALE_DEFAULT));
                        horseLabel = new JLabel(fallingImage); 
                    } else {
                        runningImage = new ImageIcon("Part2/Sprites/Horses/" + colorsChosen[i] + "HorseFull.png");
                        runningImage.setImage(runningImage.getImage().getScaledInstance(roadSideLength, roadSideLength, Image.SCALE_DEFAULT));
                        horseLabel = new JLabel(runningImage); 
                    }

                    trackPanel.remove(lanes * length - 1);
                    trackPanel.add(horseLabel, i * length + horses[i].getDistanceTravelled());
                    trackPanel.repaint();
                    trackPanel.revalidate();
                }

                infoPanel.removeAll();

                JLabel confidenceLabels[] = new JLabel[lanes];

                for (int i = 0; i < lanes; i++) {
                    confidenceLabels[i] = new JLabel("     " + horses[i].getName().toUpperCase() + " (Confidence: " + Math.round(horses[i].getConfidence() * 100) + "%) Status: " + (finished || !anyAlive ? (horses[i].getDistanceTravelled() >= length-1 ? (horses[i].hasFallen() ? "Lost " : "Won ") : "Lost ") : (horses[i].hasFallen() ? "Fallen " : "Running ")));
                    confidenceLabels[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
                    infoPanel.add(confidenceLabels[i]);
                }

                if (frame.getPreferredSize().getWidth() > frame.getWidth()) {
                    frame.setMinimumSize(null);
                    frame.pack();
                    frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
                    frame.setMaximumSize(new Dimension(frame.getWidth(), frame.getHeight()));
                }

                if (!anyAlive || finished) {
                    ((Timer)e.getSource()).stop();
                    linesSlider.setEnabled(true);
                    lengthSlider.setEnabled(true);
                    startButton.setEnabled(true);
                    resetButton.setEnabled(true);
                    betButton.setEnabled(true);
                    for (JComboBox<String> choiceBox : choiceBoxes) {
                        choiceBox.setEnabled(true);
                    }
                    
                    frame.setMinimumSize(null);
                    frame.pack();
                    frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
                    frame.setMaximumSize(new Dimension(frame.getWidth(), frame.getHeight()));

                    for (int i = 0; i < horses.length; i++) {
                        if (horses[i].getDistanceTravelled() >= length-1 && !horses[i].hasFallen()) {
                            horses[i].setConfidence(horses[i].getConfidence() + random.nextDouble(0.01, 0.1));
                            if (horses[i].getConfidence() > 1) {
                                horses[i].setConfidence(1);
                            }
                        } else {
                            horses[i].setConfidence(horses[i].getConfidence() - random.nextDouble(0.01, 0.1));
                        }
                    }

                    if (!currentBets.isEmpty()) {
                        double totalWins = 0;
                        for (HashMap.Entry<Integer, Double> bet : currentBets.entrySet()) {
                            if (horses[bet.getKey()].getDistanceTravelled() >= length-1 && !horses[bet.getKey()].hasFallen()) {
                                totalWins += bet.getValue();
                            }
                        }
                        
                        if (totalWins > 0) {
                            JOptionPane.showMessageDialog(null, "Your total winnings are " + DecimalFormat.getCurrencyInstance(Locale.US).format(totalWins) + "!", null, JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Sorry, no reward this time.", null, JOptionPane.INFORMATION_MESSAGE);
                        }
                        balance += totalWins;
                        currentBets.clear();

                        if (balance <= 0.05) {
                            JOptionPane.showMessageDialog(null, "Seems like you are running out of money :( Don't worry, here is another thousand!", null, JOptionPane.INFORMATION_MESSAGE);
                            balance += 1000;
                        }

                        balanceLabel.setText("Balance: " + DecimalFormat.getCurrencyInstance(Locale.US).format(Math.floor(balance * 100) / 100.0));
                    }
                    
                }
            }
        });

        timer.start();
    }

    private static void drawRacetrack(JPanel trackPanel, ImageIcon trackPiece, int lanes, int length) {
        trackPanel.removeAll();
        trackPanel.setLayout(new GridLayout(lanes, length, 0, 0));

        Dimension trackDimension = new Dimension(length * roadSideLength, lanes * roadSideLength);

        trackPanel.setMinimumSize(trackDimension);
        trackPanel.setPreferredSize(trackDimension);
        trackPanel.setMaximumSize(trackDimension);

        JLabel roadLabel;
        for (int i = 0; i < lanes; i++) {
            for (int j = 0; j < length; j++) {
                roadLabel = new JLabel(trackPiece);
                trackPanel.add(roadLabel);
            }
        }
    }

    public static void startRaceGUI() {
        final int lanes = 3;
        final int length = 8;

        JFrame frame = new JFrame("Horse Racing");
        frame.setLayout(new GridBagLayout());

        /* Road Pieces Start */
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE; 
        constraints.gridx = 2;
        constraints.gridy = 0;

        JPanel raceTrackPanel = new JPanel(new GridLayout(lanes, length, 0, 0));
        JPanel settings = new JPanel();

        ImageIcon roadPiece = new ImageIcon("Part2/Sprites/roadPiece.png");
        roadPiece.setImage(roadPiece.getImage().getScaledInstance(roadSideLength, roadSideLength, Image.SCALE_DEFAULT));

        frame.add(raceTrackPanel, constraints);
        /* Road Pieces End */

        /* Controls */
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        JSlider linesSlider = new JSlider(2, 5, 3);

        linesSlider.setPaintTicks(true);
        linesSlider.setPaintTrack(true);
        linesSlider.setPaintLabels(true);
        linesSlider.setMajorTickSpacing(1);
        linesSlider.setMinorTickSpacing(1);

        JLabel balanceLabel = new JLabel("Balance: " + DecimalFormat.getCurrencyInstance(Locale.US).format(Math.floor(balance * 100) / 100.0));

        JSlider lengthSlider = new JSlider(2, 13, 8);

        lengthSlider.setPaintTicks(true);
        lengthSlider.setPaintTrack(true);
        lengthSlider.setPaintLabels(true);
        lengthSlider.setMajorTickSpacing(11);
        lengthSlider.setMinorTickSpacing(1);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        linesSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                drawRacetrack(raceTrackPanel, roadPiece, linesSlider.getValue(), lengthSlider.getValue());

                Random random = new Random();
                horses = new Horse[linesSlider.getValue()];

                for (int i = 0; i < horses.length; i++) {
                    horses[i] = Horse.createHorse('H', "Horse " + i, random.nextDouble(0.15, 1));
                    horses[i].goBackToStart();
                }

                settings.removeAll();

                colorsChosen = new String[horses.length];

                JComboBox<String>[] tempBoxes = choiceBoxes.clone();
                choiceBoxes = new JComboBox[horses.length];
                for (int i = 0; i < Math.min(choiceBoxes.length, tempBoxes.length); i++) {
                    choiceBoxes[i] = tempBoxes[i];
                }

                for (int i = 0; i < horses.length; i++) {
                    if (choiceBoxes[i] == null) {
                        JComboBox<String> colorChoice = new JComboBox<>(colors);
                        choiceBoxes[i] = colorChoice;
                    }
                    settings.add(choiceBoxes[i]);

                    if (colorsChosen[i] == null) {
                        colorsChosen[i] = choiceBoxes[i].getSelectedItem().toString().toLowerCase();
                    } 

                    choiceBoxes[i].addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent e) {
                            Object choiceBox = e.getItemSelectable();

                            for (int j = 0; j < choiceBoxes.length; j++) {
                                if (choiceBox == choiceBoxes[j]) {
                                    colorsChosen[j] = (String)choiceBoxes[j].getSelectedItem().toString().toLowerCase();
                                    break;
                                }
                            }

                            drawRacetrack(raceTrackPanel, roadPiece,  linesSlider.getValue(), lengthSlider.getValue());
                            // infoPanel.removeAll();
                            addStartHorses(raceTrackPanel, linesSlider.getValue(), lengthSlider.getValue(), infoPanel);
                        }
                    });
                }

                // infoPanel.removeAll();
                addStartHorses(raceTrackPanel, linesSlider.getValue(), lengthSlider.getValue(), infoPanel);
                frame.setMinimumSize(null);
                frame.pack();
                frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
                frame.setMaximumSize(new Dimension(frame.getWidth(), frame.getHeight()));
            }
        });

        lengthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                drawRacetrack(raceTrackPanel, roadPiece, linesSlider.getValue(), lengthSlider.getValue());
                // infoPanel.removeAll();
                addStartHorses(raceTrackPanel, linesSlider.getValue(), lengthSlider.getValue(), infoPanel);
                frame.setMinimumSize(null);
                frame.pack();
                frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
                frame.setMaximumSize(new Dimension(frame.getWidth(), frame.getHeight()));
            }
        });

        Random random = new Random();
        horses = new Horse[linesSlider.getValue()];

        for (int i = 0; i < horses.length; i++) {
            horses[i] = Horse.createHorse('H', "Horse " + i, random.nextDouble(0.15, 1));
            horses[i].goBackToStart();
        }

        drawRacetrack(raceTrackPanel, roadPiece, linesSlider.getValue(), lengthSlider.getValue());

        JButton startButton = new JButton("Start Race");
        startButton.setHorizontalAlignment(JButton.CENTER);

        JButton resetButton = new JButton("Reset Race");
        JButton betButton = new JButton("Bet");

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                linesSlider.setEnabled(false);
                lengthSlider.setEnabled(false);
                startButton.setEnabled(false);
                resetButton.setEnabled(false);
                betButton.setEnabled(false);
                for (JComboBox<String> choiceBox : choiceBoxes) {
                    choiceBox.setEnabled(false);
                }

                startRace(frame, raceTrackPanel, infoPanel, linesSlider.getValue(), lengthSlider.getValue(), roadPiece, linesSlider, lengthSlider, startButton, resetButton, betButton, balanceLabel);
            }
        });

        resetButton.setHorizontalAlignment(JButton.CENTER);

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawRacetrack(raceTrackPanel, roadPiece, linesSlider.getValue(), lengthSlider.getValue());
                

                Random random = new Random();
                horses = new Horse[linesSlider.getValue()];

                for (int i = 0; i < horses.length; i++) {
                    horses[i] = Horse.createHorse('H', "Horse " + i, random.nextDouble(0.15, 1));
                    horses[i].goBackToStart();
                }

                // infoPanel.removeAll();
                addStartHorses(raceTrackPanel, linesSlider.getValue(), lengthSlider.getValue(), infoPanel);

                frame.setMinimumSize(null);
                frame.pack();
                frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
                frame.setMaximumSize(new Dimension(frame.getWidth(), frame.getHeight()));
            }
        });
        /* Controls End */

        /* Settings Start */
        colorsChosen = new String[horses.length];
        settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));

        choiceBoxes = new JComboBox[horses.length];

        for (int i = 0; i < horses.length; i++) {
            JComboBox<String> colorChoice = new JComboBox<>(colors);
            choiceBoxes[i] = colorChoice;

            colorsChosen[i] = "yellow";

            colorChoice.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    Object choiceBox = e.getItemSelectable();

                    for (int j = 0; j < choiceBoxes.length; j++) {
                        if (choiceBox == choiceBoxes[j]) {
                            colorsChosen[j] = (String)choiceBoxes[j].getSelectedItem().toString().toLowerCase();
                            break;
                        }
                    }

                    drawRacetrack(raceTrackPanel, roadPiece,  linesSlider.getValue(), lengthSlider.getValue());
                    addStartHorses(raceTrackPanel, linesSlider.getValue(), lengthSlider.getValue(), infoPanel);
                    resetButton.doClick();
                }
            });

            settings.add(colorChoice);
        }

        addStartHorses(raceTrackPanel, linesSlider.getValue(), lengthSlider.getValue(), infoPanel);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridx = 1;
        constraints.gridy = 0;

        frame.add(settings, constraints);
        /* Settings End */

        /* Betting Start */
        betButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame bettingFrame = new JFrame();
                JTextField[] betInputs = new JTextField[linesSlider.getValue()];
                bettingFrame.setLayout(new GridLayout(linesSlider.getValue()+2, 2, 10, 10));

                double[] payouts = calculatePayouts(lengthSlider.getValue());

                for (int i = 0; i < linesSlider.getValue(); i++) {
                    JLabel horseNameLabel = new JLabel(horses[i].getName().toUpperCase() + " Payout Ratio: " + payouts[i]);
                    bettingFrame.add(horseNameLabel);

                    JTextField textField = new JTextField("0", 1);
                    bettingFrame.add(textField);
                    betInputs[i] = textField;
                }

                JButton submitBetsButton = new JButton("Submit Bets and Start");
                JButton cancelBetsButton = new JButton("Cancel");

                JLabel warning1 = new JLabel("");
                warning1.setHorizontalAlignment(4);
                JLabel warning2 = new JLabel("");

                bettingFrame.add(warning1);
                bettingFrame.add(warning2);

                submitBetsButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        double totalBets = 0;
                        double betSize;
                        currentBets.clear();
                        for (int i = 0; i < betInputs.length; i++) {
                            try {
                                betSize = Double.parseDouble(betInputs[i].getText());
                                totalBets += betSize;
                                currentBets.put(i, betSize * payouts[i]);
                            } catch (NumberFormatException err) {
                                warning1.setText("Error: Incorrect input.");
                                warning2.setText("Try again or cancel");
                                totalBets = -1;
                                break;
                            }
                        }

                        if (totalBets == -1 || totalBets > balance) {
                            if (totalBets > balance) {
                                currentBets.clear();
                                warning1.setText("Error: Insufficient Balance.");
                                warning2.setText("Lower the bets or deposit.");
                            }
                        } else {
                            bettingFrame.setVisible(false);
                            for (HashMap.Entry<Integer, Double> bet : currentBets.entrySet()) {
                                balance -= bet.getValue() / payouts[bet.getKey()];
                            }
                            balanceLabel.setText("Balance: " + DecimalFormat.getCurrencyInstance(Locale.US).format(Math.floor(balance * 100) / 100.0));
                            startButton.doClick();
                        }
                    }
                });

                cancelBetsButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        bettingFrame.setVisible(false);
                    }
                });

                bettingFrame.add(submitBetsButton);
                bettingFrame.add(cancelBetsButton);

                bettingFrame.pack();
                bettingFrame.setVisible(true);
            }
        });
        /* Betting End */


        JLabel title = new JLabel("Num of Lanes:");
        controlsPanel.add(title);
        controlsPanel.add(linesSlider);
        title = new JLabel("Length of track:");
        controlsPanel.add(title);
        controlsPanel.add(lengthSlider);
        controlsPanel.add(startButton);
        controlsPanel.add(resetButton);
        controlsPanel.add(betButton);
        controlsPanel.add(balanceLabel);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;

        frame.add(controlsPanel, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.VERTICAL;

        frame.add(infoPanel, constraints);

        frame.pack();
        frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
        frame.setMaximumSize(new Dimension(frame.getWidth(), frame.getHeight()));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
