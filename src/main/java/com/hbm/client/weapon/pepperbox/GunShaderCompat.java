package com.hbm.client.weapon.pepperbox;

import com.hbm.HbmReloaded;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
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
    private record IrisModels(Method manager,Method pipeline,Class<?> pipelineType,Method active,
                              Method shaderMap,Method shader,Object brightHand,Object brightEntity,
                              java.lang.reflect.Field shadow) {}
    private static final class OptionalModels { private static final IrisModels VALUE=loadModels(); }
    private static IrisModels loadModels() {
        try {
            var pipelineType=Class.forName("net.irisshaders.iris.pipeline.ShaderRenderingPipeline");
            var key=Class.forName("net.irisshaders.iris.pipeline.programs.ShaderKey");
            return new IrisModels(Class.forName("net.irisshaders.iris.Iris").getMethod("getPipelineManager"),
                    Class.forName("net.irisshaders.iris.pipeline.PipelineManager").getMethod("getPipelineNullable"),
                    pipelineType,pipelineType.getMethod("shouldOverrideShaders"),pipelineType.getMethod("getShaderMap"),
                    Class.forName("net.irisshaders.iris.pipeline.programs.ShaderMap").getMethod("getShader",key),
                    key.getField("HAND_CUTOUT_BRIGHT").get(null),key.getField("ENTITIES_EYES_TRANS").get(null),
                    Class.forName("net.irisshaders.iris.shadows.ShadowRenderer").getField("ACTIVE"));
        } catch(ClassNotFoundException absent) { return null; }
        catch(ReflectiveOperationException|LinkageError unavailable) { warn(unavailable);return null; }
    }
    /** 实际光影实体手持阶段：普通部件受光，原全亮部件使用同一原生 Hand 程序的全亮变体。 */
    public static ShaderInstance modelShader(boolean fullbright,float cutoff) {
        // 本组两件物品的源主体阈值均为0.1；不把 NI4NI 的独立 alpha>0 分支偷换成0.1。
        if(cutoff!=.1F || !shaderPackInUse())return null;
        var iris=OptionalModels.VALUE;if(iris==null)return null;
        try {
            // 只缓存反射契约；每次读取当前 pipeline/map，光影切换或重载后不持有已关闭的 shader。
            // Shadow 原生 getter 本身优先判断 ShadowRenderer.ACTIVE，不依赖实体阶段 active 判断。
            if(iris.shadow.getBoolean(null))return GameRenderer.getRendertypeEntityCutoutNoCullShader();
            var pipeline=iris.pipeline.invoke(iris.manager.invoke(null));
            if(!iris.pipelineType.isInstance(pipeline) || !(boolean)iris.active.invoke(pipeline))return null;
            // 阴影阶段只需真实裁剪与深度；交给现有原生 getter 选择当前 shadow 程序。
            if(!fullbright)return GameRenderer.getRendertypeEntityCutoutNoCullShader();
            if(handState()!=IRIS_SOLID)return null;
            var value=iris.shader.invoke(iris.shaderMap.invoke(pipeline),iris.brightHand);
            return value instanceof ShaderInstance shader?shader:null;
        } catch(ReflectiveOperationException|LinkageError unavailable) { warn(unavailable);return null; }
    }
    /** 世界/第三人称全亮部件；只有真正实体阶段使用，GUI、手部和 shadow 均不进入此 draw。 */
    public static ShaderInstance unlitWorldModelShader(float cutoff) {
        if(cutoff!=.1F || !shaderPackInUse() || handState()!=VANILLA)return null;
        var iris=OptionalModels.VALUE;if(iris==null)return null;
        try {
            if(iris.shadow.getBoolean(null))return null;
            var pipeline=iris.pipeline.invoke(iris.manager.invoke(null));
            if(!iris.pipelineType.isInstance(pipeline) || !(boolean)iris.active.invoke(pipeline))return null;
            var value=iris.shader.invoke(iris.shaderMap.invoke(pipeline),iris.brightEntity);
            return value instanceof ShaderInstance shader?shader:null;
        } catch(ReflectiveOperationException|LinkageError unavailable) { warn(unavailable);return null; }
    }
    private GunShaderCompat() {}
}
