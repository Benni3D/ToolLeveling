package de.longcity.toolleveling;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class ToolLevelingPlugin extends JavaPlugin {
	private static final String nop = "§cDu hast keine Berrechtigung für diesen Befehl!";
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		saveDefaultConfig();
		ToolLeveling.multiplier = getConfig().getDouble("level-multiplier", 50);
	}
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)) {
			cs.sendMessage("Nur für Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(args.length == 0) {
			cs.sendMessage(new String[] {
					"§3§l---[ §4§lToolLeveling§3§l ]---",
					"§7/tl enable §e- Aktiviert das ToolLeveling",
					"§7/tl stats §e- Tool Stats",
					"§7/tl clear §e- Cleart das Item",
					"§c/tl set <level> <score> - Setzt die Stats",
					"§c/tl reload"
			});
		}else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("enable")) {
				if(!p.hasPermission("toolleveling.enable")) {
					cs.sendMessage(nop);
					return true;
				}
				ItemStack i = p.getInventory().getItemInMainHand();
				if(i == null) {
					cs.sendMessage("§cBitte nimm ein Item in die Hand!");
					return true;
				}
				if(ToolLeveling.enable(i)) {
					cs.sendMessage("§aLeveling aktiviert.");
				} else {
					cs.sendMessage("§cEin Fehler ist aufgetreten!");
				}
			} else if(args[0].equalsIgnoreCase("stats")) {
				if(!p.hasPermission("toolleveling.stats")) {
					cs.sendMessage(nop);
					return true;
				}
				ItemStack i = p.getInventory().getItemInMainHand();
				if(i == null) {
					cs.sendMessage("§cBitte nimm ein Item in die Hand!");
					return true;
				}
				int[] stats = ToolLeveling.getStats(i);
				if(stats == null) {
					cs.sendMessage("§cBitte aktiviere das ToolLeveling für dieses Item!");
					return true;
				}
				cs.sendMessage(new String[] {
						"§3ItemStats:",
						"§aTYP: "+stats[0],
						"§aLevel: "+stats[1],
						"§aScore: "+stats[2]+"/"+ToolLeveling.getNextLevelScore(stats)
				});
			} else if(args[0].equalsIgnoreCase("clear")) {
				if(!p.hasPermission("toolleveling.stats.clear")) {
					cs.sendMessage(nop);
					return true;
				}
				ItemStack i = p.getInventory().getItemInMainHand();
				if(i == null) {
					cs.sendMessage("§cBitte nimm ein Item in die Hand!");
					return true;
				}
				ToolLeveling.clearStats(i);
				cs.sendMessage("§aStats auf Null gesetzt.");
			} else if(args[0].equalsIgnoreCase("reload")) {
				if(!p.hasPermission("toolleveling.admin")) {
					cs.sendMessage(nop);
					return true;
				}
				saveDefaultConfig();
				ToolLeveling.multiplier = getConfig().getDouble("level-multiplier", 50);
				cs.sendMessage("§aConfig reloaded.");
			}
		} else if(args.length == 3) {
			if(args[0].equalsIgnoreCase("set")) {
				if(!p.hasPermission("toolleveling.stats.modify")) {
					cs.sendMessage(nop);
					return true;
				}
				ItemStack i = p.getInventory().getItemInMainHand();
				if(i == null) {
					cs.sendMessage("§cBitte nimm ein Item in die Hand!");
					return true;
				}
				int[] stats = ToolLeveling.getStats(i);
				try {
					if(!args[1].equals("~"))
						stats[1] = Integer.parseInt(args[1]);
					if(args[2].equalsIgnoreCase("max")) stats[2] = Integer.MAX_VALUE - 47;
					else if(!args[2].equals("~"))
						stats[2] = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					p.performCommand("tl");
					return true;
				}
				ToolLeveling.setStats(i, stats);
				p.sendMessage("§a Stats wurden auf §e[level="+stats[1]+",score="+stats[2]+"] §agesetzt!");
			}
		}
		
		return true;
	}
}
