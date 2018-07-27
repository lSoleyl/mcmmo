package lsoleyl.mcmmo.skills;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

/** This singleton class holds references to all skills
 */
public class SkillRegistry {
    private static SkillRegistry instance = new SkillRegistry();

    // Gathering skills (currently not active)
    //public final MiningSkill MINING;

    // Utility skills
    public final FirefightingSkill FIREFIGHTING = new FirefightingSkill();


    // Combat skills
    public final ArcherySkill ARCHERY = new ArcherySkill();
    public final AxesSkill AXES = new AxesSkill();
    public final CombatSkill COMBAT = new CombatSkill();
    public final SwordsSkill SWORDS = new SwordsSkill();
    public final UnarmedSkill UNARMED = new UnarmedSkill();



    private Map<Skill, ISkill> skillMap = new HashMap<Skill, ISkill>();


    public SkillRegistry() {
        // Add all available skill objects to the skill lookup map
        skillMap.put(Skill.FIREFIGHTING, FIREFIGHTING);

        skillMap.put(Skill.ARCHERY, ARCHERY);
        skillMap.put(Skill.AXES, AXES);
        skillMap.put(Skill.COMBAT, COMBAT);
        skillMap.put(Skill.SWORDS, SWORDS);
        skillMap.put(Skill.UNARMED, UNARMED);
    }

    /** Retrieves a skill by it's associated enum value. This can be used for generic skill handling
     *  as the skill's concrete type isn't known.
     */
    public ISkill getSkill(Skill skill) {
        return skillMap.get(skill);
    }

    public static SkillRegistry getInstance() {
        return instance;
    }

    /** This validation should be performed when starting up to make sure, all skills have corresponding
     *  skill objects registered.
     */
    public void validate() {
        // Make sure, we have an object for each skill
        for(Skill skill : Skill.values()) {
            if (!skillMap.containsKey(skill)) {
                throw new RuntimeException("Skill " + skill + " has no corresponding Skill object registered in the SkillRegistry");
            }
        }
    }
}
