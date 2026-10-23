package com.hbm.client.structure.logic;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.world.structure.logic.LogicAuthoring;
import com.hbm.world.structure.logic.LogicDisguiseState;
import com.hbm.world.structure.logic.WandLogicBlock;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/** Original ILookOverlay authoring readout, independent of Jade and preserving source row order. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class WandLogicOverlay {
    @SubscribeEvent public static void render(RenderGuiEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.level == null || !(mc.hitResult instanceof BlockHitResult hit)
                || !(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof WandLogicBlock block)
                || !(mc.level.getBlockEntity(hit.getBlockPos()) instanceof LogicAuthoring tile)) return;
        var none = Component.translatable("hud.hbm.wand_logic.none");
        Component disguise = none;
        var actual = LogicDisguiseState.resolve(tile.disguiseId,tile.disguiseMeta);
        if (actual != null && !actual.is(Blocks.AIR)) disguise = actual.getBlock().getName();
        var lines = List.of(Component.translatable("hud.hbm.wand_logic.action",tile.actionID),
                Component.translatable("hud.hbm.wand_logic.condition",tile.conditionID),
                Component.translatable("hud.hbm.wand_logic.interaction",tile.interactionID == null ? none : Component.literal(tile.interactionID)),
                Component.translatable("hud.hbm.wand_logic.disguise",disguise));
        var graphics = event.getGuiGraphics(); var font = LegacyFonts.get();
        int x = graphics.guiWidth()/2+8, y = graphics.guiHeight()/2;
        graphics.drawString(font,block.getName(),x+1,y-9,0x404000,false);
        graphics.drawString(font,block.getName(),x,y-10,0xffff00,false);
        for (var line : lines) { graphics.drawString(font,line,x,y,0xffffff,true); y += 10; }
    }
    private WandLogicOverlay() {}
}
