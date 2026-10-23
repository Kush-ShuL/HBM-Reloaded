package com.hbm.item.survivalloot;

import com.hbm.armor.*;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Actual ItemStarterKit's three hazmat and stealth-boy branches, without registering unresolved other kits. */
public final class SurvivalStarterItem extends Item {
    public enum Kind{YELLOW,RED,GREY,STEALTH}
    private static final EquipmentSlot[] ORDER={EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD};
    private final Kind kind;
    public SurvivalStarterItem(Kind kind){super(new Properties().stacksTo(kind==Kind.STEALTH?64:1));this.kind=kind;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var held=player.getItemInHand(hand);
        if(kind==Kind.STEALTH)player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,600,1,true,true));
        else giveHazmat(level,player);
        level.playSound(player,player.getX(),player.getY(),player.getZ(),HbmSounds.get("item.unpack"),SoundSource.PLAYERS,1,1);
        // The original consumes the stack even in creative mode, without an item-use statistic.
        held.shrink(1);return InteractionResultHolder.sidedSuccess(held,level.isClientSide);
    }
    private void giveHazmat(Level level,Player player){
        for(var slot:ORDER){var equipped=player.getItemBySlot(slot);if(!equipped.isEmpty()&&!level.isClientSide)level.addFreshEntity(new ItemEntity(level,player.getX(),player.getY()+player.getEyeHeight(),player.getZ(),equipped));}
        Item[] replacement=switch(kind){case YELLOW->new Item[]{HazmatContent.BOOTS.get(),HazmatContent.LEGS.get(),HazmatContent.PLATE.get(),HazmatContent.HELMET.get()};case RED->new Item[]{HazmatVariants.RED_BOOTS.get(),HazmatVariants.RED_LEGS.get(),HazmatVariants.RED_PLATE.get(),HazmatVariants.RED_HELMET.get()};case GREY->new Item[]{HazmatVariants.GREY_BOOTS.get(),HazmatVariants.GREY_LEGS.get(),HazmatVariants.GREY_PLATE.get(),HazmatVariants.GREY_HELMET.get()};default->throw new IllegalStateException("Stealth item is not a hazmat kit");};
        // Source drops feet→head, then assigns head→feet in each tier branch.
        for(int i=ORDER.length-1;i>=0;i--)player.setItemSlot(ORDER[i],new ItemStack(replacement[i]));
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){if(kind==Kind.YELLOW)lines.add(Component.translatable("tooltip.hbm.starterkit.hazmat_displacement"));}
}
