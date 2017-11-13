import java.util.ArrayList;
import java.util.Random;

/**
 * Class representing a state of the game
 *
 * State is represented as a string, cost is the number of moves to get to the state
 */

public class State {
    private int[][] state;
    private int n;
    private int cost;
    private Position indexOfEmpty;
    private State parentState;
    private State goalState;

    /**
     * Constructor from 2D array
     *
     * @param state       The state as a 2d array
     * @param n           The size of the puzzle side
     * @param cost        The cost of the path to this state
     * @param parentState The parent state of this state
     */

    public State (int[][] state, int n, int cost, State parentState) {
        if (state.length != n || state[0].length != n)
            throw new IllegalArgumentException("invalid state, size not compatible");

        this.state = state;
        this.n = n;
        this.cost = cost;
        this.indexOfEmpty = findEmpty(state);
        this.parentState = parentState;
        this.goalState = null;
    }

    /**
     * Constructor from String
     *
     * @param state            The state as a string, separated by "-"
     * @param n                The size of the puzzle side
     * @param cost            The cost of the path to this state
     * @param parentState	The parent state of this state
     */

    public State (String state, int n, int cost, State parentState){
        int[][] tab = new int[n][n];

        String[] oneDimArray = state.split("-");
        if (oneDimArray.length != Math.pow(n,2))
            throw new IllegalArgumentException("invalid state, size not compatible");

        for (int i = 0; i < oneDimArray.length; i++) {
            tab[i / n][i % n] = Integer.valueOf(oneDimArray[i]);
        }

        this.state = tab;
        this.n = n;
        this.cost = cost;
        this.indexOfEmpty = findEmpty(tab);
        this.parentState = parentState;
        this.goalState = null;
    }

    /**
     * Constructor from array, implementing goalState here
     *
     * @param state            The state as a 2d array
     * @param n                The size of the puzzle side
     * @param cost            The cost of the path to this state
     * @param parentState    The parent state of this state
     * @param goalState		The goalState of this puzzle
     */

    public State (int[][] state, int n, int cost, State parentState, State goalState) {
        this(state, n, cost, parentState);
        this.goalState = goalState;
    }

    /**
     *
     * @param tab          The State as a 2d array
     * @return             Position(0,0) if tab[i][j] == 0, null otherwise
     */

