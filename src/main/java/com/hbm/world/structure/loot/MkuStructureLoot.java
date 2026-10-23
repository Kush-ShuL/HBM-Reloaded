package com.hbm.world.structure.loot;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
/** Genuine MKU book clues share the exact same seed-shuffled grid as the executable recipe. */
public final class MkuStructureLoot {
    private static final List<String> INGREDIENTS=List.of("powder_iodine","powder_fire","dust","ingot_mercury","morning_glory","syringe_metal_empty");
    private static final String[] KEYS={"book_iodine","book_phosphorous","book_dust","book_mercury","book_flower","book_syringe"};
    private static final int[] PAGES={3,2,3,2,2,2};
    public static boolean ready(){try{for(String name:INGREDIENTS)com.hbm.registry.HbmContent.item(name);return true;}catch(IllegalArgumentException missing){return false;}}
    // These are original ModItems field names, not registry paths. In particular
    // ingot_mercury is actually registered as nugget_mercury in the source.
    private static ItemStack ingredient(String name){return new ItemStack(com.hbm.registry.HbmContent.item(name));}
    public static List<ItemStack> recipe(long seed){var grid=new ArrayList<ItemStack>();for(var name:INGREDIENTS)grid.add(ingredient(name));for(int i=0;i<3;i++)grid.add(ItemStack.EMPTY);Collections.shuffle(grid,new Random(seed));return List.copyOf(grid);}
    public static ItemStack book(ServerLevel level,ItemStack ingredient){int index=-1;for(int i=0;i<INGREDIENTS.size();i++)if(ingredient.is(ingredient(INGREDIENTS.get(i)).getItem())){index=i;break;}if(index<0)throw new IllegalArgumentException("Not a source MKU component");var grid=recipe(level.getSeed());int slot=1;for(int i=0;i<9;i++)if(grid.get(i).is(ingredient.getItem())){slot=i+1;break;}var book=LoreBookItem.create(KEYS[index],PAGES[index],0x271e44,0xfbfff4);LoreBookItem.addArgs(book,PAGES[index]-1,Integer.toString(slot));return book;}
    public static void populate(ServerLevel level,LootPileBlockEntity loot,RandomSource random){var ingredient=ingredient(INGREDIENTS.get(random.nextInt(6)));var book=book(level,ingredient);loot.add(ingredient,random.nextGaussian()*.02,0,.25+random.nextGaussian()*.02);loot.add(book,random.nextGaussian()*.02,0,-.25+random.nextGaussian()*.02);}
    public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{if(event.getPlayer()==null)install(event.getPlayerList().getServer());});}
    private static void install(MinecraftServer server){if(!ready())throw new IllegalStateException("Incomplete real MKU recipe ingredients");var id=ResourceLocation.fromNamespaceAndPath("hbm","mku_world_seed");var recipes=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var recipe:server.getRecipeManager().getOrderedRecipes())recipes.put(recipe.id(),recipe);recipes.put(id,new RecipeHolder<>(id,new MkuRecipe(server.getWorldData().worldGenOptions().seed())));server.getRecipeManager().replaceRecipes(recipes.values());}
    private MkuStructureLoot(){}
}
