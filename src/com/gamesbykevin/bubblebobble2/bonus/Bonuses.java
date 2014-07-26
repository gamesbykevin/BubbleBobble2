package com.gamesbykevin.bubblebobble2.bonus;

import com.gamesbykevin.framework.resources.Disposable;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public final class Bonuses implements Disposable
{
    //list of bonuses in play
    private List<Bonus> bonuses;
    
    //bonus spritesheet
    private Image image;
    
    public Bonuses(final Image image)
    {
        //create new container for the bonuses
        this.bonuses = new ArrayList<>();
        
        //store image
        this.image = image;
    }
    
    public void add(final Bonus bonus)
    {
        //store reference for bonus
        bonus.setImage(image);
        
        //add to list
        bonuses.add(bonus);
    }
    
    public void clear()
    {
        //remove all
        bonuses.clear();
    }
    
    public void remove(final Bonus bonus)
    {
        for (int i = 0; i < bonuses.size(); i++)
        {
            if (bonuses.get(i).getId() == bonus.getId())
            {
                bonuses.remove(i);
                break;
            }
        }
    }
    
    @Override
    public void dispose()
    {
        if (bonuses != null)
        {
            for (int i = 0; i < bonuses.size(); i++)
            {
                bonuses.get(i).dispose();
                bonuses.set(i, null);
            }
            
            bonuses.clear();
            bonuses = null;
        }
    }
    
    public void render(final Graphics graphics)
    {
        if (bonuses != null)
        {
            for (int i = 0; i < bonuses.size(); i++)
            {
                bonuses.get(i).render(graphics);
            }
        }
    }
}