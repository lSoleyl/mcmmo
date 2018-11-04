package lsoleyl.mcmmo.utility;

import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.HashSet;
import java.util.Set;

/** This class is used to reliably categorize tools. If items are not derived from the default classes (ItemAxe, ItemSword)
 *  a custom classifier may be defined to correctly classify the tool in question.
 */
public class Tools {
    // List of tool classifiers, which are used to support tools of other mods as well
    private static final ToolClassifier[] classifiers = { new GregTechTools(), new TinkerTools() };

    public static boolean isAxe(ItemStack stack) {
        if (stack.getItem() instanceof ItemAxe) {
            return true;
        }

        // Oh, how I would have loved to use HOF for this
        for (ToolClassifier classifier : classifiers) {
            if (classifier.isAxe(stack)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSword(ItemStack stack) {
        if (stack.getItem() instanceof ItemSword) {
            return true;
        }

        // Oh, how I would have loved to use HOF for this
        for (ToolClassifier classifier : classifiers) {
            if (classifier.isSword(stack)) {
                return true;
            }
        }

        return false;
    }

}

/** A generic interface for tool classification
 */
interface ToolClassifier {
    boolean isAxe(ItemStack stack);
    boolean isSword(ItemStack stack);
}

/** Gregtech tool classifier.
 *  Sadly Gregtech tools are not derived from the traditional item classes and can only be categorized by their meta data,
 *  which is stored as item damage in the itemstack
 */
class GregTechTools implements ToolClassifier {
    static final String TOOL_CLASS = "GT_MetaGenerated_Tool_01";

    /** List of Gregtech meta ids (extracted from GT_MetaGenerated_Tool_01.class)
     */
    static final short SWORD = 0;
    static final short AXE = 6;

    @Override
    public boolean isAxe(ItemStack stack) {
        if (stack.getItem().getClass().getSimpleName().equals(TOOL_CLASS)) {
            // It is a gregtech tool... check what kind of tool
            return stack.getItemDamage() == AXE;
        }

        return false;
    }

    @Override
    public boolean isSword(ItemStack stack) {
        if (stack.getItem().getClass().getSimpleName().equals(TOOL_CLASS)) {
            // It is a gregtech tool... check what kind of tool
            return stack.getItemDamage() == SWORD;
        }

        return false;
    }
}

/** Tinkers constructs tool classifier
 */
class TinkerTools implements ToolClassifier {
    static final String PACKAGE_NAME = "tconstruct.items.tools.";

    private final Set<String> swordSet;
    private final Set<String> axeSet;

    TinkerTools() {
        // Initialize the Swords and Axes class sets
        String[] swordNames = {"Broadsword", "Longsword", "Rapier", "Dagger"};
        String[] axeNames = {"Mattock", "LumberAxe", "Scythe", "Cleaver", "Battleaxe"};

        swordSet = new HashSet<String>();
        axeSet = new HashSet<String>();

        for(String name : swordNames) {
            swordSet.add(PACKAGE_NAME + name);
        }

        for(String name : axeNames) {
            axeSet.add(PACKAGE_NAME + name);
        }
    }


    @Override
    public boolean isAxe(ItemStack stack) {
        String className = stack.getItem().getClass().getCanonicalName();
        if (className.startsWith(PACKAGE_NAME)) {
            // A tinker tool, check which one
            return axeSet.contains(className);
        }

        return false;
    }

    @Override
    public boolean isSword(ItemStack stack) {
        String className = stack.getItem().getClass().getCanonicalName();
        if (className.startsWith(PACKAGE_NAME)) {
            // A tinker tool, check which one
            return swordSet.contains(className);
        }

        return false;
    }
}
