package com.hbm.item.structurewand;

import com.hbm.world.structure.templates.TemplateStateMappings;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/** Native block identity and intrinsic state, replacing the source's Block/meta pair. */
public final class StructureBlacklist {
    public record Key(BlockState state) {
        public Block actual(){return state.getBlock();}
        public String description(){return state.toString();}
    }
    private static <T extends Comparable<T>> BlockState reset(BlockState state,Property<T> property){return state.setValue(property,state.getBlock().defaultBlockState().getValue(property));}
    public static Key capture(BlockState state){
        var block=state.getBlock();
        for(var property:state.getProperties()){
            String name=property.getName();
            boolean derived=name.equals("waterlogged")
                ||block instanceof LeavesBlock&&name.equals("distance")
                ||block instanceof StairBlock&&name.equals("shape")
                ||block instanceof ChestBlock&&name.equals("type")
                ||(block instanceof FenceBlock||block instanceof IronBarsBlock||block instanceof WallBlock||block instanceof RedStoneWireBlock)&&Set.of("north","east","south","west","up").contains(name);
            if(derived)state=reset(state,property);
        }
        return new Key(state);
    }
    public static Set<Key> read(CompoundTag tag){
        var result=new HashSet<Key>();for(var raw:tag.getList("hbm_blacklist",Tag.TAG_COMPOUND))result.add(capture(TemplateStateMappings.parse((CompoundTag)raw)));return result;
    }
    public static void write(CompoundTag tag,Set<Key> keys){var list=new ListTag();for(var key:keys)list.add(NbtUtils.writeBlockState(key.state()));tag.put("hbm_blacklist",list);}
    public static boolean isAuthoring(Block block,boolean includeAir){if(block instanceof StructureAuthoringBlock)return true;String id=BuiltInRegistries.BLOCK.getKey(block).toString();return (includeAir&&id.equals("hbm:wand_air"))||Set.of("hbm:wand_jigsaw","hbm:wand_logic","hbm:wand_tandem","hbm:wand_loot").contains(id);}
    private StructureBlacklist(){}
}
