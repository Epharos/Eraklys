package fr.eraklys.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import fr.eraklys.social.groups.GroupSession;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

//TEMP
public class GroupMessageCommand
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(
				Commands.literal("group")
				.then(Commands.argument("message", StringArgumentType.greedyString())
						.executes(ctx -> messageGroup(ctx.getSource().asPlayer(), StringArgumentType.getString(ctx, "message")))));
	}

	private static int messageGroup(ServerPlayerEntity asPlayer, String message) 
	{
		GroupSession session = GroupSession.getPlayerGroup(asPlayer);
		
		if(session != null)
		{
			for(ServerPlayerEntity player : session.getMemberList())
			{
				player.sendMessage(new TranslationTextComponent("group.label.chat").appendSibling(new StringTextComponent(asPlayer.getName().getString() + " : " + message)));
			}
		}
		else
		{
			asPlayer.sendMessage(new TranslationTextComponent("group.error.nogroup"));
		}
		
		return 0;
	}
}
