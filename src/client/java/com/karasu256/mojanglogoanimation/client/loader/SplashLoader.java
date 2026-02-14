package com.karasu256.mojanglogoanimation.client.loader;

import com.karasu256.mojanglogoanimation.client.animation.FolderAnimationData;
import com.karasu256.mojanglogoanimation.client.animation.ISplashScreenAnimationData;

import java.util.LinkedHashMap;
import java.util.Map;

public class SplashLoader {
    private final Map<String, ISplashScreenAnimationData> animations = new LinkedHashMap<>();

    public void registerDefaults() {
        register(new FolderAnimationData("mojang", 39));
        register(new FolderAnimationData("mojang_april_fool", 39));
    }

    public void register(ISplashScreenAnimationData data) {
        animations.put(data.id(), data);
    }

    public ISplashScreenAnimationData getAnimation(String id) {
        ISplashScreenAnimationData data = animations.get(id);
        if (data == null && !animations.isEmpty()) {
            return animations.values().iterator().next();
        }
        return data;
    }

}
