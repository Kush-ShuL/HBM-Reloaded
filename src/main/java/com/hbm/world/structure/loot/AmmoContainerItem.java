package com.hbm.world.structure.loot;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Real two-variant ItemAmmoContainer using the migrated ItemGunBaseNT's actual default ammunition. */
public final class AmmoContainerItem extends Item {
    public AmmoContainerItem(){super(new Properties().stacksTo(1).component(HbmHazards.LEGACY_METADATA.get(),0));}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.ammo_container.name");}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);boolean makeshift=stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)==1;var guns=new ArrayList<ItemStack>();for(var held:player.getInventory().items)if(held.getItem() instanceof ItemGunBaseNT gun&&gun.defaultAmmo!=null&&!(makeshift&&gun.isDefaultExpensive))guns.add(held);if(guns.isEmpty())return InteractionResultHolder.pass(stack);if(!level.isClientSide){Collections.shuffle(guns);for(int i=0;i<3&&i<guns.size();i++){var gun=(ItemGunBaseNT)guns.get(i).getItem();var ammo=gun.defaultAmmo.copy();if(makeshift)ammo.setCount((int)Math.ceil(ammo.getCount()/2D));insertMain(player,ammo);if(!ammo.isEmpty())player.drop(ammo,false);}level.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("item.unpack"),SoundSource.PLAYERS,1,1);player.inventoryMenu.broadcastChanges();stack.shrink(1);}return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
    /** Source InventoryUtil merges all36 main slots first, then puts the remainder in the first empty slot. */
    private static void insertMain(Player player,ItemStack incoming){for(var held:player.getInventory().items)if(!held.isEmpty()&&ItemStack.isSameItemSameComponents(held,incoming)){int moved=Math.min(incoming.getCount(),held.getMaxStackSize()-held.getCount());if(moved>0){held.grow(moved);incoming.shrink(moved);if(incoming.isEmpty())return;}}for(int i=0;i<player.getInventory().items.size();i++)if(player.getInventory().items.get(i).isEmpty()){player.getInventory().items.set(i,incoming.copy());incoming.setCount(0);return;}}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){int metadata=stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);for(String line:net.minecraft.locale.Language.getInstance().getOrDefault("item.ammo_container"+(metadata==1?".1":"")+".desc").split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));}
}
