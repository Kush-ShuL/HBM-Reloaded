package com.hbm.machine.reactor.zirnox;
import com.hbm.machine.reactor.PlateFuelItem;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.*;
/** All eleven source rod enum values, thermal yield, depletion and breeding-only semantics. */
public final class ZirnoxRod extends Item {
    public enum Type {
        NATURAL_URANIUM_FUEL(250000,30,false),URANIUM_FUEL(200000,50,false),TH232(20000,0,true),THORIUM_FUEL(200000,40,false),MOX_FUEL(165000,75,false),PLUTONIUM_FUEL(175000,65,false),U233_FUEL(150000,100,false),U235_FUEL(165000,85,false),LES_FUEL(150000,150,false),LITHIUM(20000,0,true),ZFB_MOX(50000,35,false);
        public final int maxLife,heat;public final boolean breeding;
        Type(int life,int heat,boolean breeding){this.maxLife=life;this.heat=heat;this.breeding=breeding;}
    }
    private final Type type;
    public ZirnoxRod(Type type){super(new Item.Properties().stacksTo(1));this.type=Objects.requireNonNull(type);}
    public Type type(){return type;}
    public static int life(ItemStack stack){return PlateFuelItem.life(stack);}
    public static void increment(ItemStack stack){PlateFuelItem.life(stack,life(stack)+1);}
    @Override public boolean isBarVisible(ItemStack stack){return life(stack)>0;}
    @Override public int getBarWidth(ItemStack stack){return Math.round(13F-life(stack)*13F/type().maxLife);}
    @Override public int getBarColor(ItemStack stack){return Mth.hsvToRgb(Math.max(0,1F-(float)life(stack)/type().maxLife)/3F,1,1);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){var type=type();lines.add(Component.translatable("trait.rbmk.depletion",(int)((double)life(stack)/type.maxLife*100000)/1000D+"%").withStyle(ChatFormatting.YELLOW));var description=type.breeding?Component.translatable("desc.item.zirnoxBreedingRod",shortNumber(type.maxLife)):Component.translatable("desc.item.zirnoxRod",type.heat,shortNumber(type.maxLife));for(var line:description.getString().split("\\$"))lines.add(Component.literal(line));}
    private static String shortNumber(int value){return value>=1000000?value/1000000D+"M":value>=1000?value/1000D+"k":Integer.toString(value);}
}
