/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.Pokemon;

import android.content.Context;
import static android.content.Context.MODE_PRIVATE;
import android.content.SharedPreferences;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * @author Nick
 */
public final class Pokemon {
    private final Random gen = new Random();
    protected Byte[] PQI = new Byte[4];
    protected Context context;
    protected Short dexNo;
    protected String buff, name, nickname, nature, TYPE1, TYPE2;
    protected String[][] moves = new String[4][8];
    protected byte approxLevel = 100, level;
    protected char gender;
    protected float height, weight;
    protected int exp;
    protected short HP, Atk, Def, SpA, SpD, Spe;
    /* Generate a random Pokemon w/ possible level 0-100 */
    Pokemon(Context tContext){
        context = tContext;
        approxLevel = (byte)gen.nextInt(101);
        initialize();
    }
    
    /* Generate a random Pokemon w/ specified level range */
    Pokemon(Context tContext, byte levelRange){
        context = tContext;
        if(levelRange >= 0 && levelRange < 101) approxLevel = levelRange;
        initialize();
    }
    
    /* Generate a specified Pokemon w/ specified level range */
    Pokemon(Context tContext, byte levelRange, Short tDexNo){
        context = tContext;
        if(levelRange >= 0 && levelRange < 101) approxLevel = levelRange;
        if(tDexNo > 0 && tDexNo < 152) dexNo = tDexNo;
        initialize();
    }
    
    /* Generate a specific Pokemon & its stats */
    Pokemon(char tGender, byte tLevel, short tDexNo, short tHP, short tAtk, short tDef, 
            short tSpA, short tSpD, short tSpe, Byte[] tPQI, float tHeight, float tWeight, 
            String tName, String tNickname, String tNature, String tTYPE1, String tTYPE2, 
            String[][] tMoveSet, int tExp, String tBuff){
        
        dexNo = tDexNo;
        name = tName;
        nickname = tNickname;
        gender = tGender;
        TYPE1 = tTYPE1;
        TYPE2 = tTYPE2;
        level = tLevel;
        HP = tHP;
        Atk = tAtk;
        Def = tDef;
        SpA = tSpA;
        SpD = tSpD;
        Spe = tSpe;
        PQI = tPQI;
        height = tHeight;
        weight = tWeight;
        moves = tMoveSet;
        exp = tExp;
        buff = tBuff;
    }
    
    private void initialize(){
        /* Obtain Settings for base stats of any u_pokemon */
        SharedPreferences details = context.getSharedPreferences("genOneBaseStatList", MODE_PRIVATE);
        if(!details.getBoolean("setState", false)){
            Settings load = new Settings(context);
            load.setBaseInfo();
            details = context.getSharedPreferences("genOneBaseStatList", MODE_PRIVATE);   
        }
        
        /* Initialize variables */
        try {
            Set<String>tempSet = details.getStringSet((dexNo == null)? String.valueOf(gen.nextInt(150) + 1) : String.valueOf(dexNo), null);
            Iterator position = tempSet.iterator();
            String[] info = new String[tempSet.size()];

            String data, index;
            int i;
            /* Unscramble hashset data */
            while(position.hasNext()){
                index = "";
                data = position.next().toString();
                /* Builds index of array out of string form */            
                for(i = 0; i < data.length(); i++){
                    if(data.charAt(i) == '_') break;
                    else index += data.charAt(i);
                }
                /* Load into the array */
                info[Integer.parseInt(index)] = data.substring(i + 1);
            }
            /* Establish gender */
            if(info[1].equals("Nidoran♀") || info[1].equals("Nidoran♂")) gender = info[1].charAt(info[1].length() - 1);
            else {
                if(info[1].equals("Nidorino") || info[1].equals("Nidoking")) gender = '♂';
                else if(info[1].equals("Nidorina") || info[1].equals("Nidoqueen")) gender = '♀';
                else if(gen.nextInt()%2 == 0) gender = '♂';
                else gender = '♀';
            }
            /* Randomize a level within an approximate range (+/- 5) */
            level = (byte)(gen.nextInt(Math.min(approxLevel + 5, 100) - Math.max(approxLevel - 5, 1) + 1) + Math.max(approxLevel - 5, 1));

            StatGenerator stat = new StatGenerator(Short.parseShort(info[2]),
                    Short.parseShort(info[3]),
                    Short.parseShort(info[4]),
                    Short.parseShort(info[5]),
                    Short.parseShort(info[6]),
                    Short.parseShort(info[7]),
                    level
                    );

            dexNo = Short.parseShort(info[0]);
            HP = stat.HP;
            Atk = stat.Atk;
            Def = stat.Def;
            SpA = stat.SpA;
            SpD = stat.SpD;
            Spe = stat.Spe;
            name = info[1];
            nickname = name;
            nature = stat.nature;
            TYPE1 = info[10];
            TYPE2 = info[11];
            buff = null;

            loadMoves();
        } catch(Exception e){
            Battle.text.setText("From Pokemon.initialize: Unknown key value. " + e.toString());
        }
    }
    
