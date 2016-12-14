# Take-a-Hike
1. Read in the Data File

Write this code in the MapDataDrawer class constructor.  We will be using the NevadaToCalifornia.txt (113K) data file, though your program should work with any datafile of the proper format (such as Colorado.txt which is 2.8MB).  The first two lines of our data file look like the following:
117 212
 803 778 860 1066 1131 1143 1240 1350 1500 1626 1773 1832 1942 1813 1668 1645 1746 1801 1698 1522 . . .
The first integer is the number of rows in the file, and the second integer is the number of columns in the file.  These are followed by all the values, which in this case are 117 x 212 = 24,804 integer values.  There are no line breaks in the data, so you have to keep track of which values go in each row,col as you read them in.  You should use the Scanner nextInt() method to read in the values.  You should create a private class variable that is a 2-dimensional array of ints to store this data.  Each integer represents the average elevation in meters for the corresponding square mile on the map.  

If you want to create additional data sets of your own, visit NOAA Grid Extract.  To extract a dataset you need to :
Zoom in on the area desired
Select an area by clicking on the "i" button in the upper-left, then selecting the region within the displayed map
For the Output Format select "ArcGIS ASCII Grid".  It takes a moment to download the data.
Replace the metadata at the top of the file with just the row and column values.  (Note that in the metadata it is in column,row order, not row,column).
2. Find min and max values

These should be found across all file values.  Write this code in the findMin() and findMax() methods

3. Draw the map.  

(10 points)  Write this code in the drawMap( Graphics) method.  Most of the functionality to do this is already written.  You just need to determine the color, using this information to draw a small rectangle corresponding to each cell in the map at the row,col location.
First use the findMin() and findMax() values to help figure out how to scale the map values into the 0..255 RGB range.  The same computed value will be used for all three of R, G, and B, thus making it a grayscale image.  The 256 shades of gray go from black (0,0,0) to middle gray (128,128,128) to white (255,255,255).  Set the color just before drawing each small rectangle using the g.fillRect() method.  Your code will be something like the following:
int c = ???;    //calculate the grayscale value
g.setColor(new Color(c, c, c));    // set all 3 of the RGB colors to be the same 0..255 value
// While data was stored as row,col, the graphing expects it to come in as col,row, so reverse it here:
g.fillRect(col,row,1,1);         // Draw a 1x1 rectangle corresponding to row,col

Once you have finished this step and call it from within the Driver class, you should see something similar to Figure A (shown above) displayed.  

4. Draw a path.   

(10 points)  Write this code in the drawLowestElevPath( Graphics, startRow) method.  You will have a loop to go through each column, starting from column 0 on the far left.  At each step you will draw the rectangle representing the current (row,col) location, then move one column to the right, moving either straight across, or up/down one row, accumulating the difference in elevation as an absolute value.  
     Note that the Driver code will have already set the drawing color to RED, so when you draw a rectangle here it will show up as RED. Note that there is a difference between moving to the lowest elevation rectangle to the right, and moving to the rectangle to the right that has the smallest elevation difference from the current rectangle.  The former represents the strategy where you always try to travel downhill, while the latter represents the minimum-vertical-change approach. 
     Once you are done you should have a map with a red path going from left to right, such as the one shown in Figure B above.  This code should also return the total accumulated elevation change.  
     Once you are done you should have a map with a red path going from left to right, such as the one shown in Figure B above.  This code should also return the total accumulated elevation change.  In the console output you should print the starting row and the total elevation change.

5. Find Best Greedy Solution  

(10 points) Now repeat step 4, starting at every possible row in the first column.  Your output at this point might look like Figure C above.  Keep track of the best solution, and print just that one out at the end, such as the one shown in Figure D above.   In the console output again you should print the starting row and the total elevation change for the best solution.   
     If your results change each time you run your program, make sure you seed the instance of Random with the value 1 only once, then share that instance of random with the code where it is used.
6. The Fun/Challenging Part    

(25 points)  Now you can think about how to improve your results.  The interesting question is whether some other non-greedy strategy can in fact give a better solution.  You must start somewhere in the left-most column and end up in the right-most column, but otherwise can move wherever you want, with no penalty for longer paths, allowing moving back towards the left.  The evaluation function only considers total vertical distance travelled.  For our sample map, in each of the 212 columns of data you choose among 3 alternatives, giving a total of ~3212 possible paths (roughly 1.4 x 10101 ) so exhaustive search is clearly not an option.   Here are some alternatives you could try:
Start in the middle and work outwards in both directions
Start at the right and move to the left
Create multiple columns, finding lowest points in each column, and try to connect points in between
Consider more than just the 3 locations to the right.
Allow "backtracking" to move backwards temporarily (e.g. through a valley) so that you can subsequently go through a mountain pass.
Consider if shortest-path algorithms such as Floyd-Warshall or Dijkstra's might help you with one of your strategies.
