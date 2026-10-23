package com.hbm.world.structure.loot;
import com.hbm.block.crate.CrateContent;
import com.hbm.registry.HbmSounds;
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
/** Original gravity, full cube, unchanged source texture, crowbar-only opening and independent RNG. */
public final class FallingSupplyCrateBlock extends FallingBlock {
    public static final MapCodec<FallingSupplyCrateBlock> CODEC=simpleCodec(properties->new FallingSupplyCrateBlock());
    public FallingSupplyCrateBlock(){super(Properties.of().mapColor(MapColor.WOOD).strength(5,10).sound(SoundType.WOOD));}
    @Override protected MapCodec<? extends FallingBlock> codec(){return CODEC;}
    @Override public int getDustColor(BlockState state,BlockGetter world,BlockPos pos){return 0xff79563a;}
    @Override protected ItemInteractionResult useItemOn(ItemStack held,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(!held.is(CrateContent.CROWBAR.get()))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;if(level instanceof ServerLevel server){var random=new Random();for(var stack:LegacyCrateLoot.contents(server,"crateList",random)){float x=random.nextFloat()*.8F+.1F,y=random.nextFloat()*.8F+.1F,z=random.nextFloat()*.8F+.1F;var drop=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,stack);drop.setDeltaMovement((float)random.nextGaussian()*.05F,(float)random.nextGaussian()*.05F+.2F,(float)random.nextGaussian()*.05F);level.addFreshEntity(drop);}level.removeBlock(pos,false);level.playSound(null,pos.getX(),pos.getY(),pos.getZ(),HbmSounds.get("block.crateBreak"),SoundSource.BLOCKS,.5F,1);}return ItemInteractionResult.sidedSuccess(level.isClientSide);}
}
