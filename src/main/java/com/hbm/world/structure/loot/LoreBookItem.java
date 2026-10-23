package com.hbm.world.structure.loot;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
/** ItemBookLore's original flexible per-book page key, colours and per-page argument payload. */
public final class LoreBookItem extends Item {
    public static java.util.function.Consumer<ItemStack> OPEN=stack->{};
    public static java.util.function.Predicate<String> HAS_TRANSLATION=key->false;
    public LoreBookItem(){super(new Properties().stacksTo(1));}
    public static CompoundTag data(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
    public static ItemStack create(String key,int pages,int cover,int title){var stack=new ItemStack(StructureLootContent.BOOK.get());var tag=new CompoundTag();tag.putString("k",key);tag.putShort("p",(short)pages);tag.putInt("cov_col",cover);tag.putInt("tit_col",title);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return stack;}
    public static void addArgs(ItemStack stack,int page,String...args){if(!stack.has(DataComponents.CUSTOM_DATA))return;var tag=data(stack);var values=new CompoundTag();for(int i=0;i<args.length;i++)values.putString("a"+(i+1),args[i]);tag.put("p"+page,values);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
    @Override public Component getName(ItemStack stack){String key=data(stack).getString("k");return Component.translatable("book_lore."+(key.isEmpty()?"test":key)+".name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){String key=data(stack).getString("k");if(!key.isEmpty()&&HAS_TRANSLATION.test("book_lore."+key+".author"))tooltip.add(Component.translatable("book_lore.author",Component.translatable("book_lore."+key+".author")));}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(level.isClientSide)OPEN.accept(stack.copy());return InteractionResultHolder.pass(stack);}
    public static int tint(ItemStack stack,int pass){if(pass==0)return 0xffffff;int color=data(stack).getInt(pass==1?"cov_col":"tit_col");return color>0?color:pass==1?0x303030:0xffffff;}
}
