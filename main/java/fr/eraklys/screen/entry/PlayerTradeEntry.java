package fr.eraklys.screen.entry;

import fr.eraklys.screen.Menu;
import fr.eraklys.screen.MenuEntry;
import net.minecraft.client.resources.I18n;

public class PlayerTradeEntry extends MenuEntry 
{
	public PlayerTradeEntry(Menu menu, IEntry act) 
	{
		super(I18n.format("menu.trade.player"), menu, act);
	}
}
