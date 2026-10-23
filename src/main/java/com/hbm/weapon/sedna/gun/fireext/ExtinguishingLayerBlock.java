package com.hbm.weapon.sedna.gun.fireext;
import com.hbm.block.falling.LegacyFallingRules;
import com.hbm.machine.drain.OilSpillBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Foam/boron variants of the existing exact BlockLayering backend, with original material/replaceability only. */
public final class ExtinguishingLayerBlock extends OilSpillBlock {
 public final boolean foam;
 public ExtinguishingLayerBlock(boolean foam){super(properties(foam));this.foam=foam;}
 private static Properties properties(boolean foam){var properties=Properties.of().mapColor(foam?MapColor.SNOW:MapColor.SAND).strength(.1F,.1F).sound(foam?SoundType.SNOW:SoundType.SAND).noOcclusion().noLootTable().dynamicShape();if(foam)properties.replaceable();return properties;}
 public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(META,metadata&15);}
 public boolean sourceReplaceable(BlockState state){return foam||state.getValue(META)<7&&LegacyFallingRules.material(state).isReplaceable();}
 @Override protected boolean canBeReplaced(BlockState state,BlockPlaceContext context){return sourceReplaceable(state);}
 public boolean sourceCanPlace(LevelReader level,BlockPos pos){return defaultBlockState().canSurvive(level,pos);}
 @Override protected boolean propagatesSkylightDown(BlockState state,net.minecraft.world.level.BlockGetter level,BlockPos pos){return true;}
 @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block block,BlockPos from,boolean moving){if(!sourceCanPlace(level,pos))level.setBlock(pos,Blocks.AIR.defaultBlockState(),UPDATE_ALL);}
 @Override public void playerDestroy(Level level,Player player,BlockPos pos,BlockState state,BlockEntity blockEntity,ItemStack stack){super.playerDestroy(level,player,pos,state,blockEntity,stack);level.setBlock(pos,Blocks.AIR.defaultBlockState(),UPDATE_ALL);}
 // Ordinary source ItemBlock does not expose variants or stack snow layers during placement.
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState();}
}
