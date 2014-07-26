package com.gamesbykevin.bubblebobble2.bonus;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.bubblebobble2.entity.Entity;

public final class Bonus extends Entity implements Disposable
{
    public enum Type
    {
        Vegetable1, Vegetable2, Vegetable3, Vegetable4, Vegetable5
    }
    
    //store the type
    private final Type type;
    
    //dimensions of bonus
    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;
    
    protected Bonus(final Type type)
    {
        this.type = type;
        
        setupAnimations();
    }
    
    @Override
    protected void setupAnimations()
    {
        switch (type)
        {
            case Vegetable1:
                super.addAnimation(type, 1, 0 * WIDTH, 0, WIDTH, HEIGHT, 0, false);
                break;
                
            case Vegetable2:
                super.addAnimation(type, 1, 1 * WIDTH, 0, WIDTH, HEIGHT, 0, false);
                break;
                
            case Vegetable3:
                super.addAnimation(type, 1, 2 * WIDTH, 0, WIDTH, HEIGHT, 0, false);
                break;
                
            case Vegetable4:
                super.addAnimation(type, 1, 3 * WIDTH, 0, WIDTH, HEIGHT, 0, false);
                break;
                
            case Vegetable5:
                super.addAnimation(type, 1, 4 * WIDTH, 0, WIDTH, HEIGHT, 0, false);
                break;
        }
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
}