package com.hbm.item.singularity;
import com.hbm.bomb.nuclear.NuclearConfig;
import com.hbm.entity.effect.EntityBlackHole;
import com.hbm.entity.effect.EntityVortex;
import com.hbm.explosion.ExplosionChaos;
import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
/** Exact admitted ItemDrop branches. The source consumes grounded stacks even with its effect disabled. */
public final class SourceSingularityItem extends Item {
 public enum Kind{SINGULARITY,BLACK_HOLE,XEN,BETA}
 public final Kind kind;
 public SourceSingularityItem(Kind kind){super(properties(kind));this.kind=kind;}
 private static Properties properties(Kind kind){var properties=new Properties().stacksTo(1);return (kind==Kind.XEN||kind==Kind.BETA)?properties:properties.craftRemainder(HbmContent.item("nuclear_waste"));}
 @Override public boolean onEntityItemUpdate(ItemStack stack,ItemEntity entity){if(kind==Kind.BETA){entity.discard();return true;}if(!entity.onGround())return false;if(!entity.level().isClientSide){if(kind==Kind.XEN){if(NuclearConfig.DROP_CRYSTAL.get()){ExplosionChaos.floater(entity.level(),(int)entity.getX(),(int)entity.getY(),(int)entity.getZ(),25,75);ExplosionChaos.move(entity.level(),(int)entity.getX(),(int)entity.getY(),(int)entity.getZ(),25,0,75,0);}}else if(NuclearConfig.DROP_SINGULARITY.get()){if(kind==Kind.BLACK_HOLE){var hole=new EntityBlackHole(entity.level(),1.5F);hole.setPos(entity.position());entity.level().addFreshEntity(hole);}else{var vortex=new EntityVortex(entity.level(),1.5F);vortex.setPos(entity.position());entity.level().addFreshEntity(vortex);}}}entity.discard();return true;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(kind!=Kind.XEN&&kind!=Kind.BETA){String key="tooltip.hbm."+(kind==Kind.BLACK_HOLE?"black_hole":"singularity")+".drop.";for(int i=0;i<5;i++)lines.add(Component.translatable(key+i));}lines.add(Component.literal("[").append(Component.translatable("trait.drop")).append("]").withStyle(ChatFormatting.RED));}
}
