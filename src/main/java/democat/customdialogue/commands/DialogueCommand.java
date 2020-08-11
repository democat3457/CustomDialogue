package democat.customdialogue.commands;

import democat.customdialogue.CustomDialogue;
import democat.customdialogue.api.config.Configuration.*;
import democat.customdialogue.api.config.Configuration.EntryDialogue.Condition;
import democat.customdialogue.api.config.Configuration.EntryDialogue.Condition.ConditionTypes;
import democat.customdialogue.util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class DialogueCommand extends CommandBase {
    private List<String> subCommands = new ArrayList<>();
    private Map<String, String> configCommands = new HashMap<>();

    public DialogueCommand() {
        this.subCommands.add("config");
        this.subCommands.add("export");
        this.subCommands.add("list");
        this.subCommands.add("reload");

        this.configCommands.put("addMob", "");
        this.configCommands.put("removeMob", "");
        this.configCommands.put("addDialogue", "");
        this.configCommands.put("removeDialogue", "");
        this.configCommands.put("addCondition", "");
        this.configCommands.put("removeCondition", "");
        this.configCommands.put("removeAllConditionsWithType", "");
    }

    @Override
    public String getName() {
        return "cdialogue";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/cdialogue <action>\n"
             + "OR\n"
             + "/cdialogue config <action>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args == null || args.length == 0) {
            StringBuilder sb = new StringBuilder("Valid subcommands: \n");
            this.subCommands.forEach((command) -> {
                sb.append("\n/cdialogue " + command + "\n");
            });
            sender.sendMessage(Utilities.tcColor(sb.toString(), TextFormatting.YELLOW));
        } else {
            String subcommand = args[0].toLowerCase();

            /**
             * /cdialogue config
             */
            if (subcommand.equalsIgnoreCase(this.subCommands.get(0))) {
                if (args.length == 1 || args[1] == null || args[1].equals("")) {
                    StringBuilder sb = new StringBuilder("Configuration Actions: \n");
                    this.configCommands.forEach((configAction, params) -> sb.append("/cdialogue config " + configAction + " " + params));
                    sender.sendMessage(Utilities.tcColor(sb.toString(), TextFormatting.RED));
                } else {
                    String action = args[1].toLowerCase();

                    if (args.length == 2 || args[2] == null || args[2].equals("")) 
                        if (this.configCommands.containsKey(args[1]))
                            sender.sendMessage(Utilities.tcColor("/cdialogue config " + args[1] + " " + this.configCommands.get(args[1]), TextFormatting.RED));
                        else {
                            StringBuilder sb = new StringBuilder("Valid subcommands: ");
                            this.configCommands.forEach((command, params) -> {
                                sb.append("\n/cdialogue config " + command + " " + params);
                            });
                            sender.sendMessage(Utilities.tcColor(sb.toString(), TextFormatting.RED));
                        }
                    else {
                        String param = args[2].toLowerCase();

                        try {
                            /**
                             * /cdialogue config addMob
                             */
                            if (action.equalsIgnoreCase("addMob")) {
                                if (args.length == 3 || args[3] == null || args[3].equals(""))
                                    sender.sendMessage(Utilities.tcColor("Missing required argument - /cdialogue config addMob <mob> <nullWeight>", TextFormatting.RED));
                                else if (ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(param))) {
                                    try {
                                        CustomDialogue.configHandler.config.dialogue.addEntry(new EntryMob(param, Integer.parseInt(args[3])));
                                        sender.sendMessage(Utilities.tcColor("Added mob " + new ResourceLocation(param).toString(), TextFormatting.GREEN));
                                    } catch (NumberFormatException e) {
                                        sender.sendMessage(Utilities.tcColor("Invalid argument \"" + args[3] + "\" - /cdialogue config addMob <mob> <nullWeight>", TextFormatting.RED));
                                    }
                                } else
                                    sender.sendMessage(Utilities.tcColor("Invalid mob id!\n/cdialogue config addMob <mob> <nullWeight>", TextFormatting.RED));
                            }
                            /**
                             * /cdialogue config removeMob
                             */
                            else if (action.equalsIgnoreCase("removeMob")) {
                                if (CustomDialogue.configHandler.config.dialogue.mobList.containsKey(param)) {
                                    CustomDialogue.configHandler.config.dialogue.removeEntryById(param);
                                    sender.sendMessage(Utilities.tcColor("Removed mob dialogue entries for " + new ResourceLocation(param).toString(), TextFormatting.GREEN));
                                } else
                                    sender.sendMessage(Utilities.tcColor("Invalid mob id!\n/cdialogue config removeMob <mob>", TextFormatting.RED));
                            } 
                            /**
                             * /cdialogue config addDialogue
                             */
                            else if (action.equalsIgnoreCase("addDialogue")) {
                                if (args.length == 3 || args.length == 4 || args[3] == null || args[4] == null || args[3].equals("") || args[4].equals(""))
                                    sender.sendMessage(Utilities.tcColor("Missing required argument - /cdialogue config addDialogue <mob> <weight> <text>", TextFormatting.RED));
                                else if (CustomDialogue.configHandler.config.dialogue.mobList.containsKey(new ResourceLocation(param).toString())) {
                                    try {
                                        CustomDialogue.configHandler.config.dialogue.getEntry(param).addDialogue(new EntryDialogue(Integer.parseInt(args[3]), args[4]));
                                        sender.sendMessage(Utilities.tcColor("Added dialogue for mob " + new ResourceLocation(param).toString(), TextFormatting.GREEN));
                                    } catch (NumberFormatException e) {
                                        sender.sendMessage(Utilities.tcColor("Invalid argument \"" + args[3] + "\" - /cdialogue config addDialogue <mob> <weight> <text>", TextFormatting.RED));
                                    }
                                } else
                                    sender.sendMessage(Utilities.tcColor("Invalid mob id!\n/cdialogue config addDialogue <mob> <weight> <text>", TextFormatting.RED));
                            }
                            /**
                             * /cdialogue config removeDialogue
                             */
                            else if (action.equalsIgnoreCase("removeDialogue")) {
                                if (args.length == 3 || args[3] == null || args[3].equals(""))
                                    sender.sendMessage(Utilities.tcColor("Missing required argument - /cdialogue config removeDialogue <mob> <text>", TextFormatting.RED));
                                else if (CustomDialogue.configHandler.config.dialogue.mobList.containsKey(new ResourceLocation(param).toString())) {
                                    if (CustomDialogue.configHandler.config.dialogue.getEntry(param).containsText(args[3])) {
                                        CustomDialogue.configHandler.config.dialogue.getEntry(param).removeDialogueByText(args[3]);
                                        sender.sendMessage(Utilities.tcColor("Removed dialogue for mob " + new ResourceLocation(param).toString(), TextFormatting.GREEN));
                                    } else
                                        sender.sendMessage(Utilities.tcColor("Invalid dialogue text! Maybe you forgot to put it in quotes?\n/cdialogue config removeDialogue <mob> <text>", TextFormatting.RED));
                                } else
                                    sender.sendMessage(Utilities.tcColor("Invalid mob id!\n/cdialogue config removeDialogue <mob> <text>", TextFormatting.RED));
                            }
                            /**
                             * /cdialogue config addCondition
                             */
                            else if (action.equalsIgnoreCase("addCondition")) {
                                if (args.length == 3 || args.length == 4 || args.length == 5
                                        || args[3] == null || args[4] == null || args[5] == null
                                        || args[3].equals("") || args[4].equals("") || args[5].equals(""))
                                    sender.sendMessage(Utilities.tcColor("Missing required argument - /cdialogue config addCondition <mob> <text> <conditionType> <conditionParam>", TextFormatting.RED));
                                else if (CustomDialogue.configHandler.config.dialogue.mobList.containsKey(new ResourceLocation(param).toString())) {
                                    if (CustomDialogue.configHandler.config.dialogue.getEntry(param).containsText(args[3])) {
                                        CustomDialogue.configHandler.config.dialogue.getEntry(param).getDialogue(args[3]).addCondition(new Condition(ConditionTypes.valueOf(args[4].trim().toUpperCase()), args[5]));
                                        sender.sendMessage(Utilities.tcColor("Added condition for mob " + new ResourceLocation(param).toString() + " and dialogue with text " + args[3], TextFormatting.GREEN));
                                    } else 
                                        sender.sendMessage(Utilities.tcColor("Invalid dialogue text! Maybe you forgot to put it in quotes?\n/cdialogue config addCondition <mob> <text> <conditionType> <conditionParam>", TextFormatting.RED));
                                } else
                                    sender.sendMessage(Utilities.tcColor("Invalid mob id!\n/cdialogue config addCondition <mob> <text> <conditionType> <conditionParam>", TextFormatting.RED));
                            }
                            /**
                             * /cdialogue config removeCondition
                             */
                            else if (action.equalsIgnoreCase("removeCondition")) {
                                if (args.length == 3 || args.length == 4 || args.length == 5
                                        || args[3] == null || args[4] == null || args[5] == null
                                        || args[3].equals("") || args[4].equals("") || args[5].equals(""))
                                    sender.sendMessage(Utilities.tcColor("Missing required argument - /cdialogue config removeCondition <mob> <text> <conditionType> <conditionParam>", TextFormatting.RED));
                                else if (CustomDialogue.configHandler.config.dialogue.mobList.containsKey(new ResourceLocation(param).toString())) {
                                    if (CustomDialogue.configHandler.config.dialogue.getEntry(param).containsText(args[3])) {
                                        if (CustomDialogue.configHandler.config.dialogue.getEntry(param).getDialogue(args[3]).conditions.containsKey(new SimpleEntry<>(ConditionTypes.valueOf(args[4].trim().toUpperCase()), args[5]))) {
                                            CustomDialogue.configHandler.config.dialogue.getEntry(param).getDialogue(args[3]).removeConditionExplicit(new SimpleEntry<>(ConditionTypes.valueOf(args[4].trim().toUpperCase()), args[5]));
                                            sender.sendMessage(Utilities.tcColor("Removed condition for mob " + new ResourceLocation(param).toString() + " and dialogue with text " + args[3], TextFormatting.GREEN));
                                        } else
                                            sender.sendMessage(Utilities.tcColor("Unable to find condition with type " + args[4].trim().toUpperCase() + " and parameter " + args[5], TextFormatting.RED));
                                    } else
                                        sender.sendMessage(Utilities.tcColor("Invalid dialogue text! Maybe you forgot to put it in quotes?\n/cdialogue config removeCondition <mob> <text> <conditionType> <conditionParam>", TextFormatting.RED));
                                } else
                                    sender.sendMessage(Utilities.tcColor("Invalid mob id!\n/cdialogue config removeCondition <mob> <text> <conditionType> <conditionParam>", TextFormatting.RED));
                            }
                            /**
                             * /cdialogue config removeAllConditionsWithType
                             */
                            else if (action.equalsIgnoreCase("removeAllConditionsWithType")) {
                                if (args.length == 3 || args.length == 4 
                                        || args[3] == null || args[4] == null 
                                        || args[3].equals("") || args[4].equals(""))
                                    sender.sendMessage(Utilities.tcColor("Missing required argument - /cdialogue config removeAllConditionsWithType <mob> <text> <conditionType>", TextFormatting.RED));
                                else if (CustomDialogue.configHandler.config.dialogue.mobList.containsKey(new ResourceLocation(param).toString())) {
                                    if (CustomDialogue.configHandler.config.dialogue.getEntry(param).containsText(args[3])) {
                                        CustomDialogue.configHandler.config.dialogue.getEntry(param).getDialogue(args[3]).removeConditionByType(ConditionTypes.valueOf(args[4].trim().toUpperCase()));
                                        sender.sendMessage(Utilities.tcColor("Removed conditions with type " + ConditionTypes.valueOf(args[4].trim().toUpperCase()) + " for mob " + new ResourceLocation(param).toString() + " and dialogue with text " + args[3]
                                            + ".\nNOTE: This command will succeed even if it did not remove any conditions. This is due to logical complications.", TextFormatting.GREEN));
                                    } else
                                        sender.sendMessage(Utilities.tcColor("Invalid dialogue text! Maybe you forgot to put it in quotes?\n/cdialogue config removeAllConditionsWithType <mob> <text> <conditionType>", TextFormatting.RED));
                                } else
                                    sender.sendMessage(Utilities.tcColor("Invalid mob id!\n/cdialogue config removeAllConditionsWithType <mob> <text> <conditionType>", TextFormatting.RED));
                            } else {
                                StringBuilder sb = new StringBuilder(action + " is not a valid config command. Valid options: ");
                                this.configCommands.forEach((command, params) -> {
                                    sb.append("\n/cdialogue config " + command + " " + params);
                                });
                                sender.sendMessage(Utilities.tcColor(sb.toString(), TextFormatting.RED));
                            }
                        } catch (IllegalArgumentException e) {
                            sender.sendMessage(Utilities.tcColor(e.getMessage(), TextFormatting.RED));
                        }
                    }
                }
            }
            /**
             * /cdialogue export
             */
            else if (subcommand.equalsIgnoreCase(this.subCommands.get(1))) {
                try {
                    CustomDialogue.configHandler.exportConfigs();
                    sender.sendMessage(Utilities.tcColor("Successfully exported!", TextFormatting.YELLOW));
                    sender.sendMessage(Utilities.tcLinkFile("[Open Config Folder]", TextFormatting.GOLD, CustomDialogue.configHandler.getConfigFolder()));
                } catch (IOException e) {
                    sender.sendMessage(Utilities.tcColor("IO Exception: " + e.getLocalizedMessage(), TextFormatting.DARK_RED));
                    CustomDialogue.logger.catching(e);
                }
            } 
            /**
             * /cdialogue list
             */
            else if (subcommand.equalsIgnoreCase(this.subCommands.get(2))) {
                StringBuilder sb = new StringBuilder("Mob List: \n");
                CustomDialogue.configHandler.config.dialogue.mobList.forEach((resloc, entry) -> {
                    sb.append("  Mob: " + resloc + "\n");
                    sb.append("    Null weight: " + entry.nullWeight + "\n");
                    if (entry.dialogues != null) {
                        sb.append("    Dialogue List: \n");
                        entry.dialogues.forEach((dialogue) -> {
                            sb.append("      Text: " + dialogue.text + "\n");
                            sb.append("        Weight: " + dialogue.weight + "\n");
                            if (dialogue.conditions != null) {
                                sb.append("        Condition List: \n");
                                dialogue.conditions.forEach((map, condition) -> {
                                    sb.append("          Condition Type: " + condition.type + "\n");
                                    sb.append("            Condition Parameter: " + condition.resloc + "\n");
                                });
                            }
                        });
                    }
                });
                sb.trimToSize();
                sender.sendMessage(Utilities.tcColor(sb.toString(), TextFormatting.YELLOW, Formats.ITALIC));
            }
            /**
             * /cdialogue reload
             */
            else if (subcommand.equalsIgnoreCase(this.subCommands.get(3))) {
                try {
                    CustomDialogue.configHandler.loadConfigs();
                    sender.sendMessage(Utilities.tcColor("Successfully reloaded!", TextFormatting.GREEN));
                } catch (IOException e) {
                    sender.sendMessage(Utilities.tcColor("IO Exception: " + e.getLocalizedMessage(), TextFormatting.DARK_RED));
                    CustomDialogue.logger.catching(e);
                }
            }
            else {
                StringBuilder sb = new StringBuilder(subcommand + " is not a valid dialogue command. Valid options: ");
                this.subCommands.forEach((command) -> {
                    sb.append("\n/cdialogue " + command);
                });
                sender.sendMessage(Utilities.tcColor(sb.toString(), TextFormatting.RED));
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if (sender.getCommandSenderEntity() == null)
            return true;
        if (server.isSinglePlayer())
            return true;
        UserListOps ops = server.getPlayerList().getOppedPlayers();
        return (ops.getKeys() != null && ops.getKeys().length != 0) || ops.getGameProfileFromName(sender.getName()) != null;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args[0].equalsIgnoreCase("config") && (args.length == 1 || args[1].equals("") || args[1] == null)) {
            return new ArrayList<>(this.configCommands.keySet());
        } else if (args[0].equalsIgnoreCase("config")) {
            return getListOfStringsMatchingLastWord(args, this.configCommands.keySet());
        } else if (args == null || args.length == 0 || args[0] == "") {
            return this.subCommands;
        } else {
            return getListOfStringsMatchingLastWord(args, this.subCommands);
        }
    }
}