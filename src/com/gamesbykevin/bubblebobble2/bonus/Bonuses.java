package com.gamesbykevin.bubblebobble2.bonus;

import com.gamesbykevin.bubblebobble2.character.Character;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.entity.Entity;
import com.gamesbykevin.bubblebobble2.hero.Hero;
import com.gamesbykevin.bubblebobble2.resources.GameAudio;
import com.gamesbykevin.bubblebobble2.shared.IElement;

import com.gamesbykevin.framework.resources.Disposable;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public final class Bonuses implements Disposable, IElement
{
    //list of bonuses in play
    private List<Bonus> bonuses;
    
    //bonus spritesheet
    private Image image;
    
    //how many bonuses have been collected
    private int collected = 0;
    
    //each total amount collected will be an extra life
    private static final int BONUSES_PER_LIFE = 10;
    
    public Bonuses(final Image image)
    {
        //create new container for the bonuses
        this.bonuses = new ArrayList<>();
        
        //store image
        this.image = image;
    }
    
    public void add(final Entity entity)
    {
        //pick random fruit
        final int index = (int)(Math.random() * Bonus.Type.values().length);
        
        //create new bonus
        Bonus bonus = new Bonus(Bonus.Type.values()[index]);
        
        //store the location
        bonus.setLocation(entity);
        
        //move bonus up
        bonus.setY(bonus.getY() - (entity.getHeight() / 2));
        
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
    
    private void remove(final Bonus bonus)
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
    
    public boolean hasBonuses()
    {
        return (!bonuses.isEmpty());
    }
    
    private Bonus getBonus(final int index)
    {
        return bonuses.get(index);
    }
    
    @Override
    public void update(final Engine engine)
    {
        Hero hero = engine.getManager().getHero();
        
        for (int i = 0; i < bonuses.size(); i++)
        {
            //get the bonus
            Bonus bonus = getBonus(i);
            
            //deduct time from timer
            bonus.getTimer().update(engine.getMain().getTime());
            
            //if the hero is close enough to the bonus
            if (hero.hasVelocity() && hero.getDistance(bonus) <= bonus.getWidth() * Character.COLLISION_RATIO)
            {
                //add to total
                this.collected++;
                
                //if reached the next amount, we will add a life
                if (collected % BONUSES_PER_LIFE == 0)
                {
                    //add life
                    hero.setLives(hero.getLives() + 1);
                    
                    //play sound effect
                    engine.getResources().playGameAudio(GameAudio.Keys.SoundExtraLife);
                }
                else
                {
                    //play sound effect
                    engine.getResources().playGameAudio(GameAudio.Keys.SoundFruit);
                }
                
                //remove
                remove(bonus);

                //deduct index
                i--;
            }
            else
            {
                //if time has passed remove the bonus
                if (bonus.getTimer().hasTimePassed())
                {
                    //remove
                    remove(bonus);

                    //deduct index
                    i--;
                }
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
    
    @Override
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