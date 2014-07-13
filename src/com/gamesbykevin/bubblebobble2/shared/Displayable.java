package com.gamesbykevin.bubblebobble2.shared;

public interface Displayable 
{
    /**
     * Is this object currently being displayed
     * @return true if this object is to be displayed, false otherwise
     */
    public boolean isDisplayed();
    
    /**
     * Set the object to being displayed or not
     * @param display true if we are to display, false otherwise
     */
    public void setDisplayed(final boolean display);
}