package com.gamesbykevin.bubblebobble2.enemies;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.bubblebobble2.character.Character;
import com.gamesbykevin.bubblebobble2.maps.Map;
import com.gamesbykevin.bubblebobble2.projectile.Projectile;

import com.gamesbykevin.framework.util.Timers;

public abstract class Enemy extends Character implements Disposable
{
    //speed to move
    private static final double DEFAULT_SPEED_WALK = .25;
    private static final double DEFAULT_SPEED_RUN = .5;
    
    //default time delay for animations
    private static final long DEFAULT_DELAY = Timers.toNanoSeconds(250L);
    
    //time delay for death animation
    private static final long DEATH_DELAY = Timers.toNanoSeconds(100L);
    
    //time the enemy is captured
    private static final long DEFAULT_DELAY_CAPTURED = Timers.toNanoSeconds(5000L);
    
    //dimension size of enemy
    private static final int WIDTH = 18;
    private static final int HEIGHT = 18;
    
    //is the enemy angry
    private boolean angry = false;
    
    //is the enemy captured
    private boolean capture = false;
    
    //the different animations
    public enum Animations
    {
        Idle, Moving, MovingAngry, Captured, CapturedAngry, Destroyed
    }
    
    public enum Type
    {
        BubbleBuster, Incendo, Beluga, Stoner, Coiley, Hullaballoon, SuperSocket, WillyWhistle
    }
    
    private final Type type;
    
    protected Enemy(final Type type)
    {
        //store enemy type
        this.type = type;
        
        //set default speed(s)
        super.setSpeedRun(DEFAULT_SPEED_RUN);
        super.setSpeedWalk(DEFAULT_SPEED_WALK);
        
        //setup animations
        setupAnimations();
    }
    
    private Type getType()
    {
        return this.type;
    }
    
    protected void setAngry(final boolean angry)
    {
        this.angry = angry;
    }
    
    protected boolean isAngry()
    {
        return this.angry;
    }
    
    protected void setCapture(final boolean capture)
    {
        this.capture = capture;
    }
    
    protected boolean isCaptured()
    {
        return this.capture;
    }
    
    /**
     * Is enemy finally dead? So they can be removed
     * @return true if isDead() and is no longer moving, false otherwise
     */
    protected boolean hasDeathFinished()
    {
        return (isDead() && !hasVelocity());
    }
    
    @Override
    public void addProjectile()
    {
        //if can't shoot projectile don't continue
        if (!canShootProjectile())
            return;
        
        
    }
    
    @Override
    protected boolean checkProjectileCollision(final Projectile projectile)
    {
        //if this is true don't check for projectile collision
        if (isCaptured() || isStarting())
            return false;
        
        //make sure projectile is close enough to hit the enemy
        if (getDistance(projectile) <= projectile.getWidth() / 2)
        {
            //reset captured timers
            setAnimation(Animations.Captured, true);
            setAnimation(Animations.CapturedAngry, true);

            //mark as captured
            setCapture(true);

            //return true since there was collision
            return true;
        }
        
        //anything else return false
        return false;
    }
    
    @Override
    protected void setupAnimations()
    {
        final int y;
        
        switch (getType())
        {
            case BubbleBuster:
                y = 0 * HEIGHT;
                break;
                
            case Incendo:
                y = 1 * HEIGHT;
                break;
                
            case Beluga:
                y = 2 * HEIGHT;
                break;
                
            case Stoner:
                y = 3 * HEIGHT;
                break;
                
            case Coiley:
                y = 4 * HEIGHT;
                break;
                
            case Hullaballoon:
                y = 5 * HEIGHT;
                break;
                
            case SuperSocket:
                y = 6 * HEIGHT;
                break;
                
            case WillyWhistle:
                y = 7 * HEIGHT;
                break;
                
            default:
                y = 0;
                break;
        }
        
        super.addAnimation(Animations.Idle,          1, 0, y,   WIDTH, HEIGHT, 0, false);
        super.addAnimation(Animations.Moving,        2, 18, y,  WIDTH, HEIGHT, DEFAULT_DELAY, true);
        super.addAnimation(Animations.MovingAngry,   2, 54, y,  WIDTH, HEIGHT, DEFAULT_DELAY, true);
        super.addAnimation(Animations.Destroyed,     4, 90, y,  WIDTH, HEIGHT, DEATH_DELAY, true);
        super.addAnimation(Animations.Captured,      1, 162, y, WIDTH, HEIGHT, DEFAULT_DELAY_CAPTURED, false);
        super.addAnimation(Animations.CapturedAngry, 1, 180, y, WIDTH, HEIGHT, DEFAULT_DELAY_CAPTURED, false);
        
        //set animation
        super.setAnimation(Animations.Idle);
        
        //set dimension size
        super.setDimensions();
    }

    @Override
    protected void correctAnimation()
    {
        if (isStarting())
            setAnimation(Animations.Idle);

        if (isWalking() || isJumping() || isFalling())
            setAnimation(isAngry() ? Animations.MovingAngry : Animations.Moving);
            
        if (isCaptured())
            setAnimation(isAngry() ? Animations.CapturedAngry : Animations.Captured);
        
        if (isDead())
            setAnimation(Animations.Destroyed);
    }
    
    protected void manageDeath(final Map map, final long time)
    {
        //did we have east or west collision
        boolean east = false, west = false;
        
        if (hasVelocityX())
        {
            if (getVelocityX() > 0)
            {
                //if moving east and has map collision
                east = map.hasEastCollision(getX());
            }
            else
            {
                //if moving west and has map collision
                west = map.hasWestCollision(getX());
            }
            
            //if hit either side, move in opposite direction
            if (east || west)
                setVelocityX(-getVelocityX());
        }
        
        if (hasVelocityY())
        {
            if (getVelocityY() < 0)
            {
                if (map.hasNorthCollision(getY()))
                    setVelocityY(-getVelocityY());
            }
            else
            {
                //can't check for side collision if hit east or west sides
                if (!east && !west)
                {
                    //hit the ground so stop moving
                    if (map.hasSouthCollision(getX(), getY()) && map.getRow(getY()) > Map.BOUNDARY_ROW_MIN)
                    {
                        resetVelocity();
                    }
                    else
                    {
                        //if object is moving south it should spawn at top if out of bounds
                        checkLocation(map);
                    }
                }
            }
        }
        
        //update location and animation
        update(time);
        
        //set the correct animation
        correctAnimation();
    }
    
    protected void manageCapture()
    {
        //stop moving
        resetVelocityX();

        //move upwards
        setVelocityY(-getSpeedWalk());

        //update location
        super.update();

        //check timer to determine when enemy is to be freed
        if (isCaptured() && isAnimationFinished())
        {
            //no longer captured
            setCapture(false);

            //enemy should now be angry
            setAngry(true);
        }
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
}