package com.hbm.material.crystallizer;
import com.hbm.item.EnumMultiItem;
import java.util.*;
/** Actual ItemPlasticScrap: all23 ScrapType identities, shared icon/name and hidden creative tab. */
public final class PlasticScrapItem extends EnumMultiItem {
 public static final List<String> NAMES=List.of("board_blank","board_transistor","board_converter","bridge_north","bridge_south","bridge_io","bridge_bus","bridge_chipset","bridge_cmos","bridge_bios","cpu_register","cpu_clock","cpu_logic","cpu_cache","cpu_ext","cpu_socket","mem_socket","mem_16k_a","mem_16k_b","mem_16k_c","mem_16k_d","card_board","card_processor");
 public PlasticScrapItem(){super("scrap_plastic",64,false,NAMES,java.util.stream.IntStream.range(0,NAMES.size()).boxed().toList());}
}
