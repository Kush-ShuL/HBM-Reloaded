package com.hbm.bomb.conventional.grenade;

import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import com.hbm.registry.HbmSounds;

/** Original ItemGrenadeUniversal: source component ordinals, equip delay, stack limits and assembly tags. */
public final class UniversalGrenadeItem extends Item {
    public UniversalGrenadeItem(){super(new Properties().stacksTo(4));}
    private static CompoundTag tag(ItemStack stack){var data=stack.get(DataComponents.CUSTOM_DATA);return data==null?null:data.copyTag();}
    public static GrenadeParts.Shell shell(ItemStack stack){var tag=tag(stack);return tag==null?GrenadeParts.Shell.FRAG:GrenadeParts.ordinal(GrenadeParts.Shell.values(),tag.getInt("shell"));}
    public static GrenadeParts.Filling filling(ItemStack stack){var tag=tag(stack);return tag==null?GrenadeParts.Filling.HE:GrenadeParts.ordinal(GrenadeParts.Filling.values(),tag.getInt("filling"));}
    public static GrenadeParts.Fuze fuze(ItemStack stack){var tag=tag(stack);return tag==null?GrenadeParts.Fuze.S3:GrenadeParts.ordinal(GrenadeParts.Fuze.values(),tag.getInt("fuze"));}
    public static GrenadeParts.Extra extra(ItemStack stack){var tag=tag(stack);return tag==null||!tag.contains("extra")?null:GrenadeParts.ordinal(GrenadeParts.Extra.values(),tag.getInt("extra"));}
    public static ItemStack make(GrenadeParts.Shell shell,GrenadeParts.Filling filling,GrenadeParts.Fuze fuze,GrenadeParts.Extra extra,int count){var stack=new ItemStack(GrenadeContent.UNIVERSAL.get(),count);var tag=new CompoundTag();tag.putInt("shell",shell.ordinal());tag.putInt("filling",filling.ordinal());tag.putInt("fuze",fuze.ordinal());if(extra!=null)tag.putInt("extra",extra.ordinal());stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return stack;}
    @Override public int getMaxStackSize(ItemStack stack){return shell(stack).stackLimit;}
    public void equip(Player player,ItemStack stack){GrenadeDeployment.data(player).ticks=0;GrenadeEquipEffects.send(player);}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(GrenadeDeployment.data(player).ticks>=shell(stack).drawDuration){if(!level.isClientSide)level.addFreshEntity(new UniversalGrenade(level,player,stack));if(!player.getAbilities().instabuild)stack.shrink(1);if(!stack.isEmpty())equip(player,stack);}return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean held){if(!(entity instanceof LivingEntity))return;var tag=tag(stack);boolean wasHeld=tag!=null&&tag.getBoolean("eqipped");if(entity instanceof Player player){if(!wasHeld&&held)equip(player,stack);else if(held){int time=++GrenadeDeployment.data(player).ticks;var shell=shell(stack);String sound=null;float pitch=1;if(shell==GrenadeParts.Shell.FRAG&&time==18)sound="weapon.reload.revolverCock";if(shell==GrenadeParts.Shell.STICK&&(time==16||time==25)){sound="weapon.reload.boltOpen";pitch=1.25F;}if(shell==GrenadeParts.Shell.TECH&&time==18)sound="weapon.reload.grenadeTech";if(shell==GrenadeParts.Shell.NUKE&&time==26)sound="weapon.reload.grenadeNuka";if(sound!=null)level.playSound(player,player.getX(),player.getY(),player.getZ(),HbmSounds.get(sound),SoundSource.PLAYERS,1,pitch);}}
        if(tag==null)tag=new CompoundTag();tag.putBoolean("eqipped",held);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,java.util.List<Component> text,TooltipFlag flag){text.add(name("shell",shell(stack)).withStyle(ChatFormatting.YELLOW));text.add(name("filling",filling(stack)).withStyle(ChatFormatting.YELLOW));text.add(name("fuze",fuze(stack)).withStyle(ChatFormatting.YELLOW));if(extra(stack)!=null)text.add(name("extra",extra(stack)).withStyle(ChatFormatting.RED));}
    private static net.minecraft.network.chat.MutableComponent name(String part,Enum<?> value){return Component.translatable("item.grenade_"+part+"."+value.name().toLowerCase(Locale.ROOT)+".name");}
}
