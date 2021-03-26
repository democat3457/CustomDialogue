package democat.customdialogue.api.config;

import democat.customdialogue.api.config.conditions.ICondition;
import democat.customdialogue.config.conditions.*;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;

import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashMap;

import com.google.gson.annotations.*;

public class Configuration {
	public General general;
	public Dialogue dialogue;
	
	/**
	 * Default configuration
	 */
	public static final Configuration DEFAULT = 
	    new Configuration(
	        new General(false, false),
	        new Dialogue()
	            .addEntry(
	                new EntryMob("minecraft:villager", 5)
	                    .addDialogue(
	                        new EntryDialogue(2, "Hmmm...")
	                            .addRandomCondition()
	                    )
	                    .addDialogue(
	                        new EntryDialogue(2, "Hrm.")
	                            .addRandomCondition()
	                    )
	                    .addDialogue(
	                        new EntryDialogue(1, "Fancy sword!")
	                            .addCondition(
	                                new ConditionHolding(Items.DIAMOND_SWORD, 1, false)
	                            )
	                    )
	                    .addDialogue(
	                        new EntryDialogue(1, "Sleek armor!")
	                            .addCondition(
	                                new ConditionArmor(Items.IRON_HELMET, EntityEquipmentSlot.HEAD)
	                            )
	                            .addCondition(
	                                new ConditionArmor(Items.IRON_CHESTPLATE, EntityEquipmentSlot.CHEST)
	                            )
	                            .addCondition(
	                                new ConditionArmor(Items.IRON_LEGGINGS, EntityEquipmentSlot.LEGS)
	                            )
	                            .addCondition(
	                                new ConditionArmor(Items.IRON_BOOTS, EntityEquipmentSlot.FEET)
	                            )
	                    )
	            )
	    );

	public Configuration() {
		general = new General();
		dialogue = new Dialogue();
	}
	
	private Configuration(General gen, Dialogue dia) {
		general = gen;
		dialogue = dia;
	}

	public static class General {
		public boolean gamestagesCompat;
		public boolean setbonusCompat;
		
		public General() {
			this.gamestagesCompat = false;
			this.setbonusCompat = false;
		}
		
		private General(boolean gc, boolean sbc) {
			this.gamestagesCompat = gc;
			this.setbonusCompat = sbc;
		}
	}

	public static class Dialogue {
		/** Internal mob list */
		public transient Map<String, EntryMob> mobList;
		/** List for use only with gson */
		@SerializedName("mob_list") private List<EntryMob> _mobList;

		public Dialogue() {
			this.mobList = new HashMap<String, EntryMob>();
			this._mobList = new ArrayList<>();
		}

		/**
		 * Adds entry to the mob list
		 * @param entry the entry to add
		 * @return this instance for chaining
		 */
		public Dialogue addEntry(EntryMob entry) {
			if (!mobList.containsKey(entry.resloc)) {
				mobList.put(entry.resloc, entry);
				_mobList.add(entry);
			} else {
				mobList.get(entry.resloc).addDialogues(entry.dialogues);
				_mobList = new ArrayList<>(mobList.values());
			}
			return this;
		}
	
		/**
		 * Adds entries to the mob list
		 * @param entries a collection of entries to add
		 * @return this instance for chaining
		 */
		public Dialogue addEntries(Collection<EntryMob> entries) {
			entries.forEach(this::addEntry);
			return this;
		}

		/**
		 * Fetches first entry it finds from mob list if it exists
		 * @param resloc the id of the mob to fetch
		 * @return the entry, or null if it doesn't exist
		 */
		public EntryMob getEntry(String resloc) {
			if (mobList.containsKey(resloc)) {
				return mobList.get(resloc);
			}
			return null;
		}

		/**
		 * Removes an entry from the mob list if it exists
		 * @param entry the entry to remove
		 * @return this instance for chaining
		 */
		public Dialogue removeEntry(EntryMob entry) {
			if (mobList.containsKey(entry.resloc)) {
				mobList.remove(entry.resloc);
				_mobList.remove(entry);
			}
			return this;
		}

