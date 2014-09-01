package com.gamesbykevin.bubblebobble2.projectile;

import com.gamesbykevin.bubblebobble2.enemies.Enemies;
import com.gamesbykevin.bubblebobble2.enemies.Enemy;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.hero.Hero;
import com.gamesbykevin.bubblebobble2.maps.Map;
import com.gamesbykevin.framework.util.Timers;

public class Laser extends Projectile
{
    //speed the projectile will move
    private static final double VELOCITY_Y = (Enemy.DEFAULT_SPEED_RUN * Enemies.DEAD_MULTIPLIER);
    
    //dimensions
    private static final int WIDTH  = 18;
    private static final int HEIGHT = 18;
    
    //how long the projectile is valid for
    private static final long DELAY  = Timers.toNanoSeconds(3000L);
    
    private enum Key
    {
        Default, Angry
    }
    
    public Laser(final boolean angry)
    {
        super();
        
        //set the speed
        setVelocityY(VELOCITY_Y);
        
        //set the animation
        setAnimation((angry) ? Key.Angry : Key.Default);
    }
    
    @Override
    protected void setupAnimations()
    {
        //setup the animations for the spike ball
        super.addAnimation(Key.Default,  1, 216, 18, WIDTH, HEIGHT, DELAY,  false);
        
        //setup the animations for the angry spike ball
        super.addAnimation(Key.Angry,  1, 198, 18, WIDTH, HEIGHT, DELAY,  false);
    }
    
    @Override
    public boolean canStand()
    {
        return (false);
    }
    
    @Override
    public boolean canDiscard()
    {
        //discard once animation finished
        return (super.isAnimationFinished());
    }
    
    @Override
    public void markDiscard()
    {
        //mark animation as finished
        getSpriteSheet().getSpriteSheetAnimation().setFinished(true);
    }
    
    @Override
    public boolean canAttack()
    {
        //can always attack
        return true;
    }
    
    @Override
    public void checkParentCollision(final com.gamesbykevin.bubblebobble2.character.Character character)
    {
        //nothing will change if collision with parent that created projectile
    }
    
    @Override
    protected void checkMapCollision(final Map map)
    {
        if (!map.hasBounds(getX(), getY()))
            markDiscard();
    }
    
    @Override
    public void update(final Engine engine)
    {
        //update location and animation
        super.update(engine.getMain().getTime());
        
        //check collision with hero
        if (canAttack())
        {
            Hero hero = engine.getManager().getHero();
            
            //can't hurt if invincible
            if (hero.isInvincible())
                return;
            
            if (getDistance(hero) <= getWidth() / 2)
            {
                hero.setDead(true);
                markDiscard();
            }
        }
    }
}