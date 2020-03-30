package fr.eraklys.util;

import fr.eraklys.economy.trading.ContainerTrading;
import fr.eraklys.social.groups.GroupSession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerPlayerUtil 
{
	public static void groupInvite(ServerPlayerEntity sender, ServerPlayerEntity target)
	{
		new GroupSession.PendingRequest(sender, target);
	}

    public static void openTradingScreen(ServerPlayerEntity p)
    {
    	NetworkHooks.openGui(p, new INamedContainerProvider() 
    		{
				public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity player) 
				{
					return new ContainerTrading(windowID, player);
				}
	
				public ITextComponent getDisplayName() {
					return new TranslationTextComponent("trade.label");
				}
    		});
    }
}
