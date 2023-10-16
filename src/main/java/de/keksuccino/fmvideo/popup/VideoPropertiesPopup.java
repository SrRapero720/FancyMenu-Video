package de.keksuccino.fmvideo.popup;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.keksuccino.fancymenu.menu.fancy.helper.layoutcreator.content.BackgroundOptionsPopup;
import de.keksuccino.fancymenu.menu.fancy.helper.ui.popup.FMPopup;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.content.AdvancedTextField;
import de.keksuccino.konkrete.gui.screens.popup.Popup;
import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import de.keksuccino.konkrete.input.CharacterFilter;
import de.keksuccino.konkrete.input.KeyboardData;
import de.keksuccino.konkrete.input.KeyboardHandler;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.localization.Locals;
import de.keksuccino.konkrete.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;

import java.util.Arrays;
import java.util.function.Consumer;

public class VideoPropertiesPopup extends FMPopup {

    protected Popup parent;
    protected final VideoProperties props;
    protected Consumer<VideoProperties> callback;

    protected AdvancedButton doneButton;
    protected AdvancedButton cancelButton;

    protected AdvancedButton loopButton;
    protected AdvancedTextField volumeInputField;

    public VideoPropertiesPopup(Popup parent, VideoProperties props, Consumer<VideoProperties> callback) {

        super(240);
        this.parent = parent;
        this.props = props;
        this.callback = callback;

        KeyboardHandler.addKeyPressedListener(this::onEnterPressed);
        KeyboardHandler.addKeyPressedListener(this::onEscapePressed);

        this.doneButton = new AdvancedButton(0, 0, 100, 20, Locals.localize("popup.done"), true, (press) -> {
            this.onClose();
        });
        this.addButton(this.doneButton);

        this.cancelButton = new AdvancedButton(0, 0, 100, 20, Locals.localize("fancymenu.fmvideo.backgroundoptions.cancel"), true, (press) -> {
            PopupHandler.displayPopup(this.parent);
        });
        this.addButton(this.cancelButton);

        this.loopButton = new AdvancedButton(0, 0, 100, 20, "", true, (press) -> {
            if (props.looping) {
                props.looping = false;
            } else {
                props.looping = true;
            }
        }) {
            @Override
            public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                if (props.looping) {
                    this.setMessage(Locals.localize("fancymenu.fmvideo.videoproperties.option.enabled"));
                } else {
                    this.setMessage(Locals.localize("fancymenu.fmvideo.videoproperties.option.disabled"));
                }
                super.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        };
        this.addButton(this.loopButton);

        this.volumeInputField = new AdvancedTextField(Minecraft.getInstance().font, 0, 0, 100, 20, true, CharacterFilter.getIntegerCharacterFiler());
        this.volumeInputField.setMaxLength(100000);
        this.volumeInputField.setValue("" + this.props.volume);

    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, Screen renderIn) {

        super.render(matrix, mouseX, mouseY, renderIn);

        int xCenter = renderIn.width / 2;
        int yCenter = renderIn.height / 2;
        FontRenderer font = Minecraft.getInstance().font;

        this.cancelButton.setX(xCenter - (this.cancelButton.getWidth()) - 5);
        this.cancelButton.setY(yCenter + 80);

        this.doneButton.setX(xCenter + 5);
        this.doneButton.setY(yCenter + 80);

        //Loop
        String loopString = Locals.localize("fancymenu.fmvideo.videoproperties.loop");
        drawString(matrix, font, loopString, xCenter - font.width(loopString) - 10, yCenter - 19, -1);

        this.loopButton.setX(xCenter + 10);
        this.loopButton.setY(yCenter - 25);
        //------------------

        //Volume
        String volumeString = Locals.localize("fancymenu.fmvideo.videoproperties.volume");
        drawString(matrix, font, volumeString, xCenter - font.width(volumeString) - 10, yCenter + 11, -1);

        this.volumeInputField.setX(xCenter + 10);
        this.volumeInputField.setY(yCenter + 5);
        this.volumeInputField.render(matrix, mouseX, mouseY, Minecraft.getInstance().getFrameTime());
        //------------------

        this.renderButtons(matrix, mouseX, mouseY);

        //Loop Desc
        if ((mouseX >= font.width(loopString) - 10) && (mouseX <= this.loopButton.getX() + this.loopButton.getWidth()) && (mouseY >= this.loopButton.getY()) && (mouseY <= this.loopButton.getY() + this.loopButton.getHeight())) {
            BackgroundOptionsPopup.renderDescription(matrix, mouseX, mouseY, Arrays.asList(StringUtils.splitLines(Locals.localize("fancymenu.fmvideo.videoproperties.loop.desc"), "%n%")));
        }

        //Volume Desc
        if ((mouseX >= font.width(volumeString) - 10) && (mouseX <= this.volumeInputField.getX() + this.volumeInputField.getWidth()) && (mouseY >= this.volumeInputField.getY()) && (mouseY <= this.volumeInputField.getY() + this.volumeInputField.getHeight())) {
            BackgroundOptionsPopup.renderDescription(matrix, mouseX, mouseY, Arrays.asList(StringUtils.splitLines(Locals.localize("fancymenu.fmvideo.videoproperties.volume.desc"), "%n%")));
        }

    }

    protected void onClose() {
        if (MathUtils.isInteger(this.volumeInputField.getValue())) {
            this.props.volume = Integer.parseInt(this.volumeInputField.getValue());
            if (this.props.volume < 0) {
                this.props.volume = 0;
            }
            if (this.props.volume > 200) {
                this.props.volume = 200;
            }
        } else {
            this.props.volume = 100;
        }
        this.callback.accept(this.props);
        PopupHandler.displayPopup(this.parent);
    }

    protected void onEnterPressed(KeyboardData d) {
        if (d.keycode == 257 && this.isDisplayed()) {
            this.onClose();
        }
    }

    protected void onEscapePressed(KeyboardData d) {
        if (d.keycode == 256 && this.isDisplayed()) {
            this.callback.accept(null);
            PopupHandler.displayPopup(this.parent);
        }
    }

    public static class VideoProperties {

        /** Value between 0 and 200 **/
        public int volume = 100;
        public boolean looping = false;

    }

}