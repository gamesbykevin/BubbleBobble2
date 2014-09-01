package com.gamesbykevin.bubblebobble2.enemies;

import static com.gamesbykevin.bubblebobble2.enemies.Enemy.DEFAULT_SPEED_RUN;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.hero.Hero;
import com.gamesbykevin.bubblebobble2.projectile.SpikeBall;
import com.gamesbykevin.bubblebobble2.projectile.Projectile;

public final class Incendo extends Enemy
{
    protected Incendo()
    {
        super(Enemy.Type.Incendo);
        
        //player will always move fast
        super.setSpeedRun(DEFAULT_SPEED_RUN);
        super.setSpeedWalk(DEFAULT_SPEED_RUN * .8);
    }
    
    @Override
    public void update(final Engine engine)
    {
        //if we aren't starting
        if (!isStarting())
        {
            //if we aren't dead
            if (!isDead())
            {
                //update parent element
                super.update(engine);
                
                //if captured, prevent horizontal movement
                if (isCaptured())
                {
                    manageCapture(engine.getManager().getMaps().getMap());
                }
                else
                {
                    if (!isJumping() && !isFalling())
                    {
                        if (!hasVelocityX())
                        {
                            setWalk(true);
                            
                            if (engine.getRandom().nextBoolean())
                            {
                                setVelocityX(isAngry()? getSpeedRun() : getSpeedWalk());
                                setHorizontalFlip(true);
                            }
                            else
                            {
                                setVelocityX(isAngry()? -getSpeedRun() : -getSpeedWalk());
                                setHorizontalFlip(false);
                            }
                        }
                        
                        Hero hero = engine.getManager().getHero();
                        
                        final double distance = (hero.getY() > getY()) ? hero.getY() - getY() : getY() - hero.getY();
                        
                        //if close enough
                        if (distance <= hero.getHeight())
                        {
                            //if moving towards the hero, then we are facing them
                            if (hero.getX() > getX() && getVelocityX() > 0 || hero.getX() < getX() && getVelocityX() < 0)
                            {
                                //update timer
                                getTimer().update(engine.getMain().getTime());
                                
                                //add projectile
                                addProjectile();
                            }
                        }
                    }
                    else
                    {
                        //if jumping or falling don't move horizontal
                        super.resetVelocityX();
                    }
                }
            }
            else
            {
                manageDeath(engine.getManager().getMaps().getMap(), engine.getMain().getTime());
            }
        }
        else
        {
            //update parent element
            super.update(engine);
        }
        
        //set the correct animation
        correctAnimation();
    }
    
    @Override
    public void addProjectile()
    {
        //if can't shoot projectile don't continue
        if (!canShootProjectile() || !canAttack())
            return;
        
        //if enough time hasn't passed till next projectile
        if (!getTimer().hasTimePassed())
            return;
        
        //reset timer
        getTimer().reset();
        
        final Projectile projectile = new SpikeBall(!hasHorizontalFlip(), super.isAngry());
        
        //set the location
        projectile.setLocation(getX(), getY());
        
        //set the image of the projectile
        projectile.setImage(getImage());
        
        //add projectile
        super.addProjectile(projectile);
    }
}