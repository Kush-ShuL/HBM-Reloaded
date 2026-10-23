package com.hbm.client.structuretandem;

import com.hbm.client.gui.LegacyScreen;
import com.hbm.item.structuretandem.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/** Original standalone field/button layout; no fake container or modern background blur. */
public final class TandemScreen extends LegacyScreen {
    private final TandemPackets.Open opened;
    private EditBox pool,target;private Button joint;private boolean rollable,submitted;
    public TandemScreen(TandemPackets.Open opened){super(Component.translatable("block.hbm.wand_tandem"));this.opened=opened;rollable=opened.settings().getBoolean("roll");}
    @Override protected void init(){
        var current=opened.settings();if(minecraft.level!=null&&minecraft.level.getBlockEntity(opened.pos()) instanceof TandemEntity tile)current=tile.settings();rollable=current.getBoolean("roll");
        pool=new EditBox(font,width/2-150,50,300,20,Component.translatable("gui.hbm.wand_tandem.pool"));pool.setValue(current.getString("pool"));addRenderableWidget(pool);
        target=new EditBox(font,width/2+10,100,140,20,Component.translatable("gui.hbm.wand_tandem.target"));target.setValue(current.getString("target"));addRenderableWidget(target);
        joint=addRenderableWidget(new SilentJoint(width/2+60,150,90,20,jointName(),button->{rollable=!rollable;button.setMessage(jointName());}));
    }
    private Component jointName(){return Component.translatable(rollable?"gui.hbm.wand_tandem.rollable":"gui.hbm.wand_tandem.aligned");}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){
        renderBackground(graphics,mouseX,mouseY,partial);
        graphics.drawString(font,Component.translatable("gui.hbm.wand_tandem.pool"),width/2-150,37,0xA0A0A0,true);
        graphics.drawString(font,Component.translatable("gui.hbm.wand_tandem.target"),width/2+10,87,0xA0A0A0,true);
        graphics.drawString(font,Component.translatable("gui.hbm.wand_tandem.joint"),width/2+60,137,0xA0A0A0,true);
        super.render(graphics,mouseX,mouseY,partial);
    }
    @Override public boolean isPauseScreen(){return false;}
    private void submit(){
        if(submitted||pool==null||target==null)return;submitted=true;
        var settings=opened.settings().copy();
        // Source reads the current client BE when closing, including real updates from other players.
        if(minecraft.level!=null&&minecraft.level.getBlockEntity(opened.pos()) instanceof TandemEntity tile)settings=tile.settings();
        settings.putString("pool",pool.getValue());settings.putString("target",target.getValue());settings.putBoolean("roll",rollable);
        PacketDistributor.sendToServer(new TandemPackets.Control(opened.pos(),opened.token(),settings));
    }
    @Override public void onClose(){submit();super.onClose();}
    @Override public void removed(){submit();super.removed();}
    // Original jointToggle was manually handled rather than put in GuiScreen.buttonList: no press sound.
    private static final class SilentJoint extends Button {
        SilentJoint(int x,int y,int width,int height,Component title,OnPress press){super(x,y,width,height,title,press,DEFAULT_NARRATION);}
        @Override public void playDownSound(net.minecraft.client.sounds.SoundManager sound){}
    }
}
