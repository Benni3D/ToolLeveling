package de.longcity.toolleveling;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public final class Events implements Listener {
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		ItemStack i = e.getPlayer().getInventory().getItemInMainHand();
		if(ToolLeveling.isEnabled(i)) {
			if(ToolLeveling.addScore(i,
					ToolLeveling.getBlockScore(e.getBlock().getType(), ToolLeveling.getToolID(i))) == 1){
				e.getPlayer().sendMessage("§cDein Tool wurde verbessert!");
				i.setDurability((short) 0);
			}
		}
	}
	@EventHandler
	public void onKill(EntityDamageByEntityEvent e) {
		{
			Entity en = e.getEntity();
			if(!(en instanceof LivingEntity))return;
			if(((LivingEntity)en).getHealth() > e.getDamage())return;
		}
		if(e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			ItemStack i = p.getInventory().getItemInMainHand();
			if(ToolLeveling.isEnabled(i)) {
				int type = ToolLeveling.getToolID(i);
				if(type != 1 && type != 5)return;
				if(ToolLeveling.addScore(i, ToolLeveling.getMobScore(e.getEntityType())*2) == 1){
					p.sendMessage("§cDein Tool wurde verbessert!");
					i.setDurability((short) 0);
				}
			}
		} else if(e.getDamager() instanceof Arrow) {
			if(((Arrow)e.getDamager()).getShooter() instanceof Player) {
				Player p = (Player) ((Arrow)e.getDamager()).getShooter();
				ItemStack i = p.getInventory().getItemInMainHand();
				if(ToolLeveling.isEnabled(i)) {
					int type = ToolLeveling.getToolID(i);
					if(type != 1 && type != 5)return;
					if(ToolLeveling.addScore(i, ToolLeveling.getMobScore(e.getEntityType())) == 1){
						p.sendMessage("§cDein Tool wurde verbessert!");
						i.setDurability((short) 0);
					}
				}
			}
		}
	}
}
