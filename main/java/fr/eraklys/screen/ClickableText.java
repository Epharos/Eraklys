package fr.eraklys.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClickableText extends Widget 
{
	private IPressable onClick;
	
	public ClickableText(int xIn, int yIn, String msg, ClickableText.IPressable press) 
	{
		super(xIn, yIn, Minecraft.getInstance().fontRenderer.getStringWidth(msg), 7, msg);
		this.onClick = press;
	}
	
	public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) 
	{
		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fontrenderer = minecraft.fontRenderer;
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
	    RenderSystem.defaultBlendFunc();
	    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		fontrenderer.drawStringWithShadow(this.getMessage(), this.x, this.y, this.isHovered() ? 0xb1b1b1 : 0xe1e1e1);
	}
	
	public void onClick(double p_onClick_1_, double p_onClick_3_) {
	      this.onClick.onPress(this);
	   }
	
	public interface IPressable
	{
		void onPress(ClickableText ct);
	}
}
