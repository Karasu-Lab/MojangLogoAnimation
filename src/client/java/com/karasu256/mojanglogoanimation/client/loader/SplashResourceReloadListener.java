package com.karasu256.mojanglogoanimation.client.loader;

import com.karasu256.mojanglogoanimation.client.MojanglogoanimationClient;
import com.karasu256.mojanglogoanimation.client.animation.FolderAnimationData;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplashResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final Identifier ID = Identifier.of("mojanglogoanimation", "splash_loader");
    private static final Pattern FRAME_PATTERN = Pattern.compile("^mojang(\\d+)\\.png$");

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        MojanglogoanimationClient.splashLoader.clear();

        Map<Identifier, Integer> maxFrames = new HashMap<>();

        Map<Identifier, Resource> resources = manager.findResources("textures/gui/title",
                id -> id.getPath().endsWith(".png"));

        for (Identifier resourceId : resources.keySet()) {
            String path = resourceId.getPath();
            String relativePath = path.substring("textures/gui/title/".length());

            int lastSlash = relativePath.lastIndexOf('/');
            if (lastSlash == -1)
                continue;

            String animPath = relativePath.substring(0, lastSlash);
            String filename = relativePath.substring(lastSlash + 1);

            Matcher matcher = FRAME_PATTERN.matcher(filename);
            if (matcher.matches()) {
                try {
                    int frameIndex = Integer.parseInt(matcher.group(1));
                    Identifier animId = Identifier.of(resourceId.getNamespace(), animPath);
                    maxFrames.merge(animId, frameIndex, Math::max);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        for (Map.Entry<Identifier, Integer> entry : maxFrames.entrySet()) {
            MojanglogoanimationClient.splashLoader.register(
                    new FolderAnimationData(entry.getKey(), entry.getValue() + 1));
        }
    }
}
