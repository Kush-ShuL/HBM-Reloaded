package com.hbm.block.junglemechanics;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.toolability.*;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
/** Six true source blocks and the two genuinely missing zombie equipment identities; no alternate dungeon generator. */
public final class JungleMechanicsContent {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<FragileBrickBlock> FRAGILE=BLOCKS.register("brick_jungle_fragile",FragileBrickBlock::new);
 public static final DeferredBlock<JungleTrapBlock> TRAP=BLOCKS.register("brick_jungle_trap",JungleTrapBlock::new);
 public static final DeferredBlock<JungleOreBrick> OOZE=BLOCKS.register("brick_jungle_ooze",()->new JungleOreBrick(false)),MYSTIC=BLOCKS.register("brick_jungle_mystic",()->new JungleOreBrick(true));
 public static final DeferredBlock<JungleGlyphBlock> GLYPH=BLOCKS.register("brick_jungle_glyph",JungleGlyphBlock::new);
 public static final DeferredBlock<JungleCircleBlock> CIRCLE=BLOCKS.register("brick_jungle_circle",JungleCircleBlock::new);
 public static final DeferredItem<BlockItem> FRAGILE_ITEM=ITEMS.registerSimpleBlockItem(FRAGILE),OOZE_ITEM=ITEMS.registerSimpleBlockItem(OOZE),MYSTIC_ITEM=ITEMS.registerSimpleBlockItem(MYSTIC),CIRCLE_ITEM=ITEMS.registerSimpleBlockItem(CIRCLE);
 public static final DeferredItem<JungleMetaItem> TRAP_ITEM=ITEMS.register("brick_jungle_trap",()->new JungleMetaItem(TRAP.get())),GLYPH_ITEM=ITEMS.register("brick_jungle_glyph",()->new JungleMetaItem(GLYPH.get()));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<JungleTrapBlockEntity>> TRAP_ENTITY=ENTITIES.register("jungle_trapped_brick",()->BlockEntityType.Builder.of(JungleTrapBlockEntity::new,TRAP.get()).build(null));
 public static final DeferredItem<ItemSwordAbility> COBALT_SWORD=ITEMS.register("cobalt_sword",()->new ItemSwordAbility("cobalt_sword",12,0,JungleWeaponTiers.COBALT,new AvailableAbilities()));
 public static final DeferredItem<SteelHoeItem> CMB_HOE=ITEMS.register("cmb_hoe",()->new SteelHoeItem(JungleWeaponTiers.CMB));
 static BlockBehaviour.Properties properties(int light){return BlockBehaviour.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.STONE).strength(15,216).lightLevel(state->light).sound(SoundType.STONE).requiresCorrectToolForDrops();}
 public static ItemStack stack(Item item,int metadata){var stack=new ItemStack(item);stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);return stack;}
 public static List<ItemStack> allBlockStacks(){var result=new ArrayList<ItemStack>();for(var holder:List.of(FRAGILE_ITEM,OOZE_ITEM,MYSTIC_ITEM,CIRCLE_ITEM))result.add(new ItemStack(holder.get()));for(int i=0;i<15;i++)result.add(stack(TRAP_ITEM.get(),i));for(int i=0;i<16;i++)result.add(stack(GLYPH_ITEM.get(),i));return result;}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?allBlockStacks():List.of();}
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(JungleMechanicsContent::vanillaTabs);}
 private static void vanillaTabs(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))event.accept(COBALT_SWORD.get());if(event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES))event.accept(CMB_HOE.get());}
 private JungleMechanicsContent(){}
}
