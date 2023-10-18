package de.keksuccino.fmvideo.customization.background;

import de.keksuccino.fancymenu.api.background.MenuBackground;
import de.keksuccino.fancymenu.api.background.MenuBackgroundType;
import de.keksuccino.fancymenu.menu.fancy.helper.layoutcreator.LayoutEditorScreen;
import de.keksuccino.fmvideo.video.VideoHandler;
import de.keksuccino.fmvideo.video.VideoRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

import static de.keksuccino.fmvideo.FmVideo.LOGGER;

public class VideoBackground extends MenuBackground {

    public static VideoRenderer lastRenderer = null;

    protected VideoRenderer renderer;

    protected boolean isLocal;
    protected boolean cachedLooping = false;
    public Color backgroundColor;

    public VideoBackground(MenuBackgroundType type, String videoPath, boolean isLocal) {
        super("", type);
        this.renderer = VideoHandler.getRenderer(videoPath);
        this.isLocal = isLocal;
    }

    @Override
    public void onOpenMenu() {
        if (lastRenderer != this.renderer) {
            this.renderer.setTime(0L);
        }
        lastRenderer = this.renderer;

        if (this.renderer.isLooping() != this.cachedLooping) {
            this.renderer.setLooping(this.cachedLooping);
        }
        if (!renderer.isPlaying()) {
            this.renderer.play();
        }
    }

    @Override
    public void onResetBackground() {

        if (this.renderer.isPlaying()) {
            this.renderer.pause();
        }
        this.renderer.setTime(0L);
        this.renderer.setLooping(false);

    }

    @Override
    public void render(GuiScreen screen, boolean keepAspectRatio) {
        try {

            if (this.backgroundColor == null) {
                this.backgroundColor = new Color(0, 0, 0);
            }

            Gui.drawRect(0, 0, screen.width, screen.height, this.backgroundColor.getRGB());

            if (this.renderer != null) {
                if (this.renderer.isPlaying() && this.renderer.canPlay()) {

                    if (!keepAspectRatio) {

                        this.renderer.render(0, 0, screen.width, screen.height);

                    } else {

                        int w = (int) this.renderer.getVideoDimension().getWidth();
                        int h = (int) this.renderer.getVideoDimension().getHeight();

                        double ratio = (double) w / (double) h;
                        int wFinal = (int)(screen.height * ratio);
                        int screenCenterX = screen.width / 2;
                        if (wFinal < screen.width) {
                            this.renderer.render(0, 0, screen.width, screen.height);
                        } else {
                            this.renderer.render(screenCenterX - (wFinal / 2), 0, wFinal, screen.height);
                        }

                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void play() {
        this.renderer.play();
    }

    public void pause() {
        this.renderer.pause();
    }

    public boolean isLooping() {
        return this.renderer.isLooping();
    }

    public void setLooping(boolean b) {
        this.cachedLooping = b;
        this.renderer.setLooping(b);
    }

    public int getVolume() {
        return this.renderer.getBaseVolume();
    }

    public void setVolume(int volume) {
        this.renderer.setBaseVolume(volume);
    }

    public boolean isLocalVideo() {
        return this.isLocal;
    }

    public String getVideoPathOrLink() {
        return this.renderer.getMediaPath();
    }

    public VideoRenderer getRenderer() {
        return this.renderer;
    }

    protected static LayoutEditorScreen getEditorInstance() {
        if (Minecraft.getMinecraft().currentScreen instanceof LayoutEditorScreen) {
            return (LayoutEditorScreen) Minecraft.getMinecraft().currentScreen;
        }
        return null;
    }

}