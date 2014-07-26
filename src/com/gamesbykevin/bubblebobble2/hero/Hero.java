package com.gamesbykevin.bubblebobble2.hero;

import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.bubblebobble2.character.Character;
import com.gamesbykevin.bubblebobble2.projectile.*;

public final class Hero extends Character
{
    public enum Type
    {
        Hero1, Hero2
    }
    
    //the type of hero
    private Type type;
    
    public enum Animations
    {
        Idle, Walk, Jump, Fall, Attack, Die, Start
    }
    
    //the max number of projectiles
    private static final int PROJECTILE_LIMIT = 5;
    
    public static final double SPEED_WALK = 1;
    
    private static final long DELAY_NONE = 0;
    private static final long DELAY_WALK = Timers.toNanoSeconds(175L);
    private static final long DELAY_JUMP = Timers.toNanoSeconds(250L);
    private static final long DELAY_FALL = Timers.toNanoSeconds(250L);
    private static final long DELAY_ATTACK = Timers.toNanoSeconds(333L);
    private static final long DELAY_DIE = Timers.toNanoSeconds(333L);
    private static final long DELAY_START = Timers.toNanoSeconds(500L);
    
    public Hero(final Type type)
    {
        super(SPEED_WALK, SPEED_WALK);
        
        //store type of hero
        this.type = type;
        
        //set the projectile limit
        setProjectileLimit(PROJECTILE_LIMIT);
        
        //setup animations
        setupAnimations();
    }
    
    public Type getType()
    {
        return this.type;
    }
    
    @Override
    protected void setupAnimations()
    {
        switch (type)
        {
            case Hero1:
                super.addAnimation(Animations.Idle,   1, 0,  0,  16, 16, DELAY_NONE, false);
                super.addAnimation(Animations.Walk,   4, 0,  0,  16, 16, DELAY_WALK, true);
                super.addAnimation(Animations.Jump,   2, 0,  16, 16, 16, DELAY_JUMP, true);
                super.addAnimation(Animations.Fall,   2, 32, 16, 16, 16, DELAY_FALL, true);
                super.addAnimation(Animations.Attack, 1, 0,  32, 16, 16, DELAY_ATTACK, false);
                super.addAnimation(Animations.Die,    6, 0,  48, 16, 16, DELAY_DIE, false);
                super.addAnimation(Animations.Start,  2, 0,  80, 28, 32, DELAY_START, true);
                break;
                
            case Hero2:
                super.addAnimation(Animations.Idle,   1, 0,  112,  16, 16, DELAY_NONE, false);
                super.addAnimation(Animations.Walk,   4, 0,  112,  16, 16, DELAY_WALK, true);
                super.addAnimation(Animations.Jump,   2, 0,  128, 16, 16, DELAY_JUMP, true);
                super.addAnimation(Animations.Fall,   2, 32, 128, 16, 16, DELAY_FALL, true);
                super.addAnimation(Animations.Attack, 1, 0,  144, 16, 16, DELAY_ATTACK, false);
                super.addAnimation(Animations.Die,    6, 0,  160, 16, 16, DELAY_DIE, false);
                super.addAnimation(Animations.Start,  2, 0,  192, 28, 32, DELAY_START, true);
                super.setHorizontalFlip(true);
                break;
        }
        
        //stop movement
        super.resetVelocity();
        
        //set dimensions
        super.setDimensions();
        
        //flag we are starting
        super.setStart(true);
    }
    
    @Override
    public void addProjectile()
    {
        final Projectile projectile = new Bubble(!hasHorizontalFlip());
        
        //set the location
        projectile.setLocation(getX(), getY());
        
        //set the image of the projectile
        projectile.setImage(getImage());
        
        //add projectile
        super.addProjectile(projectile);
    }
    
    @Override
    protected void correctAnimation()
    {
        if (!isAttacking())
        {
            if (isStarting())
                setAnimation(Animations.Start);
            
            if (isIdle())
                setAnimation(Animations.Idle);

            if (isWalking())
                setAnimation(Animations.Walk);

            if (isJumping())
                setAnimation(Animations.Jump);

            if (isFalling())
                setAnimation(Animations.Fall);
        
            if (isDead())
                setAnimation(Animations.Die);
        }
        else
        {
            //if this is not the current animation
            if (!isAnimation(Animations.Attack))
                setAnimation(Animations.Attack, true);
            
            //if animation finished
            if (isAnimationFinished())
            {
                //set to idle
                setAnimation(Animations.Idle);
                
                //no longer attacking
                setIdle(true);
            }
        }
    }
}