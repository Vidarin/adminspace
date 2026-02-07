package com.vidarin.adminspace.inventory.gui;

import com.vidarin.adminspace.network.SPacketCompleteInstruction;
import com.vidarin.adminspace.network.AdminspaceNetworkHandler;
import com.vidarin.adminspace.util.Fonts;
import java.io.IOException;
import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.input.Keyboard;

public class GuiInstruction extends GuiScreen {
    private static final ResourceLocation TEXTURE = new ResourceLocation("adminspace:textures/gui/instruction.png");

    private final EntityPlayer editingPlayer;
    private final ItemStack instruction;

    private boolean isModified;

    private boolean gettingSigned;

    private int updateCount;
    private int totalPages = 1;
    private int currPage;
    private NBTTagList pages;
    private String title = "";
    private GuiInstruction.NextPageButton buttonNextPage;
    private GuiInstruction.NextPageButton buttonPreviousPage;
    private GuiButton buttonDone;

    private GuiButton buttonSign;
    private GuiButton buttonFinalize;
    private GuiButton buttonCancel;

    @SuppressWarnings("DataFlowIssue")
    public GuiInstruction(EntityPlayer player, ItemStack instruction) {
        this.editingPlayer = player;
        this.instruction = instruction;

        if (instruction.hasTagCompound()) {
            NBTTagCompound compound = instruction.getTagCompound();
            this.pages = compound.getTagList("pages", Constants.NBT.TAG_STRING).copy();
            this.totalPages = this.pages.tagCount();

            if (this.totalPages < 1) {
                this.totalPages = 1;
            }
        }

        if (this.pages == null) {
            this.pages = new NBTTagList();
            this.pages.appendTag(new NBTTagString(""));
            this.totalPages = 1;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        ++this.updateCount;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);

        this.buttonSign = this.addButton(new GuiButton(3, this.width / 2 - 100, 196, 98, 20, I18n.format("instruction.signButton")));
        this.buttonDone = this.addButton(new GuiButton(0, this.width / 2 + 2, 196, 98, 20, I18n.format("gui.cancel")));
        this.buttonFinalize = this.addButton(new GuiButton(5, this.width / 2 - 100, 196, 98, 20, I18n.format("instruction.finalizeButton")));
        this.buttonCancel = this.addButton(new GuiButton(4, this.width / 2 + 2, 196, 98, 20, I18n.format("gui.cancel")));

        int i = (this.width - 192) / 2;
        this.buttonNextPage = this.addButton(new GuiInstruction.NextPageButton(1, i + 120, 156, true));
        this.buttonPreviousPage = this.addButton(new GuiInstruction.NextPageButton(2, i + 38, 156, false));
        this.updateButtons();
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    private void updateButtons() {
        this.buttonNextPage.visible = !this.gettingSigned;
        this.buttonPreviousPage.visible = !this.gettingSigned && this.currPage > 0;
        this.buttonDone.visible = !this.gettingSigned;

        this.buttonSign.visible = !this.gettingSigned;
        this.buttonCancel.visible = this.gettingSigned;
        this.buttonFinalize.visible = this.gettingSigned;
        this.buttonFinalize.enabled = !this.title.trim().isEmpty();
    }

    @SuppressWarnings("DataFlowIssue")
    private void sendBookToServer() {
        if (this.isModified) {
            if (this.pages != null) {
                while (this.pages.tagCount() > 1) {
                    String pageData = this.pages.getStringTagAt(this.pages.tagCount() - 1);

                    if (!pageData.isEmpty()) {
                        break;
                    }

                    this.pages.removeTag(this.pages.tagCount() - 1);
                }

                if (!this.instruction.hasTagCompound()) {
                    this.instruction.setTagCompound(new NBTTagCompound());
                }

                NBTTagCompound compound = this.instruction.getTagCompound();
                compound.setTag("pages", this.pages);

                compound.setString("author", this.editingPlayer.getName());
                compound.setString("title", this.title.trim());
                compound.setBoolean("resolved", false);

                AdminspaceNetworkHandler.INSTANCE.sendToServer(new SPacketCompleteInstruction(this.instruction));
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 0) this.mc.displayGuiScreen(null);
            else if (button.id == 3) this.gettingSigned = true;
            else if (button.id == 1) {
                if (this.currPage < this.totalPages - 1) ++this.currPage;
                else {
                    this.addNewPage();

                    if (this.currPage < this.totalPages - 1) ++this.currPage;
                }
            }
            else if (button.id == 2) {
                if (this.currPage > 0) --this.currPage;
            }
            else if (button.id == 5 && this.gettingSigned) {
                this.sendBookToServer();
                this.mc.displayGuiScreen(null);
            }
            else if (button.id == 4 && this.gettingSigned) this.gettingSigned = false;

            this.updateButtons();
        }
    }

    private void addNewPage() {
        if (this.pages != null && this.pages.tagCount() < 50) {
            this.pages.appendTag(new NBTTagString(""));
            ++this.totalPages;
            this.isModified = true;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (this.gettingSigned) this.keyTypedInTitle(typedChar, keyCode);
        else this.keyTypedInBook(typedChar, keyCode);
    }

    private void keyTypedInBook(char typedChar, int keyCode) {
        if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            this.pageInsertIntoCurrent(GuiScreen.getClipboardString());
        }
        else {
            switch (keyCode) {
                case 14:
                    String pageData = this.pageGetCurrent();

                    if (!pageData.isEmpty()) {
                        this.pageSetCurrent(pageData.substring(0, pageData.length() - 1));
                    }

                    return;

                case 28:
                case 156:
                    this.pageInsertIntoCurrent("\n");
                    return;

                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) this.pageInsertIntoCurrent(Character.toString(typedChar));

            }
        }
    }

