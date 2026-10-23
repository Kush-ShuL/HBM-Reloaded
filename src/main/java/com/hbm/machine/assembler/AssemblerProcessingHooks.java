package com.hbm.machine.assembler;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import net.minecraft.world.item.ItemStack;
public final class AssemblerProcessingHooks {
    private static final CopyOnWriteArrayList<BiConsumer<AssemblerBlockEntity,ItemStack[]>> HOOKS=new CopyOnWriteArrayList<>();
    private AssemblerProcessingHooks(){}public static void register(BiConsumer<AssemblerBlockEntity,ItemStack[]> hook){HOOKS.add(hook);}static void processed(AssemblerBlockEntity machine,ItemStack[] inventory){HOOKS.forEach(hook->hook.accept(machine,inventory));}
}
