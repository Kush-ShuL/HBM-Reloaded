package com.hbm.machine.reactor;
import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Original ItemDepletedFuel metadata0/1 and indestructible, persistent EntityItemWaste. */
public final class WastePlateItem extends Item {
    public WastePlateItem(){super(new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
    @Override public boolean hasCustomEntity(ItemStack stack){return true;}
    @Override public int getEntityLifespan(ItemStack stack,Level level){return Integer.MAX_VALUE;}
    @Override public Entity createEntity(Level level,Entity original,ItemStack stack){
        var waste=new WastePlateEntity(level,original.getX(),original.getY(),original.getZ(),stack);
        waste.setDeltaMovement(original.getDeltaMovement());waste.setPickUpDelay(10);original.discard();return waste;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)>0)lines.add(Component.translatable("desc.item.wasteCooling").withStyle(ChatFormatting.GOLD));}
}
