package se.enji.lep;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Nick extends JavaPlugin implements Listener {
	FileConfiguration config;
	
	public void onEnable() {
		loadConfiguration();
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	public void loadConfiguration() {
		this.config = getConfig();
		this.config.options().copyDefaults(true);
		saveConfig();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		String confNick = this.config.getString(p.getName());
		if (confNick == "" || confNick == null) return;
		p.setDisplayName(confNick);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
		if ((sender instanceof Player)) {
			Player p = (Player)sender;
			if (cmd.equals("nick")) {
				if (args.length == 0) {
					p.setDisplayName(p.getName());
					this.config.set(p.getName(), null);
					p.sendMessage("Your name is now back to normal.");
				}
				else {
					if (args.length == 1) {
						String newNick = args[0];
						if (newNick.isEmpty()) {
							p.sendMessage("You didn't choose a nickname.");
						} else if ((newNick.equals(p.getName())) || (newNick.equals(p.getDisplayName()))) {
							p.sendMessage("Type /nick without anything else to have your normal name back again.");
						} else if (exists(newNick)) {
							p.sendMessage("Nickname already in use or owned by someone else.");
						} else {
							setDspn(p, newNick);
						}
						return true;
					}
					if (args.length == 2) {
						if (!p.hasPermission("lep.nick")) {
							return false;
						}
						Player toName = getPlayer(args[0]);
						if (toName == null) {
							p.sendMessage("§c" + args[0] + " is not online.");
						} else {
							setDspn(toName, args[1]);
						}
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}

	public void setDspn(Player p, String n) {
		p.setDisplayName(n);
		this.config.set(p.getName(), n);
		saveConfig();
		n = n.replace("_", " ");
		getServer().broadcastMessage(p.getName() + "s name in chat is now \"" + n + "\".");
	}
	
	public boolean exists(String s) {
		if (this.config.contains(s)) {
			return true;
		}
		HashMap<String, String> pls = new HashMap<String, String>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			pls.put(p.getName(), p.getDisplayName());
		}
		if (pls.containsKey(s)) {
			return true;
		}
		if (pls.containsValue(s)) {
			return true;
		}
		return false;
	}
	
	public Player getPlayer(String s) {
		HashMap<String, String> players = new HashMap<String, String>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			String dsp = p.getDisplayName();
			players.put(dsp, p.getName());
			if ((dsp.equalsIgnoreCase(s)) || (dsp.startsWith(s)) || (dsp.toLowerCase().startsWith(s.toLowerCase()))) {
				return Bukkit.getPlayer(p.getName());
			}
		}
		if (Bukkit.getPlayer(s) != null) {
			return Bukkit.getPlayer(s);
		}
		if (players.containsKey(s)) {
			return Bukkit.getPlayer((String)players.get(s));
		}
		return null;
	}
}
