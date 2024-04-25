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

    public static void addStartHorses(JPanel trackPanel, int lanes, int length, JPanel infoPanel) {
        ImageIcon horseIcon = new ImageIcon("Part2/Sprites/Horses/Yellow/yellowHorseFull.png");
        horseIcon.setImage(horseIcon.getImage().getScaledInstance(roadSideLength, roadSideLength, Image.SCALE_DEFAULT));

        JLabel confidenceLabels[] = new JLabel[lanes];

        for (int i = 0; i < lanes; i++) {
            confidenceLabels[i] = new JLabel("     " + horses[i].getName().toUpperCase() + " (Confidence: " + Math.round(horses[i].getConfidence() * 100) + "%) Status: Ready");
            confidenceLabels[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            infoPanel.add(confidenceLabels[i]);
        }

        JLabel horseLabel;
        for (int i = 0; i < lanes; i++) {
            horseLabel = new JLabel(horseIcon);

            trackPanel.remove(lanes * length - 1);
            trackPanel.add(horseLabel, i * length);
            trackPanel.repaint();
            trackPanel.revalidate();
        }
    }

    public static void startRace(JFrame frame, JPanel trackPanel, JPanel infoPanel, int lanes, int length, ImageIcon roadPiece, JSlider linesSlider, JSlider lengthSlider, JButton startButton, JButton resetButton, JButton betButton, JLabel balanceLabel) {
        ImageIcon horseIcon = new ImageIcon("Part2/Sprites/Horses/Yellow/yellowHorseFull.png");
        horseIcon.setImage(horseIcon.getImage().getScaledInstance(roadSideLength, roadSideLength, Image.SCALE_DEFAULT));

        ImageIcon fallenHorseIcon = new ImageIcon("Part2/Sprites/Horses/Yellow/yellowHorseFall.png");
        fallenHorseIcon.setImage(fallenHorseIcon.getImage().getScaledInstance(roadSideLength, roadSideLength, Image.SCALE_DEFAULT));

        infoPanel.removeAll();

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
                        horseLabel = new JLabel(fallenHorseIcon);
                    } else {
                        horseLabel = new JLabel(horseIcon);
                    }

                    trackPanel.remove(lanes * length - 1);
                    trackPanel.add(horseLabel, i * length + horses[i].getDistanceTravelled());
                    trackPanel.repaint();
                    trackPanel.revalidate();
                }

                infoPanel.removeAll();

                JLabel confidenceLabels[] = new JLabel[lanes];

                for (int i = 0; i < lanes; i++) {
                    confidenceLabels[i] = new JLabel("     " + horses[i].getName().toUpperCase() + " (Confidence: " + Math.round(horses[i].getConfidence() * 100) + "%) Status: " + (finished || !anyAlive ? (horses[i].getDistanceTravelled() >= length-1 ? (horses[i].hasFallen() ? "Lost " : "Won ") : "Lost ") : (horses[i].hasFallen() ? "Fallen" : "Running ")));
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

    public static void drawRacetrack(JPanel trackPanel, ImageIcon trackPiece, int lanes, int length) {
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

    public static void launchApp() {
        final int lanes = 3;
        final int length = 8;

        JFrame frame = new JFrame("Horse Racing");
        frame.setLayout(new GridBagLayout());

        /* Road Pieces Start */
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE; // Do not resize the component
        constraints.gridx = 1;
        constraints.gridy = 0;

        JPanel raceTrackPanel = new JPanel(new GridLayout(lanes, length, 0, 0));

        ImageIcon roadPiece = new ImageIcon("Part2/Sprites/roadPiece.png");
        roadPiece.setImage(roadPiece.getImage().getScaledInstance(roadSideLength, roadSideLength, Image.SCALE_DEFAULT));

        frame.add(raceTrackPanel, constraints);
        /* Road Pieces End */

        /* Controls */
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        JSlider linesSlider = new JSlider(2, 5, 3);

        linesSlider.setPaintTicks(true);
        linesSlider.setPaintTrack(true);
        linesSlider.setPaintLabels(true);
        linesSlider.setMajorTickSpacing(1);
        linesSlider.setMinorTickSpacing(1);

        JLabel balanceLabel = new JLabel("Balance: " + DecimalFormat.getCurrencyInstance(Locale.US).format(Math.floor(balance * 100) / 100.0));

        JSlider lengthSlider = new JSlider(2, 14, 8);

        lengthSlider.setPaintTicks(true);
        lengthSlider.setPaintTrack(true);
        lengthSlider.setPaintLabels(true);
        lengthSlider.setMajorTickSpacing(12);
        lengthSlider.setMinorTickSpacing(1);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        linesSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                drawRacetrack(raceTrackPanel, roadPiece, linesSlider.getValue(), lengthSlider.getValue());
                infoPanel.removeAll();

                Random random = new Random();
                horses = new Horse[linesSlider.getValue()];

                for (int i = 0; i < horses.length; i++) {
                    horses[i] = new Horse('H', "Horse " + i, random.nextDouble(0.15, 1));
                    horses[i].goBackToStart();
                }

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
                infoPanel.removeAll();
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
            horses[i] = new Horse('H', "Horse " + i, random.nextDouble(0.15, 1));
            horses[i].goBackToStart();
        }

        drawRacetrack(raceTrackPanel, roadPiece, linesSlider.getValue(), lengthSlider.getValue());
        addStartHorses(raceTrackPanel, linesSlider.getValue(), lengthSlider.getValue(), infoPanel);

        JButton startButton = new JButton("Start Race");
        // startButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
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
                startRace(frame, raceTrackPanel, infoPanel, linesSlider.getValue(), lengthSlider.getValue(), roadPiece, linesSlider, lengthSlider, startButton, resetButton, betButton, balanceLabel);
            }
        });

        // resetButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        resetButton.setHorizontalAlignment(JButton.CENTER);

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drawRacetrack(raceTrackPanel, roadPiece, linesSlider.getValue(), lengthSlider.getValue());
                infoPanel.removeAll();

                Random random = new Random();
                horses = new Horse[linesSlider.getValue()];

                for (int i = 0; i < horses.length; i++) {
                    horses[i] = new Horse('H', "Horse " + i, random.nextDouble(0.15, 1));
                    horses[i].goBackToStart();
                }

                addStartHorses(raceTrackPanel, linesSlider.getValue(), lengthSlider.getValue(), infoPanel);

                frame.setMinimumSize(null);
                frame.pack();
                frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
                frame.setMaximumSize(new Dimension(frame.getWidth(), frame.getHeight()));
            }
        });
        /* Controls End */

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
        frame.add(controlsPanel, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 2;
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
