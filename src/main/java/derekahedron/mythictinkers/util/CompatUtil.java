package derekahedron.mythictinkers.util;

import net.minecraft.resources.ResourceLocation;

public class CompatUtil {

    public static ResourceLocation forge(String id) {
        return new ResourceLocation("forge", id);
    }

    public static ResourceLocation betterEnd(String id) {
        return new ResourceLocation("betterend", id);
    }

    public static ResourceLocation biomesOPlenty(String id) {
        return new ResourceLocation("biomesoplenty", id);
    }

    public static ResourceLocation create(String id) {
        return new ResourceLocation("create", id);
    }

    public static ResourceLocation galosphere(String id) {
        return new ResourceLocation("galosphere", id);
    }

    public static ResourceLocation scorchedGuns(String id) {
        return new ResourceLocation("scguns", id);
    }

    public static ResourceLocation tinkersKatanas(String id) {
        return new ResourceLocation("tinkers_katanas", id);
    }

    public static ResourceLocation yungsCaveBiomes(String id) {
        return new ResourceLocation("yungscavebiomes", id);
    }
}
