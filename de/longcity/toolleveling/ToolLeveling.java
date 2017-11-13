package de.longcity.toolleveling;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ToolLeveling {
	private ToolLeveling() {}
	protected static double multiplier = 50;
	public static int getToolID(ItemStack i) {
		switch (i.getType()) {
		case STONE_SWORD:
		case IRON_SWORD:
		case GOLD_SWORD:
		case DIAMOND_SWORD:
			return 1;
		case STONE_PICKAXE:
		case IRON_PICKAXE:
		case GOLD_PICKAXE:
		case DIAMOND_PICKAXE:
			return 2;
		case STONE_AXE:
		case IRON_AXE:
		case GOLD_AXE:
		case DIAMOND_AXE:
			return 3;
		case STONE_SPADE:
		case IRON_SPADE:
		case GOLD_SPADE:
		case DIAMOND_SPADE:
			return 4;
		case BOW:return 5;
		default:
			return -1;
		}
	}
	public static boolean isEnabled(ItemStack i) {
		if(i==null)return false;
		List<String> lore = i.getItemMeta().getLore();
		if(lore == null) return false;
		if(lore.size() < 3) return false;
		return lore.get(lore.size()-3).startsWith("§0ToolLeveling Header");
	}
	public static boolean enable(ItemStack i) {
		if(isEnabled(i))return false;
		if(getToolID(i) == -1)return false;
		ItemMeta m = i.getItemMeta();
		List<String> lore = m.getLore();
		if(lore == null) lore = new ArrayList<>();
		lore.add("§0ToolLeveling Header "+getToolID(i));
		lore.add("§0Level=1");
		lore.add("§0Score=0");
		m.setLore(lore);
		i.setItemMeta(m);
		clearStats(i);
		return true;
	}
	public static int[] getStats(ItemStack i) {
		if(!isEnabled(i))return null;
		int[] ret = new int[3];
		List<String> lore = i.getItemMeta().getLore();
		ret[0] = getToolID(i);
		String line = lore.get(lore.size()-2);
		ret[1] = Integer.parseInt(line.substring(line.indexOf('=')+1));
		line = lore.get(lore.size()-1);
		ret[2] = Integer.parseInt(line.substring(line.indexOf('=')+1));
		return ret;
	}
	public static boolean setStats(ItemStack i, int[] stats) {
		if(stats == null || stats.length != 3) return false;
		if(!isEnabled(i)) return false;
		ItemMeta m = i.getItemMeta();
		List<String> lore = m.getLore();
		
		lore.set(lore.size()-2, "§0Level="+stats[1]);
		lore.set(lore.size()-1, "§0Score="+stats[2]);
		
		m.setLore(lore);
		i.setItemMeta(m);
		return true;
	}
	public static int getLevel(ItemStack i) {
		int[] stats = getStats(i);
		if(stats == null) return -1;
		return stats[1];
	}
	public static int getScore(ItemStack i) {
		int[] stats = getStats(i);
		if(stats == null) return -1;
		return stats[2];
	}
	public static int getNextLevelScore(int[] stats) {
		if(stats == null) return -1;
		return (int) (stats[1] * multiplier);
	}
	public static int addScore(ItemStack i, int score) {
		int[] stats = getStats(i);
		if(stats == null) return -1;
		
		int old_score = stats[2];
		if(old_score + score >= getNextLevelScore(stats)) {
			stats[2] = old_score + score - getNextLevelScore(stats);
			stats[1]++;
			Enchantment e = getNextEnchant(stats[1], stats[0]);
			if(e != null) {
				ItemMeta m = i.getItemMeta();
				if(m.hasEnchant(e)) {
					m.addEnchant(e, m.getEnchantLevel(e)+1, true);
				} else {
					m.addEnchant(e, 1, true);
				}
				i.setItemMeta(m);
			}
			setStats(i, stats);
			return 1;
		} else {
			stats[2] += score;
			setStats(i, stats);
			return 0;
		}
	}
	public static void clearStats(ItemStack i) {
		setStats(i, new int[] {0, 1, 0});
		ItemMeta m = i.getItemMeta();
		for(Enchantment e : Enchantment.values()) {
			m.removeEnchant(e);
		}
		i.setItemMeta(m);
	}
	private static Enchantment getNextEnchant(int level, int type) {
		switch (type) {
		case 1: // Schwert
			switch (level) {
			case 1:return Enchantment.DURABILITY; // Unbreaking
			case 2:return Enchantment.DAMAGE_ALL; // Sharpness
			case 3:return Enchantment.DAMAGE_ALL; // Sharpness2
			case 4:return Enchantment.DURABILITY; // Sharpness3
			case 5:return Enchantment.KNOCKBACK; //  Knockback
			case 6:return Enchantment.DAMAGE_ALL; // Sharpness4
			case 7:return Enchantment.DURABILITY; // Unbreaking2
			case 8:return Enchantment.DAMAGE_ALL; // Sharpness5
			case 9:return Enchantment.FIRE_ASPECT;// Fire Aspect
			case 10:return Enchantment.LOOT_BONUS_MOBS; // Looting
			case 11:return Enchantment.DAMAGE_ALL; // Sharpness6
			case 12:return Enchantment.KNOCKBACK;  // Knockback2
			case 13:return Enchantment.DAMAGE_ALL; // Sharpness7
			case 14:return Enchantment.DURABILITY; // Unbreaking3
			case 15:return Enchantment.FIRE_ASPECT; // Fire Aspect
			case 16:return Enchantment.DAMAGE_ALL; // Sharpness8
			case 17:return Enchantment.DURABILITY; // Unbreaking4
			case 18:return Enchantment.DAMAGE_ALL; // Sharpness9
			case 19:return Enchantment.DAMAGE_ALL; // Sharpness10
			case 20:return Enchantment.LOOT_BONUS_MOBS; // Looting2
			default:return Enchantment.DAMAGE_ALL;
			}
		case 2: // Spitzhacke
			switch (level) {
			case 1:return Enchantment.DURABILITY; // Unbreaking
			case 2:return Enchantment.DIG_SPEED;  // Effizienz
			case 3:return Enchantment.DIG_SPEED;  // Effizienz2
			case 4:return Enchantment.DURABILITY; // Unbreaking2
			case 5:return Enchantment.LOOT_BONUS_BLOCKS; // Fortune
			case 6:return Enchantment.DIG_SPEED;  // Effizienz3
			case 7:return Enchantment.DURABILITY; // Unbreaking3
			case 8:return Enchantment.DIG_SPEED;  // Effizienz4
			case 9:return Enchantment.DIG_SPEED;  // Effizienz5
			case 10:return Enchantment.LOOT_BONUS_BLOCKS; // Fortune2
			case 11:return Enchantment.DURABILITY;// Unbreaking4
			case 12:return Enchantment.DIG_SPEED; // Effizienz6
			case 13:return Enchantment.DIG_SPEED; // Effizienz7
			case 14:return Enchantment.DIG_SPEED; // Effizienz8
			case 15:return Enchantment.LOOT_BONUS_BLOCKS; // Fortune3
			case 16:return Enchantment.DURABILITY;// Unbreaking5
			case 17:return Enchantment.DIG_SPEED; // Effizienz9
			case 18:return Enchantment.DIG_SPEED; // Effizienz10
			case 19:return Enchantment.DURABILITY;// Unbreaking6
			case 20:return Enchantment.MENDING;   // Mending
			default:return Enchantment.DIG_SPEED;
			}
		case 3: // Axt
			switch (level) {
			case 1:return Enchantment.DURABILITY;  // Unbreaking
			case 2:return Enchantment.DIG_SPEED;   // Efizienz
			case 3:return Enchantment.DIG_SPEED;   // Effizienz2
			case 4:return Enchantment.DURABILITY;  // Unbreaking2
			case 5:return Enchantment.DAMAGE_ALL;  // Sharpness
			case 6:return Enchantment.DURABILITY;  // Unbreaking3
			case 7:return Enchantment.DIG_SPEED;   // Effizienz3
			case 8:return Enchantment.DIG_SPEED;   // Effizienz4
			case 9:return Enchantment.DURABILITY;  // Unbreaking4
			case 10:return Enchantment.DAMAGE_ALL; // Sharpness2
			default:return null;
			}
		case 4: // Schaufel
			switch (level) {
			case 1:return Enchantment.DURABILITY; // Unbreaking
			case 2:return Enchantment.DIG_SPEED;  // Effizienz
			case 3:return Enchantment.DIG_SPEED;  // Effizienz2
			case 4:return Enchantment.DURABILITY; // Unbreaking2
			case 5:return Enchantment.DURABILITY; // Unbreaking3
			case 6:return Enchantment.DIG_SPEED;  // Effizienz3
			case 7:return Enchantment.DURABILITY; // Unbreaking4
			case 8:return Enchantment.DIG_SPEED;  // Effizienz4
			case 9:return Enchantment.DURABILITY; // Unbreaking5
			case 10:return Enchantment.SILK_TOUCH;// Silk_Touch
			case 11:return Enchantment.DURABILITY;// Unbreaking6
			case 12:return Enchantment.DURABILITY;// Unbreaking7
			case 13:return Enchantment.DURABILITY;// Unbreaking8
			case 14:return Enchantment.DIG_SPEED; // Effizienz5
			case 15:return Enchantment.MENDING;   // Mending
			default:return Enchantment.DIG_SPEED;
			}
		case 5: // Bogen
			switch (level) {
			case 1:return Enchantment.DURABILITY;   // Unbreaking
			case 2:return Enchantment.ARROW_DAMAGE; // Power
			case 3:return Enchantment.ARROW_DAMAGE; // Power2
			case 4:return Enchantment.ARROW_DAMAGE; // Power3
			case 5:return Enchantment.ARROW_FIRE;   // Flame
			case 6:return Enchantment.DURABILITY;   // Unbreaking2
			case 7:return Enchantment.ARROW_DAMAGE; // Power4
			case 8:return Enchantment.ARROW_KNOCKBACK;//Punch
			case 9:return Enchantment.ARROW_FIRE;   // Flame2
			case 10:return Enchantment.ARROW_INFINITE;//Infinity
			case 11:return Enchantment.ARROW_DAMAGE;// Power5
			case 12:return Enchantment.DURABILITY;  // Unbreaking3
			case 13:return Enchantment.ARROW_DAMAGE;// Power6
			case 14:return Enchantment.ARROW_DAMAGE;// Power7
			case 15:return Enchantment.ARROW_KNOCKBACK;//Punch2
			case 16:return Enchantment.ARROW_DAMAGE;// Power8
			case 17:return Enchantment.DURABILITY;  // Unbreaking4
			case 18:return Enchantment.DURABILITY;  // Unbreaking5
			case 19:return Enchantment.ARROW_DAMAGE;// Power9
			case 20:return Enchantment.ARROW_FIRE;  // Flame3
			case 21:return Enchantment.ARROW_DAMAGE;// Power10
			case 22:return Enchantment.DURABILITY;  // Unbreaking6
			case 23:return Enchantment.ARROW_DAMAGE;// Power11
			case 24:return Enchantment.ARROW_DAMAGE;// Power12
			case 25:return Enchantment.MENDING;     // Mending
			default:return Enchantment.ARROW_DAMAGE;
			}
		}
		return Enchantment.DURABILITY;
	}
	protected static int getBlockScore(Material mat, int type) {
		switch (type) {
		case 1:if(mat == Material.WEB)return 5;
		case 2:
			switch (mat) {
			case SANDSTONE:
			case SANDSTONE_STAIRS:
			case COBBLESTONE_STAIRS:
			case STONE:return 2;
			case MOSSY_COBBLESTONE:
			case COBBLESTONE:return 3;
			case COAL_BLOCK:
			case COAL_ORE:return 4;
			case IRON_BLOCK:
			case IRON_ORE:return 5;
			case GOLD_BLOCK:
			case GOLD_ORE:return 10;
			case DIAMOND_BLOCK:
			case DIAMOND_ORE:return 15;
			case OBSIDIAN:return 20;
			default:return 1;
			}
		case 3:
			switch (mat) {
			case WOOD:return 3;
			case LOG:return 5;
			case LOG_2:return 5;
			case CHEST:return 7;
			case LEAVES:return 5;
			case LEAVES_2:return 5;
			default:return 1;
			}
		case 4:
			switch (mat) {
			case DIRT:return 3;
			case GRASS:
			case GRAVEL:
			case SAND:return 5;
			case CONCRETE_POWDER:return 7;
			default:return 1;
			}
		}
		return 0;
	}
	protected static int getMobScore(EntityType type) {
		switch (type) {
		case CHICKEN:return 2;
		case SHEEP:return 3;
		case PIG:return 3;
		case COW:return 3;
		case WOLF:
		case RABBIT:return 5;
		case SPIDER:return 6;
		case ZOMBIE:return 7;
		case SKELETON:return 8;
		case CREEPER:return 10;
		case ENDERMAN:return 15;
		case WITCH:return 15;
		case ENDER_DRAGON:return 1000;
		case WITHER:return 1500;
		case WITHER_SKELETON:return 20;
		default:
			return 10;
		}
	}
}
