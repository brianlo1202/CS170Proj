import java.util.*;

public class Location {
    String name;
    boolean isHome;
    ArrayList<Location> children; // is empty if no children
    HashMap<String, Float> edgeLengths; //name of child mapped to edge len

    boolean isClusterCenter;
    boolean visited;
    ArrayList<Student> studentsDroppedOffHere; //case is center

    public Location() {

        isHome = false;
        children = new ArrayList<Location>();
        edgeLengths = new HashMap<String, Float>();
        isClusterCenter = false;
        visited = false;
        studentsDroppedOffHere = new ArrayList<Student>();
    }

    public String toString() {
        return name;
    }

    public double Dikstras(Location goalLoc) {

        if (this == goalLoc) {
            return 0;
        }

        HashMap<String, Boolean> visitedTracker = new HashMap<String, Boolean>();
        HashMap<String, Float> cumCostTracker = new HashMap<String, Float>();
        HashMap<String, Float> heurTracker = new HashMap<String, Float>();

        LocationComparator pqComparator = new LocationComparator(cumCostTracker, heurTracker);
        PriorityQueue<Location> pq = new PriorityQueue<Location>(4, pqComparator);
        pq.add(this);
        visitedTracker.put(this.name, true);
        cumCostTracker.put(this.name, 0f);

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

    public LinkedList<Location> DikstrasPath(Location goalLoc) {
        LinkedList<Location> path = new LinkedList<Location>();

        if (this == goalLoc) {
            return path;
        }

        HashMap<String, Boolean> visitedTracker = new HashMap<String, Boolean>();
        HashMap<String, Float> cumCostTracker = new HashMap<String, Float>();
        HashMap<String, Float> heurTracker = new HashMap<String, Float>();
        HashMap<String, Location> prevLocTracker = new HashMap<String, Location>();

        LocationComparator pqComparator = new LocationComparator(cumCostTracker, heurTracker);
        PriorityQueue<Location> pq = new PriorityQueue<Location>(4, pqComparator);
        pq.add(this);
        visitedTracker.put(this.name, true);
        cumCostTracker.put(this.name, 0f);

        while (!pq.isEmpty()) {
            Location currentLoc = pq.remove();
            String currentLocName = currentLoc.name;

            if (currentLocName.equals(goalLoc.name)) {

                while (currentLoc != null) {
                    Location nextLoc = prevLocTracker.getOrDefault(currentLoc.name, null);
                    if (nextLoc != null) { // don't need say move to starting point; already there
                        path.addFirst(currentLoc);
                    }
                    currentLoc = nextLoc;
                }

                return path;
            }

            for (Location child: currentLoc.children) {
                if (!visitedTracker.getOrDefault(child.name, false)) {

                    prevLocTracker.put(child.name, currentLoc);

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
        return path; //shouldn't happen, case city no exist
    }

}
