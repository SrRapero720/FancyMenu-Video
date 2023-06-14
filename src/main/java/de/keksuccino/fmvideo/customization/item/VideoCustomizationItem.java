package de.keksuccino.fmvideo.customization.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.fancymenu.api.item.CustomizationItem;
import de.keksuccino.fancymenu.api.item.CustomizationItemContainer;
import de.keksuccino.fmvideo.util.GuiComponent;
import de.keksuccino.fmvideo.util.UrlUtils;
import de.keksuccino.fmvideo.video.VideoHandler;
import de.keksuccino.fmvideo.video.VideoRenderer;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;
import java.io.File;

public class VideoCustomizationItem extends CustomizationItem {

    public String mediaPathLink;
    public boolean isLocal = true;

    public int volume = 100;
    public boolean looping = false;

    public VideoRenderer renderer = null;

    public VideoCustomizationItem(CustomizationItemContainer parentContainer, PropertiesSection item) {

        super(parentContainer, item);

        //If video is local or online
        String local = item.getEntryValue("islocal");
        if ((local != null) && local.equalsIgnoreCase("false")) {
            this.isLocal = false;
        }

        //Video path or link
        this.mediaPathLink = item.getEntryValue("video");
        if (this.mediaPathLink != null) {
            if (this.isLocal) {
                File f = new File(this.mediaPathLink);
                if (f.isFile()) {
                    this.renderer = VideoHandler.getRenderer(this.mediaPathLink);
                }
            } else {
                if (UrlUtils.isValidUrl(this.mediaPathLink)) {
                    this.renderer = VideoHandler.getRenderer(this.mediaPathLink);
                }
            }
        }

        //Audio volume
        String volumeString = item.getEntryValue("volume");
        if (volumeString != null) {
            if (MathUtils.isInteger(volumeString)) {
                this.volume = Integer.parseInt(volumeString);
                if (this.volume < 0) {
                    this.volume = 0;
                }
                if (this.volume > 200) {
                    this.volume = 200;
                }
            }
        }

        //If video should loop
        String loop = item.getEntryValue("looping");
        if ((loop != null) && loop.equalsIgnoreCase("true")) {
            this.looping = true;
        }

    }

    @Override
    public void render(GuiGraphics matrix, Screen menu) {

        if (this.shouldRender()) {

            int x = this.getPosX(menu);
            int y = this.getPosY(menu);
            int w = this.getWidth();
            int h = this.getHeight();
            Font font = Minecraft.getInstance().font;

            if (this.renderer != null) {

                this.renderer.setLooping(this.looping);
                this.renderer.setBaseVolume(this.volume);
                this.renderer.play();

                this.renderer.render(matrix.pose(), x, y, this.getWidth(), this.getHeight());

            } else if (isEditorActive()) {

                RenderSystem.enableBlend();

                GuiComponent.fill(matrix.pose(), x, y, x + w, y + h, Color.MAGENTA.getRGB());
//                drawCenteredString(matrix, font, Locals.localize("fancymenu.fmvideo.item.nomedia.line1"), x + (w / 2), y + (h / 2) - (font.lineHeight / 2) - 5, -1);
//                drawCenteredString(matrix, font, Locals.localize("fancymenu.fmvideo.item.nomedia.line2"), x + (w / 2), y + (h / 2) - (font.lineHeight / 2) + 5, -1);

            }

        } else {
            if (this.renderer != null) {

                this.renderer.pause();

            }
        }

    }

}
