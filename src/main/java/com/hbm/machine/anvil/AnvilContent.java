package com.hbm.machine.anvil;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class AnvilContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public record Tier(String symbol,int tier,String texture){}
    public static final List<Tier> TIERS=List.of(new Tier("iron",1,"iron"),new Tier("lead",1,"lead"),new Tier("steel",2,"steel"),new Tier("desh",3,"desh"),new Tier("ferrouranium",4,"ferrouranium"),new Tier("saturnite",5,"saturnite"),new Tier("bismuth_bronze",5,"bismuth_bronze"),new Tier("arsenic_bronze",5,"arsenic_bronze"),new Tier("schrabidate",6,"schrabidate"),new Tier("dnt",7,"dnt"),new Tier("osmiridium",8,"osmiridium"),new Tier("murky",1916169,"steel"));
    public static final Map<String,DeferredBlock<NtmAnvilBlock>> ANVILS=new LinkedHashMap<>();public static final List<DeferredItem<BlockItem>> BLOCK_ITEMS=new ArrayList<>();public static final List<DeferredItem<HotSmithingItem>> HOT=new ArrayList<>();
    static{for(Tier tier:TIERS){var block=BLOCKS.register("anvil_"+tier.symbol(),()->new NtmAnvilBlock(tier));ANVILS.put(tier.symbol(),block);BLOCK_ITEMS.add(ITEMS.register("anvil_"+tier.symbol(),()->new BlockItem(block.get(),new Item.Properties()){@Override public void appendHoverText(ItemStack stack,Item.TooltipContext context,List<net.minecraft.network.chat.Component> tooltip,TooltipFlag flag){super.appendHoverText(stack,context,tooltip,flag);tooltip.add(net.minecraft.network.chat.Component.literal("Tier "+tier.tier()+" Anvil").withStyle(net.minecraft.ChatFormatting.GOLD));}}));}for(String symbol:List.of("ingot_meteorite","ingot_meteorite_forged","blade_meteorite","ingot_steel_dusted","ingot_chainsteel"))HOT.add(ITEMS.register(symbol,()->new HotSmithingItem(symbol,symbol.equals("ingot_steel_dusted"))));}
    public static final DeferredHolder<MenuType<?>,MenuType<AnvilMenu>> MENU=MENUS.register("ntm_anvil",()->new MenuType<>((IContainerFactory<AnvilMenu>)AnvilMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<AnvilConstructionRecipe>> CONSTRUCTION_TYPE=TYPES.register("anvil_construction",()->new RecipeType<AnvilConstructionRecipe>(){});
    public static final DeferredHolder<RecipeType<?>,RecipeType<AnvilSmithingRecipe>> SMITHING_TYPE=TYPES.register("anvil_smithing",()->new RecipeType<AnvilSmithingRecipe>(){});
    public static final DeferredHolder<RecipeSerializer<?>,AnvilConstructionRecipe.Serializer> CONSTRUCTION_SERIALIZER=SERIALIZERS.register("anvil_construction",AnvilConstructionRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>,AnvilSmithingRecipe.Serializer> SMITHING_SERIALIZER=SERIALIZERS.register("anvil_smithing",AnvilSmithingRecipe.Serializer::new);
    private AnvilContent(){}
    public static void register(IEventBus bus){MoldContent.register(bus);BLOCKS.register(bus);ITEMS.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);bus.addListener(AnvilCraftRequest::register);AnvilRecipeConfig.register();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?BLOCK_ITEMS.stream().map(item->new ItemStack(item.get())).toList():tab.equals("control")?MoldContent.creativeStacks():tab.equals("parts")?HOT.stream().map(item->new ItemStack(item.get())).toList():List.of();}
    public static List<ItemStack> exactTier(int tier){return TIERS.stream().filter(value->value.tier()==tier).map(value->new ItemStack(ANVILS.get(value.symbol()).get())).toList();}
}
