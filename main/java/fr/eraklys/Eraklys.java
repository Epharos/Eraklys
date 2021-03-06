package fr.eraklys;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.eraklys.commands.GroupMessageCommand;
import fr.eraklys.commands.PrivateMessageCommand;
import fr.eraklys.economy.trading.ContainerTrading;
import fr.eraklys.economy.trading.PacketTraderName;
import fr.eraklys.economy.trading.PacketUpdateTradingInventory;
import fr.eraklys.player.inventory.DefaultMoneyStorage;
import fr.eraklys.player.inventory.IMoney;
import fr.eraklys.player.inventory.MoneyHolder;
import fr.eraklys.player.inventory.PlayerMoneyWrapper;
import fr.eraklys.social.groups.GroupSession;
import fr.eraklys.social.groups.PacketAcceptGroup;
import fr.eraklys.social.groups.PacketInviteGroup;
import fr.eraklys.social.groups.PacketKickGroupPlayer;
import fr.eraklys.social.groups.PacketUpdateGroup;
import fr.eraklys.social.groups.ScreenGroup;
import fr.eraklys.social.notifications.ScreenNotification;
import fr.eraklys.util.ClientPlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(Eraklys.MODID)
public class Eraklys
{
	public static final String MODID = "eraklys";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	public static Proxy proxy;
	
	//--- CAPABILITIES ---
	
	@CapabilityInject(IMoney.class)
	public static final Capability<IMoney> MONEY_CAPABILITY = null;
	public static final ResourceLocation MONEY_KEY = new ResourceLocation(Eraklys.MODID, "money");
	private static final Map<PlayerEntity, IMoney> INVALIDATED_MONEY = new WeakHashMap<>();
	
	//--- CHANNELS ---
	
	public static final String PROTOCOL_VERSION = String.valueOf(1);
	
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(Eraklys.MODID, "eraklys"))
			.networkProtocolVersion(() -> Eraklys.PROTOCOL_VERSION)
			.clientAcceptedVersions(Eraklys.PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(Eraklys.PROTOCOL_VERSION::equals)
			.simpleChannel();
	
	//--- CONTAINERS ---
	
	public static ContainerType<?> tradingContainer;
	
	//--- OTHERS ---
	
	//--- --- --- ---
	
	public Eraklys() 
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void commonSetup(final FMLCommonSetupEvent event)
	{
		proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
		
		this.registerCapabilities();
		this.registerNetworkPackets();
	}
	
	public void clientSetup(final FMLClientSetupEvent event)
	{
		
	}
	
	public void serverSetup(final FMLDedicatedServerSetupEvent event)
	{
		
	}
	
	@OnlyIn(Dist.DEDICATED_SERVER)
	@SubscribeEvent
	public void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event)
	{
		GroupSession group = GroupSession.getPlayerGroup((ServerPlayerEntity)(event.getPlayer()));
		
		if(group != null)
		{
			group.removePlayer((ServerPlayerEntity)(event.getPlayer()));
		}
		
		GroupSession.PendingRequest.destroyPendingsFor((ServerPlayerEntity)(event.getPlayer()));
	}
	
	@SubscribeEvent
	public void serverStarting(final FMLServerStartingEvent event)
	{
		GroupMessageCommand.register(event.getCommandDispatcher());
		PrivateMessageCommand.register(event.getCommandDispatcher());
	}
	
	public void registerCapabilities()
	{
		CapabilityManager.INSTANCE.register(IMoney.class, new DefaultMoneyStorage(), MoneyHolder::new);
	}
	
	public void registerNetworkPackets()
	{
		int i = 0;
		CHANNEL.messageBuilder(PacketUpdateGroup.class, i++).encoder(PacketUpdateGroup::write).decoder(PacketUpdateGroup::read).consumer(PacketUpdateGroup::handle).add();
		CHANNEL.messageBuilder(PacketAcceptGroup.class, i++).encoder(PacketAcceptGroup::write).decoder(PacketAcceptGroup::read).consumer(PacketAcceptGroup::handle).add();
		CHANNEL.messageBuilder(PacketInviteGroup.class, i++).encoder(PacketInviteGroup::write).decoder(PacketInviteGroup::read).consumer(PacketInviteGroup::handle).add();
		CHANNEL.messageBuilder(PacketKickGroupPlayer.class, i++).encoder(PacketKickGroupPlayer::write).decoder(PacketKickGroupPlayer::read).consumer(PacketKickGroupPlayer::handle).add();
		CHANNEL.messageBuilder(PacketUpdateTradingInventory.class, i++).encoder(PacketUpdateTradingInventory::write).decoder(PacketUpdateTradingInventory::read).consumer(PacketUpdateTradingInventory::handle).add();
		CHANNEL.messageBuilder(PacketTraderName.class, i++).encoder(PacketTraderName::write).decoder(PacketTraderName::read).consumer(PacketTraderName::handle).add();
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void renderEntityName(final RenderNameplateEvent event)
	{
		if(Minecraft.getInstance().currentScreen instanceof ScreenGroup)
		{
			event.setResult(Result.DENY);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onClientTickEvent(final ClientTickEvent event)
	{
		if(ClientProxy.showGroup.isPressed())
		{
			Minecraft.getInstance().displayGuiScreen(new ScreenGroup());
		}
		
		if(ClientProxy.showNotif.isPressed())
		{
			Minecraft.getInstance().displayGuiScreen(new ScreenNotification());
		}
	}
	
	@OnlyIn(Dist.DEDICATED_SERVER)
	@SubscribeEvent
	public void onServerTickEvent(final ServerTickEvent event)
	{
		GroupSession.PendingRequest.checkPendings();
	}
	
	
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onPlayerInteractWithEntity(final PlayerInteractEvent.EntityInteract event)
	{		
		if(event.getTarget() instanceof PlayerEntity)
		{
			if(event.getWorld().isRemote())
			{
				if(event.getPlayer().isCrouching())
				{
					Minecraft.getInstance().displayGuiScreen(new ClientPlayerUtil.InteractScreen((AbstractClientPlayerEntity)event.getTarget()));
				}
			}
		}
	}
	
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents
	{
		@SubscribeEvent
		public static void attachToEntities(final AttachCapabilitiesEvent<Entity> event)
		{
			if(event.getObject() instanceof PlayerEntity && !event.getObject().getEntityWorld().isRemote())
			{
				PlayerMoneyWrapper wrapper = new PlayerMoneyWrapper();
				event.addCapability(MONEY_KEY, wrapper);
				event.addListener(() -> wrapper.getCapability(Eraklys.MONEY_CAPABILITY).ifPresent(cap -> INVALIDATED_MONEY.put((PlayerEntity) event.getObject(), cap)));
			}
		}
		
		@SubscribeEvent
		public static void copyCapabilities(final PlayerEvent.Clone event)
		{
			if(event.isWasDeath())
			{
				event.getEntity().getCapability(Eraklys.MONEY_CAPABILITY).ifPresent(copyCap -> {
					if(INVALIDATED_MONEY.containsKey(event.getOriginal()))
					{
						INBT nbt = Eraklys.MONEY_CAPABILITY.writeNBT(INVALIDATED_MONEY.get(event.getOriginal()), null);
						Eraklys.MONEY_CAPABILITY.readNBT(copyCap, null, nbt);
					}
				});
			}
		}
		
		@SubscribeEvent
        public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event)
        {
        	event.getRegistry().registerAll(
        				tradingContainer = IForgeContainerType.create(ContainerTrading::new).setRegistryName(new ResourceLocation(Eraklys.MODID, "container_trading"))
        			);
        }
	}
}