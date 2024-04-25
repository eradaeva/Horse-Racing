package Part1;
import java.util.concurrent.TimeUnit;
import java.lang.Math;

/**
 * A three-horse race, each horse running in its own lane
 * for a given distance
 * 
 * @author McFarewell
 * @version 1.0
 */
public class Race
{
    private int raceLength;
    private final int maxHorses = 15;
    private Horse[] horses = new Horse[maxHorses];

    /**
     * Constructor for objects of class Race
     * Initially there are no horses in the lanes
     * 
     * @param distance the length of the racetrack (in metres/yards...)
     */
    private Race(int distance)
    {
        // initialise instance variables
        raceLength = distance;
    }

    public static Race createRace(int distance) {
        if (distance < 2) {
            return null;
        }
        return new Race(distance);
    }
    
    /**
     * Adds a horse to the race in a given lane
     * 
     * @param theHorse the horse to be added to the race
     * @param laneNumber the lane that the horse will be added to
     */
    public void addHorse(Horse theHorse, int laneNumber)
    {
        horses[laneNumber-1] = theHorse;

    }
    
    /**
     * Start the race
     * The horse are brought to the start and
     * then repeatedly moved forward until the 
     * race is finished
     */
    public void startRace()
    {
        boolean hasHorces = false;
        for (int i = 0; i < maxHorses; i++) {
            if (horses[i] != null) {
                hasHorces = true;
                break;
            }
        }

        if (!hasHorces) {
            System.out.println("Error: no horses added.");
            return;
        }

        if (this.raceLength <= 0) {
            System.out.println("Error: distance of the race is incorrect");
            return;
        }

        //declare a local variable to tell us when the race is finished
        boolean finished = false;
        boolean anyAlive = true;
        Horse winner = null;
        
        //reset all the lanes (all horses not fallen and back to 0). 
        for (int i = 0; i < maxHorses; i++) {
            if (horses[i] != null) horses[i].goBackToStart();
        }
                      
        while (!finished && anyAlive)
        {
            anyAlive = false;
            //move each horse
            for (int i = 0; i < maxHorses; i++) {
                if (horses[i] != null) {
                    if(!horses[i].hasFallen()) anyAlive = true;
                    moveHorse(horses[i]);
                } 
            }
                        
            //print the race positions
            printRace();
            
            //if any of the three horses has won the race is finished
            for (int i = 0; i < maxHorses; i++) {
                if (horses[i] != null && raceWonBy(horses[i])) {
                    finished = true;
                    winner = horses[i];
                }
            }
           
            //wait for 100 milliseconds
            try{ 
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(Exception e){}
        }

        double confidence;
        if (finished) {
            System.out.println("And the winner is " + winner.getName().toUpperCase());
            confidence = winner.getConfidence();
            confidence *= 1.1;
            if (confidence > 1) confidence = 1.0;
            winner.setConfidence(confidence);

            for (int i = 0; i < maxHorses; i++) {
                if (horses[i] != null && horses[i] != winner) {
                    confidence = horses[i].getConfidence();
                    confidence /= 1.2;
                    horses[i].setConfidence(confidence);
                }
            }

        } else {
            System.out.println("All horses failed. No winner this time.");
            for (int i = 0; i < maxHorses; i++) {
                if (horses[i] != null) {
                    confidence = horses[i].getConfidence();
                    confidence /= 1.05;
                    horses[i].setConfidence(confidence);
                }
            }
        }
    }
    
    /**
     * Randomly make a horse move forward or fall depending
     * on its confidence rating
     * A fallen horse cannot move
     * 
     * @param theHorse the horse to be moved
     */
    private void moveHorse(Horse theHorse)
    {
        //if the horse has fallen it cannot move, 
        //so only run if it has not fallen
        
        if  (!theHorse.hasFallen())
        {
            //the probability that the horse will move forward depends on the confidence;
            if (Math.random() < theHorse.getConfidence())
            {
               theHorse.moveForward();
            }
            
            //the probability that the horse will fall is very small (max is 0.1)
            //but will also will depends exponentially on confidence 
            //so if you double the confidence, the probability that it will fall is *2
            if (Math.random() < (0.1*theHorse.getConfidence()*theHorse.getConfidence()))
            {
                theHorse.fall();
            }
        }
    }
        
    /** 
     * Determines if a horse has won the race
     *
     * @param theHorse The horse we are testing
     * @return true if the horse has won, false otherwise.
     */
    private boolean raceWonBy(Horse theHorse)
    {
        if (theHorse.getDistanceTravelled() == raceLength)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /***
     * Print the race on the terminal
     */
    private void printRace()
    {
        System.out.print('\u000C');  //clear the terminal window
        
        multiplePrint('=',raceLength+3); //top edge of track
        System.out.println();

        for (int i = 0; i < maxHorses; i++) {
            if (horses[i] != null) {
                printLane(horses[i]);
                System.out.println();
            }
        }

        multiplePrint('=',raceLength+3); //bottom edge of track
        System.out.println();    
    }
    
    /**
     * print a horse's lane during the race
     * for example
     * |           X                      |
     * to show how far the horse has run
     */
    private void printLane(Horse theHorse)
    {
        //calculate how many spaces are needed before
        //and after the horse
        int spacesBefore = theHorse.getDistanceTravelled();
        int spacesAfter = raceLength - theHorse.getDistanceTravelled();
        
        //print a | for the beginning of the lane
        System.out.print('|');
        
        //print the spaces before the horse
        multiplePrint(' ',spacesBefore);
        
        //if the horse has fallen then print dead
        //else print the horse's symbol
        if(theHorse.hasFallen())
        {
            System.out.print('X');
        }
        else
        {
            System.out.print(theHorse.getSymbol());
        }
        
        //print the spaces after the horse
        multiplePrint(' ',spacesAfter);
        
        //print the | for the end of the track
        System.out.print('|');
        System.out.print(' ');
        System.out.print(theHorse.getName().toUpperCase());
        System.out.print("(Current confidence: " + (double)Math.round(theHorse.getConfidence() * 100.0) / 100.0 + ")");
    }
        
    
    /***
     * print a character a given number of times.
     * e.g. printmany('x',5) will print: xxxxx
     * 
     * @param aChar the character to Print
     */
    private void multiplePrint(char aChar, int times)
    {
        int i = 0;
        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }
    }
}
