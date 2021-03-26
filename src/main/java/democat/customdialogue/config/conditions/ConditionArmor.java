package democat.customdialogue.config.conditions;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import democat.customdialogue.api.config.conditions.ICondition;
import democat.customdialogue.util.Utilities;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;;

public class ConditionArmor extends ICondition {
    private ItemStack armor;
    private List<EntityEquipmentSlot> armorSlots;

    public ConditionArmor(Item armor, EntityEquipmentSlot... armorSlots) {
        this(armor, Arrays.asList(armorSlots));
    }

    public ConditionArmor(Item armor, List<EntityEquipmentSlot> armorSlots) {
        this(new ItemStack(armor), armorSlots);
    }
    
    public ConditionArmor(ItemStack armor, EntityEquipmentSlot... armorSlots) {
        this(armor, Arrays.asList(armorSlots));
    }

    public ConditionArmor(ItemStack armor, List<EntityEquipmentSlot> armorSlots) {
        this.armor = armor;
        this.armorSlots = armorSlots;
    }

    @Override
    public String getName() {
        return "armor";
    }

    @Override
    public Predicate<EntityInteract> isSatisfied() {
    	return new Predicate<EntityInteract>() {
    		@Override
    		public boolean test(EntityInteract event) {
		        if (event.getEntityPlayer().inventory.armorInventory.contains(armor)) {
		            for (EntityEquipmentSlot slot : armorSlots)
		                if (event.getEntityPlayer().inventory.armorItemInSlot(slot.ordinal()).isItemEqualIgnoreDurability(armor))
		                    return true;
		            return false;
		        } else
		            return false;
    		}
    	};
    }

    @Override
    public JsonElement serialize(ICondition src, Type typeOfSrc, JsonSerializationContext context) {
        ConditionArmor c = (ConditionArmor) src;
        JsonObject s = (JsonObject) super.serialize(src, typeOfSrc, context);
        JsonArray a = new JsonArray();
        for (EntityEquipmentSlot slot : c.armorSlots) {
            a.add(slot.toString());
        }
        s.add("slots", a);
        JsonObject itemJson = (JsonObject) context.serialize(c.armor);
        return Utilities.merge(s, itemJson);
    }

    @Override
    public ConditionArmor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        ConditionArmor result = (ConditionArmor) super.deserialize(json, typeOfT, context);
        result.armor = context.deserialize(json, ItemStack.class);
        if (json.getAsJsonObject().has("slots")) {
            result.armorSlots = Arrays.asList();
            json.getAsJsonObject().get("slots").getAsJsonArray().forEach((entry) -> {
                result.armorSlots.add(EntityEquipmentSlot.valueOf(entry.getAsString()));
            });
        } else
            result.armorSlots = Arrays.asList(EntityEquipmentSlot.CHEST, EntityEquipmentSlot.FEET, 
                    EntityEquipmentSlot.HEAD, EntityEquipmentSlot.LEGS);
        return result;
    }
}