package de.keksuccino.fmvideo;

import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.fancymenu.events.SoftMenuReloadEvent;
import de.keksuccino.fancymenu.menu.button.ButtonCache;
import de.keksuccino.fancymenu.menu.fancy.MenuCustomization;
import de.keksuccino.fancymenu.menu.fancy.helper.MenuReloadedEvent;
import de.keksuccino.fancymenu.menu.fancy.helper.layoutcreator.LayoutEditorScreen;
import de.keksuccino.fancymenu.menu.fancy.helper.ui.FMConfigScreen;
import de.keksuccino.fancymenu.menu.fancy.helper.ui.UIBase;
import de.keksuccino.fmvideo.customization.background.VideoBackground;
import de.keksuccino.fmvideo.util.VideoUtils;
import de.keksuccino.fmvideo.video.VideoHandler;
import de.keksuccino.fmvideo.video.VideoRenderer;
import de.keksuccino.fmvideo.video.VideoVolumeHandler;
import de.keksuccino.konkrete.gui.content.AdvancedImageButton;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class EventHandler {

    protected static final ResourceLocation SETTINGS_ICON_LOCATION = new ResourceLocation("fmvideo", "fm_video_extension_settings_button.png");

    protected LayoutEditorScreen lastEditorScreen = null;
    protected boolean stoppedInWorld = false;

    protected float lastMcMasterVolume = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER);

    protected AdvancedImageButton openSettingsButton;

    public EventHandler() {

        this.openSettingsButton = new AdvancedImageButton(-10, 80, 44, 35, SETTINGS_ICON_LOCATION, true, (press) -> {
            Minecraft.getInstance().setScreen(new FmVideoConfigScreen(Minecraft.getInstance().screen));
        }) {
            @Override
            public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
                this.setDescription(Locals.localize("fancymenu.fmvideo.config"));
                if (this.isHoveredOrFocused()) {
                    this.setX(-2);
                } else {
                    this.setX(-10);
                }
                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        };
        UIBase.colorizeButton(this.openSettingsButton);

    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onScreenInitPre(ScreenEvent.Init.Pre e) {

        if (!ButtonCache.isCaching()) {
            if (MenuCustomization.isValidScreen(e.getScreen())) {

                boolean newEditorInstance = false;
                if (e.getScreen() instanceof LayoutEditorScreen) {
                    if ((lastEditorScreen == null) || (lastEditorScreen != e.getScreen())) {
                        newEditorInstance = true;
                    }
                    lastEditorScreen = (LayoutEditorScreen) e.getScreen();
                }

                if (MenuCustomization.isNewMenu() || newEditorInstance) {
                    boolean hasVideo = false;
                    List<VideoRenderer> renderersOfNewMenu = VideoUtils.getRenderersOfMenu(e.getScreen());
                    for (VideoRenderer r : VideoHandler.getCachedRenderers()) {
                        r.setLooping(false);
                        if ((e.getScreen() instanceof LayoutEditorScreen) || !renderersOfNewMenu.contains(r)) {
                            if (r.isPlaying()) {
                                r.pause();
                            }
                        } else if (!(e.getScreen() instanceof LayoutEditorScreen)) {
                            hasVideo = true;
                        }
                    }
                    //Reset time of all video backgrounds in menus without video background
                    if (!hasVideo) {
                        VideoBackground.lastRenderer = null;
                        for (VideoRenderer r : VideoHandler.getCachedRenderers()) {
                            r.setTime(0L);
                        }
                    }
                }

            }
        }

    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {

        //Stop and reset video backgrounds when in a world (when no screen is active)
        if (Minecraft.getInstance().screen == null) {
            if (!this.stoppedInWorld) {
                MenuCustomization.setIsNewMenu(true);
                VideoBackground.lastRenderer = null;
                for (VideoRenderer r : VideoHandler.getCachedRenderers()) {
                    r.setLooping(false);
                    r.setTime(0L);
                    r.pause();
                }
            }
            this.stoppedInWorld = true;
        } else {
            this.stoppedInWorld = false;
        }

        //Update video volumes after changing MC master volume (if not disabled in config)
        if (!FmVideo.config.getOrDefault("ignore_mc_master_volume", false)) {
            if (this.lastMcMasterVolume != Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER)) {
                VideoVolumeHandler.updateVolume();
            }
            this.lastMcMasterVolume = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER);
        }

    }

    @SubscribeEvent
    public void onDrawScreenPost(ScreenEvent.Render.Pre e) {

        if (e.getScreen() instanceof FMConfigScreen) {

            this.openSettingsButton.render(e.getGuiGraphics(), e.getMouseX(), e.getMouseY(), e.getPartialTick());

        }

    }

    @SubscribeEvent
    public void onReload(MenuReloadedEvent e) {
        VideoBackground.lastRenderer = null;
        for (VideoRenderer r : VideoHandler.getCachedRenderers()) {
            r.setLooping(false);
            r.setTime(0L);
            r.pause();
        }
    }

    @SubscribeEvent
    public void onSoftReload(SoftMenuReloadEvent e) {
        VideoBackground.lastRenderer = null;
        for (VideoRenderer r : VideoHandler.getCachedRenderers()) {
            r.setLooping(false);
            r.setTime(0L);
            r.pause();
        }
    }

}
