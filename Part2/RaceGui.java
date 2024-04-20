package Part2;

import Part1.Horse;
import java.awt.*;

import javax.swing.*;

public class RaceGui {
    public static void launchApp() {
        final int lanes = 3;
        final int length = 8;
        final int roadSideLength = 80;
        final Dimension raceTrackDimension = new Dimension(length * roadSideLength, lanes * roadSideLength);

        JFrame frame = new JFrame("Horse Racing");
        frame.setLayout(new GridBagLayout());

        /* Road Pieces Start */
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE; // Do not resize the component
        constraints.gridx = 0;
        constraints.gridy = 0;

        JPanel raceTrackPanel = new JPanel(new GridLayout(lanes, length, 0, 0));
        raceTrackPanel.setMinimumSize(raceTrackDimension);
        raceTrackPanel.setPreferredSize(raceTrackDimension);
        raceTrackPanel.setMaximumSize(raceTrackDimension);


        ImageIcon roadPiece = new ImageIcon("Part2/Sprites/roadPiece.png");
        roadPiece.setImage(roadPiece.getImage().getScaledInstance(roadSideLength, roadSideLength, Image.SCALE_DEFAULT));

        JLabel roadLabel;
        for (int i = 0; i < lanes; i++) {
            for (int j = 0; j < length; j++) {
                roadLabel = new JLabel(roadPiece);
                raceTrackPanel.add(roadLabel);
            }
        }

        frame.add(raceTrackPanel, constraints);
        /* Road Pieces End */

        /* Controls */
        
        /* Road Pieces End */

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
