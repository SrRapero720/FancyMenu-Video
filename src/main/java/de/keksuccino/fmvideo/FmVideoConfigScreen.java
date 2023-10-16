package de.keksuccino.fmvideo;

import de.keksuccino.konkrete.config.ConfigEntry;
import de.keksuccino.konkrete.gui.screens.ConfigScreen;
import de.keksuccino.konkrete.localization.Locals;
import net.minecraft.client.gui.GuiScreen;

public class FmVideoConfigScreen extends ConfigScreen {

    public FmVideoConfigScreen(GuiScreen parent) {
        super(FmVideo.config, Locals.localize("fancymenu.fmvideo.config"), parent);
    }

    @Override
    public void initGui() {
        super.initGui();

        for (String s : this.config.getCategorys()) {
            this.setCategoryDisplayName(s, Locals.localize("fancymenu.fmvideo.config.categories." + s));
        }

        for (ConfigEntry e : this.config.getAllAsEntry()) {
            this.setValueDisplayName(e.getName(), Locals.localize("fancymenu.fmvideo.config." + e.getName()));
            this.setValueDescription(e.getName(), Locals.localize("fancymenu.fmvideo.config." + e.getName() + ".desc"));
        }

    }

}