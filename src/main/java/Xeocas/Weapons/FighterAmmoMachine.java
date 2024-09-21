package Xeocas.Weapons;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class FighterAmmoMachine extends AContainer implements RecipeDisplayItem, Listener {

	private static final int ENERGY_CONSUMPTION = 64;
	private static final int CAPACITY = ENERGY_CONSUMPTION * 5; // Can produce 5 custom Nether Stars
	private static final int PROCESSING_TIME = 60; // 5 minutes in seconds

	private ItemStack outputItem;
	private JavaPlugin plugin;
	private NamespacedKey key; // For custom NBT-like data

	public FighterAmmoMachine(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, JavaPlugin plugin) {
		super(itemGroup, item, recipeType, getMachineRecipe());
		this.plugin = plugin;
		this.key = new NamespacedKey(plugin, "fighter_ammo");

		// Set output as a custom Nether Star with a custom name
		outputItem = new ItemStack(Material.NETHER_STAR);
		ItemMeta meta = outputItem.getItemMeta();
		if (meta != null) {
			meta.setDisplayName(ChatColor.GOLD + "Fighter Ammo");

			// Add custom NBT-like data using PersistentDataContainer
			PersistentDataContainer data = meta.getPersistentDataContainer();
			data.set(key, PersistentDataType.STRING, "true");
			outputItem.setItemMeta(meta);
		}

		// Register the event listener
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}


	@Override
	public String getInventoryTitle() {
		return "Fighter Ammo Factory";
	}

	@Override
	public ItemStack getProgressBar() {
		return new ItemStack(Material.FLINT_AND_STEEL);
	}

	@Override
	public String getMachineIdentifier() {
		return "FIGHTER_FACTORY";
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

	public static ItemStack[] getMachineRecipe() {
		return new ItemStack[]{
				new ItemStack(Material.IRON_BLOCK),
				new ItemStack(Material.PISTON),
				new ItemStack(Material.IRON_BLOCK),
				new ItemStack(Material.EMERALD),
				new ItemStack(Material.BLAST_FURNACE),
				new ItemStack(Material.DIAMOND),
				new ItemStack(Material.COPPER_BLOCK),
				new ItemStack(Material.REDSTONE_BLOCK),
				new ItemStack(Material.COPPER_BLOCK)
		};
	}

	// Recipe matching for 1 TNT and 8 Iron Ingots
	@Override
	protected MachineRecipe findNextRecipe(BlockMenu inv) {
		// Check if input slots contain 1 TNT and 8 Iron Ingots
		ItemStack ironInSlot = inv.getItemInSlot(this.getInputSlots()[0]);
		ItemStack gunpowderInSlot = inv.getItemInSlot(this.getInputSlots()[1]);

		if (ironInSlot != null && ironInSlot.getType() == Material.IRON_INGOT && ironInSlot.getAmount() >= 1 &&
				gunpowderInSlot != null && gunpowderInSlot.getType() == Material.GUNPOWDER && gunpowderInSlot.getAmount() >= 1) {

			// Consume 3 Iron Ingots
			inv.consumeItem(this.getInputSlots()[0], 1);
			inv.consumeItem(this.getInputSlots()[1], 1);

			// Return the recipe
			return new MachineRecipe(PROCESSING_TIME, new ItemStack[]{
					new ItemStack(Material.IRON_INGOT, 1), new ItemStack(Material.GUNPOWDER, 1)},
					new ItemStack[]{outputItem});
		}
		return null;
	}

	@Override
	public List<ItemStack> getDisplayRecipes() {
		List<ItemStack> displayRecipes = new ArrayList<>();
		displayRecipes.add(new ItemStack(Material.IRON_INGOT, 1));
		displayRecipes.add(new ItemStack(Material.GUNPOWDER, 1));
		displayRecipes.add(outputItem);
		return displayRecipes;
	}

	// Check if an item has the custom PersistentDataContainer key
	private boolean hasCustomData(ItemStack item, NamespacedKey key) {
		if (item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			PersistentDataContainer data = meta.getPersistentDataContainer();
			return data.has(key, PersistentDataType.STRING) && "true".equals(data.get(key, PersistentDataType.STRING));
		}
		return false;
	}

	// Cancel crafting if the custom item is used
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		for (ItemStack item : event.getInventory().getMatrix()) {
			if (item != null && hasCustomData(item, key)) {
				event.setCancelled(true);
				event.getWhoClicked().sendMessage(ChatColor.RED + "You cannot craft with Artillery Ammo!");
				return;
			}
		}
	}

	// Cancel renaming in an anvil
	@EventHandler
	public void onAnvilRename(PrepareAnvilEvent event) {
		ItemStack result = event.getResult();
		if (result != null && hasCustomData(result, key)) {
			event.setResult(null); // Prevent renaming
			event.getViewers().forEach(viewer -> viewer.sendMessage(ChatColor.RED + "You cannot rename Artillery Ammo!"));
		}
	}
}
