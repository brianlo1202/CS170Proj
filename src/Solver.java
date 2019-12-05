import java.io.BufferedWriter;
import java.io.*;
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

    public ArrayList<Location> clusterCenters;

    public ArrayList<Action> solveViaKClustering(ProblemCase prob) throws IOException {
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

        int numClusterCenters = prob.students.size() / 10; //k
        // case # students less than 10
        if (numClusterCenters == 0) {
            numClusterCenters = 3;
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

        //associate all students with center
        for (Student s: prob.students) {
            Location closestCenter = getClosestCenter(s.home, clusterCenters);
            closestCenter.studentsDroppedOffHere.add(s);

        }

        ArrayList<Action> plan = new ArrayList<Action>();

        /**
         * make drop offs at current center
         * go to next center
         */
        Location currentCenter = prob.startLoc;
        for (int currentCenterNum = 1; currentCenterNum <= numClusterCenters; currentCenterNum++) {
            currentCenter.visited = true;
            for (Student s : currentCenter.studentsDroppedOffHere) {
                Action_DropOff a = new Action_DropOff();
                a.student = s;
                plan.add(a);
            }

            //det next loc to travel to
            Location nextCenter = null;
            if (currentCenterNum != numClusterCenters) {
                nextCenter = getClosestCenter(currentCenter, clusterCenters);
            } else {
                nextCenter = prob.startLoc;
            }

            LinkedList<Location> pathToNextCenter = currentCenter.DikstrasPath(nextCenter);

            for (Location l: pathToNextCenter) {
                Action_Move a = new Action_Move();
                a.locationToMoveTo = l;
                plan.add(a);
            }

            currentCenter = nextCenter;

        }

        actionPlanToFile(prob, plan, clusterCenters);

        return plan;
    }

    private Location getClosestCenter(Location l, ArrayList<Location> clusterCenters) {
        Location currentClosestCenter = null;
        double currentClosestCenterDist = Double.MAX_VALUE;

        for (Location center: clusterCenters) {

            if (center.visited) { //important when path planning
                continue;
            }

            double dist = l.Dikstras(center);
            if (dist < currentClosestCenterDist) {
                currentClosestCenter = center;
                currentClosestCenterDist = dist;

                if (center == l) { //if home is center
                    break;
                }
            }
        }

        return currentClosestCenter;
    }

    /**
     * return Loc furthest from all current Cluster Centers (maximizes min dist)
     * @return
     */
    private Location genNextClusterCenter(ProblemCase prob, ArrayList<Location> currentClusterCenters) {
        /**
         * for all non center locs:
         *  calc sum of dists from all centers
         *  return loc w/ max of this val
         */
        Location currentFarthestLoc = null;
        double currentBestMaxMinDist = Double.MIN_VALUE;

        for (Location l: prob.locations) {
            if (!l.isClusterCenter) {

                //add up all dists to all centers
                double currentMinDist = Double.MAX_VALUE;
                for (Location center: currentClusterCenters) {
                    double distToCenter = l.Dikstras(center);
                    //TODO cache already calculated dists
                    if (distToCenter < currentMinDist) {
                        currentMinDist = distToCenter;
                    }
                }

                if (currentMinDist > currentBestMaxMinDist) {
                    currentFarthestLoc = l;
                    currentBestMaxMinDist = currentMinDist;
                }
            }
        }

        return currentFarthestLoc;
    }

    public void actionPlanToFile(ProblemCase prob, ArrayList<Action> plan,
                                 ArrayList<Location> clusterCenters) throws IOException{

        String path = prob.startLoc.name;
        String dropOffsList = "";

        for (Action a: plan) {
            if (a instanceof Action_Move) {
                path += " " + ((Action_Move)a).locationToMoveTo.name;
            }
        }
        path += '\n';

        int numDropOffLocs = 0;
        for (Location center: clusterCenters) {
            if (!center.studentsDroppedOffHere.isEmpty()) { //actually use this loc

                numDropOffLocs++;

                dropOffsList += center.name;
                for (Student s : center.studentsDroppedOffHere) {
                    dropOffsList += " " + s.home.name;
                }
                dropOffsList += '\n';
            }
        }

        String numDropOffLocsText = numDropOffLocs + "" + '\n';

        //System.out.print(path);
        //System.out.print(numDropOffLocs);
        //System.out.print(dropOffsList);


        String str = path + numDropOffLocsText + dropOffsList;
        String fileName = prob.fileName.substring(0, prob.fileName.length() - 3) + ".out";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(str);
        writer.close();


    }

    public static void main(String[] args) throws IOException {
        Solver solver = new Solver();
        int taskNum = 1;

        String folderPath = System.getProperty("user.dir") + "/input/";
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            String fileName = listOfFiles[i].getName();
            if (fileName.substring(fileName.length() - 3).equals(".in")) {
                System.out.println("working on input " + taskNum + ": " + fileName);
                taskNum++;
                ProblemCase prob = new ProblemCase(fileName);
                solver.solveViaKClustering(prob);
            }
        }
    }
}
