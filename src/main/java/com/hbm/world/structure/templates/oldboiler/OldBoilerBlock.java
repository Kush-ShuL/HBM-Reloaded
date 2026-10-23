package com.hbm.world.structure.templates.oldboiler;

import com.hbm.item.autogen.AutogenContent;
import com.hbm.material.HbmMaterials;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Actual X5846 MachineBoiler(false): an abandoned solid block, not a functioning boiler. */
public final class OldBoilerBlock extends Block {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    // The old add->placedBy sequence is collapsed only for actual ItemBlock placement.
    // Commands/native template writes still execute the original neighbour-facing rule.
    private static final ThreadLocal<Integer> ITEM_PLACEMENT=ThreadLocal.withInitial(()->0);
    public OldBoilerBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5F,6F).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    static void beginItemPlacement(){ITEM_PLACEMENT.set(ITEM_PLACEMENT.get()+1);}
    static void endItemPlacement(){int remaining=ITEM_PLACEMENT.get()-1;if(remaining==0)ITEM_PLACEMENT.remove();else ITEM_PLACEMENT.set(remaining);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){float yaw=context.getPlayer()==null?0F:context.getPlayer().getYRot();int quarter=Mth.floor((double)(yaw*4F/360F)+.5D)&3;return defaultBlockState().setValue(META,new int[]{2,5,3,4}[quarter]);}
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moving){
        super.onPlace(state,level,pos,old,moving);
        if(level.isClientSide||old.is(this)||ITEM_PLACEMENT.get()!=0)return;
        boolean north=opaque(level,pos.north()),south=opaque(level,pos.south()),west=opaque(level,pos.west()),east=opaque(level,pos.east());
        int facing=3;
        if(north&&!south)facing=3;
        if(south&&!north)facing=2;
        if(west&&!east)facing=5;
        if(east&&!west)facing=4;
        if(state.getValue(META)!=facing)level.setBlock(pos,state.setValue(META,facing),Block.UPDATE_CLIENTS);
    }
    private static boolean opaque(Level level,BlockPos pos){return level.getBlockState(pos).isSolidRender(level,pos);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){
        ItemStack tool=context.getOptionalParameter(LootContextParams.TOOL);
        // Source vanilla harvestBlock silk path bypasses MachineBoiler.getDrops.
        if(tool!=null&&EnchantmentHelper.getItemEnchantmentLevel(context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH),tool)>0)return List.of(new ItemStack(this));
        ItemStack scraps=HbmMaterials.bySymbol("MAT_STEEL").make(AutogenContent.item("scraps"));
        scraps.setCount(3+context.getLevel().random.nextInt(4));
        return List.of(scraps);
    }
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return new ItemStack(this);}
    // Source has no INBTBlockTransformable and no native rotation override.
}
