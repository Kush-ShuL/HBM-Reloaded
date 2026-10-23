package com.hbm.item.surveyscanner;

import com.hbm.block.ore.BedrockOreEntity;
import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmDeepslateOres;
import com.hbm.worldgen.bedrockresource.BedrockResources;
import com.hbm.worldgen.dimensionores.DimensionOres;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalBlock;

/** Actual ItemSurveyScanner; source sampled columns and final bedrock-resource winner are unchanged. */
public final class SurveyScannerItem extends Item {
    public SurveyScannerItem() { super(new Properties().stacksTo(1)); }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer server) scan(server);
        // Explicit source swing, without a second automatic SUCCESS swing.
        player.swing(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    private static void scan(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        int x = Mth.floor(player.getX()), y = Mth.floor(player.getY()), z = Mth.floor(player.getZ());
        boolean oil = false, coltan = false, bedrockOil = false, depth = false, gneiss = false, australium = false;
        BedrockOreEntity lastResource = null;
        var cursor = new BlockPos.MutableBlockPos();
        for (int a = -5; a <= 5; a++) for (int b = -5; b <= 5; b++) {
            for (int i = y + 15; i > level.getMinBuildHeight() + 1; i -= 2) {
                cursor.set(x + a * 5, i, z + b * 5);
                var state = level.getBlockState(cursor);
                // The current real deepslate host is the same ore, not an unrelated scanner target.
                if (HbmDeepslateOres.equivalent(state, "ore_oil")) oil = true;
                else if (HbmDeepslateOres.equivalent(state, "ore_coltan")) coltan = true;
                else if (state.is(HbmContent.block("stone_depth"))) depth = true;
                else if (state.is(HbmContent.block("stone_depth_nether"))) depth = true;
                else if (state.is(HbmContent.block("stone_gneiss"))) gneiss = true;
                else if (HbmDeepslateOres.equivalent(state, "ore_australium")) australium = true;
                else if (state.is(HbmContent.block("ore_bedrock_oil"))) {
                    bedrockOil = true;
                    SurveyScannerMarker.send(player, cursor.immutable(), 0x000000, 15_000, 300, "marker.hbm.survey.bedrock_oil");
                    break;
                }
            }
        }

        int centerX = ((x >> 4) << 4) + 8, centerZ = ((z >> 4) << 4) + 8;
        // Reuse exactly the existing geological floor selection instead of assuming old world Y0.
        int floor = level.dimension().equals(Level.NETHER) ? DimensionOres.netherBase(level)
                : (BedrockResources.ADAPT_OVERWORLD_FLOOR.get() ? level.getMinBuildHeight() : 0) + BedrockResources.OVERWORLD_OFFSET.get();
        for (int i = -2; i <= 2; i++) for (int j = -2; j <= 2; j++) {
            cursor.set(centerX + i, floor, centerZ + j);
            var state = level.getBlockState(cursor);
            // Original scanner prints the same chunk-center diagnostic for all25 checks.
            System.out.println(centerX + " " + centerZ);
            if (state.is(HbmContent.block("ore_bedrock"))) {
                SurveyScannerMarker.send(player, cursor.immutable(), 0xff0000, 15_000, 300, "marker.hbm.survey.bedrock_ore");
                // Deliberately not the first/nearest/most valuable result: original last source-loop BE wins.
                lastResource = (BedrockOreEntity) level.getBlockEntity(cursor);
            }
        }
        if (oil) notice(player, "oil", ChatFormatting.BLACK);
        if (bedrockOil) notice(player, "bedrock_oil", ChatFormatting.BLACK);
        if (coltan) notice(player, "coltan", ChatFormatting.GOLD);
        if (depth) notice(player, "depth", ChatFormatting.GRAY);
        if (gneiss) notice(player, "schist", ChatFormatting.DARK_AQUA);
        if (australium) notice(player, "australium", ChatFormatting.YELLOW);
        if (lastResource != null && !lastResource.resource.isEmpty())
            player.sendSystemMessage(Component.translatable("message.hbm.survey.bedrock_ore", lastResource.resource.getHoverName()).withStyle(ChatFormatting.RED));
    }

    private static void notice(ServerPlayer player, String result, ChatFormatting color) {
        player.sendSystemMessage(Component.translatable("message.hbm.survey." + result).withStyle(color));
    }

    @Override public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !context.getLevel().getBlockState(context.getClickedPos()).is(HbmContent.block("block_beryllium"))) return InteractionResult.PASS;
        // Legacy InventoryPlayer.hasItem looked only at main inventory, ignoring metadata/NBT and never consuming it.
        boolean hasKit = player.getInventory().items.stream().anyMatch(stack -> stack.is(HbmContent.item("entanglement_kit")));
        if (!hasKit) return InteractionResult.PASS;
        if (player instanceof ServerPlayer server) {
            if (server.serverLevel().dimension().equals(Level.END)) {
                // Original EntityPlayerMP.travelToDimension(1) while already in1 is the credits/return path.
                server.showEndCredits();
            } else {
                var transition = ((EndPortalBlock) Blocks.END_PORTAL).getPortalDestination(server.serverLevel(), server, context.getClickedPos());
                if (transition != null) server.changeDimension(transition);
            }
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
}
