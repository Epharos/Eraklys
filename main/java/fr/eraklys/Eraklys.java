package fr.eraklys;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.eraklys.player.inventory.DefaultMoneyStorage;
import fr.eraklys.player.inventory.IMoney;
import fr.eraklys.player.inventory.MoneyHolder;
import fr.eraklys.player.inventory.PlayerMoneyWrapper;
import fr.eraklys.social.groups.GroupCommand;
import fr.eraklys.social.groups.GroupScreen;
import fr.eraklys.social.groups.PacketUpdateGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
	
	@SubscribeEvent
	public void serverStarting(final FMLServerStartingEvent event)
	{
		GroupCommand.register(event.getCommandDispatcher());
	}
	
	public void registerCapabilities()
	{
		CapabilityManager.INSTANCE.register(IMoney.class, new DefaultMoneyStorage(), MoneyHolder::new);
	}
	
	public void registerNetworkPackets()
	{
		CHANNEL.messageBuilder(PacketUpdateGroup.class, 0).encoder(PacketUpdateGroup::write).decoder(PacketUpdateGroup::read).consumer(PacketUpdateGroup::handle).add();
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void renderEntityName(final RenderNameplateEvent event)
	{
		if(Minecraft.getInstance().currentScreen instanceof GroupScreen)
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
			Minecraft.getInstance().displayGuiScreen(new GroupScreen());
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
	}
}