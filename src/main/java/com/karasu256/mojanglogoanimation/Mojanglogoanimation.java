package com.karasu256.mojanglogoanimation;

import com.karasu256.mojanglogoanimation.sounds.ModSounds;
import net.fabricmc.api.ModInitializer;

public class Mojanglogoanimation implements ModInitializer {

    @Override
    public void onInitialize() {
        ModSounds.register();
    }
}
