package com.hbm.weapon.crucible;
import com.hbm.entity.mob.botprime.mixin.MaxHealthAttributeAccess;
import java.util.List;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class CrucibleContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<CrucibleItem> CRUCIBLE=ITEMS.register("crucible",CrucibleItem::new);
 public static void register(IEventBus bus){
  // Actual1.7.10 yj.<clinit>: attackDamage=new RangedAttribute(...,2D,0D,Double.MAX_VALUE).
  // Existing native accessor also serves this actual source upper-bound restoration;defaults and entity damage path are unchanged.
  var damage=(MaxHealthAttributeAccess)Attributes.ATTACK_DAMAGE.value();damage.hbm$setMaximum(Math.max(damage.hbm$getMaximum(),Double.MAX_VALUE));
  ITEMS.register(bus);bus.addListener(CrucibleAnimationPacket::register);
 }
 public static ItemStack empty(){var stack=new ItemStack(CRUCIBLE.get());stack.setDamageValue(3);return stack;}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?List.of(new ItemStack(CRUCIBLE.get())):List.of();}
 public static List<ItemStack> viewerStacks(){return List.of(new ItemStack(CRUCIBLE.get()),empty());}
 private CrucibleContent(){}
}
