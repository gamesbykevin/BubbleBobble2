package com.gamesbykevin.bubblebobble2.input;

import com.gamesbykevin.framework.input.Keyboard;

import com.gamesbykevin.bubblebobble2.character.Character;
import java.awt.event.KeyEvent;

/**
 * This class will handle the keyboard input for the object
 * @author GOD
 */
public final class Input 
{
    /**
     * The keys we will check for input
     */
    public static final int KEY_LEFT  = KeyEvent.VK_LEFT;
    public static final int KEY_RIGHT = KeyEvent.VK_RIGHT;
    public static final int KEY_DOWN  = KeyEvent.VK_DOWN;
    public static final int KEY_JUMP  = KeyEvent.VK_A;
    public static final int KEY_FIRE  = KeyEvent.VK_S;
    
    /**
     * Manage the character based on keyboard input
     * @param character The character we want to manage
     * @param keyboard Object containing keyboard input
     */
    public static void update(final Character character, final Keyboard keyboard)
    {
        //if the character is starting don't check input yet
        if (character.isStarting())
            return;
        
        //can only press left or right
        if (keyboard.hasKeyPressed(KEY_LEFT))
        {
            if (character.canWalk())
            {
                character.setVelocityX(-character.getSpeedWalk());
                character.setHorizontalFlip(true);

                if (!character.isJumping() && !character.isFalling())
                    character.setWalk(true);
            }
        }
        else if (keyboard.hasKeyPressed(KEY_RIGHT))
        {
            if (character.canWalk())
            {
                character.setVelocityX(character.getSpeedWalk());
                character.setHorizontalFlip(false);

                if (!character.isJumping() && !character.isFalling())
                    character.setWalk(true);
            }
        }
        
        //determine which way to face the character
        if (keyboard.hasKeyReleased(KEY_LEFT))
        {
            keyboard.removeKeyReleased(KEY_LEFT);
            
            character.resetVelocityX();
            character.setWalk(false);
            
            if (!character.isAttacking())
            {
                character.setHorizontalFlip(true);
                
                if (!character.isJumping() && !character.isFalling())
                    character.setIdle(true);
            }
        }
        else if (keyboard.hasKeyReleased(KEY_RIGHT))
        {
            keyboard.removeKeyReleased(KEY_RIGHT);
            
            character.resetVelocityX();
            character.setWalk(false);
            
            if (!character.isAttacking())
            {
                character.setHorizontalFlip(false);
                
                if (!character.isJumping() && !character.isFalling())
                    character.setIdle(true);
            }
        }
        
        if (keyboard.hasKeyPressed(KEY_JUMP))
        {
            if (character.canJump())
            {
                character.setJump(true);
                character.setVelocityY(-Character.MAX_SPEED_JUMP);
            }
        }
        
        if (keyboard.hasKeyReleased(KEY_FIRE))
        {
            if (character.canAttack())
            {
                character.setAttack(true);
                character.addProjectile();
            }
            
            //stop firing
            keyboard.removeKeyReleased(KEY_FIRE);
        }
    }
}