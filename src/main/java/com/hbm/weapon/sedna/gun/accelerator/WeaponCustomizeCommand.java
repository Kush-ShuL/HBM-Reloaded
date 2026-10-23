package com.hbm.weapon.sedna.gun.accelerator;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.*;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
/** Source permission-zero player-only command; the actual held item handles its own customization. */
public final class WeaponCustomizeCommand {
    public static void register(RegisterCommandsEvent event){event.getDispatcher().register(Commands.literal("ntmcustomize").executes(context->run(context.getSource(),new String[0])).then(Commands.argument("arguments",StringArgumentType.greedyString()).executes(context->run(context.getSource(),StringArgumentType.getString(context,"arguments").trim().split("\\s+")))));}
    private static int run(CommandSourceStack source,String[] arguments){
        if(!(source.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)){source.sendFailure(Component.translatable("message.hbm.customize.player_only"));return 0;}
        var stack=player.getMainHandItem();if(!(stack.getItem() instanceof CustomizableWeapon item)){source.sendFailure(Component.translatable("message.hbm.customize.held_item"));return 0;}
        item.customize(player,stack,arguments);return 1;
    }
    private WeaponCustomizeCommand(){}
}
