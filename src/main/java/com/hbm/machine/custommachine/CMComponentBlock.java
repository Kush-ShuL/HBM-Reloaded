package com.hbm.machine.custommachine;

import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;

/** Actual BlockCM enum materials/engine/circuits and same-family internal-face glass suppression. */
public class CMComponentBlock extends Block {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public enum Kind { BLOCK("cm_block",new String[]{"steel","alloy","desh","tcalloy"}),SHEET("cm_sheet",new String[]{"steel","alloy","desh","tcalloy"}),ENGINE("cm_engine",new String[]{"standard","desh","bismuth"}),TANK("cm_tank",new String[]{"steel","alloy","desh","tcalloy"}),CIRCUIT("cm_circuit",new String[]{"aluminium","copper","red_copper","gold","schrabidium"}),PORT("cm_port",new String[]{"steel","alloy","desh","tcalloy"}),FLUX("cm_flux",new String[]{""}),HEAT("cm_heat",new String[]{""});public final String id;public final String[] variants;Kind(String id,String[] variants){this.id=id;this.variants=variants;}}
    public final Kind kind;
    public CMComponentBlock(Kind kind){super(properties(kind));this.kind=kind;registerDefaultState(stateDefinition.any().setValue(META,0));}
    private static Properties properties(Kind kind){var properties=Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops();return kind==Kind.TANK?properties.noOcclusion():properties;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)&15);}
    @Override protected boolean skipRendering(BlockState state,BlockState adjacent,Direction side){return kind==Kind.TANK&&adjacent.is(this)||super.skipRendering(state,adjacent,side);}
    @Override public ItemStack getCloneItemStack(net.minecraft.world.level.LevelReader level,BlockPos pos,BlockState state){return CustomMachineContent.component(kind,Math.abs(state.getValue(META)%kind.variants.length));}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder parameters){return List.of(CustomMachineContent.component(kind,Math.abs(state.getValue(META)%kind.variants.length)));}
}
