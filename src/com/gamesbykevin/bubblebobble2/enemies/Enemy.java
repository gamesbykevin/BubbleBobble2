package com.gamesbykevin.bubblebobble2.enemies;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timer;

import com.gamesbykevin.bubblebobble2.character.Character;
import com.gamesbykevin.bubblebobble2.maps.Map;
import com.gamesbykevin.bubblebobble2.projectile.Projectile;

import com.gamesbykevin.framework.util.Timers;

public abstract class Enemy extends Character implements Disposable
{
    //speed to move
    public static final double DEFAULT_SPEED_WALK = .25;
    public static final double DEFAULT_SPEED_RUN = .5;
    
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
    
    //timer to track rate of firing projectiles
    private Timer timer;
    
    //default time
    private static final long DEFAULT_SHOOT_DELAY = Timers.toNanoSeconds(1250L);
    
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
    
    protected Enemy(final Type type) throws Exception
    {
        //store enemy type
        this.type = type;
        
        //create new timer
        this.timer = new Timer();
        this.timer.setReset(DEFAULT_SHOOT_DELAY);
        this.timer.reset();
        
        //set default speed(s)
        super.setSpeedRun(DEFAULT_SPEED_RUN);
        super.setSpeedWalk(DEFAULT_SPEED_WALK);
        
        //setup animations
        setupAnimations();
    }
    
    /**
     * Get the timer used to determine rate of fire for projectiles
     * @return Object containing time remaining
     */
    protected Timer getTimer()
    {
        return this.timer;
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
    public abstract void addProjectile();
    
    @Override
    protected boolean checkProjectileCollision(final Projectile projectile) throws Exception
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
    protected void setupAnimations() throws Exception
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
        
        try
        {
            //set dimension size
            super.setDimensions();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void correctAnimation() throws Exception
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
    
    protected void manageDeath(final Map map, final long time) throws Exception
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
        
        //update location and animation
        update(time);
        
        //set the correct animation
        correctAnimation();
    }
    
    /**
     * Check if the captured phase is 
     * @param map The current map played
     */
    protected void manageCapture(final Map map) throws Exception
    {
        //don't continue if not captured
        if (!isCaptured())
            return;
        
        //stop moving
        resetVelocityX();

        //move upwards
        setVelocityY(-getSpeedWalk());

        //if at top of map stop moving
        if (map.hasNorthCollision(getY()))
            resetVelocity();
        
        //update location
        super.update();

        //check timer to determine when enemy is to escape
        if (isAnimationFinished())
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