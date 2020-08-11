package democat.customdialogue.handlers;

import java.util.HashMap;
import java.util.Map;

import democat.customdialogue.CustomDialogue;
import democat.customdialogue.api.config.Configuration.EntryMob;
import democat.customdialogue.api.config.Configuration.EntryDialogue.Condition;
import democat.customdialogue.util.Utilities;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
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
            ItemStack holding = event.getEntityPlayer().getHeldItemMainhand();
            NonNullList<ItemStack> armorList = event.getEntityPlayer().inventory.armorInventory;
            InventoryPlayer inv = event.getEntityPlayer().inventory;

            Map<String, Integer> validStrings = new HashMap<>();

            if (mob.nullWeight != 0)
                validStrings.put("", new Integer(mob.nullWeight));

            mob.dialogues.forEach((dialogue) -> {
                boolean valid = true;

                CustomDialogue.debug("testing dialogue conditions of " + dialogue.text);

                for (Condition condition : dialogue.get_conditions()) {
                    switch (condition.type) {
                        case ITEM_HOLDING:
                            CustomDialogue.debug("found item holding condition");
                            if (!holding.isItemEqualIgnoreDurability(new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(condition.resloc))))) {
                                valid = false;
                                CustomDialogue.debug("invalid holding condition - " + condition.resloc);
                            }
                            break;
                        case ITEM_INVENTORY:
                            CustomDialogue.debug("found item inv condition");
                            if (!inv.hasItemStack(new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(condition.resloc))))) {
                                valid = false;
                                CustomDialogue.debug("invalid inv condition - " + condition.resloc);
                            }
                            break;
                        case ARMOR:
                            CustomDialogue.debug("found armor condition");
                            if (!armorList.contains(new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(condition.resloc))))) {
                                valid = false;
                                CustomDialogue.debug("invalid armor condition - " + condition.resloc);
                            }
                            break;
                        case GAMESTAGE:
                            /*
                            CustomDialogue.debug("found gamestage condition");
                            if (!gamestages.contains(condition.resloc)) {
                                valid = false;
                                CustomDialogue.debug("invalid gamestage condition - " + condition.resloc);
                            }
                            break;
                            */
                        case RANDOM:
                        default:
                            CustomDialogue.debug("found random condition");
                            break;
                    }
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