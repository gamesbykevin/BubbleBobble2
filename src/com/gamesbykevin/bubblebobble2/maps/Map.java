package com.gamesbykevin.bubblebobble2.maps;

import com.gamesbykevin.bubblebobble2.enemies.Enemies;
import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.bubblebobble2.entity.Entity;
import com.gamesbykevin.bubblebobble2.shared.Shared;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public final class Map extends Entity implements Disposable
{
    //each map contains blocks 8 x 8 in size
    public static final int BLOCK_SIZE = 8;
    
    private static final int HERO_COLUMN_RANGE = 1;
    private static final int ENEMY_COLUMN_RANGE = 2;
    
    //default size of each map
    protected static final int WIDTH = 256;
    protected static final int HEIGHT = 192;
    
    //the dimension size of 8 x 8 blocks in each map
    public static final int COLUMNS = 32;
    public static final int ROWS = 24;
    
    //the minimum number of blocks required for a map
    private static final int MINIMUM_BLOCK_REQUIREMENT = (ROWS * 4) + ((COLUMNS - 12) * 2);
    
    //each map has boundaries where gameplay takes place
    public static final int BOUNDARY_COL_MIN = 2;
    public static final int BOUNDARY_COL_MAX = COLUMNS - 3;
    public static final int BOUNDARY_ROW_MIN = 1;
    public static final int BOUNDARY_ROW_MAX = ROWS - 2;
    
    //some maps may have gaps in the floor
    private static final int FLOOR_GAP_START_COL_1 = 9;
    private static final int FLOOR_GAP_END_COL_1   = 12;
    private static final int FLOOR_GAP_START_COL_2 = 19;
    private static final int FLOOR_GAP_END_COL_2   = 22;
    
    //the array of locations in this map
    private Location[][] locations;
    
    //locations where the enemies can be placed
    private List<Cell> spawnLocations;
    
    //where the hero1 will start
    public static final int START_COL_HERO_1 = BOUNDARY_COL_MIN;
    public static final int START_ROW_HERO_1 = BOUNDARY_ROW_MAX;
    
    //where the hero2 will start
    public static final int START_COL_HERO_2 = BOUNDARY_COL_MAX;
    public static final int START_ROW_HERO_2 = BOUNDARY_ROW_MAX;
    
    //coordinate where map starts
    private final int startX, startY;
    
    //the location of the starting place for the heroes on both sides
    private Point startWest = null, startEast = null; 
    
    /**
     * Create a new map with the specified background coordinates
     * @param startX x-coordinate of background
     * @param startY y-coordinate of background
     * @param pixels The array containing all of the pixels in the level
     * @throws Exception Exception will be thrown if the map doesn't have the required number of solid blocks
     */
    protected Map(final int startX, final int startY, final int[] pixels) throws Exception
    {
        super.setDimensions(WIDTH, HEIGHT);
        
        this.startX = startX;
        this.startY = startY;

        //create new array of locations
        this.locations = new Location[ROWS][COLUMNS];
        
        //create new list for spawn locations
        this.spawnLocations = new ArrayList<>();
        
        //setup the boundaries based on pixel color
        for (int col = 0; col < COLUMNS; col++)
        {
            for (int row = 0; row < ROWS; row++)
            {
                //create new location
                Location tmp = new Location(col, row);
                
                //remove all walls by default
                tmp.getWalls().clear();
                
                //calculate the current pixel location
                final int x = startX + getBlockX(col);
                final int y = startY + getBlockY(row);
                
                //check if this location is a boundary
                if (isBoundary(x, y, pixels))
                {
                    //add all walls
                    tmp.add(Location.Wall.East);
                    tmp.add(Location.Wall.West);
                    tmp.add(Location.Wall.North);
                    tmp.add(Location.Wall.South);
                }
                else
                {
                    //else remove all walls
                    tmp.remove(Location.Wall.East);
                    tmp.remove(Location.Wall.West);
                    tmp.remove(Location.Wall.North);
                    tmp.remove(Location.Wall.South);
                }
                
                //assign new location
                locations[row][col] = tmp;
            }
        }
        
        //make sure this map has the required number of solid blocks
        if (getBlockCount() < MINIMUM_BLOCK_REQUIREMENT)
            throw new Exception("Map did not meet the required # of solid blocks (" + startX + "," + startY + ")");
        
        //start at bottom row
        for (int row = BOUNDARY_ROW_MAX - 1; row >= BOUNDARY_ROW_MIN; row--)
        {
            //only check if location hasn't been found
            if (startWest == null)
            {
                for (int col = BOUNDARY_COL_MIN; col <= (COLUMNS / 2); col++)
                {
                    //if all blocks aren't solid, this is a valid place
                    if (hasFreeSpace(col, row, HERO_COLUMN_RANGE))
                    {
                        startWest = new Point(BLOCK_SIZE * (col + 1), BLOCK_SIZE * (row + 1));
                        break;
                    }
                }
            }
            
            //only check if location hasn't been found
            if (startEast == null)
            {
                for (int col = BOUNDARY_COL_MAX - 1; col >= (COLUMNS / 2); col--)
                {
                    //if all blocks aren't solid, this is a valid place
                    if (hasFreeSpace(col, row, HERO_COLUMN_RANGE))
                    {
                        startEast = new Point(BLOCK_SIZE * (col + 1), BLOCK_SIZE * (row + 1));
                        break;
                    }
                }
            }
            
            //don't continue if both locations have been found
            if (startWest != null && startEast != null)
                break;
        }
        
        //now that hero locations are found, check for valid spawn locations for the enemies
        for (int row = BOUNDARY_ROW_MIN; row < BOUNDARY_ROW_MAX - 2; row++)
        {
            for (int col = BOUNDARY_COL_MIN + 2; col < (COLUMNS / 2) - 1; col++)
            {
                //make sure both sides are free
                if (hasFreeSpace(col, row, ENEMY_COLUMN_RANGE) && hasFreeSpace(COLUMNS - col, row, ENEMY_COLUMN_RANGE))
                {
                    //add valid location to the list
                    this.spawnLocations.add(new Cell(col + 1, row + 1));
                }
            }
        }
        
        //setup animation
        setupAnimations();
    }
    
    /**
     * Is this space open for a character to be placed
     * @param col Starting north-west column
     * @param row Starting north-west row
     * @param columnRange The columns to check on both sides of the current col
     * @return true if the cells around the specified are not solid
     */
    public boolean hasFreeSpace(final int col, final int row, final int columnRange)
    {
        for (int x = -columnRange; x <= columnRange; x++)
        {
            for (int y = 0; y <= 1; y++)
            {
                //if any space is solid it is not a free space
                if (isSolid(col + x, row + y))
                    return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get the hero starting point
     * @return The (x,y) location where the hero is to start on the west side
     */
    public Point getStartWest()
    {
        return this.startWest;
    }
    
    public List<Cell> getSpawnLocations()
    {
        return this.spawnLocations;
    }
    
    /**
     * Get the hero starting point
     * @return The (x,y) location where the hero is to start on the east side
     */
    public Point getStartEast()
    {
        return this.startEast;
    }
    
    @Override
    protected void setupAnimations()
    {
        //the background map is static and won't change, so it is 1 frame and 1 animation
        super.addAnimation(Entity.DEFAULT_ANIMATION_KEY, 1, startX, startY, WIDTH, HEIGHT, 0, false);
    }
    
    /**
     * Get the block count
     * @return The total number of solid blocks
     */
    private int getBlockCount()
    {
        int count = 0;
        
        for (int col = 0; col < COLUMNS; col++)
        {
            for (int row = 0; row < ROWS; row++)
            {
                //if this location has walls it is a solid block
                if (isSolid(col, row))
                    count++;
            }
        }
        
        //return the count
        return count;
    }
    
    /**
     * Determine if the location is in bounds
     * @param x x-coordinate
     * @param y y-coordinate
     * @return true if the location is in the playable area, false otherwise
     */
    public boolean hasBounds(final double x, final double y)
    {
        final int row = getRow(y);
        final int col = getColumn(x);
        
        return (col >= BOUNDARY_COL_MIN && col <= BOUNDARY_COL_MAX && row <= BOUNDARY_ROW_MAX);
    }
    
    /**
     * Is the location solid?
     * @param col Column
     * @param row Row
     * @return true if the location has walls, false otherwise
     */
    private boolean isSolid(final int col, final int row)
    {
        if (row < 0 || row >= locations.length)
            return false;
        
        if (col < 0 || col >= locations[0].length)
            return false;
        
        return locations[row][col].hasWalls();
    }
    
    public boolean hasNorthCollision(final double x, final double y)
    {
        //north collision when close to top
        if (hasNorthCollision(y))
            return true;
        
        //return true if solid
        return (isSolid(getColumn(x), getRow(y)));
    }
    
    public boolean hasSouthCollision(final double x, final double y)
    {
        final int col = getColumn(x);
        final int row = getRow(y);
        
        if (row > BOUNDARY_ROW_MAX)
        {
            //check if gap in floor
            if (col >= FLOOR_GAP_START_COL_1 && col <= FLOOR_GAP_END_COL_1 || col >= FLOOR_GAP_START_COL_2 && col <= FLOOR_GAP_END_COL_2)
            {
                //return true if solid
                return (isSolid(col,row));
            }
            else
            {
                //every other column in this row wil be solid
                return true;
            }
        }
        
        //check if location is solid
        return isSolid(col, row);
    }
    
    public boolean hasHorizontalCollision(final double x, final double y)
    {
        //the sides will always
        if (hasWestCollision(x) || hasEastCollision(x))
            return true;
        
        if (hasNorthCollision(y))
            return false;
        
        return isSolid(getColumn(x), getRow(y));
    }
    
    /**
     * Is the location on the boundary west side of the map
     * @param x x-coordinate
     * @return true if the column where the x-coordinate is, is less than the BOUNDARY_COL_MIN
     */
    public boolean hasWestCollision(final double x)
    {
        return (getColumn(x) < BOUNDARY_COL_MIN);
    }
    
    /**
     * Is the location on the boundary east side of the map
     * @param x x-coordinate
     * @return true if the column where the x-coordinate is, is greater than the BOUNDARY_COL_MAX
     */
    public boolean hasEastCollision(final double x)
    {
        return (getColumn(x) > BOUNDARY_COL_MAX);
    }
    
    /**
     * Is the location on the boundary north side of the map
     * @param y y-coordinate
     * @return true if the column where the y-coordinate is, is less than the BOUNDARY_ROW_MIN
     */
    public boolean hasNorthCollision(final double y)
    {
        return (getRow(y) < BOUNDARY_ROW_MIN);
    }
    
    /**
     * Get the row
     * @param y y-coordinate
     * @return the row where the specified y-coordinate is
     */
    public int getRow(final double y)
    {
        return (int)(y / BLOCK_SIZE);
    }
    
    /**
     * Get the column
     * @param x x-coordinate
     * @return the column where the specified x-coordinate is
     */
    public int getColumn(final double x)
    {
        return (int)(x / BLOCK_SIZE);
    }
    
    /**
     * Is there a boundary at this location
     * @param x x-coordinate north-west corner
     * @param y y-coordinate north-west corner
     * @param pixels int[] array containing pixel data
     * @return true if this location is a solid boundary, false otherwise
     */
    private boolean isBoundary(final int x, final int y, final int[] pixels)
    {
        //north-west corner
        if (getPixel(x, y, pixels) != Color.BLACK.getRGB())
            return true;
        
        //south-east corner
        if (getPixel(x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1, pixels) != Color.BLACK.getRGB())
            return true;
        
        //middle
        if (getPixel(x + (BLOCK_SIZE / 2), y + (BLOCK_SIZE / 2), pixels) != Color.BLACK.getRGB())
            return true;
        
        return false;
    }
    
    /**
     * Get the pixel from the array at the specified location
     * @param currentX The x-coordinate on the image
     * @param currentY The y-coordinate on the image
     * @param pixels The int[] array that contains all pixel data of the image
     * @return rgb value of pixel at specified location
     */
    private int getPixel(final int currentX, final int currentY, final int[] pixels)
    {
        return pixels[(currentY * Maps.IMAGE_PIXELS_PER_COLUMN) + currentX];
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    /**
     * Get the x-coordinate 
     * @param col The column we want to check
     * @return the x-coordinate where the column is located
     */
    public static int getBlockX(final double col)
    {
        return (int)(BLOCK_SIZE * col);
    }
    
    /**
     * Get the y-coordinate 
     * @param row The Row we want to check
     * @return the y-coordinate where the row is located
     */
    public static int getBlockY(final double row)
    {
        return (int)(BLOCK_SIZE * row);
    }
    
    /**
     * This is to display a visual of the boundaries for testing
     * @param graphics 
     */
    protected void renderTest(final Graphics graphics)
    {
        for (int col = 0; col < COLUMNS; col++)
        {
            for (int row = 0; row < ROWS; row++)
            {
                //get temporary location
                Location tmp = locations[row][col];
                
                if (!isSolid(col, row))
                    continue;

                final int x = (int)(getBlockX(tmp.getCol()) + getX());
                final int y = (int)(getBlockY(tmp.getRow()) + getY());
            
                graphics.setColor(Color.WHITE);
                graphics.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
            }
        }
    }
}