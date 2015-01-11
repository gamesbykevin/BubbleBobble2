package com.gamesbykevin.bubblebobble2.entity;

import com.gamesbykevin.framework.base.Animation;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

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
    
    /**
     * Setup the animations for the entity
     */
    protected abstract void setupAnimations();
    
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
     * @param loop Does the animation loop
     */
    protected void addAnimation(final Object object, final int count, final int x, final int y, final int w, final int h, final long delay, final boolean loop)
    {
        //create new animation
        final Animation animation = new Animation();
        
        //add each fram to animatiion
        for (int i = 0; i < count; i++)
        {
            //add frame to animation
            animation.add(x + (i * w), y, w, h, delay);
        }
        
        //set the animation to loop
        animation.setLoop(loop);
        
        //add animation to sprite sheet
        super.getSpriteSheet().add(animation, object);
        
        //if no animation has been set, set this as default
        if (getSpriteSheet().getCurrent() == null)
            setAnimation(object);
    }
    
    /**
     * Set the animation
     * @param object The unique key of the animation we want to set
     * @param reset If true we will reset the animation
     */
    protected void setAnimation(final Object object, final boolean reset)
    {
        //set current animation
        getSpriteSheet().setCurrent(object);
        
        try
        {
            //set width/height based on current animation
            setDimensions();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        if (reset)
            getSpriteSheet().reset();
    }
    
    /**
     * Set the animation
     * @param object The unique key of the animation we want to set
     */
    protected void setAnimation(final Object object)
    {
        setAnimation(object, false);
    }
    
    /**
     * Is this animation the current one?
     * @param object Unique key to check animation
     * @return true if this is the current animation, false otherwise
     */
    public boolean isAnimation(final Object object)
    {
        return (super.getSpriteSheet().getCurrent() == object);
    }
    
    /**
     * Has the animation finished
     * @return true if the current animation has finished, false otherwise
     */
    public boolean isAnimationFinished()
    {
        return (super.getSpriteSheet().hasFinished());
    }
    
    /**
     * Update the location and animation.
     * @param time The time deduction per frame (nanoseconds)
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
    
    public void render(final Graphics graphics)
    {
        //store original location
        final double x = getX();
        final double y = getY();
        
        //off-set location
        super.setX(x - (getWidth() / 2));
        super.setY(y - (getHeight() / 2));
        
        //draw character
        super.draw(graphics);
        
        //reset location
        super.setX(x);
        super.setY(y);
    }
}