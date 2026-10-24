package com.hbm.item.circuitpiece;
import java.util.List;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
/** Source tab=null: no creative-tab aggregation and no inferred viewer exclusion. */
public final class FlatCircuitPieceContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> NAMES=List.of("board_blank","board_transistor","board_converter","bridge_north","bridge_south","bridge_io","bridge_bus","bridge_chipset","bridge_cmos","bridge_bios","cpu_register","cpu_clock","cpu_logic","cpu_cache","cpu_ext","cpu_socket","mem_socket","mem_16k_a","mem_16k_b","mem_16k_c","mem_16k_d","card_board","card_processor");
    public static final List<DeferredItem<FlatCircuitPieceItem>> FRAGMENTS=NAMES.stream().map(name->ITEMS.register("circuit_star_piece_"+name,FlatCircuitPieceItem::new)).toList();
    public static void register(IEventBus bus){ITEMS.register(bus);}
    private FlatCircuitPieceContent(){}
}
