package com.github.whitehooder.LoreAnimations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class LoreAnimations extends JavaPlugin implements Listener {

	public List<Inventory> invlist = new ArrayList<Inventory>();
	//ItemID, FrameCount
	public HashMap<Integer, Integer> framecounter = new HashMap<Integer, Integer>();
	//int framecounter = 0;

	public HashMap<Integer, ArrayList<ArrayList<String>>> animation = new HashMap<Integer, ArrayList<ArrayList<String>>>();

	@Override
	public void onDisable() {
		
	}

	@Override
	public void onEnable() {

		/*
		 *  for (World w : getServer().getWorlds()) {
		 *	 for (Player p : w.getPlayers()) {
		 *	 	invlist.add(p.getInventory());
		 *	 }
		 *}
		 */
		for(File file : this.getDataFolder().listFiles()) {
			
			if(file.getAbsolutePath().endsWith(".txt")) {
				
				String[] split = file.getAbsolutePath().split(".");
				try {
					int itemid  = Integer.parseInt(split[0]);
					
					try {
						InputStream inputStream = new FileInputStream(file);
						InputStreamReader streamReader;
						try {
							
							streamReader = new InputStreamReader(inputStream, "UTF-8");
							final BufferedReader bufferedReader = new BufferedReader(streamReader);
							String line = "";
							
							try {
								ArrayList<String> frame = new ArrayList<String>();
								ArrayList<ArrayList<String>> frames = new ArrayList<ArrayList<String>>();
								while((line = bufferedReader.readLine()) != null) {
									if (line.equals("==FRAME==")) {
										frames.add((new ArrayList<String>(frame)));
										frame.clear();
									} else {
										
										frame.add(colorize(line));
									}
								}
								animation.put(itemid, frames);
							} catch (IOException e) {
								
								e.printStackTrace();
							}
						} catch (UnsupportedEncodingException e) {
							
							e.printStackTrace();
						}
					} catch (FileNotFoundException e) {
						
						e.printStackTrace();
					}
					
				} catch(NumberFormatException e) {
						
						System.err.println("[LoreAnimations] - The file " + file.getName() + " did not begin with an integer (itemid).");
				}
			}
		}
		
		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.runTaskTimer(this, new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (Inventory inv : invlist) {
					for (ItemStack item : inv.getContents()) {
						if (item != null) {
							if (animation.containsKey(item.getTypeId())) {
								framecounter.put(item.getTypeId(), 0);
								ItemMeta meta = item.getItemMeta();
								meta.setLore(animation.get(item.getTypeId()).get(framecounter.get(item.getTypeId()) + 1));
								framecounter.put(item.getTypeId(), framecounter.get(item.getTypeId()) + 1);
								item.setItemMeta(meta);
								if (framecounter.get(item.getTypeId()) > animation.size() - 1) {
									framecounter.put(item.getTypeId(), 0);
								}
							}
						}
					}
				}
			}
		}, 1, 1);
		getServer().getPluginManager().registerEvents(this, this);
	}

	public String colorize(String s) {
		String msg = s.replaceAll("&((?i)[0-9a-fk-or])", "\u00A7$1");
		msg = msg + "§r";
		return msg;
	}
	
	@EventHandler
	public void onOpenInventory(InventoryOpenEvent event) {
		
		invlist.add(event.getInventory());
	}
	
	@EventHandler
	public void onCloseInventory(InventoryCloseEvent event) {
		
		invlist.remove(event.getInventory());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onJoin(PlayerJoinEvent e) {
		invlist.add(e.getPlayer().getInventory());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onQuit(PlayerQuitEvent e) {
		invlist.remove(e.getPlayer().getInventory());
	}
}
