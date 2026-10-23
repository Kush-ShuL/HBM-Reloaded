package com.hbm.client.network.separator;
import com.hbm.energy.ConnectionPriority;
import com.hbm.item.battery.BatteryItem;
import com.hbm.network.separator.NetworkSeparatorEntity;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
/** Same original short-number formatting/arrow colours, reused by source HUD and optional Jade. */
final class NetworkSeparatorReadout {
 static net.minecraft.network.chat.MutableComponent priority(ConnectionPriority priority){return Component.translatable("gui.hbm.network_separator.priority."+priority.name().toLowerCase(Locale.ROOT));}
 private static String number(String text){var n=new BigInteger(text);return n.bitLength()<64?BatteryItem.shortNumber(n.longValueExact()):n.toString();}
 static CompoundTag data(NetworkSeparatorEntity tile){var tag=new CompoundTag();tag.putString("tick",tile.deltaTick[19].toString());tag.putString("second",tile.deltaSecond.toString());tag.putString("limit",tile.configuredLimit.toString());tag.putByte("priority",(byte)tile.priority.ordinal());tag.putBoolean("disabled",tile.redstone);return tag;}
 private static Component flow(String value,String unit){return Component.literal("-> ").withStyle(ChatFormatting.GREEN).append(Component.literal(number(value)+unit).withStyle(ChatFormatting.WHITE)).append(Component.literal(" ->").withStyle(ChatFormatting.RED));}
 static List<Component> lines(CompoundTag data){var lines=new ArrayList<Component>();lines.add(flow(data.getString("tick"),"HE/t"));lines.add(flow(data.getString("second"),"HE/s"));lines.add(Component.translatable("gui.hbm.network_separator.configured",number(data.getString("limit"))));var priorities=ConnectionPriority.values();var priority=priorities[Math.abs(data.getByte("priority")%priorities.length)];lines.add(Component.translatable("gui.hbm.network_separator.priority_value",priority(priority).withStyle(ChatFormatting.YELLOW)));if(data.getBoolean("disabled"))lines.add(Component.translatable("gui.hbm.network_separator.disabled").withStyle(ChatFormatting.RED));return lines;}
 private NetworkSeparatorReadout(){}
}
