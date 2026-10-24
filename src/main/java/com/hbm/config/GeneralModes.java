package com.hbm.config;

import java.util.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** One actual source GeneralConfig for all consumers, including source528-over-LBSM precedence. */
public final class GeneralModes {
    public static final ModConfigSpec SPEC;
    private static final net.neoforged.neoforge.registries.DeferredRegister<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.conditions.ICondition>> CONDITIONS=net.neoforged.neoforge.registries.DeferredRegister.create(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.CONDITION_CODECS,"hbm");
    private static final Object CONDITION=CONDITIONS.register("general_mode",()->GeneralModeCondition.CODEC);
    private static final Map<String,ModConfigSpec.BooleanValue> FLAGS=new LinkedHashMap<>();
    private static final Map<String,Boolean> DEFAULTS=new LinkedHashMap<>();
    private static final ModConfigSpec.IntValue COLTAN,SCHRAB;
    private static final ModConfigSpec.BooleanValue LOGIN_MESSAGES,EXTENDED_LOGGING;
    private static volatile Snapshot remote;
    static {
        var builder=new ModConfigSpec.Builder();
        flag(builder.comment("使用 HBM 自定义天空。").translation("config.hbm.impact.skyboxes"),"1.31_enableSkyboxes",true);
        flag(builder.comment("启用陨石撞击后的主世界光照与天空颜色变化。").translation("config.hbm.impact.provider"),"1.32_enableImpactWorldProvider",true);
        flag(builder,"1.05_enableLandmineSpawn",true);
        flag(builder,"1.14_enableBomberShortMode",false);
        flag(builder,"1.15_enableVaultSpawn",true);
        flag(builder,"1.06_enableRadHotspotSpawn",true);
        flag(builder.comment("灾变模式：生物死亡时触发卫星坠落。").translation("config.hbm.cataclysm"),"1.17_enableCataclysm",false);
        flag(builder.comment("允许病毒水晶扩散，默认关闭。").translation("config.hbm.crystal.virus_spreading"),"virusCrystalSpreadingEnabled",false);
        flag(builder,"1.99_enableExpensiveMode",false);
        flag(builder.comment("添加HBM原版的数字采矿机装配配方；需要同时安装Mekanism。").translation("config.hbm.general.mekanism_recipes"),"mekanismRecipeChangesEnabled",true);
        flag(builder,"1.44_enableMachineGravity",false);
        flag(builder,"1.37_enableGuideBook",true);
        flag(builder.comment("调试模式：允许结构编辑工具将导出代码追加写入 hbmConfig/structureOutput.txt，并启用仅原作者UUID可使用的聊天调试工具。默认关闭。").translation("config.hbm.structure_export.debug"),"debugModeEnabled",false);
        flag(builder,"enable528Mode",false);
        for(String key:List.of("X528_forceReasimBoilers","X528_enableColtanDeposit","X528_enableBosniaSimulator","X528_enable528NetherBurn","X528_enable528PressurizedRecipes","X528_enable528ExplosiveEnergistics","X528_enable528MachineGravity"))flag(builder,key,true);
        flag(builder,"X528_enableColtanSpawning",false);
        COLTAN=builder.defineInRange(java.util.List.of("mode528","coltanOreChanceDenominator"),2,0,Integer.MAX_VALUE);
        flag(builder,"enableLessBullshitMode",false);
        for(String key:List.of("LBSM_shortDecay","LBSM_recipeSimpleArmor","LBSM_recipeSimpleTool","LBSM_recipeSimpleAlloy","LBSM_recipeSimpleChemistry","LBSM_recipeSimpleCentrifuge","LBSM_recipeUnlockAnvil","LBSM_recipeSimpleCrafting","LBSM_recipeSimpleMedicine","LBSM_safeCrates","LBSM_safeMEDrives"))flag(builder,key,true);
        SCHRAB=builder.defineInRange(java.util.List.of("qualityOfLife","schrabidiumOreChanceDenominator"),20,1,Integer.MAX_VALUE);
        LOGIN_MESSAGES=builder.comment("玩家登录时显示欢迎与当前模组、Minecraft版本；加载器已有更新结果时显示新版本提示。默认true。")
                .define(java.util.List.of("messages","login","enabled"),true);
        EXTENDED_LOGGING=builder.comment("启用扩展事件日志。默认false；仅控制日志记录，不改变对应玩法。")
                .define(java.util.List.of("diagnostics","extendedLoggingEnabled"),false);
        SPEC=builder.build();
    }
    private static java.util.List<String> configPath(String key){return switch(key){
        case "1.31_enableSkyboxes" -> java.util.List.of("display","customSkyEnabled");
        case "1.32_enableImpactWorldProvider" -> java.util.List.of("environment","impactChangesEnabled");
        case "1.05_enableLandmineSpawn" -> java.util.List.of("worldGeneration","landminesEnabled");
        case "1.14_enableBomberShortMode" -> java.util.List.of("aircraft","shortBombingRunsEnabled");
        case "1.15_enableVaultSpawn" -> java.util.List.of("worldGeneration","surfaceVaultsEnabled");
        case "1.06_enableRadHotspotSpawn" -> java.util.List.of("worldGeneration","radiationHotspotsEnabled");
        case "1.17_enableCataclysm" -> java.util.List.of("worldHazards","satelliteCataclysmEnabled");
        case "virusCrystalSpreadingEnabled" -> java.util.List.of("worldHazards","virusCrystalSpreadingEnabled");
        case "1.99_enableExpensiveMode" -> java.util.List.of("recipes","expensiveModeEnabled");
        case "mekanismRecipeChangesEnabled" -> java.util.List.of("recipes","mekanismIntegrationEnabled");
        case "1.44_enableMachineGravity" -> java.util.List.of("machines","gravityEnabled");
        case "1.37_enableGuideBook" -> java.util.List.of("items","guideBookEnabled");
        case "debugModeEnabled" -> java.util.List.of("debugging","enabled");
        case "enable528Mode" -> java.util.List.of("mode528","enabled");
        case "X528_forceReasimBoilers" -> java.util.List.of("mode528","realisticBoilersEnabled");
        case "X528_enableColtanDeposit" -> java.util.List.of("mode528","coltanDepositsEnabled");
        case "X528_enableBosniaSimulator" -> java.util.List.of("mode528","landmineHazardsEnabled");
        case "X528_enable528NetherBurn" -> java.util.List.of("mode528","netherBurnDamageEnabled");
        case "X528_enable528PressurizedRecipes" -> java.util.List.of("mode528","pressurizedRecipesEnabled");
        case "X528_enable528ExplosiveEnergistics" -> java.util.List.of("mode528","explosiveAppliedEnergisticsEnabled");
        case "X528_enable528MachineGravity" -> java.util.List.of("mode528","machineGravityEnabled");
        case "X528_enableColtanSpawning" -> java.util.List.of("mode528","randomColtanOreEnabled");
        case "enableLessBullshitMode" -> java.util.List.of("qualityOfLife","enabled");
        case "LBSM_shortDecay" -> java.util.List.of("qualityOfLife","shorterWasteDecayEnabled");
        case "LBSM_recipeSimpleArmor" -> java.util.List.of("qualityOfLife","recipes","simplerArmorEnabled");
        case "LBSM_recipeSimpleTool" -> java.util.List.of("qualityOfLife","recipes","simplerToolsEnabled");
        case "LBSM_recipeSimpleAlloy" -> java.util.List.of("qualityOfLife","recipes","simplerAlloysEnabled");
        case "LBSM_recipeSimpleChemistry" -> java.util.List.of("qualityOfLife","recipes","simplerChemistryEnabled");
        case "LBSM_recipeSimpleCentrifuge" -> java.util.List.of("qualityOfLife","recipes","simplerCentrifugeEnabled");
        case "LBSM_recipeUnlockAnvil" -> java.util.List.of("qualityOfLife","recipes","anvilUnlocksEnabled");
        case "LBSM_recipeSimpleCrafting" -> java.util.List.of("qualityOfLife","recipes","simplerCraftingEnabled");
        case "LBSM_recipeSimpleMedicine" -> java.util.List.of("qualityOfLife","recipes","simplerMedicineEnabled");
        case "LBSM_safeCrates" -> java.util.List.of("qualityOfLife","saferCratesEnabled");
        case "LBSM_safeMEDrives" -> java.util.List.of("qualityOfLife","saferStorageDrivesEnabled");
        default -> throw new IllegalArgumentException("Unknown configuration definition "+key);
    };}
    private static void flag(ModConfigSpec.Builder builder,String key,boolean value){DEFAULTS.put(key,value);FLAGS.put(key,builder.define(configPath(key),value));}
    public static boolean flag(String key){var snapshot=remote;if(snapshot!=null&&snapshot.flags.containsKey(key))return snapshot.flags.get(key);var field=FLAGS.get(key);if(field==null)throw new IllegalArgumentException("Unknown original GeneralConfig flag "+key);return SPEC.isLoaded()?field.get():DEFAULTS.get(key);}
    /** Original client sky/provider switches remain local; use the same spec/defaults without the server mode snapshot. */
    public static boolean localFlag(String key){var field=FLAGS.get(key);if(field==null)throw new IllegalArgumentException("Unknown original GeneralConfig flag "+key);return SPEC.isLoaded()?field.get():DEFAULTS.get(key); }
    /** Server-local messages and logging are independent of the synchronized mode flags. */
    public static boolean loginMessagesEnabled(){return !SPEC.isLoaded()||LOGIN_MESSAGES.get();}
    public static boolean extendedLogging(){return SPEC.isLoaded()&&EXTENDED_LOGGING.get();}
    public static boolean mode528(){return flag("enable528Mode");}
    public static boolean lbsm(){return !mode528()&&flag("enableLessBullshitMode");}
    public static boolean expensive(){return flag("1.99_enableExpensiveMode");}
    public static boolean forceReasimBoilers(){return mode528()&&flag("X528_forceReasimBoilers");}
    public static boolean netherBurn(){return mode528()&&flag("X528_enable528NetherBurn");}
    public static boolean pressurizedRecipes(){return mode528()&&flag("X528_enable528PressurizedRecipes");}
    public static boolean explosiveEnergistics(){return mode528()&&flag("X528_enable528ExplosiveEnergistics");}
    public static boolean machineGravity(){return flag("1.44_enableMachineGravity")||mode528()&&flag("X528_enable528MachineGravity");}
    public static boolean bosnia(){return mode528()&&flag("X528_enableBosniaSimulator");}
    public static boolean coltanDeposit(){return flag("X528_enableColtanDeposit");}
    public static boolean coltanRandom(){return flag("X528_enableColtanSpawning");}
    public static int coltanFrequency(){var snapshot=remote;return snapshot!=null?snapshot.coltan:SPEC.isLoaded()?COLTAN.get():2;}
    public static int schrabRate(){var snapshot=remote;return snapshot!=null?snapshot.schrab:SPEC.isLoaded()?SCHRAB.get():20;}
    public static boolean lbsm(String key){return lbsm()&&flag(key);}
    public static Snapshot synchronizedSnapshot(){return remote;}
    public static void clearRemote(){remote=null;}
    private static Snapshot local(net.minecraft.server.MinecraftServer server){
        var values=new LinkedHashMap<String,Boolean>();
        for(var pair:FLAGS.entrySet())values.put(pair.getKey(),SPEC.isLoaded()?pair.getValue().get():DEFAULTS.get(pair.getKey()));
        int coltan=SPEC.isLoaded()?COLTAN.get():2,schrab=SPEC.isLoaded()?SCHRAB.get():20;
        boolean badge528=values.get("enable528Mode")&&values.get("X528_forceReasimBoilers")
                &&!values.get("X528_enableColtanSpawning")&&values.get("X528_enableBosniaSimulator")
                &&values.get("X528_enable528NetherBurn")&&values.get("X528_enable528PressurizedRecipes")
                &&values.get("X528_enable528ExplosiveEnergistics")&&values.get("X528_enable528MachineGravity")&&coltan<=2;
        boolean badgeExpensive=values.get("1.99_enableExpensiveMode")
                &&!com.hbm.machine.precisionassembler.PrecisionAssemblerRecipeConfig.customized(server.getRecipeManager());
        boolean badgeRampant=com.hbm.entity.mob.glyphid.GlyphidConfig.rampant()
                &&com.hbm.entity.mob.glyphid.GlyphidConfig.naturalScouts()
                &&com.hbm.entity.mob.glyphid.GlyphidConfig.scoutThreshold()<=.1D
                &&com.hbm.entity.mob.glyphid.GlyphidConfig.extended()
                &&com.hbm.entity.mob.glyphid.GlyphidConfig.dig()
                &&com.hbm.entity.mob.glyphid.GlyphidConfig.guidance();
        return new Snapshot(values,coltan,schrab,badge528,badgeExpensive,badgeRampant);
    }
    /** 三个徽章值由服务端按实际配置和已装载配方计算；客户端不以本地COMMON配置猜测。 */
    public record Snapshot(Map<String,Boolean> flags,int coltan,int schrab,
                           boolean badge528,boolean badgeExpensive,boolean badgeRampant) implements CustomPacketPayload {
        public static final Type<Snapshot> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("hbm","general_modes"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Snapshot> CODEC=StreamCodec.of((buffer,value)->{buffer.writeVarInt(value.flags.size());value.flags.forEach((key,flag)->{buffer.writeUtf(key,64);buffer.writeBoolean(flag);});buffer.writeVarInt(value.coltan);buffer.writeVarInt(value.schrab);buffer.writeBoolean(value.badge528);buffer.writeBoolean(value.badgeExpensive);buffer.writeBoolean(value.badgeRampant);},buffer->{int count=buffer.readVarInt();if(count<0||count>64)throw new IllegalArgumentException("Invalid GeneralConfig flags");var flags=new LinkedHashMap<String,Boolean>();for(int i=0;i<count;i++)flags.put(buffer.readUtf(64),buffer.readBoolean());return new Snapshot(Map.copyOf(flags),buffer.readVarInt(),buffer.readVarInt(),buffer.readBoolean(),buffer.readBoolean(),buffer.readBoolean());});
        public Snapshot{flags=Map.copyOf(flags);}
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public static void register(IEventBus bus,ModContainer container){com.hbm.player.LoginMessages.register();SourceMekanismCrafting.register();CONDITIONS.register(bus);container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-general-common.toml");bus.addListener(GeneralModes::payloads);NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->clearRemote());NeoForge.EVENT_BUS.addListener(GeneralModes::synchronize);}
    private static void payloads(RegisterPayloadHandlersEvent event){event.registrar("2").playToClient(Snapshot.TYPE,Snapshot.CODEC,(packet,context)->context.enqueueWork(()->remote=packet));}
    private static void synchronize(OnDatapackSyncEvent event){var snapshot=local(event.getPlayerList().getServer());if(event.getPlayer()!=null)PacketDistributor.sendToPlayer(event.getPlayer(),snapshot);else for(var player:event.getPlayerList().getPlayers())PacketDistributor.sendToPlayer(player,snapshot);}
    /** 精密装配配方装载完成后，经同一个模式包同步最新的真实徽章状态。 */
    public static void synchronizeCurrentState(net.minecraft.server.MinecraftServer server){
        var players=server.getPlayerList();
        if(players==null||players.getPlayers().isEmpty())return;
        var snapshot=local(server);
        for(var player:players.getPlayers())PacketDistributor.sendToPlayer(player,snapshot);
    }
    private GeneralModes(){}
}
