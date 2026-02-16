package com.karasu256.mojanglogoanimation.client.animation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AnimationPlayer {
    private final ISplashScreenAnimationData data;
    private final AtomicInteger currentFrame = new AtomicInteger(0);
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private final AtomicBoolean hasFinished = new AtomicBoolean(false);
    private final boolean soundEnabled;
    private final float soundVolume;
    private boolean firstLoad = true;

    public AnimationPlayer(ISplashScreenAnimationData data, boolean soundEnabled, float soundVolume) {
        this.data = data;
        this.soundEnabled = soundEnabled;
        this.soundVolume = soundVolume;
    }

    public void startAnimation() {
        if (!isPlaying.compareAndSet(false, true)) return;

        if (soundEnabled) {
            playSoundOnMainThread(data.soundEvent());
        }

        Thread animThread = new Thread(() -> {
            currentFrame.set(0);

            for (int i = 0; i < data.frameCount(); i++) {
                currentFrame.set(i);
                try {
                    Thread.sleep(data.getFrameIntervalMs());
                } catch (InterruptedException ignored) {
                    return;
                }
            }

            try {
                Thread.sleep(data.getHoldDurationMs());
            } catch (InterruptedException ignored) {
                return;
            }

            hasFinished.set(true);
        });

        animThread.setName("mojanglogoanimation-anim");
        animThread.setDaemon(true);
        animThread.start();
    }

    private void playSoundOnMainThread(@Nullable SoundEvent event) {
        if (event == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() ->
                client.getSoundManager().play(
                        PositionedSoundInstance.ui(event, 1.0f, soundVolume)
                )
        );
    }

    public boolean isFinished() {
        return hasFinished.get();
    }

    public Identifier getCurrentTexture() {
        if (!firstLoad) {
            return data.getStaticTexture();
        }
        return data.getFrameTexture(currentFrame.get());
    }

    public void markLoadComplete() {
        firstLoad = false;
    }

    public boolean isFirstLoad() {
        return firstLoad;
    }

    public boolean isAnimating() {
        return isPlaying.get() && !hasFinished.get();
    }
}
