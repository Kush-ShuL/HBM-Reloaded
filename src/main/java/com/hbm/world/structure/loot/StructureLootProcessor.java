package com.hbm.world.structure.loot;
import com.hbm.machine.storage.LockableBlockEntity;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.phys.Vec3;
/** Stateless processor shared by ordinary native StructureTemplates and vanilla Jigsaw pool elements. */
public final class StructureLootProcessor extends StructureProcessor {
    public static final StructureLootProcessor INSTANCE=new StructureLootProcessor();
    public static final MapCodec<StructureLootProcessor> CODEC=MapCodec.unit(INSTANCE);
    private StructureLootProcessor(){}
    @Override protected StructureProcessorType<?> getType(){return StructureLootContent.PROCESSOR.get();}
    @Override public StructureTemplate.StructureBlockInfo processBlock(LevelReader reader,BlockPos offset,BlockPos pivot,StructureTemplate.StructureBlockInfo original,StructureTemplate.StructureBlockInfo current,StructurePlaceSettings settings){
        if(current.nbt()==null)return current;var tag=current.nbt().copy();boolean marker=tag.contains("hbm_loot_replacement",Tag.TAG_COMPOUND),inventory=tag.getBoolean("hbm_legacy_items");if(!marker&&!inventory)return current;
        if(settings.getBoundingBox()!=null&&!settings.getBoundingBox().isInside(current.pos()))return current;
        if(!(reader instanceof ServerLevelAccessor accessor))throw new IllegalStateException("Source structure loot requires an actual server world");ServerLevel level=accessor.getLevel();
        if(inventory){var palette=new HashMap<Integer,String>();for(var entry:tag.getList("hbm_item_palette",Tag.TAG_COMPOUND)){var item=(CompoundTag)entry;palette.put(item.getInt("id"),item.getString("name"));}tag=LegacyStructureItems.convertInventory(tag,palette,level.registryAccess());if(tag.getString("hbm_inventory_case").equals("vanilla")){String old=tag.contains("items")?"items":"Items";var items=tag.getList(old,Tag.TAG_COMPOUND);for(var entry:items){var item=(CompoundTag)entry;if(item.contains("slot")){item.put("Slot",item.get("slot").copy());item.remove("slot");}}tag.remove("items");tag.put("Items",items);}tag.remove("hbm_legacy_items");tag.remove("hbm_item_palette");tag.remove("hbm_inventory_case");}
        if(!marker)return new StructureTemplate.StructureBlockInfo(current.pos(),current.state(),tag);
        var encoded=BlockState.CODEC.parse(level.registryAccess().createSerializationContext(NbtOps.INSTANCE),tag.getCompound("hbm_loot_replacement")).getOrThrow();
        var replacement=encoded.mirror(settings.getMirror()).rotate(settings.getRotation());
        if(!(replacement.getBlock() instanceof EntityBlock host))throw new IllegalStateException("Source loot marker replacement has no actual native block entity "+replacement);var entity=host.newBlockEntity(current.pos(),replacement);if(entity==null)throw new IllegalStateException("Source loot marker replacement created no native block entity "+replacement);
        // Constructed target has its true fresh defaults. No phantom placed BE,
        // fake player or after-placement global queue is needed in the modern API.
        if(entity instanceof LockableBlockEntity lock&&tag.getInt("lockCode")!=0){lock.setPins(tag.getInt("lockCode"));lock.setMod(tag.getFloat("lockMod"));lock.cheesable=tag.getFloat("lockMod")!=0;lock.lock();}
        // Native generation owns a worker-local stream; a non-region placement uses
        // StructurePlaceSettings' native per-placement/position RNG, never ServerLevel.random.
        var random=reader instanceof net.minecraft.server.level.WorldGenRegion region?region.getRandom():settings.getRandom(current.pos());
        if(entity instanceof Container container){int count=LegacyStructureLoot.markerAmount(tag.getInt("min"),tag.getInt("max"),random);LegacyStructureLoot.fill(level,Vec3.atCenterOf(current.pos()),container,tag.getString("pool"),count,random);}else if(entity instanceof LootPileBlockEntity pile)StructureLootPresets.populate(level,pile,tag.getString("pool"),random);else throw new IllegalStateException("Source marker target is not its original container/pile: "+replacement);
        return new StructureTemplate.StructureBlockInfo(current.pos(),encoded,entity.saveWithFullMetadata(level.registryAccess()));
    }
}
