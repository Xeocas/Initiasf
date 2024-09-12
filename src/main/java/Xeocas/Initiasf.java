package Xeocas;

import Xeocas.Weapons.KarMachine;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.deecaad.weaponmechanics.WeaponMechanics;
import me.deecaad.weaponmechanics.weapon.WeaponHandler;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Initiasf extends JavaPlugin implements SlimefunAddon {


	@Override
	public void onEnable() {

		Plugin weaponMechanicsPlugin = getServer().getPluginManager().getPlugin("WeaponMechanics");
		if (weaponMechanicsPlugin == null) {
			getLogger().warning("WeaponMechanics plugin is not found.");
		} else {
			getLogger().info("WeaponMechanics plugin found: " + weaponMechanicsPlugin.getDescription().getVersion());
		}
		getLogger().info("Plugin enabled!");

		// Initialize WeaponSetup and retry logic for WeaponMechanics

		// Delay the WeaponMechanics setup to ensure the plugin is fully loaded
		new BukkitRunnable() {
			@Override
			public void run() {
				WeaponHandler weaponHandler = WeaponMechanics.getWeaponHandler();
				// Initialize KarMachine only after WeaponSetup completes
			}
		}.runTaskLater(this, 200L); // Delay by 10 seconds

		// Delay initialization of Slimefun components to ensure Slimefun is fully loaded
		getServer().getScheduler().runTaskLater(this, () -> {
			try {
				// Define the ItemGroup for weapon machines
				NamespacedKey weaponMachineCategoryId = new NamespacedKey(this, "weaponmachine_category");
				ItemStack weaponMachineCategoryItem = new ItemStack(Material.DIAMOND_BLOCK); // Use Diamond Block as the category icon

				// Create and register the ItemGroup for weapon machines
				ItemGroup weaponMachineGroup = new ItemGroup(weaponMachineCategoryId, weaponMachineCategoryItem);

				// Define the SlimefunItemStack for the KarMachine
				SlimefunItemStack karMachineStack = new SlimefunItemStack("KAR98_FACTORY", Material.FURNACE, "&7Kar98 Factory");

				// Create the KarMachine instance and retrieve its recipe
				KarMachine karMachineInstance = new KarMachine(weaponMachineGroup, karMachineStack, RecipeType.ENHANCED_CRAFTING_TABLE, this);
				ItemStack[] karMachineRecipe = karMachineInstance.getKarMachineRecipe(); // Use instance to get the recipe

				// Validate the recipe
				if (karMachineRecipe == null || karMachineRecipe.length == 0) {
					getLogger().severe("KarMachine recipe is not properly defined.");
					return;
				}

				// Register the KarMachine
				karMachineInstance.register(this);

				getLogger().info("Kar98 Factory registered in the weapon machine category.");
			} catch (Exception e) {
				getLogger().severe("An error occurred during plugin initialization:");
				e.printStackTrace(); // Print the error stack trace for debugging
			}
		}, 20L); // Delay by 1 second (20 ticks)

		getLogger().info("Initializing plugin...");
	}


	@Override
	public void onDisable() {
		getLogger().info("Plugin disabled.");
	}

	@Override
	public JavaPlugin getJavaPlugin() {
		return this;
	}

	@Override
	public String getBugTrackerURL() {
		return null; // Provide a URL if you have a bug tracker, or leave as null
	}
}
