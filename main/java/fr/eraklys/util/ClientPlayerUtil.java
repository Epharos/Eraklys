package fr.eraklys.util;

import com.mojang.blaze3d.systems.RenderSystem;

import fr.eraklys.Eraklys;
import fr.eraklys.screen.Menu;
import fr.eraklys.social.groups.PacketAcceptGroup;
import fr.eraklys.social.groups.PacketInviteGroup;
import fr.eraklys.social.groups.PlayerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPlayerUtil 
{
	public static void groupInvite(int playerID)
	{
		Eraklys.CHANNEL.sendToServer(new PacketInviteGroup(playerID));
	}
	
	public static class InteractScreen extends Screen
	{
		private AbstractClientPlayerEntity player;
		private Menu playerMenu;
		
		public InteractScreen(AbstractClientPlayerEntity player) 
		{
			super(player.getName());
			this.player = player;
			this.playerMenu = new PlayerMenu(this.player);
		}
		
		protected void init()
		{
			super.init();
			playerMenu.setPos(this.width / 2, this.height / 2);
			this.addButton(playerMenu);
		}
		
		public void render(int mouseX, int mouseY, float partialTicks) 
		{
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			super.render(mouseX, mouseY, partialTicks);
			
			if(!playerMenu.active)
			{
				Minecraft.getInstance().displayGuiScreen((Screen)null);
			}
		}
	}

	public static void acceptTrade(int senderID, boolean b) 
	{
		Eraklys.CHANNEL.sendToServer(new PacketAcceptGroup(senderID, b));
	}
}
