package com.hbm.bobmazon;

import com.hbm.advancement.HbmAdvancements;
import com.hbm.damage.HbmDamageSources;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** Original ShadyUtil four-line puzzle and exact tainted-creeper/boxcar achievement recipients. */
public final class BobmazonUnlocks {
 private static final Set<String> HASHES=Set.of("41de5c372b0589bbdb80571e87efa95ea9e34b0d74c6005b8eab495b7afd9994","31da6223a100ed348ceb3254ceab67c9cc102cb2a04ac24de0df3ef3479b1036");
 @SubscribeEvent public void sign(PlayerInteractEvent.RightClickBlock event){
  if(event.getLevel().isClientSide)return;var clicked=event.getPos();
  // Exact X5846 ModEventHandler intentionally/actually swaps the clicked Y/Z before its sign lookup.
  var pos=new BlockPos(clicked.getX(),clicked.getZ(),clicked.getY());if(!(event.getLevel().getBlockEntity(pos) instanceof SignBlockEntity sign))return;
  var text=sign.getFrontText();var lines=new String[4];for(int i=0;i<4;i++)lines[i]=text.getMessage(i,false).getString();
  if(!HASHES.contains(smoosh(lines)))return;event.getLevel().destroyBlock(pos,false);var catalog=new ItemEntity(event.getLevel(),pos.getX(),pos.getY(),pos.getZ(),new ItemStack(BobmazonContent.HIDDEN.get()));catalog.setPickUpDelay(10);event.getLevel().addFreshEntity(catalog);
 }
 public static String smoosh(String... lines){
  if(lines.length!=4)throw new IllegalArgumentException("Source puzzle has exactly four lines");var bytes=new byte[4][];for(int i=0;i<4;i++){bytes[i]=lines[i].getBytes(StandardCharsets.UTF_8);if(bytes[i].length==0)return "";}
  var random=new Random();String result=lines[0];random.setSeed(bytes[0][0]);result+=random.nextInt(0xffffff)+lines[1];random.setSeed(random.nextInt(0xffffff)+bytes[1][0]);result+=random.nextInt(0xffffff)+lines[2];random.setSeed(random.nextInt(0xffffff)+bytes[2][0]);result+=random.nextInt(0xffffff)+lines[3];random.setSeed(random.nextInt(0xffffff)+bytes[3][0]);result+=random.nextInt(0xffffff);
  try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(result.getBytes(StandardCharsets.UTF_8)));}catch(NoSuchAlgorithmException impossible){throw new IllegalStateException(impossible);}
 }
 @SubscribeEvent public void death(LivingDeathEvent event){if(event.getEntity().level().isClientSide)return;if(event.getEntity() instanceof com.hbm.world.taint.TaintedCreeper&&event.getSource().is(HbmDamageSources.boxcar))HbmAdvancements.around(event.getEntity().level(),event.getEntity().getBoundingBox().inflate(50),"bobHidden");}
}
