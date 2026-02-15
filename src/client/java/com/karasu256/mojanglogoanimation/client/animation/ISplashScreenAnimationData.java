package com.karasu256.mojanglogoanimation.client.animation;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ISplashScreenAnimationData {
    Identifier id();

    int frameCount();

    int getFrameIntervalMs();

    int getHoldDurationMs();

    @Nullable SoundEvent soundEvent();

    List<ResourceEntry> getResourceEntries();

    Identifier getFrameTexture(int frameIndex);

    Identifier getStaticTexture();
}
