package democat.customdialogue.api.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class SerializerItemStack implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String resloc = json.getAsJsonObject().get("item").getAsString();
        int count = 1;
        Item item = Item.REGISTRY.getObject(new ResourceLocation(resloc));
        ItemStack result;
        if (item.getHasSubtypes() && json.getAsJsonObject().has("meta")) {
            NonNullList<ItemStack> items = NonNullList.create();
            item.getSubItems(item.getCreativeTab(), items);
            result = items.get(json.getAsJsonObject().get("meta").getAsInt());
        } else
            result = new ItemStack(item);
        if (json.getAsJsonObject().has("count"))
            count = json.getAsJsonObject().get("count").getAsInt();
        result.setCount(count);

        if (json.getAsJsonObject().has("nbt")) {
            String nbtString = json.getAsJsonObject().get("nbt").getAsString();
            try {
                result.deserializeNBT(JsonToNBT.getTagFromJson(nbtString));
            } catch (NBTException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject s = new JsonObject();
        s.addProperty("item", src.getItem().getRegistryName().toString());
        if (src.getHasSubtypes())
            s.addProperty("meta", src.getMetadata());
        if (src.isStackable())
            s.addProperty("count", src.getCount());
        if (src.hasTagCompound())
            s.addProperty("nbt", src.serializeNBT().toString());
        return s;
    }
}