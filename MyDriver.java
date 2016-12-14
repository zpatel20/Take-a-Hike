/**
 * MyDriver.java
 *     Driver for UIC CS 342 Fall 2016 program #2: Take a Hike
 *
 *
 * Given an row x col grid of integers where each integer represents the average elevation for one square mile
 * on a map, find the path from one side to the other that gives the lowest total elevation change.
 *
 * This assignment is slightly modified from Baker Franke's Nifty Assingments "Mountain Paths" project, using the
 * DrawingPanel class from "Building Java Programs" by Reges and Stepp.
 */

import java.awt.*;

public class MyDriver {
    private int rows;
    private int columns;
    private MapDataDrawer map;
    private Graphics graphics;  // graphics context
    private int minRow;         // lowest elevation row in left column
    private int bestRow = -1;
    private int totalChange = 0;

    public static void main(String[] args)
    {
        // ----- Step 1: construct mountain map data
        MyDriver myDriverInstance = new MyDriver();
        myDriverInstance.doIt();
    }

    private void doIt()
    {
        // ----- Step 2: min, max, minRow in left-most column
        int min = map.findMinValueInGrid();
        int max = map.findMaxValueInGrid();
        System.out.println("Step 2. Map min value is " + min + " and max value is " + max);

        // ----- Step 3: draw the map
        System.out.println("Step 3. Gray-scale map is drawn.");
        map.drawMap(graphics);

        // ----- Step 4: draw a greedy path from the lowest possible starting point
        graphics.setColor(Color.RED);      // set the color of the 'brush' before drawing
        minRow = map.findIndexOfMinimumValueInColumn(0);
        System.out.println("Row with lowest val in col 0 is: " + minRow);
        int totalChange = 0;
        totalChange =map.drawGreedyPathStartingAt(graphics,minRow,0); //use minRow from Step 2 as starting point
        System.out.println("Step 4. Red path starting at lowest left-hand row "+minRow+
                           " gives total change of: "+totalChange);

        // ----- Step 5: Find best greedy path, trying all starting locations
        // Disable display, so we can try all paths, and then only display the best one
        map.setDisplayIsOnTo(false);
        findBestGreedyPath();
        // Now that we found the best one, turn display back on and display it
        map.setDisplayIsOnTo(true);
        graphics.setColor(Color.GREEN);      // set the color of the 'brush' before drawing
        totalChange = map.drawGreedyPathStartingAt(graphics, bestRow, 0);
        System.out.println("Step 5. Green path starting at best left-hand row " + bestRow +
                           " gives total change of: " + totalChange);


        // ----- Test Step 6 - draw the best paths -----
        // First attempt is to draw both ways, starting from the lowest point in the middle
        graphics.setColor(Color.BLACK);      // Set brush color for drawing best path
        int lowestRowInMiddleOfMap = map.findIndexOfMinimumValueInColumn(columns / 2);
        totalChange = map.drawGreedyPathStartingAt(graphics, lowestRowInMiddleOfMap, columns / 2);
        totalChange += map.drawGreedyREVERSEPathStartingAt(graphics, lowestRowInMiddleOfMap, columns / 2);
        System.out.println("Step 6. Black path going both ways from lowest midle row: " + bestRow +
                           " gives a total change of: " + totalChange);
        // Second attempt is to find best reverse path
        map.setDisplayIsOnTo(false);
        // Try all possible reverse paths, starting from the right-hand side
        findBestReversePath();
        map.setDisplayIsOnTo(true);
        graphics.setColor(Color.BLUE); //set brush color for drawing best path
        totalChange = map.drawGreedyREVERSEPathStartingAt(graphics, bestRow, columns - 1);
        System.out.println("        Blue is the best REVERSE path starting from right-hand row: " + bestRow +
                " and gives a total change of: " + totalChange);

        // Draw the lowest elevation in each column, as a hint for developing heuristics
        // map.drawLowestElevationInEachColumn(graphics);
    }//end main()


    // Constructor
    private MyDriver()
    {
        map = new MapDataDrawer("NevadaToCalifornia.txt");
        rows = map.getRows();
        columns = map.getCols();

        //Construct DrawingPanel, and get its Graphics context
        DrawingPanel panel = new DrawingPanel(columns, rows);
        graphics = panel.getGraphics();
    }


    void findBestGreedyPath()
    {
        int smallestTotalChange = Integer.MAX_VALUE;
        bestRow = -1;
        for (int row = 1; row < rows; row++) {
            totalChange = map.drawGreedyPathStartingAt(graphics, row, 0);

            if (totalChange < smallestTotalChange) {
                smallestTotalChange = totalChange;
                bestRow = row;
            }
        }
    }


    // This method is virtually identical to the one above, except we call the REVERSE method and
    // start at the right-most column rather than the left.
    void findBestReversePath()
    {
        int smallestTotalChange = Integer.MAX_VALUE;
        bestRow = -1;
        for (int row = 1; row < rows; row++) {
            totalChange = map.drawGreedyREVERSEPathStartingAt(graphics, row, columns - 1);

            if (totalChange < smallestTotalChange) {
                smallestTotalChange = totalChange;
                bestRow = row;
            }
        }
    }

}//end class MyDriver
