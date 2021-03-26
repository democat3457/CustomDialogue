package democat.customdialogue.handlers;

import java.util.HashMap;
import java.util.Map;

import democat.customdialogue.CustomDialogue;
import democat.customdialogue.api.config.Configuration.EntryMob;
import democat.customdialogue.api.config.conditions.ICondition;
import democat.customdialogue.util.Utilities;

import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class DialogueHandler {
    public DialogueHandler() {

    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide().isClient())
            return;
        
        EntityEntry ee = EntityRegistry.getEntry(event.getTarget().getClass());

        if (CustomDialogue.configHandler.config.dialogue.mobList.containsKey(ee.getRegistryName().toString())) {
            EntryMob mob = CustomDialogue.configHandler.config.dialogue.mobList.get(ee.getRegistryName().toString());

            Map<String, Integer> validStrings = new HashMap<>();

            if (mob.nullWeight != 0)
                validStrings.put("", new Integer(mob.nullWeight));

            mob.dialogues.forEach((dialogue) -> {
                boolean valid = true;

                CustomDialogue.debug("testing dialogue conditions of " + dialogue.text);

                /**
                 * Check if all the conditions are satisfied
                 */
                for (ICondition condition : dialogue.conditions) {
                	if (!condition.isSatisfied().test(event)) {
                		valid = false;
                		CustomDialogue.debug("invalid condition - " + condition.toString());
                	}
                	if (!CustomDialogue.configHandler.config.general.debug) break;
                }

                if (valid) {
                    validStrings.put(dialogue.text, new Integer(dialogue.weight));
                    CustomDialogue.debug("valid dialogue " + dialogue.text);
                }
            });

            String msg = Utilities.randomStringWithWeight(validStrings);
            if (!msg.equals("")) {
                event.getEntityPlayer().sendMessage(new TextComponentString(event.getTarget().getName() + ": " + msg));
            }
        }
    }
}