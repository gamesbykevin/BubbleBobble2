package com.gamesbykevin.bubblebobble2.hero;

import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.bubblebobble2.character.Character;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.maps.Map;
import com.gamesbykevin.bubblebobble2.projectile.*;
import java.awt.AlphaComposite;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

public final class Hero extends Character
{
    public enum Type
    {
        Hero1, Hero2
    }
    
    //the type of hero
    private Type type;
    
    //the default amount of starting lives
    private static final int DEFAULT_LIVES = 5;
    
    //the amount of lives the hero has
    private int lives = DEFAULT_LIVES;
    
    //where to render lives remaining
    private Point renderLocation;
    
    public enum Animations
    {
        Idle, Walk, Jump, Fall, Attack, Die, Start
    }
    
    //the max number of projectiles
    private static final int PROJECTILE_LIMIT = 5;
    
    public static final double SPEED_WALK = 1;
    
    private static final long DELAY_NONE = 0;
    private static final long DELAY_WALK = Timers.toNanoSeconds(175L);
    private static final long DELAY_JUMP = Timers.toNanoSeconds(250L);
    private static final long DELAY_FALL = Timers.toNanoSeconds(250L);
    private static final long DELAY_ATTACK = Timers.toNanoSeconds(333L);
    private static final long DELAY_DIE = Timers.toNanoSeconds(175L);
    private static final long DELAY_START = Timers.toNanoSeconds(500L);
    
    //our transparent image and original
    private BufferedImage transparentImage;
    private Image original;
    
    public Hero(final Type type)
    {
        super.setSpeedRun(SPEED_WALK);
        super.setSpeedWalk(SPEED_WALK);
        
        //store type of hero
        this.type = type;
        
        //set the projectile limit
        setProjectileLimit(PROJECTILE_LIMIT);
        
        //setup animations
        setupAnimations();
    }
    
    @Override
    public void setImage(final Image image)
    {
        super.setImage(image);
        
        //store original image
        this.original = image;
        
        //create new image
        this.transparentImage = new BufferedImage(getImage().getWidth(null), getImage().getHeight(null), BufferedImage.TYPE_INT_ARGB);

        //get the graphics object to write
        Graphics2D g2d = this.transparentImage.createGraphics();

        //set transparency for this image
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

        //write original spritesheet to this image
        g2d.drawImage(getImage(), 0, 0, null);
    }
    
    public Type getType()
    {
        return this.type;
    }
    
    public void setLives(final int lives)
    {
        this.lives = lives;
    }
    
    public int getLives()
    {
        return this.lives;
    }
    
    public boolean hasLives()
    {
        return (getLives() > 0);
    }
    
    @Override
    public void update(final Engine engine)
    {
        if (!isDead())
        {
            //update basics 
            super.update(engine);
            
            if (isInvincible())
            {
                //if time has passed, no longer invincible
                if (getTimer().hasTimePassed())
                {
                    setInvincible(false);
                }
                
                //update invincible timer
                getTimer().update(engine.getMain().getTime());
            }
        }
        else
        {
            //update location and animation
            update(engine.getMain().getTime());

            //set the correct animation
            correctAnimation();
            
            //stop moving if dead
            resetVelocity();
            
            //make sure death animation is finished
            if (isAnimationFinished())
            {
                //deduct a life
                setLives(getLives() - 1);
                
                //reset death animation
                getSpriteSheet().reset();
                
                if (hasLives())
                {
                    //start invincibility
                    setInvincible(true);
                    
                    //mark walking
                    setWalk(true);
                    
                    //reset location
                    setLocation(getDestinationX(), getDestinationY());
                }
                else
                {
                    //flag game over since no more lives
                    engine.getManager().setResult(false);
                }
            }
        }
    }
    
    @Override
    protected void setupAnimations()
    {
        switch (type)
        {
            case Hero1:
                super.addAnimation(Animations.Idle,   1, 0,  0,  16, 16, DELAY_NONE, false);
                super.addAnimation(Animations.Walk,   4, 0,  0,  16, 16, DELAY_WALK, true);
                super.addAnimation(Animations.Jump,   2, 0,  16, 16, 16, DELAY_JUMP, true);
                super.addAnimation(Animations.Fall,   2, 32, 16, 16, 16, DELAY_FALL, true);
                super.addAnimation(Animations.Attack, 1, 0,  32, 16, 16, DELAY_ATTACK, false);
                super.addAnimation(Animations.Die,    6, 0,  48, 16, 16, DELAY_DIE, false);
                super.addAnimation(Animations.Start,  2, 0,  80, 28, 32, DELAY_START, true);
                break;
                
            case Hero2:
                super.addAnimation(Animations.Idle,   1, 0,  112,  16, 16, DELAY_NONE, false);
                super.addAnimation(Animations.Walk,   4, 0,  112,  16, 16, DELAY_WALK, true);
                super.addAnimation(Animations.Jump,   2, 0,  128, 16, 16, DELAY_JUMP, true);
                super.addAnimation(Animations.Fall,   2, 32, 128, 16, 16, DELAY_FALL, true);
                super.addAnimation(Animations.Attack, 1, 0,  144, 16, 16, DELAY_ATTACK, false);
                super.addAnimation(Animations.Die,    6, 0,  160, 16, 16, DELAY_DIE, false);
                super.addAnimation(Animations.Start,  2, 0,  192, 28, 32, DELAY_START, true);
                super.setHorizontalFlip(true);
                break;
        }
        
        //stop movement
        super.resetVelocity();
        
        //set dimensions
        super.setDimensions();
        
        //flag we are starting
        super.setStart(true);
    }
    
    @Override
    protected boolean checkProjectileCollision(final Projectile projectile)
    {
        return false;
    }
    
    @Override
    public void addProjectile()
    {
        final Projectile projectile = new Bubble(!hasHorizontalFlip());
        
        //set the location
        projectile.setLocation(getX(), getY());
        
        //set the image of the projectile
        projectile.setImage(getImage());
        
        //add projectile
        super.addProjectile(projectile);
    }
    
    @Override
    protected void correctAnimation()
    {
        if (!isAttacking())
        {
            if (isStarting())
                setAnimation(Animations.Start);
            
            if (isIdle())
                setAnimation(Animations.Idle);

            if (isWalking())
                setAnimation(Animations.Walk);

            if (isJumping())
                setAnimation(Animations.Jump);

            if (isFalling())
                setAnimation(Animations.Fall);
        
            if (isDead())
                setAnimation(Animations.Die);
        }
        else
        {
            //if this is not the current animation
            if (!isAnimation(Animations.Attack))
                setAnimation(Animations.Attack, true);
            
            //if animation finished
            if (isAnimationFinished())
            {
                //set to idle
                setAnimation(Animations.Idle);
                
                //no longer attacking
                setIdle(true);
            }
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //set image depending on invincible
        super.setImage(isInvincible() ? transparentImage : original);
        
        //draw character
        super.render(graphics);
        
        //draw amount of lives
        if (renderLocation == null)
            renderLocation = new Point(Map.BLOCK_SIZE, Map.BLOCK_SIZE);
        
        graphics.setColor(Color.BLACK);
        graphics.fillRect(renderLocation.x, renderLocation.y - Map.BLOCK_SIZE, Map.BLOCK_SIZE * 2, Map.BLOCK_SIZE);
        graphics.setColor(Color.WHITE);
        graphics.drawString((getLives() < 0) ? "" + 0 : getLives() + "", renderLocation.x + 1, renderLocation.y);
    }
}