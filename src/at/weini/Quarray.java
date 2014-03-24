package at.weini;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.server.v1_7_R1.TileEntityChest;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R1.block.CraftChest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.BlockVector;

public class Quarray extends Thread {

	BlockVector min, max;
	World world;
	JavaPlugin plugin;

	int xOperate, yOperate, zOperate;

	TileEntityChest teChest;
	CraftChest chest;

	Player p;

	public Quarray(BlockVector min, BlockVector max, World world, JavaPlugin plugin, Player p) {
		this.min = min;
		this.max = max;
		this.world = world;
		this.plugin = plugin;
		this.p = p;

		xOperate = min.getBlockX();
		yOperate = min.getBlockY();
		zOperate = min.getBlockZ();
	}

	@Override
	public void run() {
		if (chest == null) {
			p.sendMessage("Please select a chest first (/quarry chest)");
			return;
		}
		for (yOperate = max.getBlockY(); yOperate >= 0; yOperate--) {
			for (xOperate = min.getBlockX(); xOperate <= max.getBlockX(); xOperate++) {
				for (zOperate = min.getBlockZ(); zOperate <= max.getBlockZ(); zOperate++) {
					if (interrupted()) {
						return;
					}
					Block block = world.getBlockAt(xOperate, yOperate, zOperate);
					if (block.getType() != Material.BEDROCK && block.getType() != Material.AIR) {
						if (chest.getBlock().getType() != Material.CHEST) {
							p.sendMessage("Chest removed -> Removed quarry");
							Main.quarries.remove(p.getName());
							interrupt();
						}
						HashMap<Integer, ItemStack> doesntFit = chest.getInventory().addItem(new ItemStack(block.getType(), 1));
						for (Integer key : doesntFit.keySet()) {
							world.dropItemNaturally(chest.getLocation(), doesntFit.get(key));
						}
						block.setType(Material.AIR);
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							e.printStackTrace();
							return;
						}
					}
				}
			}
		}
	}

	public void initChest(Block block) {
		if (block.getType() != Material.CHEST) {
			p.sendMessage("Please look at a chest!");
		}
		chest = (CraftChest) block.getState(); // block has to be a chest
		try {
			Field inventoryField = chest.getClass().getDeclaredField("chest");
			inventoryField.setAccessible(true);
			teChest = ((TileEntityChest) inventoryField.get(chest));
			teChest.a("Quarry");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
