package com.karasu256.mojanglogoanimation.mixin.client;

import com.karasu256.mojanglogoanimation.Constants;
import com.karasu256.mojanglogoanimation.client.MojanglogoanimationClient;
import com.karasu256.mojanglogoanimation.client.animation.AnimationPlayer;
import com.karasu256.mojanglogoanimation.client.animation.ISplashScreenAnimationData;
import com.karasu256.mojanglogoanimation.client.config.ModConfig;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.RenderLayer;
import java.util.function.Function;
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
        private MinecraftClient client;
        @Shadow
        @Final
        private boolean reloading;

        @Unique
        private boolean animationStarting = false;

        @Unique
        private AnimationPlayer animationPlayer;

        @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;LOGO:Lnet/minecraft/util/Identifier;"))
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

        @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(Lnet/minecraft/client/render/RenderLayer;IIIII)V"))
        private void fill(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        }

        @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIFFIIIIIII)V", ordinal = 0))
        private void drawTexture0(DrawContext context, Function<Identifier, RenderLayer> renderLayers,
                        Identifier texture, int x, int y, float u, float v, int width, int height,
                        int uWidth, int vHeight, int textureWidth, int textureHeight, int color) {
                if (!this.reload.isComplete()) {
                        context.drawTexture(renderLayers, texture, x, y, u, v, width, height, uWidth, vHeight,
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

                context.drawTexture(RenderLayer::getGuiTextured, currentTexture, x, y, 0.0F, 0.0F, width * 2, height,
                                textureWidth, textureHeight,
                                textureWidth, textureHeight, color);
        }

        @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIFFIIIIIII)V", ordinal = 1))
        private void drawTexture1(DrawContext context, Function<Identifier, RenderLayer> renderLayers,
                        Identifier texture, int x, int y, float u, float v, int width, int height,
                        int uWidth, int vHeight, int textureWidth, int textureHeight, int color) {
                if (!this.reload.isComplete()) {
                        context.drawTexture(renderLayers, texture, x, y, u, v, width, height, uWidth, vHeight,
                                        textureWidth, textureHeight, color);
                }
        }

        @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;reloading:Z", opcode = Opcodes.GETFIELD, ordinal = 2))
        private boolean isReloading(SplashOverlay instance) {
                boolean animFinished = animationPlayer != null && animationPlayer.isFinished();
                boolean isFirstLoad = animationPlayer != null && animationPlayer.isFirstLoad();
                return (isFirstLoad && !animFinished) || reloading;
        }

        @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init(Lnet/minecraft/client/MinecraftClient;II)V"), method = "render")
        private void onFirstLoadComplete(DrawContext context, int mouseX, int mouseY,
                        float delta, CallbackInfo ci) {
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
