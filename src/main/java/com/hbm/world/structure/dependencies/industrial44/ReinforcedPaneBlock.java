package com.hbm.world.structure.dependencies.industrial44;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import com.mojang.serialization.MapCodec;
import com.hbm.hazard.HbmHazards;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;
import net.minecraft.world.phys.shapes.*;
/** BlockNTMGlassPane(0,rock,false), including the old isolated cross collision and bounding-box selection. */
public final class ReinforcedPaneBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED=BlockStateProperties.WATERLOGGED;
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public static final BooleanProperty NORTH=BlockStateProperties.NORTH,EAST=BlockStateProperties.EAST,SOUTH=BlockStateProperties.SOUTH,WEST=BlockStateProperties.WEST;
 private static final Map<Direction,BooleanProperty> SIDES=Map.of(Direction.NORTH,NORTH,Direction.EAST,EAST,Direction.SOUTH,SOUTH,Direction.WEST,WEST);
 // Exact BlockNTMGlass / CT initializers in immutable ModBlocks; absent IDs are not registered here.
 private static final Set<String> SOURCE_GLASS=Set.of("reinforced_glass","reinforced_laminate","glass_boron","glass_lead","glass_uranium","glass_trinitite","glass_polonium","glass_ash","glass_quartz","glass_polarized");
 private final boolean ordinaryDrops;
 public ReinforcedPaneBlock(){this(Properties.of().mapColor(MapColor.STONE).strength(2,15).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion().isValidSpawn((s,w,p,t)->false).isViewBlocking((s,w,p)->false).isSuffocating((s,w,p)->false),false);}
 public ReinforcedPaneBlock(Properties properties,boolean ordinaryDrops){super(properties);this.ordinaryDrops=ordinaryDrops;registerDefaultState(stateDefinition.any().setValue(META,0).setValue(NORTH,false).setValue(EAST,false).setValue(SOUTH,false).setValue(WEST,false).setValue(WATERLOGGED,false));}
 @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META,NORTH,EAST,SOUTH,WEST,WATERLOGGED);}
 public boolean connects(BlockGetter world,BlockPos pos,Direction outward){BlockState state=world.getBlockState(pos);var block=state.getBlock();var id=BuiltInRegistries.BLOCK.getKey(block);return state.isSolidRender(world,pos)||block==this||block instanceof IronBarsBlock||block==Blocks.GLASS||block instanceof StainedGlassBlock||id.getNamespace().equals("hbm")&&SOURCE_GLASS.contains(id.getPath())||state.isFaceSturdy(world,pos,outward.getOpposite());}
 private BlockState connected(BlockState state,BlockGetter world,BlockPos pos){for(var entry:SIDES.entrySet())state=state.setValue(entry.getValue(),connects(world,pos.relative(entry.getKey()),entry.getKey()));return state;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){return connected(defaultBlockState().setValue(WATERLOGGED,context.getLevel().getFluidState(context.getClickedPos()).getType()==Fluids.WATER),context.getLevel(),context.getClickedPos());}
 @Override protected BlockState updateShape(BlockState state,Direction direction,BlockState neighbor,LevelAccessor world,BlockPos pos,BlockPos neighborPos){if(state.getValue(WATERLOGGED))world.scheduleTick(pos,Fluids.WATER,Fluids.WATER.getTickDelay(world));return direction.getAxis().isHorizontal()?state.setValue(SIDES.get(direction),connects(world,neighborPos,direction)):state;}
 public static int mask(BlockState state){return (state.getValue(NORTH)?1:0)|(state.getValue(SOUTH)?2:0)|(state.getValue(WEST)?4:0)|(state.getValue(EAST)?8:0);}
 private static VoxelShape collision(int mask){boolean n=(mask&1)!=0,s=(mask&2)!=0,w=(mask&4)!=0,e=(mask&8)!=0;VoxelShape shape=Shapes.empty();if(w&&e||mask==0)shape=Shapes.or(shape,Block.box(0,0,7,16,16,9));else if(w)shape=Shapes.or(shape,Block.box(0,0,7,8,16,9));else if(e)shape=Shapes.or(shape,Block.box(8,0,7,16,16,9));if(n&&s||mask==0)shape=Shapes.or(shape,Block.box(7,0,0,9,16,16));else if(n)shape=Shapes.or(shape,Block.box(7,0,0,9,16,8));else if(s)shape=Shapes.or(shape,Block.box(7,0,8,9,16,16));return shape;}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return collision(mask(connected(state,world,pos)));}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){int m=mask(connected(state,world,pos));return Block.box((m&4)!=0||m==0?0:7,0,(m&1)!=0||m==0?0:7,(m&8)!=0||m==0?16:9,16,(m&2)!=0||m==0?16:9);}
 @Override protected VoxelShape getVisualShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return Shapes.empty();}
 @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 1;}
 @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return !state.getValue(WATERLOGGED);}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){if(ordinaryDrops)return super.getDrops(state,context);var tool=context.getOptionalParameter(LootContextParams.TOOL);if(tool==null)return List.of();var registry=context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);return EnchantmentHelper.getItemEnchantmentLevel(registry.getOrThrow(Enchantments.SILK_TOUCH),tool)>0?List.of(silkStack(state)):List.of();}
 private ItemStack silkStack(BlockState state){ItemStack stack=new ItemStack(this);int metadata=state.getValue(META);if(metadata!=0)stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);return stack;}
 @Override protected BlockState rotate(BlockState state,Rotation rotation){BlockState result=state;for(var side:SIDES.keySet())result=result.setValue(SIDES.get(rotation.rotate(side)),state.getValue(SIDES.get(side)));return result;}
 @Override protected BlockState mirror(BlockState state,Mirror mirror){BlockState result=state;for(var side:SIDES.keySet())result=result.setValue(SIDES.get(mirror.mirror(side)),state.getValue(SIDES.get(side)));return result;}

    @Override protected FluidState getFluidState(BlockState state){
        return state.getValue(WATERLOGGED)?Fluids.WATER.getSource(false):super.getFluidState(state);
    }
}
