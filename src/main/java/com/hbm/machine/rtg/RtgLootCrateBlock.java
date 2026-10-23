package com.hbm.machine.rtg;

import com.hbm.block.crate.CrateContent;
import com.hbm.registry.HbmSounds;
import com.hbm.world.structure.loot.LegacyCrateLoot;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Same original BlockCrate physics and independent RNG; true fixed source pools are shared. */
public final class RtgLootCrateBlock extends FallingBlock {
    private final String pool;private final boolean lead;private final MapCodec<RtgLootCrateBlock> codec;
    public RtgLootCrateBlock(String pool,boolean lead){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.METAL).requiresCorrectToolForDrops());this.pool=pool;this.lead=lead;codec=simpleCodec(properties->new RtgLootCrateBlock(pool,lead));}
    @Override protected MapCodec<? extends FallingBlock> codec(){return codec;}
    @Override public int getDustColor(BlockState state,BlockGetter world,BlockPos pos){return lead?0xff747474:0xff82858a;}
    @Override protected ItemInteractionResult useItemOn(ItemStack held,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(!held.is(CrateContent.CROWBAR.get()))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(level instanceof ServerLevel server){var random=new Random();for(var stack:LegacyCrateLoot.contents(server,pool,random)){float x=random.nextFloat()*.8F+.1F,y=random.nextFloat()*.8F+.1F,z=random.nextFloat()*.8F+.1F;var drop=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,stack);drop.setDeltaMovement((float)random.nextGaussian()*.05F,(float)random.nextGaussian()*.05F+.2F,(float)random.nextGaussian()*.05F);level.addFreshEntity(drop);}level.removeBlock(pos,false);level.playSound(null,pos.getX(),pos.getY(),pos.getZ(),HbmSounds.get("block.crateBreak"),SoundSource.BLOCKS,.5F,1);}
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
}
