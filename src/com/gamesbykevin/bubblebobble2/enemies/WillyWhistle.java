package com.gamesbykevin.bubblebobble2.enemies;

import com.gamesbykevin.bubblebobble2.engine.Engine;

public final class WillyWhistle extends Enemy
{
    protected WillyWhistle() throws Exception
    {
        super(Enemy.Type.WillyWhistle);
    }
    
    @Override
    public void update(final Engine engine) throws Exception
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
    }
}