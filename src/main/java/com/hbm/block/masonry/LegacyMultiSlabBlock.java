package com.hbm.block.masonry;

import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.storage.loot.LootParams;

/** Source BlockMultiSlab: material metadata, separate double block and original parent properties. */
public class LegacyMultiSlabBlock extends SlabBlock {
    public static final IntegerProperty MATERIAL = IntegerProperty.create("material", 0, 7);
    private final boolean doubled;
    private final List<String> materials;
    private final Supplier<LegacyMultiSlabBlock> single, full;
    private final MapCodec<LegacyMultiSlabBlock> codec;

    public LegacyMultiSlabBlock(boolean doubled, List<String> materials, Supplier<LegacyMultiSlabBlock> single, Supplier<LegacyMultiSlabBlock> full) {
        this(Properties.of().strength(1).requiresCorrectToolForDrops().dynamicShape(), doubled, materials, single, full);
    }
    protected LegacyMultiSlabBlock(Properties properties, boolean doubled, List<String> materials, Supplier<LegacyMultiSlabBlock> single, Supplier<LegacyMultiSlabBlock> full) {
        super(properties);
        this.doubled=doubled; this.materials=materials; this.single=single; this.full=full;
        codec=simpleCodec(p -> new LegacyMultiSlabBlock(p,doubled,materials,single,full));
        registerDefaultState(defaultBlockState().setValue(MATERIAL,0).setValue(TYPE,doubled?SlabType.DOUBLE:SlabType.BOTTOM).setValue(WATERLOGGED,false));
    }
    @Override public MapCodec<? extends SlabBlock> codec() { return codec; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(MATERIAL); }
    public int material(int metadata) { return (metadata & 7) % materials.size(); }
    public String materialName(int metadata) { return materials.get(material(metadata)); }
    public BlockState fromLegacy(int metadata) { return defaultBlockState().setValue(MATERIAL,metadata&7).setValue(TYPE,doubled?SlabType.DOUBLE:(metadata&8)==0?SlabType.BOTTOM:SlabType.TOP); }
    protected BlockState parent(BlockState state) { return HbmContent.block(materialName(state.getValue(MATERIAL))).defaultBlockState(); }
    public ItemStack stack(int metadata, int count) { ItemStack stack=new ItemStack(single.get(),count); stack.set(HbmHazards.LEGACY_METADATA.get(),material(metadata)); return stack; }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        int material=context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)&7;
        BlockState current=context.getLevel().getBlockState(context.getClickedPos());
        if (!doubled && current.is(this)) return full.get().defaultBlockState().setValue(TYPE,SlabType.DOUBLE).setValue(MATERIAL,current.getValue(MATERIAL));
        if (doubled) return defaultBlockState().setValue(MATERIAL,material);
        return super.getStateForPlacement(context).setValue(MATERIAL,material);
    }
    @Override protected boolean canBeReplaced(BlockState state,BlockPlaceContext context) {
        return !doubled && state.getValue(MATERIAL)==(context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)&7) && super.canBeReplaced(state,context);
    }
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context) { return List.of(stack(state.getValue(MATERIAL),doubled?2:1)); }
    @Override public ItemStack getCloneItemStack(LevelReader world,BlockPos pos,BlockState state) { return stack(state.getValue(MATERIAL),1); }
    @Override protected float getDestroyProgress(BlockState state,Player player,BlockGetter world,BlockPos pos) {
        float hardness=parent(state).getDestroySpeed(world,pos);
        if (hardness==-1) return 0;
        int divisor=net.neoforged.neoforge.event.EventHooks.doPlayerHarvestCheck(player,state,world,pos)?30:100;
        return player.getDigSpeed(state,pos)/hardness/divisor;
    }
    @Override public float getExplosionResistance(BlockState state,BlockGetter world,BlockPos pos,Explosion explosion) { return parent(state).getBlock().getExplosionResistance(); }
}
