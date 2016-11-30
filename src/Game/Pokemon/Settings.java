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
    private float speedSum = 0f;
    private int latestIndex;
    
    public Settings(Context inst){
        context = inst;
    }    
    
    void setAll(){
        setBaseInfo();
        setOrient();
        setMoves();
        setLearnedMoves();
    }
    
    void clearAll(){
        delete = true;
        setAll();
    }
    
    void clear(){
        editor.clear();
        editor.commit();
    }
    
    protected void setBaseInfo(){
        BaseInfo load = new BaseInfo(context);
    }
    
    protected void setOrient(){
        Orient load = new Orient(context);
    }
    
    protected void setMoves(){
        Moves load = new Moves(context);
    }
    
    protected void setLearnedMoves(){
        LearnedMoves load = new LearnedMoves(context);
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
    
    private final class BaseInfo extends Activity {
        private static final String STAT_LIST = "genOneBaseStatList";

        BaseInfo(Context context){
            initialize(context);
        }

        BaseInfo(Context context, AttributeSet attribs){
            initialize(context);
        }

        BaseInfo(Context context, AttributeSet attribs, int defStyle){
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
            //{"#", "Name", "HP", "Attack", "Defense", "Sp Attack", "Sp Defense", "Speed", "ExpYield", "EvolveLVL"} // NOTE2SELF
            loadDetails(new String[]{"1", "Bulbasaur", "45", "49", "49", "65", "65", "45", "64", "16", "GRASS", "POISON"}); //
            loadDetails(new String[]{"2", "Ivysaur", "60", "62", "63", "80", "80", "60", "142", "32", "GRASS", "POISON"}); //
            loadDetails(new String[]{"3", "Venusaur", "80", "82", "83", "100", "100", "80", "236", "", "GRASS", "POISON"}); //
        //        loadDetails(new String[]{"3M", "Venusaur (Mega Venusaur)", "80", "100", "123", "122", "120", "80", "236", "", "GRASS", "POISON"}); //
            loadDetails(new String[]{"4", "Charmander", "39", "52", "43", "60", "50", "65", "62", "16", "FIRE", ""}); //
            loadDetails(new String[]{"5", "Charmeleon", "58", "64", "58", "80", "65", "80", "142", "36", "FIRE", ""}); //
            loadDetails(new String[]{"6", "Charizard", "78", "84", "78", "109", "85", "100", "240", "", "FIRE", "FLYING"}); //
        //        loadDetails(new String[]{"6MX", "Charizard (Mega Charizard X)", "78", "130", "111", "130", "85", "100", "240", "", "FIRE", "DRAGON"}); //
        //        loadDetails(new String[]{"6MY", "Charizard (Mega Charizard Y)", "78", "104", "78", "159", "115", "100", "240", "", "FIRE", "FLYING"}); //
            loadDetails(new String[]{"7", "Squirtle", "44", "48", "65", "50", "64", "43", "63", "16", "WATER", ""}); //
            loadDetails(new String[]{"8", "Wartortle", "59", "63", "80", "65", "80", "58", "142", "36", "WATER", ""}); //
            loadDetails(new String[]{"9", "Blastoise", "79", "83", "100", "85", "105", "78", "239", "", "WATER", ""}); //
        //        loadDetails(new String[]{"9M", "Blastoise (Mega Blastoise)", "79", "103", "120", "135", "115", "78", "239", "", "WATER", ""}); //
            loadDetails(new String[]{"10", "Caterpie", "45", "30", "35", "20", "20", "45", "39", "7", "BUG", ""}); //
            loadDetails(new String[]{"11", "Metapod", "50", "20", "55", "25", "25", "30", "72", "10", "BUG", ""}); //
            loadDetails(new String[]{"12", "Butterfree", "60", "45", "50", "90", "80", "70", "173", "", "BUG", "FLYING"}); //
            loadDetails(new String[]{"13", "Weedle", "40", "35", "30", "20", "20", "50", "39", "7", "BUG", "POISON"}); //
            loadDetails(new String[]{"14", "Kakuna", "45", "25", "50", "25", "25", "35", "72", "10", "BUG", "POISON"}); //
            loadDetails(new String[]{"15", "Beedrill", "65", "90", "40", "45", "80", "75", "173", "", "BUG", "POISON"}); //
        //        loadDetails(new String[]{"15M", "Beedrill (Mega Beedrill)", "65", "150", "40", "15", "80", "145", "173", "", "BUG", "POISON"}); //
            loadDetails(new String[]{"16", "Pidgey", "40", "45", "40", "35", "35", "56", "50", "18", "NORMAL", "FLYING"}); //
            loadDetails(new String[]{"17", "Pidgeotto", "63", "60", "55", "50", "50", "71", "122", "36", "NORMAL", "FLYING"}); //
            loadDetails(new String[]{"18", "Pidgeot", "83", "80", "75", "70", "70", "101", "211", "", "NORMAL", "FLYING"}); //
        //        loadDetails(new String[]{"18M", "Pidgeot (Mega Pidgeot)", "83", "80", "80", "135", "80", "121", "211", "", "NORMAL", "FLYING"}); //
            loadDetails(new String[]{"19", "Rattata", "30", "56", "35", "25", "35", "72", "51", "20", "NORMAL", ""}); //
            loadDetails(new String[]{"20", "Raticate", "55", "81", "60", "50", "70", "97", "145", "", "NORMAL", ""}); //
            loadDetails(new String[]{"21", "Spearow", "40", "60", "30", "31", "31", "70", "52", "20", "NORMAL", "FLYING"}); //
            loadDetails(new String[]{"22", "Fearow", "65", "90", "65", "61", "61", "100", "155", "", "NORMAL", "FLYING"}); //
            loadDetails(new String[]{"23", "Ekans", "35", "60", "44", "40", "54", "55", "58", "22", "POISON", ""}); //
            loadDetails(new String[]{"24", "Arbok", "60", "85", "69", "65", "79", "80", "153", "", "POISON", ""}); //
            loadDetails(new String[]{"25", "Pikachu", "35", "55", "40", "50", "50", "90", "105", "THUNDERSTONE", "ELECTRIC", ""}); //
            loadDetails(new String[]{"26", "Raichu", "60", "90", "55", "90", "80", "110", "214", "", "ELECTRIC", ""}); //
            loadDetails(new String[]{"27", "Sandshrew", "50", "75", "85", "20", "30", "40", "60", "22", "GROUND", ""}); //
            loadDetails(new String[]{"28", "Sandslash", "75", "100", "110", "45", "55", "65", "158", "", "GROUND", ""}); //
            loadDetails(new String[]{"29", "Nidoran♀", "55", "47", "52", "40", "40", "41", "55", "16", "POISON", ""}); //
            loadDetails(new String[]{"30", "Nidorina", "70", "62", "67", "55", "55", "56", "128", "MOONSTONE", "POISON", ""}); //
            loadDetails(new String[]{"31", "Nidoqueen", "90", "92", "87", "75", "85", "76", "223", "", "POISON", "GROUND"}); //
            loadDetails(new String[]{"32", "Nidoran♂", "46", "57", "40", "40", "40", "50", "55", "16", "POISON", ""}); //
            loadDetails(new String[]{"33", "Nidorino", "61", "72", "57", "55", "55", "65", "128", "MOONSTONE", "POISON", ""}); //
            loadDetails(new String[]{"34", "Nidoking", "81", "102", "77", "85", "75", "85", "223", "", "POISON", "GROUND"}); //
            loadDetails(new String[]{"35", "Clefairy", "70", "45", "48", "60", "65", "35", "113", "MOONSTONE", "FAIRY", ""}); //
            loadDetails(new String[]{"36", "Clefable", "95", "70", "73", "95", "90", "60", "213", "", "FAIRY", ""}); //
            loadDetails(new String[]{"37", "Vulpix", "38", "41", "40", "50", "65", "65", "60", "FIRESTONE", "FIRE", ""}); //
            loadDetails(new String[]{"38", "Ninetales", "73", "76", "75", "81", "100", "100", "177", "", "FIRE", ""}); //
            loadDetails(new String[]{"39", "Jigglypuff", "115", "45", "20", "45", "25", "20", "95", "MOONSTONE", "NORMAL", "FAIRY"}); //
            loadDetails(new String[]{"40", "Wigglytuff", "140", "70", "45", "85", "50", "45", "191", "", "NORMAL", "FAIRY"}); //
            loadDetails(new String[]{"41", "Zubat", "40", "45", "35", "30", "40", "55", "49", "22", "POISON", "FLYING"}); //
            loadDetails(new String[]{"42", "Golbat", "75", "80", "70", "65", "75", "90", "159", "", "POISON", "FLYING"}); //
            loadDetails(new String[]{"43", "Oddish", "45", "50", "55", "75", "65", "30", "64", "21", "GRASS", "POISON"}); //
            loadDetails(new String[]{"44", "Gloom", "60", "65", "70", "85", "75", "40", "138", "LEAFSTONE", "GRASS", "POISON"}); //
            loadDetails(new String[]{"45", "Vileplume", "75", "80", "85", "110", "90", "50", "216", "", "GRASS", "POISON"}); //
            loadDetails(new String[]{"46", "Paras", "35", "70", "55", "45", "55", "25", "57", "24", "BUG", "GRASS"}); //
            loadDetails(new String[]{"47", "Parasect", "60", "95", "80", "60", "80", "30", "142", "", "BUG", "GRASS"}); //
            loadDetails(new String[]{"48", "Venonat", "60", "55", "50", "40", "55", "45", "61", "31", "BUG", "POISON"}); //
            loadDetails(new String[]{"49", "Venomoth", "70", "65", "60", "90", "75", "90", "158", "", "BUG", "POISON"}); //
            loadDetails(new String[]{"50", "Diglett", "10", "55", "25", "35", "45", "95", "53", "26", "GROUND", ""}); //
            loadDetails(new String[]{"51", "Dugtrio", "35", "80", "50", "50", "70", "120", "142", "", "GROUND", ""}); //
            loadDetails(new String[]{"52", "Meowth", "40", "45", "35", "40", "40", "90", "58", "28", "NORMAL", ""}); //
            loadDetails(new String[]{"53", "Persian", "65", "70", "60", "65", "65", "115", "154", "", "NORMAL", ""}); //
            loadDetails(new String[]{"54", "Psyduck", "50", "52", "48", "65", "50", "55", "64", "33", "WATER", ""}); //
            loadDetails(new String[]{"55", "Golduck", "80", "82", "78", "95", "80", "85", "175", "", "WATER", ""}); //
            loadDetails(new String[]{"56", "Mankey", "40", "80", "35", "35", "45", "70", "61", "28", "FIGHTING", ""}); //
            loadDetails(new String[]{"57", "Primeape", "65", "105", "60", "60", "70", "95", "159", "", "FIGHTING", ""}); //
            loadDetails(new String[]{"58", "Growlithe", "55", "70", "45", "70", "50", "60", "70", "FIRESTONE", "FIRE", ""}); //
            loadDetails(new String[]{"59", "Arcanine", "90", "110", "80", "100", "80", "95", "194", "", "FIRE", ""}); //
            loadDetails(new String[]{"60", "Poliwag", "40", "50", "40", "40", "40", "90", "60", "25", "WATER", ""}); //
            loadDetails(new String[]{"61", "Poliwhirl", "65", "65", "65", "50", "50", "90", "135", "WATERSTONE", "WATER", ""}); //
            loadDetails(new String[]{"62", "Poliwrath", "90", "95", "95", "70", "90", "70", "225", "", "WATER", "FIGHTING"}); //
            loadDetails(new String[]{"63", "Abra", "25", "20", "15", "105", "55", "90", "62", "16", "PSYCHIC", ""}); //
            loadDetails(new String[]{"64", "Kadabra", "40", "35", "30", "120", "70", "105", "140", "TRADE", "PSYCHIC", ""}); //
            loadDetails(new String[]{"65", "Alakazam", "55", "50", "45", "135", "95", "120", "221", "", "PSYCHIC", ""}); //
        //        loadDetails(new String[]{"65M", "Alakazam (Mega Alakazam)", "55", "50", "65", "175", "95", "150", "221", "", "PSYCHIC", ""}); //
            loadDetails(new String[]{"66", "Machop", "70", "80", "50", "35", "35", "35", "61", "28", "FIGHTING", "AA"}); //
            loadDetails(new String[]{"67", "Machoke", "80", "100", "70", "50", "60", "45", "142", "TRADE", "FIGHTING", ""}); //
            loadDetails(new String[]{"68", "Machamp", "90", "130", "80", "65", "85", "55", "227", "", "FIGHTING", ""}); //
            loadDetails(new String[]{"69", "Bellsprout", "50", "75", "35", "70", "30", "40", "60", "21", "GRASS", "POISON"}); //
            loadDetails(new String[]{"70", "Weepinbell", "65", "90", "50", "85", "45", "55", "137", "LEAFSTONE", "GRASS", "POISON"}); //
            loadDetails(new String[]{"71", "Victreebel", "80", "105", "65", "100", "70", "70", "216", "", "GRASS", "POISON"}); //
            loadDetails(new String[]{"72", "Tentacool", "40", "40", "35", "50", "100", "70", "67", "30", "WATER", "POISON"}); //
            loadDetails(new String[]{"73", "Tentacruel", "80", "70", "65", "80", "120", "100", "180", "", "WATER", "POISON"}); //
            loadDetails(new String[]{"74", "Geodude", "40", "80", "100", "30", "30", "20", "60", "25", "ROCK", "GROUND"}); //
            loadDetails(new String[]{"75", "Graveler", "55", "95", "115", "45", "45", "35", "137", "TRADE", "ROCK", "GROUND"}); //
            loadDetails(new String[]{"76", "Golem", "80", "120", "130", "55", "65", "45", "218", "", "ROCK", "GROUND"}); //
            loadDetails(new String[]{"77", "Ponyta", "50", "85", "55", "65", "65", "90", "82", "40", "FIRE", ""}); //
            loadDetails(new String[]{"78", "Rapidash", "65", "100", "70", "80", "80", "105", "175", "", "FIRE", ""}); //
            loadDetails(new String[]{"79", "Slowpoke", "90", "65", "65", "40", "40", "15", "63", "37", "PSYCHIC", ""}); //
            loadDetails(new String[]{"80", "Slowbro", "95", "75", "110", "100", "80", "30", "172", "", "PSYCHIC", ""}); //
        //        loadDetails(new String[]{"80M", "Slowbro (Mega Slowbro)", "95", "75", "180", "130", "80", "30", "172", "", "PSYCHIC", ""}); //
            loadDetails(new String[]{"81", "Magnemite", "25", "35", "70", "95", "55", "45", "65", "30", "ELECTRIC", "STEEL"}); //
            loadDetails(new String[]{"82", "Magneton", "50", "60", "95", "120", "70", "70", "163", "", "ELECTRIC", "STEEL"}); //
            loadDetails(new String[]{"83", "Farfetch'd", "52", "65", "55", "58", "62", "60", "123", "", "NORMAL", "FLYING"}); //
            loadDetails(new String[]{"84", "Doduo", "35", "85", "45", "35", "35", "75", "62", "31", "NORMAL", "FLYING"}); //
            loadDetails(new String[]{"85", "Dodrio", "60", "110", "70", "60", "60", "100", "161", "", "NORMAL", "FLYING"}); //
            loadDetails(new String[]{"86", "Seel", "65", "45", "55", "45", "70", "45", "65", "34", "WATER", ""}); //
            loadDetails(new String[]{"87", "Dewgong", "90", "70", "80", "70", "95", "70", "166", "", "WATER", "ICE"}); //
            loadDetails(new String[]{"88", "Grimer", "80", "80", "50", "40", "50", "25", "65", "38", "POISON", ""}); //
            loadDetails(new String[]{"89", "Muk", "105", "105", "75", "65", "100", "50", "175", "", "POISON", ""}); //
            loadDetails(new String[]{"90", "Shellder", "30", "65", "100", "45", "25", "40", "61", "WATERSTONE", "WATER", ""}); //
            loadDetails(new String[]{"91", "Cloyster", "50", "95", "180", "85", "45", "70", "184", "", "WATER", "ICE"}); //
            loadDetails(new String[]{"92", "Gastly", "30", "35", "30", "100", "35", "80", "62", "25", "GHOST", "POISON"}); //
            loadDetails(new String[]{"93", "Haunter", "45", "50", "45", "115", "55", "95", "142", "TRADE", "GHOST", "POISON"}); //
            loadDetails(new String[]{"94", "Gengar", "60", "65", "60", "130", "75", "110", "225", "", "GHOST", "POISON"}); //
        //        loadDetails(new String[]{"94M", "Gengar (Mega Gengar)", "60", "65", "80", "170", "95", "130", "225", "", "GHOST", "POISON"}); //
            loadDetails(new String[]{"95", "Onix", "35", "45", "160", "30", "45", "70", "77", "", "ROCK", "GROUND"}); //
            loadDetails(new String[]{"96", "Drowzee", "60", "48", "45", "43", "90", "42", "66", "26", "PSYCHIC", ""}); //
            loadDetails(new String[]{"97", "Hypno", "85", "73", "70", "73", "115", "67", "169", "", "PSYCHIC", ""}); //
            loadDetails(new String[]{"98", "Krabby", "30", "105", "90", "25", "25", "50", "65", "28", "WATER", ""}); //
            loadDetails(new String[]{"99", "Kingler", "55", "130", "115", "50", "50", "75", "166", "", "WATER", ""}); //
            loadDetails(new String[]{"100", "Voltorb", "40", "30", "50", "55", "55", "100", "66", "30", "ELECTRIC", ""}); //
            loadDetails(new String[]{"101", "Electrode", "60", "50", "70", "80", "80", "140", "168", "", "ELECTRIC", ""}); //
            loadDetails(new String[]{"102", "Exeggcute", "60", "40", "80", "60", "45", "40", "65", "LEAFSTONE", "GRASS", "PSYCHIC"}); //
            loadDetails(new String[]{"103", "Exeggutor", "95", "95", "85", "125", "65", "55", "182", "", "GRASS", "PSYCHIC"}); //
            loadDetails(new String[]{"104", "Cubone", "50", "50", "95", "40", "50", "35", "64", "28", "GROUND", ""}); //
            loadDetails(new String[]{"105", "Marowak", "60", "80", "110", "50", "80", "45", "149", "", "GROUND", ""}); //
            loadDetails(new String[]{"106", "Hitmonlee", "50", "120", "53", "35", "110", "87", "159", "", "FIGHTING", ""}); //
            loadDetails(new String[]{"107", "Hitmonchan", "50", "105", "79", "35", "110", "76", "159", "", "FIGHTING", ""}); //
            loadDetails(new String[]{"108", "Lickitung", "90", "55", "75", "60", "75", "30", "77", "", "NORMAL", ""}); //
            loadDetails(new String[]{"109", "Koffing", "40", "65", "95", "60", "45", "35", "68", "35", "POISON", ""}); //
            loadDetails(new String[]{"110", "Weezing", "65", "90", "120", "85", "70", "60", "172", "", "POISON", ""}); //
            loadDetails(new String[]{"111", "Rhyhorn", "80", "85", "95", "30", "30", "25", "69", "42", "GROUND", "ROCK"}); //
            loadDetails(new String[]{"112", "Rhydon", "105", "130", "120", "45", "45", "40", "170", "", "GROUND", "ROCK"}); //
            loadDetails(new String[]{"113", "Chansey", "250", "5", "5", "35", "105", "50", "395", "", "NORMAL", ""}); //
            loadDetails(new String[]{"114", "Tangela", "65", "55", "115", "100", "40", "60", "87", "", "GRASS", ""}); //
            loadDetails(new String[]{"115", "Kangaskhan", "105", "95", "80", "40", "80", "90", "172", "", "NORMAL", ""}); //
        //        loadDetails(new String[]{"115M", "Kangaskhan (Mega Kangaskhan)", "105", "125", "100", "60", "100", "100", "172", "", "NORMAL", ""}); //
            loadDetails(new String[]{"116", "Horsea", "30", "40", "70", "70", "25", "60", "59", "32", "WATER", ""}); //
            loadDetails(new String[]{"117", "Seadra", "55", "65", "95", "95", "45", "85", "154", "", "WATER", ""}); //
            loadDetails(new String[]{"118", "Goldeen", "45", "67", "60", "35", "50", "63", "64", "33", "WATER", ""}); //
            loadDetails(new String[]{"119", "Seaking", "80", "92", "65", "65", "80", "68", "158", "", "WATER", ""}); //
            loadDetails(new String[]{"120", "Staryu", "30", "45", "55", "70", "55", "85", "68", "WATERSTONE", "WATER", ""}); //
            loadDetails(new String[]{"121", "Starmie", "60", "75", "85", "100", "85", "115", "182", "", "WATER", "PSYCHIC"}); //
            loadDetails(new String[]{"122", "Mr. Mime", "40", "45", "65", "100", "120", "90", "161", "", "PSYCHIC", "FAIRY"}); //
            loadDetails(new String[]{"123", "Scyther", "70", "110", "80", "55", "80", "105", "100", "", "BUG", "FLYING"}); //
            loadDetails(new String[]{"124", "Jynx", "65", "50", "35", "115", "95", "95", "159", "", "ICE", "PSYCHIC"}); //
            loadDetails(new String[]{"125", "Electabuzz", "65", "83", "57", "95", "85", "105", "172", "", "ELECTRIC", ""}); //
            loadDetails(new String[]{"126", "Magmar", "65", "95", "57", "100", "85", "93", "173", "", "FIRE", ""}); //
            loadDetails(new String[]{"127", "Pinsir", "65", "125", "100", "55", "70", "85", "175", "A", "BUG", ""}); //
        //        loadDetails(new String[]{"127M", "Pinsir (Mega Pinsir)", "65", "155", "120", "65", "90", "105", "175", "", "BUG", "FLYING"}); //
            loadDetails(new String[]{"128", "Tauros", "75", "100", "95", "40", "70", "110", "172", "", "NORMAL", ""}); //
            loadDetails(new String[]{"129", "Magikarp", "20", "10", "55", "15", "20", "80", "40", "20", "WATER", ""}); //
            loadDetails(new String[]{"130", "Gyarados", "95", "125", "79", "60", "100", "81", "189", "", "WATER", "FLYING"}); //
        //        loadDetails(new String[]{"130M", "Gyarados (Mega Gyarados)", "95", "155", "109", "70", "130", "81", "189", "", "WATER", "DARK"}); //
            loadDetails(new String[]{"131", "Lapras", "130", "85", "80", "85", "95", "60", "187", "", "WATER", "ICE"}); //
            loadDetails(new String[]{"132", "Ditto", "48", "48", "48", "48", "48", "48", "101", "", "NORMAL", ""}); //
            loadDetails(new String[]{"133", "Eevee", "55", "55", "50", "45", "65", "55", "65", "?STONE", "NORMAL", ""}); //
            loadDetails(new String[]{"134", "Vaporeon", "130", "65", "60", "110", "95", "65", "184", "", "WATER", ""}); //
            loadDetails(new String[]{"135", "Jolteon", "65", "65", "60", "110", "95", "130", "184", "", "ELECTRIC", ""}); //
            loadDetails(new String[]{"136", "Flareon", "65", "130", "60", "95", "110", "65", "184", "", "FIRE", ""}); //
            loadDetails(new String[]{"137", "Porygon", "65", "60", "70", "85", "75", "40", "79", "", "NORMAL", ""}); //
            loadDetails(new String[]{"138", "Omanyte", "35", "40", "100", "90", "55", "35", "71", "40", "ROCK", "WATER"}); //
            loadDetails(new String[]{"139", "Omastar", "70", "60", "125", "115", "70", "55", "173", "", "ROCK", "WATER"}); //
            loadDetails(new String[]{"140", "Kabuto", "30", "80", "90", "55", "45", "55", "71", "40", "ROCK", "WATER"}); //
            loadDetails(new String[]{"141", "Kabutops", "60", "115", "105", "65", "70", "80", "173", "", "ROCK", "WATER"}); //
            loadDetails(new String[]{"142", "Aerodactyl", "80", "105", "65", "60", "75", "130", "180", "", "ROCK", "FLYING"}); //
        //        loadDetails(new String[]{"142M", "Aerodactyl (Mega Aerodactyl)", "80", "135", "85", "70", "95", "150", "180", "", "ROCK", "FLYING"}); //
            loadDetails(new String[]{"143", "Snorlax", "160", "110", "65", "65", "110", "30", "189", "", "NORMAL", ""}); //
            loadDetails(new String[]{"144", "Articuno", "90", "85", "100", "95", "125", "85", "261", "", "ICE", "FLYING"}); //
            loadDetails(new String[]{"145", "Zapdos", "90", "90", "85", "125", "90", "100", "261", "", "ELECTRIC", "FLYING"}); //
            loadDetails(new String[]{"146", "Moltres", "90", "100", "90", "125", "85", "90", "261", "", "FIRE", "FLYING"}); //
            loadDetails(new String[]{"147", "Dratini", "41", "64", "45", "50", "50", "50", "60", "30", "DRAGON", ""}); //
            loadDetails(new String[]{"148", "Dragonair", "61", "84", "65", "70", "70", "70", "147", "55", "DRAGON", ""}); //
            loadDetails(new String[]{"149", "Dragonite", "91", "134", "95", "100", "100", "80", "270", "", "DRAGON", "FLYING"}); //
            loadDetails(new String[]{"150", "Mewtwo", "106", "110", "90", "154", "90", "130", "306", "", "PSYCHIC", ""}); //
        //        loadDetails(new String[]{"150MX", "Mewtwo (Mega Mewtwo X)", "106", "190", "100", "154", "100", "130", "306", "", "PSYCHIC", "FIGHTING"}); //
        //        loadDetails(new String[]{"150MY", "Mewtwo (Mega Mewtwo Y)", "106", "150", "70", "194", "120", "140", "306", "", "PSYCHIC", ""}); //
            loadDetails(new String[]{"151", "Mew", "100", "100", "100", "100", "100", "100", "270", "", "PSYCHIC", ""}); //
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
//             * Increase or decrease in accuracy translates & converts up or down, 
             * respectively, according to the scale */

            /* Evasiveness affects the dodge timer. Dodge timer length is 5 secs */
            loadDetails(new String[]{"1", "Pound", "NORMAL", "Physical", "1", "40", "TRACKING", "null"}); //
            loadDetails(new String[]{"2", "Karate Chop", "FIGHTING", "Physical", "1", "50", "TRACKING", "High critical hit ratio."}); //
            loadDetails(new String[]{"3", "Double Slap", "NORMAL", "Physical", "1", "15", "TARGET", "Hits 2-5 times."}); //
            loadDetails(new String[]{"4", "Comet Punch", "NORMAL", "Physical", "1", "18", "TARGET", "Hits 2-5 times."}); //
            loadDetails(new String[]{"5", "Mega Punch", "NORMAL", "Physical", "2", "80", "TARGET", "null"}); //
            loadDetails(new String[]{"6", "Pay Day", "NORMAL", "Physical", "1", "40", "TARGET", "A small chance to earn an item after the battle resolves."}); //Increase travel speed
            loadDetails(new String[]{"7", "Fire Punch", "FIRE", "Physical", "2", "75", "TRACKING", "May burn opponent."}); //
            loadDetails(new String[]{"8", "Ice Punch", "ICE", "Physical", "2", "75", "TRACKING", "May freeze opponent."}); //
            loadDetails(new String[]{"9", "Thunder Punch", "ELECTRIC", "Physical", "2", "75", "TRACKING", "May paralyze opponent."}); //
            loadDetails(new String[]{"10", "Scratch", "NORMAL", "Physical", "1", "40", "TRACKING", "null"}); //
            loadDetails(new String[]{"11", "Vice Grip", "NORMAL", "Physical", "1", "55", "TRACKING", "null"}); //
            loadDetails(new String[]{"12", "Guillotine", "NORMAL", "Physical", "2", "null", "CONDITIONAL", "One-Hit-KO, if it hits."}); //Does not affect opponents faster than the user AND fails against a target Pokémon at least 30 levels above the user.
            loadDetails(new String[]{"13", "Razor Wind", "NORMAL", "Special", "2", "80", "ONSCREEN", "Slowed action time before attack. High critical hit ratio."}); //
            loadDetails(new String[]{"14", "Swords Dance", "NORMAL", "Status", "4", "null", "null", "Sharply raises user's Attack."}); //
            loadDetails(new String[]{"15", "Cut", "NORMAL", "Physical", "1", "50", "TRACKING", "null"}); //
            loadDetails(new String[]{"16", "Gust", "FLYING", "Special", "2", "40", "ONSCREEN", "Hits Pokémon using Fly/Bounce with double power. Misses Pokémon using Dig/Dive."}); //
            loadDetails(new String[]{"17", "Wing Attack", "FLYING", "Physical", "1", "60", "TRACKING", "null"}); //
            loadDetails(new String[]{"18", "Whirlwind", "NORMAL", "Status", "4", "null", "ONSCREEN", "In trainer battles, the opponent switches. In random battles, the battle ends."}); //
            loadDetails(new String[]{"19", "Fly", "FLYING", "Physical", "2", "90", "TRACKING", "Flies up, attacks. Slowed action time"}); //
            loadDetails(new String[]{"20", "Bind", "NORMAL", "Physical", "1", "15", "TARGET", "Traps opponent, damaging them for some time."}); //Damages 4-5 avg turns
            loadDetails(new String[]{"21", "Slam", "NORMAL", "Physical", "2", "80", "TARGET", "null"}); //
            loadDetails(new String[]{"22", "Vine Whip", "GRASS", "Physical", "1", "45", "TRACKING", "null"}); //
            loadDetails(new String[]{"23", "Stomp", "NORMAL", "Physical", "1", "65", "TRACKING", "May cancel opponent's action entirely."}); //
            loadDetails(new String[]{"24", "Double Kick", "FIGHTING", "Physical", "1", "30", "TRACKING", "Hits twice."}); //
            loadDetails(new String[]{"25", "Mega Kick", "NORMAL", "Physical", "2", "120", "TARGET", "null"}); //
            loadDetails(new String[]{"26", "Jump Kick", "FIGHTING", "Physical", "2", "100", "TARGET", "If it misses, the user loses half their HP"}); //Increase travel speed
            loadDetails(new String[]{"27", "Rolling Kick", "FIGHTING", "Physical", "1", "60", "TARGET", "May cancel opponent's action entirely."}); //
            loadDetails(new String[]{"28", "Sand Attack", "GROUND", "Status", "3", "null", "ONSCREEN", "Lowers opponent's Accuracy."}); //
            loadDetails(new String[]{"29", "Headbutt", "NORMAL", "Physical", "2", "70", "TRACKING", "May cancel opponent's action entirely."}); //
            loadDetails(new String[]{"30", "Horn Attack", "NORMAL", "Physical", "1", "65", "TRACKING", "null"}); //
            loadDetails(new String[]{"31", "Fury Attack", "NORMAL", "Physical", "1", "15", "TARGET", "Hits 2-5 times."}); //
            loadDetails(new String[]{"32", "Horn Drill", "NORMAL", "Physical", "2", "null", "CONDITIONAL", "One-Hit-KO, if it hits."}); //Does not affect opponents faster than the user AND fails against a target Pokémon at least 30 levels above the user.
            loadDetails(new String[]{"33", "Tackle", "NORMAL", "Physical", "1", "50", "TRACKING", "null"}); //
            loadDetails(new String[]{"34", "Body Slam", "NORMAL", "Physical", "2", "85", "TRACKING", "May paralyze opponent."}); //
            loadDetails(new String[]{"35", "Wrap", "NORMAL", "Physical", "1", "15", "TRACKING", "Traps opponent, damaging them for some time."}); //Damages 4-5 avg turns
            loadDetails(new String[]{"36", "Take Down", "NORMAL", "Physical", "2", "90", "TARGET", "User receives recoil damage."}); //
            loadDetails(new String[]{"37", "Thrash", "NORMAL", "Physical", "2", "120", "TRACKING", "User's action is set for 2-3 turns but then becomes confused."}); //
            loadDetails(new String[]{"38", "Double-Edge", "NORMAL", "Physical", "2", "120", "TRACKING", "User receives recoil damage."}); //
            loadDetails(new String[]{"39", "Tail Whip", "NORMAL", "Status", "1", "null", "TRACKING", "Lowers opponent's Defense."}); //
            loadDetails(new String[]{"40", "Poison Sting", "POISON", "Physical", "1", "15", "TRACKING", "May poison the opponent."}); //
            loadDetails(new String[]{"41", "Twineedle", "BUG", "Physical", "1", "25", "TRACKING", "Hits twice. May poison opponent."}); //
            loadDetails(new String[]{"42", "Pin Missile", "BUG", "Physical", "1", "25", "TRACKING", "Hits 2-5 times."}); //
            loadDetails(new String[]{"43", "Leer", "NORMAL", "Status", "3", "null", "ONSCREEN", "Lowers opponent's Defense."}); //
            loadDetails(new String[]{"44", "Bite", "DARK", "Physical", "1", "60", "TRACKING", "May cancel opponent's action entirely."}); //
            loadDetails(new String[]{"45", "Growl", "NORMAL", "Status", "3", "null", "ONSCREEN", "Lowers opponent's Attack."}); //
            loadDetails(new String[]{"46", "Roar", "NORMAL", "Status", "4", "null", "ONSCREEN", "In trainer battles, the opponent switches. In random battles, the battle ends."}); //
            loadDetails(new String[]{"47", "Sing", "NORMAL", "Status", "4", "null", "DEBUFF", "Puts opponent to sleep."}); //Decreased action speed, opponent MUST be in initial spot
            loadDetails(new String[]{"48", "Supersonic", "NORMAL", "Status", "3", "null", "DEBUFF", "Confuses opponent."}); //Decreased action speed, opponent MUST be in initial spot
            loadDetails(new String[]{"49", "Sonic Boom", "NORMAL", "Special", "1", "20", "ONSCREEN", "Always inflicts 20 HP."}); //
            loadDetails(new String[]{"50", "Disable", "NORMAL", "Status", "1", "null", "ONSCREEN", "Opponent can't use its last attack for sometime."}); //
            loadDetails(new String[]{"51", "Acid", "POISON", "Special", "1", "40", "TARGET", "May lower opponent's Special Defense."}); //Increase travel speed
            loadDetails(new String[]{"52", "Ember", "FIRE", "Special", "1", "40", "TARGET", "May burn opponent."}); //Increase travel speed
            loadDetails(new String[]{"53", "Flamethrower", "FIRE", "Special", "2", "90", "TARGET", "May burn opponent."}); //Increase travel speed
            loadDetails(new String[]{"54", "Mist", "ICE", "Status", "3", "null", "null", "User's stats cannot be changed for a period of time."}); //
            loadDetails(new String[]{"55", "Water Gun", "WATER", "Special", "1", "40", "TARGET", "null"}); //Increase travel speed
            loadDetails(new String[]{"56", "Hydro Pump", "WATER", "Special", "2", "110", "TARGET", "null"}); //Increase travel speed
            loadDetails(new String[]{"57", "Surf", "WATER", "Special", "2", "90", "ONSCREEN", "Hits all adjacent Pokémon."}); //
            loadDetails(new String[]{"58", "Ice Beam", "ICE", "Special", "2", "90", "TARGET", "May freeze opponent."}); //Increase travel speed
            loadDetails(new String[]{"59", "Blizzard", "ICE", "Special", "2", "110", "DEBUFF", "May freeze opponent."}); //Decreased action speed, opponent MUST be in initial spot
            loadDetails(new String[]{"60", "Psybeam", "PSYCHIC", "Special", "2", "65", "TARGET", "May confuse opponent."}); //Increase travel speed
            loadDetails(new String[]{"61", "Bubble Beam", "WATER", "Special", "2", "65", "TARGET", "May lower opponent's Speed."}); //Increase travel speed
            loadDetails(new String[]{"62", "Aurora Beam", "ICE", "Special", "2", "65", "TARGET", "May lower opponent's Attack."}); //Increase travel speed
            loadDetails(new String[]{"63", "Hyper Beam", "NORMAL", "Special", "2", "150", "TARGET", "User must take time to recharge."}); //Increase travel speed
            loadDetails(new String[]{"64", "Peck", "FLYING", "Physical", "1", "35", "TRACKING", "null"}); //
            loadDetails(new String[]{"65", "Drill Peck", "FLYING", "Physical", "2", "80", "TRACKING", "null"}); //
            loadDetails(new String[]{"66", "Submission", "FIGHTING", "Physical", "2", "80", "TARGET", "User receives recoil damage."}); //
            loadDetails(new String[]{"67", "Low Kick", "FIGHTING", "Physical", "1", "null", "TRACKING", "The heavier the opponent, the stronger the attack."}); //
            loadDetails(new String[]{"68", "Counter", "FIGHTING", "Physical", "2", "null", "TRACKING", "When hit by a Physical Attack, user strikes back with 2x power."}); //
            loadDetails(new String[]{"69", "Seismic Toss", "FIGHTING", "Physical", "1", "null", "TRACKING", "Inflicts damage equal to user's level."}); //
            loadDetails(new String[]{"70", "Strength", "NORMAL", "Physical", "2", "80", "TRACKING", "null"}); //
            loadDetails(new String[]{"71", "Absorb", "GRASS", "Special", "1", "20", "TRACKING", "User recovers half the HP inflicted on opponent."}); //
            loadDetails(new String[]{"72", "Mega Drain", "GRASS", "Special", "1", "40", "TRACKING", "User recovers half the HP inflicted on opponent."}); //
            loadDetails(new String[]{"73", "Leech Seed", "GRASS", "Status", "2", "null", "TARGET", "User steals HP from opponent each turn."}); //Increase travel speed
            loadDetails(new String[]{"74", "Growth", "NORMAL", "Status", "4", "null", "null", "Raises user's Attack and Special Attack."}); //
            loadDetails(new String[]{"75", "Razor Leaf", "GRASS", "Physical", "1", "55", "TARGET", "High critical hit ratio."}); //Increase travel speed
            loadDetails(new String[]{"76", "Solar Beam", "GRASS", "Special", "2", "120", "TARGET", "Charges first, then attacks."}); //Increase travel speed. Slow action time
            loadDetails(new String[]{"77", "Poison Powder", "POISON", "Status", "2", "null", "TARGET", "Poisons opponent."}); //Increase travel speed
            loadDetails(new String[]{"78", "Stun Spore", "GRASS", "Status", "2", "null", "TARGET", "Paralyzes opponent."}); //Increase travel speed
            loadDetails(new String[]{"79", "Sleep Powder", "GRASS", "Status", "2", "null", "TARGET", "Puts opponent to sleep."}); //Increase travel speed
            loadDetails(new String[]{"80", "Petal Dance", "GRASS", "Special", "2", "120", "ONSCREEN", "User's action is set for 2-3 turns but then becomes confused."}); //
            loadDetails(new String[]{"81", "String Shot", "BUG", "Status", "1", "null", "TARGET", "Sharply lowers opponent's Speed."}); //Increase travel speed
            loadDetails(new String[]{"82", "Dragon Rage", "DRAGON", "Special", "1", "40", "TARGET", "Always inflicts 40 HP."}); //Increase travel speed
            loadDetails(new String[]{"83", "Fire Spin", "FIRE", "Special", "1", "35", "TARGET", "Traps opponent, damaging them for some time."}); //Increase travel speed. Damages 4-5 avg turns
            loadDetails(new String[]{"84", "Thunder Shock", "ELECTRIC", "Special", "1", "40", "TRACKING", "May paralyze opponent."}); //
            loadDetails(new String[]{"85", "Thunderbolt", "ELECTRIC", "Special", "2", "90", "TRACKING", "May paralyze opponent."}); //
            loadDetails(new String[]{"86", "Thunder Wave", "ELECTRIC", "Status", "2", "null", "ONSCREEN", "Paralyzes opponent."}); //
            loadDetails(new String[]{"87", "Thunder", "ELECTRIC", "Special", "2", "110", "DEBUFF", "May paralyze opponent."}); //Decreased action speed, opponent MUST be in initial spot
            loadDetails(new String[]{"88", "Rock Throw", "ROCK", "Physical", "1", "50", "TARGET", "null"}); //Increase travel speed
            loadDetails(new String[]{"89", "Earthquake", "GROUND", "Physical", "2", "100", "ONSCREEN", "Power is doubled if opponent is underground from using Dig."}); //
            loadDetails(new String[]{"90", "Fissure", "GROUND", "Physical", "2", "null", "CONDITIONAL", "One-Hit-KO, if it hits."}); //Can hit Pokémon of any level, but does not affect Pokémon faster than the user.
            loadDetails(new String[]{"91", "Dig", "GROUND", "Physical", "2", "80", "TRACKING", "Digs underground, then attacks."}); //
            loadDetails(new String[]{"92", "Toxic", "POISON", "Status", "2", "null", "TARGET", "Badly poisons opponent."}); //Increase travel speed
            loadDetails(new String[]{"93", "Confusion", "PSYCHIC", "Special", "1", "50", "ONSCREEN", "May confuse opponent."}); //
            loadDetails(new String[]{"94", "Psychic", "PSYCHIC", "Special", "2", "90", "ONSCREEN", "May lower opponent's Special Defense."}); //
            loadDetails(new String[]{"95", "Hypnosis", "PSYCHIC", "Status", "2", "null", "DEBUFF", "Puts opponent to sleep."}); //Decreased action speed, opponent MUST be in initial spot
            loadDetails(new String[]{"96", "Meditate", "PSYCHIC", "Status", "3", "null", "null", "Raises user's Attack."}); //
            loadDetails(new String[]{"97", "Agility", "PSYCHIC", "Status", "4", "null", "null", "Sharply raises user's Speed."}); //
            loadDetails(new String[]{"98", "Quick Attack", "NORMAL", "Physical", "1", "40", "TRACKING", "User attacks fast."}); //
            loadDetails(new String[]{"99", "Rage", "NORMAL", "Physical", "1", "20", "TRACKING", "Raises user's Attack when hit."}); //
            loadDetails(new String[]{"100", "Teleport", "PSYCHIC", "Status", "3", "null", "null", "Ends random battles; also dodges attacks during trainer battles. Cannot use consecutively. Lowers evasiveness"}); //Slows users dodge time, permanently
            loadDetails(new String[]{"101", "Night Shade", "GHOST", "Special", "1", "null", "ONSCREEN", "Inflicts damage equal to user's level."}); //
            loadDetails(new String[]{"102", "Mimic", "NORMAL", "Status", "3", "null", "null", "Copies the opponent's last move."}); //
            loadDetails(new String[]{"103", "Screech", "NORMAL", "Status", "4", "null", "TARGET", "Sharply lowers opponent's Defense."}); //
            loadDetails(new String[]{"104", "Double Team", "NORMAL", "Status", "4", "null", "null", "Raises user's Evasiveness."}); //
            loadDetails(new String[]{"105", "Recover", "NORMAL", "Status", "4", "null", "null", "User recovers half its max HP."}); //
            loadDetails(new String[]{"106", "Harden", "NORMAL", "Status", "3", "null", "null", "Raises user's Defense."}); //
            loadDetails(new String[]{"107", "Minimize", "NORMAL", "Status", "4", "null", "null", "Sharply raises user's Evasiveness."}); //
            loadDetails(new String[]{"108", "Smokescreen", "NORMAL", "Status", "2", "null", "ONSCREEN", "Lowers opponent's Accuracy."}); //
            loadDetails(new String[]{"109", "Confuse Ray", "GHOST", "Status", "2", "null", "TRACKING", "Confuses opponent."}); //
            loadDetails(new String[]{"110", "Withdraw", "WATER", "Status", "3", "null", "null", "Raises user's Defense."}); //
            loadDetails(new String[]{"111", "Defense Curl", "NORMAL", "Status", "3", "null", "null", "Raises user's Defense."}); //
            loadDetails(new String[]{"112", "Barrier", "PSYCHIC", "Status", "4", "null", "null", "Sharply raises user's Defense."}); //
            loadDetails(new String[]{"113", "Light Screen", "PSYCHIC", "Status", "4", "null", "null", "Halves damage from Special attacks for some time."}); //Take 0.5 SpA damage for 5 avg turns
            loadDetails(new String[]{"114", "Haze", "ICE", "Status", "4", "null", "null", "Resets all stat changes."}); //
            loadDetails(new String[]{"115", "Reflect", "PSYCHIC", "Status", "4", "null", "null", "Halves damage from Physical attacks for some time."}); //Take 0.5 Atk damage for 5 avg turns
            loadDetails(new String[]{"116", "Focus Energy", "NORMAL", "Status", "4", "null", "null", "Increases critical hit ratio."}); //
            loadDetails(new String[]{"117", "Bide", "NORMAL", "Physical", "4", "null", "TRACKING", "User takes damage for some time, then strikes back double."}); //Takes damage for 2 avg turns
            loadDetails(new String[]{"118", "Metronome", "NORMAL", "Status", "3", "null", "null", "User performs any move in the game at random."}); //
            loadDetails(new String[]{"119", "Mirror Move", "FLYING", "Status", "3", "null", "null", "User performs the opponent's last move."}); //
            loadDetails(new String[]{"120", "Self-Destruct", "NORMAL", "Physical", "4", "200", "ONSCREEN", "User faints."}); //
            loadDetails(new String[]{"121", "Egg Bomb", "NORMAL", "Physical", "2", "100", "TARGET", "null"}); //Increase travel speed
            loadDetails(new String[]{"122", "Lick", "GHOST", "Physical", "1", "30", "TRACKING", "May paralyze opponent."}); //
            loadDetails(new String[]{"123", "Smog", "POISON", "Special", "1", "30", "DEBUFF", "May poison opponent."}); //Decreased action speed, opponent MUST be in initial spot
            loadDetails(new String[]{"124", "Sludge", "POISON", "Special", "2", "65", "TARGET", "May poison opponent."}); //Increase travel speed
            loadDetails(new String[]{"125", "Bone Club", "GROUND", "Physical", "1", "65", "TARGET", "May cancel opponent's action entirely."}); //
            loadDetails(new String[]{"126", "Fire Blast", "FIRE", "Special", "2", "110", "TARGET", "null"}); //
            loadDetails(new String[]{"127", "Waterfall", "WATER", "Physical", "2", "80", "ONSCREEN", "May cancel opponent's action entirely."}); //
            loadDetails(new String[]{"128", "Clamp", "WATER", "Physical", "1", "35", "TARGET", "Traps opponent, damaging them for 4-5 turns."}); //
            loadDetails(new String[]{"129", "Swift", "NORMAL", "Special", "1", "60", "ONSCREEN", "Ignores Accuracy and Evasiveness."}); //
            loadDetails(new String[]{"130", "Skull Bash", "NORMAL", "Physical", "2", "130", "TRACKING", "Raises Defense on first turn, attacks on second."}); //
            loadDetails(new String[]{"131", "Spike Cannon", "NORMAL", "Physical", "1", "20", "TARGET", "Hits 2-5 times in one turn."}); //Increase travel speed
            loadDetails(new String[]{"132", "Constrict", "NORMAL", "Physical", "1", "10", "TRACKING", "May lower opponent's Speed by one stage."}); //
            loadDetails(new String[]{"133", "Amnesia", "PSYCHIC", "Status", "4", "null", "null", "Sharply raises user's Special Defense."}); //
            loadDetails(new String[]{"134", "Kinesis", "PSYCHIC", "Status", "1", "null", "ONSCREEN", "Lowers opponent's Accuracy."}); //
            loadDetails(new String[]{"135", "Soft-Boiled", "NORMAL", "Status", "4", "null", "null", "User recovers half its max HP."}); //
            loadDetails(new String[]{"136", "High Jump Kick", "FIGHTING", "Physical", "2", "130", "TARGET", "If it misses, the user loses half their HP."}); //Increase travel speed
            loadDetails(new String[]{"137", "Glare", "NORMAL", "Status", "2", "null", "ONSCREEN", "Paralyzes opponent."}); //
            loadDetails(new String[]{"138", "Dream Eater", "PSYCHIC", "Special", "2", "100", "ONSCREEN", "User recovers half the HP inflicted on a sleeping opponent."}); //
            loadDetails(new String[]{"139", "Poison Gas", "POISON", "2", "40", "null", "ONSCREEN", "Poisons opponent."}); //
            loadDetails(new String[]{"140", "Barrage", "NORMAL", "Physical", "1", "15", "TARGET", "Hits 2-5 times."}); //Increase travel speed
            loadDetails(new String[]{"141", "Leech Life", "BUG", "Physical", "1", "20", "TRACKING", "User recovers half the HP inflicted on opponent."}); //
            loadDetails(new String[]{"142", "Lovely Kiss", "NORMAL", "Status", "1", "null", "TARGET", "Puts opponent to sleep."}); //
            loadDetails(new String[]{"143", "Sky Attack", "FLYING", "Physical", "2", "140", "TRACKING", "Charges first, then attacks. May cancel opponent's action entirely."}); //Slowed action time
            loadDetails(new String[]{"144", "Transform", "NORMAL", "Status", "3", "null", "null", "User takes on the form and attacks of the opponent."}); //
            loadDetails(new String[]{"145", "Bubble", "WATER", "Special", "2", "40", "TRACKING", "May lower opponent's Speed."}); //
            loadDetails(new String[]{"146", "Dizzy Punch", "NORMAL", "Physical", "2", "70", "TRACKING", "May confuse opponent."}); //
            loadDetails(new String[]{"147", "Spore", "GRASS", "Status", "2", "null", "TARGET", "Puts opponent to sleep."}); //Increase travel speed
            loadDetails(new String[]{"148", "Flash", "NORMAL", "Status", "3", "null", "ONSCREEN", "Lowers opponent's Accuracy."}); //
            loadDetails(new String[]{"149", "Psywave", "PSYCHIC", "Special", "2", "null", "TARGET", "Inflicts damage 50-150% of user's level."}); //Increase travel speed
            loadDetails(new String[]{"150", "Splash", "NORMAL", "Status", "3", "null", "null", "Doesn't do ANYTHING."}); //
            loadDetails(new String[]{"151", "Acid Armor", "POISON", "Status", "4", "null", "null", "Sharply raises user's Defense."}); //
            loadDetails(new String[]{"152", "Crabhammer", "WATER", "Physical", "2", "100", "TRACKING", "High critical hit ratio."}); //
            loadDetails(new String[]{"153", "Explosion", "NORMAL", "Physical", "4", "250", "ONSCREEN", "User faints."}); //
            loadDetails(new String[]{"154", "Fury Swipes", "NORMAL", "Physical", "1", "18", "TARGET", "Hits 2-5 times."}); //
            loadDetails(new String[]{"155", "Bonemerang", "GROUND", "Physical", "1", "50", "TRACKING", "Hits twice."}); //
            loadDetails(new String[]{"156", "Rest", "PSYCHIC", "Status", "4", "null", "null", "User sleeps for some time, but user is fully healed."}); //Sleeps for 2 avg turns
            loadDetails(new String[]{"157", "ROCK Slide", "ROCK", "Physical", "2", "75", "TRACKING", "May cancel opponent's action entirely."}); //
            loadDetails(new String[]{"158", "Hyper Fang", "NORMAL", "Physical", "2", "80", "TRACKING", "May cancel opponent's action entirely."}); //
            loadDetails(new String[]{"159", "Sharpen", "NORMAL", "Status", "3", "null", "null", "Raises user's Attack."}); //
            loadDetails(new String[]{"160", "Conversion", "NORMAL", "Status", "3", "null", "null", "Changes user's type to that of its first move."}); //
            loadDetails(new String[]{"161", "Tri Attack", "NORMAL", "Special", "2", "80", "TARGET", "May paralyze, burn or freeze opponent."}); //Increase travel speed
            loadDetails(new String[]{"162", "Super Fang", "NORMAL", "Physical", "1", "null", "TRACKING", "Always takes off half of the opponent's HP."}); //
            loadDetails(new String[]{"163", "Slash", "NORMAL", "Physical", "2", "70", "TRACKING", "High critical hit ratio."}); //
            loadDetails(new String[]{"164", "Substitute", "NORMAL", "Status", "4", "null", "null", "Uses HP to creates a decoy that takes hits."}); //
            loadDetails(new String[]{"165", "Struggle", "NORMAL", "Physical", "null", "50", "null", "Hurts the user."}); //
            editor.putBoolean("setState", true);
            editor.commit();
        }
    }
    
    private final class LearnedMoves extends Activity {
        private static final String LEARNED_LIST = "learningSet";

        LearnedMoves(Context context){
            initialize(context);
        }

        LearnedMoves(Context context, AttributeSet attribs){
            initialize(context);
        }

        LearnedMoves(Context context, AttributeSet attribs, int defStyle){
            initialize(context);
        }

        void initialize(Context context){
            editor = context.getSharedPreferences(LEARNED_LIST, Context.MODE_PRIVATE).edit();

            //{"Dex#", {"Learned_Move_Lvl", "Move#"}*}
            loadDetails(new String[]{"1", "1", "33", "3", "45", "7", "73", "9", "22", "13", "77", "13", "79", "15", "36", "19", "75", "25", "74", "27", "38"});
            loadDetails(new String[]{"2", "1", "45", "1", "73", "1", "33", "3", "45", "7", "73", "9", "22", "13", "77", "13", "79", "15", "36", "20", "75", "28", "74", "31", "38", "44", "76"});
            loadDetails(new String[]{"3", "1", "45", "1", "73", "1", "33", "1", "22", "3", "45", "7", "73", "9", "22", "13", "77", "13", "79", "15", "36", "20", "75", "28", "74", "31", "38", "32", "80", "53", "76"});
            loadDetails(new String[]{"4", "1", "45", "1", "10", "7", "52", "10", "108", "16", "82", "34", "163", "37", "53", "43", "83"});
            loadDetails(new String[]{"5", "1", "52", "1", "45", "1", "10", "7", "52", "10", "108", "17", "82", "39", "163", "43", "53", "50", "83"});
            loadDetails(new String[]{"6", "1", "52", "1", "45", "1", "10", "1", "108", "7", "52", "10", "108", "17", "82", "36", "17", "41", "163", "47", "53", "56", "83"});
            loadDetails(new String[]{"7", "1", "33", "4", "39", "7", "55", "10", "110", "13", "145", "16", "44", "31", "130", "40", "56"});
            loadDetails(new String[]{"8", "1", "33", "1", "39", "1", "55", "4", "39", "7", "55", "10", "110", "13", "145", "16", "44", "36", "130", "48", "56"});
            loadDetails(new String[]{"9", "1", "33", "1", "39", "1", "55", "1", "110", "4", "39", "7", "55", "10", "110", "13", "145", "16", "44", "39", "130", "60", "56"});
            loadDetails(new String[]{"10", "1", "81", "1", "33"});
            loadDetails(new String[]{"11", "1", "106", "7", "106"});
            loadDetails(new String[]{"12", "1", "93", "10", "93", "12", "77", "12", "79", "12", "78", "16", "16", "18", "48", "22", "18", "24", "60"});
            loadDetails(new String[]{"13", "1", "40", "1", "81"});
            loadDetails(new String[]{"14", "1", "106", "7", "106"});
            loadDetails(new String[]{"15", "1", "31", "10", "31", "13", "116", "16", "41", "19", "99", "28", "42", "31", "97"});
            loadDetails(new String[]{"16", "1", "33", "5", "28", "9", "16", "13", "98", "17", "18", "29", "97", "33", "17", "45", "119"});
            loadDetails(new String[]{"17", "1", "16", "1", "28", "1", "33", "5", "28", "9", "16", "13", "98", "17", "18", "32", "97", "37", "17", "52", "119"});
            loadDetails(new String[]{"18", "1", "16", "1", "98", "1", "28", "1", "33", "5", "28", "9", "16", "13", "98", "17", "18", "32", "97", "38", "17", "56", "119"});
            loadDetails(new String[]{"19", "1", "33", "1", "39", "4", "98", "7", "116", "10", "44", "16", "158", "28", "162", "31", "38"});
            loadDetails(new String[]{"20", "1", "116", "1", "98", "1", "14", "1", "33", "1", "39", "4", "98", "7", "116", "10", "44", "16", "158", "34", "162", "39", "38"});
            loadDetails(new String[]{"21", "1", "45", "1", "64", "5", "43", "9", "31", "21", "119", "25", "97", "37", "65"});
            loadDetails(new String[]{"22", "1", "31", "1", "45", "1", "43", "1", "64", "5", "43", "9", "31", "23", "119", "29", "97", "47", "65"});
            loadDetails(new String[]{"23", "1", "43", "1", "35", "4", "40", "9", "44", "12", "137", "17", "103", "20", "51", "41", "114"});
            loadDetails(new String[]{"24", "1", "44", "1", "43", "1", "40", "1", "35", "4", "40", "9", "44", "12", "137", "17", "103", "20", "51", "51", "114"});
            loadDetails(new String[]{"25", "1", "39", "1", "84", "5", "45", "10", "98", "18", "86", "23", "104", "37", "21", "42", "85", "45", "97", "53", "113", "58", "87"});
            loadDetails(new String[]{"26", "1", "98", "1", "39", "1", "84", "1", "85"});
            loadDetails(new String[]{"27", "1", "111", "1", "10", "3", "28", "5", "40", "17", "129", "20", "154", "26", "163", "30", "91", "38", "14", "46", "89"});
            loadDetails(new String[]{"28", "1", "111", "1", "40", "1", "28", "1", "10", "3", "28", "5", "40", "17", "129", "20", "154", "28", "163", "33", "91", "43", "14", "53", "89"});
            loadDetails(new String[]{"29", "1", "45", "1", "10", "7", "39", "9", "24", "13", "40", "19", "154", "21", "44"});
            loadDetails(new String[]{"30", "1", "45", "1", "10", "7", "39", "9", "24", "13", "40", "20", "154", "23", "44"});
            loadDetails(new String[]{"31", "1", "24", "1", "40", "1", "10", "1", "39", "35", "34"});
            loadDetails(new String[]{"32", "1", "43", "1", "64", "7", "116", "9", "24", "13", "40", "19", "31", "21", "30", "45", "32"});
            loadDetails(new String[]{"33", "1", "43", "1", "64", "7", "116", "9", "24", "13", "40", "20", "31", "23", "30", "58", "32"});
            loadDetails(new String[]{"34", "1", "24", "1", "116", "1", "64", "1", "40", "35", "37"});
            loadDetails(new String[]{"35", "1", "45", "1", "1", "7", "47", "10", "3", "13", "111", "25", "107", "31", "118", "40", "34"});
            loadDetails(new String[]{"36", "1", "3", "1", "118", "1", "107", "1", "47"});
            loadDetails(new String[]{"37", "1", "52", "4", "39", "7", "46", "10", "98", "12", "109", "15", "83", "36", "53", "42", "126"});
            loadDetails(new String[]{"39", "1", "47", "3", "111", "5", "1", "15", "50", "18", "3", "32", "156", "35", "34", "37", "102", "49", "38"});
            loadDetails(new String[]{"40", "1", "111", "1", "50", "1", "38", "1", "3", "1", "47"});
            loadDetails(new String[]{"41", "1", "141", "5", "48", "11", "44", "13", "17", "17", "109", "23", "129", "35", "114"});
            loadDetails(new String[]{"42", "1", "44", "1", "141", "1", "103", "1", "48", "5", "48", "11", "44", "13", "17", "17", "109", "24", "129", "40", "114"});
            loadDetails(new String[]{"43", "1", "71", "9", "51", "13", "77", "14", "78", "15", "79", "19", "72", "35", "92", "51", "80"});
            loadDetails(new String[]{"44", "1", "71", "1", "51", "9", "51", "13", "77", "14", "78", "15", "79", "19", "72", "39", "92", "59", "80"});
            loadDetails(new String[]{"45", "1", "72", "1", "77", "1", "78", "59", "80", "64", "76"});
            loadDetails(new String[]{"46", "1", "10", "6", "77", "6", "78", "11", "141", "22", "147", "27", "163", "33", "74"});
            loadDetails(new String[]{"47", "1", "141", "1", "77", "1", "10", "1", "78", "6", "77", "6", "78", "11", "141", "22", "147", "29", "163", "37", "74"});
            loadDetails(new String[]{"48", "1", "50", "1", "33", "5", "48", "11", "93", "13", "77", "17", "141", "23", "78", "25", "60", "29", "79", "47", "94"});
            loadDetails(new String[]{"49", "1", "50", "1", "48", "1", "33", "5", "48", "11", "93", "13", "77", "17", "141", "23", "78", "25", "60", "29", "79", "31", "16", "55", "94"});
            loadDetails(new String[]{"50", "1", "28", "1", "10", "4", "45", "34", "91", "37", "163", "40", "89", "45", "90"});
            loadDetails(new String[]{"51", "1", "45", "1", "28", "1", "10", "1", "161", "4", "45", "40", "91", "45", "163", "50", "89", "57", "90"});
            loadDetails(new String[]{"52", "1", "45", "1", "10", "6", "44", "14", "154", "17", "103", "30", "6", "33", "163"});
            loadDetails(new String[]{"53", "1", "44", "1", "45", "1", "10", "6", "44", "14", "154", "17", "103", "28", "129", "37", "163"});
            loadDetails(new String[]{"54", "1", "10", "4", "39", "8", "55", "11", "93", "15", "154", "22", "50", "25", "103", "43", "133", "46", "56"});
            loadDetails(new String[]{"55", "1", "10", "1", "39", "1", "55", "4", "39", "8", "55", "11", "93", "15", "154", "22", "50", "29", "103", "49", "133", "54", "56"});
            loadDetails(new String[]{"56", "1", "116", "1", "43", "1", "67", "1", "10", "9", "154", "13", "2", "17", "69", "21", "103", "41", "37"});
            loadDetails(new String[]{"57", "1", "116", "1", "43", "1", "67", "1", "10", "9", "154", "13", "2", "17", "69", "21", "103", "28", "99", "47", "37"});
            loadDetails(new String[]{"58", "1", "44", "1", "46", "6", "52", "8", "43", "23", "36", "30", "97", "34", "53"});
            loadDetails(new String[]{"59", "1", "44", "1", "46"});
            loadDetails(new String[]{"60", "5", "55", "8", "95", "11", "145", "15", "3", "21", "34", "25", "61", "38", "56"});
            loadDetails(new String[]{"61", "1", "95", "1", "55", "5", "55", "8", "95", "11", "145", "15", "3", "21", "34", "27", "61", "48", "56"});
            loadDetails(new String[]{"62", "1", "61", "1", "3", "1", "95", "1", "66"});
            loadDetails(new String[]{"63", "1", "100"});
            loadDetails(new String[]{"64", "1", "93", "1", "134", "1", "100", "16", "93", "18", "50", "21", "60", "26", "115", "31", "105", "38", "94"});
            loadDetails(new String[]{"65", "1", "93", "1", "134", "1", "100", "16", "93", "18", "50", "21", "60", "26", "115", "31", "105", "38", "94"});
            loadDetails(new String[]{"66", "1", "43", "1", "67", "3", "116", "7", "2", "15", "69", "33", "66"});
            loadDetails(new String[]{"67", "1", "116", "1", "2", "1", "43", "1", "67", "3", "116", "7", "2", "15", "69", "37", "66"});
            loadDetails(new String[]{"68", "1", "116", "1", "2", "1", "43", "1", "67", "3", "116", "7", "2", "15", "69", "37", "66"});
            loadDetails(new String[]{"69", "1", "22", "7", "74", "11", "35", "13", "79", "15", "77", "17", "78", "23", "51", "39", "75", "41", "21"});
            loadDetails(new String[]{"70", "1", "74", "1", "22", "1", "35", "7", "74", "11", "35", "13", "79", "15", "77", "17", "78", "23", "51", "39", "75", "41", "21"});
            loadDetails(new String[]{"71", "1", "75", "1", "79", "1", "22"});
            loadDetails(new String[]{"72", "1", "40", "4", "48", "7", "132", "10", "51", "19", "35", "25", "61", "28", "112", "37", "103", "46", "56"});
            loadDetails(new String[]{"73", "1", "51", "1", "132", "1", "40", "1", "48", "4", "48", "7", "132", "10", "51", "19", "35", "25", "61", "28", "112", "40", "103", "52", "56"});
            loadDetails(new String[]{"74", "1", "111", "1", "33", "16", "88", "24", "120", "34", "89", "36", "153", "40", "38"});
            loadDetails(new String[]{"75", "1", "111", "1", "33", "16", "88", "24", "120", "40", "89", "44", "153", "50", "38"});
            loadDetails(new String[]{"76", "1", "111", "1", "33", "16", "88", "24", "120", "40", "89", "44", "153", "50", "38"});
            loadDetails(new String[]{"77", "1", "45", "1", "33", "4", "39", "9", "52", "17", "23", "25", "83", "29", "36", "37", "97", "41", "126"});
            loadDetails(new String[]{"78", "1", "52", "1", "45", "1", "98", "1", "39", "4", "39", "9", "52", "17", "23", "25", "83", "29", "36", "37", "97", "40", "31", "41", "126"});
            loadDetails(new String[]{"79", "1", "33", "5", "45", "9", "55", "14", "93", "19", "50", "23", "29", "41", "133", "45", "94"});
            loadDetails(new String[]{"80", "1", "45", "1", "33", "5", "45", "9", "55", "14", "93", "19", "50", "23", "29", "37", "110", "43", "133", "49", "94"});
            loadDetails(new String[]{"81", "1", "33", "5", "48", "7", "84", "11", "49", "13", "86", "35", "103"});
            loadDetails(new String[]{"82", "1", "49", "1", "48", "1", "33", "1", "84", "5", "48", "7", "84", "11", "49", "13", "86", "30", "161", "39", "103"});
            loadDetails(new String[]{"83", "1", "43", "1", "64", "1", "28", "7", "31", "19", "163", "25", "14", "31", "97"});
            loadDetails(new String[]{"84", "1", "45", "1", "64", "5", "98", "9", "99", "13", "31", "33", "97", "37", "65", "49", "37"});
            loadDetails(new String[]{"85", "1", "45", "1", "64", "1", "98", "1", "99", "5", "98", "9", "99", "13", "31", "25", "161", "35", "97", "41", "65", "59", "37"});
            loadDetails(new String[]{"86", "1", "29", "3", "45", "21", "156", "27", "62", "37", "36", "47", "58"});
            loadDetails(new String[]{"87", "1", "45", "1", "29", "3", "45", "21", "156", "27", "62", "39", "36", "55", "58"});
            loadDetails(new String[]{"88", "1", "139", "1", "1", "4", "106", "12", "50", "15", "124", "21", "107", "37", "103", "43", "151"});
            loadDetails(new String[]{"89", "1", "106", "1", "139", "1", "1", "4", "106", "12", "50", "15", "124", "21", "107", "37", "103", "46", "151"});
            loadDetails(new String[]{"90", "1", "33", "4", "110", "8", "48", "20", "43", "25", "128", "37", "62", "52", "58", "61", "56"});
            loadDetails(new String[]{"91", "1", "62", "1", "56", "1", "48", "1", "110", "13", "131"});
            loadDetails(new String[]{"92", "1", "95", "1", "122", "15", "101", "19", "109", "33", "138"});
            loadDetails(new String[]{"93", "1", "95", "1", "122", "15", "101", "19", "109", "39", "138"});
            loadDetails(new String[]{"94", "1", "95", "1", "122", "15", "101", "19", "109", "39", "138"});
            loadDetails(new String[]{"95", "1", "20", "1", "106", "1", "33", "7", "88", "13", "99", "28", "21", "31", "103", "34", "157", "43", "91", "49", "38"});
            loadDetails(new String[]{"96", "1", "95", "1", "1", "5", "50", "9", "93", "13", "29", "17", "139", "21", "96", "25", "60", "29", "29", "49", "94"});
            loadDetails(new String[]{"97", "1", "93", "1", "50", "1", "95", "1", "1", "5", "50", "9", "93", "13", "29", "17", "139", "21", "96", "25", "60", "29", "29", "49", "94"});
            loadDetails(new String[]{"98", "1", "145", "5", "11", "9", "43", "11", "106", "15", "61", "25", "23", "31", "12", "35", "21", "41", "152"});
            loadDetails(new String[]{"99", "1", "145", "1", "43", "1", "11", "5", "11", "9", "43", "11", "106", "15", "61", "25", "23", "37", "12", "44", "21", "56", "152"});
            loadDetails(new String[]{"100", "1", "33", "4", "49", "13", "103", "20", "129", "26", "120", "29", "113", "41", "153"});
            loadDetails(new String[]{"101", "1", "49", "1", "33", "4", "49", "13", "103", "20", "129", "26", "120", "29", "113", "47", "153"});
            loadDetails(new String[]{"102", "1", "140", "1", "95", "7", "115", "11", "73", "19", "78", "21", "77", "23", "79", "27", "93", "43", "76"});
            loadDetails(new String[]{"103", "1", "140", "1", "93", "1", "95", "1", "23", "27", "121"});
            loadDetails(new String[]{"104", "1", "45", "3", "39", "7", "125", "11", "29", "13", "43", "17", "116", "21", "155", "23", "99", "31", "37", "43", "38"});
            loadDetails(new String[]{"105", "1", "125", "1", "45", "1", "29", "1", "39", "3", "39", "7", "125", "11", "29", "13", "43", "17", "116", "21", "155", "23", "99", "33", "37", "53", "38"});
            loadDetails(new String[]{"106", "1", "24", "1", "25", "5", "96", "9", "27", "13", "26", "21", "116", "29", "136", "53", "25"});
            loadDetails(new String[]{"107", "1", "4", "1", "68", "6", "97", "36", "7", "36", "8", "36", "9", "46", "5", "61", "68"});
            loadDetails(new String[]{"108", "1", "122", "5", "48", "9", "111", "17", "35", "21", "23", "25", "50", "29", "21", "49", "103"});
            loadDetails(new String[]{"109", "1", "139", "1", "33", "4", "123", "7", "108", "18", "124", "23", "120", "26", "114", "37", "153"});
            loadDetails(new String[]{"110", "1", "139", "1", "123", "1", "108", "1", "33", "4", "123", "7", "108", "18", "124", "23", "120", "26", "114", "40", "153"});
            loadDetails(new String[]{"111", "1", "30", "1", "39", "5", "31", "17", "23", "37", "36", "45", "89", "53", "32"});
            loadDetails(new String[]{"112", "1", "31", "1", "30", "1", "32", "1", "39", "5", "31", "17", "23", "37", "36", "48", "89", "62", "32"});
            loadDetails(new String[]{"113", "1", "111", "1", "38", "1", "45", "1", "1", "5", "39", "12", "3", "16", "135", "23", "107", "27", "36", "31", "47", "42", "121", "46", "113", "54", "38"});
            loadDetails(new String[]{"114", "1", "132", "4", "79", "7", "22", "10", "71", "14", "77", "17", "20", "20", "74", "23", "72", "30", "78", "41", "21"});
            loadDetails(new String[]{"115", "1", "4", "1", "43", "10", "39", "13", "44", "22", "99", "25", "5", "34", "146"});
            loadDetails(new String[]{"116", "1", "145", "5", "108", "9", "43", "13", "55", "21", "61", "26", "116", "36", "97", "52", "56"});
            loadDetails(new String[]{"117", "1", "145", "1", "56", "1", "43", "1", "108", "1", "55", "5", "108", "9", "43", "13", "55", "21", "61", "26", "116", "38", "97", "60", "56"});
            loadDetails(new String[]{"118", "1", "64", "1", "39", "5", "48", "8", "30", "24", "31", "29", "97", "32", "127", "37", "32"});
            loadDetails(new String[]{"119", "1", "64", "1", "48", "1", "39", "5", "48", "8", "30", "24", "31", "29", "97", "32", "127", "40", "32"});
            loadDetails(new String[]{"120", "1", "106", "1", "33", "4", "55", "10", "105", "13", "149", "16", "129", "18", "61", "31", "107", "40", "109", "42", "94", "46", "113", "53", "56"});
            loadDetails(new String[]{"121", "1", "56", "1", "105", "1", "129", "1", "55", "40", "109"});
            loadDetails(new String[]{"122", "1", "112", "1", "93", "8", "96", "11", "3", "15", "102", "15", "149", "22", "113", "22", "115", "25", "60", "29", "164", "39", "94"});
            loadDetails(new String[]{"123", "1", "43", "1", "98", "5", "116", "17", "97", "21", "17", "29", "163", "33", "13", "37", "104", "57", "14"});
            loadDetails(new String[]{"124", "1", "122", "1", "142", "1", "1", "5", "122", "8", "142", "15", "3", "18", "8", "44", "34", "60", "59"});
            loadDetails(new String[]{"125", "1", "43", "1", "98", "1", "84", "5", "84", "8", "67", "12", "129", "19", "86", "26", "113", "29", "9", "42", "103", "49", "85", "55", "87"});
            loadDetails(new String[]{"126", "1", "52", "1", "43", "1", "123", "5", "52", "8", "108", "15", "83", "26", "109", "29", "7", "49", "53", "55", "126"});
            loadDetails(new String[]{"127", "1", "116", "1", "11", "4", "20", "8", "69", "11", "106", "29", "66", "40", "14", "43", "37", "50", "12"});
            loadDetails(new String[]{"128", "1", "33", "3", "39", "5", "99", "8", "30", "19", "156", "41", "36", "50", "37"});
            loadDetails(new String[]{"129", "1", "150", "15", "33"});
            loadDetails(new String[]{"130", "1", "37", "20", "44", "23", "82", "26", "43", "44", "56", "50", "63"});
            loadDetails(new String[]{"131", "1", "45", "1", "47", "1", "55", "4", "54", "7", "109", "18", "34", "32", "58", "47", "56"});
            loadDetails(new String[]{"132", "1", "144"});
            loadDetails(new String[]{"133", "1", "45", "1", "33", "1", "39", "5", "28", "10", "129", "13", "98", "17", "44", "25", "36", "37", "38"});
            loadDetails(new String[]{"134", "1", "33", "1", "39", "5", "28", "9", "55", "13", "98", "20", "62", "29", "151", "33", "114", "45", "56"});
            loadDetails(new String[]{"135", "1", "33", "1", "39", "5", "28", "9", "84", "13", "98", "17", "24", "25", "42", "29", "97", "33", "86", "45", "87"});
            loadDetails(new String[]{"136", "1", "33", "1", "39", "5", "28", "9", "52", "13", "98", "17", "44", "25", "83", "33", "123"});
            loadDetails(new String[]{"137", "1", "160", "1", "159", "1", "33", "7", "60", "12", "97", "18", "105", "50", "161"});
            loadDetails(new String[]{"138", "1", "132", "1", "110", "7", "44", "10", "55", "19", "43", "55", "56"});
            loadDetails(new String[]{"139", "1", "44", "1", "132", "1", "56", "1", "110", "7", "44", "10", "55", "19", "43", "40", "131", "75", "56"});
            loadDetails(new String[]{"140", "1", "106", "1", "10", "6", "71", "11", "43", "21", "28", "36", "72"});
            loadDetails(new String[]{"141", "1", "71", "1", "106", "1", "43", "1", "10", "6", "71", "11", "43", "21", "28", "36", "72", "40", "163"});
            loadDetails(new String[]{"142", "1", "44", "1", "48", "1", "17", "9", "46", "17", "97", "41", "36", "65", "63", "73", "157"});
            loadDetails(new String[]{"143", "1", "33", "4", "111", "9", "133", "12", "122", "25", "34", "28", "156"});
            loadDetails(new String[]{"144", "1", "16", "8", "54", "36", "97", "43", "58", "50", "115", "71", "59"});
            loadDetails(new String[]{"145", "1", "65", "1", "64", "1", "84", "8", "86", "43", "97", "64", "113", "71", "65", "78", "87"});
            loadDetails(new String[]{"146", "1", "52", "1", "143", "1", "17", "8", "83", "15", "97", "36", "53", "71", "76", "78", "143"});
            loadDetails(new String[]{"147", "1", "43", "1", "35", "5", "86", "15", "82", "21", "21", "25", "97", "61", "63"});
            loadDetails(new String[]{"148", "1", "43", "1", "86", "1", "35", "5", "86", "15", "82", "21", "21", "25", "97", "75", "63"});
            loadDetails(new String[]{"149", "1", "7", "1", "43", "1", "9", "1", "86", "1", "35", "5", "86", "15", "82", "21", "21", "25", "97", "55", "17", "75", "63"});
            loadDetails(new String[]{"150", "1", "93", "1", "50", "8", "129", "50", "105", "57", "94", "64", "112", "79", "133", "86", "54"});
            loadDetails(new String[]{"151", "1", "1", "1", "144", "10", "5", "20", "118", "30", "94", "40", "112", "60", "133"});
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