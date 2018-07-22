package lsoleyl.mcmmo.skills;

public enum Skill {
    ACROBATICS, ARCHERY, AXES, EXCAVATION, MINING, SWORDS, UNARMED;

    @Override
    public String toString() {
        // Print each skill with a capital letter.
        return this.name().substring(0,1) + this.name().substring(1).toLowerCase();
    }
}
