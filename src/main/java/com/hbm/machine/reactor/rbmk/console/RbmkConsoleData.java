package com.hbm.machine.reactor.rbmk.console;
import com.hbm.fluid.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
/** Exact original column order/icon offsets and six source status-screen types. */
public final class RbmkConsoleData {
    public enum ColumnType {BLANK(0),FUEL(10),FUEL_SIM(90),CONTROL(20),CONTROL_AUTO(30),BOILER(40),MODERATOR(50),ABSORBER(60),REFLECTOR(70),OUTGASSER(80),BREEDER(100),STORAGE(110),COOLER(120),HEATEX(130);public final int offset;ColumnType(int offset){this.offset=offset;}}
    public enum ScreenType {NONE,COL_TEMP,ROD_EXTRACTION,FUEL_DEPLETION,FUEL_POISON,FUEL_TEMP;public int offset(){return ordinal()*18;}}
    public static final class Status {public ScreenType type=ScreenType.NONE;public int[] columns=new int[0];public String display;}
    public record Column(ColumnType type,CompoundTag data){
        public List<Component> fancy(){var lines=new ArrayList<Component>();lines.add(Component.translatable("rbmk.heat",((int)(data.getDouble("heat")*10))/10D+"°C").withStyle(ChatFormatting.YELLOW));switch(type){
            case FUEL,FUEL_SIM->{lines.add(Component.translatable("rbmk.rod.depletion",((int)((1-data.getDouble("enrichment"))*100000))/1000D+"%").withStyle(ChatFormatting.GREEN));lines.add(Component.translatable("rbmk.rod.xenon",((int)(data.getDouble("xenon")*1000))/1000D+"%").withStyle(ChatFormatting.DARK_PURPLE));lines.add(Component.translatable("rbmk.rod.coreTemp",((int)(data.getDouble("c_coreHeat")*10))/10D+"°C").withStyle(ChatFormatting.DARK_RED));lines.add(Component.translatable("rbmk.rod.skinTemp",((int)(data.getDouble("c_heat")*10))/10D+"°C",((int)(data.getDouble("c_maxHeat")*10))/10D+"°C").withStyle(ChatFormatting.RED));}
            case BOILER->{lines.add(Component.translatable("rbmk.boiler.water",data.getInt("water"),data.getInt("maxWater")).withStyle(ChatFormatting.BLUE));lines.add(Component.translatable("rbmk.boiler.steam",data.getInt("steam"),data.getInt("maxSteam")).withStyle(ChatFormatting.WHITE));lines.add(Component.translatable("rbmk.boiler.type",HbmFluids.fromID(data.getShort("type")).displayName()).withStyle(ChatFormatting.YELLOW));}
            case CONTROL,CONTROL_AUTO->{if(type==ColumnType.CONTROL&&data.contains("color")){int color=data.getShort("color");if(color>=0&&color<5)lines.add(Component.translatable("rbmk.control."+new String[]{"red","yellow","green","blue","purple"}[color]).withStyle(ChatFormatting.YELLOW));}lines.add(Component.translatable("rbmk.control.level",(int)(data.getDouble("level")*100)+"%").withStyle(ChatFormatting.YELLOW));}
            case HEATEX->{lines.add(HbmFluids.fromID(data.getShort("type")).displayName().copy().append(" "+data.getInt("water")+"/"+data.getInt("maxWater")+"mB").withStyle(ChatFormatting.BLUE));lines.add(HbmFluids.fromID(data.getShort("hottype")).displayName().copy().append(" "+data.getInt("steam")+"/"+data.getInt("maxSteam")+"mB").withStyle(ChatFormatting.RED));}default->{} }
            if(data.getBoolean("moderated"))lines.add(Component.translatable("rbmk.moderated").withStyle(ChatFormatting.YELLOW));return lines;
        }
    }
    private RbmkConsoleData(){}
}
