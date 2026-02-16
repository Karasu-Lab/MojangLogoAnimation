package com.karasu256.mojanglogoanimation.mixin.client;

import com.karasu256.mojanglogoanimation.Constants;
import com.karasu256.mojanglogoanimation.client.MojanglogoanimationClient;
import com.karasu256.mojanglogoanimation.client.animation.AnimationPlayer;
import com.karasu256.mojanglogoanimation.client.animation.ISplashScreenAnimationData;
import com.karasu256.mojanglogoanimation.client.config.ModConfig;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {
    @Shadow
    @Final
    private ResourceReload reload;
    @Shadow
    private float progress;

    @Shadow
    @Final
    private boolean reloading;

    @Unique
    private boolean animationStarting = false;

    @Unique
    private AnimationPlayer animationPlayer;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceReload;isComplete()Z"))
    private boolean isReloadComplete(ResourceReload instance) {
        if (this.reload.isComplete()) {
            ensureAnimationPlayer();
            return animationPlayer == null || !animationPlayer.isFirstLoad() || animationPlayer.isFinished();
        }
        return false;
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;LOGO:Lnet/minecraft/util/Identifier;", opcode = Opcodes.GETSTATIC))
    private Identifier logo() {
        if (!this.reload.isComplete()) {
            return Identifier.ofVanilla("textures/gui/title/mojangstudios.png");
        }
        ensureAnimationPlayer();
        if (animationPlayer == null) {
            return Identifier.ofVanilla("textures/gui/title/mojangstudios.png");
        }
        return animationPlayer.getCurrentTexture();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceReload;getProgress()F"))
    private float getProgress(ResourceReload instance) {
        return this.reload.getProgress();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"))
    private void fill(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        if (x1 == 0 && y1 == 0) {
            int finalColor = color;
            if (animationPlayer == null || !animationPlayer.isFinished()) {
                finalColor |= 0xFF000000;
            }
            context.fill(x1, y1, x2, y2, finalColor);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIFFIIIIIII)V", ordinal = 0))
    private void drawTexture0(DrawContext context, RenderPipeline pipeline,
            Identifier texture, int x, int y, float u, float v, int width, int height,
            int uWidth, int vHeight, int textureWidth, int textureHeight, int color) {
        if (!this.reload.isComplete()) {
            context.drawTexture(pipeline, texture, x, y, u, v, width, height, uWidth, vHeight,
                    textureWidth, textureHeight, color);
            return;
        }

        ensureAnimationPlayer();

        if (progress > 0 && !animationStarting && animationPlayer != null && animationPlayer.isFirstLoad()) {
            this.reload.whenComplete().thenAccept(object -> animationPlayer.startAnimation());
            animationStarting = true;
        }

        Identifier currentTexture = animationPlayer != null
                ? animationPlayer.getCurrentTexture()
                : texture;

        if (animationPlayer != null && animationPlayer.isFinished()) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, currentTexture, x, y, 0.0F, 0.0F, width * 2, height,
                    textureWidth, textureHeight,
                    textureWidth, textureHeight, color);
        } else {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, currentTexture, x, y, 0.0F, 0.0F, width * 2, height,
                    textureWidth, textureHeight,
                    textureWidth, textureHeight, color | 0xFF000000);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIFFIIIIIII)V", ordinal = 1))
    private void drawTexture1(DrawContext context, RenderPipeline pipeline,
            Identifier texture, int x, int y, float u, float v, int width, int height,
            int uWidth, int vHeight, int textureWidth, int textureHeight, int color) {
        if (!this.reload.isComplete()) {
            context.drawTexture(pipeline, texture, x, y, u, v, width, height, uWidth, vHeight,
                    textureWidth, textureHeight, color);
        }
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;reloading:Z", opcode = Opcodes.GETFIELD, ordinal = 1))
    private boolean isReloading(SplashOverlay instance) {
        boolean animFinished = animationPlayer != null && animationPlayer.isFinished();
        boolean isFirstLoad = animationPlayer != null && animationPlayer.isFirstLoad();
        return (isFirstLoad && !animFinished) || reloading;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init(II)V"), method = "tick")
    private void onFirstLoadComplete(CallbackInfo ci) {
        if (animationPlayer != null) {
            animationPlayer.markLoadComplete();
        }
    }

    @Unique
    private void ensureAnimationPlayer() {
        if (animationPlayer != null)
            return;

        ModConfig config = MojanglogoanimationClient.config;
        if (config == null || !config.animationEnabled)
            return;

        Identifier id = Identifier.tryParse(config.animationId);
        if (id == null)
            return;

        ISplashScreenAnimationData data = MojanglogoanimationClient.splashLoader.getAnimation(id);

        if (data == null && id.getNamespace().equals("minecraft")) {
            data = MojanglogoanimationClient.splashLoader.getAnimation(
                    Identifier.of(Constants.MOD_ID, id.getPath()));
        }

        if (data == null) {
            return;
        }

        float volume = config.soundVolume / 100.0f;
        animationPlayer = new AnimationPlayer(data, config.soundEnabled, volume);
    }
}
