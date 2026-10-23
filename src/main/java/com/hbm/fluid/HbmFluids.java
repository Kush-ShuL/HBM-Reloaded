package com.hbm.fluid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.HbmReloaded;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.neoforged.fml.loading.FMLPaths;

/** Fixed local IDs, original names/order, JSON overrides and identity-preserving reload. */
public final class HbmFluids {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<Integer, HbmFluidType> BY_ID = new LinkedHashMap<>();
    private static final Map<String, HbmFluidType> BY_NAME = new LinkedHashMap<>();
    private static final Map<String, HbmFluidType> BY_SYMBOL = new LinkedHashMap<>();
    private static final Map<String, HbmFluidType> MIGRATION = new LinkedHashMap<>();
    private static final Map<String, String> RENAMES = new LinkedHashMap<>();
    private static final List<HbmFluidType> REGISTER_ORDER = new ArrayList<>();
    private static final List<HbmFluidType> NICE_ORDER = new ArrayList<>();
    private static final List<HbmFluidType> CUSTOM = new ArrayList<>();
    private static final List<HbmFluidType> FOREIGN = new ArrayList<>();
    public static final List<Runnable> additionalListeners = new ArrayList<>();
    private static final JsonObject SOURCE = readSource();
    private static Path configFolder;

    static {
        int expected = 0;
        for (var value : SOURCE.getAsJsonArray("fluids")) {
            var row = value.getAsJsonObject();
            int id = row.get("id").getAsInt();
            if (id != expected++) throw new IllegalStateException("Original fixed fluid ID shifted: " + id);
            var type = new HbmFluidType(row);
            add(type);
            BY_SYMBOL.put(row.get("symbol").getAsString(), type);
        }
        for (var name : SOURCE.getAsJsonArray("niceOrder")) NICE_ORDER.add(BY_SYMBOL.get(name.getAsString()));
        SOURCE.getAsJsonObject("renames").entrySet().forEach(entry -> RENAMES.put(entry.getKey(), entry.getValue().getAsString()));
        if (NICE_ORDER.size() != BY_ID.size()) throw new IllegalStateException("Original fluid nice order and ID registry differ");
    }

