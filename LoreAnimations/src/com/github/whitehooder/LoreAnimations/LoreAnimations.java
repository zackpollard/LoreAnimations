package com.github.whitehooder.LoreAnimations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class LoreAnimations extends JavaPlugin implements Listener {

	List<Inventory> invlist = new ArrayList<Inventory>();
	int framecounter = 0;

	ArrayList<String> frame = new ArrayList<String>();

	ArrayList<ArrayList<String>> animation = new ArrayList<ArrayList<String>>();

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {

		for (World w : getServer().getWorlds()) {
			for (Player p : w.getPlayers()) {
				invlist.add(p.getInventory());
			}
		}

		File apple = new File(getDataFolder().getPath() + File.separator
				+ "apple.txt");
		try {
			// final StringBuilder stringBuilder = new StringBuilder();
			InputStream inStream = new FileInputStream(getDataFolder()
					+ File.separator + "apple.txt");
			InputStreamReader streamReader;
			try {
				streamReader = new InputStreamReader(inStream, "UTF-8");
				final BufferedReader bufferedReader = new BufferedReader(
						streamReader);
				String line = "";
				try {
					while ((line = bufferedReader.readLine()) != null) {
						if (line.equals("==FRAME==")) {
							animation.add(new ArrayList<String>(frame));
							frame.clear();
						} else {
							frame.add(colorize(line));
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// BufferedReader reader = new BufferedReader(new FileReader(
			// getDataFolder() + File.separator + "apple.txt"));
			// String str = null;
			// try {
			// while ((str = reader.readLine()) != null) {
			// if (str.equals("==FRAME==")) {
			// animation.add(new ArrayList<String>(frame));
			// frame.clear();
			// } else {
			//
			// frame.add(colorize(line));
			// }
			// }
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				for (Inventory inv : invlist) {
					for (ItemStack item : inv.getContents()) {
						if (item != null) {
							if (item.getType() == Material.APPLE) {
								ItemMeta meta = item.getItemMeta();
								meta.setLore(animation.get(framecounter++));
								item.setItemMeta(meta);
								if (framecounter > animation.size() - 1) {
									framecounter = 0;
								}
							}
						}
					}
				}
			}
		}, 2, 2);
		getServer().getPluginManager().registerEvents(this, this);
	}

	public String colorize(String s) {
		String msg = s.replaceAll("&((?i)[0-9a-fk-or])", "\u00A7$1");
		msg = msg + "§r";
		return msg;
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
