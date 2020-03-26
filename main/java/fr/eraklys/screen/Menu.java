package fr.eraklys.screen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;

public class Menu extends Widget 
{
	public final List<MenuEntry> entries = new ArrayList<MenuEntry>();
	
	public Menu() 
	{
		super(0, 0, 0, 0, "");
	}
	
	public Menu addEntry(MenuEntry entry)
	{
		this.entries.add(entry);
		
		for(MenuEntry entry2 : this.entries)
		{
			entry2.setWidth(this.getMaxEntrySize());
			entry2.setHeight(this.hasNext(entry2) ? 12 : 13);			
		}
		
		this.setWidth(this.getMaxEntrySize());
		this.setHeight(this.entries.size() * 12 + 1);
		return this;
	}
	
	public void renderButton(int mouseX, int mouseY, float partialTick) 
	{
		int i = 0;
		for(MenuEntry entry : this.entries)
		{
			entry.x = this.x;
			entry.y = this.y + i;
			i += 12;
			entry.renderButton(mouseX, mouseY, partialTick);
		}
	}
	
	public void onClick(double p_onClick_1_, double p_onClick_3_) 
	{
		for(MenuEntry entry : this.entries)
		{
			if(entry.isHovered() && entry.active)
			{
				entry.onClick(p_onClick_1_, p_onClick_3_);
			}
		}
	}
	
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) 
	{
	    if (this.active && this.visible) 
	    {
	    	if(!this.isHovered())
	  		{
	  			this.visible = false;
	  			this.active = false;
	  			return false;
	  		}
	    	  
	       if (this.isValidClickButton(p_mouseClicked_5_)) 
	       {
	          boolean flag = this.clicked(p_mouseClicked_1_, p_mouseClicked_3_);
	          if (flag) 
	          {
	             this.playDownSound(Minecraft.getInstance().getSoundHandler());
	             this.onClick(p_mouseClicked_1_, p_mouseClicked_3_);
	             return true;
	          }
	       }
	
	       return false;
	    } else {
	       return false;
	    }
	}
	
	public int getMaxEntrySize()
	{
		int max = -1;
		
		for(MenuEntry entry : entries)
		{
			max = Math.max(max, Minecraft.getInstance().fontRenderer.getStringWidth(entry.getMessage()));
		}
		
		return max;
	}
	
	public Menu setPos(int i, int j)
	{
		this.x = i;
		this.y = j;
		return this;
	}
	
	public boolean hasNext(MenuEntry menuEntry) 
	{
		for(int i = 0 ; i < this.entries.size() ; i++)
		{
			if(entries.get(i) == menuEntry)
			{
				if(i != (entries.size() - 1))
				{
					return true;
				}
				
				return false;
			}
		}
		
		return false;
	}
}
