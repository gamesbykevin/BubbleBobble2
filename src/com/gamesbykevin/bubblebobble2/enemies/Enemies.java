package com.gamesbykevin.bubblebobble2.enemies;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.bubblebobble2.character.Character;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.maps.Map;
import com.gamesbykevin.bubblebobble2.projectile.Projectile;
import com.gamesbykevin.bubblebobble2.shared.IElement;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Enemies implements Disposable, IElement
{
    //list of enemies in play
    private List<Enemy> enemies;
    
    //enemy spritesheet
    private Image image;
    
    //the number of enemies to spawn at once
    public static final int SPAWN_LIMIT = 4;
    
    private static final int DEAD_MULTIPLIER = 3;
    
    public Enemies(final Image image)
    {
        //create new container for the enemies
        this.enemies = new ArrayList<>();
        
        //store image
        this.image = image;
    }
    
    public Enemy getEnemy(final int index)
    {
        return this.enemies.get(index);
    }
    
    /**
     * Check the enemies if they collided with the character
     * @param character The character we want to check
     */
    private void checkCharacterCollision(final Character character)
    {
        //don't bother checking if dead or haven't started
        if (character.isDead() || character.isStarting())
            return;
        
        for (int i = 0; i < this.getCount(); i++)
        {
            Enemy enemy = getEnemy(i);
            
            //don't bother checking if dead or haven't started
            if (enemy.isDead() || enemy.isStarting())
                return;
            
            //check if the enemy has collided with the character
            if (enemy.getDistance(character) <= enemy.getWidth() / 2)
            {
                if (enemy.isCaptured())
                {
                    //mark enemy as dead
                    enemy.setDead(true);
                    
                    //move enemy same direction as the character
                    enemy.setVelocityX(character.getVelocityX() > 0 ? enemy.getSpeedRun() * DEAD_MULTIPLIER : -enemy.getSpeedRun() * DEAD_MULTIPLIER);
                    
                    //enemy always flies in the air
                    enemy.setVelocityY(-enemy.getSpeedRun());
                    
                    //remove projectiles
                    enemy.removeProjectiles();
                    
                    //character hurt the enemy
                    System.out.println("Enemy hurt");
                }
                else
                {
                    //enemy hurt the character
                    System.out.println("Character hurt");
                }
            }
        }
    }
    
    /**
     * Check the enemies for projectile collision<br>
     * The first enemy that collides with the projectile will be captured and the projectile will be removed
     * @param projectile The projectile we want to check
     */
    public void checkProjectileCollision(final Projectile projectile)
    {
        for (int i = 0; i < this.getCount(); i++)
        {
            boolean result = getEnemy(i).checkProjectileCollision(projectile);

            //if the projectile hit don't check for collision anywhere else
            if (result)
            {
                //flag the projectile to be removed
                projectile.markDiscard();
                break;
            }
        }
    }
    
    /**
     * Spawn enemies for the given map
     * @param map The current map where we want to place the enemies
     * @param random Object used to make random decisions
     */
    public void spawn(final Map map, final Random random)
    {
        //get the total number of different enemy types
        final int size = Enemy.Type.values().length;
        
        //continue to spawn enemies until we have reached the limit
        while (getCount() < SPAWN_LIMIT)
        {
            //pick random enemy type
            final Enemy.Type type = Enemy.Type.values()[random.nextInt(size)];
        
            //can't continue if no locations are left
            if (map.getSpawnLocations().isEmpty())
                break;
            
            //pick random index
            final int index = random.nextInt(map.getSpawnLocations().size());
            
            //get the x,y coordinates of the enemy on west side
            int x = Map.getBlockX(map.getSpawnLocations().get(index).getCol());
            int y = Map.getBlockY(map.getSpawnLocations().get(index).getRow());
            
            //add enemy at location
            add(type, x, y);
            
            //get the x,y coordinates of the enemy on east side
            x = Map.getBlockX(Map.COLUMNS - map.getSpawnLocations().get(index).getCol());
            y = Map.getBlockY(map.getSpawnLocations().get(index).getRow());
            
            //add enemy at location
            add(type, x, y);
            
            //remove location from the list
            map.getSpawnLocations().remove(index);
        }
    }
    
    /**
     * Add enemy to list
     * @param type The type of enemy we want to add
     * @param x x-coordinate of our destination
     * @param y y-coordinate of our destination
     */
    private void add(final Enemy.Type type, final int x, final int y)
    {
        try
        {
            //enemy instance
            final Enemy enemy;

            //create new enemy of specified type
            switch(type)
            {
                case BubbleBuster:
                    enemy = new BubbleBuster();
                    break;

                case Incendo:
                    enemy = new Incendo();
                    break;

                case Beluga:
                    enemy = new Beluga();
                    break;

                case Stoner:
                    enemy = new Stoner();
                    break;

                case Coiley:
                    enemy = new Coiley();
                    break;

                case Hullaballoon:
                    enemy = new Hullaballoon();
                    break;

                case SuperSocket:
                    enemy = new SuperSocket();
                    break;

                case WillyWhistle:
                    enemy = new WillyWhistle();
                    break;

                default:
                    throw new Exception("Type not setup here: " + type.toString());
            }

            //set the destination
            enemy.setDestinationX(x);
            enemy.setDestinationY(y);

            //set the location to spawn at the top
            enemy.setX(x);
            enemy.setY(Map.getBlockY(-1));

            //store reference for enemy
            enemy.setImage(image);

            //add to list
            enemies.add(enemy);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public int getCount()
    {
        return enemies.size();
    }
    
    public void remove(final Enemy enemy)
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            if (getEnemy(i).getId() == enemy.getId())
            {
                enemies.remove(i);
                break;
            }
        }
    }
    
    @Override
    public void dispose()
    {
        if (enemies != null)
        {
            for (int i = 0; i < enemies.size(); i++)
            {
                enemies.get(i).dispose();
                enemies.set(i, null);
            }
            
            enemies.clear();
            enemies = null;
        }
    }
    
    @Override
    public void update(final Engine engine)
    {
        if (enemies != null)
        {
            for (int i = 0; i < enemies.size(); i++)
            {
                Enemy enemy = getEnemy(i);
                
                enemy.update(engine);
                
                if (enemy.hasDeathFinished())
                {
                    //remove enemy
                    remove(enemy);
                    
                    //add fruit here
                }
            }
        }
        
        //check if the enemies hit the hero(es)
        checkCharacterCollision(engine.getManager().getHero());
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        if (enemies != null)
        {
            for (int i = 0; i < enemies.size(); i++)
            {
                getEnemy(i).render(graphics);
            }
        }
    }
}