    public static final HbmFluidType NONE = symbol("NONE");
    public static final HbmFluidType WATER = symbol("WATER");
    public static final HbmFluidType STEAM = symbol("STEAM");
    public static final HbmFluidType HOTSTEAM = symbol("HOTSTEAM");
    public static final HbmFluidType SUPERHOTSTEAM = symbol("SUPERHOTSTEAM");
    public static final HbmFluidType ULTRAHOTSTEAM = symbol("ULTRAHOTSTEAM");
    public static final HbmFluidType COOLANT = symbol("COOLANT");
    public static final HbmFluidType LAVA = symbol("LAVA");
    public static final HbmFluidType DEUTERIUM = symbol("DEUTERIUM");
    public static final HbmFluidType TRITIUM = symbol("TRITIUM");
    public static final HbmFluidType OIL = symbol("OIL");
    public static final HbmFluidType HOTOIL = symbol("HOTOIL");
    public static final HbmFluidType HEAVYOIL = symbol("HEAVYOIL");
    public static final HbmFluidType BITUMEN = symbol("BITUMEN");
    public static final HbmFluidType SMEAR = symbol("SMEAR");
    public static final HbmFluidType HEATINGOIL = symbol("HEATINGOIL");
    public static final HbmFluidType RECLAIMED = symbol("RECLAIMED");
    public static final HbmFluidType PETROIL = symbol("PETROIL");
    public static final HbmFluidType LUBRICANT = symbol("LUBRICANT");
    public static final HbmFluidType NAPHTHA = symbol("NAPHTHA");
    public static final HbmFluidType DIESEL = symbol("DIESEL");
    public static final HbmFluidType LIGHTOIL = symbol("LIGHTOIL");
    public static final HbmFluidType KEROSENE = symbol("KEROSENE");
    public static final HbmFluidType GAS = symbol("GAS");
    public static final HbmFluidType PETROLEUM = symbol("PETROLEUM");
    public static final HbmFluidType LPG = symbol("LPG");
    public static final HbmFluidType BIOGAS = symbol("BIOGAS");
    public static final HbmFluidType BIOFUEL = symbol("BIOFUEL");
    public static final HbmFluidType NITAN = symbol("NITAN");
    public static final HbmFluidType UF6 = symbol("UF6");
    public static final HbmFluidType PUF6 = symbol("PUF6");
    public static final HbmFluidType SAS3 = symbol("SAS3");
    public static final HbmFluidType SCHRABIDIC = symbol("SCHRABIDIC");
    public static final HbmFluidType AMAT = symbol("AMAT");
    public static final HbmFluidType ASCHRAB = symbol("ASCHRAB");
    public static final HbmFluidType PEROXIDE = symbol("PEROXIDE");
    public static final HbmFluidType WATZ = symbol("WATZ");
    public static final HbmFluidType CRYOGEL = symbol("CRYOGEL");
    public static final HbmFluidType HYDROGEN = symbol("HYDROGEN");
    public static final HbmFluidType OXYGEN = symbol("OXYGEN");
    public static final HbmFluidType XENON = symbol("XENON");
    public static final HbmFluidType BALEFIRE = symbol("BALEFIRE");
    public static final HbmFluidType MERCURY = symbol("MERCURY");
    public static final HbmFluidType PAIN = symbol("PAIN");
    public static final HbmFluidType WASTEFLUID = symbol("WASTEFLUID");
    public static final HbmFluidType WASTEGAS = symbol("WASTEGAS");
    public static final HbmFluidType GASOLINE = symbol("GASOLINE");
    public static final HbmFluidType COALGAS = symbol("COALGAS");
    public static final HbmFluidType SPENTSTEAM = symbol("SPENTSTEAM");
    public static final HbmFluidType FRACKSOL = symbol("FRACKSOL");
    public static final HbmFluidType PLASMA_DT = symbol("PLASMA_DT");
    public static final HbmFluidType PLASMA_HD = symbol("PLASMA_HD");
    public static final HbmFluidType PLASMA_HT = symbol("PLASMA_HT");
    public static final HbmFluidType PLASMA_XM = symbol("PLASMA_XM");
    public static final HbmFluidType PLASMA_BF = symbol("PLASMA_BF");
    public static final HbmFluidType CARBONDIOXIDE = symbol("CARBONDIOXIDE");
    public static final HbmFluidType PLASMA_DH3 = symbol("PLASMA_DH3");
    public static final HbmFluidType HELIUM3 = symbol("HELIUM3");
    public static final HbmFluidType DEATH = symbol("DEATH");
    public static final HbmFluidType ETHANOL = symbol("ETHANOL");
    public static final HbmFluidType HEAVYWATER = symbol("HEAVYWATER");
    public static final HbmFluidType CRACKOIL = symbol("CRACKOIL");
    public static final HbmFluidType COALOIL = symbol("COALOIL");
    public static final HbmFluidType HOTCRACKOIL = symbol("HOTCRACKOIL");
    public static final HbmFluidType NAPHTHA_CRACK = symbol("NAPHTHA_CRACK");
    public static final HbmFluidType LIGHTOIL_CRACK = symbol("LIGHTOIL_CRACK");
    public static final HbmFluidType DIESEL_CRACK = symbol("DIESEL_CRACK");
    public static final HbmFluidType AROMATICS = symbol("AROMATICS");
    public static final HbmFluidType UNSATURATEDS = symbol("UNSATURATEDS");
    public static final HbmFluidType SALIENT = symbol("SALIENT");
    public static final HbmFluidType XPJUICE = symbol("XPJUICE");
    public static final HbmFluidType ENDERJUICE = symbol("ENDERJUICE");
    public static final HbmFluidType PETROIL_LEADED = symbol("PETROIL_LEADED");
    public static final HbmFluidType GASOLINE_LEADED = symbol("GASOLINE_LEADED");
    public static final HbmFluidType COALGAS_LEADED = symbol("COALGAS_LEADED");
    public static final HbmFluidType SULFURIC_ACID = symbol("SULFURIC_ACID");
    public static final HbmFluidType COOLANT_HOT = symbol("COOLANT_HOT");
    public static final HbmFluidType MUG = symbol("MUG");
    public static final HbmFluidType MUG_HOT = symbol("MUG_HOT");
    public static final HbmFluidType WOODOIL = symbol("WOODOIL");
    public static final HbmFluidType COALCREOSOTE = symbol("COALCREOSOTE");
    public static final HbmFluidType SEEDSLURRY = symbol("SEEDSLURRY");
    public static final HbmFluidType NITRIC_ACID = symbol("NITRIC_ACID");
    public static final HbmFluidType SOLVENT = symbol("SOLVENT");
    public static final HbmFluidType BLOOD = symbol("BLOOD");
    public static final HbmFluidType BLOOD_HOT = symbol("BLOOD_HOT");
    public static final HbmFluidType SYNGAS = symbol("SYNGAS");
    public static final HbmFluidType OXYHYDROGEN = symbol("OXYHYDROGEN");
    public static final HbmFluidType RADIOSOLVENT = symbol("RADIOSOLVENT");
    public static final HbmFluidType CHLORINE = symbol("CHLORINE");
    public static final HbmFluidType HEAVYOIL_VACUUM = symbol("HEAVYOIL_VACUUM");
    public static final HbmFluidType REFORMATE = symbol("REFORMATE");
    public static final HbmFluidType LIGHTOIL_VACUUM = symbol("LIGHTOIL_VACUUM");
    public static final HbmFluidType SOURGAS = symbol("SOURGAS");
    public static final HbmFluidType XYLENE = symbol("XYLENE");
    public static final HbmFluidType HEATINGOIL_VACUUM = symbol("HEATINGOIL_VACUUM");
    public static final HbmFluidType DIESEL_REFORM = symbol("DIESEL_REFORM");
    public static final HbmFluidType DIESEL_CRACK_REFORM = symbol("DIESEL_CRACK_REFORM");
    public static final HbmFluidType KEROSENE_REFORM = symbol("KEROSENE_REFORM");
    public static final HbmFluidType REFORMGAS = symbol("REFORMGAS");
    public static final HbmFluidType COLLOID = symbol("COLLOID");
    public static final HbmFluidType PHOSGENE = symbol("PHOSGENE");
    public static final HbmFluidType MUSTARDGAS = symbol("MUSTARDGAS");
    public static final HbmFluidType IONGEL = symbol("IONGEL");
    public static final HbmFluidType OIL_COKER = symbol("OIL_COKER");
    public static final HbmFluidType NAPHTHA_COKER = symbol("NAPHTHA_COKER");
    public static final HbmFluidType GAS_COKER = symbol("GAS_COKER");
    public static final HbmFluidType EGG = symbol("EGG");
    public static final HbmFluidType CHOLESTEROL = symbol("CHOLESTEROL");
    public static final HbmFluidType ESTRADIOL = symbol("ESTRADIOL");
    public static final HbmFluidType FISHOIL = symbol("FISHOIL");
    public static final HbmFluidType SUNFLOWEROIL = symbol("SUNFLOWEROIL");
    public static final HbmFluidType NITROGLYCERIN = symbol("NITROGLYCERIN");
    public static final HbmFluidType REDMUD = symbol("REDMUD");
    public static final HbmFluidType CHLOROCALCITE_SOLUTION = symbol("CHLOROCALCITE_SOLUTION");
    public static final HbmFluidType CHLOROCALCITE_MIX = symbol("CHLOROCALCITE_MIX");
    public static final HbmFluidType CHLOROCALCITE_CLEANED = symbol("CHLOROCALCITE_CLEANED");
    public static final HbmFluidType POTASSIUM_CHLORIDE = symbol("POTASSIUM_CHLORIDE");
    public static final HbmFluidType CALCIUM_CHLORIDE = symbol("CALCIUM_CHLORIDE");
    public static final HbmFluidType CALCIUM_SOLUTION = symbol("CALCIUM_SOLUTION");
    public static final HbmFluidType SMOKE = symbol("SMOKE");
    public static final HbmFluidType SMOKE_LEADED = symbol("SMOKE_LEADED");
    public static final HbmFluidType SMOKE_POISON = symbol("SMOKE_POISON");
    public static final HbmFluidType HELIUM4 = symbol("HELIUM4");
    public static final HbmFluidType HEAVYWATER_HOT = symbol("HEAVYWATER_HOT");
    public static final HbmFluidType SODIUM = symbol("SODIUM");
    public static final HbmFluidType SODIUM_HOT = symbol("SODIUM_HOT");
    public static final HbmFluidType THORIUM_SALT = symbol("THORIUM_SALT");
    public static final HbmFluidType THORIUM_SALT_HOT = symbol("THORIUM_SALT_HOT");
    public static final HbmFluidType THORIUM_SALT_DEPLETED = symbol("THORIUM_SALT_DEPLETED");
    public static final HbmFluidType FULLERENE = symbol("FULLERENE");
    public static final HbmFluidType PHEROMONE = symbol("PHEROMONE");
    public static final HbmFluidType PHEROMONE_M = symbol("PHEROMONE_M");
    public static final HbmFluidType OIL_DS = symbol("OIL_DS");
    public static final HbmFluidType HOTOIL_DS = symbol("HOTOIL_DS");
    public static final HbmFluidType CRACKOIL_DS = symbol("CRACKOIL_DS");
    public static final HbmFluidType HOTCRACKOIL_DS = symbol("HOTCRACKOIL_DS");
    public static final HbmFluidType NAPHTHA_DS = symbol("NAPHTHA_DS");
    public static final HbmFluidType LIGHTOIL_DS = symbol("LIGHTOIL_DS");
    public static final HbmFluidType STELLAR_FLUX = symbol("STELLAR_FLUX");
    public static final HbmFluidType VITRIOL = symbol("VITRIOL");
    public static final HbmFluidType SLOP = symbol("SLOP");
    public static final HbmFluidType LEAD = symbol("LEAD");
    public static final HbmFluidType LEAD_HOT = symbol("LEAD_HOT");
    public static final HbmFluidType PERFLUOROMETHYL = symbol("PERFLUOROMETHYL");
    public static final HbmFluidType PERFLUOROMETHYL_COLD = symbol("PERFLUOROMETHYL_COLD");
    public static final HbmFluidType PERFLUOROMETHYL_HOT = symbol("PERFLUOROMETHYL_HOT");
    public static final HbmFluidType LYE = symbol("LYE");
    public static final HbmFluidType SODIUM_ALUMINATE = symbol("SODIUM_ALUMINATE");
    public static final HbmFluidType BAUXITE_SOLUTION = symbol("BAUXITE_SOLUTION");
    public static final HbmFluidType ALUMINA = symbol("ALUMINA");
    public static final HbmFluidType AIR = symbol("AIR");
    public static final HbmFluidType CONCRETE = symbol("CONCRETE");
    public static final HbmFluidType DHC = symbol("DHC");
    public static final HbmFluidType AIRBLAST = symbol("AIRBLAST");
    public static final HbmFluidType FLUE = symbol("FLUE");
    public static final HbmFluidType TITANIUM_TETRACHLORIDE = symbol("TITANIUM_TETRACHLORIDE");
    @Deprecated public static final HbmFluidType ACID = PEROXIDE;

