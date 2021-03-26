package democat.customdialogue.config.conditions;

import java.lang.reflect.Type;
import java.util.function.Predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

import democat.customdialogue.api.config.conditions.ICondition;
import democat.customdialogue.util.Utilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;;

public class ConditionHolding extends IConditionItemStack {
	public ConditionHolding(ItemStack stack, boolean testForNBT) {
		this(stack, testForNBT, true);
	}
	
    public ConditionHolding(ItemStack stack, boolean testForNBT, boolean exclusiveNBT) {
        this.stack = stack;
        this.testForNBT = testForNBT;
        this.exclusiveNBT = exclusiveNBT;
    }
    
    public ConditionHolding(Item item, int count, boolean testForNBT) {
    	this(item, count, testForNBT, true);
    }

    public ConditionHolding(Item item, int count, boolean testForNBT, boolean exclusiveNBT) {
        this(new ItemStack(item, count), testForNBT, exclusiveNBT);
    }
    
    public ConditionHolding(String resloc, int count, boolean testForNBT) {
    	this(resloc, count, testForNBT, true);
    }

    public ConditionHolding(String resloc, int count, boolean testForNBT, boolean exclusiveNBT) {
        this(Item.REGISTRY.getObject(new ResourceLocation(resloc)), count, testForNBT, exclusiveNBT);
    }

    @Override
    public String getName() {
        return "holding";
    }

    @Override
    public Predicate<EntityInteract> isSatisfied() {
    	return new Predicate<EntityInteract>() {
    		@Override
    		public boolean test(EntityInteract event) {
    			ItemStack temp = stack;
    			if (temp == null) temp = event.getEntityPlayer().getHeldItemMainhand();
		        if (testForNBT)
		            if (!Utilities.itemStackMatchWithNBT(temp, stack.serializeNBT(), exclusiveNBT))
		                return false;
		        return stack.isItemEqualIgnoreDurability(temp) && stack.getCount() == temp.getCount();
    		}
    	};
    }

    @Override
    public JsonElement serialize(ICondition src, Type typeOfSrc, JsonSerializationContext context) {
        return super.serialize(src, typeOfSrc, context);
    }

    @Override
    public ConditionHolding deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return (ConditionHolding) super.deserialize(json, typeOfT, context);
    }
}