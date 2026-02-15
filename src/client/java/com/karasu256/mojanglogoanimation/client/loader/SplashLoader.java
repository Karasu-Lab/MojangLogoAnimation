package com.karasu256.mojanglogoanimation.client.loader;

import com.karasu256.mojanglogoanimation.client.animation.ISplashScreenAnimationData;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class SplashLoader {
    private final Map<Identifier, ISplashScreenAnimationData> animations = new HashMap<>();

    public void register(ISplashScreenAnimationData data) {
        animations.put(data.id(), data);
    }

    public ISplashScreenAnimationData getAnimation(Identifier id) {
        return animations.get(id);
    }
    
    public void clear() {
        animations.clear();
    }
}
