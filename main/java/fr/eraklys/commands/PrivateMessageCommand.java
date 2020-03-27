package fr.eraklys.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class PrivateMessageCommand 
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(
				Commands.literal("mp")
				.then(Commands.argument("player", EntityArgument.player())
						.then(Commands.argument("message", StringArgumentType.greedyString())
								.executes(ctx -> sendPrivateMessage(ctx.getSource().asPlayer(), EntityArgument.getPlayer(ctx, "player"), StringArgumentType.getString(ctx, "message")))))
		);
	}

	private static int sendPrivateMessage(ServerPlayerEntity sender, ServerPlayerEntity receiver, String message) 
	{
		sender.sendMessage(new TranslationTextComponent("private.label.chat.sender", receiver.getName().getString(), message));
		receiver.sendMessage(new TranslationTextComponent("private.label.chat.receiver", sender.getName().getString(), message));
		return 1;
	}
}
