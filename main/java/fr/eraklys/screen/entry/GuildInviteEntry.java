package fr.eraklys.screen.entry;

import fr.eraklys.screen.Menu;
import fr.eraklys.screen.MenuEntry;
import net.minecraft.client.resources.I18n;

public class GuildInviteEntry extends MenuEntry 
{
	public GuildInviteEntry(Menu menu, IEntry act) 
	{
		super(I18n.format("menu.invite.guild"), menu, act);
	}
}
