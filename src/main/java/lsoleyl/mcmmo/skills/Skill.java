package lsoleyl.mcmmo.skills;

import java.util.Optional;

public enum Skill {
    // Gathering skills - currently disabled... as they could debalance gregtech.
    //                    They are hard to implement right to prevent abuse.
    //EXCAVATION, MINING, WOODCUTTING,
    // Utility Skills
    FIREFIGHTING, //CLIMBING,
    // Combat skills
    ARCHERY, AXES, COMBAT, SWORDS, UNARMED;


    @Override
    public String toString() {
        // Print each skill with a capital letter.
        return this.name().substring(0,1) + this.name().substring(1).toLowerCase();
    }

    public static Optional<Skill> getByName(String name) {
        String uppercaseName = name.toUpperCase();
        for(Skill skill : Skill.values()) {
            if (skill.name().equals(uppercaseName)) {
                return Optional.of(skill);
            }
        }

        return Optional.empty();
    }
}
