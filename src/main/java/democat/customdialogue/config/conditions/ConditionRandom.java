package democat.customdialogue.config.conditions;

import java.lang.reflect.Type;
import java.util.function.Predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

import democat.customdialogue.api.config.conditions.ICondition;
import democat.customdialogue.util.PredicateHelper;

import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;

public class ConditionRandom extends ICondition {
    @Override
    public String getName() {
        return "random";
    }

    @Override
    public Predicate<EntityInteract> isSatisfied() {
        return PredicateHelper.always(true);
    }

    @Override
    public JsonElement serialize(ICondition src, Type typeOfSrc, JsonSerializationContext context) {
        return super.serialize(src, typeOfSrc, context);
    }

    @Override
    public ConditionRandom deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return (ConditionRandom) super.deserialize(json, typeOfT, context);
    }
}