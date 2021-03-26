package democat.customdialogue.api.config.conditions;

import java.util.HashMap;
import java.util.Map;

public class ConditionManager {
	/** Map of condition names to their classes */
    public static final Map<String, Class<? extends ICondition>> CONDITIONS = new HashMap<>();

    /**
     * Registers condition. Call this in postInit.
     */
    public static <T extends ICondition> void register(T condition) {
        // It's the only way I know of accessing T's static methods...
        CONDITIONS.put(condition.getName(), condition.getClass());
    }
}