package com.hbm.client.network.rtty.autocal;
import com.hbm.client.gui.LegacyScreen;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.network.rtty.autocal.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
/** Exact170x138 original standalone screen and explicit local-file upload/document workflow. */
public final class AutocalScreen extends LegacyScreen {
 private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/machine/gui_rtty_autocal.png");private final AutocalEntity autocal;private int left,top;
 private static final Map<String,String> MESSAGES=Map.ofEntries(Map.entry("Program has terminated","terminated"),Map.entry("Program index is out of bounds","index_oob"),Map.entry("Program requested shutdown","shutdown"),Map.entry("Unrecognized command","unrecognized"),Map.entry("Parameter missing","missing_parameter"),Map.entry("Parameter out of bounds","parameter_oob"),Map.entry("Error parsing parameter","parse_error"),Map.entry("Missing JMP destination","missing_jump"),Map.entry("Arithmetic error","arithmetic"),Map.entry("Buffer empty","buffer_empty"),Map.entry("Undefined behavior","undefined"),Map.entry("Stack exceeded capacity","stack_full"),Map.entry("Evaluation unsuccessful","evaluation"),Map.entry("User requested shutdown","user_shutdown"),Map.entry("Script has changed","changed_script"));
 public AutocalScreen(AutocalEntity autocal){super(Component.empty());this.autocal=autocal;}@Override protected void init(){font=LegacyFonts.get();left=(width-170)/2;top=(height-138)/2;}
 private boolean at(double x,double y,int ox){return left+ox<=x&&x<left+ox+18&&top+36<y&&y<=top+54;}
 private Component message(String line){if(line.startsWith("Buffer: "))return Component.translatable("gui.hbm.autocal.buffer",line.substring(8));String key=MESSAGES.get(line);return key==null?Component.literal(line):Component.translatable("gui.hbm.autocal.message."+key);}
 @Override public void render(GuiGraphics graphics,int x,int y,float partial){if(autocal.isRemoved()){onClose();return;}renderBackground(graphics,x,y,partial);graphics.blit(TEXTURE,left,top,0F,0F,170,138,256,256);if(autocal.isOn)graphics.blit(TEXTURE,left+8,top+36,170F,0F,18,18,256,256);if(!autocal.ignoreError)graphics.blit(TEXTURE,left+28,top+36,170F,18F,18,18,256,256);if(!autocal.autoReboot)graphics.blit(TEXTURE,left+48,top+36,170F,36F,18,18,256,256);for(int i=0;i<autocal.history.length;i++){String line=autocal.history[i];if(line!=null&&!line.isEmpty())graphics.drawString(font,message(line),left+7,top+73+10*i,0x00ff00,false);}
  int[] offsets={8,28,48,84,104,124,144};String[] names={"on","ignore","auto","upload","open","download","docs"};for(int i=0;i<offsets.length;i++)if(at(x,y,offsets[i])){var lines=new ArrayList<Component>();lines.add(Component.translatable("gui.hbm.autocal."+names[i]+".title").withStyle(i<3?net.minecraft.ChatFormatting.RED:net.minecraft.ChatFormatting.BLUE));int count=i==1?4:i==2?3:i==5?1:0;for(int j=0;j<count;j++)lines.add(Component.translatable("gui.hbm.autocal."+names[i]+".desc"+j).withStyle(i==5?net.minecraft.ChatFormatting.RED:net.minecraft.ChatFormatting.WHITE));graphics.renderComponentTooltip(font,lines,x,y);}
 }
 private Path folder()throws java.io.IOException{Path folder=minecraft.gameDirectory.toPath().resolve("hbmComputerUpload");Files.createDirectories(folder);return folder;}
 private void open(Path file){Util.getPlatform().openUri(file.toUri());}
 @Override public boolean mouseClicked(double x,double y,int button){CompoundTag data=null;if(at(x,y,8)){data=new CompoundTag();data.putBoolean("on",true);}if(at(x,y,28)){data=new CompoundTag();data.putBoolean("ignore",true);}if(at(x,y,48)){data=new CompoundTag();data.putBoolean("auto",true);}
  if(at(x,y,104))try{Path file=folder().resolve("script.txt");if(!Files.exists(file))Files.createFile(file);file.toFile().setExecutable(false);open(file);}catch(Throwable error){com.mojang.logging.LogUtils.getLogger().error("Couldn't open AUTOCAL program file",error);}
  if(at(x,y,144))try{Path file=folder().resolve("documentation_v1.1.md");if(!Files.exists(file)){var resource=minecraft.getResourceManager().getResourceOrThrow(ResourceLocation.parse("hbm:docs/autocal_v1.1.md"));try(var input=resource.open()){Files.copy(input,file);}}open(file);}catch(Throwable error){com.mojang.logging.LogUtils.getLogger().error("Couldn't open AUTOCAL documentation",error);}
  if(at(x,y,84))try{Path file=folder().resolve("script.txt");if(!Files.exists(file)){Files.createFile(file);file.toFile().setExecutable(false);return true;}data=new CompoundTag();data.putString("payload",Files.readString(file,StandardCharsets.UTF_8));}catch(Throwable ignored){}
  // The original Download button is explicitly unsupported and has no action; do not introduce server-to-file writing.
  if(data!=null){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));PacketDistributor.sendToServer(new AutocalControl(autocal.getBlockPos(),data));return true;}return at(x,y,84)||at(x,y,104)||at(x,y,124)||at(x,y,144);
 }
 @Override public boolean keyPressed(int key,int scan,int modifiers){if(key==GLFW.GLFW_KEY_ESCAPE||minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return false;}@Override public boolean isPauseScreen(){return false;}
}
