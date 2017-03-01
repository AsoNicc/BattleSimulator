/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.Pokemon;

import java.util.Random;

/**
 * @author Nick
 */
public class Damage {
    private final static float[][] type = {
        /*0*/   {1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 0.5f, 0f  , 1f  , 1f  , 0.5f, 1f  , 1f}, 
        /*1*/   {1f  , 0.5f, 0.5f, 1f  , 2f  , 2f  , 1f  , 1f  , 1f  , 1f  , 1f  , 2f  , 0.5f, 1f  , 0.5f, 1f  , 2f  , 1f  , 1f},  
        /*2*/   {1f  , 2f  , 0.5f, 1f  , 0.5f, 1f  , 1f  , 1f  , 2f  , 1f  , 1f  , 1f  , 2f  , 1f  , 0.5f, 1f  , 1f  , 1f  , 1f},  
        /*3*/   {1f  , 1f  , 2f  , 0.5f, 0.5f, 1f  , 1f  , 1f  , 0f  , 2f  , 1f  , 1f  , 1f  , 1f  , 0.5f, 1f  , 1f  , 1f  , 1f},  
        /*4*/   {1f  , 0.5f, 2f  , 1f  , 0.5f, 1f  , 1f  , 0.5f, 2f  , 0.5f, 1f  , 0.5f, 2f  , 1f  , 0.5f, 1f  , 0.5f, 1f  , 1f},  
        /*5*/   {1f  , 0.5f, 0.5f, 1f  , 2f  , 0.5f, 1f  , 1f  , 2f  , 2f  , 1f  , 1f  , 1f  , 1f  , 2f  , 1f  , 0.5f, 1f  , 1f},  
        /*6*/   {2f  , 1f  , 1f  , 1f  , 1f  , 2f  , 1f  , 0.5f, 1f  , 0.5f, 0.5f, 0.5f, 2f  , 0f  , 1f  , 2f  , 2f  , 0.5f, 1f},  
        /*7*/   {1f  , 1f  , 1f  , 1f  , 2f  , 1f  , 1f  , 0.5f, 0.5f, 1f  , 1f  , 1f  , 0.5f, 0.5f, 1f  , 1f  , 0f  , 2f  , 1f},  
        /*8*/   {1f  , 2f  , 1f  , 2f  , 0.5f, 1f  , 1f  , 2f  , 1f  , 0f  , 1f  , 0.5f, 2f  , 1f  , 1f  , 1f  , 2f  , 1f  , 1f},  
        /*9*/   {1f  , 1f  , 1f  , 0.5f, 2f  , 1f  , 2f  , 1f  , 1f  , 1f  , 1f  , 2f  , 0.5f, 1f  , 1f  , 1f  , 0.5f, 1f  , 1f},  
        /*10*/  {1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 2f  , 2f  , 1f  , 1f  , 0.5f, 1f  , 1f  , 1f  , 1f  , 0f  , 0.5f, 1f  , 1f},  
        /*11*/  {1f  , 0.5f, 1f  , 1f  , 2f  , 1f  , 0.5f, 0.5f, 1f  , 0.5f, 2f  , 1f  , 1f  , 0.5f, 1f  , 2f  , 0.5f, 0.5f, 1f},  
        /*12*/  {1f  , 2f  , 1f  , 1f  , 1f  , 2f  , 0.5f, 1f  , 0.5f, 2f  , 1f  , 2f  , 1f  , 1f  , 1f  , 1f  , 0.5f, 1f  , 1f},  
        /*13*/  {0f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 2f  , 1f  , 1f  , 2f  , 1f  , 0.5f, 1f  , 1f  , 1f},  
        /*14*/  {1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 2f  , 1f  , 0.5f, 0f  , 1f},  
        /*15*/  {1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 0.5f, 1f  , 1f  , 1f  , 2f  , 1f  , 1f  , 2f  , 1f  , 0.5f, 1f  , 0.5f, 1f},  
        /*16*/  {1f  , 0.5f, 0.5f, 0.5f, 1f  , 2f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 2f  , 1f  , 1f  , 1f  , 0.5f, 2f  , 1f},  
        /*17*/  {1f  , 0.5f, 1f  , 1f  , 1f  , 1f  , 2f  , 0.5f, 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 2f  , 2f  , 0.5f, 1f  , 1f},
        /*18*/  {1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f  , 1f} 
    };
    private final static Random gen = new Random();
    private static String[] move;
    protected static byte attacker, c;
    protected static boolean IsaCrit;
    protected static float effect, r, s;
    
