package tae.bookmapviewer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = BookMapViewer.MODID, version = BookMapViewer.VERSION, useMetadata = true)
public class BookMapViewer {
	public static final String MODID = "taebookmapviewer";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void preInit(FMLInitializationEvent event) {
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {    	
		MinecraftForge.EVENT_BUS.register(new EventSubscribers());
    }
}
