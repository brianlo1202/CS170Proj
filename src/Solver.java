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
         * pick #homes/10??? cluster centers
         * clusterCenter1 = startLoc
         * next clusters: loc furtherst from current centers
         *
         * put all centers in list
         *
         * for all non centers:
         *  associate with closest center
         *
         * action list:
         * at current cluster center, dep corr students
         * go to next cluster center, repeat
         */

        int numClusterCenters = prob.students.size() / 10;
        // case # students less than 10
        if (numClusterCenters == 0) {
            numClusterCenters = 2;
        }

        ArrayList<Location> clusterCenters = new ArrayList<Location>();

        //add first Cluster center
        prob.startLoc.isClusterCenter = true;
        clusterCenters.add(prob.startLoc);

        for (int currentNumClusterCenters = 1; currentNumClusterCenters < numClusterCenters; currentNumClusterCenters++) {
            Location newClusterCenter = genNextClusterCenter(prob, clusterCenters);
            newClusterCenter.isClusterCenter = true;
            clusterCenters.add(newClusterCenter);
        }

        //associate all homes with center
        for (Location l: prob.locations) {
            if (l.isHome) {
                Location currentClosestCenter = null;
                double currentClosestCenterDist = Double.MAX_VALUE;

                for (Location center: clusterCenters) {

                    double dist = l.Dikstras(center);
                    if (dist < currentClosestCenterDist) {
                        currentClosestCenter = center;
                        currentClosestCenterDist = dist;

                        if (center == l) { //if home is center
                            break;
                        }
                    }
                }

                currentClosestCenter.nonCentersAssociatedWithMe.add(l);
            }
        }

        return "I tried my best :(";
    }

    /**
     * return Loc furthest from all current Cluster Centers
     * @return
     */
    private static Location genNextClusterCenter(ProblemCase prob, ArrayList<Location> currentClusterCenters) {
        /**
         * for all non center locs:
         *  calc sum of dists from all centers
         *  return loc w/ max of this val
         */
        Location currentFarthestLoc = null;
        double currentLargestTotalDist = Double.MIN_VALUE;

        for (Location l: prob.locations) {
            if (!l.isClusterCenter) {

                //add up all dists to all centers
                double totalDist = 0;
                for (Location center: currentClusterCenters) {
                    double distToCenter = l.Dikstras(center);
                    //TODO cache already calculated dists
                    totalDist += distToCenter;
                }

                if (totalDist > currentLargestTotalDist) {
                    currentFarthestLoc = l;
                    currentLargestTotalDist = totalDist;
                }
            }
        }

        return currentFarthestLoc;
    }

    public static void main(String[] args) throws FileNotFoundException {
        ProblemCase prob1 = new ProblemCase("6.in");
        String result = solveViaKClustering(prob1);
        System.out.println(result);
    }
}
