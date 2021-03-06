package com.gamesbykevin.bubblebobble2.maps;

import com.gamesbykevin.framework.resources.Progress;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.shared.IElement;

import java.awt.Graphics;
import java.awt.image.PixelGrabber;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public final class Maps implements Disposable, IElement
{
    //image for all maps will be in this image
    private Image image;
    
    //list of maps
    private List<Map> maps;
    
    //the current map
    private int index = 0;
    
    //do we transition to the next level
    private boolean transition = false;
    
    //the coordinates where the first map is located
    private static final int START_X = 3;
    private static final int START_Y = 3;
    
    //the pixel distance between each map on the image
    private static final int PIXEL_SPACE = 3;
    
    //used to determine the coordinates of the maps
    private static final int MAPS_PER_COLUMN = 10;
    private static final int MAPS_PER_ROW = 10;
    
    //the speed at which the next level appears
    private static final int MAP_TRANSITION_SPEED = -1;
    
    //track progress of maps being created
    private Progress progress;
    
    //the total number of maps
    private static final int MAP_COUNT = 200;
    
    //used to analyze pixels in image
    private PixelGrabber pixelGrabber;
    
    //the pixels representing the image
    private int[] pixels;
    
    //the total dimension size of our entire image that contains all maps
    protected static final int IMAGE_PIXELS_PER_COLUMN = 2593;
    protected static final int IMAGE_PIXELS_PER_ROW = 4488;
    
    /**
     * 
     * @param image Image of all maps
     * @param window Area where map will be displayed
     * @param level 
     */
    public Maps(final Image image, final Rectangle window)
    {
        //our image that contains all maps
        this.image = image;
        
        //create new list to contain our maps
        this.maps = new ArrayList<>();
        
        try
        {
            //create pixelGrabber object
            this.pixelGrabber = new PixelGrabber(image, 0, 0, IMAGE_PIXELS_PER_COLUMN, IMAGE_PIXELS_PER_ROW, true);
            final boolean result = this.pixelGrabber.grabPixels();

            if (!result)
            {
                throw new Exception("Failed to grab all pixels");
            }
            else
            {
                //get pixels from object
                pixels = (int[])pixelGrabber.getPixels();
            }
            
            //this will track progress of creating maps
            this.progress = new Progress(MAP_COUNT);
            this.progress.setDescription("Creating levels");
            this.progress.setScreen(window);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Do we want to start moving to the next level
     * @param transition true if yes, false otherwise
     */
    public void setTransition(final boolean transition)
    {
        this.transition = transition;
        
        //if transition start moving all maps
        if (hasTransition())
        {
            for (int i = 0; i < maps.size(); i++)
            {
                getMap(i).setVelocityY(MAP_TRANSITION_SPEED);
            }
        }
    }
    
    public boolean hasTransition()
    {
        return this.transition;
    }
    
    /**
     * Set the map
     * @param index The desired map
     */
    public void setStartingMap(final int index)
    {
        this.index = index;
    }
    
    /**
     * Create all the maps by analyzing the pixel data to determine where boundaries are
     */
    private void create()
    {
        //determine the next map to create
        final int next = progress.getCount();
        
        //calculate column, row
        final int column = next - ((next / MAPS_PER_COLUMN) * MAPS_PER_COLUMN);
        final int row    = (next / MAPS_PER_ROW);
                
        //calculate start x,y cooridate of background map
        final int x = START_X + (column * (PIXEL_SPACE + Map.WIDTH));
        final int y = START_Y + (row *    (PIXEL_SPACE + Map.HEIGHT));
        
        try
        {
            //create new map with background at (x,y) and map the boundaries
            final Map map = new Map(x, y, pixels);

            //each map will be placed one after the other
            map.setLocation(0, next * Map.HEIGHT);

            //add map to list
            maps.add(map);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        //increase progress regardless of error
        progress.increase();
    }
    
    /**
     * Make sure all maps have the correct y-coordinate with the current map having y-coordinate = 0
     */
    private void setMap()
    {
        while (maps.get(getIndex()).getY() != 0)
        {
            for (int i = 0; i < maps.size(); i++)
            {
                if (maps.get(getIndex()).getY() > 0)
                {
                    maps.get(i).setY(maps.get(i).getY() - 1);
                }
                else
                {
                    maps.get(i).setY(maps.get(i).getY() + 1);
                }
            }
        }
    }
    
    @Override
    public void dispose()
    {
        if (maps != null)
        {
            for (int i = 0; i < maps.size(); i++)
            {
                maps.get(i).dispose();
                maps.set(i, null);
            }

            maps.clear();
            maps = null;
        }
    }
    
    /**
     * Get the current map
     * @return The current map
     */
    public Map getMap()
    {
        return getMap(getIndex());
    }
    
    /**
     * Get the specified map
     * @param index The location of the map we want
     * @return Map
     */
    public Map getMap(final int index)
    {
        return maps.get(index);
    }
    
    private int getIndex()
    {
        return this.index;
    }
    
    /**
     * Is this the last map
     * @return true if so, otherwise false if not
     */
    public boolean isLastMap()
    {
        return (getIndex() >= maps.size() - 1);
    }
    
    /**
     * Are we done creating the levels?
     * @return true if the levels have been created, false otherwise
     */
    public boolean isComplete()
    {
        return progress.isComplete();
    }
    
    @Override
    public void update(final Engine engine)
    {
        try
        {
            //if not complete continue with level creation
            if (!progress.isComplete())
            {
                create();
                
                if (progress.isComplete())
                    setMap();
            }
            else
            {
                //if we are switching levels
                if (hasTransition())
                {
                    //update location of all maps
                    for (int i = 0; i < maps.size(); i++)
                    {
                        getMap(i).update();
                    }
                    
                    //is the next map at the finish line?
                    if (getMap(getIndex() + 1).getY() <= 0)
                    {
                        //stop the transition
                        setTransition(false);
                        
                        //set the next level
                        setStartingMap(getIndex() + 1);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Draw the map.<br>
     * If the maps haven't been created yet draw the progress
     * @param graphics Object used to draw graphics
     */
    @Override
    public void render(final Graphics graphics) throws Exception
    {
        if (!progress.isComplete())
        {
            //draw progress
            progress.render(graphics);
        }
        else
        {
            //draw map
            /*
            if (Shared.DEBUG)
            {
                getMap().renderTest(graphics);

                if (hasTransition())
                    getMap(index + 1).renderTest(graphics);
            }
            */
            
            //draw current map
            getMap().draw(graphics, image);

            //if moving to new map draw new map also
            if (hasTransition())
                getMap(getIndex() + 1).draw(graphics, image);
        }
    }
}