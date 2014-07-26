package com.gamesbykevin.bubblebobble2.enemies;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.bubblebobble2.character.Character;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.framework.util.Timers;

public final class Enemy extends Character implements Disposable
{
    //speed to move
    private static final double DEFAULT_SPEED_WALK = .25;
    private static final double DEFAULT_SPEED_RUN = .75;
    
    //default time delay for animations
    private static final long DEFAULT_DELAY = Timers.toNanoSeconds(250L);
    
    //time the enemy is captured
    private static final long DEFAULT_DELAY_CAPTURED = Timers.toNanoSeconds(3250L);
    
    //dimension size of enemy
    private static final int WIDTH = 18;
    private static final int HEIGHT = 18;
    
    //is the enemy angry
    private boolean angry = false;
    
    //is the enemy captured
    private boolean capture = false;
    
    //the different opponents
    public enum Type
    {
        Opponent1(false, DEFAULT_SPEED_WALK, DEFAULT_SPEED_RUN), 
        Opponent2(false, DEFAULT_SPEED_WALK, DEFAULT_SPEED_RUN), 
        Opponent3(false, DEFAULT_SPEED_WALK, DEFAULT_SPEED_RUN), 
        Opponent4(false, DEFAULT_SPEED_WALK, DEFAULT_SPEED_RUN), 
        Opponent5(false, DEFAULT_SPEED_WALK, DEFAULT_SPEED_RUN), 
        Opponent6(false, DEFAULT_SPEED_WALK, DEFAULT_SPEED_RUN), 
        Opponent7(false, DEFAULT_SPEED_WALK, DEFAULT_SPEED_RUN), 
        Opponent8(true,  DEFAULT_SPEED_WALK, DEFAULT_SPEED_RUN);
        
        //can this type shoot a projectile
        private final boolean shootProjectile;
        
        private final double moveWalk, moveRun;
        
        private Type(final boolean shootProjectile, final double moveWalk, final double moveRun)
        {
            this.shootProjectile = shootProjectile;
            
            this.moveWalk = moveWalk;
            this.moveRun = moveRun;
        }
        
        private boolean canShootProjectile()
        {
            return this.shootProjectile;
        }
    }

    //the different animations
    public enum Animations
    {
        Idle, Moving, MovingAngry, Captured, CapturedAngry, Destroyed
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
    
    //the type of opponent
    private final Type type;
    
    protected Enemy(final Type type)
    {
        super(type.moveWalk, type.moveRun);
        
        this.type = type;
        
        //setup animations
        setupAnimations();
    }
    
    protected Type getType()
    {
        return this.type;
    }
    
    @Override
    public void addProjectile()
    {
        //if can't shoot projectile don't continue
        if (!type.canShootProjectile())
            return;
    }
    
    @Override
    public void setupAnimations()
    {
        final int y;
        
        switch (getType())
        {
            case Opponent1:
                y = 0 * HEIGHT;
                break;
                
            case Opponent2:
                y = 1 * HEIGHT;
                break;
                
            case Opponent3:
                y = 2 * HEIGHT;
                break;
                
            case Opponent4:
                y = 3 * HEIGHT;
                break;
                
            case Opponent5:
                y = 4 * HEIGHT;
                break;
                
            case Opponent6:
                y = 5 * HEIGHT;
                break;
                
            case Opponent7:
                y = 6 * HEIGHT;
                break;
                
            case Opponent8:
                y = 7 * HEIGHT;
                break;
                
            default:
                y = 0;
                break;
        }
        
        super.addAnimation(Animations.Idle, 1, 0, y, WIDTH, HEIGHT, 0, false);
        super.addAnimation(Animations.Moving, 2, 18, y, WIDTH, HEIGHT, DEFAULT_DELAY, true);
        super.addAnimation(Animations.MovingAngry, 2, 54, y, WIDTH, HEIGHT, DEFAULT_DELAY, true);
        super.addAnimation(Animations.Destroyed, 4, 90, y, WIDTH, HEIGHT, DEFAULT_DELAY, true);
        super.addAnimation(Animations.Captured, 1, 162, y, WIDTH, HEIGHT, DEFAULT_DELAY_CAPTURED, false);
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

        if (isAngry())
        {
            if (isWalking() || isJumping() || isFalling())
                setAnimation(Animations.MovingAngry);
            
            if (isCaptured())
                setAnimation(Animations.CapturedAngry);
        }
        else
        {
            if (isWalking() || isJumping() || isFalling())
                setAnimation(Animations.Moving);
            
            if (isCaptured())
                setAnimation(Animations.Captured);
        }
        
        if (isDead())
            setAnimation(Animations.Destroyed);
    }
    
    @Override
    public void update(final Engine engine)
    {
        super.update(engine);
        
        //if we aren't starting
        if (!isStarting())
        {
            if (!isDead())
            {
                
            }
        }
        
        //set the correct animation
        correctAnimation();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
}