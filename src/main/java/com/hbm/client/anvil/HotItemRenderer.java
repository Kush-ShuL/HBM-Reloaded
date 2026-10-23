package com.hbm.client.anvil;
import com.hbm.machine.anvil.HotSmithingItem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** Original inventory-only hot overlay alpha, unlit; all other contexts use the ordinary source base icon. */
public final class HotItemRenderer extends BlockEntityWithoutLevelRenderer{
    static final Map<Item,BakedModel> BASE=new HashMap<>();
    public HotItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext context,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var base=BASE.get(stack.getItem());if(base==null)return;pose.pushPose();pose.translate(.5,.5,.5);Minecraft.getInstance().getItemRenderer().render(stack,context,false,pose,buffers,light,overlay,base);pose.popPose();float heat=(float)HotSmithingItem.heat(stack);if(heat<=0||context!=ItemDisplayContext.GUI||!(stack.getItem() instanceof HotSmithingItem item))return;VertexConsumer vertices=buffers.getBuffer(RenderType.entityTranslucentEmissive(ResourceLocation.fromNamespaceAndPath("hbm","textures/item/"+item.symbol()+"_hot.png")));float z=.53135F;vertex(vertices,pose,0,0,z,0,1,heat,overlay);vertex(vertices,pose,1,0,z,1,1,heat,overlay);vertex(vertices,pose,1,1,z,1,0,heat,overlay);vertex(vertices,pose,0,1,z,0,0,heat,overlay);}
    private static void vertex(VertexConsumer consumer,PoseStack pose,float x,float y,float z,float u,float v,float alpha,int overlay){consumer.addVertex(pose.last(),x,y,z).setColor(1,1,1,alpha).setUv(u,v).setOverlay(overlay).setLight(LightTexture.FULL_BRIGHT).setNormal(pose.last(),0,0,1);}
}
