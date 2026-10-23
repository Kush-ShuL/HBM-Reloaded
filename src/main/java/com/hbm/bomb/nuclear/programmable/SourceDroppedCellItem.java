package com.hbm.bomb.nuclear.programmable;
import com.hbm.bomb.nuclear.*;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
/** ItemDrop's actual antimatter/anti-Schrab cells: source on-ground destruction and configured true blast paths. */
public final class SourceDroppedCellItem extends Item {
 private final boolean schrab;
 public SourceDroppedCellItem(boolean schrab){super(new Properties().craftRemainder(HbmContent.item("cell_empty")));this.schrab=schrab;}
 @Override public boolean onEntityItemUpdate(ItemStack stack,ItemEntity entity){if(!entity.onGround())return false;if(!entity.level().isClientSide&&NuclearConfig.DROP_CELL.get()){if(!schrab)new ExplosionVNT(entity.level(),entity.getX(),entity.getY(),entity.getZ(),3F).makeAmat().explode();else{int radius=NuclearConfig.radius("aSchrab");var ex=NuclearExplosionMK3.statFacFleija(entity.level(),entity.getX(),entity.getY(),entity.getZ(),radius);if(!ex.isRemoved()){entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),SoundEvents.GENERIC_EXPLODE,SoundSource.BLOCKS,100,entity.level().random.nextFloat()*.1F+.9F);entity.level().addFreshEntity(ex);var cloud=new NuclearFleijaCloud(NuclearContent.FLEIJA_CLOUD.get(),entity.level(),radius);cloud.setPos(entity.position());entity.level().addFreshEntity(cloud);}}}entity.discard();return true;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){String key=schrab?"item.hbm.cell_anti_schrabidium.drop":"item.hbm.cell_antimatter.drop";lines.add(Component.translatable(key+".1"));lines.add(Component.translatable(key+".2"));lines.add(Component.literal("[").append(Component.translatable("trait.drop")).append("]").withStyle(ChatFormatting.RED));}
}
