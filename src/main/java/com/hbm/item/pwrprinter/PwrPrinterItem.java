package com.hbm.item.pwrprinter;

import com.hbm.machine.reactor.pwr.PwrControllerBlockEntity;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;

/** Exact ItemPWRPrinter use-on-controller; PWR controller already passes the genuine item through. */
public final class PwrPrinterItem extends Item {
    public PwrPrinterItem(){super(new Properties());}
    @Override public InteractionResult useOn(UseOnContext context){if(!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof PwrControllerBlockEntity controller))return InteractionResult.PASS;if(context.getLevel() instanceof ServerLevel level)PwrPrinterPackets.send(level,controller);return InteractionResult.sidedSuccess(context.getLevel().isClientSide);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){tooltip.add(Component.translatable("tooltip.hbm.pwr_printer.use"));}
}
