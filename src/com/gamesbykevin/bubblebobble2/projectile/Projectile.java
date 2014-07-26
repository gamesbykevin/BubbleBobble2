package com.gamesbykevin.bubblebobble2.projectile;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.bubblebobble2.character.Character;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.entity.Entity;
import com.gamesbykevin.bubblebobble2.maps.Map;

public abstract class Projectile extends Entity implements Disposable
{
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    /**
     * How does the projectile manage collision
     * @param Character The character we are checking for collision
     */
    protected abstract void checkCharacterCollision(final Character character);
    
    /**
     * Check the collision with the map
     * @param map The current map played
     */
    protected abstract void checkMapCollision(final Map map);
    
    /**
     * Manage the collision between the projectile and the parent that it belongs to
     * @param character Parent that added the projectile
     */
    public abstract void checkParentCollision(final Character character);
    
    /**
     * Can the projectile be discarded?
     * @return true if projectile can be removed from character, false otherwise
     */
    public abstract boolean canDiscard();
    
    /**
     * Can the projectile be stood on
     * @return true if the character can stand on the projectile, false otherwise
     */
    public abstract boolean canStand();
    
    /**
     * Each projectile will have its own separate logic
     * @param engine Object containing all game elements
     */
    public abstract void update(final Engine engine);
}