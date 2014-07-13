package com.gamesbykevin.bubblebobble2.maps;

import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.bubblebobble2.entity.Entity;

import java.awt.Color;
import java.awt.Graphics;

public final class Map extends Entity implements Disposable
{
    //each map contains blocks 8 x 8 in size
    public static final int BLOCK_SIZE = 8;
    
    //default size of each map
    protected static final int WIDTH = 256;
    protected static final int HEIGHT = 192;
    
    //the dimension size of 8 x 8 blocks in each map
    public static final int COLUMNS = 32;
    public static final int ROWS = 24;
    
    //each map has boundaries where gameplay takes place
    private static final int BOUNDARY_COL_MIN = 2;
    private static final int BOUNDARY_COL_MAX = COLUMNS - 3;
    private static final int BOUNDARY_ROW_MIN = 1;
    private static final int BOUNDARY_ROW_MAX = ROWS - 2;
    
    //some maps may have gaps in the floor
    private static final int FLOOR_GAP_START_COL_1 = 9;
    private static final int FLOOR_GAP_END_COL_1   = 12;
    private static final int FLOOR_GAP_START_COL_2 = 19;
    private static final int FLOOR_GAP_END_COL_2   = 22;
    
    //the array of locations in this map
    private Location[][] locations;
    
    /**
     * Create a new map with the specified background coordinates
     * @param startX x-coordinate of background
     * @param startY y-coordinate of background
     * @param pixels The array containing all of the pixels in the level
     */
    protected Map(final int startX, final int startY, final int[] pixels)
    {
        super.setDimensions(WIDTH, HEIGHT);
        
        //the background map is static and won't change
        super.addAnimation(Entity.DEFAULT_ANIMATION_KEY, 1, startX, startY, WIDTH, HEIGHT, 0, false);

        //create new array of locations
        this.locations = new Location[ROWS][COLUMNS];
        
        //setup the boundaries based on pixel color
        for (int col = 0; col < COLUMNS; col++)
        {
            for (int row = 0; row < ROWS; row++)
            {
                //create new location
                Location tmp = new Location(col, row);
                
                //calculate the current pixel location
                final int x = startX + (col * BLOCK_SIZE);
                final int y = startY + (row * BLOCK_SIZE);
                
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
    }
    
    /**
     * Determine if the location is in bounds
     * @param x x-coordinate
     * @param y y-coordinate
     * @return true if the location is not in the playable area
     */
    public boolean hasBounds(final double x, final double y)
    {
        final int row = getRow(y);
        final int col = getColumn(x);
        
        return (col >= BOUNDARY_COL_MIN && col <= BOUNDARY_COL_MAX &&
                row >= BOUNDARY_ROW_MIN && row <= BOUNDARY_ROW_MAX);
    }
    
    /**
     * Is there a solid block at this location
     * @param x x-coordinate
     * @param y y-coordinate
     * @return true if the location at (x, y) has walls meaning it is a boundary
     */
    public boolean hasSolid(final double x, final double y)
    {
        final int row = getRow(y);
        
        //if out of bounds there will not be a solid block
        if (row < BOUNDARY_ROW_MIN - 1 || row > BOUNDARY_ROW_MAX + 1)
            return false;
        
        //if there is walls this is solid
        return locations[row][getColumn(x)].hasWalls();
    }
    
    public boolean hasNorthCollision(final double x, final double y)
    {
        final int col = getColumn(x);
        final int row = getRow(y);
        
        //if not within columns there could never be collision
        if (col < BOUNDARY_COL_MIN || col > BOUNDARY_COL_MAX)
            return false;
        
        //never allow past the minimum row even if there is a floor gap above
        if (row < BOUNDARY_ROW_MIN)
            return true;
        
        return false;
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
                return (hasSolid(x,y));
            }
            else
            {
                //every other column in this row wil be solid
                return true;
            }
        }
        
        //no collision detected
        return false;
    }
    
    public boolean hasHorizontalCollision(final double x, final double y)
    {
        final int col = getColumn(x);
        
        //the sides will always
        if (col < 2 || col > COLUMNS - 2)
            return true;
        
        return hasSolid(x, y);
    }
    
    private int getRow(final double y)
    {
        return (int)(y / BLOCK_SIZE);
    }
    
    private int getColumn(final double x)
    {
        return (int)(x / BLOCK_SIZE);
    }
    
    /**
     * Is there a boundary at this location
     * @param x x-coordinate north-west corner
     * @param y y-coordinate north-west corner
     * @param pixels int[] array containing pixel data
     * @return true if this location is a boundary, false otherwise
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
                
                if (!tmp.hasWalls())
                    continue;

                final int x = (int)(BLOCK_SIZE * tmp.getCol());
                final int y = (int)(BLOCK_SIZE * tmp.getRow());
            
                graphics.setColor(Color.WHITE);
                graphics.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
            }
        }
    }
}