    private void keyTypedInTitle(char typedChar, int keyCode) {
        switch (keyCode) {
            case 14:
                if (!this.title.isEmpty()) {
                    this.title = this.title.substring(0, this.title.length() - 1);
                    this.updateButtons();
                }

                return;

            case 28:
            case 156:
                if (!this.title.isEmpty()) {
                    this.sendBookToServer();
                    this.mc.displayGuiScreen(null);
                }

                return;

            default:
                if (this.title.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    this.title = this.title + typedChar;
                    this.updateButtons();
                    this.isModified = true;
                }
        }
    }

    private String pageGetCurrent() {
        return this.pages != null && this.currPage >= 0 && this.currPage < this.pages.tagCount() ? this.pages.getStringTagAt(this.currPage) : "";
    }

    private void pageSetCurrent(String content) {
        if (this.pages != null && this.currPage >= 0 && this.currPage < this.pages.tagCount()) {
            this.pages.set(this.currPage, new NBTTagString(content));
            this.isModified = true;
        }
    }

    private void pageInsertIntoCurrent(String content) {
        String pageData = this.pageGetCurrent();
        String newData = pageData + content;
        int wordWrappedHeight = this.fontRenderer.getWordWrappedHeight(newData + Fonts.Black + "_", 118);

        if (wordWrappedHeight <= 128 && newData.length() < 256) {
            this.pageSetCurrent(newData);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int guiCenter = (this.width - 192) / 2;
        this.drawTexturedModalRect(guiCenter, 2, 0, 0, 192, 192);

        if (this.gettingSigned) {
            String title = this.title;

            if (this.updateCount / 6 % 2 == 0) title = title + Fonts.Black + "_";
            else title = title + Fonts.Gray + "_";

            String editText = I18n.format("book.editTitle");
            int editTextWidth = this.fontRenderer.getStringWidth(editText);
            this.fontRenderer.drawString(editText, guiCenter + 36 + (116 - editTextWidth) / 2, 34, 0);
            int titleWidth = this.fontRenderer.getStringWidth(title);
            this.fontRenderer.drawString(title, guiCenter + 36 + (116 - titleWidth) / 2, 50, 0);
            String authorText = I18n.format("book.byAuthor", this.editingPlayer.getName());
            int authorTextWidth = this.fontRenderer.getStringWidth(authorText);
            this.fontRenderer.drawString(Fonts.DarkGray + authorText, guiCenter + 36 + (116 - authorTextWidth) / 2, 60, 0);
            String finalizeWarning = I18n.format("instruction.finalizeWarning");
            this.fontRenderer.drawSplitString(finalizeWarning, guiCenter + 36, 82, 116, 0);
        } else {
            String pageIndicator = I18n.format("book.pageIndicator", this.currPage + 1, this.totalPages);
            String currentText = "";

            if (this.pages != null && this.currPage >= 0 && this.currPage < this.pages.tagCount()) currentText = this.pages.getStringTagAt(this.currPage);

            if (this.fontRenderer.getBidiFlag()) currentText = currentText + "_";
            else if (this.updateCount / 6 % 2 == 0) currentText = currentText + Fonts.Black + "_";
            else currentText = currentText + Fonts.Gray + "_";

            int pageIndicatorWidth = this.fontRenderer.getStringWidth(pageIndicator);
            this.fontRenderer.drawString(pageIndicator, guiCenter - pageIndicatorWidth + 192 - 44, 18, 0);

            this.fontRenderer.drawSplitString(currentText, guiCenter + 36, 34, 116, 0);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean handleComponentClick(ITextComponent component) {
        ClickEvent event = component.getStyle().getClickEvent();

        if (event == null) return false;
        else if (event.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String pageString = event.getValue();

            try {
                int page = Integer.parseInt(pageString) - 1;

                if (page >= 0 && page < this.totalPages && page != this.currPage)
                {
                    this.currPage = page;
                    this.updateButtons();
                    return true;
                }
            }
            catch (Throwable ignored) {}

            return false;
        }
        else
        {
            boolean clicked = super.handleComponentClick(component);

            if (clicked && event.getAction() == ClickEvent.Action.RUN_COMMAND) this.mc.displayGuiScreen(null);

            return clicked;
        }
    }

    static class NextPageButton extends GuiButton {
        private final boolean isForward;

        public NextPageButton(int id, int x, int y, boolean isForward)
        {
            super(id, x, y, 23, 13, "");
            this.isForward = isForward;
        }

        @Override
        public void drawButton(@Nonnull Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
                boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                minecraft.getTextureManager().bindTexture(GuiInstruction.TEXTURE);
                int textureX = 0;
                int textureY = 192;

                if (hovered) textureX += 23;

                if (!this.isForward) textureY += 13;

                this.drawTexturedModalRect(this.x, this.y, textureX, textureY, 23, 13);
            }
        }
    }
}