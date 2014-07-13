package com.gamesbykevin.bubblebobble2.hero;

import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.bubblebobble2.character.Character;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.shared.IElement;


public final class Hero extends Character implements IElement
{
    public enum Type
    {
        Hero1, Hero2
    }
    
    public enum Animations
    {
        Idle, Walk, Jump, Fall, Attack, Die, Start
    }
    
    private static final double SPEED_WALK = 1;
    
    private static final long DELAY_NONE = 0;
    private static final long DELAY_WALK = Timers.toNanoSeconds(175L);
    private static final long DELAY_JUMP = Timers.toNanoSeconds(250L);
    private static final long DELAY_FALL = Timers.toNanoSeconds(250L);
    private static final long DELAY_ATTACK = Timers.toNanoSeconds(1000L);
    private static final long DELAY_DIE = Timers.toNanoSeconds(333L);
    private static final long DELAY_START = Timers.toNanoSeconds(500L);
    
    public Hero(final Type type)
    {
        super(SPEED_WALK, SPEED_WALK);
        
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
                break;
        }
        
        super.resetVelocity();
        super.setAnimation(Animations.Idle);
        super.setDimensions();
    }
    
    @Override
    public void update(final Engine engine)
    {
        super.update(engine.getManager().getMaps().getMap(), engine.getMain().getTime());
        
        //set the correct animation
        correctAnimation();
    }
    
    private void correctAnimation()
    {
        if (!isAttacking())
        {
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
            if (getSpriteSheet().hasFinished())
            {
                //set to idle
                setAnimation(Animations.Idle);
                
                //no longer attacking
                setIdle(true);
            }
        }
    }
}