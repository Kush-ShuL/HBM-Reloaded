package com.hbm.world.structure.templates.materials;

import com.hbm.damage.HbmDamageSources;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.*;

/** X5846 Spikes: no collision, source downward velocity threshold and actual armor-bypassing damage type. */
public final class TemplateSpikesBlock extends Block {
    public TemplateSpikesBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(2.5F,3F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return Shapes.empty();}
    @Override protected void entityInside(BlockState state,Level world,BlockPos pos,Entity entity){
        if(entity instanceof LivingEntity&&entity.getDeltaMovement().y<-.1D&&entity.hurt(HbmDamageSources.source(world,HbmDamageSources.spikes),100F))
            world.playSound(null,pos.getX()+.5D,pos.getY()+.5D,pos.getZ()+.5D,HbmSounds.get("entity.slicer"),SoundSource.BLOCKS,1F,1F);
    }
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){Float radius=context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);return radius!=null&&context.getLevel().random.nextFloat()>1F/radius?List.of():List.of(new ItemStack(this));}
}
