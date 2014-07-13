package com.gamesbykevin.bubblebobble2.shared;

import com.gamesbykevin.bubblebobble2.resources.GameAudio;

public interface IAudio 
{
    public void setAudioKey(final GameAudio.Keys audioKey);
    
    public GameAudio.Keys getAudioKey();
}