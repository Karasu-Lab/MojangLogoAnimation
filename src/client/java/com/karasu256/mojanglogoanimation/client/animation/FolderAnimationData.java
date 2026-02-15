package com.karasu256.mojanglogoanimation.client.animation;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record FolderAnimationData(Identifier id, int frameCount,
        @Nullable SoundEvent soundEvent) implements ISplashScreenAnimationData {
    private static final int DEFAULT_FRAME_INTERVAL_MS = 70;
    private static final int DEFAULT_HOLD_DURATION_MS = 1000;

    public FolderAnimationData(Identifier id, int frameCount) {
        this(id, frameCount, resolveSoundByConvention(id));
    }

    private static @Nullable SoundEvent resolveSoundByConvention(Identifier animationId) {
        Identifier soundId = Identifier.of(animationId.getNamespace(), "gui.title." + animationId.getPath());
        return SoundEvent.of(soundId);
    }

    @Override
    public int getFrameIntervalMs() {
        return DEFAULT_FRAME_INTERVAL_MS;
    }

    @Override
    public int getHoldDurationMs() {
        return DEFAULT_HOLD_DURATION_MS;
    }

    @Override
    public List<ResourceEntry> getResourceEntries() {
        return new ArrayList<>();
    }

    @Override
    public Identifier getFrameTexture(int frameIndex) {
        Identifier texture = Identifier.of(id.getNamespace(),
                "textures/gui/title/" + id.getPath() + "/mojang" + frameIndex + ".png");
        return texture;
    }

    @Override
    public Identifier getStaticTexture() {
        return getFrameTexture(frameCount - 1);
    }
}
