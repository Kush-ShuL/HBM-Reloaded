package com.hbm.commands;

import com.hbm.block.pipe.TypedPipeItem;
import com.hbm.client.wiki.WikiRenderScreen;
import com.hbm.machine.reactor.WastePlateItem;
import com.hbm.machine.reactor.rbmk.fuel.RbmkPelletItem;
import com.hbm.registry.HbmCreativeTabs;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import org.slf4j.Logger;

/** 按实际物品或方块类名导出 Wiki 图片；与开发快捷键共用原生物品变体收集器。 */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class CommandWikiRender {
    public static final String OUTPUT_DIRECTORY="wiki-block-renders-256";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final SimpleCommandExceptionType USAGE = new SimpleCommandExceptionType(
            Component.literal("/ntmwikirender <type> ").withStyle(ChatFormatting.GREEN)
                    .append(Component.translatable("commands.hbm.wiki_render.usage").withStyle(ChatFormatting.LIGHT_PURPLE)));

    private CommandWikiRender() {}

    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ntmwikirender")
                .executes(context -> { throw USAGE.create(); })
                .then(Commands.argument("type", StringArgumentType.word())
                        .executes(context -> execute(context.getSource(), StringArgumentType.getString(context, "type")))
                        // 参数一决定类型与文件名前缀；后续参数和源命令一样不参与处理。
                        .then(Commands.argument("ignored", StringArgumentType.greedyString())
                                .executes(context -> execute(context.getSource(), StringArgumentType.getString(context, "type"))))));
    }

    private static int execute(CommandSourceStack source, String type) throws CommandSyntaxException {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || source.getEntity() != minecraft.player) {
            throw CommandSourceStack.ERROR_NOT_PLAYER.create();
        }
        LOGGER.info("Taking a screenshot of {}", type);
        var preview=collectVariants(item->item.getClass().getSimpleName().equalsIgnoreCase(type)
                ||item instanceof BlockItem blockItem&&blockItem.getBlock().getClass().getSimpleName().equalsIgnoreCase(type));
        minecraft.player.closeContainer();
        minecraft.setScreen(new WikiRenderScreen(preview,type,OUTPUT_DIRECTORY,16));
        return 1;
    }

    /** The command and explicit bulk-gun shortcut retain one actual native variant producer. */
    public static List<ItemStack> collectVariants(Predicate<Item> filter) {
        Minecraft minecraft=Minecraft.getInstance();
        if(minecraft.player==null||minecraft.level==null)throw new IllegalStateException("Wiki item collection requires a current client player/world");

        // 原生创造栏生成器是组件变体的真实入口；本次主动生成，避免沿用流体等重载前的缓存。
        var parameters = new CreativeModeTab.ItemDisplayParameters(minecraft.player.connection.enabledFeatures(),
                minecraft.player.canUseGameMasterBlocks(), minecraft.player.connection.registryAccess());
        var tabs = CreativeModeTabs.allTabs();
        tabs.stream().filter(tab -> tab.getType() == CreativeModeTab.Type.CATEGORY)
                .forEach(tab -> tab.buildContents(parameters));
        tabs.stream().filter(tab -> tab.getType() != CreativeModeTab.Type.CATEGORY)
                .forEach(tab -> tab.buildContents(parameters));
        Map<Item, List<ItemStack>> variants = new IdentityHashMap<>();
        for (ItemStack stack : BuiltInRegistries.CREATIVE_MODE_TAB.getOrThrow(CreativeModeTabs.SEARCH).getDisplayItems()) {
            if (!stack.isEmpty()) variants.computeIfAbsent(stack.getItem(), ignored -> new ArrayList<>()).add(stack);
        }

        List<ItemStack> preview = new ArrayList<>();
        // 外层严格按实际物品注册遍历顺序，不按显示名、注册名或距离另行排序。
        for (Item item : BuiltInRegistries.ITEM) {
            var id = BuiltInRegistries.ITEM.getKey(item);
            if (!id.getNamespace().equals("hbm") || item == HbmCreativeTabs.UI_ICON.get()
                    || item instanceof com.hbm.item.FixedEnumItem fixed && fixed.family().equals("achievement_icon")) continue;
            if(!filter.test(item))continue;
            Class<? extends Item> itemClass = item.getClass();
            if (itemClass == RbmkPelletItem.class || itemClass == WastePlateItem.class || itemClass == TypedPipeItem.class) {
                preview.add(new ItemStack(item));
            } else {
                List<ItemStack> itemVariants = variants.get(item);
                if (itemVariants == null || itemVariants.isEmpty()) {
                    // 无创造栏条目的隐藏物品仍有默认实例，不因浏览器隐藏规则漏掉该注册物品。
                    preview.add(new ItemStack(item));
                } else {
                    itemVariants.forEach(stack -> preview.add(stack.copy()));
                }
            }
        }
        return preview;
    }
}
