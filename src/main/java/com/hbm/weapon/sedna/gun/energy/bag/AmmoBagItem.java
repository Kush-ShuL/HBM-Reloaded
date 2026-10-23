package com.hbm.weapon.sedna.gun.energy.bag;
import com.hbm.weapon.sedna.gun.AmmoInventoryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
public final class AmmoBagItem extends Item implements AmmoInventoryProvider {
    private final boolean infinite;
    public AmmoBagItem(boolean infinite){super(new Properties().stacksTo(1));this.infinite=infinite;}
    @Override public Component getName(ItemStack stack){return Component.translatable(infinite?"item.ammo_bag_infinite.name":"item.ammo_bag.name");}
    @Override public Container ammoInventory(ItemStack bag,HolderLookup.Provider lookup){return new AmmoBagInventory(bag,lookup);}
    @Override public boolean infiniteAmmo(ItemStack stack){return infinite;}
    @Override public int getUseDuration(ItemStack stack,net.minecraft.world.entity.LivingEntity holder){return 1;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(player instanceof ServerPlayer server)server.openMenu(new SimpleMenuProvider((id,inventory,viewer)->new AmmoBagMenu(id,inventory,hand),stack.has(DataComponents.CUSTOM_NAME)?stack.getHoverName():Component.translatable("container.ammoBag")),buffer->buffer.writeEnum(hand));return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
    private double fullness(ItemStack stack){var data=stack.get(DataComponents.CUSTOM_DATA);if(data==null)return 0;var lookup=net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);var inventory=new AmmoBagInventory(stack,lookup);int capacity=0,bullets=0;for(int i=0;i<8;i++){var item=inventory.getItem(i);capacity+=item.isEmpty()?64:item.getMaxStackSize();bullets+=item.getCount();}return (double)bullets/capacity;}
    @Override public boolean isBarVisible(ItemStack stack){return !infinite&&fullness(stack)<1;}
    @Override public int getBarWidth(ItemStack stack){return (int)Math.round(13*fullness(stack));}
    @Override public int getBarColor(ItemStack stack){return Mth.hsvToRgb((float)fullness(stack)/3F,1,1);}
}
