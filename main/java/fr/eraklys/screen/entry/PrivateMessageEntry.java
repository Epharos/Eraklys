package fr.eraklys.screen.entry;

import fr.eraklys.screen.Menu;
import fr.eraklys.screen.MenuEntry;
import net.minecraft.client.resources.I18n;

public class PrivateMessageEntry extends MenuEntry 
{
	public PrivateMessageEntry(Menu menu, IEntry act) 
	{
		super(I18n.format("menu.private"), menu, act);
	}
}
