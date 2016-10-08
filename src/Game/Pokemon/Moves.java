package Game.Pokemon;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.AttributeSet;
import java.util.HashSet;

public final class Moves extends Activity {
    private static final String MOVE_LIST = "genOneMoveList";
    private static Editor editor;

    Moves(Context context){
        initialize(context);
    }

    Moves(Context context, AttributeSet attribs){
        initialize(context);
    }

    Moves(Context context, AttributeSet attribs, int defStyle){
        initialize(context);
    }

    void initialize(Context context){
        editor = context.getSharedPreferences(MOVE_LIST, Context.MODE_PRIVATE).edit();

        //{"#", "Name", "Type", "Category", "PQ", "Power", "Accuracy", Effect} // NOTE2SELF
        
        /* Accuracy from most accurate to least accurate is as follows: 
         * ONSCREEN -> TRACKING -> TARGET -> DEBUFF -> CONDITIONAL 
         * Increase or decrease in accuracy translates & converts up or down, 
         * respectively, according to the scale */
        
        /* Evasiveness affects the dodge timer. Dodge timer length is 5 secs */
        loadDetails(new String[]{"1", "Pound", "Normal", "Physical", "1", "40", "TRACKING", "null"}); //
        loadDetails(new String[]{"2", "Karate Chop", "Fighting", "Physical", "1", "50", "TRACKING", "High critical hit ratio."}); //
        loadDetails(new String[]{"3", "Double Slap", "Normal", "Physical", "1", "15", "TARGET", "Hits 2-5 times."}); //
        loadDetails(new String[]{"4", "Comet Punch", "Normal", "Physical", "1", "18", "TARGET", "Hits 2-5 times."}); //
        loadDetails(new String[]{"5", "Mega Punch", "Normal", "Physical", "2", "80", "TARGET", "null"}); //
        loadDetails(new String[]{"6", "Pay Day", "Normal", "Physical", "1", "40", "TARGET", "A small chance to earn an item after the battle resolves."}); //Increase travel speed
        loadDetails(new String[]{"7", "Fire Punch", "Fire", "Physical", "2", "75", "TRACKING", "May burn opponent."}); //
        loadDetails(new String[]{"8", "Ice Punch", "Ice", "Physical", "2", "75", "TRACKING", "May freeze opponent."}); //
        loadDetails(new String[]{"9", "Thunder Punch", "Electric", "Physical", "2", "75", "TRACKING", "May paralyze opponent."}); //
        loadDetails(new String[]{"10", "Scratch", "Normal", "Physical", "1", "40", "TRACKING", "null"}); //
        loadDetails(new String[]{"11", "Vice Grip", "Normal", "Physical", "1", "55", "TRACKING", "null"}); //
        loadDetails(new String[]{"12", "Guillotine", "Normal", "Physical", "2", "null", "CONDITIONAL", "One-Hit-KO, if it hits."}); //Does not affect opponents faster than the user AND failS against a target Pokémon at least 30 levels above the user.
        loadDetails(new String[]{"13", "Razor Wind", "Normal", "Special", "2", "80", "ONSCREEN", "Slowed action time before attack. High critical hit ratio."}); //
        loadDetails(new String[]{"14", "Swords Dance", "Normal", "Status", "4", "null", "null", "Sharply raises user's Attack."}); //
        loadDetails(new String[]{"15", "Cut", "Normal", "Physical", "1", "50", "TRACKING", "null"}); //
        loadDetails(new String[]{"16", "Gust", "Flying", "Special", "2", "40", "ONSCREEN", "Hits Pokémon using Fly/Bounce with double power. Misses Pokémon using Dig/Dive."}); //
        loadDetails(new String[]{"17", "Wing Attack", "Flying", "Physical", "1", "60", "TRACKING", "null"}); //
        loadDetails(new String[]{"18", "Whirlwind", "Normal", "Status", "4", "null", "ONSCREEN", "In trainer battles, the opponent switches. In random battles, the battle ends."}); //
        loadDetails(new String[]{"19", "Fly", "Flying", "Physical", "2", "90", "TRACKING", "Flies up, attacks. Slowed action time"}); //
        loadDetails(new String[]{"20", "Bind", "Normal", "Physical", "1", "15", "TARGET", "Traps opponent, damaging them for some time."}); //Damages 4-5 avg turns
        loadDetails(new String[]{"21", "Slam", "Normal", "Physical", "2", "80", "TARGET", "null"}); //
        loadDetails(new String[]{"22", "Vine Whip", "Grass", "Physical", "1", "45", "TRACKING", "null"}); //
        loadDetails(new String[]{"23", "Stomp", "Normal", "Physical", "1", "65", "TRACKING", "May cancel opponent's action entirely."}); //
        loadDetails(new String[]{"24", "Double Kick", "Fighting", "Physical", "1", "30", "TRACKING", "Hits twice."}); //
        loadDetails(new String[]{"25", "Mega Kick", "Normal", "Physical", "2", "120", "TARGET", "null"}); //
        loadDetails(new String[]{"26", "Jump Kick", "Fighting", "Physical", "2", "100", "TARGET", "If it misses, the user loses half their HP"}); //Increase travel speed
        loadDetails(new String[]{"27", "Rolling Kick", "Fighting", "Physical", "1", "60", "TARGET", "May cancel opponent's action entirely."}); //
        loadDetails(new String[]{"28", "Sand Attack", "Ground", "Status", "3", "null", "ONSCREEN", "Lowers opponent's Accuracy."}); //
        loadDetails(new String[]{"29", "Headbutt", "Normal", "Physical", "2", "70", "TRACKING", "May cancel opponent's action entirely."}); //
        loadDetails(new String[]{"30", "Horn Attack", "Normal", "Physical", "1", "65", "TRACKING", "null"}); //
        loadDetails(new String[]{"31", "Fury Attack", "Normal", "Physical", "1", "15", "TARGET", "Hits 2-5 times."}); //
        loadDetails(new String[]{"32", "Horn Drill", "Normal", "Physical", "2", "null", "CONDITIONAL", "One-Hit-KO, if it hits."}); //Does not affect opponents faster than the user AND fails against a target Pokémon at least 30 levels above the user.
        loadDetails(new String[]{"33", "Tackle", "Normal", "Physical", "1", "50", "TRACKING", "null"}); //
        loadDetails(new String[]{"34", "Body Slam", "Normal", "Physical", "2", "85", "TRACKING", "May paralyze opponent."}); //
        loadDetails(new String[]{"35", "Wrap", "Normal", "Physical", "1", "15", "TRACKING", "Traps opponent, damaging them for some time."}); //Damages 4-5 avg turns
        loadDetails(new String[]{"36", "Take Down", "Normal", "Physical", "2", "90", "TARGET", "User receives recoil damage."}); //
        loadDetails(new String[]{"37", "Thrash", "Normal", "Physical", "2", "120", "TRACKING", "User's action is set for 2-3 turns but then becomes confused."}); //
        loadDetails(new String[]{"38", "Double-Edge", "Normal", "Physical", "2", "120", "TRACKING", "User receives recoil damage."}); //
        loadDetails(new String[]{"39", "Tail Whip", "Normal", "Status", "1", "null", "TRACKING", "Lowers opponent's Defense."}); //
        loadDetails(new String[]{"40", "Poison Sting", "Poison", "Physical", "1", "15", "TRACKING", "May poison the opponent."}); //
        loadDetails(new String[]{"41", "Twineedle", "Bug", "Physical", "1", "25", "TRACKING", "Hits twice. May poison opponent."}); //
        loadDetails(new String[]{"42", "Pin Missile", "Bug", "Physical", "1", "25", "TRACKING", "Hits 2-5 times."}); //
        loadDetails(new String[]{"43", "Leer", "Normal", "Status", "3", "null", "ONSCREEN", "Lowers opponent's Defense."}); //
        loadDetails(new String[]{"44", "Bite", "Dark", "Physical", "1", "60", "TRACKING", "May cancel opponent's action entirely."}); //
        loadDetails(new String[]{"45", "Growl", "Normal", "Status", "3", "null", "ONSCREEN", "Lowers opponent's Attack."}); //
        loadDetails(new String[]{"46", "Roar", "Normal", "Status", "4", "null", "ONSCREEN", "In trainer battles, the opponent switches. In random battles, the battle ends."}); //
        loadDetails(new String[]{"47", "Sing", "Normal", "Status", "4", "null", "DEBUFF", "Puts opponent to sleep."}); //Decreased action speed, opponent MUST be in initial spot
        loadDetails(new String[]{"48", "Supersonic", "Normal", "Status", "3", "null", "DEBUFF", "Confuses opponent."}); //Decreased action speed, opponent MUST be in initial spot
        loadDetails(new String[]{"49", "Sonic Boom", "Normal", "Special", "1", "20", "ONSCREEN", "Always inflicts 20 HP."}); //
        loadDetails(new String[]{"50", "Disable", "Normal", "Status", "1", "null", "ONSCREEN", "Opponent can't use its last attack for sometime."}); //
        loadDetails(new String[]{"51", "Acid", "Poison", "Special", "1", "40", "TARGET", "May lower opponent's Special Defense."}); //Increase travel speed
        loadDetails(new String[]{"52", "Ember", "Fire", "Special", "1", "40", "TARGET", "May burn opponent."}); //Increase travel speed
        loadDetails(new String[]{"53", "Flamethrower", "Fire", "Special", "2", "90", "TARGET", "May burn opponent."}); //Increase travel speed
        loadDetails(new String[]{"54", "Mist", "Ice", "Status", "3", "null", "null", "User's stats cannot be changed for a period of time."}); //
        loadDetails(new String[]{"55", "Water Gun", "Water", "Special", "1", "40", "TARGET", "null"}); //Increase travel speed
        loadDetails(new String[]{"56", "Hydro Pump", "Water", "Special", "2", "110", "TARGET", "null"}); //Increase travel speed
        loadDetails(new String[]{"57", "Surf", "Water", "Special", "2", "90", "ONSCREEN", "Hits all adjacent Pokémon."}); //
        loadDetails(new String[]{"58", "Ice Beam", "Ice", "Special", "2", "90", "TARGET", "May freeze opponent."}); //Increase travel speed
        loadDetails(new String[]{"59", "Blizzard", "Ice", "Special", "2", "110", "DEBUFF", "May freeze opponent."}); //Decreased action speed, opponent MUST be in initial spot
        loadDetails(new String[]{"60", "Psybeam", "Psychic", "Special", "2", "65", "TARGET", "May confuse opponent."}); //Increase travel speed
        loadDetails(new String[]{"61", "Bubble Beam", "Water", "Special", "2", "65", "TARGET", "May lower opponent's Speed."}); //Increase travel speed
        loadDetails(new String[]{"62", "Aurora Beam", "Ice", "Special", "2", "65", "TARGET", "May lower opponent's Attack."}); //Increase travel speed
        loadDetails(new String[]{"63", "Hyper Beam", "Normal", "Special", "2", "150", "TARGET", "User must take time to recharge."}); //Increase travel speed
        loadDetails(new String[]{"64", "Peck", "Flying", "Physical", "1", "35", "TRACKING", "null"}); //
        loadDetails(new String[]{"65", "Drill Peck", "Flying", "Physical", "2", "80", "TRACKING", "null"}); //
        loadDetails(new String[]{"66", "Submission", "Fighting", "Physical", "2", "80", "TARGET", "User receives recoil damage."}); //
        loadDetails(new String[]{"67", "Low Kick", "Fighting", "Physical", "1", "null", "TRACKING", "The heavier the opponent, the stronger the attack."}); //
        loadDetails(new String[]{"68", "Counter", "Fighting", "Physical", "2", "null", "TRACKING", "When hit by a Physical Attack, user strikes back with 2x power."}); //
        loadDetails(new String[]{"69", "Seismic Toss", "Fighting", "Physical", "1", "null", "TRACKING", "Inflicts damage equal to user's level."}); //
        loadDetails(new String[]{"70", "Strength", "Normal", "Physical", "2", "80", "TRACKING", "null"}); //
        loadDetails(new String[]{"71", "Absorb", "Grass", "Special", "1", "20", "TRACKING", "User recovers half the HP inflicted on opponent."}); //
        loadDetails(new String[]{"72", "Mega Drain", "Grass", "Special", "1", "40", "TRACKING", "User recovers half the HP inflicted on opponent."}); //
        loadDetails(new String[]{"73", "Leech Seed", "Grass", "Status", "2", "null", "TARGET", "User steals HP from opponent each turn."}); //Increase travel speed
        loadDetails(new String[]{"74", "Growth", "Normal", "Status", "4", "null", "null", "Raises user's Attack and Special Attack."}); //
        loadDetails(new String[]{"75", "Razor Leaf", "Grass", "Physical", "1", "55", "TARGET", "High critical hit ratio."}); //Increase travel speed
        loadDetails(new String[]{"76", "Solar Beam", "Grass", "Special", "2", "120", "TARGET", "Charges first, then attacks."}); //Increase travel speed. Slow action time
        loadDetails(new String[]{"77", "Poison Powder", "Poison", "Status", "2", "null", "TARGET", "Poisons opponent."}); //Increase travel speed
        loadDetails(new String[]{"78", "Stun Spore", "Grass", "Status", "2", "null", "TARGET", "Paralyzes opponent."}); //Increase travel speed
        loadDetails(new String[]{"79", "Sleep Powder", "Grass", "Status", "2", "null", "TARGET", "Puts opponent to sleep."}); //Increase travel speed
        loadDetails(new String[]{"80", "Petal Dance", "Grass", "Special", "2", "120", "ONSCREEN", "User's action is set for 2-3 turns but then becomes confused."}); //
        loadDetails(new String[]{"81", "String Shot", "Bug", "Status", "1", "null", "TARGET", "Sharply lowers opponent's Speed."}); //Increase travel speed
        loadDetails(new String[]{"82", "Dragon Rage", "Dragon", "Special", "1", "40", "TARGET", "Always inflicts 40 HP."}); //Increase travel speed
        loadDetails(new String[]{"83", "Fire Spin", "Fire", "Special", "1", "35", "TARGET", "Traps opponent, damaging them for some time."}); //Increase travel speed. Damages 4-5 avg turns
        loadDetails(new String[]{"84", "Thunder Shock", "Electric", "Special", "1", "40", "TRACKING", "May paralyze opponent."}); //
        loadDetails(new String[]{"85", "Thunderbolt", "Electric", "Special", "2", "90", "TRACKING", "May paralyze opponent."}); //
        loadDetails(new String[]{"86", "Thunder Wave", "Electric", "Status", "2", "null", "ONSCREEN", "Paralyzes opponent."}); //
        loadDetails(new String[]{"87", "Thunder", "Electric", "Special", "2", "110", "DEBUFF", "May paralyze opponent."}); //Decreased action speed, opponent MUST be in initial spot
        loadDetails(new String[]{"88", "Rock Throw", "Rock", "Physical", "1", "50", "TARGET", "null"}); //Increase travel speed
        loadDetails(new String[]{"89", "Earthquake", "Ground", "Physical", "2", "100", "ONSCREEN", "Power is doubled if opponent is underground from using Dig."}); //
        loadDetails(new String[]{"90", "Fissure", "Ground", "Physical", "2", "null", "CONDITIONAL", "One-Hit-KO, if it hits."}); //Can hit Pokémon of any level, but does not affect Pokémon faster than the user.
        loadDetails(new String[]{"91", "Dig", "Ground", "Physical", "2", "80", "TRACKING", "Digs underground, then attacks."}); //
        loadDetails(new String[]{"92", "Toxic", "Poison", "Status", "2", "null", "TARGET", "Badly poisons opponent."}); //Increase travel speed
        loadDetails(new String[]{"93", "Confusion", "Psychic", "Special", "1", "50", "ONSCREEN", "May confuse opponent."}); //
        loadDetails(new String[]{"94", "Psychic", "Psychic", "Special", "2", "90", "ONSCREEN", "May lower opponent's Special Defense."}); //
        loadDetails(new String[]{"95", "Hypnosis", "Psychic", "Status", "2", "null", "DEBUFF", "Puts opponent to sleep."}); //Decreased action speed, opponent MUST be in initial spot
        loadDetails(new String[]{"96", "Meditate", "Psychic", "Status", "3", "null", "null", "Raises user's Attack."}); //
        loadDetails(new String[]{"97", "Agility", "Psychic", "Status", "4", "null", "null", "Sharply raises user's Speed."}); //
        loadDetails(new String[]{"98", "Quick Attack", "Normal", "Physical", "1", "40", "TRACKING", "User attacks fast."}); //
        loadDetails(new String[]{"99", "Rage", "Normal", "Physical", "1", "20", "TRACKING", "Raises user's Attack when hit."}); //
        loadDetails(new String[]{"100", "Teleport", "Psychic", "Status", "3", "null", "null", "Ends random battles; also dodges attacks during trainer battles. Cannot use consecutively. Lowers evasiveness"}); //Slows users dodge time, permanently
        loadDetails(new String[]{"101", "Night Shade", "Ghost", "Special", "1", "null", "ONSCREEN", "Inflicts damage equal to user's level."}); //
        loadDetails(new String[]{"102", "Mimic", "Normal", "Status", "3", "null", "null", "Copies the opponent's last move."}); //
        loadDetails(new String[]{"103", "Screech", "Normal", "Status", "4", "null", "TARGET", "Sharply lowers opponent's Defense."}); //
        loadDetails(new String[]{"104", "Double Team", "Normal", "Status", "4", "null", "null", "Raises user's Evasiveness."}); //
        loadDetails(new String[]{"105", "Recover", "Normal", "Status", "4", "null", "null", "User recovers half its max HP."}); //
        loadDetails(new String[]{"106", "Harden", "Normal", "Status", "3", "null", "null", "Raises user's Defense."}); //
        loadDetails(new String[]{"107", "Minimize", "Normal", "Status", "4", "null", "null", "Sharply raises user's Evasiveness."}); //
        loadDetails(new String[]{"108", "Smokescreen", "Normal", "Status", "2", "null", "ONSCREEN", "Lowers opponent's Accuracy."}); //
        loadDetails(new String[]{"109", "Confuse Ray", "Ghost", "Status", "2", "null", "TRACKING", "Confuses opponent."}); //
        loadDetails(new String[]{"110", "Withdraw", "Water", "Status", "3", "null", "null", "Raises user's Defense."}); //
        loadDetails(new String[]{"111", "Defense Curl", "Normal", "Status", "3", "null", "null", "Raises user's Defense."}); //
        loadDetails(new String[]{"112", "Barrier", "Psychic", "Status", "4", "null", "null", "Sharply raises user's Defense."}); //
        loadDetails(new String[]{"113", "Light Screen", "Psychic", "Status", "4", "null", "null", "Halves damage from Special attacks for some time."}); //Take 0.5 SpA damage for 5 avg turns
        loadDetails(new String[]{"114", "Haze", "Ice", "Status", "4", "null", "null", "Resets all stat changes."}); //
        loadDetails(new String[]{"115", "Reflect", "Psychic", "Status", "4", "null", "null", "Halves damage from Physical attacks for some time."}); //Take 0.5 Atk damage for 5 avg turns
        loadDetails(new String[]{"116", "Focus Energy", "Normal", "Status", "4", "null", "null", "Increases critical hit ratio."}); //
        loadDetails(new String[]{"117", "Bide", "Normal", "Physical", "4", "null", "TRACKING", "User takes damage for some time, then strikes back double."}); //Takes damage for 2 avg turns
        loadDetails(new String[]{"118", "Metronome", "Normal", "Status", "3", "null", "null", "User performs any move in the game at random."}); //
        loadDetails(new String[]{"119", "Mirror Move", "Flying", "Status", "3", "null", "null", "User performs the opponent's last move."}); //
        loadDetails(new String[]{"120", "Self-Destruct", "Normal", "Physical", "4", "200", "ONSCREEN", "User faints."}); //
        loadDetails(new String[]{"121", "Egg Bomb", "Normal", "Physical", "2", "100", "TARGET", "null"}); //Increase travel speed
        loadDetails(new String[]{"122", "Lick", "Ghost", "Physical", "1", "30", "TRACKING", "May paralyze opponent."}); //
        loadDetails(new String[]{"123", "Smog", "Poison", "Special", "1", "30", "DEBUFF", "May poison opponent."}); //Decreased action speed, opponent MUST be in initial spot
        loadDetails(new String[]{"124", "Sludge", "Poison", "Special", "2", "65", "TARGET", "May poison opponent."}); //Increase travel speed
        loadDetails(new String[]{"125", "Bone Club", "Ground", "Physical", "1", "65", "TARGET", "May cancel opponent's action entirely."}); //
        loadDetails(new String[]{"126", "Fire Blast", "Fire", "Special", "2", "110", "TARGET", "null"}); //
        loadDetails(new String[]{"127", "Waterfall", "Water", "Physical", "2", "80", "ONSCREEN", "May cancel opponent's action entirely."}); //
        loadDetails(new String[]{"128", "Clamp", "Water", "Physical", "1", "35", "TARGET", "Traps opponent, damaging them for 4-5 turns."}); //
        loadDetails(new String[]{"129", "Swift", "Normal", "Special", "1", "60", "ONSCREEN", "Ignores Accuracy and Evasiveness."}); //
        loadDetails(new String[]{"130", "Skull Bash", "Normal", "Physical", "2", "130", "TRACKING", "Raises Defense on first turn, attacks on second."}); //
        loadDetails(new String[]{"131", "Spike Cannon", "Normal", "Physical", "1", "20", "TARGET", "Hits 2-5 times in one turn."}); //Increase travel speed
        loadDetails(new String[]{"132", "Constrict", "Normal", "Physical", "1", "10", "TRACKING", "May lower opponent's Speed by one stage."}); //
        loadDetails(new String[]{"133", "Amnesia", "Psychic", "Status", "4", "null", "null", "Sharply raises user's Special Defense."}); //
        loadDetails(new String[]{"134", "Kinesis", "Psychic", "Status", "1", "null", "ONSCREEN", "Lowers opponent's Accuracy."}); //
        loadDetails(new String[]{"135", "Soft-Boiled", "Normal", "Status", "4", "null", "null", "User recovers half its max HP."}); //
        loadDetails(new String[]{"136", "High Jump Kick", "Fighting", "Physical", "2", "130", "TARGET", "If it misses, the user loses half their HP."}); //Increase travel speed
        loadDetails(new String[]{"137", "Glare", "Normal", "Status", "2", "null", "ONSCREEN", "Paralyzes opponent."}); //
        loadDetails(new String[]{"138", "Dream Eater", "Psychic", "Special", "2", "100", "ONSCREEN", "User recovers half the HP inflicted on a sleeping opponent."}); //
        loadDetails(new String[]{"139", "Poison Gas", "Poison", "2", "40", "null", "ONSCREEN", "Poisons opponent."}); //
        loadDetails(new String[]{"140", "Barrage", "Normal", "Physical", "1", "15", "TARGET", "Hits 2-5 times."}); //Increase travel speed
        loadDetails(new String[]{"141", "Leech Life", "Bug", "Physical", "1", "20", "TRACKING", "User recovers half the HP inflicted on opponent."}); //
        loadDetails(new String[]{"142", "Lovely Kiss", "Normal", "Status", "1", "null", "TARGET", "Puts opponent to sleep."}); //
        loadDetails(new String[]{"143", "Sky Attack", "Flying", "Physical", "2", "140", "TRACKING", "Charges first, then attacks. May cancel opponent's action entirely."}); //Slowed action time
        loadDetails(new String[]{"144", "Transform", "Normal", "Status", "3", "null", "null", "User takes on the form and attacks of the opponent."}); //
        loadDetails(new String[]{"145", "Bubble", "Water", "Special", "2", "40", "TRACKING", "May lower opponent's Speed."}); //
        loadDetails(new String[]{"146", "Dizzy Punch", "Normal", "Physical", "2", "70", "TRACKING", "May confuse opponent."}); //
        loadDetails(new String[]{"147", "Spore", "Grass", "Status", "2", "null", "TARGET", "Puts opponent to sleep."}); //Increase travel speed
        loadDetails(new String[]{"148", "Flash", "Normal", "Status", "3", "null", "ONSCREEN", "Lowers opponent's Accuracy."}); //
        loadDetails(new String[]{"149", "Psywave", "Psychic", "Special", "2", "null", "TARGET", "Inflicts damage 50-150% of user's level."}); //Increase travel speed
        loadDetails(new String[]{"150", "Splash", "Normal", "Status", "3", "null", "null", "Doesn't do ANYTHING."}); //
        loadDetails(new String[]{"151", "Acid Armor", "Poison", "Status", "4", "null", "null", "Sharply raises user's Defense."}); //
        loadDetails(new String[]{"152", "Crabhammer", "Water", "Physical", "2", "100", "TRACKING", "High critical hit ratio."}); //
        loadDetails(new String[]{"153", "Explosion", "Normal", "Physical", "4", "250", "ONSCREEN", "User faints."}); //
        loadDetails(new String[]{"154", "Fury Swipes", "Normal", "Physical", "1", "18", "TARGET", "Hits 2-5 times."}); //
        loadDetails(new String[]{"155", "Bonemerang", "Ground", "Physical", "1", "50", "TRACKING", "Hits twice."}); //
        loadDetails(new String[]{"156", "Rest", "Psychic", "Status", "4", "null", "null", "User sleeps for some time, but user is fully healed."}); //Sleeps for 2 avg turns
        loadDetails(new String[]{"157", "Rock Slide", "Rock", "Physical", "2", "75", "TRACKING", "May cancel opponent's action entirely."}); //
        loadDetails(new String[]{"158", "Hyper Fang", "Normal", "Physical", "2", "80", "TRACKING", "May cancel opponent's action entirely."}); //
        loadDetails(new String[]{"159", "Sharpen", "Normal", "Status", "3", "null", "null", "Raises user's Attack."}); //
        loadDetails(new String[]{"160", "Conversion", "Normal", "Status", "3", "null", "null", "Changes user's type to that of its first move."}); //
        loadDetails(new String[]{"161", "Tri Attack", "Normal", "Special", "2", "80", "TARGET", "May paralyze, burn or freeze opponent."}); //Increase travel speed
        loadDetails(new String[]{"162", "Super Fang", "Normal", "Physical", "1", "null", "TRACKING", "Always takes off half of the opponent's HP."}); //
        loadDetails(new String[]{"163", "Slash", "Normal", "Physical", "2", "70", "TRACKING", "High critical hit ratio."}); //
        loadDetails(new String[]{"164", "Substitute", "Normal", "Status", "4", "null", "null", "Uses HP to creates a decoy that takes hits."}); //
        loadDetails(new String[]{"165", "Struggle", "Normal", "Physical", "null", "50", "null", "Hurts the user."}); //
        editor.putBoolean("setState", true);
        editor.commit();
    }

    private void loadDetails(String[] set){
        HashSet values = new HashSet();

        for(int i = 0; i < set.length; i++) values.add(i + "_" + set[i]);

        editor.putStringSet(set[0], values);
    }
}