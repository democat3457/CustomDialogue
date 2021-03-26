package democat.customdialogue.config.conditions;

import java.lang.reflect.Type;
import java.util.function.Predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import democat.customdialogue.api.config.conditions.ICondition;
import democat.customdialogue.util.Utilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;

public class ConditionInventory extends ICondition {
    private ItemStack invItem;

    public ConditionInventory(ItemStack invItem) {
        this.invItem = invItem;
    }

    public ConditionInventory(Item item, int count) {
        this.invItem = new ItemStack(item);
        this.invItem.setCount(count);
    }

    public ConditionInventory(String resloc, int count) {
        this.invItem = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(resloc)));
        this.invItem.setCount(count);
    }

    @Override
    public String getName() {
        return "inventory";
    }

    @Override
    public Predicate<EntityInteract> isSatisfied() {
        return new Predicate<EntityInteract>() {
        	@Override
        	public boolean test(EntityInteract event) {
        		return event.getEntityPlayer().inventory.hasItemStack(invItem);
        	}
        };
    }

    @Override
    public JsonElement serialize(ICondition src, Type typeOfSrc, JsonSerializationContext context) {
        ConditionInventory c = (ConditionInventory) src;
        JsonObject s = (JsonObject) super.serialize(src, typeOfSrc, context);
        JsonObject itemJson = (JsonObject) context.serialize(c.invItem);
        return Utilities.merge(s, itemJson);
    }

    @Override
    public ConditionInventory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        ConditionInventory result = (ConditionInventory) super.deserialize(json, typeOfT, context);
        result.invItem = context.deserialize(json, ItemStack.class);
        return result;
    }
}