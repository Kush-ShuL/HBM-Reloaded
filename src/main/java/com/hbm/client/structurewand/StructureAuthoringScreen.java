package com.hbm.client.structurewand;

import com.hbm.client.gui.LegacyScreen;
import com.hbm.item.structurewand.StructureWandPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/** Source standalone save/load forms: identical field/button/list bounds and commit-on-close behavior. */
public final class StructureAuthoringScreen extends LegacyScreen {
    private final StructureWandPackets.Open opened;
    private EditBox name,sizeX,sizeY,sizeZ;
    private FileList files;
    private boolean perform,sent;
    public StructureAuthoringScreen(StructureWandPackets.Open opened){super(Component.translatable(opened.loading()?"tile.wand_structure.load.name":"tile.wand_structure.save.name"));this.opened=opened;}
    private EditBox field(int x,int y,int width,String value){var box=new EditBox(font,x,y,width,20,Component.empty());box.setValue(value);return addRenderableWidget(box);}
    @Override protected void init(){
        name=field(width/2-150,50,300,opened.settings().getString("name"));
        if(opened.loading()){
            files=addRenderableWidget(new FileList(minecraft,width,height-160,70));
            files.filter(name.getValue());name.setResponder(files::filter);
        }else{
            sizeX=field(width/2-150,100,50,Integer.toString(opened.settings().getInt("sizeX")));
            sizeY=field(width/2-100,100,50,Integer.toString(opened.settings().getInt("sizeY")));
            sizeZ=field(width/2-50,100,50,Integer.toString(opened.settings().getInt("sizeZ")));
        }
        addRenderableWidget(Button.builder(Component.translatable(opened.loading()?"gui.hbm.structure_wand.load":"gui.hbm.structure_wand.save"),button->{perform=true;onClose();}).bounds(width/2-150,opened.loading()?height-70:150,300,20).build());
    }
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){
        if(!opened.loading())renderBackground(graphics,mouseX,mouseY,partial);
        // Source load list draws behind its text field and button.
        if(files!=null)files.render(graphics,mouseX,mouseY,partial);
        name.render(graphics,mouseX,mouseY,partial);
        for(var renderable:renderables)if(renderable!=files&&renderable!=name)renderable.render(graphics,mouseX,mouseY,partial);
    }
    private static void dimension(net.minecraft.nbt.CompoundTag tag,String key,EditBox field){try{tag.putInt(key,Integer.parseInt(field.getValue()));}catch(NumberFormatException ignored){}}
    private void commit(){
        if(sent||name==null)return;sent=true;var tag=opened.settings().copy();tag.putString("name",name.getValue());
        if(!opened.loading()){dimension(tag,"sizeX",sizeX);dimension(tag,"sizeY",sizeY);dimension(tag,"sizeZ",sizeZ);}
        PacketDistributor.sendToServer(new StructureWandPackets.Control(opened.position(),opened.token(),tag,perform&&!opened.loading(),perform&&opened.loading()));
    }
    @Override public void removed(){commit();super.removed();}
    @Override public boolean isPauseScreen(){return false;}
    private final class FileList extends ObjectSelectionList<FileEntry> {
        FileList(Minecraft client,int width,int height,int top){super(client,width,Math.max(0,height),top,16);}
        void filter(String text){clearEntries();setScrollAmount(0);for(String file:opened.files())if(file.contains(text)){var entry=new FileEntry(file);addEntry(entry);if(file.equals(text+".nbt"))setSelected(entry);}}
    }
    private final class FileEntry extends ObjectSelectionList.Entry<FileEntry> {
        private final String file;
        FileEntry(String file){this.file=file;}
        @Override public Component getNarration(){return Component.literal(file);}
        @Override public void render(GuiGraphics graphics,int index,int y,int x,int rowWidth,int rowHeight,int mouseX,int mouseY,boolean hovered,float partial){graphics.drawString(font,file,x+20,y+1,0xffffff,true);}
        @Override public boolean mouseClicked(double x,double y,int button){files.setSelected(this);name.setValue(file.substring(0,file.length()-4));return true;}
    }
}
