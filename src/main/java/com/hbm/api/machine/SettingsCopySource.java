package com.hbm.api.machine;

import com.google.gson.Gson;
import com.hbm.multiblock.DummyableBlock;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/** Original source priority: tile first, block second; original BlockDummyable delegates to its core. */
public record SettingsCopySource(ICopiable settings,Block block,BlockPos position) {
    private static final Map<String,String> LEGACY_NAMES=names();
    private static Map<String,String> names() {
        try(var stream=SettingsCopySource.class.getClassLoader().getResourceAsStream("hbm/settings-tool.json")) {
            if(stream==null) return Map.of();
            var root=new Gson().fromJson(new InputStreamReader(stream,StandardCharsets.UTF_8),com.google.gson.JsonObject.class);
            var result=new java.util.HashMap<String,String>();
            root.getAsJsonObject("legacyBlockNames").entrySet().forEach(entry->result.put(entry.getKey(),entry.getValue().getAsString()));return Map.copyOf(result);
        } catch(java.io.IOException error) { throw new IllegalStateException("Cannot read original settings source names",error); }
    }
    public String legacyBlockName() { var id=BuiltInRegistries.BLOCK.getKey(block);return LEGACY_NAMES.getOrDefault(id.toString(),id.getNamespace().equals("minecraft")?block.getDescriptionId():"tile."+id.getPath()); }
    public static SettingsCopySource resolve(Level level,BlockPos position) {
        var entity=level.getBlockEntity(position);var block=level.getBlockState(position).getBlock();
        if(entity instanceof ICopiable settings&&settings.supportsSettingsCopy()) return new SettingsCopySource(settings,block,position);
        if(block instanceof ICopiable settings&&settings.supportsSettingsCopy()) return new SettingsCopySource(settings,block,position);
        if(block instanceof DummyableBlock dummy) return new SettingsCopySource(new ICopiable() {
            private SettingsCopySource core() { var core=dummy.findCore(level,position);if(core==null) return null;var entity=level.getBlockEntity(core);return entity instanceof ICopiable settings&&settings.supportsSettingsCopy()?new SettingsCopySource(settings,block,core):null; }
            @Override public CompoundTag getSettings(Level world,BlockPos pos) { var target=core();return target==null?null:target.settings().getSettings(world,target.position()); }
            @Override public void pasteSettings(CompoundTag data,int index,Level world,Player player,BlockPos pos) { var target=core();if(target!=null) target.settings().pasteSettings(data,index,world,player,target.position()); }
            @Override public String[] infoForDisplay(Level world,BlockPos pos) { var target=core();return target==null?null:target.settings().infoForDisplay(world,pos); }
        },block,position);
        return null;
    }
}
