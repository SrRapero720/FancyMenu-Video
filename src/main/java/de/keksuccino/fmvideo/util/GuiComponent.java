package de.keksuccino.fmvideo.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public abstract class GuiComponent {
   public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/options_background.png");
   public static final ResourceLocation STATS_ICON_LOCATION = new ResourceLocation("textures/gui/container/stats_icons.png");
   public static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
   private int blitOffset;

   protected void hLine(PoseStack p_93155_, int p_93156_, int p_93157_, int p_93158_, int p_93159_) {
      if (p_93157_ < p_93156_) {
         int i = p_93156_;
         p_93156_ = p_93157_;
         p_93157_ = i;
      }

      fill(p_93155_, p_93156_, p_93158_, p_93157_ + 1, p_93158_ + 1, p_93159_);
   }

   protected void vLine(PoseStack p_93223_, int p_93224_, int p_93225_, int p_93226_, int p_93227_) {
      if (p_93226_ < p_93225_) {
         int i = p_93225_;
         p_93225_ = p_93226_;
         p_93226_ = i;
      }

      fill(p_93223_, p_93224_, p_93225_ + 1, p_93224_ + 1, p_93226_, p_93227_);
   }

   public static void enableScissor(int p_239261_, int p_239262_, int p_239263_, int p_239264_) {
      Window window = Minecraft.getInstance().getWindow();
      int i = window.getHeight();
      double d0 = window.getGuiScale();
      double d1 = (double)p_239261_ * d0;
      double d2 = (double)i - (double)p_239264_ * d0;
      double d3 = (double)(p_239263_ - p_239261_) * d0;
      double d4 = (double)(p_239264_ - p_239262_) * d0;
      RenderSystem.enableScissor((int)d1, (int)d2, Math.max(0, (int)d3), Math.max(0, (int)d4));
   }

   public static void disableScissor() {
      RenderSystem.disableScissor();
   }

   public static void fill(PoseStack p_93173_, int p_93174_, int p_93175_, int p_93176_, int p_93177_, int p_93178_) {
      innerFill(p_93173_.last().pose(), p_93174_, p_93175_, p_93176_, p_93177_, p_93178_);
   }

   private static void innerFill(Matrix4f p_93106_, int p_93107_, int p_93108_, int p_93109_, int p_93110_, int p_93111_) {
      if (p_93107_ < p_93109_) {
         int i = p_93107_;
         p_93107_ = p_93109_;
         p_93109_ = i;
      }

      if (p_93108_ < p_93110_) {
         int j = p_93108_;
         p_93108_ = p_93110_;
         p_93110_ = j;
      }

      float f3 = (float)(p_93111_ >> 24 & 255) / 255.0F;
      float f = (float)(p_93111_ >> 16 & 255) / 255.0F;
      float f1 = (float)(p_93111_ >> 8 & 255) / 255.0F;
      float f2 = (float)(p_93111_ & 255) / 255.0F;
      BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      bufferbuilder.vertex(p_93106_, (float)p_93107_, (float)p_93110_, 0.0F).color(f, f1, f2, f3).endVertex();
      bufferbuilder.vertex(p_93106_, (float)p_93109_, (float)p_93110_, 0.0F).color(f, f1, f2, f3).endVertex();
      bufferbuilder.vertex(p_93106_, (float)p_93109_, (float)p_93108_, 0.0F).color(f, f1, f2, f3).endVertex();
      bufferbuilder.vertex(p_93106_, (float)p_93107_, (float)p_93108_, 0.0F).color(f, f1, f2, f3).endVertex();
      BufferUploader.drawWithShader(bufferbuilder.end());
      RenderSystem.disableBlend();
   }

   protected static void fillGradient(Matrix4f p_93124_, BufferBuilder p_93125_, int p_93126_, int p_93127_, int p_93128_, int p_93129_, int p_93130_, int p_93131_, int p_93132_) {
      float f = (float)(p_93131_ >> 24 & 255) / 255.0F;
      float f1 = (float)(p_93131_ >> 16 & 255) / 255.0F;
      float f2 = (float)(p_93131_ >> 8 & 255) / 255.0F;
      float f3 = (float)(p_93131_ & 255) / 255.0F;
      float f4 = (float)(p_93132_ >> 24 & 255) / 255.0F;
      float f5 = (float)(p_93132_ >> 16 & 255) / 255.0F;
      float f6 = (float)(p_93132_ >> 8 & 255) / 255.0F;
      float f7 = (float)(p_93132_ & 255) / 255.0F;
      p_93125_.vertex(p_93124_, (float)p_93128_, (float)p_93127_, (float)p_93130_).color(f1, f2, f3, f).endVertex();
      p_93125_.vertex(p_93124_, (float)p_93126_, (float)p_93127_, (float)p_93130_).color(f1, f2, f3, f).endVertex();
      p_93125_.vertex(p_93124_, (float)p_93126_, (float)p_93129_, (float)p_93130_).color(f5, f6, f7, f4).endVertex();
      p_93125_.vertex(p_93124_, (float)p_93128_, (float)p_93129_, (float)p_93130_).color(f5, f6, f7, f4).endVertex();
   }


   public static void blit(PoseStack p_93201_, int p_93202_, int p_93203_, int p_93204_, int p_93205_, int p_93206_, TextureAtlasSprite p_93207_) {
      innerBlit(p_93201_.last().pose(), p_93202_, p_93202_ + p_93205_, p_93203_, p_93203_ + p_93206_, p_93204_, p_93207_.getU0(), p_93207_.getU1(), p_93207_.getV0(), p_93207_.getV1());
   }

   public void blit(PoseStack p_93229_, int p_93230_, int p_93231_, int p_93232_, int p_93233_, int p_93234_, int p_93235_) {
      blit(p_93229_, p_93230_, p_93231_, this.blitOffset, (float)p_93232_, (float)p_93233_, p_93234_, p_93235_, 256, 256);
   }

   public static void blit(PoseStack p_93144_, int p_93145_, int p_93146_, int p_93147_, float p_93148_, float p_93149_, int p_93150_, int p_93151_, int p_93152_, int p_93153_) {
      innerBlit(p_93144_, p_93145_, p_93145_ + p_93150_, p_93146_, p_93146_ + p_93151_, p_93147_, p_93150_, p_93151_, p_93148_, p_93149_, p_93152_, p_93153_);
   }

   public static void blit(PoseStack p_93161_, int p_93162_, int p_93163_, int p_93164_, int p_93165_, float p_93166_, float p_93167_, int p_93168_, int p_93169_, int p_93170_, int p_93171_) {
      innerBlit(p_93161_, p_93162_, p_93162_ + p_93164_, p_93163_, p_93163_ + p_93165_, 0, p_93168_, p_93169_, p_93166_, p_93167_, p_93170_, p_93171_);
   }

   public static void blit(PoseStack p_93134_, int p_93135_, int p_93136_, float p_93137_, float p_93138_, int p_93139_, int p_93140_, int p_93141_, int p_93142_) {
      blit(p_93134_, p_93135_, p_93136_, p_93139_, p_93140_, p_93137_, p_93138_, p_93139_, p_93140_, p_93141_, p_93142_);
   }

   private static void innerBlit(PoseStack p_93188_, int p_93189_, int p_93190_, int p_93191_, int p_93192_, int p_93193_, int p_93194_, int p_93195_, float p_93196_, float p_93197_, int p_93198_, int p_93199_) {
      innerBlit(p_93188_.last().pose(), p_93189_, p_93190_, p_93191_, p_93192_, p_93193_, (p_93196_ + 0.0F) / (float)p_93198_, (p_93196_ + (float)p_93194_) / (float)p_93198_, (p_93197_ + 0.0F) / (float)p_93199_, (p_93197_ + (float)p_93195_) / (float)p_93199_);
   }

   private static void innerBlit(Matrix4f p_93113_, int p_93114_, int p_93115_, int p_93116_, int p_93117_, int p_93118_, float p_93119_, float p_93120_, float p_93121_, float p_93122_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferbuilder.vertex(p_93113_, (float)p_93114_, (float)p_93117_, (float)p_93118_).uv(p_93119_, p_93122_).endVertex();
      bufferbuilder.vertex(p_93113_, (float)p_93115_, (float)p_93117_, (float)p_93118_).uv(p_93120_, p_93122_).endVertex();
      bufferbuilder.vertex(p_93113_, (float)p_93115_, (float)p_93116_, (float)p_93118_).uv(p_93120_, p_93121_).endVertex();
      bufferbuilder.vertex(p_93113_, (float)p_93114_, (float)p_93116_, (float)p_93118_).uv(p_93119_, p_93121_).endVertex();
      BufferUploader.drawWithShader(bufferbuilder.end());
   }

   public int getBlitOffset() {
      return this.blitOffset;
   }

   public void setBlitOffset(int p_93251_) {
      this.blitOffset = p_93251_;
   }
}