		/**
		 * Removes an entry by id from the mod list if it exists
		 * @param resloc the id of the mob to remove
		 * @return this instance for chaining
		 */
		public Dialogue removeEntryById(String resloc) {
			if (mobList.containsKey(resloc)) {
				_mobList.remove(mobList.get(resloc));
				mobList.remove(resloc);
			}
			return this;
		}

		/**
		 * Removes a list of entries from the mob list if they exist
		 * @param entries the list of entries to remove
		 * @return this instance for chaining
		 */
		public Dialogue removeEntries(Collection<EntryMob> entries) {
			entries.forEach(this::removeEntry);
			return this;
		}

		/**
		 * Removes a list of entries by id from the mob list if they exist
		 * @param reslocs the list of ids to remove
		 * @return this instance for chaining
		 */
		public Dialogue removeEntriesById(Collection<String> reslocs) {
			reslocs.forEach(this::removeEntryById);
			return this;
		}
		
		public List<EntryMob> get_mobList() {
		  return _mobList;
		}
	}

	public static class EntryMob {
		@SerializedName("mob_id") public String resloc;
		/** The weight of a mob not giving dialogue */
		@Since(0.2) public int nullWeight;
		public List<EntryDialogue> dialogues;

		public EntryMob(String resloc, int nullWeight) {
			this.resloc = resloc;
			this.nullWeight = nullWeight;
			this.dialogues = new ArrayList<EntryDialogue>();
		}
		
		/**
		 * Adds a dialogue entry to the list
		 * @param entry the entry to add
		 * @return this instance for chaining
		 */
		public EntryMob addDialogue(EntryDialogue entry) {
			dialogues.add(entry);
			return this;
		}

		/** 
		 * Adds a list of dialogue entries to the list
		 * @param entries the list to add
		 * @return this instance for chaining
		 */
		public EntryMob addDialogues(Collection<EntryDialogue> entries) {
			dialogues.addAll(entries);
			return this;
		}

		/**
		 * Checks whether the dialogue list contains an entry with the given text
		 * @param text the text to search for
		 * @return whether the list contains an entry with the text
		 */
		public boolean containsText(String text) {
			return this.getDialogue(text) != null;
		}

		/**
		 * Finds the first dialogue entry it finds by text from the list if it exists
		 * @param text the id of the entry to be retrieved
		 * @return the dialogue entry if it exists, or null
		 */
		public EntryDialogue getDialogue(String text) {
			Stream<EntryDialogue> stream = dialogues.stream();
			EntryDialogue result = stream.filter((dialogue) -> dialogue.text.equals(text)).findFirst().orElse(null);
			stream.close();
			return result;
		}

		/**
		 * Removes all dialogue entries that have a text matching the parameter
		 * @param text the text to remove from the list
		 * @return this instance for chaining
		 */
		public EntryMob removeDialogueByText(String text) {
			dialogues.removeIf((dialogue) -> dialogue.text.equals(text));
			return this;
		}

		/**
		 * Removes a dialogue entry from the list if it exists
		 * @param entry the entry to remove
		 * @return this instance for chaining
		 */
		public EntryMob removeDialogue(EntryDialogue entry) {
			dialogues.remove(entry);
			return this;
		}

		/**
		 * Removes all dialogue entries that have texts matching the paramater
		 * @param texts the texts to remove from the list
		 * @return this instance for chaining
		 */
		public EntryMob removeDialoguesByText(List<String> texts) {
			texts.forEach(this::removeDialogueByText);
			return this;
		}

		/**
		 * Removes a list of dialogue entries from the list if they exist
		 * @param entries the list of entries to remove
		 * @return this instance for chaining
		 */
		public EntryMob removeDialogues(Collection<EntryDialogue> entries) {
			entries.forEach(this::removeDialogue);
			return this;
		}
	}

	public static class EntryDialogue {
		/** Internal usage */
		public transient Map<Map.Entry<Class<? extends ICondition>, String>, ICondition> conditions;
		/** List for use only gson */
		@SerializedName("conditions") private List<ICondition> _conditions;
		public int weight;
		public String text;

