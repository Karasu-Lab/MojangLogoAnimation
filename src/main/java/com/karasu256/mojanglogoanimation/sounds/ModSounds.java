package com.karasu256.mojanglogoanimation.sounds;

import com.karasu256.mojanglogoanimation.Constants;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static SoundEvent registerForAnimation(String animationId) {
        Identifier id = Identifier.of(Constants.MOD_ID, "gui.title." + animationId);
        SoundEvent event = SoundEvent.of(id);
        return Registry.register(Registries.SOUND_EVENT, id, event);
    }

    public static void register() {
        registerForAnimation("mojang");
        registerForAnimation("mojang_april_fool");
    }
}
