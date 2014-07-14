package com.gamesbykevin.bubblebobble2.character;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.bubblebobble2.entity.Entity;
import com.gamesbykevin.bubblebobble2.maps.Map;
import com.gamesbykevin.bubblebobble2.projectile.Projectile;

import java.awt.Graphics;

public abstract class Character extends Entity implements Disposable
{
    //the projectile the character throws
    private Projectile projectile;
    
    //the speed at which to walk, run
    private double speedWalk, speedRun;
    
    //track what we are doing for the character
    private boolean idle = false, walk = false, jump = false, fall = false, attack = false, dead = false, start = true;
    
    //the max speed a character can fall
    private static final double MAX_SPEED_FALL = 1;
    
    //the max speed a character can jump
    public static final double MAX_SPEED_JUMP = 4.75;
    
    //the rate at which to increase/decrease y-velocity
    private static final double VELOCITY_Y_CHANGE = .25;
    
    //where to place the character
    private double destinationX, destinationY;
    
    protected Character(final double speedWalk, final double speedRun)
    {
        this.speedWalk = speedWalk;
        this.speedRun = speedRun;
    }
    
    /**
     * Set the destination where the character will be placed
     * @param destinationX x-coordinate
     * @param destinationY y-coordinate
     */
    public void setDestination(final double destinationX, final double destinationY)
    {
        this.destinationX = destinationX;
        this.destinationY = destinationY;
    }
    
    protected double getDestinationX()
    {
        return this.destinationX;
    }
    
    protected double getDestinationY()
    {
        return this.destinationY;
    }
    
    public double getSpeedRun()
    {
        return this.speedRun;
    }
    
    public double getSpeedWalk()
    {
        return this.speedWalk;
    }
    
    private void reset()
    {
        setStart(false);
        setIdle(false);
        setWalk(false);
        setJump(false);
        setFall(false);
        setAttack(false);
        setDead(false);
    }
    
    public void setStart(final boolean start)
    {
        if (start)
            reset();
        
        this.start = start;
    }
    
    public boolean isStarting()
    {
        return this.start;
    }
    
    public void setIdle(final boolean idle)
    {
        if (idle)
            reset();
        
        this.idle = idle;
    }
    
    public boolean isIdle()
    {
        return this.idle;
    }
    
    public void setWalk(final boolean walk)
    {
        if (walk)
            reset();
        
        this.walk = walk;
    }
    
    public boolean isWalking()
    {
        return this.walk;
    }
    
    public void setJump(final boolean jump)
    {
        if (jump)
            reset();
        
        this.jump = jump;
    }
    
    public boolean isJumping()
    {
        return this.jump;
    }
    
    public void setFall(final boolean fall)
    {
        if (fall)
            reset();
        
        this.fall = fall;
    }
    
    public boolean isFalling()
    {
        return this.fall;
    }
    
    public boolean canAttack()
    {
        return (!isAttacking() && !isDead());
    }
    
    public boolean canWalk()
    {
        return (isJumping() || isFalling() || isIdle()) && !isDead() && !isAttacking();
    }
    
    public boolean canJump()
    {
        return (!isJumping() && !isFalling() && !isDead() && !isAttacking());
    }
    
    public boolean canFall()
    {
        return (!isJumping() && !isFalling() && !isDead() && !isAttacking());
    }
    
    public void setAttack(final boolean attack)
    {
        if (attack)
            reset();
        
        this.attack = attack;
    }
    
    public boolean isAttacking()
    {
        return this.attack;
    }
    
    public void setDead(final boolean dead)
    {
        if (dead)
            reset();
        
        this.dead = dead;
    }
    
