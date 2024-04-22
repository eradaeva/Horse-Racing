import java.util.Random;
import java.util.Scanner;

import Part1.*;

public class HorseRacing {
    static public void main(String[] a) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        Horse horse;

        System.out.print("Enter race distance: ");
        int distance = Integer.parseInt(scanner.nextLine());

        Race race = new Race(distance);
        for (int i = 1; i <= 3; i++) {
            horse = new Horse(
                String.valueOf(i).charAt(0), 
                "Horse " + i, 
                random.nextDouble()
            );

            race.addHorse(horse, i);
        }

        race.startRace();

        scanner.close();
    }
}
