package com.etriacraft.customapples;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CustomApples extends JavaPlugin implements Listener {

	public static Logger log;
	public static CustomApples instance;

	File configFile;
	FileConfiguration config;

	Commands cmd;

	public void onEnable() {
		instance = this;

		this.log = this.getLogger();

		configFile = new File(getDataFolder(), "config.yml");
		try {
			firstRun();
		} catch (Exception e) {
			e.printStackTrace();
		}

		config = new YamlConfiguration();
		loadYamls();

		this.getServer().getPluginManager().registerEvents(this, this);

		cmd = new Commands(this);
	}

	@EventHandler
	public void onMove (PlayerItemConsumeEvent e) {
		final Player player = e.getPlayer();
		ItemStack is = e.getItem();
		if (is.getType() == Material.GOLDEN_APPLE) {
			if (is.getData().getData() == 1) {
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						if (player.hasPotionEffect(PotionEffectType.ABSORPTION) && player.hasPotionEffect(PotionEffectType.REGENERATION) && player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE) && player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
							player.removePotionEffect(PotionEffectType.ABSORPTION);
							player.removePotionEffect(PotionEffectType.REGENERATION);
							player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
							player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
						}
						Set<String> AllPotionEffects = getConfig().getConfigurationSection("").getKeys(false);
						int power;
						int time;
						for (String s: AllPotionEffects) {
							if (getConfig().getBoolean(s + ".enabled") == true) {
								time = getConfig().getInt(s + ".time") * 20;
								power = getConfig().getInt(s + ".power") - 1;
								player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(s), time, power));
							}
						}
					}
				}, 1L);
			}
		}
	}
	public void firstRun() throws Exception {
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), configFile);
			log.info("Config not found, Generating.");
		}
	}

	private void loadYamls() {
		try {
			config.load(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf))>0) {
				out.write(buf,0,len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveYamls() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static CustomApples getInstance() {
		return instance;
	}
}
