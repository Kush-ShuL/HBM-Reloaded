package com.hbm.client.pedestal;
import com.hbm.client.gui.LegacyScreen;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.pedestal.*;
import java.util.Random;
import net.minecraft.core.component.DataComponents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
/** Actual142x84 source clay-tablet screen, seed-selected recipe and unchanged per-slot revelation draws. */
public final class ClayTabletScreen extends LegacyScreen {
    static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/guide_pedestal.png");
    private final InteractionHand hand;private int left,top;
    public ClayTabletScreen(InteractionHand hand){super(Component.translatable("item.hbm.clay_tablet"));this.hand=hand;}
    @Override protected void init(){font=LegacyFonts.get();left=(width-142)/2;top=(height-84)/2;}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){
        renderBackground(graphics,mouseX,mouseY,partial);if(minecraft.player==null||minecraft.level==null)return;
        var held=minecraft.player.getItemInHand(hand);int metadata=held.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0),icon=metadata==1?16:0;float reveal=metadata==1?.25F:.5F;
        graphics.blit(TEXTURE,left,top,0,metadata==1?84:0,142,84,256,256);
        var recipes=PedestalRecipeConfig.recipes(minecraft.level.getRecipeManager()).stream().filter(recipe->Math.abs(recipe.recipeSet())%2==Math.abs(metadata)%2).toList();
        var data=held.get(DataComponents.CUSTOM_DATA);
        if(data==null||!data.copyTag().contains("tabletSeed")||recipes.isEmpty()){
            for(int row=0;row<3;row++)for(int col=0;col<3;col++)graphics.blit(TEXTURE,left+7+col*27,top+7+row*27,142+icon,16,16,16,256,256);return;
        }
        var random=new Random(data.copyTag().getLong("tabletSeed"));var recipe=recipes.get(random.nextInt(recipes.size()));
        int condition=switch(recipe.extra()){case FULL_MOON->32;case NEW_MOON->48;case SUN->64;default->-1;};
        if(condition>=0)graphics.blit(TEXTURE,left+120,top+62,142+icon,condition,16,16,256,256);
        for(int row=0;row<3;row++)for(int col=0;col<3;col++){
            int x=left+7+col*27,y=top+7+row*27;
            if(random.nextFloat()>reveal){graphics.blit(TEXTURE,x,y,142+icon,16,16,16,256,256);continue;}
            var ingredient=recipe.inputs().get(col+row*3);
            if(ingredient.count()==0){graphics.blit(TEXTURE,x,y,142+icon,0,16,16,256,256);continue;}
            var variants=ingredient.examples();
            var display=variants.isEmpty()?new net.minecraft.world.item.ItemStack(com.hbm.registry.HbmContent.item("nothing")):variants.get((int)Math.floorMod(System.currentTimeMillis()/1000,variants.size()));graphics.renderItem(display,x,y);graphics.renderItemDecorations(font,display,x,y,display.getCount()>1?Integer.toString(display.getCount()):null);
        }
        graphics.renderItem(recipe.result(),left+142/2-8,top-20);graphics.renderItemDecorations(font,recipe.result(),left+142/2-8,top-20,recipe.result().getCount()>1?Integer.toString(recipe.result().getCount()):null);
        String label=recipe.result().getHoverName().getString();graphics.drawString(font,label,left+(142-font.width(label))/2,top-30,0xffffff,false);
    }
    @Override public boolean keyPressed(int key,int scan,int modifiers){if(key==256||minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return false;}
    @Override public boolean isPauseScreen(){return false;}
}
