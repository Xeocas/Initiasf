package Xeocas.Weapons;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.inventory.InvUtils;
import me.deecaad.weaponmechanics.WeaponMechanics;
import me.deecaad.weaponmechanics.WeaponMechanicsLoader;
import me.deecaad.weaponmechanics.weapon.info.InfoHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

import me.deecaad.weaponmechanics.weapon.WeaponHandler;
import me.deecaad.weaponmechanics.WeaponMechanics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class KarMachine extends AContainer implements RecipeDisplayItem {

	private static final int ENERGY_CONSUMPTION = 64;
	private static final int CAPACITY = ENERGY_CONSUMPTION * 5; // Can produce 5 guns
	private static final int PROCESSING_TIME = 5 * 60; // 5 minutes in seconds

	private final ItemStack[] inputItems = {
			new ItemStack(Material.IRON_INGOT, 64),
			new ItemStack(Material.EMERALD, 2),
	};

	private ItemStack outputItem;
	private JavaPlugin plugin;

	public KarMachine(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, JavaPlugin plugin) {
		super(itemGroup, item, recipeType, getKarMachineRecipe());
		this.plugin = plugin;
		setupWeaponHandler();
	}

	private void setupWeaponHandler() {
		try {
			// Get the WeaponMechanicsLoader plugin instance
			Plugin weaponMechanicsPlugin = getServer().getPluginManager().getPlugin("WeaponMechanics");
			if (weaponMechanicsPlugin == null) {
				getLogger().warning("WeaponMechanics plugin is not found.");
				return;
			}

			// Check if the plugin is an instance of WeaponMechanicsLoader
			if (weaponMechanicsPlugin instanceof WeaponMechanicsLoader) {
				// Cast to WeaponMechanicsLoader
				WeaponMechanicsLoader loader = (WeaponMechanicsLoader) weaponMechanicsPlugin;

				// Get the private plugin field using reflection
				java.lang.reflect.Field pluginField = WeaponMechanicsLoader.class.getDeclaredField("plugin");
				pluginField.setAccessible(true);
				WeaponMechanics weaponMechanics = (WeaponMechanics) pluginField.get(loader);

				if (weaponMechanics != null) {
					WeaponHandler weaponHandler = weaponMechanics.getWeaponHandler();

					if (weaponHandler != null) {
						InfoHandler infoHandler = weaponHandler.getInfoHandler();
						if (infoHandler != null) {
							String weaponTitle = weaponHandler.getInfoHandler().getWeaponTitle("Kar98k");
							this.outputItem = weaponHandler.getInfoHandler().generateWeapon(weaponTitle, 1);
						} else {
							throw new IllegalStateException("InfoHandler is null.");
						}
					} else {
						throw new IllegalStateException("WeaponHandler is null.");
					}
				} else {
					throw new IllegalStateException("WeaponMechanics instance is null.");
				}
			} else {
				throw new IllegalStateException("WeaponMechanicsLoader plugin is not of the expected class type.");
			}
		} catch (Exception e) {
			plugin.getLogger().warning("Failed to initialize WeaponHandler: " + e.getMessage());
			retrySetupWeaponHandler();
		}
	}


	private void retrySetupWeaponHandler() {
		new BukkitRunnable() {
			@Override
			public void run() {
				setupWeaponHandler(); // Retry the initialization
			}
		}.runTaskLater(plugin, 100L); // Retry after 5 seconds
	}



	public static ItemStack[] getKarMachineRecipe() {
		return new ItemStack[]{
				new ItemStack(Material.IRON_BLOCK),
				new ItemStack(Material.PISTON),
				new ItemStack(Material.IRON_BLOCK),
				new ItemStack(Material.DIAMOND),
				new ItemStack(Material.FURNACE),
				new ItemStack(Material.DIAMOND),
				new ItemStack(Material.IRON_BLOCK),
				new ItemStack(Material.REDSTONE_BLOCK),
				new ItemStack(Material.IRON_BLOCK)
		};
	}

	@Override
	public String getInventoryTitle() {
		return "Kar98 Factory";
	}

	@Override
	public ItemStack getProgressBar() {
		return new ItemStack(Material.FLINT_AND_STEEL);
	}

	@Override
	public String getMachineIdentifier() {
		return "KAR98_FACTORY";
	}

	@Override
	public int getCapacity() {
		return CAPACITY;
	}

	@Override
	public int getEnergyConsumption() {
		return ENERGY_CONSUMPTION;
	}

	@Override
	public int getSpeed() {
		return PROCESSING_TIME;
	}

	protected MachineRecipe findNextRecipe(BlockMenu inv) {
		Map<Integer, ItemStack> inventory = new HashMap<>();
		int[] inputSlots = this.getInputSlots();

		// Collect items from the input slots
		for (int slot : inputSlots) {
			ItemStack item = inv.getItemInSlot(slot);
			if (item != null && !item.getType().equals(Material.AIR)) {
				inventory.put(slot, item);
			}
		}

		// Debug log for collected items
		getLogger().info("Items in input slots: " + inventory);

		// Iterate over recipes to find a match
		for (MachineRecipe recipe : this.recipes) {
			ItemStack[] recipeInputs = recipe.getInput();
			Map<Integer, Integer> found = new HashMap<>();

			// Debug log for recipe details
			getLogger().info("Checking recipe: " + recipe);
			getLogger().info("Recipe inputs:");
			for (ItemStack input : recipeInputs) {
				getLogger().info(" - " + input); // Log each recipe input
			}

			getLogger().info("Recipe output: " + Arrays.toString(recipe.getOutput())); // Log the recipe output

			// Check if recipe inputs match items in the input slots
			boolean allInputsMatch = true;
			for (ItemStack input : recipeInputs) {
				boolean matchFound = false;
				for (Map.Entry<Integer, ItemStack> entry : inventory.entrySet()) {
					ItemStack itemInSlot = entry.getValue();
					// Log item similarity check
					getLogger().info("Comparing input: " + input + " with item in slot: " + itemInSlot);
					if (SlimefunUtils.isItemSimilar(itemInSlot, input, true, false)) {
						found.put(entry.getKey(), input.getAmount());
						matchFound = true;
						break;
					}
				}
				if (!matchFound) {
					allInputsMatch = false;
					break;
				}
			}

			// Debug log for recipe matching
			getLogger().info("Inputs matched: " + allInputsMatch);
			getLogger().info("Found items: " + found);

			// If all inputs are matched
			if (allInputsMatch) {
				// Check if output fits in output slots
				if (InvUtils.fitAll(inv.toInventory(), recipe.getOutput(), this.getOutputSlots())) {
					// Consume items from input slots
					for (Map.Entry<Integer, Integer> entry : found.entrySet()) {
						inv.consumeItem(entry.getKey(), entry.getValue());
					}
					getLogger().info("Recipe matched and processed: " + recipe);
					return recipe;
				}
			}
		}

		getLogger().info("No matching recipe found.");
		return null;
	}






	@Override
	public List<ItemStack> getDisplayRecipes() {
		List<ItemStack> displayRecipes = new ArrayList<>();
		displayRecipes.add(new ItemStack(Material.IRON_INGOT, 64));
		displayRecipes.add(new ItemStack(Material.EMERALD, 2));
		displayRecipes.add(outputItem);
		return displayRecipes;
	}
}