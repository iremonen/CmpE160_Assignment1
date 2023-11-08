//İrem Önen, 2022400279, 26.03.2023
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Irem_Onen{
    public static void main(String[] args) throws FileNotFoundException{
        //getting the user input, opening the file and handling the error if file does not exist
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the beginning point: ");
        String str1 = sc.next();
        System.out.print("Enter the destination: ");
        String str2 = sc.next();
        sc.close();
        File file = new File("src/coordinates.txt");
        if(!file.exists()){
            System.out.println("File can not be found");
            System.exit(0);
        }

        //reading the text file and storing the station names, coordinate values and rgb values separately
        ArrayList<String[]> stations = new ArrayList<>();
        ArrayList<int[][]> allCoordinates = new ArrayList<>();
        ArrayList<String[]> allColors = new ArrayList<>();
        ArrayList<String> breakpoints = new ArrayList<>();
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] lineSplit = line.split(" ");
            String[] whichLine = lineSplit[1].split(",");
            if (whichLine.length == 3) {
                allColors.add(whichLine);
            } else if (whichLine.length == 1) {
                breakpoints.add(lineSplit[0]);
            } else {
                String[] stationsOfALine = new String[lineSplit.length / 2];
                int[][] coordinates = new int[lineSplit.length / 2][2];
                for (int j = 0; j < lineSplit.length; j = j + 2) {
                    stationsOfALine[j / 2] = lineSplit[j];
                    coordinates[j / 2][0] = Integer.parseInt(lineSplit[j + 1].split(",")[0]);
                    coordinates[j / 2][1] = Integer.parseInt(lineSplit[j + 1].split(",")[1]);
                }
                allCoordinates.add(coordinates);
                stations.add(stationsOfALine);
            }
        }

        //here copying the stations arraylist to another arraylist named stations2 without "*" character before the station names
        ArrayList<String[]> stations2 = new ArrayList<>();
        ArrayList<String> easyStats = new ArrayList<>();
        for(int i = 0; i<stations.size(); i++){
            String[] stationsOfALine2 = new String[stations.get(i).length];
            for(int j = 0; j<stations.get(i).length; j++){
                if(stations.get(i)[j].startsWith("*")){
                    stationsOfALine2[j] = stations.get(i)[j].substring(1);
                    easyStats.add(stations.get(i)[j].substring(1));
                }
                else{
                    stationsOfALine2[j] = stations.get(i)[j];
                    easyStats.add(stations.get(i)[j]);
            }}
        stations2.add(stationsOfALine2);}

        //checking if the given station names exist and if so getting their index values and calling the path-finding and animation methods
        ArrayList<String> path = new ArrayList<>();
        ArrayList<String> visited = new ArrayList<>();
        int column1 = 0;
        int row1 = 0;
        if(easyStats.contains(str1)&&easyStats.contains(str2)){
            column1 = findIndexByName(stations2,str1)[1];
            row1 = findIndexByName(stations2,str1)[0];
            try {
                for (String d : pathFinder(stations2, str2, row1, column1, visited)) {
                    if (!path.contains(d)){
                        System.out.println(d);
                        path.add(d);}
                }
                showPath(allColors,stations2,stations,allCoordinates,path,str2);
            }
            catch (NullPointerException e){
                System.out.println("These two stations are not connected");
            }
        }
        else
            System.out.println("The station names provided are not present in this map.");
    }
    //attempt to find a correct path
    public static ArrayList<String> pathFinder(ArrayList<String[]> stations, String destination, int r2, int c2, ArrayList<String> visited) {
        if (c2 < 0 || r2 < 0 || c2 >= stations.get(r2).length || r2 >= 10) {
            return null;
        }
        if (visited.contains(r2 + "," + c2)) {
            return null;
        }
        visited.add(r2 + "," + c2);
        if (destination.equals(stations.get(r2)[c2])) {
            ArrayList<String> path = new ArrayList<>();
            path.add(stations.get(r2)[c2]);
            return path;
        }
        ArrayList<String> path = null;
        for (int[] i : findAllIndexes(stations, stations.get(r2)[c2])) {
            int newR = i[0];
            int newC = i[1];
            path = pathFinder(stations, destination,newR, newC + 1, visited);
            if (path == null) {
                path = pathFinder(stations, destination, newR, newC - 1, visited);
            }
            if (path != null) {
                path.add(0, stations.get(r2)[c2]);
                break;
            }
        }
        return path;
    }

    //to find a string item from its name in 2d arraylist containing arrays
    public static int[] findIndexByName(ArrayList<String[]> arrList,String name) {
        int[] bored = new int[0];
        for (int i = 0; i < arrList.size(); i++) {
            for (int j = 0; j < arrList.get(i).length; j++) {
                if (name.equals(arrList.get(i)[j])) {
                    bored = new int[]{i, j};
                    break;
                }
            }
        }
        return bored;
    }
    //to find the different index values of stations that are breakpoints
    public static ArrayList<int[]> findAllIndexes(ArrayList<String[]> arrList,String name){
        ArrayList<int[]> allLines = new ArrayList<>();
        for (int i = 0; i < arrList.size(); i++) {
            for (int j = 0; j < arrList.get(i).length; j++) {
                int[] indexes = new int[2];
                if (name.equals(arrList.get(i)[j])) {
                    indexes = new int[]{i, j};
                    allLines.add(indexes);
                }
        }
        }
        return allLines;
    }
    //drawing the map and showing the path
