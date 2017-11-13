import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class who contains the "main"
 * Solver who contains all of the algorithms of research
 */

public class Solver {
    private static final int MAX_TENT = 1000000;
    private static int count = 0;
    private static long startTime = 0;
    private static long endTime = 0;

    public static void main (String[] args) {
        if (args.length < 3){
            System.out.println("Use the program with these args");
            System.out.println("<Search type> <Initial State> <Size> <Goal State>");
            System.out.println();
            System.out.print("\t");
            System.out.println("<Search type>: blind, cachedBlind, manhattan, misplaced (last two are heuristics)");
            System.out.print("\t");
            System.out.println("<Initial State>: Write the cases from top to bottom, left to right, first one top left, " +
                    "last one bottom right, 0 for the empty, separated by '-' or use 'RANDOM'");
            System.out.print("\t");
            System.out.print("<Size>: 3 or 4 -> Depends of what you want");
            System.out.print("\t");
            System.out.println("<Goal State>: Same as <Initial State> but you can use GOAL " +
                    "to set the goal state as the perfect goal state for that size");
            return;
        }

        State initialState = null;
        State goalState = null;
        State finishedState = null;

        int size = 0;

        try {
            size = Integer.valueOf(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("The size param was not valid");
            e.printStackTrace();
            System.exit(1);
        }

        // Switch for initial State, if it's random, we generate it, otherwise we read from input
        switch (args[1]){
            case "RANDOM":
                initialState = State.getRandomGrid(size);
                System.out.print("\nInitialState: \n"+initialState+"\n");
                break;
            default:
                try {
                    initialState = new State(args[1], size, 0, null);
                } catch (IllegalArgumentException e) {
                    System.out.println("The input for initialState was not conform to the size");
                    e.printStackTrace();
                    System.exit(1);
                }
                break;
        }

        // Switch for the goal State
        switch (args[3]){
            case "RANDOM":
                goalState = State.getRandomGrid(size);
                break;
            case "GOAL":
                goalState = State.getPerfectGrid(size);
                break;
            default:
                try {
                    goalState = new State(args[3], size, 0, null);
                } catch (IllegalArgumentException e) {
                    System.out.println("The input for initialState was not conform to the size");
                    e.printStackTrace();
                    System.exit(1);
                }
                break;
        }

        // We set the goalState to the initialState
        initialState.setGoalState(goalState);

        // Switch for how to solve
        switch (args[0]){
            case "blind":
                startTime = System.nanoTime();
                finishedState = blindSolve(initialState,goalState,size,false);
                break;
            case "cachedBlind":
                startTime = System.nanoTime();
                finishedState = blindSolve(initialState,goalState,size,true);
                break;
            case "manhattan":
                startTime = System.nanoTime();
                finishedState = heuristicsSolve(initialState,goalState,size,true);
                break;
            case "misplaced":
                startTime = System.nanoTime();
                finishedState = heuristicsSolve(initialState,goalState,size,false);
                break;
            default:
                System.out.println("This type of search is invalid");
                System.exit(1);
        }

        endTime = System.nanoTime();
        Double timeInSeconds = ((endTime - startTime) / Math.pow(10, 9));

        if (finishedState != null) {
            if (!finishedState.equals(goalState)) {
                System.out.println("Unachieved - Path:");
                System.out.println(finishedState);
            } else {
                System.out.print("\nOK - Path to the result:\n");
                displayPath(finishedState);
            }
        }else{
            System.out.println("Unachieved");
            System.out.println(initialState);
        }

        System.out.println(timeInSeconds + " seconds to solve the Taquin with the "+args[0]+" method");
        System.out.println("We resolved it through " + count + " states");

    }

    /**
     * Method that implements a blindSearch to solve
     *
     * @param initialState The initialState of our puzzle
     * @param goalState    The state we want to achieve
     * @param size         The size of our puzzle
     * @param optimize     If we use a cache to store already visited states
     * @return The goalState created by the path we used, or null if there is no solution
     */

    private static State blindSolve(State initialState, State goalState, int size, boolean optimize){

        // Collection of State Elements
        LinkedBlockingQueue<State> queue = new LinkedBlockingQueue<>();

        State currentState;

        // HashSet is a HashMap who accepts null elements
        // Constructs a new, empty set; the backing HashMap instance has default initial capacity (16) and load factor (0.75)
        HashSet<State> visited = new HashSet<>();

        //We add the initial state in the LinkedBlockingQueue
        queue.add(initialState);

        // Loop while the LinkedBlockingQueue isn't empty (while initialState isn't empty)
        while (!queue.isEmpty()){
            // The method pool retrieves and removes the head of this queue, or returns null if this queue is empty
            // We put this element in the currentState variable
            currentState = queue.poll();
            count++; // We increment the count variable of 1

            // If the size is equal to 4 or even more and the size of the LinkedBlockingQueue is bigger than MAX_SIZE_QUEUE
            // We return the currentState (the head of the queue)
            if (size > 3 && queue.size()> MAX_TENT) {
                return currentState;
            }

            // If we set the boolean optimize at true
            if (optimize) {
                // If the HashSet doesn't contain the currentState
                if (!visited.contains(currentState)){
                    // We add it
                    visited.add(currentState);
                    // If the current state correspond to the goal state
                    if (currentState.equals(goalState))
                        // We return it
                        return currentState;
                    // If the current state not correspond to the goal state
                    else
                        // We add all the successors of the current state to the LinkedBlockingQueue
                        queue.addAll(currentState.successors());
                }
            }
            // If we set the boolean optimize at false
            else{
                // If the current state correspond to the goalState
                if (currentState.equals(goalState))
                    // We return it
                    return currentState;
                else
                    // Otherwise we add all the successors of the currentState to the LinkedBlockingQueue
                    queue.addAll(currentState.successors());
            }

        }

        return null;
    }

    /**
     * Method to display the path of the result, from initialState to goalState
     *
     * @param result	The result state
     */

    private static void displayPath(State result) {

        // If the result of what we found is null
        if (result == null){
            // We print it and we return nothing
            System.out.println("No path was found");
            return;
        }

        // The Stack class represents a last-in-first-out (LIFO) stack of objects
        // We create a Stack of the path
        Stack<State> path = new Stack<>();

        // We create a state which is the final state
        State temp = result;

        // Loop when the resultState isn't null
        while (temp!=null){
            // We push the state into the Stack
            path.push(temp);
            // We get the parent state of the final state
            temp = temp.getParentState();
        }

        // Loop when the Stack isn't empty
        // We print all the states who permits to acceed to the final path
        while (!path.isEmpty()){
            System.out.println();
            // We put in tempState the first element of the Stack and we print it
            State tempState = path.pop();
            System.out.println(tempState);
            System.out.println();
        }

        // We print the cost of the complete path of the result
        System.out.println("Cost of path: " + result.getCost());
    }

    /**
     * Heuristic search method to solve puzzle
     *
     * @param initialState    The initial state of our puzzle
     * @param goalState        The goal state of our puzzle
     * @param size            The size of the puzzle
     * @param manhattan        Boolean to tell if we use manhattan or misplaced tiles heuristics
     * @return The result state, obtained from the search
     */

    private static State heuristicsSolve(State initialState, State goalState, int size, boolean manhattan){

        // We create a PriorityQueue of states with the comparaison of two Integer who get the manhattan distance and the misplaced tiles
        PriorityQueue<State> priority = new PriorityQueue<>((o1, o2) -> {
            Integer heuristic1 = manhattan ? o1.getManhattanDistance() : o1.getMisplacedElements();
            Integer heuristic2 = manhattan ? o2.getManhattanDistance() : o2.getMisplacedElements();
            // We return the Integer code
            return heuristic1.compareTo(heuristic2);
        });

        // We create a currentState initialized at null
        State currentState = null;

        // We create an HashSet
        HashSet<State> visited = new HashSet<>();

        // We add in the PriorityQueue the initialState
        priority.add(initialState);

        // Loop when the PriorityQueue isn't empty
        while (!priority.isEmpty()) {
            // We retrieves and removes the head of this PriorityQueue and put it into the currentState
            currentState = priority.poll();
            // We add 1 at the count value
            count++;
            // If the current state is the goalState
            if (currentState.equals(goalState))
                // We return the currentState
                return currentState;

            // We create an ArrayList of successors by getting all the successors of the currentState
            ArrayList<State> succ = currentState.successors();

            // For each successors in the State Object
            for (State successor : succ) {
                // If the the HashSet of the visited states contains the successor and the the PriorityQueue doesn't contain the successor
                if (!visited.contains(successor) && !priority.contains(successor))
                    // We add to the PriorityQueue the successor
                    priority.add(successor);
            }
            // Finally we add to the HashSet the currentState
            visited.add(currentState);
        }

        // We return the currentState
        return currentState;
    }

}
