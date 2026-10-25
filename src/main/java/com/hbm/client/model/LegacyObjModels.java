package com.hbm.client.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Client-only cache; register INSTANCE with RegisterClientReloadListenersEvent. */
public final class LegacyObjModels implements PreparableReloadListener {
    public static final LegacyObjModels INSTANCE = new LegacyObjModels();
    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyObjModels.class);
    private final Map<ResourceLocation, WavefrontMesh> meshes = new ConcurrentHashMap<>();

    record PreparedReload(Map<ResourceLocation, WavefrontMesh> meshes, LegacyObjRenderer.ReloadCache renderer) { }

    private LegacyObjModels() { }

    public WavefrontMesh get(ResourceManager resources, ResourceLocation location) {
        return meshes.computeIfAbsent(location, key -> load(resources, key));
    }

    private static WavefrontMesh load(ResourceManager resources, ResourceLocation location) {
        try (var input = resources.open(location); var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            return WavefrontMeshLoader.load(reader, location.toString());
        } catch (IOException exception) {
            throw new UncheckedIOException("Cannot load original HBM model " + location, exception);
        }
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resources,
            ProfilerFiller preparationProfiler, ProfilerFiller reloadProfiler,
            Executor preparationExecutor, Executor reloadExecutor) {
        var models = CompletableFuture.supplyAsync(() -> prepare(resources), preparationExecutor);
        // Wait for TextureManager's own reload before adding new textures, so it cannot reset them a second time.
        return models.thenCompose(barrier::wait)
                .thenComposeAsync(prepared -> preloadTextures(resources, preparationExecutor)
                        .thenRunAsync(() -> apply(prepared), reloadExecutor), reloadExecutor);
    }

    private static CompletableFuture<Void> preloadTextures(ResourceManager resources, Executor preparationExecutor) {
        var manager = Minecraft.getInstance().getTextureManager();
        var futures = new ArrayList<CompletableFuture<Void>>();
        for (var location : resources.listResources("textures", key -> key.getNamespace().equals("hbm")
                && key.getPath().endsWith(".png") && (key.getPath().startsWith("textures/models/")
                || key.getPath().startsWith("textures/armor/") || key.getPath().startsWith("textures/entity/"))).keySet()) {
            futures.add(manager.preload(location, preparationExecutor));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private PreparedReload prepare(ResourceManager resources) {
        // Resource packs have already selected each winning resource. This phase uses no renderer or GL state.
        Map<ResourceLocation, WavefrontMesh> loaded = new ConcurrentHashMap<>();
        for (var location : resources.listResources("models", key -> key.getNamespace().equals("hbm")
                // X5846 contains this unused, malformed source asset; keep it available only on demand.
                && !key.getPath().equals("models/weapons/.obj")
                && (key.getPath().endsWith(".obj") || key.getPath().endsWith(".hmf"))).keySet()) {
            try {
                loaded.put(location, load(resources, location));
            } catch (UncheckedIOException exception) {
                // An unused invalid resource must keep its existing on-demand error semantics.
                LOGGER.warn("Cannot preload original HBM model {}; retaining on-demand loading", location, exception);
            }
        }
        return new PreparedReload(Map.copyOf(loaded), LegacyObjRenderer.prepareReload(loaded.values()));
    }

    private void apply(PreparedReload prepared) {
        meshes.clear();
        meshes.putAll(prepared.meshes());
        LegacyObjRenderer.installReload(prepared.renderer());
        LegacyContactShadows.clear();
    }
}
