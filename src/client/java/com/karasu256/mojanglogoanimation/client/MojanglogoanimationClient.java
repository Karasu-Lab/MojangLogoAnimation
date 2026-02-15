package com.karasu256.mojanglogoanimation.client;

import com.karasu256.mojanglogoanimation.client.config.ModConfig;
import com.karasu256.mojanglogoanimation.client.loader.SplashLoader;
import com.karasu256.mojanglogoanimation.client.loader.SplashResourceReloadListener;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class MojanglogoanimationClient implements ClientModInitializer {
    public static final SplashLoader splashLoader = new SplashLoader();
    public static ModConfig config;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(
                        new SplashResourceReloadListener());
    }
}
