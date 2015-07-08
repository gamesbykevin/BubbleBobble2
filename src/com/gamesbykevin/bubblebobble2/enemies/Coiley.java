package com.gamesbykevin.bubblebobble2.enemies;

import com.gamesbykevin.bubblebobble2.character.Character;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.hero.Hero;

public class Coiley extends Enemy
{
    protected Coiley() throws Exception
    {
        super(Enemy.Type.Coiley);
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
                //if captured, prevent horizontal movement
                if (isCaptured())
                {
                    //update parent element
                    super.update(engine);
                
                    //manage capture
                    manageCapture(engine.getManager().getMaps().getMap());
                }
                else
                {
                    Hero hero = engine.getManager().getHero();
                    
                    if (!hasVelocityX())
                    {
                        setWalk(true);
                        
                        if (hero.getX() > getX())
                        {
                            setVelocityX(isAngry()? getSpeedRun() : getSpeedWalk());
                            setHorizontalFlip(true);
                        }
                        else
                        {
                            setVelocityX(isAngry()? -getSpeedRun() : -getSpeedWalk());
                            setHorizontalFlip(false);
                        }
                        
                        //make sure not moving if out of bounds
                        super.checkLocation(engine.getManager().getMaps().getMap());
                    }
                    
                    //get velocity
                    final double vx = super.getVelocityX();
                    
                    //update parent element
                    super.update(engine);
                    
                    //if not moving switch directions
                    if (!hasVelocityX())
                        setVelocityX(-vx);
                    
                    //make sure facing correct direction
                    setHorizontalFlip((getVelocityX() > 0) ? true : false);
                    
                    if (!isJumping() && !isFalling())
                    {
                        //start jump
                        setJump(true);
                        setVelocityY(-Character.MAX_SPEED_JUMP / 2);
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