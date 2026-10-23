package com.hbm.item.settings;

import com.hbm.api.machine.SettingsCopySource;
import com.hbm.block.pipe.PipeControls;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** Original ItemSettingsTool: first-use protocol, persistent source data and server Alt selection delay. */
public final class SettingsToolItem extends Item {
    public SettingsToolItem() { super(new Properties().stacksTo(1)); }
    @Override public InteractionResult onItemUseFirst(ItemStack stack,UseOnContext context) {
        var level=context.getLevel();var player=context.getPlayer();if(player==null) return InteractionResult.PASS;
        var source=SettingsCopySource.resolve(level,context.getClickedPos());if(source==null) return InteractionResult.PASS;
        if(player.isShiftKeyDown()) {
            CompoundTag settings=source.settings().getSettings(level,source.position());
            if(settings!=null) {
                settings.putString("tileName",source.settings().getSettingsSourceID(source));settings.putInt("copyIndex",0);settings.putInt("inputDelay",0);
                String[] info=source.settings().infoForDisplay(level,source.position());
                if(info!=null) { var display=new ListTag();for(String line:info) { var entry=new CompoundTag();entry.putString("info",line);display.add(entry); }settings.put("displayInfo",display); }
                stack.set(DataComponents.CUSTOM_DATA,CustomData.of(settings));
                if(level.isClientSide) player.displayClientMessage(prefix(stack).append(Component.translatable("message.hbm.settings.copied",source.settings().getSettingsSourceDisplay(source)).withStyle(ChatFormatting.AQUA)),false);
            } else {
                stack.remove(DataComponents.CUSTOM_DATA);
                player.displayClientMessage(prefix(stack).append(Component.translatable("message.hbm.settings.unsupported",source.settings().getSettingsSourceDisplay(source)).withStyle(ChatFormatting.RED)),false);
            }
        } else if(stack.has(DataComponents.CUSTOM_DATA)) {
            var settings=stack.get(DataComponents.CUSTOM_DATA).copyTag();source.settings().pasteSettings(settings,settings.getInt("copyIndex"),level,player,source.position());
        }
        // The original returns false on the client, true on the server; use-first prevents machine GUI.
        return level.isClientSide?InteractionResult.PASS:InteractionResult.CONSUME;
    }
    private static net.minecraft.network.chat.MutableComponent prefix(ItemStack stack) { return Component.literal("[").append(Component.translatable(stack.getItem().getDescriptionId())).append("] ").withStyle(ChatFormatting.DARK_AQUA); }
    @Override public void inventoryTick(ItemStack stack,Level level,Entity holder,int slot,boolean selected) {
        if(!(holder instanceof ServerPlayer player)||player.getMainHandItem()!=stack||!stack.has(DataComponents.CUSTOM_DATA)) return;
        var data=stack.get(DataComponents.CUSTOM_DATA).copyTag();int delay=data.getInt("inputDelay")+1;
        var display=data.getList("displayInfo",Tag.TAG_COMPOUND);
        if(PipeControls.flags(player).alt()&&delay>4) { int index=data.getInt("copyIndex")+1;if(index>display.size()-1) index=0;data.putInt("copyIndex",index);delay=0; }
        data.putInt("inputDelay",delay);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.hbm.settings.description"));
        tooltip.add(Component.translatable("tooltip.hbm.settings.use"));
        tooltip.add(Component.translatable("tooltip.hbm.settings.pipes"));
        var custom=stack.get(DataComponents.CUSTOM_DATA);if(custom==null) return;var data=custom.copyTag();
        tooltip.add(data.contains("tileName")?Component.translatable(data.getString("tileName")+".name").withStyle(ChatFormatting.BLUE):Component.translatable("tooltip.hbm.settings.none").withStyle(ChatFormatting.RED));
    }
}
