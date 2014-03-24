package at.weini;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class Main extends JavaPlugin {

	public static HashMap<String, Quarray> quarries;

	@Override
	public void onEnable() {
		quarries = new HashMap<>();
	}

	@Override
	public void onDisable() {
		for (String key : quarries.keySet()) {
			quarries.get(key).interrupt();
			quarries.remove(key);
		}
		Bukkit.getServer().clearRecipes();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Whoa there buddy, only players may execute this!");
			return true;
		}

		Player p = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("quarry")) {
			if (args.length != 1) {
				return false;
			}

			if (args[0].equalsIgnoreCase("create")) {

				if (quarries.containsKey(p.getName())) {
					p.sendMessage("Quarry existiert bereits");
					return true;
				}

				WorldEditPlugin worldEditPlugin = null;
				worldEditPlugin = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
				Selection sel = worldEditPlugin.getSelection(p);
				if (sel instanceof Polygonal2DSelection) {
					sender.sendMessage("[Quarry] Must be an Cuboid Selection");
				} else if (sel instanceof CuboidSelection) {
					BlockVector min = sel.getNativeMinimumPoint().toBlockVector();
					BlockVector max = sel.getNativeMaximumPoint().toBlockVector();
					quarries.put(p.getName(), new Quarray(min, max, p.getWorld(), this, p));
				}

				p.sendMessage("Create quarry");

				return true;
			} else if (args[0].equalsIgnoreCase("chest")) {
				if (!quarries.containsKey(p.getName())) {
					p.sendMessage("Erstelle zuerst eine Quarry!");
					return false;
				}

				quarries.get(p.getName()).initChest(p.getTargetBlock(null, 10));
				p.sendMessage("Quarry chest was successfully created");
				return true;

			} else if (args[0].equalsIgnoreCase("start")) {
				if (!quarries.containsKey(p.getName())) {
					p.sendMessage("Erstelle zuerst eine Quarry!");
					return false;
				}

				quarries.get(p.getName()).start();
				p.sendMessage("Quarry started");
				return true;
			} else if (args[0].equalsIgnoreCase("stop")) {

				if (!quarries.containsKey(p.getName())) {
					p.sendMessage("Erstelle zuerst eine Quarry!");
					return false;
				}
				quarries.get(p.getName()).interrupt();
				quarries.remove(p.getName());
				p.sendMessage("Quarry stopped and removed");
				return true;
			}

		}

		return false;
	}
}
