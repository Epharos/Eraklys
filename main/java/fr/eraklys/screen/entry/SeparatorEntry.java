package fr.eraklys.screen.entry;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import fr.eraklys.screen.Menu;
import fr.eraklys.screen.MenuEntry;

public class SeparatorEntry extends MenuEntry 
{
	public SeparatorEntry(Menu menu) 
	{
		super("", menu, (ent) -> {});
	}
	
	public void renderButton(int mouseX, int mouseY, float partialTick) 
	{
		this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
	    RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
	    RenderSystem.enableBlend();
	    RenderSystem.defaultBlendFunc();
	    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		fill(x, y, x + this.getWidth() + 6, y, 0xff666666);
		
		this.active = this.menuIn.active;
	}
}
