import java.util.*;

public class Location {
    String name;
    ArrayList<Location> children; // is empty if no children

    HashMap<String, Float> edgeLengths; //name of child mapped to edge len

    public Location() {

        children = new ArrayList<Location>();
        edgeLengths = new HashMap<String, Float>();
    }

    public String toString() {
        return name;
    }

}
