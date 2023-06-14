package de.keksuccino.fmvideo.video;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.lib720.caprica.vlcj4.player.embedded.videosurface.callback.BufferFormat;
import me.lib720.caprica.vlcj4.player.embedded.videosurface.callback.UnAllocBufferFormatCallback;
import me.srrapero720.watermedia.api.media.players.VideoLanPlayer;
import net.minecraft.client.renderer.GameRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.nio.IntBuffer;
import java.util.concurrent.locks.ReentrantLock;

public class VideoRenderer {
    private static final Logger LOGGER = LogManager.getLogger("fmvideo/VideoRenderer");
    protected String mediaPath;
    protected VideoLanPlayer player;
    private final ReentrantLock lock = new ReentrantLock();
    protected int texture;

    // Texture data
    private volatile int width = 1;
    private volatile int height = 1;
    private volatile IntBuffer buffer;
    private volatile boolean first = true;
    private volatile boolean needsUpdate = false;

    protected boolean playing = false;
    protected int baseVolume = 100;

    public VideoRenderer(String mediaPathOrLink) {
        this.texture = GlStateManager._genTexture();
        this.mediaPath = mediaPathOrLink;
        this.player = new VideoLanPlayer((mediaPlayer, nativeBuffers, bufferFormat) -> {
            lock.lock();
            try {
                buffer.put(nativeBuffers[0].asIntBuffer());
                buffer.rewind();
                needsUpdate = true;
            } finally {
                lock.unlock();
            }
        }, new UnAllocBufferFormatCallback() {
            @Override
            public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
                lock.lock();
                try {
                    width = sourceWidth;
                    height = sourceHeight;
                    first = true;
                    buffer = MemoryTracker.create(sourceWidth * sourceHeight * 4).asIntBuffer();
                    needsUpdate = true;
                } finally {
                    lock.unlock();
                }
                return new BufferFormat("RGBA", sourceWidth, sourceHeight, new int[] { sourceWidth * 4 }, new int[] { sourceHeight });
            }
        });

        if (this.player.getRawPlayer() != null) {
            if (!this.player.isValid()) this.player.start(mediaPathOrLink);
        } else {
            LOGGER.error("ERROR: Unable to initialize player for media: " + this.mediaPath);
        }

    }

    private void prerender() {
        lock.lock();
        try {
            if (needsUpdate) {
                GlStateManager._pixelStore(3314, 0);
                GlStateManager._pixelStore(3316, 0);
                GlStateManager._pixelStore(3315, 0);
                RenderSystem.bindTexture(texture);
                if (first) {
                    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
                    first = false;
                } else
                    GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
                needsUpdate = false;
            }
        } finally {
            lock.unlock();
        }
    }

    public void render(PoseStack matrix, int posX, int posY, int width, int height) {
        if (player == null || player.getRawPlayer() == null) return;

        try {
            this.prerender();

            if (texture != -1) {
                RenderSystem.bindTexture(texture);
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, texture);
                RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//                RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                S.blit(matrix, posX, posY, 0.0F, 0.0F, width, height, width, height);
                RenderSystem.disableBlend();
            }

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

    public boolean isPlaying() {
        return this.playing;
    }

    public void setLooping(boolean b) {
        if (this.player != null) {
            this.player.setRepeatMode(b);
        }
    }

    public boolean isLooping() {
        if (this.player != null) {
            return this.player.getRepeatMode();
        }
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
            if (volume < 0) {
                volume = 0;
            }
            if (volume > 200) {
                volume = 200;
            }
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
            if (d != null) {
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    public String getMediaPath() {
        return this.mediaPath;
    }

    @Nullable
    public Dimension getVideoDimension() {
        if (this.player != null && this.player.getRawPlayer() != null) {
            return this.player.getRawPlayer().mediaPlayer().video().videoDimension();
        }
        return null;
    }

    public VideoLanPlayer getPlayer() {
        return this.player;
    }

    public void destroy() {
        if (this.player != null) {
            this.stop();
            if (texture != -1) GlStateManager._deleteTexture(texture);
            if (player.getRawPlayer() != null) {
                this.player.release();
            }
            this.player = null;
        }
    }

}