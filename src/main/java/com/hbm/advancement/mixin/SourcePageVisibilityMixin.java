package com.hbm.advancement.mixin;

import com.hbm.advancement.SourceAchievementData;
import net.minecraft.server.advancements.AdvancementVisibilityEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Publish every genuine source page entry without awarding it or changing its parent requirement. */
@Mixin(AdvancementVisibilityEvaluator.class)
public abstract class SourcePageVisibilityMixin {
    @ModifyVariable(
        method="evaluateVisibility(Lnet/minecraft/advancements/AdvancementNode;Ljava/util/function/Predicate;Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator$Output;)V",
        at=@At("HEAD"),argsOnly=true,ordinal=0,require=1
    )
    private static AdvancementVisibilityEvaluator.Output hbm$showOriginalPage(AdvancementVisibilityEvaluator.Output output){
        return (node,visible)->{
            var source=SourceAchievementData.node(node.holder().id());
            boolean sourceEntry=source!=null&&source.available();
            output.accept(node,visible||sourceEntry);
        };
    }
}
