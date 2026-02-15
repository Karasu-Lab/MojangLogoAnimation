package com.karasu256.mojanglogoanimation.client.animation;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record FolderAnimationData(Identifier id, int frameCount,
        @Nullable SoundEvent soundEvent) implements ISplashScreenAnimationData {
    private static final int DEFAULT_FRAME_INTERVAL_MS = 70;
    private static final int DEFAULT_HOLD_DURATION_MS = 1000;

    public FolderAnimationData(Identifier id, int frameCount) {
        this(id, frameCount, resolveSoundByConvention(id));
    }

    private static @NotNull SoundEvent resolveSoundByConvention(Identifier animationId) {
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
    public Identifier getFrameTexture(int frameIndex) {
        return Identifier.of(id.getNamespace(),
                "textures/gui/title/" + id.getPath() + "/mojang" + frameIndex + ".png");
    }

    @Override
    public Identifier getStaticTexture() {
        return getFrameTexture(frameCount - 1);
    }
}
