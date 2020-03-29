package fr.eraklys;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends Proxy 
{
	public static final KeyBinding showGroup = new KeyBinding("key.group", GLFW.GLFW_KEY_G, "key.categories." + Eraklys.MODID);
	public static final KeyBinding showNotif = new KeyBinding("key.notif", GLFW.GLFW_KEY_N, "key.categories." + Eraklys.MODID);
	
	static
	{
		ClientRegistry.registerKeyBinding(showGroup);
		ClientRegistry.registerKeyBinding(showNotif);
	}
	
	public ClientProxy()
	{
		
	}
}
