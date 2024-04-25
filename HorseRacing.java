import java.util.Random;
import java.util.Scanner;

import Part1.*;
import Part2.RaceGui;

public class HorseRacing {
    static public void main(String[] a) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        Horse horse;
        char[] symbols = new char[]{
            '\u265A', '\u265B', '\u265C', '\u265D', '\u265E', '\u265F',
            '\u2660', '\u2661', '\u2662', '\u2663', '\u2664', '\u2665'
        };

        System.out.println("Choose the version:");
        System.out.println("1. Textual UI");
        System.out.println("2. Graphical UI");

        int choice = Integer.parseInt(scanner.nextLine());

        if (choice == 1) {
            System.out.print("Enter race distance: ");
            int distance = Integer.parseInt(scanner.nextLine());

            System.out.print("Choose number of horses: ");
            int horsesNum = Integer.parseInt(scanner.nextLine());

            Race race = Race.createRace(distance);
            for (int i = 1; i <= horsesNum; i++) {
                horse = Horse.createHorse(
                symbols[random.nextInt(symbols.length)], 
                "Horse " + i, 
                random.nextDouble(0.1, 1)
            );

                race.addHorse(horse, i);
            }

            race.startRace();

            System.out.println("Another round? (y/n)");
            String input = scanner.nextLine();
            while (!input.equals("n")) {
                if (input.equals("y")) {
                    race.startRace();
                    System.out.println("Another round? (y/n)");
                    input = scanner.nextLine();
                } else {
                    System.out.println("\nError: the answer must be either 'y' or 'n'. Try again.");
                    System.out.println("Another round? (y/n)");
                    input = scanner.nextLine();
                }
            }
        } else if (choice == 2) {
            RaceGui.startRaceGUI();
        } else {
            System.out.println("Error: incorrect choice");
        }

        scanner.close();
    }
}
