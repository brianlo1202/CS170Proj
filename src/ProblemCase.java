import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;
import java.util.Scanner;

public class ProblemCase {
    String fileName;
    ArrayList<Location> locations;
    ArrayList<Student> students;
    Location startLoc;

    /**
     * fileName should be .in file as specified in proj specs
     * fileName should be put in src folder with this class
     * @param fileName
     * @throws FileNotFoundException
     */
    public ProblemCase (String fileName) throws FileNotFoundException {
        this.fileName = fileName;

        //set up scanner to read file

        String currentPath = System.getProperty("user.dir") + "/input/";
        File file = new File(currentPath + fileName);
        Scanner scan = new Scanner(file);

        int numLocs = Integer.parseInt(scan.nextLine().trim());
        int numHomes = Integer.parseInt(scan.nextLine().trim());
        String listOfLocNames = scan.nextLine();
        String listOfStudentHomeNames = scan.nextLine();
        String startLocName = scan.nextLine().trim();

        //init locations list
        //for helping init students
        HashMap<String, Location> nameLocationTracker = new HashMap<String, Location>();

        this.locations = new ArrayList<Location>();
        Scanner lineScan = new Scanner(listOfLocNames);
        while (lineScan.hasNext()) {
            String currentLocName = lineScan.next();
            Location l = new Location();
            l.name = currentLocName;

            //for helping init students
            nameLocationTracker.put(l.name, l);

            //set as startLoc if it is
            if (currentLocName.equals(startLocName)) {
                this.startLoc = l;
            }
            this.locations.add(l);
        }

        //init students list

        this.students = new ArrayList<Student>();
        lineScan = new Scanner(listOfStudentHomeNames);
        while (lineScan.hasNext()) {
            String currentLocName = lineScan.next();
            Student s = new Student();
            Location studentHome = nameLocationTracker.get(currentLocName);
            s.home = studentHome;
            studentHome.isHome = true;
            this.students.add(s);
        }

        //init children

        for (int currentLocI = 0; currentLocI < numLocs; currentLocI++) {
            Location currentLocation = this.locations.get(currentLocI);

            //init scanner for loc's matrix row
            String row = scan.nextLine();
            lineScan = new Scanner(row);

            //scan each loc's data in row
            for (int currentChildI = 0; currentChildI < numLocs; currentChildI++) {
                String dataChar = lineScan.next();
                if (!dataChar.equals("x")) {
                    Location currentChild = this.locations.get(currentChildI);
                    float edgeLen = Float.valueOf(dataChar.trim());

                    currentLocation.children.add(currentChild);
                    currentLocation.edgeLengths.put(currentChild.name, edgeLen);
                }
            }

        }

    }

    public boolean goalTest(State s) {
        return s.currentLocation == startLoc && s.studentsInCar.isEmpty();
    }

    public static void main (String[] args) throws FileNotFoundException {
        ProblemCase prob1 = new ProblemCase("input/50.in");
    }
}
