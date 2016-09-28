package Game.Pokemon;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.AttributeSet;
import java.util.HashSet;

public final class Moves extends Activity {
    private static final String MOVE_LIST = "genOneMoveList";
    private static Editor editor;

    Moves(Context context) {
        initialize(context);
    }

    Moves(Context context, AttributeSet attribs) {
        initialize(context);
    }

    Moves(Context context, AttributeSet attribs, int defStyle) {
        initialize(context);
    }

    void initialize(Context context) {
        editor = context.getSharedPreferences(MOVE_LIST, 0).edit();
        loadDetails(new String[]{"Absorb", "GRASS", "Special", "20", "100", "25"});
        loadDetails(new String[]{"Acid", "POISON", "Special", "40", "100", "30"});
        loadDetails(new String[]{"Acid Armor", "POISON", "Status", "", "", "20"});
        loadDetails(new String[]{"Agility", "PSYCHIC", "Status", "", "", "30"});
        loadDetails(new String[]{"Amnesia", "PSYCHIC", "Status", "", "", "20"});
        loadDetails(new String[]{"Aurora Beam", "ICE", "Special", "65", "100", "20"});
        loadDetails(new String[]{"Barrage", "NORMAL", "Physical", "15", "85", "20"});
        loadDetails(new String[]{"Barrier", "PSYCHIC", "Status", "", "", "20"});
        loadDetails(new String[]{"Bide", "NORMAL", "Physical", "", "", "10"});
        loadDetails(new String[]{"Bind", "NORMAL", "Physical", "15", "85", "20"});
        loadDetails(new String[]{"Bite", "DARK", "Physical", "60", "100", "25"});
        loadDetails(new String[]{"Blizzard", "ICE", "Special", "110", "70", "5"});
        loadDetails(new String[]{"Body Slam", "NORMAL", "Physical", "85", "100", "15"});
        loadDetails(new String[]{"Bone Club", "GROUND", "Physical", "65", "85", "20"});
        loadDetails(new String[]{"Bonemerang", "GROUND", "Physical", "50", "90", "10"});
        loadDetails(new String[]{"Bubble", "WATER", "Special", "40", "100", "30"});
        loadDetails(new String[]{"Bubble Beam", "WATER", "Special", "65", "100", "20"});
        loadDetails(new String[]{"Clamp", "WATER", "Physical", "35", "85", "10"});
        loadDetails(new String[]{"Comet Punch", "NORMAL", "Physical", "18", "85", "15"});
        loadDetails(new String[]{"Confuse Ray", "GHOST", "Status", "", "100", "10"});
        loadDetails(new String[]{"Confusion", "PSYCHIC", "Special", "50", "100", "25"});
        loadDetails(new String[]{"Constrict", "NORMAL", "Physical", "10", "100", "35"});
        loadDetails(new String[]{"Conversion", "NORMAL", "Status", "", "", "30"});
        loadDetails(new String[]{"Counter", "FIGHTING", "Physical", "", "100", "20"});
        loadDetails(new String[]{"Crabhammer", "WATER", "Physical", "100", "90", "10"});
        loadDetails(new String[]{"Cut", "NORMAL", "Physical", "50", "95", "30"});
        loadDetails(new String[]{"Defense Curl", "NORMAL", "Status", "", "", "40"});
        loadDetails(new String[]{"Dig", "GROUND", "Physical", "80", "100", "10"});
        loadDetails(new String[]{"Disable", "NORMAL", "Status", "", "100", "20"});
        loadDetails(new String[]{"Dizzy Punch", "NORMAL", "Physical", "70", "100", "10"});
        loadDetails(new String[]{"Double Kick", "FIGHTING", "Physical", "30", "100", "30"});
        loadDetails(new String[]{"Double Slap", "NORMAL", "Physical", "15", "85", "10"});
        loadDetails(new String[]{"Double Team", "NORMAL", "Status", "", "", "15"});
        loadDetails(new String[]{"Double-Edge", "NORMAL", "Physical", "120", "100", "15"});
        loadDetails(new String[]{"Dragon Rage", "DRAGON", "Special", "", "100", "10"});
        loadDetails(new String[]{"Dream Eater", "PSYCHIC", "Special", "100", "100", "15"});
        loadDetails(new String[]{"Drill Peck", "FLYING", "Physical", "80", "100", "20"});
        loadDetails(new String[]{"Earthquake", "GROUND", "Physical", "100", "100", "10"});
        loadDetails(new String[]{"Egg Bomb", "NORMAL", "Physical", "100", "75", "10"});
        loadDetails(new String[]{"Ember", "FIRE", "Special", "40", "100", "25"});
        loadDetails(new String[]{"Explosion", "NORMAL", "Physical", "250", "100", "5"});
        loadDetails(new String[]{"Fire Blast", "FIRE", "Special", "110", "85", "5"});
        loadDetails(new String[]{"Fire Punch", "FIRE", "Physical", "75", "100", "15"});
        loadDetails(new String[]{"Fire Spin", "FIRE", "Special", "35", "85", "15"});
        loadDetails(new String[]{"Fissure", "GROUND", "Physical", "", "", "5"});
        loadDetails(new String[]{"Flamethrower", "FIRE", "Special", "90", "100", "15"});
        loadDetails(new String[]{"Flash", "NORMAL", "Status", "", "100", "20"});
        loadDetails(new String[]{"Fly", "FLYING", "Physical", "90", "95", "15"});
        loadDetails(new String[]{"Focus Energy", "NORMAL", "Status", "", "", "30"});
        loadDetails(new String[]{"Fury Attack", "NORMAL", "Physical", "15", "85", "20"});
        loadDetails(new String[]{"Fury Swipes", "NORMAL", "Physical", "18", "80", "15"});
        loadDetails(new String[]{"Glare", "NORMAL", "Status", "", "100", "30"});
        loadDetails(new String[]{"Growl", "NORMAL", "Status", "", "100", "40"});
        loadDetails(new String[]{"Growth", "NORMAL", "Status", "", "", "40"});
        loadDetails(new String[]{"Guillotine", "NORMAL", "Physical", "", "", "5"});
        loadDetails(new String[]{"Gust", "FLYING", "Special", "40", "100", "35"});
        loadDetails(new String[]{"Harden", "NORMAL", "Status", "", "", "30"});
        loadDetails(new String[]{"Haze", "ICE", "Status", "", "", "30"});
        loadDetails(new String[]{"Headbutt", "NORMAL", "Physical", "70", "100", "15"});
        loadDetails(new String[]{"High Jump Kick", "FIGHTING", "Physical", "130", "90", "10"});
        loadDetails(new String[]{"Horn Attack", "NORMAL", "Physical", "65", "100", "25"});
        loadDetails(new String[]{"Horn Drill", "NORMAL", "Physical", "", "", "5"});
        loadDetails(new String[]{"Hydro Pump", "WATER", "Special", "110", "80", "5"});
        loadDetails(new String[]{"Hyper Beam", "NORMAL", "Special", "150", "90", "5"});
        loadDetails(new String[]{"Hyper Fang", "NORMAL", "Physical", "80", "90", "15"});
        loadDetails(new String[]{"Hypnosis", "PSYCHIC", "Status", "", "60", "20"});
        loadDetails(new String[]{"Ice Beam", "ICE", "Special", "90", "100", "10"});
        loadDetails(new String[]{"Ice Punch", "ICE", "Physical", "75", "100", "15"});
        loadDetails(new String[]{"Jump Kick", "FIGHTING", "Physical", "100", "95", "10"});
        loadDetails(new String[]{"Karate Chop", "FIGHTING", "Physical", "50", "100", "25"});
        loadDetails(new String[]{"Kinesis", "PSYCHIC", "Status", "", "80", "15"});
        loadDetails(new String[]{"Leech Life", "BUG", "Physical", "20", "100", "15"});
        loadDetails(new String[]{"Leech Seed", "GRASS", "Status", "", "90", "10"});
        loadDetails(new String[]{"Leer", "NORMAL", "Status", "", "100", "30"});
        loadDetails(new String[]{"Lick", "GHOST", "Physical", "30", "100", "30"});
        loadDetails(new String[]{"Light Screen", "PSYCHIC", "Status", "", "", "30"});
        loadDetails(new String[]{"Lovely Kiss", "NORMAL", "Status", "", "75", "10"});
        loadDetails(new String[]{"Low Kick", "FIGHTING", "Physical", "", "100", "20"});
        loadDetails(new String[]{"Meditate", "PSYCHIC", "Status", "", "", "40"});
        loadDetails(new String[]{"Mega Drain", "GRASS", "Special", "40", "100", "15"});
        loadDetails(new String[]{"Mega Kick", "NORMAL", "Physical", "120", "75", "5"});
        loadDetails(new String[]{"Mega Punch", "NORMAL", "Physical", "80", "85", "20"});
        loadDetails(new String[]{"Metronome", "NORMAL", "Status", "", "", "10"});
        loadDetails(new String[]{"Mimic", "NORMAL", "Status", "", "", "10"});
        loadDetails(new String[]{"Minimize", "NORMAL", "Status", "", "", "10"});
        loadDetails(new String[]{"Mirror Move", "FLYING", "Status", "", "", "20"});
        loadDetails(new String[]{"Mist", "ICE", "Status", "", "", "30"});
        loadDetails(new String[]{"Night Shade", "GHOST", "Special", "", "100", "15"});
        loadDetails(new String[]{"Pay Day", "NORMAL", "Physical", "40", "100", "20"});
        loadDetails(new String[]{"Peck", "FLYING", "Physical", "35", "100", "35"});
        loadDetails(new String[]{"Petal Dance", "GRASS", "Special", "120", "100", "10"});
        loadDetails(new String[]{"Pin Missile", "BUG", "Physical", "25", "95", "20"});
        loadDetails(new String[]{"Poison Gas", "POISON", "Status", "", "90", "40"});
        loadDetails(new String[]{"Poison Powder", "POISON", "Status", "", "75", "35"});
        loadDetails(new String[]{"Poison Sting", "POISON", "Physical", "15", "100", "35"});
        loadDetails(new String[]{"Pound", "NORMAL", "Physical", "40", "100", "35"});
        loadDetails(new String[]{"Psybeam", "PSYCHIC", "Special", "65", "100", "20"});
        loadDetails(new String[]{"Psychic", "PSYCHIC", "Special", "90", "100", "10"});
        loadDetails(new String[]{"Psywave", "PSYCHIC", "Special", "", "80", "15"});
        loadDetails(new String[]{"Quick Attack", "NORMAL", "Physical", "40", "100", "30"});
        loadDetails(new String[]{"Rage", "NORMAL", "Physical", "20", "100", "20"});
        loadDetails(new String[]{"Razor Leaf", "GRASS", "Physical", "55", "95", "25"});
        loadDetails(new String[]{"Razor Wind", "NORMAL", "Special", "80", "100", "10"});
        loadDetails(new String[]{"Recover", "NORMAL", "Status", "", "", "10"});
        loadDetails(new String[]{"Reflect", "PSYCHIC", "Status", "", "", "20"});
        loadDetails(new String[]{"Rest", "PSYCHIC", "Status", "", "", "10"});
        loadDetails(new String[]{"Roar", "NORMAL", "Status", "", "", "20"});
        loadDetails(new String[]{"Rock Slide", "ROCK", "Physical", "75", "90", "10"});
        loadDetails(new String[]{"Rock Throw", "ROCK", "Physical", "50", "90", "15"});
        loadDetails(new String[]{"Rolling Kick", "FIGHTING", "Physical", "60", "85", "15"});
        loadDetails(new String[]{"Sand Attack", "GROUND", "Status", "", "100", "15"});
        loadDetails(new String[]{"Scratch", "NORMAL", "Physical", "40", "100", "35"});
        loadDetails(new String[]{"Screech", "NORMAL", "Status", "", "85", "40"});
        loadDetails(new String[]{"Seismic Toss", "FIGHTING", "Physical", "", "100", "20"});
        loadDetails(new String[]{"Self-Destruct", "NORMAL", "Physical", "200", "100", "5"});
        loadDetails(new String[]{"Sharpen", "NORMAL", "Status", "", "", "30"});
        loadDetails(new String[]{"Sing", "NORMAL", "Status", "", "55", "15"});
        loadDetails(new String[]{"Skull Bash", "NORMAL", "Physical", "130", "100", "10"});
        loadDetails(new String[]{"Sky Attack", "FLYING", "Physical", "140", "90", "5"});
        loadDetails(new String[]{"Slam", "NORMAL", "Physical", "80", "75", "20"});
        loadDetails(new String[]{"Slash", "NORMAL", "Physical", "70", "100", "20"});
        loadDetails(new String[]{"Sleep Powder", "GRASS", "Status", "", "75", "15"});
        loadDetails(new String[]{"Sludge", "POISON", "Special", "65", "100", "20"});
        loadDetails(new String[]{"Smog", "POISON", "Special", "30", "70", "20"});
        loadDetails(new String[]{"Smokescreen", "NORMAL", "Status", "", "100", "20"});
        loadDetails(new String[]{"Soft-Boiled", "NORMAL", "Status", "", "", "10"});
        loadDetails(new String[]{"Solar Beam", "GRASS", "Special", "120", "100", "10"});
        loadDetails(new String[]{"Sonic Boom", "NORMAL", "Special", "", "90", "20"});
        loadDetails(new String[]{"Spike Cannon", "NORMAL", "Physical", "20", "100", "15"});
        loadDetails(new String[]{"Splash", "NORMAL", "Status", "", "", "40"});
        loadDetails(new String[]{"Spore", "GRASS", "Status", "", "100", "15"});
        loadDetails(new String[]{"Stomp", "NORMAL", "Physical", "65", "100", "20"});
        loadDetails(new String[]{"Strength", "NORMAL", "Physical", "80", "100", "15"});
        loadDetails(new String[]{"String Shot", "BUG", "Status", "", "95", "40"});
        loadDetails(new String[]{"Struggle", "NORMAL", "Physical", "50", "100", ""});
        loadDetails(new String[]{"Stun Spore", "GRASS", "Status", "", "75", "30"});
        loadDetails(new String[]{"Submission", "FIGHTING", "Physical", "80", "80", "25"});
        loadDetails(new String[]{"Substitute", "NORMAL", "Status", "", "", "10"});
        loadDetails(new String[]{"Super Fang", "NORMAL", "Physical", "", "90", "10"});
        loadDetails(new String[]{"Supersonic", "NORMAL", "Status", "", "55", "20"});
        loadDetails(new String[]{"Surf", "WATER", "Special", "90", "100", "15"});
        loadDetails(new String[]{"Swift", "NORMAL", "Special", "60", "\u221e", "20"});
        loadDetails(new String[]{"Swords Dance", "NORMAL", "Status", "", "", "20"});
        loadDetails(new String[]{"Tackle", "NORMAL", "Physical", "50", "100", "35"});
        loadDetails(new String[]{"Tail Whip", "NORMAL", "Status", "", "100", "30"});
        loadDetails(new String[]{"Take Down", "NORMAL", "Physical", "90", "85", "20"});
        loadDetails(new String[]{"Teleport", "PSYCHIC", "Status", "", "", "20"});
        loadDetails(new String[]{"Thrash", "NORMAL", "Physical", "120", "100", "10"});
        loadDetails(new String[]{"Thunder", "ELECTRIC", "Special", "110", "70", "10"});
        loadDetails(new String[]{"Thunder Punch", "ELECTRIC", "Physical", "75", "100", "15"});
        loadDetails(new String[]{"Thunder Shock", "ELECTRIC", "Special", "40", "100", "30"});
        loadDetails(new String[]{"Thunder Wave", "ELECTRIC", "Status", "", "100", "20"});
        loadDetails(new String[]{"Thunderbolt", "ELECTRIC", "Special", "90", "100", "15"});
        loadDetails(new String[]{"Toxic", "POISON", "Status", "", "90", "10"});
        loadDetails(new String[]{"Transform", "NORMAL", "Status", "", "", "10"});
        loadDetails(new String[]{"Tri Attack", "NORMAL", "Special", "80", "100", "10"});
        loadDetails(new String[]{"Twineedle", "BUG", "Physical", "25", "100", "20"});
        loadDetails(new String[]{"Vice Grip", "NORMAL", "Physical", "55", "100", "30"});
        loadDetails(new String[]{"Vine Whip", "GRASS", "Physical", "45", "100", "25"});
        loadDetails(new String[]{"Water Gun", "WATER", "Special", "40", "100", "25"});
        loadDetails(new String[]{"Waterfall", "WATER", "Physical", "80", "100", "15"});
        loadDetails(new String[]{"Whirlwind", "NORMAL", "Status", "", "", "20"});
        loadDetails(new String[]{"Wing Attack", "FLYING", "Physical", "60", "100", "35"});
        loadDetails(new String[]{"Withdraw", "WATER", "Status", "", "", "40"});
        loadDetails(new String[]{"Wrap", "NORMAL", "Physical", "15", "90", "20"});
        editor.putBoolean("setState", true);
        editor.commit();
    }

    private void loadDetails(String[] set) {
        HashSet values = new HashSet();
        
        for(int i = 0; i < set.length; i++) values.add(i + "_" + set[0]);
        
        editor.putStringSet(set[0], values);
    }
}
