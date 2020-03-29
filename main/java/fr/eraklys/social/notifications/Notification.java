package fr.eraklys.social.notifications;

import java.util.ArrayList;
import java.util.List;

import fr.eraklys.util.FontRendererStringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Notification extends Widget
{
	private final List<Widget> widgets;
	
	public static final int NOTIF_WIDTH = 200;
	
	public Notification(int xIn, int yIn, int widthIn, int heightIn, String msg, Widget ... widgets) 
	{
		super(xIn, yIn, widthIn, heightIn, msg);
		this.widgets = new ArrayList<Widget>();
		
		for(Widget w : widgets)
		{
			this.widgets.add(w);
		}
	}
	
	protected void addWidget(Widget w)
	{
		this.widgets.add(w);
	}
	
	public abstract Widget defaultAction();

	public List<Widget> getWidgets() 
	{
		return widgets;
	}

	public void renderButton(int mouseX, int mouseY, float partialTick) 
	{		
		for(Widget w : this.getWidgets())
		{
			if(w.isHovered())
			{
				this.isHovered = false;
				break;
			}
		}
		
		fill(x - 1, y - 1, x + this.getWidth() + 1, y + this.getHeight() + 1, 0xffbbbbbb);
		fill(x, y, x + this.getWidth() - 1, y + this.getHeight(), !this.isHovered() ? 0xff222222 : 0xff454545);
		
		List<String> notifText = FontRendererStringUtil.splitStringMultiline(this.getWidth() - 20, this.getMessage());
		
		this.setHeight(notifText.size() * 9 + 17);
		int i = 0;
		
		for(String s : notifText)
		{
			Minecraft.getInstance().fontRenderer.drawString(s, this.x + 3, this.y + 3 + i * 9, 0xffffff);
			i++;
		}
		
		for(Widget w : this.getWidgets())
		{
			w.x += this.x;
			w.y += this.y;
			w.render(mouseX, mouseY, partialTick);
			w.x -= this.x;
			w.y -= this.y;
		}
	}
	
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) 
	{
		if (this.active && this.visible) 
		{
			if (this.isValidClickButton(p_mouseClicked_5_)) 
			{
				boolean flag = this.clicked(p_mouseClicked_1_, p_mouseClicked_3_);
				if (flag) 
				{
					this.onClick(p_mouseClicked_1_, p_mouseClicked_3_);
					return true;
				}
			}
			
			return false;
		} 
		else
		{
			return false;
		}
	}
	
	public void onClick(double p_onClick_1_, double p_onClick_3_) 
	{
		for(Widget w : this.getWidgets())
		{
			if(w.isHovered() && w.active)
			{
				w.onClick(p_onClick_1_, p_onClick_3_);
			}
		}
	}
}
