package com.hbm.item.rtg;

import com.hbm.hazard.HazardItemState;
import com.hbm.machine.rtg.RtgConfig;
import com.hbm.machine.rtg.RtgContent;
import java.text.NumberFormat;
import java.util.List;
import java.util.OptionalDouble;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/** X5846's real single-stack RTG pellets, sharing the hazard system's original NBT field. */
public final class RtgPelletItem extends Item implements HazardItemState {
    private final String name;
    private final int normalHeat,decayingHeat,decayMaterial;
    private final long maximumLife;
    public RtgPelletItem(String name,int normalHeat,int decayingHeat,int decayMaterial,float halfLife,int ticksPerUnit){
        super(new Properties().stacksTo(1));this.name=name;this.normalHeat=normalHeat;this.decayingHeat=decayingHeat;this.decayMaterial=decayMaterial;
        // Source first rounds to IEEE float, then truncates to long, then multiplies by double 1.5.
        float life=ticksPerUnit*halfLife;maximumLife=(long)((long)life*1.5D);
    }
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+name+".name");}
    public int heat(){return (short)(RtgConfig.decays()?decayingHeat:normalHeat);}
    public long maximumLife(){return maximumLife;}
    public ItemStack decayItem(){return RtgContent.DEPLETED.get().stack(decayMaterial);}
    public long life(ItemStack stack){
        var existing=stack.get(DataComponents.CUSTOM_DATA);
        if(existing!=null)return existing.copyTag().getLong("PELLET_DEPLETION");
        var tag=new CompoundTag();tag.putLong("PELLET_DEPLETION",maximumLife);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return maximumLife;
    }
    public short scaledHeat(ItemStack stack){return (short)Math.ceil(heat()*((double)life(stack)/maximumLife));}
    public short power(ItemStack stack){return RtgConfig.scales()?scaledHeat(stack):(short)heat();}
    public ItemStack handleDecay(ItemStack stack){
        if(!RtgConfig.decays())return stack;
        if(life(stack)<=0)return decayItem();
        var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();tag.putLong("PELLET_DEPLETION",life(stack)-1);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return stack;
    }
    @Override public OptionalDouble hazardDepletion(ItemStack stack){return OptionalDouble.of(1D-(double)life(stack)/maximumLife);}
    @Override public boolean isBarVisible(ItemStack stack){return life(stack)!=maximumLife;}
    @Override public int getBarWidth(ItemStack stack){return Math.round((float)(13D*life(stack)/maximumLife));}
    @Override public int getBarColor(ItemStack stack){float f=(float)life(stack)/maximumLife;return net.minecraft.util.Mth.hsvToRgb(f/3F,1,1);}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        if(!level.isClientSide&&name.equals("pellet_rtg")){
            player.sendSystemMessage(Component.translatable("info.hbm.rtg.fact."+level.random.nextInt(16)).withStyle(ChatFormatting.YELLOW));
            level.playSound(null,player,SoundEvents.EXPERIENCE_ORB_PICKUP,SoundSource.PLAYERS,1,1);
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){
        tooltip.add(Component.translatable("item."+name+".desc"));tooltip.add(Component.translatable("desc.item.rtgHeat",RtgConfig.scales()?scaledHeat(stack):heat()));
        tooltip.add(Component.translatable("desc.item.rtgDecay",decayItem().getHoverName(),decayItem().getCount()));
        // BobMathUtil accepts floats here, even for long lifespans.
        tooltip.add(Component.literal(NumberFormat.getPercentInstance().format((float)life(stack)/(float)maximumLife)));
        if(flag.isAdvanced()){
            tooltip.add(Component.translatable("tooltip.hbm.rtg.extended"));tooltip.add(Component.translatable("tooltip.hbm.rtg.ticks",life(stack),maximumLife));
            var remaining=date(life(stack));var maximum=date(maximumLife);
            tooltip.add(Component.translatable("tooltip.hbm.rtg.remaining",(Object[])remaining));tooltip.add(Component.translatable("tooltip.hbm.rtg.maximum",(Object[])maximum));
        }
    }
    private static String[] date(long ticks){long year=Math.floorDiv(ticks,4800000);byte day=(byte)Math.floorDiv(ticks-4800000*year,48000);float time=ticks-(4800000*year+48000*day);time=time/48000F*10F;return new String[]{Long.toString(year),Byte.toString(day),Float.toString(time)};}
}
