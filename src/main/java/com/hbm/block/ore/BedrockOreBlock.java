package com.hbm.block.ore;

import java.util.List;
import com.hbm.fluid.FillableItem;
import com.hbm.fluid.FluidContainerRegistry;
import com.hbm.fluid.FluidStack;
import com.hbm.fluid.HbmFluids;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

/** Original unbreakable bedrock resource; creative edits never consume their held stack. */
public final class BedrockOreBlock extends LegacyOreBlock implements EntityBlock {
    private final MapCodec<BedrockOreBlock> bedrockCodec;
    public BedrockOreBlock(OreSpec spec) { super(spec); bedrockCodec = simpleCodec(ignored -> new BedrockOreBlock(spec)); }
    @Override protected MapCodec<? extends LegacyOreBlock> codec() { return bedrockCodec; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new BedrockOreEntity(pos, state); }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder context) { return List.of(); }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.isEmpty() || !player.getAbilities().instabuild) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof BedrockOreEntity ore) {
            if (stack.getItem() instanceof DrillbitItem drillbit) ore.tier = drillbit.tier(stack);
            else if (FluidContainerRegistry.getFluidType(stack) != HbmFluids.NONE) {
                var type = FluidContainerRegistry.getFluidType(stack);
                ore.acidRequirement = new FluidStack(type, FluidContainerRegistry.getFluidContent(stack, type));
            } else if (stack.getItem() instanceof FillableItem item) {
                var type = item.getFirstFluidType(stack);
                if (type != null) ore.acidRequirement = new FluidStack(type, item.getFill(stack));
            } else { ore.resource = stack.copy(); ore.shape = level.random.nextInt(10); }
            ore.changedAndSync();
        }
        return ItemInteractionResult.CONSUME;
    }
}
