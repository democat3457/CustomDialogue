package democat.customdialogue;

import com.google.gson.*;
import com.google.gson.FieldNamingPolicy;

import org.apache.logging.log4j.Logger;

import democat.customdialogue.commands.*;
import democat.customdialogue.config.*;
// import democat.customdialogue.config.Configuration.*;
// import democat.customdialogue.config.Configuration.EntryDialogue.*;
import democat.customdialogue.handlers.*;
// import democat.customdialogue.util.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.IOException;
// import java.lang.StringBuilder;
// import java.util.Scanner;

// import java.util.List;
// import java.util.ArrayList;
// import java.util.Map;
// import java.util.HashMap;

@Mod(modid = CustomDialogue.MODID, name = CustomDialogue.NAME, version = CustomDialogue.VERSION)
public class CustomDialogue {
	public static final String MODID = "customdialogue";
	public static final String NAME = "Entity Testing";
	public static final String VERSION = "0.4.2";

	public static Logger logger;

	public static double configVersion = 0.2;
	
	public static ConfigHandler configHandler;
	public static Gson gson;

	public CustomDialogue() {
		MinecraftForge.EVENT_BUS.register(new DialogueHandler());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws IOException {
		logger = event.getModLog();

		gson = new GsonBuilder()
		  .setVersion(configVersion)
		  .setPrettyPrinting()
		  .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
		  .create();

		configHandler = new ConfigHandler(configVersion, event.getModConfigurationDirectory(), "customdialogue/General.json", "customdialogue/dialogue");

		configHandler.saveDefaultConfigs();
		configHandler.loadConfigs();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// some example code
		logger.info("Custom Dialogue successfully initialized!");
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new DialogueCommand());
	}

	/*
	public static void main(String[] args) throws IOException {

		// To get the mod config directory, use
		// FMLPreInitializationEvent
		//   #getModConfigurationDirectory
		// This returns a java.io.File object. Then, 
		// use the File(File, Sring) constructor to
		// create the designated files.
		configHandler = new ConfigHandler(configVersion, "customdialogue/General.json", "customdialogue/dialogue");
		
		configHandler.saveDefaultConfigs();
		
		configHandler.loadConfigs();

		String holding = "";
		String armor = "";
		List<String> gamestages = new ArrayList<>();
		
		Scanner sc = new Scanner(System.in);
		System.out.print("> ");
		while (sc.hasNext()) {
			String input = sc.nextLine().trim();
			if (input.equals("exit"))
				break;
			if (input.toLowerCase().startsWith("holding: ")) {
				holding = input.substring(9);
			} else if (input.toLowerCase().startsWith("armor: ")) {
				armor = input.substring(7);
			} else if (input.toLowerCase().startsWith("gamestage: ")) {
				gamestages.add(input.substring(11));
			} else if (input.toLowerCase().startsWith("mob: ")) {
			  debug("found startswith mob: " + input.substring(5));
				if (!configHandler.config.dialogue.mobList.containsKey(input.substring(5))) {
					System.out.println("Invalid mob!");
					debug(gson.toJson(configHandler.config.dialogue));
					System.out.print("> ");
					continue;
				}
				debug("moblist contains mob");
				EntryMob mob = configHandler.config.dialogue.mobList.get(input.substring(5));
				System.out.println(randomDialogue(holding, armor, gamestages, mob));
			} else if (input.equals("status")) {
				System.out.println("Holding item: " + holding);
				System.out.println("Armor item: " + armor);
				
				StringBuilder sb = new StringBuilder();
				for (String s : gamestages) {
					sb.append(s);
					sb.append("\t");
				}
				System.out.println("Gamestages: " + sb.toString());
			} else if (input.equals("export")) {
			    configHandler.exportConfigs();
			}
			
		  System.out.print("> ");
		}
		System.out.println("Bye!");
		sc.close();
	}

	public static String randomDialogue(String holding, String armor, List<String> gamestages, EntryMob mob) {
		debug("entered randomDialogue");
		
		Map<String, Integer> validStrings = new HashMap<>();

		if (mob.nullWeight != 0)
			validStrings.put("", new Integer(mob.nullWeight));
		
		mob.dialogues.forEach((dialogue) -> {
			boolean valid = true;
			
			debug("testing dialogue conditions of " + dialogue.text);

			for (Condition condition : dialogue.get_conditions()) {
				switch (condition.type) {
					case ITEM_HOLDING:
					  debug("found item holding condition");
						if (!condition.resloc.equals(holding)) {
							valid = false;
							debug("invalid holding condition - " + condition.resloc + " != " + holding);
						}
						break;
					case ITEM_INVENTORY:
					  debug("found item inv condition");
						if (!condition.resloc.equals(holding) && !condition.resloc.equals(armor)) {
							valid = false;
							debug("invalid inv condition - " + condition.resloc + " != " + holding + " && " + armor);
						}
						break;
					case ARMOR:
					  debug("found armor condition");
						if (!condition.resloc.equals(armor)) {
							valid = false;
							debug("invalid armor condition - " + condition.resloc + " != " + armor);
						}
						break;
					case GAMESTAGE:
					  debug("found gamestage condition");
						if (!gamestages.contains(condition.resloc)) {
							valid = false;
							debug("invalid gamestage condition - " + condition.resloc);
						}
						break;
					case RANDOM:
					default:
					  debug("found random condition");
						break;
				}
			}
			
			if (valid) {
				validStrings.put(dialogue.text, new Integer(dialogue.weight));
				debug("valid dialogue " + dialogue.text);
		  }
		});
		
		return Utilities.randomStringWithWeight(validStrings);
	}
	*/
	
	public static void debug(String str) {
	  logger.debug(str);
	}
}
