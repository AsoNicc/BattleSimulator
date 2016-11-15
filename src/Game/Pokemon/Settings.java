/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.Pokemon;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import java.util.HashSet;

/**
 *
 * @author Nick
 */
public class Settings extends Activity {
    private Context context;
    private SharedPreferences.Editor editor;
    private boolean collect = false, delete = false;
    private float speedSum = 0;
    private int latestIndex;
    
    public Settings(Context inst){
        context = inst;
    }    
    
    void SetAll(){
        setBaseStats();
        setOrient();
        setMoves();
    }
    
    void clearAll(){
        delete = true;
        SetAll();
    }
    
    void clear(){
        editor.clear();
        editor.commit();
    }
    
    protected void setBaseStats(){
        BaseStats load = new BaseStats(context);
    }
    
    protected void setOrient(){
        Orient load = new Orient(context);
    }
    
    protected void setMoves(){
        Moves load = new Moves(context);
    }
    
    private final class Orient extends Activity {
        private static final String ELEMENTS = "objects";
        private float AVG_WIDTH, AVG_HEIGHT;
        private int  LARGEST_HEIGHT = 0, LARGEST_WIDTH = 0;
        private String Largest_Width_Details, Largest_Height_Details;
        
        Orient(Context context){
            editor = context.getSharedPreferences(ELEMENTS, Context.MODE_PRIVATE).edit();
            
            if(delete){ //ClearAll() was called
                clear();
                return;
            }
            
            setLargest();
            editor.putFloat("avg_width", AVG_WIDTH);
            editor.putFloat("avg_height", AVG_HEIGHT);
            editor.putInt("max_width", LARGEST_WIDTH);
            editor.putInt("max_height", LARGEST_HEIGHT);
            editor.putString("width_details", Largest_Width_Details);
            editor.putString("height_details", Largest_Height_Details);
            editor.putBoolean("setState", true);
            editor.commit();
        }
        
        /* Method used for designing the UI, arranging elements throughout */
        private void setLargest(){
            AssetManager asset = context.getAssets();
            BitmapFactory.Options options;
            Bitmap bitmap;
            int fs_cnt, o_cnt, pkmn_cnt = 0, sum_width, sum_height;
            float AVGSUM_O_WIDTH, AVGSUM_O_HEIGHT, AVGSUM_PKMN_WIDTH = 0, AVGSUM_PKMN_HEIGHT = 0;
            String[] frames, files, folders;

            try {
                folders = asset.list("sprites");

                for(String folder : folders){
                    o_cnt = 0; //(Re)set orientation counter | NOTE: Only == (1 | 2 max)
                    AVGSUM_O_WIDTH = 0;
                    AVGSUM_O_HEIGHT = 0;

                    files = asset.list("sprites/" + folder);
                    pkmn_cnt++; //Inc. folder count

                    for(String fileFolder : files){
                        fs_cnt = 0; //(Re)set counter for # of frames w/n an orientation
                        sum_width = 0; //(Re)set sum for width
                        sum_height = 0; //(Re)set sum for height
                        frames = asset.list("sprites/" + folder + "/" + fileFolder);
                        o_cnt++; //Inc. orientation

                        for(String file : frames){
                            options = new BitmapFactory.Options();
                            bitmap = BitmapFactory.decodeStream(asset.open("sprites/" + folder + "/" + fileFolder + "/" + file), null, options);
                            fs_cnt++;

                            sum_width += options.outWidth;
                            if(options.outWidth > LARGEST_WIDTH){
                                LARGEST_WIDTH = options.outWidth;
                                Largest_Width_Details = "sprites/" + folder + "/" + fileFolder + "/" + file + " " + String.valueOf(LARGEST_WIDTH);
                            }
                            sum_height += options.outHeight;
                            if(options.outHeight > LARGEST_HEIGHT){
                                LARGEST_HEIGHT = options.outHeight;
                                Largest_Height_Details = "sprites/" + folder + "/" + fileFolder + "/" + file + " " + String.valueOf(LARGEST_HEIGHT);
                            }
                        } // EOF : frameSet

                        AVGSUM_O_WIDTH += ((float)sum_width)/fs_cnt;
                        AVGSUM_O_HEIGHT += ((float)sum_height)/fs_cnt;
                    } // EOF : orientation

                    AVGSUM_PKMN_WIDTH += AVGSUM_O_WIDTH/o_cnt;
                    AVGSUM_PKMN_HEIGHT += AVGSUM_O_HEIGHT/o_cnt;
                } // EOF : Pokemon
            } catch (Exception e) {

            } finally {
                AVG_WIDTH = AVGSUM_PKMN_WIDTH/pkmn_cnt;
                AVG_HEIGHT = AVGSUM_PKMN_HEIGHT/pkmn_cnt;
            }
        }
    }
    
    private final class BaseStats extends Activity {
        private static final String STAT_LIST = "genOneBaseStatList";

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

            if(delete){ //ClearAll() was called
                clear();
                return;
            }
            
            //Set flag to collect speed stat for avg
            collect = true;
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
            //Stop counting speed sum
            collect = false;
            editor.putInt("avg_speed", Round(speedSum/latestIndex));
            editor.putBoolean("setState", true);
            editor.commit();
        }
    }
    
    private final class Moves extends Activity {
        private static final String MOVE_LIST = "genOneMoveList";

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

            if(delete){ //ClearAll() was called
                clear();
                return;
            }
            
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
    }
    
    private void loadDetails(String[] set){
        HashSet values = new HashSet();

        for(int i = 0; i < set.length; i++) values.add(i + "_" + set[i]);

        if(collect){ //Gather data for speed avg
            speedSum += Float.parseFloat(set[7]);
            latestIndex = Integer.parseInt(set[0]);
        }
        
        editor.putStringSet(set[0], values);
    }
    
    private int Round(double num){
        if(num/((int)num) >= .5 ) return (int)Math.ceil(num);
        else return (int)Math.floor(num);
    }
}