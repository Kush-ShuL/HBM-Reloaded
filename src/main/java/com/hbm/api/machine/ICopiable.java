package com.hbm.api.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Original X5846 ICopiable, with a modern source context instead of Either<TileEntity,Block>. */
public interface ICopiable {
    CompoundTag getSettings(Level level,BlockPos position);
    void pasteSettings(CompoundTag data,int index,Level level,Player player,BlockPos position);
    default String getSettingsSourceID(SettingsCopySource source) { return source.legacyBlockName(); }
    default Component getSettingsSourceDisplay(SettingsCopySource source) { return source.block().getName(); }
    default String[] infoForDisplay(Level level,BlockPos position) { return null; }
    /** Modern unified BE classes can exclude variants whose original class did not implement ICopiable. */
    default boolean supportsSettingsCopy() { return true; }
}
