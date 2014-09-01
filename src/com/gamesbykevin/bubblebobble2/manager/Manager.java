package com.gamesbykevin.bubblebobble2.manager;

import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.bubblebobble2.bonus.Bonuses;
import com.gamesbykevin.bubblebobble2.enemies.Enemies;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.hero.Hero;
import com.gamesbykevin.bubblebobble2.input.Input;
import com.gamesbykevin.bubblebobble2.maps.Map;
import com.gamesbykevin.bubblebobble2.maps.Maps;
import com.gamesbykevin.bubblebobble2.menu.CustomMenu;
import com.gamesbykevin.bubblebobble2.menu.CustomMenu.*;
import com.gamesbykevin.bubblebobble2.resources.*;
import com.gamesbykevin.bubblebobble2.shared.Shared;

import java.awt.Graphics;
import java.awt.Image;
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
    
    //object containing enemies
    private Enemies enemies;
    
    //object containing bonuses
    private Bonuses bonuses;
    
    //has the game ended
    private boolean gameover = false;
    
    //did they win
    private boolean result = false;
    
    //image to diplay for win/lose
    private Image imageWin, imageLose;
    
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
        
        //store image references
        this.imageWin  = engine.getResources().getGameImage(GameImages.Keys.Victory);
        this.imageLose = engine.getResources().getGameImage(GameImages.Keys.GameOver);
        
        //reset game
        reset(engine);
    }
    
    /**
     * Determine if the player won or lost
     * @param result true if won, false otherwise
     */
    public void setResult(final boolean result)
    {
        //mark game over
        this.gameover = true;
        
        //store the result
        this.result = result;
    }
    
    private boolean hasGameover()
    {
        return this.gameover;
    }
    
    private boolean hasResult()
    {
        return this.result;
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
        //determine how many lives to set
        switch (engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.Lives))
        {
            case 0:
                hero.setLives(5);
                break;
                
            case 1:
                hero.setLives(10);
                break;
                
            case 2:
                hero.setLives(25);
                break;
                
            case 3:
                hero.setLives(99);
                break;
                
            case 4:
            default:
                hero.setLives(1);
                break;
        }
        
        //determine which level to start at
        switch (engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.Level))
        {
            case 0:
                maps.setStartingMap(0);
                break;
                
            case 1:
                maps.setStartingMap(19);
                break;
                
            case 2:
                maps.setStartingMap(39);
                break;
                
            case 3:
                maps.setStartingMap(59);
                break;
                
            case 4:
                maps.setStartingMap(79);
                break;
                
            case 5:
                maps.setStartingMap(99);
                break;
                
            case 6:
                maps.setStartingMap(119);
                break;
                
            case 7:
                maps.setStartingMap(139);
                break;
                
            case 8:
                maps.setStartingMap(159);
                break;
                
            case 9:
                maps.setStartingMap(179);
                break;
                
            case 10:
            default:
                maps.setStartingMap(199);
                break;
        }
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
        //don't continue if game is over
        if (hasGameover())
            return;
        
        if (!getMaps().isComplete())
        {
            //this is called to generate the maps
            getMaps().update(engine);
            
            //if the maps are now complete set the hero start location
            if (getMaps().isComplete())
            {
                //spawn the enemies
                getEnemies().spawn(getMaps().getMap(), engine.getRandom());
                
                switch(getHero().getType())
                {
                    case Hero1:
                        getHero().setDestination(getMaps().getMap().getStartWest());
                        break;
                        
                    default:
                        getHero().setDestination(getMaps().getMap().getStartEast());
                        break;
                }
            }
        }
        else
        {
            //if there are no enemies or bonuses the level is complete
            if (!getEnemies().hasEnemies() && !getBonuses().hasBonuses() && !getMaps().hasTransition())
            {
                //if there are no enemies and this is the last map
                if (getMaps().isLastMap())
                {
                    //set victory
                    setResult(true);
                }
                
                //start transition
                getMaps().setTransition(true);
                
                //remove all projectiles
                getHero().removeProjectiles();
                
                //set starting
                getHero().setStart(true);
                
                //update hero
                getHero().update(engine);
            }
            else
            {
                if (getMaps().hasTransition())
                {
                    //update the maps
                    getMaps().update(engine);
                    
                    //if transition has finished
                    if (!getMaps().hasTransition())
                    {
                        //update hero
                        getHero().update(engine);

                        //set starting
                        getHero().setStart(true);
                        
                        //set the new destination
                        getHero().setDestination(getMaps().getMap().getStartWest());
                        
                        //spawn the enemies
                        getEnemies().spawn(getMaps().getMap(), engine.getRandom());
                    }
                }
                else
                {
                    //update character
                    Input.update(getHero(), engine.getKeyboard());

                    //update hero
                    getHero().update(engine);

                    //update enemies
                    getEnemies().update(engine);

                    //update bonuses
                    getBonuses().update(engine);

                    //update the maps
                    getMaps().update(engine);
                }
            }
        }
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        //draw game elements if not over
        if (!hasGameover())
        {
            //draw the map (progress will be drawn if maps aren't created yet)
            getMaps().render(graphics);

            if (getMaps().isComplete())
            {
                getBonuses().render(graphics);
                getEnemies().render(graphics);
                getHero().render(graphics);
            }
        }
        else
        {
            if (hasResult())
            {
                graphics.drawImage(imageWin,  0, 0, Map.WIDTH, Map.HEIGHT, null);
            }
            else
            {
                graphics.drawImage(imageLose, 0, 0, Map.WIDTH, Map.HEIGHT, null);
            }
        }
    }
}