package fr.eraklys.screen.entry;

import fr.eraklys.screen.Menu;
import fr.eraklys.screen.MenuEntry;
import net.minecraft.client.resources.I18n;

public class KickPlayerGroupEntry extends MenuEntry
{
	public KickPlayerGroupEntry(Menu menu, IEntry act) 
	{
		super(I18n.format("menu.group.kick.player"), menu, act);
	}
}
