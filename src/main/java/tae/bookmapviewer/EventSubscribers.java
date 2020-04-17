package tae.bookmapviewer;

import java.awt.Color;

import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventSubscribers {

	/**
	 * TheAlphaEpsilon
	 * 17 April 2020
	 */
	
	private BookRenderer toRender = null;
	
	//Cancel normal tooltips
	@SubscribeEvent
	public void onHover(RenderTooltipEvent.Pre event) {
		Item item = event.getStack().getItem();
		if(!(Minecraft.getMinecraft().currentScreen instanceof GuiContainer)) {
			return;
		} else if(item != null && (item instanceof ItemMap || item instanceof ItemWrittenBook)) {
			event.setCanceled(true);
		}
	}
	
	//To draw new gui
	@SubscribeEvent
	public void drawNewGui(GuiScreenEvent.DrawScreenEvent.Post event) {
		if(!(Minecraft.getMinecraft().currentScreen instanceof GuiContainer)) {
			return;
		}
		
		GlStateManager.disableDepth();

		GuiContainer container = (GuiContainer) Minecraft.getMinecraft().currentScreen;
		
		Slot hovered = container.getSlotUnderMouse();
		
		if(hovered != null) {
			
			Item item = hovered.getStack().getItem();
			
			if(item instanceof ItemMap) {
				drawMap((ItemStack)hovered.getStack(), (ItemMap)item, event.getMouseX(), event.getMouseY());
			} else if(item instanceof ItemWrittenBook) {
				
				if(toRender == null || (toRender != null && !toRender.isNBTSame(hovered.getStack().getTagCompound()))) {
					toRender = new BookRenderer(hovered.getStack(), 100, 10, 2);
				} 
				toRender.draw(event.getMouseX(), event.getMouseY());
			}
			
		}
	}
	
	//Draws the map on screen
	private void drawMap(ItemStack mapStack, ItemMap map, int x, int y) {
		
		final double scale = 2;
		
		x = (int) (x * scale + 5);
		y = (int) (y * scale - 128);
		
		GlStateManager.scale(1 / scale, 1 / scale, 1 / scale);
		
		byte[] colors = map.getMapData(mapStack, Minecraft.getMinecraft().world).colors;
		
		for (int i = 0; i < colors.length; ++i) {
			
            int j = colors[i] & 255;

            if (j / 4 == 0) {
                drawPixel(i % 128 + x, i / 128 + y, (i + i / 128 & 1) * 8 + 16 << 24);
            }
            else {
                drawPixel(i % 128 + x, i / 128 + y, MapColor.COLORS[j / 4].getMapColor(j & 3));
            }
        }
		
		GlStateManager.scale(scale, scale, scale);
		
	}
	//helper
	private void drawPixel(int x, int y, int color) {
		Gui.drawRect(x, y, x+1, y+1, color);
	}
	
	//Draw the big book blob
	static class BookRenderer extends Gui {
		
		private static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		
		private String author;
		private String title;
		private String[] pages;
		private NBTTagCompound nbt;
		private int width;
		private int padding;
		private double scale;
		
		private BookRenderer(ItemStack bookStack, int width, int padding, double scale) {
			this.nbt = bookStack.getTagCompound();
			author = nbt.getString("author");
			title = nbt.getString("title");
			pages = getPageText(nbt);
			this.scale = scale;
			this.width = width;
			this.padding = padding;
			
			//Auto resize
			while(height() > this.width * 2) {
				this.width *= 2;
			}
		}
		
		//Draw white and call text
		private void draw(int x, int y) {
			
			GlStateManager.scale(1D / scale, 1D / scale, 1D / scale);
			
			x *= scale;
			y *= scale;
			
			drawRect(x, y - height() - padding * 2, x + width + padding * 2, y, Color.WHITE.getRGB());
			
			drawText(x + padding, y - height() - padding);
			
			GlStateManager.scale(scale, scale, scale);
			
		}
		
		private void drawText(int x, int y) {
						
			fontRenderer.drawString(title, x + width / 2 - fontRenderer.getStringWidth(title) / 2, y, 0);
			fontRenderer.drawString("By: " + author, x + width / 2 - fontRenderer.getStringWidth("By: " + author) / 2, y + fontRenderer.FONT_HEIGHT, 0);
			
			fontRenderer.drawSplitString(arrayToString(pages), x, y + fontRenderer.FONT_HEIGHT * 2, width, 0);
						
		}
		
		private int height() {
			
			int textHeight = fontRenderer.getWordWrappedHeight(arrayToString(pages), width);
			
			return fontRenderer.FONT_HEIGHT * 2 + textHeight;
			
		}

		private String arrayToString(String[] array) {
			StringBuffer buff = new StringBuffer();
			for(int i = 0; i < array.length; i++) {
				buff.append(array[i]);
				buff.append(' ');
			}
			return buff.toString();
		}
		
		private String[] getPageText(NBTTagCompound nbt) {
			
			NBTTagList pages = nbt.getTagList("pages", 8);
			
			String[] toReturn = new String[pages.tagCount()];
			
			for(int i = 0; i < pages.tagCount(); i++) {
				
				NBTTagString index = (NBTTagString)pages.get(i);
							
				String raw = index.getString();
							
				try {
					
					NBTTagCompound tag = JsonToNBT.getTagFromJson(raw);
					
					String text = tag.getString("text");
					
					toReturn[i] = text;
					
				} catch (NBTException e) {}
				
			}
			
			return toReturn;
		}
		
		private boolean isNBTSame(NBTTagCompound other) {
			return nbt.equals(other);
		}
		
	}
	
}
