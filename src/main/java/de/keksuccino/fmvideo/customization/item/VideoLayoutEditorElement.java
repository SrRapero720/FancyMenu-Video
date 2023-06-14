package de.keksuccino.fmvideo.customization.item;

import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.fancymenu.api.item.CustomizationItem;
import de.keksuccino.fancymenu.api.item.CustomizationItemContainer;
import de.keksuccino.fancymenu.api.item.LayoutEditorElement;
import de.keksuccino.fancymenu.menu.fancy.helper.layoutcreator.LayoutEditorScreen;
import de.keksuccino.fancymenu.menu.fancy.helper.ui.popup.FMTextInputPopup;
import de.keksuccino.fmvideo.popup.SetVideoPopup;
import de.keksuccino.fmvideo.util.VideoUtils;
import de.keksuccino.fmvideo.video.VideoHandler;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.CharacterFilter;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nonnull;
import java.awt.*;

public class VideoLayoutEditorElement extends LayoutEditorElement {

    public VideoLayoutEditorElement(@Nonnull CustomizationItemContainer parentContainer, @Nonnull CustomizationItem customizationItemInstance, boolean destroyable, @Nonnull LayoutEditorScreen handler) {
        super(parentContainer, customizationItemInstance, destroyable, handler);
    }

    @Override
    public void init() {

        super.init();

        VideoCustomizationItem i = (VideoCustomizationItem) this.object;

        AdvancedButton setVideoButton = new AdvancedButton(0, 0, 0, 0, Locals.localize("fancymenu.fmvideo.item.options.setvideo"), (press) -> {
            String valueString = null;
            if (i.mediaPathLink != null) {
                valueString = VideoUtils.buildValueString(i.mediaPathLink, i.isLocal, null);
            }
            SetVideoPopup p = new SetVideoPopup(handler, null, valueString, false, (call) -> {
                if (call != null) {
                    PropertiesSection videoProps = VideoUtils.readValueString(call);
                    if (videoProps != null) {
                        String video = videoProps.getEntryValue("video");
                        if (video != null) {
                            if ((i.mediaPathLink == null) || !video.equals(i.mediaPathLink)) {
                                handler.history.saveSnapshot(handler.history.createSnapshot());
                            }
                            i.mediaPathLink = video;
                            String local = videoProps.getEntryValue("islocal");
                            if ((local != null) && local.equalsIgnoreCase("false")) {
                                i.isLocal = false;
                            }
                            i.renderer = VideoHandler.getRenderer(i.mediaPathLink);
                            if (i.renderer != null) {
                                i.renderer.setTime(0L);
                            }
                        }
                    }
                }
            });
            PopupHandler.displayPopup(p);
        });
        setVideoButton.setDescription(StringUtils.splitLines(Locals.localize("fancymenu.fmvideo.item.options.setvideo.btn.desc"), "%n%"));
        this.rightclickMenu.addContent(setVideoButton);

        AdvancedButton resetVideoButton = new AdvancedButton(0, 0, 0, 0, Locals.localize("fancymenu.fmvideo.item.options.resetvideo"), (press) -> {
            this.handler.history.saveSnapshot(this.handler.history.createSnapshot());
            if (i.renderer != null) {
                i.renderer.pause();
                i.renderer.setTime(0L);
                i.renderer.setLooping(false);
            }
            i.renderer = null;
            i.mediaPathLink = null;
            i.looping = false;
            i.volume = 100;
            i.isLocal = true;
        });
        resetVideoButton.setDescription(StringUtils.splitLines(Locals.localize("fancymenu.fmvideo.item.options.resetvideo.btn.desc"), "%n%"));
        this.rightclickMenu.addContent(resetVideoButton);

        this.rightclickMenu.addSeparator();

        AdvancedButton setToAspectRationButton = new AdvancedButton(0, 0, 0, 0, Locals.localize("fancymenu.fmvideo.item.options.aspectratio"), (press) -> {
            if (i.renderer != null) {
                if (i.renderer.canPlay()) {
                    this.handler.history.saveSnapshot(this.handler.history.createSnapshot());
                    int w = (int) i.renderer.getVideoDimension().getWidth();
                    int h = (int) i.renderer.getVideoDimension().getHeight();
                    double ratio = (double) w / (double) h;
                    i.height = 100;
                    i.width = (int)(i.height * ratio);
                }
            }
        });
        setToAspectRationButton.setDescription(StringUtils.splitLines(Locals.localize("fancymenu.fmvideo.item.options.aspectratio.btn.desc"), "%n%"));
        this.rightclickMenu.addContent(setToAspectRationButton);

        this.rightclickMenu.addSeparator();

        AdvancedButton loopButton = new AdvancedButton(0, 0, 0, 0, Locals.localize("fancymenu.fmvideo.item.options.loop.off"), (press) -> {
            if (i.looping) {
                i.looping = false;
            } else {
                i.looping = true;
            }
            if (i.renderer != null) {
                i.renderer.setTime(0L);
                i.renderer.pause();
            }
        }) {
            @Override
            public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
                if (i.looping) {
                    this.setMessage(Locals.localize("fancymenu.fmvideo.item.options.loop.on"));
                } else {
                    this.setMessage(Locals.localize("fancymenu.fmvideo.item.options.loop.off"));
                }
                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        };
        loopButton.setDescription(StringUtils.splitLines(Locals.localize("fancymenu.fmvideo.item.options.loop.btn.desc"), "%n%"));
        this.rightclickMenu.addContent(loopButton);

        AdvancedButton volumeButton = new AdvancedButton(0, 0, 0, 0, Locals.localize("fancymenu.fmvideo.item.options.volume"), (press) -> {
            FMTextInputPopup p = new FMTextInputPopup(new Color(0, 0, 0, 0), Locals.localize("fancymenu.fmvideo.item.options.volume"), CharacterFilter.getIntegerCharacterFiler(), 240, (call) -> {
                if (call != null) {
                    if (!call.equals("" + i.volume)) {
                        if (MathUtils.isInteger(call)) {
                            this.handler.history.saveSnapshot(this.handler.history.createSnapshot());
                            i.volume = Integer.parseInt(call);
                        }
                    }
                }
            });
            p.setText("" + i.volume);
            PopupHandler.displayPopup(p);
        });
        volumeButton.setDescription(StringUtils.splitLines(Locals.localize("fancymenu.fmvideo.item.options.volume.btn.desc"), "%n%"));
        this.rightclickMenu.addContent(volumeButton);

    }

    @Override
    public SimplePropertiesSection serializeItem() {

        VideoCustomizationItem i = (VideoCustomizationItem) this.object;

        SimplePropertiesSection sec = new SimplePropertiesSection();

        if (i.mediaPathLink != null) {
            sec.addEntry("video", i.mediaPathLink);
        }
        sec.addEntry("islocal", "" + i.isLocal);
        sec.addEntry("volume", "" + i.volume);
        sec.addEntry("looping", "" + i.looping);

        return sec;

    }

}
