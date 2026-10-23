package com.hbm.weapon.b92;
import com.hbm.hazard.HbmHazards;import java.util.List;import net.minecraft.network.chat.Component;import net.minecraft.world.entity.Entity;import net.minecraft.world.entity.player.Player;import net.minecraft.world.item.*;import net.minecraft.world.level.Level;
/** Original permanent25charge bomb cell: drains only main inventory B92s with at least2charges, never refills the gun. */
public final class B92Cell extends Item {
 public B92Cell(){super(new Properties().stacksTo(1).component(HbmHazards.LEGACY_METADATA.get(),0));}
 @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected){if(!(entity instanceof Player player)||B92Gun.energy(stack)>=25)return;for(var gun:player.getInventory().items)if(gun.is(B92Content.GUN.get())&&B92Gun.energy(gun)>1){B92Gun.setEnergy(gun,B92Gun.energy(gun)-1);B92Gun.setEnergy(stack,B92Gun.energy(stack)+1);if(B92Gun.energy(stack)==25)stack.set(HbmHazards.LEGACY_METADATA.get(),1);return;}}
 public static ItemStack full(){var stack=new ItemStack(B92Content.CELL.get());stack.set(HbmHazards.LEGACY_METADATA.get(),1);B92Gun.setEnergy(stack,25);return stack.copy();}
 @Override public Component getName(ItemStack stack){return Component.translatable("item.gun_b92_ammo.name");}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){for(int i=1;i<=5;i++)lines.add(Component.translatable("tooltip.hbm.b92.cell."+i));lines.add(Component.empty());lines.add(Component.translatable("tooltip.hbm.b92.cell.charges",B92Gun.energy(stack)));}
}