    protected static float Calculator(String[] attack, byte battler){
        move = attack;
        attacker = battler;
        
        if(attacker == 1){
            if(move[1].equals("Psystrike")) return (float)Round((((2*Battle.o_pokemon.level+10)/250f)*((float)Battle.o_pokemon.SpA/Battle.u_pokemon.Def)*Short.parseShort(move[5])+2)*modifier(), 1);
            else if(move[3].equals("Physical")) return (float)Round((((2*Battle.o_pokemon.level+10)/250f)*((float)Battle.o_pokemon.Atk/Battle.u_pokemon.Def)*Short.parseShort(move[5])+2)*modifier(), 1);
            else return (float)Round((((2*Battle.o_pokemon.level+10)/250f)*((float)Battle.o_pokemon.SpA/Battle.u_pokemon.SpD)*Short.parseShort(move[5])+2)*modifier(), 1);
        } else { 
            if(move[1].equals("Psystrike")) return (float)Round((((2*Battle.u_pokemon.level+10)/250f)*((float)Battle.u_pokemon.SpA/Battle.o_pokemon.Def)*Short.parseShort(move[5])+2)*modifier(), 1);
            else if(move[3].equals("Physical")) return (float)Round((((2*Battle.u_pokemon.level+10)/250f)*((float)Battle.u_pokemon.Atk/Battle.o_pokemon.Def)*Short.parseShort(move[5])+2)*modifier(), 1);
            else return (float)Round((((2*Battle.u_pokemon.level+10)/250f)*((float)Battle.u_pokemon.SpA/Battle.o_pokemon.SpD)*Short.parseShort(move[5])+2)*modifier(), 1);
        }
    }
    
    private static float modifier(){
        return STAB()*effectiveness()*critical()*other()*random();
    }
    
    private static float STAB(){
        if(attacker == 1) return (s = (move[2].equals(Battle.o_pokemon.TYPE1) || move[2].equals(Battle.o_pokemon.TYPE2))? 1.5f : 1f);
        else return (s = (move[2].equals(Battle.u_pokemon.TYPE1) || move[2].equals(Battle.u_pokemon.TYPE2))? 1.5f : 1f);
    }
    
    private static float effectiveness(){
        if(attacker == 1) return (effect = compareTypes(move[2], Battle.u_pokemon.TYPE1)*compareTypes(move[2], Battle.u_pokemon.TYPE2));
        else return (effect = compareTypes(move[2], Battle.o_pokemon.TYPE1)*compareTypes(move[2], Battle.o_pokemon.TYPE2));
    }
    
    private static byte critical(){
        float T;

        if(attacker == 1){
            T = (move[2].equals("NORMAL"))? Battle.o_pokemon.Spe/2f : Battle.o_pokemon.Spe;            
            T /= ((Battle.o_unfocusedEnergy)? 4 : 1);
        } else {
            T = (move[2].equals("NORMAL"))? Battle.u_pokemon.Spe/2f : Battle.u_pokemon.Spe;            
            T /= ((Battle.u_unfocusedEnergy)? 4 : 1);
        }
        
        T *= ((HCHR_Move())? 8 : 1);
        T *= (1/8f); //Possible outcomes
        
        return (IsaCrit = (gen.nextInt(256)/256f < T/256))? (c = (byte)2) : (c = (byte)1);
    }
    
    private static byte other(){
        return 1;
    }
    
    private static float random(){
        return (r = 0.85f + gen.nextInt(16)/100f);
    }
    
    private static float compareTypes(String attack, String defense){
        return type[getIndex(attack)][getIndex(defense)];
    }
    
    private static byte getIndex(String token){
        if(token.equals("NORMAL")) return 0;
        if(token.equals("FIRE")) return 1;
        if(token.equals("WATER")) return 2;
        if(token.equals("ELECTRIC")) return 3;
        if(token.equals("GRASS")) return 4;
        if(token.equals("ICE")) return 5;
        if(token.equals("FIGHTING")) return 6;
        if(token.equals("POISON")) return 7;
        if(token.equals("GROUND")) return 8;
        if(token.equals("FLYING")) return 9;
        if(token.equals("PSYCHIC")) return 10;
        if(token.equals("BUG")) return 11;
        if(token.equals("ROCK")) return 12;
        if(token.equals("GHOST")) return 13;
        if(token.equals("DRAGON")) return 14;
        if(token.equals("DARK")) return 15;
        if(token.equals("STEEL")) return 16;
        if(token.equals("FAIRY")) return 17;
        else return 18;
    }
    
    private static boolean HCHR_Move(){
        return (move[1].equals("Aeroblast") || move[1].equals("AA=ir Cutter") || move[1].equals("Attack Order") || 
                move[1].equals("Blaze Kick") || move[1].equals("Crabhammer") || move[1].equals("Cross Chop") || 
                move[1].equals("Cross Poison") || move[1].equals("Drill Run") || move[1].equals("Karate Chop") || 
                move[1].equals("Leaf Blade") || move[1].equals("Night Slash") || move[1].equals("Poison Tail") || 
                move[1].equals("Psycho Cut") || move[1].equals("Razor Leaf") || move[1].equals("Razor Wind") || 
                move[1].equals("Shadow Claw") || move[1].equals("Sky Attack") || move[1].equals("Slash") || 
                move[1].equals("Spacial Rend") || move[1].equals("Stone Edge") 
                );
    }
    
    protected static double Round(double number, int placeAfterDecimal){        
        double doa = Math.pow(10, placeAfterDecimal); //degree of accuracy
        
        if(((number*doa) - (int)(number*doa)) >= .5) return ((int)(number*doa) + 1)/doa;
        else {
            if(number < 0) return ((int)(number*doa) - 1)/doa;
            else return ((int)(number*doa))/doa;
        }
    }
}