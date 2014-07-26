package com.gamesbykevin.bubblebobble2.projectile;

import com.gamesbykevin.bubblebobble2.character.Character;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.hero.Hero;
import com.gamesbykevin.bubblebobble2.maps.Map;
import com.gamesbykevin.framework.util.Timers;

public final class Bubble extends Projectile
{
    public enum Key
    {
        //when the projectile is first added
        Begin, 
        
        //when the projectile is idle
        Middle,
        
        //when the projectile dies
        End
    }
    
    //speed the projectile will move
    private static final double VELOCITY_X = (Hero.SPEED_WALK * .75);
    private static final double VELOCITY_Y = (Hero.SPEED_WALK * .50);
    
    //dimensions
    private static final int WIDTH  = 16;
    private static final int HEIGHT = 16;
    
    //how long each frame is
    private static final long BEGIN_DELAY  = Timers.toNanoSeconds(200L);
    private static final long MIDDLE_DELAY = Timers.toNanoSeconds(3250L);
    private static final long END_DELAY    = Timers.toNanoSeconds(175L);
    
    public Bubble(final boolean east)
    {
        //set the speed
        super.setVelocityX(east ? VELOCITY_X : -VELOCITY_X);
        
        //setup animations
        setupAnimations();
    }
    
    @Override
    protected void setupAnimations()
    {
        //setup the animations for the bubble
        super.addAnimation(Key.Begin,  4, 0,  64, WIDTH, HEIGHT, BEGIN_DELAY,  false);
        super.addAnimation(Key.Middle, 1, 48, 64, WIDTH, HEIGHT, MIDDLE_DELAY, false);
        super.addAnimation(Key.End,    2, 64, 64, WIDTH, HEIGHT, END_DELAY,    false);
        
        //set the current animation
        super.setAnimation(Key.Begin);
        
        //set the dimension
        super.setDimensions();
    }
    
    @Override
    public boolean canStand()
    {
        return (super.isAnimation(Key.Middle));
    }
    
    @Override
    public boolean canDiscard()
    {
        return (super.isAnimation(Key.End) && super.isAnimationFinished());
    }
    
    @Override
    protected void checkCharacterCollision(final Character character)
    {
        //can only interact when using middle animation
        if (!isAnimation(Key.Middle))
            return;
        
        //check character collision here
    }
    
    @Override
    public void checkParentCollision(final Character character)
    {
        //we only want to check if the projectile is in the middle animation
        if (!isAnimation(Key.Middle))
            return;
        
        //only check the character if falling or attacking
        if (!character.isFalling() && !character.isAttacking())
            return;
        
        //make sure the character is in range
        if (character.getX() < getX() - (getWidth() / 2))
            return;
        
        //make sure the character is in range
        if (character.getX() > getX() + (getWidth() / 2))
            return;
        
        //make sure the character is in range
        if (character.getY() >= getY())
            return;
        
        //make sure the character is in range
        if (character.getY() <= getY() - getHeight())
            return;
        
        //set idle if not attacking
        if (!character.isAttacking())
            character.setIdle(true);
        
        //set the character right above the projectile
        character.setY(getY() - character.getHeight());
    }
    
    private void setupEndAnimation()
    {
        super.setAnimation(Key.End, true);
        super.resetVelocity();
    }
    
    private void setupMiddleAnimation()
    {
        super.setAnimation(Key.Middle, true);
        super.resetVelocity();
        super.setVelocityY(-VELOCITY_Y);
    }
    
    @Override
    protected void checkMapCollision(final Map map)
    {
        if (super.hasVelocityX())
        {
            if (super.getVelocityX() > 0)
            {
                if (map.hasHorizontalCollision(getX() + (getWidth() / 2),  getY()) || 
                    map.hasHorizontalCollision(getX() + (getWidth() / 2),  getY() - (getHeight() / 2)))
                {
                    setupMiddleAnimation();
                }
            }
            else
            {
                if (map.hasHorizontalCollision(getX() - (getWidth() / 2), getY()) || 
                    map.hasHorizontalCollision(getX() - (getWidth() / 2), getY() - (getHeight() / 2)))
                {
                    setupMiddleAnimation();
                }
            }
        }
        
        if (super.hasVelocityY())
        {
            if (map.hasNorthCollision(getX(), getY() - (getHeight() / 2)))
                super.resetVelocityY();
        }
    }
    
    @Override
    public void update(final Engine engine)
    {
        //update location and animation
        super.update(engine.getMain().getTime());
        
        //check map collision
        checkMapCollision(engine.getManager().getMaps().getMap());
        
        //set the correct animation once finished with current
        if (isAnimationFinished())
        {
            if (isAnimation(Key.Begin))
            {
                setupMiddleAnimation();
            }
            else if (isAnimation(Key.Middle))
            {
                setupEndAnimation();
            }
        }
        
        //check for collision with any characters
        //checkCharacterCollision();
    }
}