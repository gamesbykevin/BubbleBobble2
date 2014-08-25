package com.gamesbykevin.bubblebobble2.character;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.entity.Entity;
import com.gamesbykevin.bubblebobble2.input.Input;
import com.gamesbykevin.bubblebobble2.maps.Map;
import com.gamesbykevin.bubblebobble2.projectile.Projectile;
import com.gamesbykevin.bubblebobble2.shared.IElement;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public abstract class Character extends Entity implements Disposable, IElement
{
    //the projectile the character throws
    private List<Projectile> projectiles;
    
    //how many projectiles are we limited to, default is 1
    private int limit = 1;
    
    //the speed at which to walk, run
    private double speedWalk = 0, speedRun = 0;
    
    //no movement
    public static final double SPEED_NONE = 0;
    
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
    
    protected Character()
    {
        //create container for projectiles
        this.projectiles = new ArrayList<>();
    }
    
    protected void setSpeedWalk(final double speedWalk)
    {
        this.speedWalk = speedWalk;
    }
    
    protected void setSpeedRun(final double speedRun)
    {
        this.speedRun = speedRun;
    }
    
    private List<Projectile> getProjectiles()
    {
        return this.projectiles;
    }
    
    public void removeProjectiles()
    {
        getProjectiles().clear();
    }
    
    public void setProjectileLimit(final int limit)
    {
        this.limit = limit;
    }
    
    /**
     * Can this character shoot projectiles
     * @return true if the projectile limit is greater than 0
     */
    protected boolean canShootProjectile()
    {
        return (getProjectileLimit() > 0);
    }
    
    private int getProjectileLimit()
    {
        return this.limit;
    }
    
    /**
     * Set the destination where the character will be placed
     * @param destinationX x-coordinate
     * @param destinationY y-coordinate
     */
    public void setDestination(final Point destination)
    {
        setDestinationX(destination.x);
        setDestinationY(destination.y);
    }
    
    public void setDestinationX(final int destinationX)
    {
        this.destinationX = destinationX;
    }
    
    public void setDestinationY(final int destinationY)
    {
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
        return (!isAttacking() && !isDead() && getProjectiles().size() < getProjectileLimit());
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
    
    /**
     * Make sure the appropriate animation is set
     */
    protected abstract void correctAnimation();
    
    /**
     * Add projectile
     */
    public abstract void addProjectile();
    
    @Override
    public void update(final Engine engine)
    {
        final Map map = engine.getManager().getMaps().getMap();
        
        //make sure the character is not being placed
        if (!isStarting())
        {
            //apply gravity
            applyGravity(map);

            //check for side collision
            checkHorizontalCollision(map);

            //perform any final adjustments to ensure character is in bounds
            checkLocation(map);
            
            if (!getProjectiles().isEmpty())
            {
                for (int i = 0; i < getProjectiles().size(); i++)
                {
                    Projectile projectile = getProjectiles().get(i);
                    
                    //manage the collision of projectile with the parent that created it
                    projectile.checkParentCollision(this);
                    
                    //update projectile
                    projectile.update(engine);
                    
                    //if we can discard the projectile
                    if (projectile.canDiscard())
                    {
                        getProjectiles().remove(i);
                        i--;
                    }
                }
            }
        }
        else
        {
            //go to our destination
            locateDestination();
        }
        
        //update location and animation
        update(engine.getMain().getTime());
        
        //set the correct animation
        correctAnimation();
    }
    
    /**
     * How do we manage projectile collision
     * @param projectile The projectile we are checking for collision
     * @return true if collision occurred, false otherwise
     */
    protected abstract boolean checkProjectileCollision(final Projectile projectile);
    
    /**
     * Add projectile to list
     * @param projectile Projectile we want to add into play
     */
    protected void addProjectile(final Projectile projectile)
    {
        getProjectiles().add(projectile);
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
            if (getX() - getSpeedWalk() <= getDestinationX())
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
            if (getY() - getSpeedWalk() <= getDestinationY())
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
    
    protected void checkLocation(final Map map)
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
                if (map.hasHorizontalCollision(getX() + (getWidth() / 2), getY() + (getHeight() / 4)))
                    super.resetVelocityX();
            }
            else
            {
                if (map.hasHorizontalCollision(getX() - (getWidth() / 2), getY() + (getHeight() / 4)))
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
        //we hit the ground if there is south collision and we are not jumping
        final boolean hitGround = map.hasSouthCollision(getX(), getY() + (getHeight() / 2)) && !isJumping();
        
        //is the character at the top of the screen
        final boolean atTop = (map.getRow(getY() + (getHeight() / 2)) <= Map.BOUNDARY_ROW_MIN);
        
        //is the character stuck in a block
        final boolean stuck = map.hasSouthCollision(getX(), getY() + (getHeight() / 2)) && map.hasSouthCollision(getX(), getY());
        
        //if we hit the ground and we are not stuck and not at the top of the screen
        if (hitGround && !stuck && !atTop)
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
    
    @Override
    public void render(final Graphics graphics)
    {
        if (!getProjectiles().isEmpty())
        {
            for (int i = 0; i < getProjectiles().size(); i++)
            {
                getProjectiles().get(i).render(graphics);
            }
        }
        
        super.render(graphics);
    }
}