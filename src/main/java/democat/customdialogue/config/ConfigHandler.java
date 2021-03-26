package democat.customdialogue.config;

import democat.customdialogue.api.config.*;
import democat.customdialogue.api.config.conditions.ICondition;
import democat.customdialogue.api.serializers.SerializerItemStack;
import net.minecraft.item.ItemStack;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.File;
import java.nio.file.*;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ConfigHandler {
	public Configuration config;
	private Gson gson;

	private String generalFp;
	private String dialogueFolder;
	private String defaultDialogueConfigFp;

	private String configFolder;

	public ConfigHandler(double configVersion, String generalFp, String dialogueFolder) {
		this(configVersion, new File("config/"), generalFp, dialogueFolder);
	}

	public ConfigHandler(double configVersion, File configDirectory, String generalFp, String dialogueFolder) {
		config = new Configuration();

		JsonSerializer<ICondition> conditionSerializer = new JsonSerializer<ICondition>() {
			@Override
			public JsonElement serialize(ICondition src, Type typeOfSrc, JsonSerializationContext context) {
				return src.serialize(src, typeOfSrc, context);
			}
		};
		gson = new GsonBuilder()
		  .setVersion(configVersion)
		  .setPrettyPrinting()
		  .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
		  .registerTypeAdapter(ItemStack.class, new SerializerItemStack())
		  .registerTypeAdapter(ICondition.class, conditionSerializer)
		  .create();
		
		this.configFolder = configDirectory.getAbsolutePath() + "/customdialogue";
		this.generalFp = configDirectory.getAbsolutePath() + "/" + generalFp;
		this.dialogueFolder = configDirectory.getAbsolutePath() + "/" + dialogueFolder;
		this.defaultDialogueConfigFp = "dialogue.json";
	}

	public void loadConfigs() throws IOException {
		System.out.println("Loading configs");
		
		try {
			config.general = gson.fromJson(
				new BufferedReader(
				  new FileReader(generalFp)
				),
				config.general.getClass()
			);

			System.out.println("Loaded general config");
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find general config! Make sure it's at location " + generalFp);
		}

		try {
			config.dialogue = new Configuration.Dialogue();
			try (Stream<Path> paths = Files.walk(Paths.get(dialogueFolder))) {
				List<File> dialogueConfigs = paths.filter(Files::isRegularFile)
					.map(Path::toFile)
					.collect(Collectors.toList());
				
				dialogueConfigs.forEach((file) -> {
					try {
						config.dialogue.addEntries(gson.fromJson(
							new BufferedReader(
							  new FileReader(file)
							),
							config.dialogue.getClass()
						).get_mobList());
						System.out.println("Loaded dialogue config " + file.getName());
					} catch (FileNotFoundException e) {
						System.out.println("Unknown file not found exception for " + file.getName() + ": " + e);
						e.printStackTrace();
					}
				});

				System.out.println("Loaded " + dialogueConfigs.size() + " dialogue configs");
			}
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find dialogue configs! Make sure they're at location " + dialogueFolder);
		}
	}

	public void exportConfigs() throws IOException {
		System.out.println("Exporting configs");
		
		File generalConfig = new File(generalFp);
		if (!generalConfig.isFile()) {
			generalConfig.getParentFile().mkdirs();
			generalConfig.createNewFile();
			System.out.println("Created general config");
		}

		BufferedWriter generalWriter = new BufferedWriter(new FileWriter(generalConfig));
		gson.toJson(config.general, generalWriter);
		generalWriter.close();

		System.out.println("Exported general config");

		File dialogueConfig = new File(dialogueFolder, defaultDialogueConfigFp.replace(".json", "") 
		                                                + "-" 
		                                                + ZonedDateTime.now().format(
		                                                    DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")
		                                                )
		                                                + ".json");
		if (!dialogueConfig.isFile()) {
			dialogueConfig.getParentFile().mkdirs();
			dialogueConfig.createNewFile();
			System.out.println("Created export dialogue config");
		}

		BufferedWriter dialogueWriter = new BufferedWriter(new FileWriter(dialogueConfig));
		gson.toJson(config.dialogue, dialogueWriter);
		dialogueWriter.close();
		
		System.out.println("Exported dialogue config");
	}
	
	public void saveDefaultConfigs() throws IOException {
	    System.out.println("Saving default configs if they don't exist");
		
		File generalConfig = new File(generalFp);
		if (!generalConfig.isFile()) {
			generalConfig.getParentFile().mkdirs();
			generalConfig.createNewFile();
			System.out.println("Created default general config");
			
			BufferedWriter generalWriter = new BufferedWriter(new FileWriter(generalConfig));
		    gson.toJson(Configuration.DEFAULT.general, generalWriter);
		    generalWriter.close();
		    System.out.println("Saved default general config");
		}

		File dialogueF = new File(dialogueFolder);
		if (!dialogueF.isDirectory() || (dialogueF.isDirectory() && dialogueF.list().length == 0)) {
		    File dialogueConfig = new File(dialogueFolder, defaultDialogueConfigFp);
			dialogueConfig.getParentFile().mkdirs();
			dialogueConfig.createNewFile();
			System.out.println("Created default dialogue config");
			
			BufferedWriter dialogueWriter = new BufferedWriter(new FileWriter(dialogueConfig));
		    gson.toJson(Configuration.DEFAULT.dialogue, dialogueWriter);
		    dialogueWriter.close();
		    System.out.println("Saved default dialogue config");
		}
	}

	public String getConfigFolder() {
		return configFolder;
	}
}