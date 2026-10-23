package com.hbm.advancement.mixin;

import com.hbm.advancement.SourceAchievementData;
import net.minecraft.advancements.*;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Keep the source61-node Nuclear Tech page coordinates after native tree positioning; other tabs are untouched. */
@Mixin(TreeNodePosition.class)
public abstract class SourcePageLayoutMixin {
    @Inject(method="run",at=@At("RETURN"))
    private static void hbm$sourcePage(AdvancementNode root,CallbackInfo result){if(root.holder().id().equals(ResourceLocation.parse("hbm:nuclear_tech")))place(root);}
    private static void place(AdvancementNode node){var source=SourceAchievementData.node(node.holder().id());if(source!=null)node.advancement().display().ifPresent(display->display.setLocation(source.x()+10,source.y()+7));else if(node.parent()==null)node.advancement().display().ifPresent(display->display.setLocation(-2,-2));for(var child:node.children())place(child);}
}
