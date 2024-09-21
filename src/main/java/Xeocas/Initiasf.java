package Xeocas;

import Xeocas.Weapons.*;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class Initiasf extends JavaPlugin implements SlimefunAddon {

	@Override
	public void onEnable() {
		// Delay initialization of Slimefun components to ensure Slimefun is fully loaded
		getServer().getScheduler().runTaskLater(this, () -> {
			try {
				// Define the ItemGroup for weapon machines
				NamespacedKey weaponMachineCategoryId = new NamespacedKey(this, "weaponmachine_category");
				CustomItemStack weaponMachineCategoryItem = new CustomItemStack(Material.DIAMOND_BLOCK, "&4Initia's Slimefun Factories!"); // Use Diamond Block as the category icon
				ItemGroup weaponMachineGroup = new ItemGroup(weaponMachineCategoryId, weaponMachineCategoryItem);

				// Define the SlimefunItemStack for the ArtilleryAmmoMachine
				SlimefunItemStack artilleryAmmoMachineStack = new SlimefunItemStack("ARTILLERY_AMMO_MACHINE", Material.FURNACE, "&7Artillery Ammo Machine");
				SlimefunItemStack antiairAmmoMachineStack = new SlimefunItemStack("ANTIAIR_AMMO_MACHINE", Material.FURNACE, "&7Anti-Air Ammo Machine");
				SlimefunItemStack onefiveNavalAmmoMachineStack = new SlimefunItemStack("ONEFIVE_AMMO_MACHINE", Material.FURNACE, "&7Fifteen Inch Ammo Machine");
				SlimefunItemStack onethreeNavalAmmoMachineStack = new SlimefunItemStack("ONETHREE_AMMO_MACHINE", Material.FURNACE, "&7Thirteen Inch Ammo Machine");
				SlimefunItemStack onetwoNavalAmmoMachineStack = new SlimefunItemStack("ONETWO_AMMO_MACHINE", Material.FURNACE, "&7Twelve Inch Ammo Machine");
				SlimefunItemStack steelMachineStack = new SlimefunItemStack("STEEL_MACHINE", Material.FURNACE, "&7Steel Machine");
				SlimefunItemStack tankAmmoMachineStack = new SlimefunItemStack("TANK_AMMO_MACHINE", Material.FURNACE, "&7Tank Ammo Machine");
				SlimefunItemStack fighterAmmoMachineStack = new SlimefunItemStack("FIGHTER_AMMO_MACHINE", Material.FURNACE, "&7Fighter Ammo Machine");


				// Create the ArtilleryAmmoMachine instance
				ArtilleryAmmoMachine artilleryAmmoMachine = new ArtilleryAmmoMachine(weaponMachineGroup, artilleryAmmoMachineStack, RecipeType.ENHANCED_CRAFTING_TABLE, this);
				AntiAirAmmoMachine antiAirAmmoMachine = new AntiAirAmmoMachine(weaponMachineGroup, antiairAmmoMachineStack, RecipeType.ENHANCED_CRAFTING_TABLE, this);
				onefiveNavalAmmoMachine OnefiveNavalAmmoMachine = new onefiveNavalAmmoMachine(weaponMachineGroup, onefiveNavalAmmoMachineStack, RecipeType.ENHANCED_CRAFTING_TABLE, this);
				onethreeNavalAmmoMachine OnethreeNavalAmmoMachine = new onethreeNavalAmmoMachine(weaponMachineGroup, onethreeNavalAmmoMachineStack, RecipeType.ENHANCED_CRAFTING_TABLE, this);
				onetwoNavalAmmoMachine OnetwoNavalAmmoMachine = new onetwoNavalAmmoMachine(weaponMachineGroup, onetwoNavalAmmoMachineStack, RecipeType.ENHANCED_CRAFTING_TABLE, this);
				SteelMachine steelMachine = new SteelMachine(weaponMachineGroup, steelMachineStack, RecipeType.ENHANCED_CRAFTING_TABLE, this);
				TankAmmoMachine tankAmmoMachine = new TankAmmoMachine(weaponMachineGroup, tankAmmoMachineStack, RecipeType.ENHANCED_CRAFTING_TABLE, this);
				FighterAmmoMachine fighterAmmoMachine = new FighterAmmoMachine(weaponMachineGroup, fighterAmmoMachineStack, RecipeType.ENHANCED_CRAFTING_TABLE, this);



				// Register the ArtilleryAmmoMachine
				artilleryAmmoMachine.register(this);
				antiAirAmmoMachine.register(this);
				OnefiveNavalAmmoMachine.register(this);
				OnethreeNavalAmmoMachine.register(this);
				OnetwoNavalAmmoMachine.register(this);
				steelMachine.register(this);
				tankAmmoMachine.register(this);
				fighterAmmoMachine.register(this);


				// If necessary, you can also add recipes here for additional configurations
				// e.g., SlimefunItems.addRecipe(artilleryAmmoMachineStack, RecipeType.ENHANCED_CRAFTING_TABLE, recipe);

				getLogger().info("ArtilleryAmmoMachine registered.");
			} catch (Exception e) {
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
