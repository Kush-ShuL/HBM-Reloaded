package com.hbm.world.structure.templates.computer;
import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.*;
/** Actual BlockDecoModel metadata packing, four rotated authored bounds and one final placement state. */
public final class ComputerBlock extends Block {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private static final VoxelShape[] SHAPES={box(2,0,6,14,14,16),box(2,0,0,14,14,10),box(6,0,2,16,14,14),box(0,0,2,10,14,14)};
    public ComputerBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5F,6F).noOcclusion().requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){float yaw=context.getPlayer()==null?0F:context.getPlayer().getYRot();int face=Mth.floor((double)(yaw*4F/360F)+.5D)&3,rotation=(face&1)!=1?face>>1:face==3?2:3;int type=context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);return defaultBlockState().setValue(META,((rotation<<2)|type)&15);}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return SHAPES[state.getValue(META)>>2];}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return getShape(state,level,pos,context);}
    public ItemStack stack(int metadata){var stack=new ItemStack(this);stack.set(HbmHazards.LEGACY_METADATA.get(),metadata&3);return stack;}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){return List.of(stack(state.getValue(META)));}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return stack(state.getValue(META));}
    // No native rotation override: TemplateSourceStateProcessor applies the literal INBTBlockTransformable algorithm before native placement.
}
