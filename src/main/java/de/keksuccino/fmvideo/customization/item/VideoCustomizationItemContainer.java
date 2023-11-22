package de.keksuccino.fmvideo.customization.item;

import de.keksuccino.fancymenu.api.item.CustomizationItem;
import de.keksuccino.fancymenu.api.item.CustomizationItemContainer;
import de.keksuccino.fancymenu.api.item.LayoutEditorElement;
import de.keksuccino.fancymenu.menu.fancy.helper.layoutcreator.LayoutEditorScreen;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.properties.PropertiesSection;

public class VideoCustomizationItemContainer extends CustomizationItemContainer {

    public VideoCustomizationItemContainer() {
        super("fancymenu_extension:video_item");
    }

    @Override
    public CustomizationItem constructDefaultItemInstance() {
        VideoCustomizationItem i = new VideoCustomizationItem(this, new PropertiesSection("dummy"));
        i.width = 100;
        i.height = 100;
        return i;
    }

    @Override
    public CustomizationItem constructCustomizedItemInstance(PropertiesSection serializedItem) {
        return new VideoCustomizationItem(this, serializedItem);
    }

    @Override
    public LayoutEditorElement constructEditorElementInstance(CustomizationItem item, LayoutEditorScreen handler) {
        return new VideoLayoutEditorElement(this, item, true, handler);
    }

    @Override
    public String getDisplayName() {
        return Locals.localize("fancymenu.fmvideo.item");
    }

    @Override
    public String[] getDescription() {
        return StringUtils.splitLines(Locals.localize("fancymenu.fmvideo.item.desc"), "%n%");
    }

}