    private HbmFluids() {}

    /** Common initialization hook; call once from the mod's constructor. */
    public static void register() { register(FMLPaths.CONFIGDIR.get().resolve("hbmConfig")); }
    public static void register(Path folder) {
        if (configFolder != null) return;
        configFolder = folder;
        reloadFluids();
    }

    public static void reloadFluids() {
        if (configFolder == null) configFolder = FMLPaths.CONFIGDIR.get().resolve("hbmConfig");
        Path types = configFolder.resolve("hbmFluidTypes.json");
        try {
            Files.createDirectories(configFolder);
            if (!Files.exists(types)) write(types, SOURCE.getAsJsonObject("defaultCustom"));
        } catch (IOException error) {
            HbmReloaded.LOGGER.error("Cannot initialize custom NTM fluid configuration", error);
            return;
        }
        removeForReload(CUSTOM);
        removeForReload(FOREIGN);
        readCustomFluids(types);
        NICE_ORDER.addAll(CUSTOM);
        Path overrides = configFolder.resolve("hbmFluidTraits.json");
        if (Files.exists(overrides)) readTraits(overrides);
        else {
            try { exportTraits(configFolder.resolve("_hbmFluidTraits.json")); }
            catch (IOException error) { HbmReloaded.LOGGER.error("Cannot write NTM fluid trait template", error); }
        }
        List.copyOf(additionalListeners).forEach(Runnable::run);
        NICE_ORDER.addAll(FOREIGN);
    }

