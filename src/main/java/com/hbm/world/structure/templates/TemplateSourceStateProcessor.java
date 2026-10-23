package com.hbm.world.structure.templates;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/** Source metadata is transformed before decoding. Native placement applies mirror/rotate AFTER processors, so explicitly invert that final operation. */
public final class TemplateSourceStateProcessor extends StructureProcessor {
    public static final TemplateSourceStateProcessor INSTANCE=new TemplateSourceStateProcessor();
    public static final MapCodec<TemplateSourceStateProcessor> CODEC=MapCodec.unit(INSTANCE);
    public static final String SOURCE="hbm_legacy_state";
    @Override public StructureBlockInfo processBlock(LevelReader level,BlockPos origin,BlockPos pivot,StructureBlockInfo original,StructureBlockInfo transformed,StructurePlaceSettings settings){
        if(original.nbt()==null||original.nbt().contains("hbm_loot_replacement",Tag.TAG_COMPOUND)||!original.nbt().contains(SOURCE,Tag.TAG_COMPOUND)||original.state().is(Blocks.JIGSAW))return transformed;
        var marker=original.nbt().getCompound(SOURCE);int turns=switch(settings.getRotation()){case NONE->0;case CLOCKWISE_90->1;case CLOCKWISE_180->2;case COUNTERCLOCKWISE_90->3;};
        var alternatives=marker.getList("rotations",Tag.TAG_COMPOUND);
        BlockState desired=alternatives.size()==4?TemplateStateMappings.parse(alternatives.getCompound(turns)):TemplateStateMappings.resolve(marker.getString("name"),marker.getInt("meta"),turns);
        // No mirrored generation existed in X5846. Compensating any native mirror still preserves the exact source state decision.
        Rotation inverse=switch(settings.getRotation()){case NONE->Rotation.NONE;case CLOCKWISE_90->Rotation.COUNTERCLOCKWISE_90;case CLOCKWISE_180->Rotation.CLOCKWISE_180;case COUNTERCLOCKWISE_90->Rotation.CLOCKWISE_90;};
        BlockState beforeNative=desired.rotate(inverse).mirror(settings.getMirror());
        CompoundTag data=transformed.nbt()==null?null:transformed.nbt().copy();
        if(data!=null){
            data.remove(SOURCE);
            // Source-compatible BE fields are loaded by the actual admitted block's actual registered type, including core/proxy distinctions.
            if(data.getBoolean("hbm_legacy_block_entity")){
                data.remove("hbm_legacy_block_entity");
                if(!(desired.getBlock() instanceof EntityBlock factory))throw new IllegalStateException("Source BE has no actual native EntityBlock: "+desired);
                var entity=factory.newBlockEntity(transformed.pos(),desired);if(entity==null)throw new IllegalStateException("Source BE has no actual native instance: "+desired);
                var id=BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(entity.getType());if(id==null)throw new IllegalStateException("Unregistered actual template block entity");data.putString("id",id.toString());
                if(entity instanceof com.hbm.item.structurelooteditor.LootEditorEntity&&com.hbm.world.structure.tandem.TandemSourceScope.active())com.hbm.item.structurelooteditor.LootEditorEntity.sourceTransform(data,turns);
                // Source TileEntityPlushie.transformTE uses world.rand, even when rotation is NONE.
                if(entity instanceof com.hbm.item.bobmazondeps.PlushieEntity){
                    if(!(level instanceof net.minecraft.world.level.WorldGenLevel region))throw new IllegalStateException("Source plushie transformation requires the actual generation world");
                    var random=region instanceof net.minecraft.server.level.WorldGenRegion worker?worker.getRandom():settings.getRandom(transformed.pos());
                    data.putByte("type",(byte)com.hbm.item.bobmazondeps.PlushieEntity.transformedType(random));
                }
            }
            if(data.isEmpty())data=null;
        }
        return new StructureBlockInfo(transformed.pos(),beforeNative,data);
    }
    @Override protected StructureProcessorType<?> getType(){return TemplateProcessors.SOURCE_STATES.get();}
    private TemplateSourceStateProcessor(){}
}
