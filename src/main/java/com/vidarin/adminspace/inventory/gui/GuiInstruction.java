package com.vidarin.adminspace.inventory.gui;

import com.vidarin.adminspace.network.SPacketCompleteInstruction;
import com.vidarin.adminspace.network.AdminspaceNetworkHandler;
import com.vidarin.adminspace.util.Fonts;
import io.netty.buffer.Unpooled;
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
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import org.lwjgl.input.Keyboard;

public class GuiInstruction extends GuiScreen
{
    private static final ResourceLocation BOOK_GUI_TEXTURES = new ResourceLocation("adminspace:textures/gui/instruction.png");

    private final EntityPlayer editingPlayer;
    private final ItemStack bookObj;

    private boolean bookIsModified;

    private boolean bookGettingSigned;

    private int updateCount;
    private int bookTotalPages = 1;
    private int currPage;
    private NBTTagList bookPages;
    private String bookTitle = "";
    private GuiInstruction.NextPageButton buttonNextPage;
    private GuiInstruction.NextPageButton buttonPreviousPage;
    private GuiButton buttonDone;

    private GuiButton buttonSign;
    private GuiButton buttonFinalize;
    private GuiButton buttonCancel;

    @SuppressWarnings("DataFlowIssue")
    public GuiInstruction(EntityPlayer player, ItemStack book) {
        this.editingPlayer = player;
        this.bookObj = book;

        if (book.hasTagCompound()) {
            NBTTagCompound nbttagcompound = book.getTagCompound();
            this.bookPages = nbttagcompound.getTagList("pages", 8).copy();
            this.bookTotalPages = this.bookPages.tagCount();

            if (this.bookTotalPages < 1) {
                this.bookTotalPages = 1;
            }
        }

        if (this.bookPages == null) {
            this.bookPages = new NBTTagList();
            this.bookPages.appendTag(new NBTTagString(""));
            this.bookTotalPages = 1;
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
        this.buttonNextPage.visible = !this.bookGettingSigned;
        this.buttonPreviousPage.visible = !this.bookGettingSigned && this.currPage > 0;
        this.buttonDone.visible = !this.bookGettingSigned;

        this.buttonSign.visible = !this.bookGettingSigned;
        this.buttonCancel.visible = this.bookGettingSigned;
        this.buttonFinalize.visible = this.bookGettingSigned;
        this.buttonFinalize.enabled = !this.bookTitle.trim().isEmpty();
    }

    @SuppressWarnings("DataFlowIssue")
    private void sendBookToServer() {
        if (this.bookIsModified) {
            if (this.bookPages != null) {
                while (this.bookPages.tagCount() > 1) {
                    String pageData = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1);

                    if (!pageData.isEmpty()) {
                        break;
                    }

                    this.bookPages.removeTag(this.bookPages.tagCount() - 1);
                }

                if (!this.bookObj.hasTagCompound()) {
                    this.bookObj.setTagCompound(new NBTTagCompound());
                }

                NBTTagCompound compound = this.bookObj.getTagCompound();
                compound.setTag("pages", this.bookPages);

                compound.setString("author", this.editingPlayer.getName());
                compound.setString("title", this.bookTitle.trim());

                PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                buffer.writeItemStack(this.bookObj);
                AdminspaceNetworkHandler.INSTANCE.sendToServer(new SPacketCompleteInstruction(this.bookObj));
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 0) this.mc.displayGuiScreen(null);
            else if (button.id == 3) this.bookGettingSigned = true;
            else if (button.id == 1) {
                if (this.currPage < this.bookTotalPages - 1) ++this.currPage;
                else {
                    this.addNewPage();

                    if (this.currPage < this.bookTotalPages - 1) ++this.currPage;
                }
            }
            else if (button.id == 2) {
                if (this.currPage > 0) --this.currPage;
            }
            else if (button.id == 5 && this.bookGettingSigned) {
                this.sendBookToServer();
                this.mc.displayGuiScreen(null);
            }
            else if (button.id == 4 && this.bookGettingSigned) this.bookGettingSigned = false;

            this.updateButtons();
        }
    }

    private void addNewPage() {
        if (this.bookPages != null && this.bookPages.tagCount() < 50) {
            this.bookPages.appendTag(new NBTTagString(""));
            ++this.bookTotalPages;
            this.bookIsModified = true;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (this.bookGettingSigned) this.keyTypedInTitle(typedChar, keyCode);
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
                if (!this.bookTitle.isEmpty()) {
                    this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
                    this.updateButtons();
                }

                return;

            case 28:
            case 156:
                if (!this.bookTitle.isEmpty()) {
                    this.sendBookToServer();
                    this.mc.displayGuiScreen(null);
                }

                return;

            default:
                if (this.bookTitle.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    this.bookTitle = this.bookTitle + typedChar;
                    this.updateButtons();
                    this.bookIsModified = true;
                }
        }
    }

    private String pageGetCurrent() {
        return this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount() ? this.bookPages.getStringTagAt(this.currPage) : "";
    }

    private void pageSetCurrent(String content) {
        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            this.bookPages.set(this.currPage, new NBTTagString(content));
            this.bookIsModified = true;
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
        this.mc.getTextureManager().bindTexture(BOOK_GUI_TEXTURES);
        int guiCenter = (this.width - 192) / 2;
        this.drawTexturedModalRect(guiCenter, 2, 0, 0, 192, 192);

        if (this.bookGettingSigned) {
            String title = this.bookTitle;

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
        }
        else {
            String pageIndicator = I18n.format("book.pageIndicator", this.currPage + 1, this.bookTotalPages);
            String currentText = "";

            if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) currentText = this.bookPages.getStringTagAt(this.currPage);

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

                if (page >= 0 && page < this.bookTotalPages && page != this.currPage)
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
                minecraft.getTextureManager().bindTexture(GuiInstruction.BOOK_GUI_TEXTURES);
                int textureX = 0;
                int textureY = 192;

                if (hovered) textureX += 23;

                if (!this.isForward) textureY += 13;

                this.drawTexturedModalRect(this.x, this.y, textureX, textureY, 23, 13);
            }
        }
    }
}