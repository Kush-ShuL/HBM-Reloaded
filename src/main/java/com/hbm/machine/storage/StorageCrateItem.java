package com.hbm.machine.storage;

import com.hbm.item.lock.KeyPinItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** Source ItemBlockStorageCrate: in-hand opening, exact matching-key checks and privacy-preserving item lore. */
public final class StorageCrateItem extends BlockItem {
    public final StorageCrateKind kind;
    public StorageCrateItem(StorageCrateBlock block){super(block,new Item.Properties());kind=block.kind;}
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 1;}
    @Override public InteractionResult useOn(UseOnContext context){if(StorageCrateConfig.openHeld()&&context.getPlayer()!=null&&!context.getPlayer().isShiftKeyDown())return InteractionResult.PASS;return super.useOn(context);}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);if(!StorageCrateConfig.openHeld()||hand!=InteractionHand.MAIN_HAND)return InteractionResultHolder.pass(stack);
        if(!level.isClientSide&&stack.getCount()==1&&player instanceof ServerPlayer server){
            var tag=CrateStackData.data(stack);if(tag.contains("lock")){
                boolean found=false;for(var item:player.getInventory().items)if(item.getItem() instanceof KeyPinItem key&&key.isKey()&&item.has(DataComponents.CUSTOM_DATA)&&KeyPinItem.getPins(item)==tag.getInt("lock")){found=true;break;}
                if(!found)return InteractionResultHolder.pass(stack);
                //The source held-lock path intentionally does not accept the red master key or a lockpick.
            }
            if(tag.getBoolean("spiders")){CrateContents.spiders(player,player.getX(),player.getY(),player.getZ());tag.remove("spiders");CrateStackData.data(stack,tag);}
            var inventory=new HeldCrateInventory(player,stack,kind);server.openMenu(new SimpleMenuProvider((id,inv,viewer)->new StorageCrateMenu(id,inv,inventory,kind,null),inventory.displayName()),buffer->{buffer.writeBoolean(true);buffer.writeEnum(kind);});
        }
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        var tag=CrateStackData.data(stack);boolean locked=tag.contains("lock");if(tag.getBoolean("spiders")){if(locked)lines.add(Component.translatable("tooltip.hbm.storage_crate.locked").withStyle(ChatFormatting.RED));lines.add(Component.translatable("tooltip.hbm.storage_crate.spiders").withStyle(ChatFormatting.GRAY,ChatFormatting.ITALIC));return;}
        if(locked)lines.add(Component.translatable("tooltip.hbm.storage_crate.locked").withStyle(ChatFormatting.RED));
        var lookup=context.registries();if(lookup==null)return;
        if(locked){for(int i=0;i<104;i++)if(!CrateStackData.slot(tag,i,lookup).isEmpty()){lines.add(Component.translatable("tooltip.hbm.storage_crate.heavy").withStyle(ChatFormatting.YELLOW));return;}lines.add(Component.translatable("tooltip.hbm.storage_crate.empty").withStyle(ChatFormatting.YELLOW));return;}
        var contents=new ArrayList<Component>();int amount=0;
        for(int i=0;i<104;i++){var item=CrateStackData.slot(tag,i,lookup);if(item.isEmpty())continue;amount++;if(contents.size()<10){var line=Component.literal(" - ").append(item.getHoverName());if(item.getCount()>1)line.append(Component.literal(" x"+item.getCount()));contents.add(line.withStyle(ChatFormatting.AQUA));}}
        if(!contents.isEmpty()){lines.add(Component.translatable("tooltip.hbm.storage_crate.contains").withStyle(ChatFormatting.AQUA));lines.addAll(contents);amount-=contents.size();if(amount>0)lines.add(Component.translatable("tooltip.hbm.storage_crate.more",amount).withStyle(ChatFormatting.AQUA));}
    }
}
