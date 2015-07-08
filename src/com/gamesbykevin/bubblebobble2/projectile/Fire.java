package com.gamesbykevin.bubblebobble2.projectile;

import com.gamesbykevin.bubblebobble2.character.Character;
import com.gamesbykevin.bubblebobble2.enemies.Enemies;
import com.gamesbykevin.bubblebobble2.enemies.Enemy;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.hero.Hero;
import com.gamesbykevin.bubblebobble2.maps.Map;
import com.gamesbykevin.framework.util.Timers;

public final class Fire extends Projectile
{
    //speed the projectile will move
    private static final double VELOCITY_X = (Enemy.DEFAULT_SPEED_RUN * Enemies.DEAD_MULTIPLIER);
    
    //dimensions
    private static final int WIDTH  = 18;
    private static final int HEIGHT = 18;
    
    //how long the projectile is valid for
    private static final long DELAY  = Timers.toNanoSeconds(3000L);
    
    private static final String KEY = "Default";
    
    public Fire(final boolean east) throws Exception
    {
        super();
        
        //set the speed
        setVelocityX(east ? -VELOCITY_X : VELOCITY_X);
    }
    
    @Override
    protected void setupAnimations() throws Exception
    {
        //setup the animations for the bubble
        super.addAnimation(KEY,  1, 198, 0, WIDTH, HEIGHT, DELAY,  false);
    }
    
    @Override
    public boolean canStand()
    {
        return (false);
    }
    
    @Override
    public boolean canDiscard() throws Exception
    {
        //discard once animation finished
        return (super.isAnimationFinished());
    }
    
    @Override
    public void markDiscard() throws Exception
    {
        //mark animation as finished
        super.getSpriteSheet().getSpriteSheetAnimation().setFinished(true);
    }
    
    @Override
    public boolean canAttack()
    {
        //can always attack
        return true;
    }
    
    @Override
    public void checkParentCollision(final Character character)
    {
        //nothing will change if collision with parent that created projectile
    }
    
    @Override
    protected void checkMapCollision(final Map map) throws Exception
    {
        if (!map.hasBounds(getX(), getY()))
            markDiscard();
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //update location and animation
        super.update(engine.getMain().getTime());
        
        if (hasVelocityX())
            setHorizontalFlip((getVelocityX() > 0) ? false : true);
        
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