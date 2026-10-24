package com.hbm.material.crystallizer;
import com.hbm.item.FlatSourceItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import java.util.*;
/** Actual ItemPlasticScrap: one of23 physical ScrapType identities, shared icon/name and hidden creative tab. */
public final class PlasticScrapItem extends Item implements FlatSourceItem {
 public static final List<String> NAMES=List.of("board_blank","board_transistor","board_converter","bridge_north","bridge_south","bridge_io","bridge_bus","bridge_chipset","bridge_cmos","bridge_bios","cpu_register","cpu_clock","cpu_logic","cpu_cache","cpu_ext","cpu_socket","mem_socket","mem_16k_a","mem_16k_b","mem_16k_c","mem_16k_d","card_board","card_processor");
 public PlasticScrapItem(){super(new Properties().stacksTo(64));}
 @Override public String getDescriptionId(){return "item.hbm.scrap_plastic";}
 @Override public Component getName(ItemStack stack){return Component.translatable("item.scrap_plastic.name");}
}
