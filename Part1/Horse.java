package Part1;
/**
 * Horse Class represents a horse in race. 
 * Contains all the information, like name, symbol, distance travelled, etc.
 * 
 * @author Ekaterina Radaeva
 * @version 1.0
 */
public class Horse
{
    //Fields of class Horse
    private String name;
    private char symbol;
    private boolean eliminated;
    private double confidence;
    private int distance;
    
      
    //Constructor of class Horse
    /**
     * Constructor for objects of class Horse
     */
    private Horse(char horseSymbol, String horseName, double horseConfidence)
    {
       this.confidence = horseConfidence;
       this.name = horseName;
       this.symbol = horseSymbol;
       this.distance = 0;
       this.eliminated = false;
    }
    
    public static Horse createHorse(char horseSymbol, String horseName, double horseConfidence) {
        if (horseConfidence > 1) return null;
        if (horseConfidence < 0) return null;
        if (horseName.equals("")) return null;

        Horse horse = new Horse(horseSymbol, horseName, horseConfidence);
        return horse;
    }
    
    //Other methods of class Horse
    public void fall()
    {
    this.eliminated = true;
    }
    
    public double getConfidence()
    {
        return this.confidence;
    }
    
    public int getDistanceTravelled()
    {
        return this.distance;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public char getSymbol()
    {
        return this.symbol;
    }
    
    public void goBackToStart()
    {
        this.eliminated = false;
        this.distance = 0;
    }
    
    public boolean hasFallen()
    {
        return this.eliminated;
    }

    public void moveForward()
    {
        this.distance++;
    }

    public void setConfidence(double newConfidence)
    {
        if (newConfidence > 1) newConfidence = 1;
        if (newConfidence < 0) newConfidence = 0;
        this.confidence = newConfidence;
    }
    
    public void setSymbol(char newSymbol)
    {
        this.symbol = newSymbol;
    }
    
}
