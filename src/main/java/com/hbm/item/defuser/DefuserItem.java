package com.hbm.item.defuser;
import com.hbm.api.tool.ToolableBlock.ToolType;
import com.hbm.item.tool.ToolingItem;
import com.hbm.machine.pedestal.PedestalDefuser;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
/** Original ItemDefuser inherits the unique actual tooling/degradation path. Entity interactions never wear the tool. */
public final class DefuserItem extends ToolingItem {
    public DefuserItem(){super(ToolType.DEFUSER,100);}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.defuser.name");}
    @Override public InteractionResult interactLivingEntity(ItemStack stack,Player player,LivingEntity target,InteractionHand hand){
        if(target instanceof Creeper creeper)return PedestalDefuser.defuse(creeper,player,true)?InteractionResult.SUCCESS:InteractionResult.PASS;
        if(target instanceof NuclearGlyphidDefusable glyphid){if(!player.level().isClientSide&&glyphid.hbm$deathTicks()>0)DefuserActions.nuclearGlyphid(target);return InteractionResult.SUCCESS;}
        return InteractionResult.PASS;
    }
}
