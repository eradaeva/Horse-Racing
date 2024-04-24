package Part2;

import Part1.Horse;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

public class RaceGui {
    private static final int roadSideLength = 80;

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

    public static void addStartHorses(JPanel trackPanel, int lanes, int length) {
        ImageIcon horseIcon = new ImageIcon("Part2/Sprites/Horses/Yellow/yellowHorseFull.png");
        horseIcon.setImage(horseIcon.getImage().getScaledInstance(roadSideLength, roadSideLength, Image.SCALE_DEFAULT));


        JLabel horseLabel;
        for (int i = 0; i < lanes; i++) {
            horseLabel = new JLabel(horseIcon);

            trackPanel.remove(lanes * length - 1);
            trackPanel.add(horseLabel, i * length);
            trackPanel.repaint();
            trackPanel.revalidate();
        }
    }

    public static void startRace(JFrame frame, JPanel trackPanel, JPanel infoPanel, int lanes, int length, ImageIcon roadPiece, JSlider linesSlider, JSlider lengthSlider, JButton startButton) {
        Random random = new Random();
        Horse[] horses = new Horse[lanes];

        ImageIcon horseIcon = new ImageIcon("Part2/Sprites/Horses/Yellow/yellowHorseFull.png");
        horseIcon.setImage(horseIcon.getImage().getScaledInstance(roadSideLength, roadSideLength, Image.SCALE_DEFAULT));

        ImageIcon fallenHorseIcon = new ImageIcon("Part2/Sprites/Horses/Yellow/yellowHorseFall.png");
        fallenHorseIcon.setImage(fallenHorseIcon.getImage().getScaledInstance(roadSideLength, roadSideLength, Image.SCALE_DEFAULT));

        drawRacetrack(trackPanel, roadPiece, lanes, length);
        addStartHorses(trackPanel, lanes, length);

        for (int i = 0; i < horses.length; i++) {
            horses[i] = new Horse('H', "Horse " + i, random.nextDouble(0.15, 1));
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

                drawRacetrack(trackPanel, roadPiece, lanes, length);

                JLabel confidenceLabels[] = new JLabel[lanes];

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
                    
                    frame.setMinimumSize(null);
                    frame.pack();
                    frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
                    frame.setMaximumSize(new Dimension(frame.getWidth(), frame.getHeight()));
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

        drawRacetrack(raceTrackPanel, roadPiece, lanes, length);
        addStartHorses(raceTrackPanel, lanes, length);

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
                addStartHorses(raceTrackPanel, linesSlider.getValue(), lengthSlider.getValue());
                infoPanel.removeAll();
                frame.setMinimumSize(null);
                frame.pack();
                frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
                frame.setMaximumSize(new Dimension(frame.getWidth(), frame.getHeight()));
            }
        });

        lengthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                drawRacetrack(raceTrackPanel, roadPiece, linesSlider.getValue(), lengthSlider.getValue());
                addStartHorses(raceTrackPanel, linesSlider.getValue(), lengthSlider.getValue());
                infoPanel.removeAll();
                frame.setMinimumSize(null);
                frame.pack();
                frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
                frame.setMaximumSize(new Dimension(frame.getWidth(), frame.getHeight()));
            }
        });

        JButton startButton = new JButton("Start Race");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                linesSlider.setEnabled(false);
                lengthSlider.setEnabled(false);
                startButton.setEnabled(false);
                startRace(frame, raceTrackPanel, infoPanel, linesSlider.getValue(), lengthSlider.getValue(), roadPiece, linesSlider, lengthSlider, startButton);
            }
        });

        JButton resetButton = new JButton("Reset Race");

        JLabel title = new JLabel("Num of Lanes:");
        controlsPanel.add(title);
        controlsPanel.add(linesSlider);
        title = new JLabel("Length of track:");
        controlsPanel.add(title);
        controlsPanel.add(lengthSlider);
        controlsPanel.add(startButton);
        frame.add(controlsPanel, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.VERTICAL;

        frame.add(infoPanel, constraints);
        /* Controls End */

        frame.pack();
        frame.setMinimumSize(new Dimension(frame.getWidth(), frame.getHeight()));
        frame.setMaximumSize(new Dimension(frame.getWidth(), frame.getHeight()));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
