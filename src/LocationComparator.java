import java.util.*;

public class LocationComparator implements Comparator<Location> {

    HashMap<String, Float> cumCostTracker;
    HashMap<String, Float> heurTracker;

    public LocationComparator(HashMap<String, Float> cumCostTracker, HashMap<String, Float> heurTracker) {
        this.cumCostTracker = cumCostTracker;
        this.heurTracker = heurTracker;
    }

    public int compare(Location l1, Location l2) {
       //least cumCost + heuristic
        float loc1Val = cumCostTracker.get(l1.name);
        float loc2Val = cumCostTracker.get(l2.name);

        if (loc1Val < loc2Val) {
            return 1;
        } else {
            return -1;
        }
    }
}
