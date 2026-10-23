package com.hbm.client.pylon;

import com.hbm.block.pylon.ConnectorBlock;
import com.hbm.block.pylon.PylonBlock;
import com.hbm.block.pylon.PylonKind;
import com.hbm.block.pylon.compat.PylonJadePlugin;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.config.IPluginConfig;

public final class PylonJadeClient {
    private PylonJadeClient() {}
    public static void register(IWailaClientRegistration registration) { registration.registerBlockComponent(Data.INSTANCE,PylonBlock.class);registration.registerBlockComponent(Data.INSTANCE,ConnectorBlock.class); }
    private enum Data implements IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid() { return PylonJadePlugin.UID; }
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config) {
            var block=accessor.getBlockState().getBlock();PylonKind kind=block instanceof PylonBlock pylon?pylon.kind:((ConnectorBlock)block).kind;
            tooltip.add(Component.translatable("tooltip.hbm.network.connection_type",Component.translatable("tooltip.hbm.network."+(kind.wires==1?"single":kind.wires==3?"triple":"quadruple")).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.hbm.network.range",Component.translatable("tooltip.hbm.network.metres",(int)kind.range).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GOLD));
            if(accessor.getServerData().contains(PylonJadePlugin.DATA)) tooltip.add(Component.translatable("jade.hbm.network.connections",accessor.getServerData().getInt(PylonJadePlugin.DATA)));
        }
    }
}
