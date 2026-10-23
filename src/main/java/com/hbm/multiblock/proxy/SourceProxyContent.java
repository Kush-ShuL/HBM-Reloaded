package com.hbm.multiblock.proxy;
import com.hbm.machine.reactor.ReactorContent;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.*;
/** Only the proved missing real source factory; no new block/item identity or permissive fallback registry. */
public final class SourceProxyContent {
 private static final DeferredRegister<BlockEntityType<?>> TYPES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SourceStandaloneProxy>> COMBO=TYPES.register("source_proxy_combo",()->BlockEntityType.Builder.of(SourceStandaloneProxy::new,ReactorContent.RESEARCH.get()).build(null));
 private static boolean registered;
 public static void register(IEventBus bus){if(registered)return;registered=true;TYPES.register(bus);bus.addListener(SourceProxyContent::capabilities);}
 private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,COMBO.get(),(proxy,side)->new SidedInvWrapper(proxy,side==null?Direction.UP:side));}
 private SourceProxyContent(){}
}
