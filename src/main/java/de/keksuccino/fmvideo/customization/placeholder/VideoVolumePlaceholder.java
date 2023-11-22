package de.keksuccino.fmvideo.customization.placeholder;

import de.keksuccino.fancymenu.api.placeholder.PlaceholderTextContainer;
import de.keksuccino.fmvideo.video.VideoVolumeHandler;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;

public class VideoVolumePlaceholder extends PlaceholderTextContainer {

    public VideoVolumePlaceholder() {
        super("fancymenu_extension:video_placeholder:videovolume");
    }

    @Override
    public String replacePlaceholders(String rawIn) {

        return rawIn.replace(getPlaceholder(), "" + VideoVolumeHandler.getVolume());

    }

    @Override
    public String getPlaceholder() {
        return "%video_volume%";
    }

    @Override
    public String getCategory() {
        return Locals.localize("fancymenu.fmvideo.placeholder.category");
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("fancymenu.fmvideo.placeholder.videovolume");
    }

    @Override
    public String[] getDescription() {
        return StringUtils.splitLines(Locals.localize("fancymenu.fmvideo.placeholder.videovolume.desc"), "%n%");
    }

}
