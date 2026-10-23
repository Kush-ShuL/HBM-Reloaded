package com.hbm.bomb.nuclear;

import com.google.gson.Gson;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Actual ItemStarterKit nuclear branches, including ordered stacks and displaced armor. */
public final class NuclearStarterKit extends Item {
    public record StackDefinition(String symbol,String id,int count,Integer metadata,com.google.gson.JsonObject nativeStack){}
    public record Definition(String symbol,String id,List<StackDefinition> stacks,Integer hazmatTier,boolean warnInventory,boolean warnArmor){}
    private record Document(List<Definition> kits){}
    private static final Map<String,Definition> DEFINITIONS=load();
    private final Definition definition;
    public NuclearStarterKit(String symbol){super(new Properties().stacksTo(1));definition=DEFINITIONS.get(symbol);if(definition==null)throw new IllegalArgumentException("Unknown original nuclear starter kit "+symbol);}
    private static Map<String,Definition> load(){
        try(var stream=NuclearStarterKit.class.getResourceAsStream("/hbm/nuclear-kits.json")){
            if(stream==null)throw new IllegalStateException("Missing original nuclear kit definitions");
            var definitions=new java.util.LinkedHashMap<String,Definition>();
            for(var kit:new Gson().fromJson(new InputStreamReader(stream,StandardCharsets.UTF_8),Document.class).kits())definitions.put(kit.symbol(),kit);
            try(var extras=NuclearStarterKit.class.getResourceAsStream("/hbm/starter-kits74.json")){
                if(extras!=null)for(var kit:new Gson().fromJson(new InputStreamReader(extras,StandardCharsets.UTF_8),Document.class).kits()){
                    if(definitions.putIfAbsent(kit.symbol(),kit)!=null)throw new IllegalStateException("Duplicate original starter kit "+kit.symbol());
                }
            }
            return java.util.Collections.unmodifiableMap(definitions);
        }catch(java.io.IOException exception){throw new IllegalStateException(exception);}
    }
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+definition.symbol()+".name");}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        ItemStack kit=player.getItemInHand(hand);
        for(var supplied:definition.stacks()){
            var stack=supplied.nativeStack()!=null
                ?ItemStack.CODEC.parse(level.registryAccess().createSerializationContext(com.mojang.serialization.JsonOps.INSTANCE),supplied.nativeStack()).getOrThrow()
                :new ItemStack(HbmContent.item(supplied.symbol()),supplied.count());
            if(supplied.nativeStack()==null&&supplied.metadata()!=null)stack.set(HbmHazards.LEGACY_METADATA.get(),supplied.metadata());
            // The original kit deliberately ignores the remainder when the inventory is full.
            player.getInventory().add(stack);
        }
        if(definition.hazmatTier()!=null){
            for(int i=0;i<4;i++){ItemStack previous=player.getInventory().armor.get(i);if(!previous.isEmpty()&&!level.isClientSide)level.addFreshEntity(new ItemEntity(level,player.getX(),player.getEyeY(),player.getZ(),previous));}
            String suffix=switch(definition.hazmatTier()){case 1->"_red";case 2->"_grey";default->"";};
            String[] slots={"boots","legs","plate","helmet"};for(int i=0;i<4;i++)player.getInventory().armor.set(i,new ItemStack(HbmContent.item("hazmat_"+slots[i]+suffix)));
        }
        level.playSound(player,player.getX(),player.getY(),player.getZ(),HbmSounds.get("item.unpack"),SoundSource.PLAYERS,1F,1F);kit.shrink(1);return InteractionResultHolder.sidedSuccess(kit,level.isClientSide);
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){
        if(definition.warnInventory())tooltip.add(Component.translatable("item.hbm.nuclear_kit.warn_inventory"));
        if(definition.warnArmor())tooltip.add(Component.translatable("item.hbm.nuclear_kit.warn_armor"));
    }
}
