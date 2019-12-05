import java.util.*;

public class Location {
    String name;
    boolean isHome;
    ArrayList<Location> children; // is empty if no children
    HashMap<String, Float> edgeLengths; //name of child mapped to edge len

    boolean isClusterCenter;
    ArrayList<Location> nonCentersAssociatedWithMe; //case is center

    public Location() {

        isHome = false;
        children = new ArrayList<Location>();
        edgeLengths = new HashMap<String, Float>();
        isClusterCenter = false;
        nonCentersAssociatedWithMe = new ArrayList<Location>();
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

}
