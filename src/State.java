import java.util.*;

public class State {
    //describes result of action

    static int blah = 0;

    State prevState;
    Action actionThatLedToHere;
    float currentCost; //includes completion of the action that led to this state
    Location currentLocation;
    ArrayList<Student> studentsInCar;

    public State successor(Action a) {
        State nextState = new State();
        nextState.prevState = this;
        nextState.actionThatLedToHere = a;

        if (a instanceof Action_Move) {
            Location nextLoc = ((Action_Move)a).locationToMoveTo;
            float distMoved = currentLocation.edgeLengths.get(nextLoc.name);
            float cost = 2f/3f * distMoved;
            nextState.currentCost = currentCost + cost;

            nextState.currentLocation = nextLoc;
            nextState.studentsInCar = (ArrayList<Student>)studentsInCar.clone();
        } else { //is drop off action
            Location studentHome = ((Action_DropOff)a).student.home;
            float shortestDistToStudentsHome = Dikstras(studentHome); //TODO
            float cost = shortestDistToStudentsHome * 1f;

            nextState.currentCost = currentCost + cost;
            nextState.currentLocation = currentLocation;

            Student droppedStudent = ((Action_DropOff)a).student;
            nextState.studentsInCar = (ArrayList<Student>)studentsInCar.clone();
            nextState.studentsInCar.remove(droppedStudent);
        }
        return nextState;
    }

    public float Dikstras(Location goalLoc) {

        HashMap<String, Boolean> visitedTracker = new HashMap<String, Boolean>();
        HashMap<String, Float> cumCostTracker = new HashMap<String, Float>();
        HashMap<String, Float> heurTracker = new HashMap<String, Float>();

        LocationComparator pqComparator = new LocationComparator(cumCostTracker, heurTracker);
        PriorityQueue<Location> pq = new PriorityQueue<Location>(4, pqComparator);
        pq.add(this.currentLocation);
        visitedTracker.put(this.currentLocation.name, true);
        cumCostTracker.put(this.currentLocation.name, 0f);

        while (!pq.isEmpty()) {
            Location currentLoc = pq.remove();
            String currentLocName = currentLoc.name;

            if (currentLocName.equals(goalLoc.name)) {
                return cumCostTracker.get(currentLocName);
            }

            for (Location child: currentLoc.children) {
                if (!visitedTracker.getOrDefault(child.name, false)) {

                    //mark as visited
                    visitedTracker.put(child.name, true);

                    //cumCost
                    float parentCumCost = cumCostTracker.get(currentLocName);
                    float distParentToChild = currentLoc.edgeLengths.get(child.name);
                    cumCostTracker.put(child.name, parentCumCost + distParentToChild);

                    pq.add(child);
                }
            }
        }
        return Float.MAX_VALUE; //shouldn't happen, case city no exist
    }

    public ArrayList<Action> nextPossibleActions() {
        ArrayList<Action> actionList = new ArrayList<Action>();

        Location prevLoc = null;
        if (prevState != null) {
            prevLoc = prevState.currentLocation;
        }

        for (Location child: currentLocation.children) {

            if (child != prevLoc && !child.children.isEmpty()) { //don't go backwards and don't go into dead ends
                Action_Move a = new Action_Move();
                a.locationToMoveTo = child;

                actionList.add(a);
            }
        }
        for (Student s: studentsInCar) {
            Action_DropOff a = new Action_DropOff();
            a.student = s;

            actionList.add(a);
        }

        Collections.shuffle(actionList);


        return actionList;
    }

    public String toString() {
        if (actionThatLedToHere != null) {
            return actionThatLedToHere.toString() + " | "
                    + prevState.currentLocation.toString() + " to "
                    + currentLocation.toString();
        }
        return "start @ " + currentLocation.toString();
    }

    public String recToString() {
        if (prevState == null) {
            return "start at " + currentLocation.toString();
        } else {
            return prevState.recToString() + ", " + actionThatLedToHere.toString();
        }
    }

}
