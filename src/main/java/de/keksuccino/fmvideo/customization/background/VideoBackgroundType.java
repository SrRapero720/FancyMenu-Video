package de.keksuccino.fmvideo.customization.background;

import de.keksuccino.fancymenu.api.background.MenuBackground;
import de.keksuccino.fancymenu.api.background.MenuBackgroundType;
import de.keksuccino.fancymenu.menu.fancy.helper.layoutcreator.LayoutEditorScreen;
import de.keksuccino.fancymenu.menu.fancy.helper.layoutcreator.content.BackgroundOptionsPopup;
import de.keksuccino.fmvideo.popup.SetVideoPopup;
import de.keksuccino.fmvideo.util.VideoUtils;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;

import java.util.Arrays;
import java.util.List;

public class VideoBackgroundType extends MenuBackgroundType {

    public VideoBackgroundType() {
        super("fancymenu_extension:video_background");
    }

    @Override
    public void loadBackgrounds() {
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("fancymenu.fmvideo.backgroundtype");
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(StringUtils.splitLines(Locals.localize("fancymenu.fmvideo.backgroundtype.desc"), "%n%"));
    }

    @Override
    public boolean needsInputString() {
        return true;
    }

    @Override
    public MenuBackground createInstanceFromInputString(String s) {

        PropertiesSection ps = VideoUtils.readValueString(s);
        String video = "";
        boolean isLocal = true;
        boolean loop = false;
        int volume = 100;
        if (ps.hasEntry("video")) {
            video = ps.getEntryValue("video");
        }
        if (ps.hasEntry("islocal")) {
            isLocal = ps.getEntryValue("islocal").equals("true");
        }
        if (ps.hasEntry("loop")) {
            loop = ps.getEntryValue("loop").equals("true");
        }
        if (ps.hasEntry("volume")) {
            if (MathUtils.isInteger(ps.getEntryValue("volume"))) {
                volume = Integer.parseInt(ps.getEntryValue("volume"));
            }
        }

        VideoBackground vid = new VideoBackground(this, video, isLocal);
        vid.setLooping(loop);
        vid.setVolume(volume);

        return vid;

    }

    @Override
    public String inputStringButtonLabel() {
        return Locals.localize("fancymenu.fmvideo.backgroundoptions.choosevideo");
    }

    @Override
    public void onInputStringButtonPress(LayoutEditorScreen handler, BackgroundOptionsPopup optionsPopup) {
        String valueString = handler.customMenuBackgroundInputString;
        SetVideoPopup p = new SetVideoPopup(handler, optionsPopup, valueString, (call) -> {
            if (call != null) {
                handler.history.saveSnapshot(handler.history.createSnapshot());
                if (handler.customMenuBackground != null) {
                    if (handler.customMenuBackground instanceof VideoBackground) {
                        ((VideoBackground)handler.customMenuBackground).renderer.pause();
                        ((VideoBackground)handler.customMenuBackground).renderer.setLooping(false);
                        ((VideoBackground)handler.customMenuBackground).renderer.setTime(0L);
                    }
                }
                optionsPopup.resetBackgrounds();
                handler.customMenuBackgroundInputString = call;
                handler.customMenuBackground = this.createInstanceFromInputString(call);
                handler.customMenuBackground.onOpenMenu();
            }
        });
        PopupHandler.displayPopup(p);
    }

}
