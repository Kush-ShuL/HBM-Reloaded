package com.hbm.item.analysis;

import com.hbm.api.block.IAnalyzable;
import com.hbm.multiblock.DummyableBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

/** Original ItemAnalysisTool: block/core analysis, four real network values, server yellow chat only. */
public final class AnalysisToolItem extends Item {
    public AnalysisToolItem(){super(new Properties().stacksTo(1));}

    @Override public InteractionResult useOn(UseOnContext context){
        var player=context.getPlayer();
        if(player==null)return InteractionResult.PASS;
        var level=context.getLevel();
        var pos=context.getClickedPos();
        // As in source, retain the clicked block's interface while resolving its dummy coordinates.
        var block=level.getBlockState(pos).getBlock();
        if(block instanceof DummyableBlock dummy){
            var core=dummy.findCore(level,pos);
            if(core!=null)pos=core;
        }
        if(!(block instanceof IAnalyzable analyzable)||!analyzable.supportsAnalysis())return InteractionResult.PASS;
        var debug=analyzable.getDebugInfo(level,pos);
        if(debug!=null&&!level.isClientSide)
            for(String line:debug)player.sendSystemMessage(Component.literal(line).withStyle(ChatFormatting.YELLOW));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
