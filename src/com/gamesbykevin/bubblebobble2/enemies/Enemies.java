package com.gamesbykevin.bubblebobble2.enemies;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.shared.IElement;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public final class Enemies implements Disposable, IElement
{
    //list of enemies in play
    private List<Enemy> enemies;
    
    //enemy spritesheet
    private Image image;
    
    public Enemies(final Image image)
    {
        //create new container for the enemies
        this.enemies = new ArrayList<>();
        
        //store image
        this.image = image;
    }
    
    public void add(final Enemy enemy)
    {
        //store reference for enemy
        enemy.setImage(image);
        
        //add to list
        enemies.add(enemy);
    }
    
    public void remove(final Enemy enemy)
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            if (enemies.get(i).getId() == enemy.getId())
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
                enemies.get(i).update(engine);
            }
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        if (enemies != null)
        {
            for (int i = 0; i < enemies.size(); i++)
            {
                enemies.get(i).render(graphics);
            }
        }
    }
}