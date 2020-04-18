package tae.bookmapviewer;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = BookMapViewer.MODID, version = BookMapViewer.VERSION, useMetadata = true)
public class BookMapViewer {
	public static final String MODID = "taebookmapviewer";
    public static final String VERSION = "1.0";
    
	static final KeyBinding keybind = new KeyBinding("Key to press", 0, "Book And Map Visualizer");
    
    @EventHandler
    public void preInit(FMLInitializationEvent event) {
	    ClientRegistry.registerKeyBinding(keybind);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {    	
		MinecraftForge.EVENT_BUS.register(new EventSubscribers());
    }
}
