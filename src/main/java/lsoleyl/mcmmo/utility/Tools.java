package lsoleyl.mcmmo.utility;

import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

/** This class is used to reliably categorize tools.
 *  Sadly Gregtech tools are not derived from the traditional item classes and can only be categorized by their meta data,
 *  which is stored as item damage in the itemstack
 */
public class Tools {
    public static final String GT_TOOL_CLASS = "GT_MetaGenerated_Tool_01";

    public static boolean isAxe(ItemStack stack) {
        if (stack.getItem() instanceof ItemAxe)
            return true;

        if (stack.getItem().getClass().getSimpleName().equals(GT_TOOL_CLASS)) {
            // It is a gregtech tool... check what kind of tool
            return stack.getItemDamage() == GregTechMeta.AXE;
        }

        return false;
    }

    public static boolean isSword(ItemStack stack) {
        if (stack.getItem() instanceof ItemSword)
            return true;

        if (stack.getItem().getClass().getSimpleName().equals(GT_TOOL_CLASS)) {
            // It is a gregtech tool... check what kind of tool
            return stack.getItemDamage() == GregTechMeta.SWORD;
        }

        return false;
    }

}

/** List of Gregtech meta ids (extracted from GT_MetaGenerated_Tool_01.class)
 */
class GregTechMeta {
    public static final short SWORD = 0;
    public static final short AXE = 6;
}
