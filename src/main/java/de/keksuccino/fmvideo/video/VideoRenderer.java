package de.keksuccino.fmvideo.video;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import me.srrapero720.watermedia.api.player.SyncVideoPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;

public class VideoRenderer {
    private static final Logger LOGGER = LogManager.getLogger("fmvideo");
    private static final Marker IT = MarkerManager.getMarker("VideoRenderer");
    protected String mediaPath;
    protected SyncVideoPlayer player;

    protected boolean playing = false;
    protected int baseVolume = 100;

    public VideoRenderer(String mediaPathOrLink) {
        this.mediaPath = mediaPathOrLink;
        this.player = new SyncVideoPlayer(null, Minecraft.getInstance());

        if (this.player.raw() != null) {
            if (!this.player.isValid()) this.player.start(mediaPathOrLink);
        } else {
            LOGGER.error(IT, "ERROR: Unable to initialize player for media: " + this.mediaPath);
        }

    }

    public void render(PoseStack matrix, int posX, int posY, int width, int height) {
        if (player == null || player.raw() == null) return;

        try {
            int texture = player.prepareTexture();

            if (texture == -1) return;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, texture);
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            Matrix4f matrix4f = matrix.last().pose();
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferBuilder.vertex(matrix4f, (float)0, (float)0, (float)0).uv(0, 0).endVertex();
            bufferBuilder.vertex(matrix4f, (float)0, (float)height, (float)0).uv(0, 1).endVertex();
            bufferBuilder.vertex(matrix4f, (float)width, (float)height, (float)0).uv(1, 1).endVertex();
            bufferBuilder.vertex(matrix4f, (float)width, (float)0, (float)0).uv(1, 0).endVertex();
            BufferUploader.drawWithShader(bufferBuilder.end());

            RenderSystem.disableBlend();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void play() {
        if (!isPlaying()) {
            if (this.player != null) {
                this.playing = true;
                this.player.play();
            }
        }
    }

    public void pause() {
        if (isPlaying()) {
            if (this.player != null) {
                this.playing = false;
                this.player.pause();
            }
        }
    }

    public void stop() {
        if (this.player != null) {
            this.playing = false;
            this.player.stop();
        }
    }

    public boolean isPlaying() { return this.playing; }

    public void setLooping(boolean b) {
        if (this.player != null) this.player.setRepeatMode(b);
    }

    public boolean isLooping() {
        if (this.player != null) return this.player.getRepeatMode();
        return false;
    }

    /**
     * <b>FOR INTERNAL USE ONLY!</b><br>
     * Use {@link VideoRenderer#setBaseVolume(int)} instead, if you want to set the video volume.<br><br>
     *
     * @param volume Value between 0 and 200.
     */
    public void setVolume(int volume) {
        if (this.player != null) {
            if (volume < 0) volume = 0;
            if (volume > 200) volume = 200;
            this.player.setVolume(volume);
        }
    }

    public int getVolume() {
        if (this.player != null) {
            return this.player.getVolume();
        }
        return -1;
    }

    public void setBaseVolume(int vol) {
        this.baseVolume = vol;
        VideoVolumeHandler.updateRendererVolume(this);
    }

    public int getBaseVolume() {
        return this.baseVolume;
    }

    public void setTime(long time) {
        if (this.player != null) {
            this.player.seekTo(time);
        }
    }

    public void restart() {
        this.setTime(0L);
    }

    public boolean canPlay() {
        try {
            Dimension d = this.getVideoDimension();
            if (d != null) return true;
        } catch (Exception e) {}
        return false;
    }

    public String getMediaPath() {
        return this.mediaPath;
    }

    @Nullable
    public Dimension getVideoDimension() {
        if (this.player != null) return this.player.getDimensions();
        return null;
    }

    public SyncVideoPlayer getPlayer() {
        return this.player;
    }

    public void destroy() {
        if (this.player != null) {
            this.stop();
            this.player.release();
            this.player = null;
        }
    }

}