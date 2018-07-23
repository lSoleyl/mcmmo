package lsoleyl.mcmmo.skills;

import java.util.HashMap;
import java.util.Map;

/** This singleton class holds references to all skills
 */
public class SkillRegistry {
    private static SkillRegistry instance = new SkillRegistry();

    // Gathering skills (currently not active)
    //public final MiningSkill MINING;

    // Utility skills
    public final FirefightingSkill FIREFIGHTING;


    // Combat skills
    public final CombatSkill COMBAT;



    private Map<Skill, ISkill> skillMap = new HashMap<>();


    public SkillRegistry() {
        FIREFIGHTING = new FirefightingSkill();
        COMBAT = new CombatSkill();

        // Add all available skill objects to the skill lookup map
        skillMap.put(Skill.FIREFIGHTING, FIREFIGHTING);
        skillMap.put(Skill.COMBAT, COMBAT);
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