public static void showPath(ArrayList<String[]> allColors,ArrayList<String[]> stations2,ArrayList<String[]> stations,ArrayList<int[][]> allCoordinates,ArrayList<String> path,String destination){
    StdDraw.enableDoubleBuffering();
    StdDraw.setCanvasSize(1024, 482);
    StdDraw.setXscale(0, 1024);
    StdDraw.setYscale(0, 482);
    int pauseDuration = 300;
    String currPos = path.get(0);

    //until finding the destination station redrawing the map and updating the currPos variable at the end of the loop
    while(!currPos.equals(destination)) {
        for(int i = 0; i< path.size(); i++){
            StdDraw.picture(512, 241, "background.jpg");
            int x = allCoordinates.get(findAllIndexes(stations2, path.get(i)).get(0)[0])[findAllIndexes(stations2, path.get(i)).get(0)[1]][0];
            int y = allCoordinates.get(findAllIndexes(stations2, path.get(i)).get(0)[0])[findAllIndexes(stations2, path.get(i)).get(0)[1]][1];

            //to draw the lines with rgb values that are stored in allColors
            for (int m = 0; m < allColors.size(); m++){
                for (int k = 1; k < stations.get(m).length; k++) {
                    StdDraw.setPenRadius(0.012);
                    StdDraw.setPenColor(Integer.parseInt(allColors.get(m)[0]), Integer.parseInt(allColors.get(m)[1]), Integer.parseInt(allColors.get(m)[2]));
                    StdDraw.line(allCoordinates.get(m)[k - 1][0], allCoordinates.get(m)[k - 1][1], allCoordinates.get(m)[k][0], allCoordinates.get(m)[k][1]);
                }
            }
            //accessing the coordinate values from allCoordinates and drawing the points and printing the station names with "*"
            for (int a = 0; a < stations.size(); a++) {
                for (int b = 0; b < stations.get(a).length; b++) {
                    StdDraw.setPenRadius(0.01);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.point(allCoordinates.get(a)[b][0], allCoordinates.get(a)[b][1]);
                    if (stations.get(a)[b].startsWith("*")) {
                        StdDraw.setPenColor(Color.BLACK);
                        StdDraw.setFont(new Font("Helvetica", Font.BOLD, 8));
                        StdDraw.text(allCoordinates.get(a)[b][0], allCoordinates.get(a)[b][1] + 5, stations.get(a)[b].substring(1));
                        }
                }}
            //for animation
            StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
            StdDraw.filledCircle(x,y,5);//prints out the bigger filled circle at current station
            //marks the points orange that are already visited
            for(String s: path.subList(0,i)){
                x = allCoordinates.get(findAllIndexes(stations2, s).get(0)[0])[findAllIndexes(stations2, s).get(0)[1]][0];
                y = allCoordinates.get(findAllIndexes(stations2, s).get(0)[0])[findAllIndexes(stations2, s).get(0)[1]][1];
                StdDraw.setPenRadius(0.012);
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                StdDraw.point(x,y);
            }
            StdDraw.show();
            StdDraw.pause(pauseDuration);
            StdDraw.clear();
            currPos = path.get(i);// updating the current station value
        }
    }
}}