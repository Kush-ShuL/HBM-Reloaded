package com.hbm.worldgen.orecave;

import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.*;

/** Both original BlockStalagmite instances, all seven source enum textures and all16 raw block metadata values. */
public final class CaveFormationBlock extends Block {
    public static final IntegerProperty LEGACY_META=IntegerProperty.create("legacy_meta",0,15);
    public static final List<String> VARIANTS=List.of("sulfur","asbestos","ice","snow","glyphid1","glyphid2","glyphid3");
    private final boolean hanging;
    private final MapCodec<CaveFormationBlock> codec;
    public CaveFormationBlock(boolean hanging){super(Properties.of().mapColor(MapColor.STONE).strength(.5F,1.2F).sound(SoundType.STONE).requiresCorrectToolForDrops().noCollission().noOcclusion().isRedstoneConductor((state,level,pos)->false).isSuffocating((state,level,pos)->false).isViewBlocking((state,level,pos)->false));this.hanging=hanging;codec=simpleCodec(properties->new CaveFormationBlock(hanging));registerDefaultState(stateDefinition.any().setValue(LEGACY_META,0));}
    @Override protected MapCodec<? extends Block> codec(){return codec;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(LEGACY_META);}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.empty();}
    // Source BlockStalagmite has default full-unit selection bounds but no collision.
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.block();}
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){BlockPos support=hanging?pos.above():pos.below();return level.getBlockState(support).isFaceSturdy(level,support,hanging?Direction.DOWN:Direction.UP);}
    public boolean canPlaceAt(LevelReader level,BlockPos pos){return defaultBlockState().canSurvive(level,pos);}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos neighborPos,boolean moving){if(!level.isClientSide&&!state.canSurvive(level,pos))level.destroyBlock(pos,true);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){
        // Source getDropsWithoutDamage passes raw block metadata to getItemDropped.
        // Metadata4..15 never drop anything, even if their modulo texture aliases0..3.
        Item item=switch(state.getValue(LEGACY_META)){case 0->HbmContent.item("sulfur");case 1->HbmContent.item("powder_asbestos");case 2->HbmContent.item("powder_ice");case 3->Items.SNOWBALL;default->null;};
        if(item==null)return List.of();Float radius=context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);return radius!=null&&context.getLevel().random.nextFloat()>1F/radius?List.of():List.of(new ItemStack(item));
    }
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){var stack=new ItemStack(this);stack.set(HbmHazards.LEGACY_METADATA.get(),state.getValue(LEGACY_META));return stack;}
}
