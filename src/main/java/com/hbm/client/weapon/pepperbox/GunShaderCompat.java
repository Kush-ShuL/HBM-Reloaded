package com.hbm.client.weapon.pepperbox;

import com.hbm.HbmReloaded;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

/** Optional Iris hand integration. No shader pack, global Iris flag or world-effect renderer is changed. */
public final class GunShaderCompat {
    public static final int VANILLA=0, IRIS_SOLID=1, IRIS_TRANSLUCENT=2;
    private static boolean warned;
    private record Iris(Object api,Method pack,Object hand,Method active,Method solid,
                        Object captured,Method readItem,Method writeItem,Object settings,Method itemIds,
                        Constructor<?> namespacedId) {}
    private static final class OptionalIris { private static final Iris VALUE=load(); }
    private static Iris load() {
        try {
            var apiType=Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            var handType=Class.forName("net.irisshaders.iris.pathways.HandRenderer");
            var capturedType=Class.forName("net.irisshaders.iris.uniforms.CapturedRenderingState");
            var settingsType=Class.forName("net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings");
            return new Iris(apiType.getMethod("getInstance").invoke(null),apiType.getMethod("isShaderPackInUse"),
                    handType.getField("INSTANCE").get(null),handType.getMethod("isActive"),handType.getMethod("isRenderingSolid"),
                    capturedType.getField("INSTANCE").get(null),capturedType.getMethod("getCurrentRenderedItem"),capturedType.getMethod("setCurrentRenderedItem",int.class),
                    settingsType.getField("INSTANCE").get(null),settingsType.getMethod("getItemIds"),
                    Class.forName("net.irisshaders.iris.shaderpack.materialmap.NamespacedId").getConstructor(String.class,String.class));
        } catch(ClassNotFoundException absent) { return null; }
        catch(ReflectiveOperationException|LinkageError unavailable) { warn(unavailable);return null; }
    }
    private static void warn(Throwable failure) { if(!warned){warned=true;HbmReloaded.LOGGER.warn("Cannot use this Iris version's optional gun hand integration",failure);} }
    /** Same optional Iris API as the hand bridge; no pack/config/phase is changed. */
    public static boolean shaderPackInUse() {
        var iris=OptionalIris.VALUE;if(iris==null)return false;
        try { return (boolean)iris.pack.invoke(iris.api); }
        catch(ReflectiveOperationException failure) { warn(failure);return false; }
    }
    /** Opaque gun items belong only to Iris's solid hand call, even when the other hand holds glass. */
    public static int handState() {
        var iris=OptionalIris.VALUE;if(iris==null)return VANILLA;
        try {
            if(!(boolean)iris.pack.invoke(iris.api)||!(boolean)iris.active.invoke(iris.hand))return VANILLA;
            return (boolean)iris.solid.invoke(iris.hand)?IRIS_SOLID:IRIS_TRANSLUCENT;
        } catch(ReflectiveOperationException failure) { warn(failure);return VANILLA; }
    }
    /** Keep Iris's clip-depth compression and all other projection entries; change only the source weapon FOV. */
    public static Matrix4f projection(float fov,float aspect,float farPlane,boolean irisHand) {
        var desired=new Matrix4f().perspective((float)Math.toRadians(fov),aspect,.05F,farPlane);
        if(!irisHand)return desired;
        return new Matrix4f(RenderSystem.getProjectionMatrix()).m00(desired.m00()).m11(desired.m11());
    }
    public interface ItemScope extends AutoCloseable { @Override void close(); }
    private static final ItemScope EMPTY=()->{};
    /** RenderHandEvent bypasses ItemRenderer.render, so retain the same real shader-pack item.properties lookup. */
    public static ItemScope item(ItemStack stack,boolean irisHand) {
        var iris=OptionalIris.VALUE;if(!irisHand||iris==null)return EMPTY;
        try {
            var mapping=iris.itemIds.invoke(iris.settings);if(!(mapping instanceof Object2IntFunction<?> ids))return EMPTY;
            int previous=(int)iris.readItem.invoke(iris.captured);var id=BuiltInRegistries.ITEM.getKey(stack.getItem());
            int material=ids.getInt(iris.namespacedId.newInstance(id.getNamespace(),id.getPath()));
            iris.writeItem.invoke(iris.captured,material);
            return ()->{try{iris.writeItem.invoke(iris.captured,previous);}catch(ReflectiveOperationException failure){warn(failure);}};
        } catch(ReflectiveOperationException failure) { warn(failure);return EMPTY; }
    }
    private GunShaderCompat() {}
}
