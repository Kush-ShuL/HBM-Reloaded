package com.hbm.world.soyuz;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

/** Source ItemModRecord("glass"): real jukebox playback, rare, hidden, vanilla record11's display name. */
public class GlassRecordItem extends Item {
    public GlassRecordItem() { this("glass"); }
    /** Shared original ItemModRecord song-key constructor; existing hidden glass keeps exactly its key/name. */
    protected GlassRecordItem(String song) {
        super(new Properties().stacksTo(1).rarity(Rarity.RARE)
                .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath("hbm", song))));
    }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.minecraft.music_disc_11"); }
}
