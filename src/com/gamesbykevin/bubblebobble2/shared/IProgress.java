package com.gamesbykevin.bubblebobble2.shared;

public interface IProgress 
{
    /**
     * Determine if everything has been setup
     * @param complete Is setup complete
     */
    public void setComplete(final boolean complete);
    
    /**
     * Is setup complete
     * @return true if the game setup has completed, false otherwise
     */
    public boolean isComplete();
}