package lsoleyl.mcmmo.skills;

import java.util.HashMap;
import java.util.Map;

/** This singleton class holds references to all skills
 */
public class SkillRegistry {
    private static SkillRegistry instance = new SkillRegistry();

    public final MiningSkill MINING;
    private Map<Skill, ISkill> skillMap = new HashMap<>();


    public SkillRegistry() {
        MINING = new MiningSkill();

        // Add all available skill objects to the skill lookup map
        skillMap.put(Skill.MINING, MINING);
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