    private void loadMoves(){
        /* Used to setup SharedPreferences, if it has not been set */
        SharedPreferences moveData = context.getSharedPreferences("genOneMoveList", MODE_PRIVATE);
        if(!moveData.getBoolean("setState", false)){
            Settings load = new Settings(context);
            load.setMoves();
            moveData = context.getSharedPreferences("genOneMoveList", MODE_PRIVATE);    
        }
        
        SharedPreferences moveSet = context.getSharedPreferences("learningSet", MODE_PRIVATE);
        if(!moveSet.getBoolean("setState", false)){
            Settings load = new Settings(context);
            load.setLearnedMoves();
            moveSet = context.getSharedPreferences("learningSet", MODE_PRIVATE);
        }
        
        try {
            Set<String> tempSet = moveSet.getStringSet(String.valueOf(dexNo), null);
            Iterator position = tempSet.iterator();
            String[] learningSet = new String[tempSet.size()];

            String move, index;
            byte i;
            while(position.hasNext()){
                index = "";
                move = position.next().toString();

                for(i = 0; i < move.length(); i++){
                    if(move.charAt(i) == '_') break;
                    else index += move.charAt(i);
                }

                learningSet[Integer.parseInt(index)] = move.substring(i + 1);
            }        

            String[] temp = new String[8];
            for(byte j = 1, k = 0; j < learningSet.length && Byte.parseByte(learningSet[j]) <= level; j += 2, k++){
                if(k%4 == 0){
                    try {
                        Set<String> tempSet1 = moveData.getStringSet(learningSet[j + 1], null); // Retrieve HashSet & store in Set var
                        Iterator position1 = tempSet1.iterator(); // Create an interator

                        String values; // temp var
                        try {
                            while(position1.hasNext()){ // Populate details into MOVE1 array
                                values = position1.next().toString(); // Capture obj & convert to String-- iterator then moves to next

                                if(values.length() > 2) temp[Integer.parseInt(values.substring(0, 1))] = values.substring(2);
                                else temp[Integer.parseInt(values.substring(0, 1))] = "";
                            }
                        } catch(Exception e) {
                            Battle.text.setText("From Pokemon.loadMoves:Move1: " + e.toString());
                        } finally {
                            if(!((temp[1]).equals(moves[1][1]) || (temp[1]).equals(moves[2][1]) || (temp[1]).equals(moves[3][1]))){ //Then record of move already exist
                                PQI[Integer.parseInt(temp[4]) - 1] = 0; //Nullify corresponding PQ
                                moves[0][0] = temp[0]; //Nullify all details entry entirely
                                moves[0][1] = temp[1]; //Nullify all details entry entirely
                                moves[0][2] = temp[2]; //Nullify all details entry entirely
                                moves[0][3] = temp[3]; //Nullify all details entry entirely
                                moves[0][4] = temp[4]; //Nullify all details entry entirely
                                moves[0][5] = temp[5]; //Nullify all details entry entirely
                                moves[0][6] = temp[6]; //Nullify all details entry entirely
                                moves[0][7] = temp[7]; //Nullify all details entry entirely
                            }
                        }

                        if(PQI[((Integer.parseInt(moves[0][4]) - 1) + 1)%4] != null)
                            if(PQI[((Integer.parseInt(moves[0][4]) - 1) + 1)%4] == 0) PQI[((Integer.parseInt(moves[0][4]) - 1) + 1)%4] = null;
                        if(PQI[((Integer.parseInt(moves[0][4]) - 1) + 2)%4] != null)
                            if(PQI[((Integer.parseInt(moves[0][4]) - 1) + 2)%4] == 0) PQI[((Integer.parseInt(moves[0][4]) - 1) + 2)%4] = null;
                        if(PQI[((Integer.parseInt(moves[0][4]) - 1) + 3)%4] != null)
                            if(PQI[((Integer.parseInt(moves[0][4]) - 1) + 3)%4] == 0) PQI[((Integer.parseInt(moves[0][4]) - 1) + 3)%4] = null;                        
                    } catch(Exception err){
                        Battle.text.setText("From Pokemon.loadMoves:Move1: " + err.toString());
                    }
                } else if(k%4 == 1){
                    try {
                        Set<String> tempSet2 = moveData.getStringSet(learningSet[j + 1], null); // Retrieve HashSet & store in Set var
                        Iterator position2 = tempSet2.iterator(); // Create an interator

                        String values; // temp var
                        try {
                            while(position2.hasNext()){ // Populate details into MOVE2 array
                                values = position2.next().toString(); // Capture obj & convert to String-- iterator then moves to next

                                if(values.length() > 2) temp[Integer.parseInt(values.substring(0, 1))] = values.substring(2);
                                else temp[Integer.parseInt(values.substring(0, 1))] = "";
                            }
                        } catch(Exception e) {
                            Battle.text.setText("From Pokemon.loadMoves:Move2: " + e.toString());
                        } finally {
                            if(!((temp[1]).equals(moves[0][1]))){
                                PQI[Integer.parseInt(temp[4]) - 1] = 1;
                                moves[1][0] = temp[0]; //Nullify all details entry entirely
                                moves[1][1] = temp[1]; //Nullify all details entry entirely
                                moves[1][2] = temp[2]; //Nullify all details entry entirely
                                moves[1][3] = temp[3]; //Nullify all details entry entirely
                                moves[1][4] = temp[4]; //Nullify all details entry entirely
                                moves[1][5] = temp[5]; //Nullify all details entry entirely
                                moves[1][6] = temp[6]; //Nullify all details entry entirely
                                moves[1][7] = temp[7]; //Nullify all details entry entirely
                            }
                        }

                        if(PQI[((Integer.parseInt(moves[1][4]) - 1) + 1)%4] != null)
                            if(PQI[((Integer.parseInt(moves[1][4]) - 1) + 1)%4] == 1) PQI[((Integer.parseInt(moves[1][4]) - 1) + 1)%4] = null;
                        if(PQI[((Integer.parseInt(moves[1][4]) - 1) + 2)%4] != null)
                            if(PQI[((Integer.parseInt(moves[1][4]) - 1) + 2)%4] == 1) PQI[((Integer.parseInt(moves[1][4]) - 1) + 2)%4] = null;
                        if(PQI[((Integer.parseInt(moves[1][4]) - 1) + 3)%4] != null)
                            if(PQI[((Integer.parseInt(moves[1][4]) - 1) + 3)%4] == 1) PQI[((Integer.parseInt(moves[1][4]) - 1) + 3)%4] = null;                        
                    } catch(Exception err){
                        Battle.text.setText("From Pokemon.loadMoves:Move2: " + err.toString());
                    }
                } else if(k%4 == 2){
                    try {
                        Set<String> tempSet3 = moveData.getStringSet(learningSet[j + 1], null); // Retrieve HashSet & store in Set var
                        Iterator position3 = tempSet3.iterator(); // Create an interator

                        String values; // temp 
                        try {
                            while(position3.hasNext()){ // Populate details into MOVE3 array
                                values = position3.next().toString(); // Capture obj & convert to String-- iterator then moves to next

                                if(values.length() > 2) temp[Integer.parseInt(values.substring(0, 1))] = values.substring(2);
                                else temp[Integer.parseInt(values.substring(0, 1))] = "";
                            }
                        } catch(Exception e) {
                            Battle.text.setText("From Pokemon.loadMoves:Move3: " + e.toString());
                        } finally {                        
                            if(!((temp[1]).equals(moves[0][1]) || (temp[1]).equals(moves[1][1]))){
                                PQI[Integer.parseInt(temp[4]) - 1] = 2;
                                moves[2][0] = temp[0]; //Nullify all details entry entirely
                                moves[2][1] = temp[1]; //Nullify all details entry entirely
                                moves[2][2] = temp[2]; //Nullify all details entry entirely
                                moves[2][3] = temp[3]; //Nullify all details entry entirely
                                moves[2][4] = temp[4]; //Nullify all details entry entirely
                                moves[2][5] = temp[5]; //Nullify all details entry entirely
                                moves[2][6] = temp[6]; //Nullify all details entry entirely
                                moves[2][7] = temp[7]; //Nullify all details entry entirely
                            }
                        }

                        if(PQI[((Integer.parseInt(moves[2][4]) - 1) + 1)%4] != null)
                            if(PQI[((Integer.parseInt(moves[2][4]) - 1) + 1)%4] == 2) PQI[((Integer.parseInt(moves[2][4]) - 1) + 1)%4] = null;
                        if(PQI[((Integer.parseInt(moves[2][4]) - 1) + 2)%4] != null) 
                            if(PQI[((Integer.parseInt(moves[2][4]) - 1) + 2)%4] == 2) PQI[((Integer.parseInt(moves[2][4]) - 1) + 2)%4] = null;
                        if(PQI[((Integer.parseInt(moves[2][4]) - 1) + 3)%4] != null)
                            if(PQI[((Integer.parseInt(moves[2][4]) - 1) + 3)%4] == 2) PQI[((Integer.parseInt(moves[2][4]) - 1) + 3)%4] = null;                        
                    } catch(Exception err){
                        Battle.text.setText("From Pokemon.loadMoves:Move3: " + err.toString());
                    }
                } else if(k%4 == 3){
                    try {
                        Set<String> tempSet4 = moveData.getStringSet(learningSet[j + 1], null); // Retrieve HashSet & store in Set var
                        Iterator position4 = tempSet4.iterator(); // Create an interator

                        String values; // temp var
                        try {
                            while(position4.hasNext()){ // Populate details into MOVE4 array
                                values = position4.next().toString(); // Capture obj & convert to String-- iterator then moves to next

                                if(values.length() > 2) temp[Integer.parseInt(values.substring(0, 1))] = values.substring(2);
                                else temp[Integer.parseInt(values.substring(0, 1))] = "";
                            }
                        } catch(Exception e) {
                            Battle.text.setText("From Pokemon.loadMoves:Move4: " + e.toString());
                        } finally {                        
                            if(!((temp[1]).equals(moves[0][1]) || (temp[1]).equals(moves[1][1]) || (temp[1]).equals(moves[2][1]))){
                                PQI[Integer.parseInt(temp[4]) - 1] = 3;
                                moves[3][0] = temp[0]; //Nullify all details entry entirely
                                moves[3][1] = temp[1]; //Nullify all details entry entirely
                                moves[3][2] = temp[2]; //Nullify all details entry entirely
                                moves[3][3] = temp[3]; //Nullify all details entry entirely
                                moves[3][4] = temp[4]; //Nullify all details entry entirely
                                moves[3][5] = temp[5]; //Nullify all details entry entirely
                                moves[3][6] = temp[6]; //Nullify all details entry entirely
                                moves[3][7] = temp[7]; //Nullify all details entry entirely
                            }
                        }

                        if(PQI[((Integer.parseInt(moves[3][4]) - 1) + 1)%4] != null)
                            if(PQI[((Integer.parseInt(moves[3][4]) - 1) + 1)%4] == 3) PQI[((Integer.parseInt(moves[3][4]) - 1) + 1)%4] = null;
                        if(PQI[((Integer.parseInt(moves[3][4]) - 1) + 2)%4] != null)
                            if(PQI[((Integer.parseInt(moves[3][4]) - 1) + 2)%4] == 3) PQI[((Integer.parseInt(moves[3][4]) - 1) + 2)%4] = null;
                        if(PQI[((Integer.parseInt(moves[3][4]) - 1) + 3)%4] != null)
                            if(PQI[((Integer.parseInt(moves[3][4]) - 1) + 3)%4] == 3) PQI[((Integer.parseInt(moves[3][4]) - 1) + 3)%4] = null;                        
                    } catch(Exception err){
                        Battle.text.setText("From Pokemon.loadMoves:Move4: " + err.toString());
                    }
                }
            }
        } catch(Exception e){
            Battle.text.setText("Unknown key value for moveSet: " + e.toString());
        } finally {
            
        }
    }
}