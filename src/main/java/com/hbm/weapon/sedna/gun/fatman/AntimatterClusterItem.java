package com.hbm.weapon.sedna.gun.fatman;
import com.hbm.bomb.nuclear.NuclearConfig;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
/** Exact active ItemDrop.pellet_antimatter branch; no generic antimatter block or new explosion system. */
public final class AntimatterClusterItem extends Item {
 public AntimatterClusterItem(){super(new Properties().craftRemainder(HbmContent.item("cell_empty")));}
 @Override public boolean onEntityItemUpdate(ItemStack stack,ItemEntity entity){if(!entity.onGround())return false;if(!entity.level().isClientSide&&NuclearConfig.DROP_CELL.get())new ExplosionVNT(entity.level(),entity.getX(),entity.getY(),entity.getZ(),20F).makeAmat().explode();entity.discard();return true;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){lines.add(Component.translatable("tooltip.hbm.pellet_antimatter.0"));lines.add(Component.translatable("tooltip.hbm.pellet_antimatter.1"));lines.add(Component.literal("[").append(Component.translatable("trait.drop")).append("]").withStyle(ChatFormatting.RED));}
}
