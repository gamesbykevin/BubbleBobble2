package com.gamesbykevin.bubblebobble2.enemies;

import com.gamesbykevin.bubblebobble2.engine.Engine;

public class Hullaballoon extends Enemy
{
    protected Hullaballoon()
    {
        super(Enemy.Type.Hullaballoon);
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
                    super.manageCapture();
                }
                else
                {
                    if (!isJumping() && !isFalling())
                    {
                        if (!hasVelocityX())
                        {
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
}