package fr.eraklys.util;

import fr.eraklys.social.groups.GroupSession;
import net.minecraft.entity.player.ServerPlayerEntity;

public class ServerPlayerUtil 
{
	public static void groupInvite(ServerPlayerEntity sender, ServerPlayerEntity target)
	{
		new GroupSession.PendingRequest(sender, target);
	}
}
