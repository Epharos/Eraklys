package fr.eraklys.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import fr.eraklys.Eraklys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;

public abstract class MenuEntry extends Widget
{
	public final List<MenuEntry> entries = new ArrayList<MenuEntry>();
	private Menu menuIn;
	protected IEntry action;
	
	public MenuEntry(String msg, Menu menu, IEntry act) 
	{
		super(0, 0, 0, 0, msg);
		this.menuIn = menu;
		this.action = act;
	}		
	
	public void renderButton(int mouseX, int mouseY, float partialTick) 
	{
		this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
	    Minecraft minecraft = Minecraft.getInstance();
	    FontRenderer fontRenderer = minecraft.fontRenderer;
	    RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
	    RenderSystem.enableBlend();
	    RenderSystem.defaultBlendFunc();
	    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		fill(x, y, x + this.getWidth() + 6, y + 13, 0xff666666);
		fill(x + 1, y + 1, x + this.getWidth() + 5, y + 12, !this.isHovered() ? 0xffbbbbbb : 0xff9a9a9a);
			
		fontRenderer.drawStringWithShadow(I18n.format(this.getMessage()), x + (this.isHovered() ? 5 : 3), y + 3, 0xeeeeee);
		
		this.active = this.menuIn.active;
	}
	
	public void onClick(double p_onClick_1_, double p_onClick_3_) 
	{
		Eraklys.LOGGER.warn("Click sur " + this.getMessage());
		this.action.onPress(this);
	}
	
	public interface IEntry
	{
		void onPress(MenuEntry e);
	}
}
