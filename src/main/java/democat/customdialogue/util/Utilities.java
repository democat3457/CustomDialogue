package democat.customdialogue.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public class Utilities {
	public static String randomStringWithWeight(Map<String, Integer> strings) {
        if (strings.size() == 1) {
            return Math.random() <= strings.values().toArray(new Integer[1])[0]/100.0 ? strings.keySet().toArray(new String[1])[0] : "";
        }

        int sumChance = 0;
        for (int weight : strings.values())
            sumChance += weight;
        if (sumChance < 100 && !strings.containsKey(""))
            strings.put("", 100 - sumChance);
        int r = (int) (Math.random() * (sumChance + 1));
        int countChance = 0;
        for (String key : strings.keySet()) {
            countChance += strings.get(key);
            if (countChance >= r)
                return key;
        }
        return "Default message";
    }

    public static ITextComponent tcColor(String str, TextFormatting color, Formats... _formats) {
        List<Formats> formats = Arrays.asList(_formats);
        ITextComponent result = new TextComponentString(str)
            .setStyle(
                new Style()
                    .setColor(color)
            );
        if (formats.contains(Formats.BOLD))
            result.getStyle().setBold(true);
        if (formats.contains(Formats.ITALIC))
            result.getStyle().setItalic(true);
        if (formats.contains(Formats.OBFUSCATED))
            result.getStyle().setObfuscated(true);
        if (formats.contains(Formats.STRIKETHROUGH))
            result.getStyle().setStrikethrough(true);
        if (formats.contains(Formats.UNDERLINE))
            result.getStyle().setUnderlined(true);
        return result;
    }

    public static ITextComponent tcLinkFile(String str, TextFormatting format, String fp) {
        return new TextComponentString(str)
            .setStyle(
                new Style()
                    .setColor(format)
                    .setClickEvent(
                        new ClickEvent(ClickEvent.Action.OPEN_FILE, fp)
                    )
            );
    }

    public static JsonObject merge(JsonObject one, JsonObject two) {
        JsonObject result = one;
        two.entrySet().forEach((entry) -> {
            result.add(entry.getKey(), entry.getValue());
        });
        return result;
    }

    public static JsonArray merge(JsonArray one, JsonArray two) {
        JsonArray result = one;
        result.addAll(two);
        return result;
    }

    public static <T> JsonArray listToJson(List<T> list, JsonSerializationContext context) {
        JsonArray result = new JsonArray();
        list.forEach((item) -> {
            result.add(context.serialize(item));
        });
        return result;
    }

    public static <T> List<T> jsonToList(JsonArray json, Class<T> cls, JsonDeserializationContext context) {
        List<T> result = Arrays.asList();
        json.forEach((item) -> {
            result.add(context.deserialize(item, cls));
        });
        return result;
    }

    public static boolean itemStackMatchWithNBT(ItemStack stack, NBTTagCompound nbt, boolean exclusive) {
        if (exclusive) {
            ItemStack copy = stack.copy();
            copy.setTagCompound(nbt);
            return ItemStack.areItemStackTagsEqual(stack, copy);
        } else {
            NBTTagCompound stackNbt = stack.getTagCompound();
            return nbtContains(stackNbt, nbt);
        }
    }

    public static boolean nbtContains(NBTTagCompound nbt, NBTTagCompound toCheck) {
        if (toCheck.hasNoTags())
            return true;
        if (nbt.hasNoTags())
            return false;
        Iterator<String> it = toCheck.getKeySet().iterator();
        boolean recursiveFlag = true;
        while (it.hasNext()) {
            String str = it.next();
            if (!nbt.hasKey(str))
                return false;
            if (nbt.getTag(str).getId() != toCheck.getTag(str).getId())
                return false;
            if (nbt.hasKey(str, nbt.getId())) {
                recursiveFlag = recursiveFlag && nbtContains(nbt.getCompoundTag(str), toCheck.getCompoundTag(str));
                if (!recursiveFlag)
                    return false;
            }
            if (!nbt.getString(str).equals(toCheck.getString(str)))
                return false;
            if (!nbt.getTag(str).equals(toCheck.getTag(str)))
                return false;
        }
        return true;
    }
}