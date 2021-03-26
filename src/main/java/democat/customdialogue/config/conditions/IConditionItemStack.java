package democat.customdialogue.config.conditions;

import java.lang.reflect.Type;
import java.util.function.Predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import democat.customdialogue.api.config.conditions.ICondition;
import democat.customdialogue.util.PredicateHelper;
import democat.customdialogue.util.Utilities;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;

public abstract class IConditionItemStack extends ICondition {
    protected ItemStack stack;
    protected boolean testForNBT;
    protected boolean exclusiveNBT;

    @Override
    public Predicate<EntityInteract> isSatisfied() {
	    return PredicateHelper.always(true);
    }

    @Override
    public JsonElement serialize(ICondition src, Type typeOfSrc, JsonSerializationContext context) {
        IConditionItemStack c = (IConditionItemStack) src;
        JsonObject s = (JsonObject) super.serialize(src, typeOfSrc, context);
        s.addProperty("useNBT", c.testForNBT);
        s.addProperty("exclusiveNBT", c.exclusiveNBT);
        JsonObject itemJson = (JsonObject) context.serialize(c.stack);
        return Utilities.merge(s, itemJson);
    }

    @Override
    public IConditionItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        IConditionItemStack result = (IConditionItemStack) super.deserialize(json, typeOfT, context);
        result.testForNBT = json.getAsJsonObject().get("useNBT").getAsBoolean();
        result.exclusiveNBT = json.getAsJsonObject().get("exclusiveNBT").getAsBoolean();
        result.stack = context.deserialize(json, ItemStack.class);
        return result;
    }
}