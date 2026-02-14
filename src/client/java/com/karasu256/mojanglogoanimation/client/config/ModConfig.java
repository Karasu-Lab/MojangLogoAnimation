package com.karasu256.mojanglogoanimation.client.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "mojanglogoanimation")
public class ModConfig implements ConfigData {
    public String animationId = "mojang";
    public boolean animationEnabled = true;
    public boolean soundEnabled = true;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int soundVolume = 100;
}