    private static void removeForReload(List<HbmFluidType> types) {
        for (var type : types) {
            MIGRATION.put(type.getName(), type);
            BY_ID.remove(type.getID());
            BY_NAME.remove(type.getName());
            REGISTER_ORDER.remove(type);
            NICE_ORDER.remove(type);
        }
        types.clear();
    }

    private static void readCustomFluids(Path path) {
        try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            for (var entry : JsonParser.parseReader(reader).getAsJsonObject().entrySet()) {
                HbmFluidType type = MIGRATION.get(entry.getKey());
                if (type == null) type = new HbmFluidType();
                type.setupCustom(entry.getKey(), entry.getValue().getAsJsonObject());
                add(type);
                CUSTOM.add(type);
            }
        } catch (IOException | RuntimeException error) { HbmReloaded.LOGGER.error("Cannot read custom NTM fluid types from {}", path, error); }
    }

    /** Foreign integrations can reuse their previous instance after a reload. */
    public static HbmFluidType registerForeign(String name, JsonObject customFields) {
        HbmFluidType type = MIGRATION.get(name);
        if (type == null) type = new HbmFluidType();
        type.setupCustom(name, customFields);
        add(type);
        FOREIGN.add(type);
        return type;
    }

    public static void readTraits(Path path) {
        try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            var overrides = JsonParser.parseReader(reader).getAsJsonObject();
            for (var type : NICE_ORDER) {
                if (overrides.has(type.getName())) type.replaceTraits(overrides.getAsJsonObject(type.getName()));
            }
        } catch (IOException | RuntimeException error) { HbmReloaded.LOGGER.error("Cannot read NTM fluid trait overrides from {}", path, error); }
    }

    public static void exportTraits(Path path) throws IOException {
        var root = new JsonObject();
        for (var type : NICE_ORDER) root.add(type.getName(), type.traitsJson());
        write(path, root);
    }

    public static HbmFluidType fromID(int id) { return BY_ID.getOrDefault(id, BY_NAME.get("NONE")); }
    public static HbmFluidType fromName(String name) { return BY_NAME.getOrDefault(name, BY_NAME.get("NONE")); }
    public static HbmFluidType fromNameCompat(String name) { return fromName(RENAMES.getOrDefault(name, name)); }
    public static String toNameCompat(HbmFluidType type) {
        return RENAMES.entrySet().stream().filter(entry -> fromName(entry.getValue()) == type).map(Map.Entry::getKey).findFirst().orElse(type.getName());
    }
    public static HbmFluidType[] getAll() { return getInOrder(REGISTER_ORDER); }
    public static HbmFluidType[] getInNiceOrder() { return getInOrder(NICE_ORDER); }
    public static List<HbmFluidType> customFluids() { return List.copyOf(CUSTOM); }
    public static List<HbmFluidType> foreignFluids() { return List.copyOf(FOREIGN); }
    public static boolean isSmoke(HbmFluidType type) { return type == SMOKE || type == SMOKE_POISON || type == SMOKE_LEADED; }

    private static void add(HbmFluidType type) { BY_ID.put(type.getID(), type); BY_NAME.put(type.getName(), type); REGISTER_ORDER.add(type); }
    private static HbmFluidType[] getInOrder(List<HbmFluidType> order) {
        var result = new HbmFluidType[BY_ID.size()];
        for (int index = 0; index < result.length; index++) {
            var type = order.get(index);
            if (type == null) throw new IllegalStateException("Missing NTM fluid in original order at index " + index);
            result[index] = type;
        }
        return result;
    }
    private static HbmFluidType symbol(String symbol) { return BY_SYMBOL.get(symbol); }
    private static void write(Path path, JsonObject value) throws IOException {
        try (var writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) { GSON.toJson(value, writer); writer.write('\n'); }
    }
    private static JsonObject readSource() {
        try (var stream = HbmFluids.class.getResourceAsStream("/hbm/fluids.json")) {
            if (stream == null) throw new IllegalStateException("Missing fluids.json: run tools/port_fluids.py");
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException error) { throw new ExceptionInInitializerError(error); }
    }
}
