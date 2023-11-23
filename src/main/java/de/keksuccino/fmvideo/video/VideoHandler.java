package de.keksuccino.fmvideo.video;

import de.keksuccino.fmvideo.FmVideo;
import me.srrapero720.watermedia.api.url.UrlAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoHandler {

    protected static Map<String, VideoRenderer> renderers = new HashMap<>();

    public static VideoRenderer getRenderer(String videoPathOrLink) {
        if (!renderers.containsKey(videoPathOrLink)) {
            String resultingUrl = !UrlAPI.isValid(videoPathOrLink) ? ("file:///" + videoPathOrLink) : videoPathOrLink;
            if (!UrlAPI.isValid(resultingUrl)) resultingUrl = videoPathOrLink; // IF WASN'T VALID YET MEAN IT WAS BROKEN
            FmVideo.LOGGER.debug(resultingUrl);
            renderers.put(videoPathOrLink, new VideoRenderer(resultingUrl));
        }
        return renderers.get(videoPathOrLink);
    }

    public static void removeRenderer(VideoRenderer renderer) {
        String s = null;
        for (Map.Entry<String, VideoRenderer> m : renderers.entrySet()) {
            if (m.getValue() == renderer) {
                s = m.getKey();
                break;
            }
        }
        if (s != null) {
            removeRenderer(s);
        }
    }

    public static void removeRenderer(String videoPathOrLink) {
        VideoRenderer r = renderers.remove(videoPathOrLink);
        if (r != null) {
            r.destroy();
        }
    }

    public static void clearAll() {
        for (VideoRenderer r : renderers.values()) {
            r.destroy();
        }
        renderers.clear();
    }

    public static List<VideoRenderer> getCachedRenderers() {
        List<VideoRenderer> l = new ArrayList<>();
        l.addAll(renderers.values());
        return l;
    }

    public static boolean isRendererCached(String videoPathOrLink) {
        return renderers.containsKey(videoPathOrLink);
    }

}