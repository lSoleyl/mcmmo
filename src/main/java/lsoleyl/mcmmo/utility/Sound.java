package lsoleyl.mcmmo.utility;

import net.minecraft.entity.player.EntityPlayer;

// from: https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/mapping-and-modding-tutorials/1571574-all-minecraft-playsound-file-names
// list is still incomplete
public enum Sound {
    NOTE_BASS("note.bass"),
    NOTE_BD("note.bd"),
    NOTE_HARP("note.harp"),
    NOTE_HAT("note.hat"),
    NOTE_PLING("note.pling"),
    NOTE_SNARE("note.snare"),

    HORSE_ARMOR("mob.horse.armor"),
    HORSE_JUMP("mob.horse.jump"),
    HORSE_LAND("mob.horse.land"),
    HORSE_LEATHER("mob.horse.leather"),

    PORTAL("portal.portal"),
    PORTAL_TRAVEL("portal.travel"),
    PORTAL_TRIGGER("portal.trigger"),


    ANVIL_BREAK("random.anvil_break"),
    ANVIL_LAND("random.anvil_land"),
    ANVIL_USE("random.anvil_use"),
    BOW("random.bow"),
    BOW_HIT("random.bowhit"),
    BREAK("random.break"),
    BURP("random.burp"),
    CHEST_CLOSED("random.chestclosed"),
    CHEST_OPEN("random.chestopen"),
    CLICK("random.click"),
    DOOR_CLOSE("random.door_close"),
    DOOR_OPEN("random.door_open"),
    DRINK("random.drink"),
    EAT("random.eat"),
    EXPLODE("random.explode"),
    FIZZ("random.fizz"),
    LEVEL_UP("random.levelup"),
    ORB("random.orb"),
    POP("random.pop"),
    WOOD_CLICK("random.wood_click");



    private final String soundId;
    Sound(String soundId) {
        this.soundId = soundId;
    }


    @Override
    public String toString() {
        return soundId;
    }

    public void playAt(EntityPlayer player) {
        playAt(player, 1.0f, 1.0f);
    }

    public void playAt(EntityPlayer player, float volume) {
        playAt(player, volume,1.0f);
    }

    public void playAt(EntityPlayer player, float volume, float pitch) {
        playSoundAt(soundId, player, volume, pitch);
    }

    public static void playSoundAt(String soundId, EntityPlayer player) {
        playSoundAt(soundId, player, 1.0f, 1.0f);
    }

    public static void playSoundAt(String soundId, EntityPlayer player, float volume, float pitch) {
        player.getEntityWorld().playSoundEffect(player.posX+0.5, player.posY+0.5, player.posZ+0.5, soundId, volume, pitch);
    }
}
