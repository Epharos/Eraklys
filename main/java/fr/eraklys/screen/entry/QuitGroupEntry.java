package fr.eraklys.screen.entry;

import fr.eraklys.screen.Menu;
import fr.eraklys.screen.MenuEntry;
import net.minecraft.client.resources.I18n;

public class QuitGroupEntry extends MenuEntry 
{
	public QuitGroupEntry(Menu menu, IEntry act) 
	{
		super(I18n.format("menu.group.quit"), menu, act);
	}
}
