package democat.customdialogue.api.config.conditions;

import java.lang.reflect.Type;
import java.util.function.Predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;

/**
 * The parent class for dialogue conditions.
 */
public abstract class ICondition {
    /**
     * The name of the condition should be what is defined in the config; case
     * doesn't matter but config will default to as returned in here.
     * 
     * @return the condition's name
     */
    public String getName() {
        return "";
    }

    /**
     * Your condition-checking code should go here.
     * 
     * @param event the event that runs when the player interacts with an entity
     * @return whether the condition is satisfied
     */
    public abstract Predicate<EntityInteract> isSatisfied();

    /**
     * The function that is called when serializing the condition in the config.
     * 
     * @param src       the object to be serialized
     * @param typeOfSrc the type of the object to be serialized
     * @param context   the context of the json serialization
     * @return the JsonElement to be used to represent the condition
     */
    public JsonElement serialize(ICondition src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject s = new JsonObject();
        s.addProperty("name", src.getName());
        return s;
    }

    /**
     * The function that is called when deserializing the condition in the config.
     * @param json the json to be deserialized
     * @param typeOfT the type of the json to be deserialized
     * @param context the context of the json deserialization
     * @return the condition
     */
    public ICondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        // Should be handled by ConditionManager
        // String s = json.getAsJsonObject().get("name").getAsString();
        ICondition result = new ICondition() {
			@Override
			public Predicate<EntityInteract> isSatisfied() {
				return null;
			}
        };
        return result;
    }
}