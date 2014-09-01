package com.gamesbykevin.bubblebobble2.enemies;

import static com.gamesbykevin.bubblebobble2.enemies.Enemy.DEFAULT_SPEED_RUN;
import static com.gamesbykevin.bubblebobble2.enemies.Enemy.DEFAULT_SPEED_WALK;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.hero.Hero;
import com.gamesbykevin.bubblebobble2.maps.Map;

public class Beluga extends Enemy
{
    protected Beluga()
    {
        super(Enemy.Type.Beluga);
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
                    if (!hasVelocityX())
                    {
                        Hero hero = engine.getManager().getHero();
                        
                        if (hero.getX() > getX())
                        {
                            setVelocityX(isAngry()? getSpeedRun() : getSpeedWalk());
                        }
                        else
                        {
                            setVelocityX(isAngry()? -getSpeedRun() : -getSpeedWalk());
                        }
                    }
                    
                    if (!hasVelocityY())
                    {
                        setJump(true);
                        setVelocityY((getVelocityX() < 0) ? -getVelocityX() : getVelocityX());
                    }
                    
                    final double vx = super.getVelocityX();
                    
                    checkHorizontalCollision(engine.getManager().getMaps().getMap());
                    
                    //if we were moving and are no more switch directions
                    if (!hasVelocityX())
                        setVelocityX(-vx);
                        
                    //make sure we are facing the correct direction
                    setHorizontalFlip((getVelocityX() > 0) ? true : false);

                    if (hasVelocityY())
                    {
                        Map map = engine.getManager().getMaps().getMap();
                        
                        if (getVelocityY() < 0)
                        {
                            if (map.hasNorthCollision(getX(), getY() - (getHeight() / 2)))
                                setVelocityY(-getVelocityY());
                        }
                        else
                        {
                            //hit the ground so move opposite
                            if (map.hasSouthCollision(getX(), getY() + (getHeight() / 2)) && map.getRow(getY()) > Map.BOUNDARY_ROW_MIN)
                            {
                                setVelocityY(-getVelocityY());
                            }
                            else
                            {
                                //if object is moving south it should spawn at top if out of bounds
                                checkLocation(map);
                            }
                        }
                    }

                    //update location and animation
                    update(engine.getMain().getTime());

                    //set the correct animation
                    correctAnimation();
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