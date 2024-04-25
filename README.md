# Horse Racing Game
## Introduction
The Horse Racing Game is a fun and interactive Java application that simulates horse racing competitions. Players can choose horses, place bets, and watch as the races unfold through both a command-line interface and a graphical user interface.

## Installation and Usage
To run the Horse Racing Game, ensure you have Java installed on your machine. You can download and install Java from [Oracle's official site](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).

1. Clone the repository or download the ZIP file.
2. Extract the contents to your preferred directory.
3. Compile the Java files using the following command:
```
javac HorseRacing.java
```
4. Run the game using the following command:
```
java HorseRacing
```

After that follow the instructions displayed in the Terminal.
The program will allow you to choose the version of the game (textual or visual).
In case, when textual is chosen, the following control over the game will be through the Terminal.
In case, when visual is chosen, the game will be controlled fully through the visiual interface. 

## Alternative Usage (Harder)
The classes can be used directly bypassing the main file and using the classes and assets provided directly. 

### Files required for textual version:
- Horse.java
- Race.java


### Public Interfaces
#### Horse.java (Part1.Horse)
Methods:
- fall(): Marks the horse as having fallen.
- getConfidence(): Returns the confidence level of the horse.
- getDistanceTravelled(): Returns the distance travelled by the horse in the race.
- getName(): Returns the name of the horse.
- getSymbol(): Returns the symbol representing the horse.
- goBackToStart(): Resets the horse's position to the start.
- hasFallen(): Checks if the horse has fallen.
- moveForward(int distance): Moves the horse forward by the specified distance.
- setConfidence(double confidence): Sets the horse's confidence level.
- setSymbol(char symbol): Sets the symbol representing the horse.
- createHorse(char horseSymbol, String horseName, double horseConfidence): Creates an instance of Horse. Returns null on incorrect parameters.
#### Race.java (Part1.Race)
Methods:
- addHorse(Horse horse): Adds a horse to the race.
- startRace(): Starts the race.
- createRace(int distance): Creates an instance of race.

#### Example of direct usage:
```
// Create instances of horses
Horse horse1 = Horse.createHorse('S', "The Great Horse", 0.75);
Horse horse2 =  Horse.createHorse('F', "The Greater Horse", 0.99);

// Create a race and add horses
Race race = Race.createRace(15);
race.addHorse(horse1);
race.addHorse(horse2);

// Start the race
race.startRace();
```

### Files required for textual version:
- Horse.java
- RaceGui.java
- Sprites in Part2

### Public Interfaces
#### RaceGui.java (Part2.RaceGui)
- startRaceGUI(): Starts the GUI version of the game

#### Example of direct usage:
```
RaceGui.startRaceGUI();
// Important!
// The files containing sprites for GUI version must have same relative direction from the main file, as in the original version.
```
