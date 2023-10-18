package de.keksuccino.fmvideo;

import de.keksuccino.fancymenu.api.background.MenuBackgroundTypeRegistry;
import de.keksuccino.fancymenu.api.buttonaction.ButtonActionRegistry;
import de.keksuccino.fancymenu.api.item.CustomizationItemRegistry;
import de.keksuccino.fancymenu.api.placeholder.PlaceholderTextRegistry;
import de.keksuccino.fmvideo.customization.background.VideoBackgroundType;
import de.keksuccino.fmvideo.customization.buttonaction.LowerVideoVolumeButtonAction;
import de.keksuccino.fmvideo.customization.buttonaction.UpperVideoVolumeButtonAction;
import de.keksuccino.fmvideo.customization.item.VideoCustomizationItemContainer;
import de.keksuccino.fmvideo.customization.placeholder.VideoVolumePlaceholder;
import de.keksuccino.fmvideo.video.VideoVolumeHandler;
import de.keksuccino.konkrete.Konkrete;
import de.keksuccino.konkrete.config.Config;
import de.keksuccino.konkrete.config.exceptions.InvalidValueException;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = "fancymenu_video", acceptableRemoteVersions = FmVideo.VERSION, dependencies = "required-after:watermedia@[2.0,2.1];after:fancymenu@[2.14.9]")
public class FmVideo {
    public static final String ID = "fancymenu_video";
    public static final String VERSION = "2.1.2";
    public static final Logger LOGGER = LogManager.getLogger(FmVideo.class);
    public static final File MOD_DIR = new File("config/fancymenu/extensions/" + ID);
    public static Config config;

    public FmVideo() {
        if (FMLCommonHandler.instance().getSide().isServer()) {
            LOGGER.warn("## WARNING ## 'FancyMenu Video Extension' is a client mod and has no effect when loaded on a server!");
            return;
        }

        try {
            if (!MOD_DIR.exists()) MOD_DIR.mkdirs();

            updateConfig();
            VideoVolumeHandler.init();

            //Register video background type
            MenuBackgroundTypeRegistry.registerBackgroundType(new VideoBackgroundType());

            //Register video item container
            CustomizationItemRegistry.registerItem(new VideoCustomizationItemContainer());

            //Register button actions
            ButtonActionRegistry.registerButtonAction(new LowerVideoVolumeButtonAction());
            ButtonActionRegistry.registerButtonAction(new UpperVideoVolumeButtonAction());

            //Register placeholders
            PlaceholderTextRegistry.registerPlaceholder(new VideoVolumePlaceholder());

            Konkrete.addPostLoadingEvent(ID, this::onClientSetup);

            MinecraftForge.EVENT_BUS.register(new EventHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onClientSetup() {
        try {
            initLocals();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void initLocals() {
        String baseDir = "locals/";
        File f = new File(MOD_DIR.getPath() + "/locals");
        if (!f.exists()) {
            f.mkdirs();
        }

        Locals.copyLocalsFileToDir(new ResourceLocation(FmVideo.ID, baseDir + "en_us.local"), "en_us", f.getPath());
//        Locals.copyLocalsFileToDir(new ResourceLocation("fmvideo", baseDir + "de_de.local"), "de_de", f.getPath());
        Locals.getLocalsFromDir(f.getPath());
    }

    public static void updateConfig() {
        try {
            config = new Config(MOD_DIR.getPath() + "/config.cfg");
            config.registerValue("ignore_mc_master_volume", false, "audio", "If the video volume should ignore Minecraft's master volume.");
            config.syncConfig();
            config.clearUnusedValues();
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
    }

}