		public EntryDialogue(int weight, String text) {
			this.conditions = new HashMap<>();
			this._conditions = new ArrayList<>();
			this.weight = weight;
			this.text = text;

			this.addRandomCondition();
		}
		
		/**
		 * Adds a RANDOM condition for convenience
		 * @return this instance for chaining
		 */
		public EntryDialogue addRandomCondition() {
		    return this.addCondition(
		        new ConditionRandom()
		    );
		}

		/**
		 * Adds a condition to the dialogue
		 * @param condition the condition to add
		 * @return this instance for chaining
		 */
		public EntryDialogue addCondition(ICondition condition) {
			// Skip if another random condition is added
			// or if a duplicate condition is found
			if ((condition instanceof ConditionRandom && conditions.containsKey(new SimpleEntry<>(condition.type, null)))
				|| conditions.containsKey(new SimpleEntry<>(condition.getClass(), condition.resloc)))
				return this;
			conditions.put(new SimpleEntry<>(condition.getClass(), condition.resloc), condition);
			_conditions.add(condition);
			return this;
		}
		
		/**
		 * Adds a list of conditions to the dialogue
		 * @param entries the list of conditions to add
		 * @return this instance for chaining
		 */
		public EntryDialogue addConditions(Collection<ICondition> entries) {
			entries.forEach(this::addCondition);
			return this;
		}
		
		/**
		 * Fetches the list of (public) conditions. 
		 * Also sets internal conditions list to align with public condition list.
		 * @return the list of gson conditions
		 */
		public List<ICondition> get_conditions() {
			try (Stream<ICondition> stream = _conditions.stream()) {
				conditions = stream.collect(Collectors.toMap((condition) -> new SimpleEntry<>(condition.type, condition.resloc), (condition) -> condition));
			}
			return _conditions;
		}

		/**
		 * Removes a condition from the dialogue if it exists
		 * @param condition the condition to remove
		 * @return this instance for chaining
		 */
		public EntryDialogue removeCondition(ICondition condition) {
			if (conditions.containsKey(new SimpleEntry<>(condition.type, condition.resloc))) {
				conditions.remove(new SimpleEntry<>(condition.type, condition.resloc));
			}
			if (_conditions.contains(condition)) {
				_conditions.remove(condition);
			}
			return this;
		}

		/**
		 * Removes a condition from the dialogue based on condition type and parameter if it exists
		 * @param entry a Map.Entry of the condition type and condition parameter
		 * @return this instance for chaining
		 */
		public EntryDialogue removeConditionExplicit(Map.Entry<Class<? extends ICondition>, String> entry) {
			if (conditions.containsKey(entry)) {
				if (_conditions.contains(conditions.get(entry))) {
					_conditions.remove(conditions.get(entry));
				}
				conditions.remove(entry);
			}
			return this;
		}

		/**
		 * Removes all conditions of a certain type from the dialogue if they exist
		 * @param type the type to remove
		 * @return this instance for chaining
		 */
		public EntryDialogue removeConditionByType(Class<? extends ICondition> type) {
			try (Stream<Map.Entry<Class<? extends ICondition>, String>> stream = conditions.keySet().stream()) {
				this.removeConditionsExplicit(stream.filter((entry) -> entry.getKey() == type).collect(Collectors.toList()));
			}
			return this;
		}

		/**
		 * Removes a list of conditions from the dialogue if they exist
		 * @param conditions the list of conditions to remove
		 * @return this instance for chaining
		 */
		public EntryDialogue removeConditions(Collection<ICondition> conditions) {
			conditions.forEach(this::removeCondition);
			return this;
		}

		/**
		 * Removes a list of conditions from the dialogue based on type and parameter if they exist
		 * @param conditions the list of Map.Entry to remove, with the type and parameter
		 * @return this instance for chaining
		 */
		public EntryDialogue removeConditionsExplicit(Collection<Map.Entry<Class<? extends ICondition>, String>> conditions) {
			conditions.forEach(this::removeConditionExplicit);
			return this;
		}
	}
}
