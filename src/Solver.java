import java.io.FileNotFoundException;
import java.util.*;
public class Solver {
    public static String solve(ProblemCase prob) {

        //init start state
        State startState = new State();
        startState.prevState = null;
        startState.actionThatLedToHere = null;
        startState.currentCost = 0;
        startState.currentLocation = prob.startLoc;
        startState.studentsInCar = (ArrayList<Student>)prob.students.clone();

        Stack<State> stack = new Stack<State>();
        stack.push(startState);

        float currentBestCost = Float.MAX_VALUE;

        while (!stack.isEmpty()) {
            State currentState = stack.pop();

            if (currentState.currentCost > currentBestCost) {
                continue;
            }

            //if is sol, update best cost
            if (prob.goalTest(currentState) == true) {
                currentBestCost = currentState.currentCost;

                System.out.println("possible sol:");
                System.out.println(currentState.recToString());
                System.out.println("cost: " + currentState.currentCost);
                System.out.println("_________");
            } else {
                ArrayList<Action> nextPossibleActions = currentState.nextPossibleActions();
                for (Action a: nextPossibleActions) {
                    State corrState = currentState.successor(a);

                    //only explore route if its cost so far is still less than current best
                    if (corrState.currentCost < currentBestCost) {
                        stack.push(corrState);
                    }
                }
            }
        }

        return "I tried my best :(";
    }

    public static String solveViaKClustering(ProblemCase prob) {
        /**
         * 
         */
    }

    public static void main(String[] args) throws FileNotFoundException {
        ProblemCase prob1 = new ProblemCase("6.in");
        String result = solve(prob1);
        System.out.println(result);
    }
}
