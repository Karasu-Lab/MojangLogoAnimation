package com.karasu256.mojanglogoanimation.client.animation;

import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.util.function.Supplier;

public record ResourceEntry(Identifier id, Supplier<InputStream> dataSupplier) {
}
