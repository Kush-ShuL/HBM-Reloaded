package com.hbm.client.reactor.rbmk.panels;
import com.hbm.machine.reactor.rbmk.panels.*;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
/** Per-actual-panel screen factories; shared block never loads a client class on a dedicated server. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkRadioPanelClient{
    private static final Map<BlockEntityType<?>,Function<RbmkRadioPanelEntity,Screen>> SCREENS=new IdentityHashMap<>();
    public static <T extends RbmkRadioPanelEntity>void register(BlockEntityType<T> type,Function<T,Screen> factory){SCREENS.put(type,panel->factory.apply((T)panel));RbmkRadioPanelBlock.OPEN=pos->{var game=Minecraft.getInstance();if(game.level!=null&&game.level.getBlockEntity(pos) instanceof RbmkRadioPanelEntity panel){var screen=SCREENS.get(panel.getType());if(screen!=null)game.setScreen(screen.apply(panel));}};}
    @SubscribeEvent public static void geometry(ModelEvent.RegisterGeometryLoaders event){event.register(ResourceLocation.parse("hbm:rbmk_panel_inventory"),RbmkPanelInventoryGeometry.LOADER);}
    private RbmkRadioPanelClient(){}
}
