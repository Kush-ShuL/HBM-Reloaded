package com.hbm.item.singularity.refinement;
import com.hbm.bomb.nuclear.NuclearConfig;
import com.hbm.entity.effect.EntityRagingVortex;
import com.hbm.entity.effect.EntityVortex;
import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
/** Exact original on-ground consumption, heated vortex2.5 and spark raging vortex3.5. Counter has no source effect. */
public final class SingularityRefinementItem extends Item {
 public enum Kind{COUNTER,HEATED,SPARK}
 public final Kind kind;
 public SingularityRefinementItem(Kind kind){super(new Properties().stacksTo(1).craftRemainder(HbmContent.item("nuclear_waste")));this.kind=kind;}
 @Override public boolean onEntityItemUpdate(ItemStack stack,ItemEntity item){if(!item.onGround())return false;if(!item.level().isClientSide&&NuclearConfig.DROP_SINGULARITY.get()){if(kind==Kind.HEATED){var vortex=new EntityVortex(item.level(),2.5F);vortex.setPos(item.position());item.level().addFreshEntity(vortex);}else if(kind==Kind.SPARK){var vortex=new EntityRagingVortex(item.level(),3.5F);vortex.setPos(item.position());item.level().addFreshEntity(vortex);}}item.discard();return true;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){int count=kind==Kind.COUNTER?6:kind==Kind.HEATED?4:0;String key="tooltip.hbm.singularity_refinement."+kind.name().toLowerCase(java.util.Locale.ROOT)+".";for(int i=0;i<count;i++)lines.add(Component.translatable(key+i));lines.add(Component.literal("[").append(Component.translatable("trait.drop")).append("]").withStyle(ChatFormatting.RED));}
}
