package com.hbm.armor.rpa;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.factory.GunStateDecider;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import java.util.List;
/** Actual XFactoryPA controller items; all input/state/network/animation remains the one SEDNA engine. */
public final class PowerArmorGuns {
 public static ItemGunBaseNT melee(){return new ItemGunPA(new GunConfig().draw(10).crosshair(Crosshair.NONE).rec(new Receiver(0)).pp((s,c)->{var m=PAWeaponsProvider.melee(c.getPlayer());if(m!=null)m.clickPrimary(s,c);}).ps((s,c)->{var m=PAWeaponsProvider.melee(c.getPlayer());if(m!=null)m.clickSecondary(s,c);}).decider(GunStateDecider.LAMBDA_STANDARD_DECIDER).anim((s,a)->{var player=ItemGunBaseNT.CLIENT==null?null:ItemGunBaseNT.CLIENT.localPlayer();var m=PAWeaponsProvider.melee(player);return m==null?null:m.playAnim(s,a);}).orchestra((s,c)->{var m=PAWeaponsProvider.melee(c.getPlayer());if(m!=null)m.orchestra(s,c);}));}
 public static ItemGunBaseNT ranged(){return new ItemGunPA(new GunConfig().draw(0).crosshair(Crosshair.CROSS).rec(new Receiver(0)).pp((s,c)->{var m=PAWeaponsProvider.ranged(c.getPlayer());if(m!=null)m.clickPrimary(s,c);}).ps((s,c)->{var m=PAWeaponsProvider.ranged(c.getPlayer());if(m!=null)m.clickSecondary(s,c);}).decider(GunStateDecider.LAMBDA_STANDARD_DECIDER));}
 public static void swing(ItemStack stack,LambdaContext ctx,GunAnimation animation,int cooldown){if(ItemGunBaseNT.getState(stack,ctx.configIndex)==GunState.IDLE){ItemGunBaseNT.playAnimation(ctx.getPlayer(),stack,animation,ctx.configIndex);ItemGunBaseNT.setState(stack,ctx.configIndex,GunState.COOLDOWN);ItemGunBaseNT.setTimer(stack,ctx.configIndex,cooldown);}}
 public static final class ItemGunPA extends ItemGunBaseNT implements com.hbm.weapon.MainHandWeapon {
  public ItemGunPA(GunConfig config){super(WeaponQuality.UTILITY,config);pistolDualWield(false);}
  @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){} // Source suppresses generic ammo/durability tooltip; controller has no magazine.
 }
 private PowerArmorGuns(){}
}
