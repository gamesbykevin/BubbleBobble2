package com.gamesbykevin.bubblebobble2.entity;

import com.gamesbykevin.framework.base.Animation;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.bubblebobble2.engine.Engine;

import java.awt.Graphics;

public abstract class Entity extends Sprite implements Disposable
{
    //default animation key
    protected static final String DEFAULT_ANIMATION_KEY = "Default";
    
    protected Entity()
    {
        //create sprite sheet
        super.createSpriteSheet();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    /**
     * Add animation to sprite sheet
     * @param object Key to identify this animation
     * @param count Number of frames
     * @param x Starting x-coordinate
     * @param y Starting y-coordinate
     * @param w Width
     * @param h Height
     * @param delay Time delay between each frame
     */
    protected void addAnimation(final Object object, final int count, final int x, final int y, final int w, final int h, final long delay)
    {
        //create new animation
        final Animation animation = new Animation();
        
        //add each fram to animatiion
        for (int i = 0; i < count; i++)
        {
            //add frame to animation
            animation.add(x + (i * w), y, w, h, delay);
        }
        
        //add animation to sprite sheet
        super.getSpriteSheet().add(animation, object);
        
        //if no animation has been set, set this as default
        if (getSpriteSheet().getCurrent() == null)
            getSpriteSheet().setCurrent(object);
    }
    
    /**
     * Update the location and animation.
     * @param time The time deduction per frame (nano-seconds)
     */
    public void update(final long time)
    {
        try
        {
            //update location based on velocity
            super.update();

            //update animation
            super.getSpriteSheet().update(time);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}