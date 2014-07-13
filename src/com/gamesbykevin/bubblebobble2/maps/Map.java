package com.gamesbykevin.bubblebobble2.maps;

import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.bubblebobble2.entity.Entity;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public final class Map extends Entity implements Disposable
{
    //each map contains blocks 8 x 8 in size
    private static final int BLOCK_SIZE = 8;
    
    //default size of each map
    protected static final int WIDTH = 256;
    protected static final int HEIGHT = 192;
    
    //the dimension size of 8 x 8 blocks in each map
    private static final int COLUMNS = 32;
    private static final int ROWS = 24;
    
    private List<Location> locations;
    
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
        super.addAnimation(Entity.DEFAULT_ANIMATION_KEY, 1, startX, startY, WIDTH, HEIGHT, 0);

        //create new list of locations
        this.locations = new ArrayList<>();
        
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
                
                //add new location
                locations.add(tmp);
            }
        }
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
        for (int i = 0; i < locations.size(); i++)
        {
            Location tmp = locations.get(i);
            
            if (!tmp.hasWalls())
                continue;
            
            final int x = (int)(BLOCK_SIZE * tmp.getCol());
            final int y = (int)(BLOCK_SIZE * tmp.getRow());
            graphics.setColor(Color.WHITE);
            graphics.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
        }
    }
}