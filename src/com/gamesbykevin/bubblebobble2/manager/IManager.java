package com.gamesbykevin.bubblebobble2.manager;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.bubblebobble2.engine.Engine;
import com.gamesbykevin.bubblebobble2.shared.IProgress;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Basic methods required for game elements
 * @author GOD
 */
public interface IManager extends Disposable
{
    /**
     * Update our game element accordingly
     * @param engine The Engine containing resources etc... if needed
     * @throws Exception 
     */
    public void update(final Engine engine) throws Exception;
    
    /**
     * Draw our game element(s) accordingly
     */
    public void render(final Graphics graphics);
    
    /**
     * Provide a way to reset the game elements
     * @param engine The Engine containing resources etc... if needed
     * @throws Exception 
     */
    public void reset(final Engine engine) throws Exception;
    
    /**
     * Set the window
     * @param window The area where the game play will take place
     */
    public void setWindow(final Rectangle window);
    
    /**
     * Get the window
     * @return Area where game play will take place
     */
    public Rectangle getWindow();
}