    public boolean isDead()
    {
        return this.dead;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    protected void update(final Map map, final long time)
    {
        //make sure the character is not being placed
        if (!isStarting())
        {
            //apply gravity
            applyGravity(map);

            //check for side collision
            checkHorizontalCollision(map);

            //perform any final adjustments to ensure character is in bounds
            checkLocation(map);
            
            if (projectile != null)
            {
                //update location and animation of projectile
                projectile.update(time);
            }
        }
        else
        {
            //go to our destination
            locateDestination();
        }
        
        //update location and animation
        super.update(time);
    }
    
    private void locateDestination()
    {
        if (getX() < getDestinationX())
        {
            if (getX() + getSpeedWalk() > getDestinationX())
            {
                setX(getDestinationX());
                resetVelocityX();
            }
            else
            {
                setVelocityX(getSpeedWalk());
            }
        }
        else if (getX() > getDestinationX())
        {
            if (getX() - getSpeedWalk() < getDestinationX())
            {
                setX(getDestinationX());
                resetVelocityX();
            }
            else
            {
                setVelocityX(-getSpeedWalk());
            }
        }
        
        if (getY() < getDestinationY())
        {
            if (getY() + getSpeedWalk() > getDestinationY())
            {
                setY(getDestinationY());
                resetVelocityY();
            }
            else
            {
                setVelocityY(getSpeedWalk());
            }
        }
        else if (getY() > getDestinationY())
        {
            if (getY() - getSpeedWalk() < getDestinationY())
            {
                setY(getDestinationY());
                resetVelocityY();
            }
            else
            {
                setVelocityY(-getSpeedWalk());
            }
        }
        
        //if we have reached our destination
        if (getX() == getDestinationX() && getY() == getDestinationY())
        {
            //now idle, and stop moving
            setIdle(true);
            resetVelocity();
        }
    }
    
    private void checkLocation(final Map map)
    {
        if (hasVelocityY())
        {
            if (getVelocityY() > 0)
            {
                if (!map.hasBounds(getX(), getY() + (getHeight() / 2)))
                {
                    //stop horizontal movement
                    resetVelocityX();
                }
                    
                //check to see if needs to be reset at top
                if (getY() + (getHeight() / 2) >= Map.ROWS * Map.BLOCK_SIZE)
                    setY(-getHeight());
            }
        }
    }
    
    private void checkHorizontalCollision(final Map map)
    {
        if (super.hasVelocityX())
        {
            if (super.getVelocityX() > 0)
            {
                if (map.hasHorizontalCollision(getX() + (getWidth() / 2), getY()))
                    super.resetVelocityX();
            }
            else
            {
                if (map.hasHorizontalCollision(getX() - (getWidth() / 2), getY()))
                    super.resetVelocityX();
            }
        }
    }
    
    /**
     * Apply gravity to entity
     * @param map Map that contains the boundaries
     */
    private void applyGravity(final Map map)
    {
        if (map.hasSouthCollision(getX(), getY() + (getHeight() / 2)) && 
            !map.hasSouthCollision(getX(), getY() - (getHeight() / 2)) && 
            !map.hasSouthCollision(getX(), getY()) && 
            !isJumping())
        {
            //stop falling
            resetVelocityY();
            
            //set back to idle
            if (isFalling())
                setIdle(true);
        }
        else
        {
            //if there is nothing below us and we aren't falling
            if (!hasVelocityY())
            {
                //start falling
                super.setVelocityY(VELOCITY_Y_CHANGE);
                
                //flag we are falling
                setFall(true);
            }
            else
            {
                //adjust velocity y
                setVelocityY(getVelocityY() + VELOCITY_Y_CHANGE);
                
                if (getVelocityY() > 0)
                {
                    //flag we are falling, if not attacking
                    if (!isAttacking())
                        setFall(true);
                    
                    //limit fall speed
                    if (getVelocityY() > MAX_SPEED_FALL)
                        setVelocityY(MAX_SPEED_FALL);
                }
                else
                {
                    //flag we are jumping
                    setJump(true);
                }
            }
        }
    }
    
    public void render(final Graphics graphics)
    {
        final double x = getX();
        final double y = getY();
        
        super.setX(x - (getWidth() / 2));
        super.setY(y - (getHeight() / 2));
        
        //draw character
        super.draw(graphics);
        
        super.setX(x);
        super.setY(y);
        
        if (projectile != null)
        {
            //draw projectile
            projectile.draw(graphics, getImage());
        }
    }
}