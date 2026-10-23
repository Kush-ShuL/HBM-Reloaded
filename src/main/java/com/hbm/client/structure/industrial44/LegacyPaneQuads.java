package com.hbm.client.structure.industrial44;
import java.util.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
/** Generated from Mojang1.7.10 RenderBlocks.renderBlockPane type18; preserves all sheets/rims/UVs/.01 and .005 offsets. */
final class LegacyPaneQuads {
 static List<BakedQuad> bake(int key,TextureAtlasSprite face,TextureAtlasSprite rim){
  int var2=0,var3=0,var4=0;var var63=new Icon(face);var var64=new Icon(rim);var capture=new Capture(face);
      double var66 = var63.c();
      double var15 = var63.a(8.0);
      double var17 = var63.d();
      double var19 = var63.e();
      double var21 = var63.f();
      double var23 = var64.a(7.0);
      double var25 = var64.a(9.0);
      double var27 = var64.e();
      double var29 = var64.b(8.0);
      double var31 = var64.f();
      double var33 = var2;
      double var35 = var2 + 0.5;
      double var37 = var2 + 1;
      double var39 = var4;
      double var41 = var4 + 0.5;
      double var43 = var4 + 1;
      double var45 = var2 + 0.5 - 0.0625;
      double var47 = var2 + 0.5 + 0.0625;
      double var49 = var4 + 0.5 - 0.0625;
      double var51 = var4 + 0.5 + 0.0625;
      boolean var53 = (key & 1) != 0;
      boolean var54 = (key & 2) != 0;
      boolean var55 = (key & 4) != 0;
      boolean var56 = (key & 8) != 0;
      boolean var57 = (key & 16) != 0;
      boolean var58 = (key & 32) != 0;
      double var59 = 0.01;
      double var61 = 0.005;
      if ((!var55 || !var56) && (var55 || var56 || var53 || var54)) {
         if (var55 && !var56) {
            capture.vertex(var33, var3 + 1, var41, var66, var19);
            capture.vertex(var33, var3 + 0, var41, var66, var21);
            capture.vertex(var35, var3 + 0, var41, var15, var21);
            capture.vertex(var35, var3 + 1, var41, var15, var19);
            capture.vertex(var35, var3 + 1, var41, var66, var19);
            capture.vertex(var35, var3 + 0, var41, var66, var21);
            capture.vertex(var33, var3 + 0, var41, var15, var21);
            capture.vertex(var33, var3 + 1, var41, var15, var19);
            if (!var54 && !var53) {
               capture.vertex(var35, var3 + 1, var51, var23, var27);
               capture.vertex(var35, var3 + 0, var51, var23, var31);
               capture.vertex(var35, var3 + 0, var49, var25, var31);
               capture.vertex(var35, var3 + 1, var49, var25, var27);
               capture.vertex(var35, var3 + 1, var49, var23, var27);
               capture.vertex(var35, var3 + 0, var49, var23, var31);
               capture.vertex(var35, var3 + 0, var51, var25, var31);
               capture.vertex(var35, var3 + 1, var51, var25, var27);
            }

            if (var57 || (key & 16384) != 0 && (key & 64) != 0) {
               capture.vertex(var33, var3 + 1 + 0.01, var51, var25, var29);
               capture.vertex(var35, var3 + 1 + 0.01, var51, var25, var31);
               capture.vertex(var35, var3 + 1 + 0.01, var49, var23, var31);
               capture.vertex(var33, var3 + 1 + 0.01, var49, var23, var29);
               capture.vertex(var35, var3 + 1 + 0.01, var51, var25, var29);
               capture.vertex(var33, var3 + 1 + 0.01, var51, var25, var31);
               capture.vertex(var33, var3 + 1 + 0.01, var49, var23, var31);
               capture.vertex(var35, var3 + 1 + 0.01, var49, var23, var29);
            }

            if (var58 || (key & 32768) != 0 && (key & 1024) != 0) {
               capture.vertex(var33, var3 - 0.01, var51, var25, var29);
               capture.vertex(var35, var3 - 0.01, var51, var25, var31);
               capture.vertex(var35, var3 - 0.01, var49, var23, var31);
               capture.vertex(var33, var3 - 0.01, var49, var23, var29);
               capture.vertex(var35, var3 - 0.01, var51, var25, var29);
               capture.vertex(var33, var3 - 0.01, var51, var25, var31);
               capture.vertex(var33, var3 - 0.01, var49, var23, var31);
               capture.vertex(var35, var3 - 0.01, var49, var23, var29);
            }
         } else if (!var55 && var56) {
            capture.vertex(var35, var3 + 1, var41, var15, var19);
            capture.vertex(var35, var3 + 0, var41, var15, var21);
            capture.vertex(var37, var3 + 0, var41, var17, var21);
            capture.vertex(var37, var3 + 1, var41, var17, var19);
            capture.vertex(var37, var3 + 1, var41, var15, var19);
            capture.vertex(var37, var3 + 0, var41, var15, var21);
            capture.vertex(var35, var3 + 0, var41, var17, var21);
            capture.vertex(var35, var3 + 1, var41, var17, var19);
            if (!var54 && !var53) {
               capture.vertex(var35, var3 + 1, var49, var23, var27);
               capture.vertex(var35, var3 + 0, var49, var23, var31);
               capture.vertex(var35, var3 + 0, var51, var25, var31);
               capture.vertex(var35, var3 + 1, var51, var25, var27);
               capture.vertex(var35, var3 + 1, var51, var23, var27);
               capture.vertex(var35, var3 + 0, var51, var23, var31);
               capture.vertex(var35, var3 + 0, var49, var25, var31);
               capture.vertex(var35, var3 + 1, var49, var25, var27);
            }

            if (var57 || (key & 16384) != 0 && (key & 128) != 0) {
               capture.vertex(var35, var3 + 1 + 0.01, var51, var25, var27);
               capture.vertex(var37, var3 + 1 + 0.01, var51, var25, var29);
               capture.vertex(var37, var3 + 1 + 0.01, var49, var23, var29);
               capture.vertex(var35, var3 + 1 + 0.01, var49, var23, var27);
               capture.vertex(var37, var3 + 1 + 0.01, var51, var25, var27);
               capture.vertex(var35, var3 + 1 + 0.01, var51, var25, var29);
               capture.vertex(var35, var3 + 1 + 0.01, var49, var23, var29);
               capture.vertex(var37, var3 + 1 + 0.01, var49, var23, var27);
            }

            if (var58 || (key & 32768) != 0 && (key & 2048) != 0) {
               capture.vertex(var35, var3 - 0.01, var51, var25, var27);
               capture.vertex(var37, var3 - 0.01, var51, var25, var29);
               capture.vertex(var37, var3 - 0.01, var49, var23, var29);
               capture.vertex(var35, var3 - 0.01, var49, var23, var27);
               capture.vertex(var37, var3 - 0.01, var51, var25, var27);
               capture.vertex(var35, var3 - 0.01, var51, var25, var29);
               capture.vertex(var35, var3 - 0.01, var49, var23, var29);
               capture.vertex(var37, var3 - 0.01, var49, var23, var27);
            }
         }
      } else {
         capture.vertex(var33, var3 + 1, var41, var66, var19);
         capture.vertex(var33, var3 + 0, var41, var66, var21);
         capture.vertex(var37, var3 + 0, var41, var17, var21);
         capture.vertex(var37, var3 + 1, var41, var17, var19);
         capture.vertex(var37, var3 + 1, var41, var66, var19);
         capture.vertex(var37, var3 + 0, var41, var66, var21);
         capture.vertex(var33, var3 + 0, var41, var17, var21);
         capture.vertex(var33, var3 + 1, var41, var17, var19);
         if (var57) {
            capture.vertex(var33, var3 + 1 + 0.01, var51, var25, var31);
            capture.vertex(var37, var3 + 1 + 0.01, var51, var25, var27);
            capture.vertex(var37, var3 + 1 + 0.01, var49, var23, var27);
            capture.vertex(var33, var3 + 1 + 0.01, var49, var23, var31);
            capture.vertex(var37, var3 + 1 + 0.01, var51, var25, var31);
            capture.vertex(var33, var3 + 1 + 0.01, var51, var25, var27);
            capture.vertex(var33, var3 + 1 + 0.01, var49, var23, var27);
            capture.vertex(var37, var3 + 1 + 0.01, var49, var23, var31);
         } else {
            if ((key & 16384) != 0 && (key & 64) != 0) {
               capture.vertex(var33, var3 + 1 + 0.01, var51, var25, var29);
               capture.vertex(var35, var3 + 1 + 0.01, var51, var25, var31);
               capture.vertex(var35, var3 + 1 + 0.01, var49, var23, var31);
               capture.vertex(var33, var3 + 1 + 0.01, var49, var23, var29);
               capture.vertex(var35, var3 + 1 + 0.01, var51, var25, var29);
               capture.vertex(var33, var3 + 1 + 0.01, var51, var25, var31);
               capture.vertex(var33, var3 + 1 + 0.01, var49, var23, var31);
               capture.vertex(var35, var3 + 1 + 0.01, var49, var23, var29);
            }

            if ((key & 16384) != 0 && (key & 128) != 0) {
               capture.vertex(var35, var3 + 1 + 0.01, var51, var25, var27);
               capture.vertex(var37, var3 + 1 + 0.01, var51, var25, var29);
               capture.vertex(var37, var3 + 1 + 0.01, var49, var23, var29);
               capture.vertex(var35, var3 + 1 + 0.01, var49, var23, var27);
               capture.vertex(var37, var3 + 1 + 0.01, var51, var25, var27);
               capture.vertex(var35, var3 + 1 + 0.01, var51, var25, var29);
               capture.vertex(var35, var3 + 1 + 0.01, var49, var23, var29);
               capture.vertex(var37, var3 + 1 + 0.01, var49, var23, var27);
            }
         }

         if (var58) {
            capture.vertex(var33, var3 - 0.01, var51, var25, var31);
            capture.vertex(var37, var3 - 0.01, var51, var25, var27);
            capture.vertex(var37, var3 - 0.01, var49, var23, var27);
            capture.vertex(var33, var3 - 0.01, var49, var23, var31);
            capture.vertex(var37, var3 - 0.01, var51, var25, var31);
            capture.vertex(var33, var3 - 0.01, var51, var25, var27);
            capture.vertex(var33, var3 - 0.01, var49, var23, var27);
            capture.vertex(var37, var3 - 0.01, var49, var23, var31);
         } else {
            if ((key & 32768) != 0 && (key & 1024) != 0) {
               capture.vertex(var33, var3 - 0.01, var51, var25, var29);
               capture.vertex(var35, var3 - 0.01, var51, var25, var31);
               capture.vertex(var35, var3 - 0.01, var49, var23, var31);
               capture.vertex(var33, var3 - 0.01, var49, var23, var29);
               capture.vertex(var35, var3 - 0.01, var51, var25, var29);
               capture.vertex(var33, var3 - 0.01, var51, var25, var31);
               capture.vertex(var33, var3 - 0.01, var49, var23, var31);
               capture.vertex(var35, var3 - 0.01, var49, var23, var29);
            }

            if ((key & 32768) != 0 && (key & 2048) != 0) {
               capture.vertex(var35, var3 - 0.01, var51, var25, var27);
               capture.vertex(var37, var3 - 0.01, var51, var25, var29);
               capture.vertex(var37, var3 - 0.01, var49, var23, var29);
               capture.vertex(var35, var3 - 0.01, var49, var23, var27);
               capture.vertex(var37, var3 - 0.01, var51, var25, var27);
               capture.vertex(var35, var3 - 0.01, var51, var25, var29);
               capture.vertex(var35, var3 - 0.01, var49, var23, var29);
               capture.vertex(var37, var3 - 0.01, var49, var23, var27);
            }
         }
      }

      if ((!var53 || !var54) && (var55 || var56 || var53 || var54)) {
         if (var53 && !var54) {
            capture.vertex(var35, var3 + 1, var39, var66, var19);
            capture.vertex(var35, var3 + 0, var39, var66, var21);
            capture.vertex(var35, var3 + 0, var41, var15, var21);
            capture.vertex(var35, var3 + 1, var41, var15, var19);
            capture.vertex(var35, var3 + 1, var41, var66, var19);
            capture.vertex(var35, var3 + 0, var41, var66, var21);
            capture.vertex(var35, var3 + 0, var39, var15, var21);
            capture.vertex(var35, var3 + 1, var39, var15, var19);
            if (!var56 && !var55) {
               capture.vertex(var45, var3 + 1, var41, var23, var27);
               capture.vertex(var45, var3 + 0, var41, var23, var31);
               capture.vertex(var47, var3 + 0, var41, var25, var31);
               capture.vertex(var47, var3 + 1, var41, var25, var27);
               capture.vertex(var47, var3 + 1, var41, var23, var27);
               capture.vertex(var47, var3 + 0, var41, var23, var31);
               capture.vertex(var45, var3 + 0, var41, var25, var31);
               capture.vertex(var45, var3 + 1, var41, var25, var27);
            }

            if (var57 || (key & 16384) != 0 && (key & 256) != 0) {
               capture.vertex(var45, var3 + 1 + 0.005, var39, var25, var27);
               capture.vertex(var45, var3 + 1 + 0.005, var41, var25, var29);
               capture.vertex(var47, var3 + 1 + 0.005, var41, var23, var29);
               capture.vertex(var47, var3 + 1 + 0.005, var39, var23, var27);
               capture.vertex(var45, var3 + 1 + 0.005, var41, var25, var27);
               capture.vertex(var45, var3 + 1 + 0.005, var39, var25, var29);
               capture.vertex(var47, var3 + 1 + 0.005, var39, var23, var29);
               capture.vertex(var47, var3 + 1 + 0.005, var41, var23, var27);
            }

            if (var58 || (key & 32768) != 0 && (key & 4096) != 0) {
               capture.vertex(var45, var3 - 0.005, var39, var25, var27);
               capture.vertex(var45, var3 - 0.005, var41, var25, var29);
               capture.vertex(var47, var3 - 0.005, var41, var23, var29);
               capture.vertex(var47, var3 - 0.005, var39, var23, var27);
               capture.vertex(var45, var3 - 0.005, var41, var25, var27);
               capture.vertex(var45, var3 - 0.005, var39, var25, var29);
               capture.vertex(var47, var3 - 0.005, var39, var23, var29);
               capture.vertex(var47, var3 - 0.005, var41, var23, var27);
            }
         } else if (!var53 && var54) {
            capture.vertex(var35, var3 + 1, var41, var15, var19);
            capture.vertex(var35, var3 + 0, var41, var15, var21);
            capture.vertex(var35, var3 + 0, var43, var17, var21);
            capture.vertex(var35, var3 + 1, var43, var17, var19);
            capture.vertex(var35, var3 + 1, var43, var15, var19);
            capture.vertex(var35, var3 + 0, var43, var15, var21);
            capture.vertex(var35, var3 + 0, var41, var17, var21);
            capture.vertex(var35, var3 + 1, var41, var17, var19);
            if (!var56 && !var55) {
               capture.vertex(var47, var3 + 1, var41, var23, var27);
               capture.vertex(var47, var3 + 0, var41, var23, var31);
               capture.vertex(var45, var3 + 0, var41, var25, var31);
               capture.vertex(var45, var3 + 1, var41, var25, var27);
               capture.vertex(var45, var3 + 1, var41, var23, var27);
               capture.vertex(var45, var3 + 0, var41, var23, var31);
               capture.vertex(var47, var3 + 0, var41, var25, var31);
               capture.vertex(var47, var3 + 1, var41, var25, var27);
            }

            if (var57 || (key & 16384) != 0 && (key & 512) != 0) {
               capture.vertex(var45, var3 + 1 + 0.005, var41, var23, var29);
               capture.vertex(var45, var3 + 1 + 0.005, var43, var23, var31);
               capture.vertex(var47, var3 + 1 + 0.005, var43, var25, var31);
               capture.vertex(var47, var3 + 1 + 0.005, var41, var25, var29);
               capture.vertex(var45, var3 + 1 + 0.005, var43, var23, var29);
               capture.vertex(var45, var3 + 1 + 0.005, var41, var23, var31);
               capture.vertex(var47, var3 + 1 + 0.005, var41, var25, var31);
               capture.vertex(var47, var3 + 1 + 0.005, var43, var25, var29);
            }

            if (var58 || (key & 32768) != 0 && (key & 8192) != 0) {
               capture.vertex(var45, var3 - 0.005, var41, var23, var29);
               capture.vertex(var45, var3 - 0.005, var43, var23, var31);
               capture.vertex(var47, var3 - 0.005, var43, var25, var31);
               capture.vertex(var47, var3 - 0.005, var41, var25, var29);
               capture.vertex(var45, var3 - 0.005, var43, var23, var29);
               capture.vertex(var45, var3 - 0.005, var41, var23, var31);
               capture.vertex(var47, var3 - 0.005, var41, var25, var31);
               capture.vertex(var47, var3 - 0.005, var43, var25, var29);
            }
         }
      } else {
         capture.vertex(var35, var3 + 1, var43, var66, var19);
         capture.vertex(var35, var3 + 0, var43, var66, var21);
         capture.vertex(var35, var3 + 0, var39, var17, var21);
         capture.vertex(var35, var3 + 1, var39, var17, var19);
         capture.vertex(var35, var3 + 1, var39, var66, var19);
         capture.vertex(var35, var3 + 0, var39, var66, var21);
         capture.vertex(var35, var3 + 0, var43, var17, var21);
         capture.vertex(var35, var3 + 1, var43, var17, var19);
         if (var57) {
            capture.vertex(var47, var3 + 1 + 0.005, var43, var25, var31);
            capture.vertex(var47, var3 + 1 + 0.005, var39, var25, var27);
            capture.vertex(var45, var3 + 1 + 0.005, var39, var23, var27);
            capture.vertex(var45, var3 + 1 + 0.005, var43, var23, var31);
            capture.vertex(var47, var3 + 1 + 0.005, var39, var25, var31);
            capture.vertex(var47, var3 + 1 + 0.005, var43, var25, var27);
            capture.vertex(var45, var3 + 1 + 0.005, var43, var23, var27);
            capture.vertex(var45, var3 + 1 + 0.005, var39, var23, var31);
         } else {
            if ((key & 16384) != 0 && (key & 256) != 0) {
               capture.vertex(var45, var3 + 1 + 0.005, var39, var25, var27);
               capture.vertex(var45, var3 + 1 + 0.005, var41, var25, var29);
               capture.vertex(var47, var3 + 1 + 0.005, var41, var23, var29);
               capture.vertex(var47, var3 + 1 + 0.005, var39, var23, var27);
               capture.vertex(var45, var3 + 1 + 0.005, var41, var25, var27);
               capture.vertex(var45, var3 + 1 + 0.005, var39, var25, var29);
               capture.vertex(var47, var3 + 1 + 0.005, var39, var23, var29);
               capture.vertex(var47, var3 + 1 + 0.005, var41, var23, var27);
            }

            if ((key & 16384) != 0 && (key & 512) != 0) {
               capture.vertex(var45, var3 + 1 + 0.005, var41, var23, var29);
               capture.vertex(var45, var3 + 1 + 0.005, var43, var23, var31);
               capture.vertex(var47, var3 + 1 + 0.005, var43, var25, var31);
               capture.vertex(var47, var3 + 1 + 0.005, var41, var25, var29);
               capture.vertex(var45, var3 + 1 + 0.005, var43, var23, var29);
               capture.vertex(var45, var3 + 1 + 0.005, var41, var23, var31);
               capture.vertex(var47, var3 + 1 + 0.005, var41, var25, var31);
               capture.vertex(var47, var3 + 1 + 0.005, var43, var25, var29);
            }
         }

         if (var58) {
            capture.vertex(var47, var3 - 0.005, var43, var25, var31);
            capture.vertex(var47, var3 - 0.005, var39, var25, var27);
            capture.vertex(var45, var3 - 0.005, var39, var23, var27);
            capture.vertex(var45, var3 - 0.005, var43, var23, var31);
            capture.vertex(var47, var3 - 0.005, var39, var25, var31);
            capture.vertex(var47, var3 - 0.005, var43, var25, var27);
            capture.vertex(var45, var3 - 0.005, var43, var23, var27);
            capture.vertex(var45, var3 - 0.005, var39, var23, var31);
         } else {
            if ((key & 32768) != 0 && (key & 4096) != 0) {
               capture.vertex(var45, var3 - 0.005, var39, var25, var27);
               capture.vertex(var45, var3 - 0.005, var41, var25, var29);
               capture.vertex(var47, var3 - 0.005, var41, var23, var29);
               capture.vertex(var47, var3 - 0.005, var39, var23, var27);
               capture.vertex(var45, var3 - 0.005, var41, var25, var27);
               capture.vertex(var45, var3 - 0.005, var39, var25, var29);
               capture.vertex(var47, var3 - 0.005, var39, var23, var29);
               capture.vertex(var47, var3 - 0.005, var41, var23, var27);
            }

            if ((key & 32768) != 0 && (key & 8192) != 0) {
               capture.vertex(var45, var3 - 0.005, var41, var23, var29);
               capture.vertex(var45, var3 - 0.005, var43, var23, var31);
               capture.vertex(var47, var3 - 0.005, var43, var25, var31);
               capture.vertex(var47, var3 - 0.005, var41, var25, var29);
               capture.vertex(var45, var3 - 0.005, var43, var23, var29);
               capture.vertex(var45, var3 - 0.005, var41, var23, var31);
               capture.vertex(var47, var3 - 0.005, var41, var25, var31);
               capture.vertex(var47, var3 - 0.005, var43, var25, var29);
            }
         }
      }

  return List.copyOf(capture.quads);
 }
 private record Icon(TextureAtlasSprite sprite){double c(){return sprite.getU0();}double d(){return sprite.getU1();}double e(){return sprite.getV0();}double f(){return sprite.getV1();}double a(double u){return sprite.getU((float)(u/16));}double b(double v){return sprite.getV((float)(v/16));}}
 private static final class Capture {
  final TextureAtlasSprite sprite;final List<BakedQuad> quads=new ArrayList<>();final double[][] points=new double[4][5];int cursor;
  Capture(TextureAtlasSprite sprite){this.sprite=sprite;}
  void vertex(double x,double y,double z,double u,double v){points[cursor++]=new double[]{x,y,z,u,v};if(cursor!=4)return;cursor=0;double ax=points[1][0]-points[0][0],ay=points[1][1]-points[0][1],az=points[1][2]-points[0][2],bx=points[2][0]-points[0][0],by=points[2][1]-points[0][1],bz=points[2][2]-points[0][2];float nx=(float)(ay*bz-az*by),ny=(float)(az*bx-ax*bz),nz=(float)(ax*by-ay*bx);float length=(float)Math.sqrt(nx*nx+ny*ny+nz*nz);if(length!=0){nx/=length;ny/=length;nz/=length;}var out=new QuadBakingVertexConsumer();out.setSprite(sprite);out.setDirection(Direction.getNearest(nx,ny,nz));out.setShade(false);out.setHasAmbientOcclusion(false);for(var point:points)out.addVertex((float)point[0],(float)point[1],(float)point[2]).setColor(-1).setUv((float)point[3],(float)point[4]).setLight(0).setNormal(nx,ny,nz);quads.add(out.bakeQuad());
  }
 }
}
