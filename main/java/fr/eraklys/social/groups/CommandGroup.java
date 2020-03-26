package fr.eraklys.social.groups;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

//TEMP
public class CommandGroup
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(
				Commands.literal("group")
				.then(Commands.literal("send")
						.then(Commands.argument("player", EntityArgument.player())
								.executes(ctx -> groupRequest(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))))
		);
	}

	private static int groupRequest(CommandSource source, ServerPlayerEntity player) throws CommandSyntaxException 
	{			
		if(GroupSession.getPlayerGroup(source.asPlayer()) == null)
		{
			GroupSession.createGroup(source.asPlayer());
		}
		
		GroupSession.getPlayerGroup(source.asPlayer()).addPlayer(player);
		
		return 0;
	}
}