    private static Position findEmpty (int[][] tab) {
        // We loop into the 2d array (i and j)
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[i].length; j++) {
                // In the first iteration we return a new Position(0,0)
                if (tab[i][j] == 0) {
                    return new Position(i, j);
                }
            }
        }
        // If tab[i][j] != 0, we return null
        return null;
    }

    /**
     * There are at least 2 successors, and at most 4, for each state
     *
     * @return An ArrayList of the states that are successors to this state instance
     */

    public ArrayList<State> successors () {
        // We create an ArrayList of States
        ArrayList<State> successors = new ArrayList<>();

        // We create a state called temp
        State temp;

        // If we can move to the left the actual State
        if ((temp = this.moveLeft()) != null) {
            // We add the State temp to the ArrayList of successors
            successors.add(temp);
        }

        // If we can move up the actual State
        if ((temp = this.moveUp()) != null) {
            // We add the State temp to the ArrayList of successors
            successors.add(temp);
        }

        // If we can move to the right the actual State
        if ((temp = this.moveRight()) != null) {
            // We add the State temp to the ArrayList of successors
            successors.add(temp);
        }

        // If we can move down the actual State
        if ((temp = this.moveDown()) != null) {
            // We add the State temp to the ArrayList of successors
            successors.add(temp);
        }

        // And the we return the ArrayList of successors
        return successors;
    }

    /**
     *
     * @return The state obtained by moving the empty case to the left, null if it doesn't exist
     */
    private State moveLeft () {
        // If the Position(i,j) get j == 0
        // We return null
        if (this.indexOfEmpty.getJ() == 0)
            return null;

        // Else we return the new State with the move to the left with j-1
        return swapEmptyWith(new Position(this.indexOfEmpty.getI(), this.indexOfEmpty.getJ() - 1));
    }

    /**
     *
     * @return The state obtained by moving the empty case to the right, null if it doesn't exist
     */
    private State moveRight () {
        // If the Position(i,j) get j == n-1
        // We return null
        if (this.indexOfEmpty.getJ() == n - 1)
            return null;

        // Else we return the new State with the move to the right with j+1
        return swapEmptyWith(new Position(this.indexOfEmpty.getI(), this.indexOfEmpty.getJ() + 1));
    }

    /**
     *
     * @return The state obtained by moving the empty case up, null if it doesn't exist
     */
    private State moveUp () {
        // If the Position(i,j) get i == 0
        // We return null
        if (this.indexOfEmpty.getI() == 0)
            return null;

        // Else we return the new State with the move up with i-1
        return swapEmptyWith(new Position(this.indexOfEmpty.getI() - 1, this.indexOfEmpty.getJ()));
    }

    /**
     *
     * @return The state obtained by moving the empty case down, null if it doesn't exist
     */
    private State moveDown () {
        // If the Position(i,j) get i == n - 1
        // We return null
        if (this.indexOfEmpty.getI() == n - 1)
            return null;

        // Else we return the new State with the move down with i+1
        return swapEmptyWith(new Position(this.indexOfEmpty.getI() + 1, this.indexOfEmpty.getJ()));
    }

    /**
     *
     * @param obj    The object to check against
     * @return True if the state are equals (we talk about the board, not the cost or anything else), false otherwise
     */
    @Override
    public boolean equals (Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    /**
     *
     * @param newIndex    The index where to put the empty case
     * @return The state obtained by swapping the empty case with the case at newIndex
     */
    private State swapEmptyWith (Position newIndex) {
        // We create a new 2d array
        int[][] newState = new int[n][n];

        // We loop into the 2d array
        for (int i = 0; i < this.state.length; i++) {
            for (int j = 0; j < this.state[i].length; j++) {
                // We put i and j values of the current state into a newState
                newState[i][j] = this.state[i][j];
            }
        }

        // We put into the newState[][] the Position of the parameter newIndex.getI() and newIndex.getJ()
        newState[this.indexOfEmpty.getI()][this.indexOfEmpty.getJ()] = newState[newIndex.getI()][newIndex.getJ()];
        // We assign to the newStatePosition(newIndex.getI(),newIndex.getJ()) = Position(0,0)
        newState[newIndex.getI()][newIndex.getJ()] = 0;

        /* Loop just to know what's going on on the newState variable
        System.out.print("SwapEmptyWith Function\n");
        System.out.print("Double loop for newState\n");
        for (int i = 0; i < this.state.length; i++) {
            for (int j = 0; j < this.state[i].length; j++) {
                System.out.print(newState[i][j]+"\n");
            }
        }*/

        // We return a new State with the 2d array newState
        return new State(newState, this.n, this.cost + 1, this, goalState);
    }

    /**
     *
     * @return	The state as a 2d array
     */
    public int[][] getState () {
        return state;
    }

    /**
     *
     * @return The size of the side of the puzzle, n
     */
    public int getN () {
        return n;
    }

    /**
     *
     * @return The cost of the path to this state
     */
    public int getCost () {
        return cost;
    }

    /**
     *
     * @return The hashcode, which is the hashcode of the string of all the cases concatenated
     */
    @Override
    public int hashCode () {
        String str ="";
        for (int i = 0; i < this.state.length; i++) {
            for (int j = 0; j < this.state[i].length; j++) {
                str += String.valueOf(this.state[i][j]);
            }
        }
        return str.hashCode();
    }

    /**
     *
     * @return The array representation for output
     */
    @Override
    public String toString () {
        String str = "";

        for (int i = 0; i < this.state.length; i++) {
            for (int j = 0; j < this.state[i].length; j++) {
                if (this.state[i][j] == 0)
                    str += "-";
                else
                    str += this.state[i][j];

                if (j != n - 1)
                    str += "\t";

            }
            str += "\n";
        }

        return str;
    }

    /**
     *
     * @return The parent of state of this state instance
     */
    public State getParentState () {
        return parentState;
    }

    /**
     * @param n The size of the state we want to get
     * @return The perfect state, that is the state where you go from 1 to (n^2)-1 with the empty case on bottom right
     */
    public static State getPerfectState (int n) {
        int[][] goal = new int[n][n];

        // Perfect final tab (12345678-)
        for (int i = 0; i < Math.pow(n, 2) - 1; i++) {
            goal[i / n][i % n] = i + 1;
        }
        goal[n - 1][n - 1] = 0;

        return new State(goal, n, 0, null);
    }

    /**
     *
     * @param n    The size of the state we want to get
     * @return	A random state of that size
     */
    public static State getRandomState (int n) {
        int[][] newState = new int[n][n];
        Random rand = new Random();
        ArrayList<Integer> values = new ArrayList<>();

        // ArrayList of the possible values (012345678)
        for (int i = 0; i < Math.pow(n, 2); i++) {
            values.add(i);
        }

        // Loop to get a random 2d array of random values with no repetition
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int index = rand.nextInt(values.size());
                //System.out.print("i: "+i+" - j: "+j+" - index: "+index+"\n");
                int temp = values.get(index);
                //System.out.print("i: "+i+" - j: "+j+" - temp: "+temp+"\n");
                values.remove(index);
                newState[i][j] = temp;
            }
        }

        // We return a State with the new random State
        return new State(newState, n, 0, null);
    }

    /**
     * Heuristic method that represents the manhattan distance to the final solution
     *
     * @return	The manhattan distance to the solution
     */

    public int getManhattanDistance(){
        // We initialize the distance at 0
        int distance = 0;

        // Loop on the length of the state
        for (int i = 0; i < this.state.length; i++) {
            // Loop on the length of the elements on state
            for (int j = 0; j < this.state[i].length; j++) {
                // We create a position goalPos who correspond to the final Position of i and j
                Position goalPos = getPositionInGoalState(state[i][j]);
                // We add the distance in the operation : abs(i-goalPos.getI())+abs(j-goalPos.getJ())
                distance += Math.abs(i-goalPos.getI())+Math.abs(j-goalPos.getJ());
            }
        }
        // We return the final distance of all the path
        System.out.print("Distance of manhattanDistance: "+distance);
        return distance;
    }

    /**
     * Heuristic that represents the number of tiles misplaced
     *
     * @return	The number of tiles that are not in the correct position
     */
    public int getMisplacedTiles(){
        // We initialize the value at 0
        int value = 0;

        // Loop on the length of the state
        for (int i = 0; i < this.state.length; i++) {
            // Loop on the length of the elements on state
            for (int j = 0; j < this.state[i].length; j++) {
                // We create a position goalPos who correspond to the final Position of i and j
                Position goalPos = getPositionInGoalState(state[i][j]);
                // If the goalPos of i isn't the current i or the goalPos of j isn't the current j
                if (goalPos.getI() != i || goalPos.getJ() != j){
                    // We add 1 to the value
                    value++;
                }
            }
        }
        // We print the value of misplaced tiles and return it
        System.out.print("Value of misplaced tiles: "+value);
        return value;
    }

    /**
     *
     * @param number    The number we're looking
     * @return The position of that number in the goalState
     */
    private Position getPositionInGoalState (int number) {
        // Loop into the size of the puzzle into a 2d array
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // If the goalState correspond to the state expected
                if (goalState.getState()[i][j] == number)
                    // We return the position
                    return new Position(i, j);
            }
        }
        return null;
    }

    /**
     *
     * @param goalState	The goalState we want to set
     */
    public void setGoalState (State goalState) {
        this.goalState = goalState;
    }

    /**
     *
     * @return	The goalState of this puzzle
     */
    public State getGoalState () {
        return goalState;
    }




}
