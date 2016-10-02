/*
 * To change this license header, choose License Headers in Project Properties
 * To change this template file, choose Tools | Templates
 * and open the template in the editor
 */
package Game.Pokemon;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import java.util.HashSet;

/**
 * @author Nick
 * FUTURE CHANGE: Noticed a particular pattern w/n ExpYield. Search for the
 * formula that gives stated output. Doing so will save KB(s) of memory, making
 * source code more flexible to future changes, makes .apk smaller.
 */
public class BaseStats extends Activity {
    private static final String STAT_LIST = "genOneBaseStatList";
    private static SharedPreferences.Editor editor;

    BaseStats(Context context){
        initialize(context);
    }

    BaseStats(Context context, AttributeSet attribs){
        initialize(context);
    }

    BaseStats(Context context, AttributeSet attribs, int defStyle){
        initialize(context);
    }

    void initialize(Context context){
        editor = context.getSharedPreferences(STAT_LIST, Context.MODE_PRIVATE).edit();
        
        //{"#", "Name", "HP", "Attack", "Defense", "Sp Attack", "Sp Defense", "Speed", "ExpYield"} // NOTE2SELF
        loadDetails(new String[]{"1", "Bulbasaur", "45", "49", "49", "65", "65", "45", "64"}); //
        loadDetails(new String[]{"2", "Ivysaur", "60", "62", "63", "80", "80", "60", "142"}); //
        loadDetails(new String[]{"3", "Venusaur", "80", "82", "83", "100", "100", "80", "236"}); //
//        loadDetails(new String[]{"3M", "Venusaur (Mega Venusaur)", "80", "100", "123", "122", "120", "80", "236"}); //
        loadDetails(new String[]{"4", "Charmander", "39", "52", "43", "60", "50", "65", "62"}); //
        loadDetails(new String[]{"5", "Charmeleon", "58", "64", "58", "80", "65", "80", "142"}); //
        loadDetails(new String[]{"6", "Charizard", "78", "84", "78", "109", "85", "100", "240"}); //
//        loadDetails(new String[]{"6MX", "Charizard (Mega Charizard X)", "78", "130", "111", "130", "85", "100", "240"}); //
//        loadDetails(new String[]{"6MY", "Charizard (Mega Charizard Y)", "78", "104", "78", "159", "115", "100", "240"}); //
        loadDetails(new String[]{"7", "Squirtle", "44", "48", "65", "50", "64", "43", "63"}); //
        loadDetails(new String[]{"8", "Wartortle", "59", "63", "80", "65", "80", "58", "142"}); //
        loadDetails(new String[]{"9", "Blastoise", "79", "83", "100", "85", "105", "78", "239"}); //
//        loadDetails(new String[]{"9M", "Blastoise (Mega Blastoise)", "79", "103", "120", "135", "115", "78", "239"}); //
        loadDetails(new String[]{"10", "Caterpie", "45", "30", "35", "20", "20", "45", "39"}); //
        loadDetails(new String[]{"11", "Metapod", "50", "20", "55", "25", "25", "30", "72"}); //
        loadDetails(new String[]{"12", "Butterfree", "60", "45", "50", "90", "80", "70", "173"}); //
        loadDetails(new String[]{"13", "Weedle", "40", "35", "30", "20", "20", "50", "39"}); //
        loadDetails(new String[]{"14", "Kakuna", "45", "25", "50", "25", "25", "35", "72"}); //
        loadDetails(new String[]{"15", "Beedrill", "65", "90", "40", "45", "80", "75", "173"}); //
//        loadDetails(new String[]{"15M", "Beedrill (Mega Beedrill)", "65", "150", "40", "15", "80", "145", "173"}); //
        loadDetails(new String[]{"16", "Pidgey", "40", "45", "40", "35", "35", "56", "50"}); //
        loadDetails(new String[]{"17", "Pidgeotto", "63", "60", "55", "50", "50", "71", "122"}); //
        loadDetails(new String[]{"18", "Pidgeot", "83", "80", "75", "70", "70", "101", "211"}); //
//        loadDetails(new String[]{"18M", "Pidgeot (Mega Pidgeot)", "83", "80", "80", "135", "80", "121", "211"}); //
        loadDetails(new String[]{"19", "Rattata", "30", "56", "35", "25", "35", "72", "51"}); //
        loadDetails(new String[]{"20", "Raticate", "55", "81", "60", "50", "70", "97", "145"}); //
        loadDetails(new String[]{"21", "Spearow", "40", "60", "30", "31", "31", "70", "52"}); //
        loadDetails(new String[]{"22", "Fearow", "65", "90", "65", "61", "61", "100", "155"}); //
        loadDetails(new String[]{"23", "Ekans", "35", "60", "44", "40", "54", "55", "58"}); //
        loadDetails(new String[]{"24", "Arbok", "60", "85", "69", "65", "79", "80", "153"}); //
        loadDetails(new String[]{"25", "Pikachu", "35", "55", "40", "50", "50", "90", "105"}); //
        loadDetails(new String[]{"26", "Raichu", "60", "90", "55", "90", "80", "110", "214"}); //
        loadDetails(new String[]{"27", "Sandshrew", "50", "75", "85", "20", "30", "40", "60"}); //
        loadDetails(new String[]{"28", "Sandslash", "75", "100", "110", "45", "55", "65", "158"}); //
        loadDetails(new String[]{"29", "Nidoran♀", "55", "47", "52", "40", "40", "41", "55"}); //
        loadDetails(new String[]{"30", "Nidorina", "70", "62", "67", "55", "55", "56", "128"}); //
        loadDetails(new String[]{"31", "Nidoqueen", "90", "92", "87", "75", "85", "76", "223"}); //
        loadDetails(new String[]{"32", "Nidoran♂", "46", "57", "40", "40", "40", "50", "55"}); //
        loadDetails(new String[]{"33", "Nidorino", "61", "72", "57", "55", "55", "65", "128"}); //
        loadDetails(new String[]{"34", "Nidoking", "81", "102", "77", "85", "75", "85", "223"}); //
        loadDetails(new String[]{"35", "Clefairy", "70", "45", "48", "60", "65", "35", "113"}); //
        loadDetails(new String[]{"36", "Clefable", "95", "70", "73", "95", "90", "60", "213"}); //
        loadDetails(new String[]{"37", "Vulpix", "38", "41", "40", "50", "65", "65", "60"}); //
        loadDetails(new String[]{"38", "Ninetales", "73", "76", "75", "81", "100", "100", "177"}); //
        loadDetails(new String[]{"39", "Jigglypuff", "115", "45", "20", "45", "25", "20", "95"}); //
        loadDetails(new String[]{"40", "Wigglytuff", "140", "70", "45", "85", "50", "45", "191"}); //
        loadDetails(new String[]{"41", "Zubat", "40", "45", "35", "30", "40", "55", "49"}); //
        loadDetails(new String[]{"42", "Golbat", "75", "80", "70", "65", "75", "90", "159"}); //
        loadDetails(new String[]{"43", "Oddish", "45", "50", "55", "75", "65", "30", "64"}); //
        loadDetails(new String[]{"44", "Gloom", "60", "65", "70", "85", "75", "40", "138"}); //
        loadDetails(new String[]{"45", "Vileplume", "75", "80", "85", "110", "90", "50", "216"}); //
        loadDetails(new String[]{"46", "Paras", "35", "70", "55", "45", "55", "25", "57"}); //
        loadDetails(new String[]{"47", "Parasect", "60", "95", "80", "60", "80", "30", "142"}); //
        loadDetails(new String[]{"48", "Venonat", "60", "55", "50", "40", "55", "45", "61"}); //
        loadDetails(new String[]{"49", "Venomoth", "70", "65", "60", "90", "75", "90", "158"}); //
        loadDetails(new String[]{"50", "Diglett", "10", "55", "25", "35", "45", "95", "53"}); //
        loadDetails(new String[]{"51", "Dugtrio", "35", "80", "50", "50", "70", "120", "142"}); //
        loadDetails(new String[]{"52", "Meowth", "40", "45", "35", "40", "40", "90", "58"}); //
        loadDetails(new String[]{"53", "Persian", "65", "70", "60", "65", "65", "115", "154"}); //
        loadDetails(new String[]{"54", "Psyduck", "50", "52", "48", "65", "50", "55", "64"}); //
        loadDetails(new String[]{"55", "Golduck", "80", "82", "78", "95", "80", "85", "175"}); //
        loadDetails(new String[]{"56", "Mankey", "40", "80", "35", "35", "45", "70", "61"}); //
        loadDetails(new String[]{"57", "Primeape", "65", "105", "60", "60", "70", "95", "159"}); //
        loadDetails(new String[]{"58", "Growlithe", "55", "70", "45", "70", "50", "60", "70"}); //
        loadDetails(new String[]{"59", "Arcanine", "90", "110", "80", "100", "80", "95", "194"}); //
        loadDetails(new String[]{"60", "Poliwag", "40", "50", "40", "40", "40", "90", "60"}); //
        loadDetails(new String[]{"61", "Poliwhirl", "65", "65", "65", "50", "50", "90", "135"}); //
        loadDetails(new String[]{"62", "Poliwrath", "90", "95", "95", "70", "90", "70", "225"}); //
        loadDetails(new String[]{"63", "Abra", "25", "20", "15", "105", "55", "90", "62"}); //
        loadDetails(new String[]{"64", "Kadabra", "40", "35", "30", "120", "70", "105", "140"}); //
        loadDetails(new String[]{"65", "Alakazam", "55", "50", "45", "135", "95", "120", "221"}); //
//        loadDetails(new String[]{"65M", "Alakazam (Mega Alakazam)", "55", "50", "65", "175", "95", "150", "221"}); //
        loadDetails(new String[]{"66", "Machop", "70", "80", "50", "35", "35", "35", "61"}); //
        loadDetails(new String[]{"67", "Machoke", "80", "100", "70", "50", "60", "45", "142"}); //
        loadDetails(new String[]{"68", "Machamp", "90", "130", "80", "65", "85", "55", "227"}); //
        loadDetails(new String[]{"69", "Bellsprout", "50", "75", "35", "70", "30", "40", "60"}); //
        loadDetails(new String[]{"70", "Weepinbell", "65", "90", "50", "85", "45", "55", "137"}); //
        loadDetails(new String[]{"71", "Victreebel", "80", "105", "65", "100", "70", "70", "216"}); //
        loadDetails(new String[]{"72", "Tentacool", "40", "40", "35", "50", "100", "70", "67"}); //
        loadDetails(new String[]{"73", "Tentacruel", "80", "70", "65", "80", "120", "100", "180"}); //
        loadDetails(new String[]{"74", "Geodude", "40", "80", "100", "30", "30", "20", "60"}); //
        loadDetails(new String[]{"75", "Graveler", "55", "95", "115", "45", "45", "35", "137"}); //
        loadDetails(new String[]{"76", "Golem", "80", "120", "130", "55", "65", "45", "218"}); //
        loadDetails(new String[]{"77", "Ponyta", "50", "85", "55", "65", "65", "90", "82"}); //
        loadDetails(new String[]{"78", "Rapidash", "65", "100", "70", "80", "80", "105", "175"}); //
        loadDetails(new String[]{"79", "Slowpoke", "90", "65", "65", "40", "40", "15", "63"}); //
        loadDetails(new String[]{"80", "Slowbro", "95", "75", "110", "100", "80", "30", "172"}); //
//        loadDetails(new String[]{"80M", "Slowbro (Mega Slowbro)", "95", "75", "180", "130", "80", "30", "172"}); //
        loadDetails(new String[]{"81", "Magnemite", "25", "35", "70", "95", "55", "45", "65"}); //
        loadDetails(new String[]{"82", "Magneton", "50", "60", "95", "120", "70", "70", "163"}); //
        loadDetails(new String[]{"83", "Farfetch'd", "52", "65", "55", "58", "62", "60", "123"}); //
        loadDetails(new String[]{"84", "Doduo", "35", "85", "45", "35", "35", "75", "62"}); //
        loadDetails(new String[]{"85", "Dodrio", "60", "110", "70", "60", "60", "100", "161"}); //
        loadDetails(new String[]{"86", "Seel", "65", "45", "55", "45", "70", "45", "65"}); //
        loadDetails(new String[]{"87", "Dewgong", "90", "70", "80", "70", "95", "70", "166"}); //
        loadDetails(new String[]{"88", "Grimer", "80", "80", "50", "40", "50", "25", "65"}); //
        loadDetails(new String[]{"89", "Muk", "105", "105", "75", "65", "100", "50", "175"}); //
        loadDetails(new String[]{"90", "Shellder", "30", "65", "100", "45", "25", "40", "61"}); //
        loadDetails(new String[]{"91", "Cloyster", "50", "95", "180", "85", "45", "70", "184"}); //
        loadDetails(new String[]{"92", "Gastly", "30", "35", "30", "100", "35", "80", "62"}); //
        loadDetails(new String[]{"93", "Haunter", "45", "50", "45", "115", "55", "95", "142"}); //
        loadDetails(new String[]{"94", "Gengar", "60", "65", "60", "130", "75", "110", "225"}); //
//        loadDetails(new String[]{"94M", "Gengar (Mega Gengar)", "60", "65", "80", "170", "95", "130", "225"}); //
        loadDetails(new String[]{"95", "Onix", "35", "45", "160", "30", "45", "70", "77"}); //
        loadDetails(new String[]{"96", "Drowzee", "60", "48", "45", "43", "90", "42", "66"}); //
        loadDetails(new String[]{"97", "Hypno", "85", "73", "70", "73", "115", "67", "169"}); //
        loadDetails(new String[]{"98", "Krabby", "30", "105", "90", "25", "25", "50", "65"}); //
        loadDetails(new String[]{"99", "Kingler", "55", "130", "115", "50", "50", "75", "166"}); //
        loadDetails(new String[]{"100", "Voltorb", "40", "30", "50", "55", "55", "100", "66"}); //
        loadDetails(new String[]{"101", "Electrode", "60", "50", "70", "80", "80", "140", "168"}); //
        loadDetails(new String[]{"102", "Exeggcute", "60", "40", "80", "60", "45", "40", "65"}); //
        loadDetails(new String[]{"103", "Exeggutor", "95", "95", "85", "125", "65", "55", "182"}); //
        loadDetails(new String[]{"104", "Cubone", "50", "50", "95", "40", "50", "35", "64"}); //
        loadDetails(new String[]{"105", "Marowak", "60", "80", "110", "50", "80", "45", "149"}); //
        loadDetails(new String[]{"106", "Hitmonlee", "50", "120", "53", "35", "110", "87", "159"}); //
        loadDetails(new String[]{"107", "Hitmonchan", "50", "105", "79", "35", "110", "76", "159"}); //
        loadDetails(new String[]{"108", "Lickitung", "90", "55", "75", "60", "75", "30", "77"}); //
        loadDetails(new String[]{"109", "Koffing", "40", "65", "95", "60", "45", "35", "68"}); //
        loadDetails(new String[]{"110", "Weezing", "65", "90", "120", "85", "70", "60", "172"}); //
        loadDetails(new String[]{"111", "Rhyhorn", "80", "85", "95", "30", "30", "25", "69"}); //
        loadDetails(new String[]{"112", "Rhydon", "105", "130", "120", "45", "45", "40", "170"}); //
        loadDetails(new String[]{"113", "Chansey", "250", "5", "5", "35", "105", "50", "395"}); //
        loadDetails(new String[]{"114", "Tangela", "65", "55", "115", "100", "40", "60", "87"}); //
        loadDetails(new String[]{"115", "Kangaskhan", "105", "95", "80", "40", "80", "90", "172"}); //
//        loadDetails(new String[]{"115M", "Kangaskhan (Mega Kangaskhan)", "105", "125", "100", "60", "100", "100", "172"}); //
        loadDetails(new String[]{"116", "Horsea", "30", "40", "70", "70", "25", "60", "59"}); //
        loadDetails(new String[]{"117", "Seadra", "55", "65", "95", "95", "45", "85", "154"}); //
        loadDetails(new String[]{"118", "Goldeen", "45", "67", "60", "35", "50", "63", "64"}); //
        loadDetails(new String[]{"119", "Seaking", "80", "92", "65", "65", "80", "68", "158"}); //
        loadDetails(new String[]{"120", "Staryu", "30", "45", "55", "70", "55", "85", "68"}); //
        loadDetails(new String[]{"121", "Starmie", "60", "75", "85", "100", "85", "115", "182"}); //
        loadDetails(new String[]{"122", "Mr Mime", "40", "45", "65", "100", "120", "90", "161"}); //
        loadDetails(new String[]{"123", "Scyther", "70", "110", "80", "55", "80", "105", "100"}); //
        loadDetails(new String[]{"124", "Jynx", "65", "50", "35", "115", "95", "95", "159"}); //
        loadDetails(new String[]{"125", "Electabuzz", "65", "83", "57", "95", "85", "105", "172"}); //
        loadDetails(new String[]{"126", "Magmar", "65", "95", "57", "100", "85", "93", "173"}); //
        loadDetails(new String[]{"127", "Pinsir", "65", "125", "100", "55", "70", "85", "175"}); //
//        loadDetails(new String[]{"127M", "Pinsir (Mega Pinsir)", "65", "155", "120", "65", "90", "105", "175"}); //
        loadDetails(new String[]{"128", "Tauros", "75", "100", "95", "40", "70", "110", "172"}); //
        loadDetails(new String[]{"129", "Magikarp", "20", "10", "55", "15", "20", "80", "40"}); //
        loadDetails(new String[]{"130", "Gyarados", "95", "125", "79", "60", "100", "81", "189"}); //
//        loadDetails(new String[]{"130M", "Gyarados (Mega Gyarados)", "95", "155", "109", "70", "130", "81", "189"}); //
        loadDetails(new String[]{"131", "Lapras", "130", "85", "80", "85", "95", "60", "187"}); //
        loadDetails(new String[]{"132", "Ditto", "48", "48", "48", "48", "48", "48", "101"}); //
        loadDetails(new String[]{"133", "Eevee", "55", "55", "50", "45", "65", "55", "65"}); //
        loadDetails(new String[]{"134", "Vaporeon", "130", "65", "60", "110", "95", "65", "184"}); //
        loadDetails(new String[]{"135", "Jolteon", "65", "65", "60", "110", "95", "130", "184"}); //
        loadDetails(new String[]{"136", "Flareon", "65", "130", "60", "95", "110", "65", "184"}); //
        loadDetails(new String[]{"137", "Porygon", "65", "60", "70", "85", "75", "40", "79"}); //
        loadDetails(new String[]{"138", "Omanyte", "35", "40", "100", "90", "55", "35", "71"}); //
        loadDetails(new String[]{"139", "Omastar", "70", "60", "125", "115", "70", "55", "173"}); //
        loadDetails(new String[]{"140", "Kabuto", "30", "80", "90", "55", "45", "55", "71"}); //
        loadDetails(new String[]{"141", "Kabutops", "60", "115", "105", "65", "70", "80", "173"}); //
        loadDetails(new String[]{"142", "Aerodactyl", "80", "105", "65", "60", "75", "130", "180"}); //
//        loadDetails(new String[]{"142M", "Aerodactyl (Mega Aerodactyl)", "80", "135", "85", "70", "95", "150", "180"}); //
        loadDetails(new String[]{"143", "Snorlax", "160", "110", "65", "65", "110", "30", "189"}); //
        loadDetails(new String[]{"144", "Articuno", "90", "85", "100", "95", "125", "85", "261"}); //
        loadDetails(new String[]{"145", "Zapdos", "90", "90", "85", "125", "90", "100", "261"}); //
        loadDetails(new String[]{"146", "Moltres", "90", "100", "90", "125", "85", "90", "261"}); //
        loadDetails(new String[]{"147", "Dratini", "41", "64", "45", "50", "50", "50", "60"}); //
        loadDetails(new String[]{"148", "Dragonair", "61", "84", "65", "70", "70", "70", "147"}); //
        loadDetails(new String[]{"149", "Dragonite", "91", "134", "95", "100", "100", "80", "270"}); //
        loadDetails(new String[]{"150", "Mewtwo", "106", "110", "90", "154", "90", "130", "306"}); //
//        loadDetails(new String[]{"150MX", "Mewtwo (Mega Mewtwo X)", "106", "190", "100", "154", "100", "130", "306"}); //
//        loadDetails(new String[]{"150MY", "Mewtwo (Mega Mewtwo Y)", "106", "150", "70", "194", "120", "140", "306"}); //
        loadDetails(new String[]{"151", "Mew", "100", "100", "100", "100", "100", "100", "270"}); //
    }
    
    private void loadDetails(String[] set){
        HashSet values = new HashSet();

        for(int i = 0; i < set.length; i++) values.add(i + "_" + set[0]);

        editor.putStringSet(set[0], values);
    }
}