package com.gamesbykevin.bubblebobble2.manager;

import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.bubblebobble2.bonus.Bonuses;
import com.gamesbykevin.bubblebobble2.enemies.Enemies;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.hero.Hero;
import com.gamesbykevin.bubblebobble2.input.Input;
import com.gamesbykevin.bubblebobble2.maps.Maps;
import com.gamesbykevin.bubblebobble2.menu.CustomMenu;
import com.gamesbykevin.bubblebobble2.menu.CustomMenu.*;
import com.gamesbykevin.bubblebobble2.resources.*;
import com.gamesbykevin.bubblebobble2.shared.Shared;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements IManager
{
    //where gameplay occurs
    private Rectangle window;
    
    //the levels in our game
    private Maps maps;
    
    //the hero
    private Hero hero;
    
    private Enemies enemies;
    
    private Bonuses bonuses;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine Engine for our game that contains all objects needed
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //determine if sound is enabled
        final boolean enabled = (Toggle.values()[engine.getMenu().getOptionSelectionIndex(LayerKey.OptionsInGame, OptionKey.Sound)] == Toggle.Off);

        //set the audio depending on menu setting
        engine.getResources().setAudioEnabled(enabled);
        
        //set the game window where game play will occur
        setWindow(engine.getMain().getScreen());

        //create new maps
        maps = new Maps(engine.getResources().getGameImage(GameImages.Keys.Maps), getWindow());
        
        //create new hero
        hero = new Hero(Hero.Type.Hero1);
        hero.setImage(engine.getResources().getGameImage(GameImages.Keys.Heroes));
        
        //create container object for the enemies
        enemies = new Enemies(engine.getResources().getGameImage(GameImages.Keys.Enemies));
        
        //create container object for the bonuses
        bonuses = new Bonuses(engine.getResources().getGameImage(GameImages.Keys.Bonus));
        
        //check the number of lives set
        //switch (engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.Lives))
    }
    
    public Bonuses getBonuses()
    {
        return this.bonuses;
    }
    
    public Enemies getEnemies()
    {
        return this.enemies;
    }
    
    
    @Override
    public void reset(final Engine engine) throws Exception
    {
        
    }
    
    public Hero getHero()
    {
        return this.hero;
    }
    
    public Maps getMaps()
    {
        return this.maps;
    }
    
    @Override
    public Rectangle getWindow()
    {
        return this.window;
    }
    
    @Override
    public void setWindow(final Rectangle window)
    {
        this.window = new Rectangle(window);
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        if (window != null)
            window = null;
        
        if (maps != null)
        {
            maps.dispose();
            maps = null;
        }
        
        if (bonuses != null)
        {
            bonuses.dispose();
            bonuses = null;
        }
        
        if (enemies != null)
        {
            enemies.dispose();
            enemies = null;
        }
        
        try
        {
            //recycle objects
            super.finalize();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Update all elements
     * @param engine Our game engine
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        if (!maps.isComplete())
        {
            //this is called to generate the maps
            maps.update(engine);
            
            //if the maps are now complete set the hero start location
            if (maps.isComplete())
            {
                //spawn the enemies
                enemies.spawn(maps.getMap(), engine.getRandom());
                
                switch(hero.getType())
                {
                    case Hero1:
                        hero.setDestination(maps.getMap().getStartWest());
                        break;
                        
                    default:
                        hero.setDestination(maps.getMap().getStartEast());
                        break;
                }
            }
        }
        else
        {
            //update character
            Input.update(hero, engine.getKeyboard());
            
            //update hero
            hero.update(engine);
            
            //update enemies
            enemies.update(engine);
        }
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        //draw the map (progress will be drawn if maps aren't created yet)
        maps.render(graphics);
        
        if (maps.isComplete())
        {
            bonuses.render(graphics);
            enemies.render(graphics);
            hero.render(graphics);
        }
    }
}