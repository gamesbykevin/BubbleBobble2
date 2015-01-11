package com.gamesbykevin.bubblebobble2.enemies;

import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.hero.Hero;
import com.gamesbykevin.bubblebobble2.projectile.Laser;
import com.gamesbykevin.bubblebobble2.projectile.Projectile;

public final class SuperSocket extends Enemy
{
    protected SuperSocket()
    {
        super(Enemy.Type.SuperSocket);
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
                        
                        //the hero to attack
                        Hero hero = engine.getManager().getHero();
                        
                        //make sure hero is below enemy
                        if (hero.getY() > getY())
                        {
                            if (hero.getX() > getX() - getWidth() && hero.getX() < getX() + getWidth())
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
        
        //stop moving
        resetVelocityX();
        
        try
        {
            final Projectile projectile = new Laser(isAngry());

            //set the location
            projectile.setLocation(getX(), getY() + (getHeight() / 2));

            //set the image of the projectile
            projectile.setImage(getImage());

            //add projectile
            super.addProjectile(projectile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}