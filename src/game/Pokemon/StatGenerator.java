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
public class StatGenerator {
    private static final Random gen = new Random();
    private static final byte[] order = new byte[3];
    protected short HP, Atk, Def, SpA, SpD, Spe;
    
    /* Method used for generating stats of a random pokemon */
    protected void StatGenrator(short tHP, short tAtk, short tDef, short tSpA, short tSpD, short tSpe, byte limit){
        /* Both base & iv data sets need to be imported as well as natures */
        final short bHP = tHP, bAtk = tAtk, bDef = tDef, bSpA = tSpA, bSpD = tSpD, bSpe = tSpe;
        
        /* Generate random IVs */
        final int ivHP = gen.nextInt(32), ivAtk = gen.nextInt(32), 
                ivDef = gen.nextInt(32), ivSpA = gen.nextInt(32), 
                ivSpD = gen.nextInt(32), ivSpe = gen.nextInt(32);
        
        /* Initialize EVs */
        short evHP = 0, evAtk = 0, evDef = 0, evSpA = 0, evSpD = 0, evSpe = 0;
        
        for(byte LVL = 1; LVL < limit; LVL++){
            /* Choose top three performing attr. randomly */
            int choice = gen.nextInt(6);
            int choice2, choice3;

            do choice2 = gen.nextInt(6);
            while(choice2 == choice);

            do choice3 = gen.nextInt(6);
            while(choice3 == choice2 || choice3 == choice);

            /* if(total EV's < 510) then */evSpread();

            /* REMINDER If total EVs > 510, handle situation @tp before assigning */
             
            HP = (short)Round(((2*bHP + ivHP + (((choice == 0)? evHP += order[0] : ((choice2 == 0)? evHP += order[1] : ((choice3 == 0)? evHP += order[2] : evHP))))/4f + 100)*LVL)/100f + 10);            
            Atk = (short)Round((((2*bAtk + ivAtk + ((choice == 1)? evAtk += order[0] : ((choice2 == 1)? evAtk += order[1] : ((choice3 == 1)? evAtk += order[2] : evAtk)))/4f)*LVL)/100f + 5)*1/*<-Nature*/);            
            Def = (short)Round((((2*bDef + ivDef + ((choice == 2)? evDef += order[0] : ((choice2 == 2)? evDef += order[1] : ((choice3 == 2)? evDef += order[2] : evDef)))/4f)*LVL)/100f + 5)*1/*<-Nature*/);            
            SpA = (short)Round((((2*bSpA + ivSpA + ((choice == 3)? evSpA += order[0] : ((choice2 == 3)? evSpA += order[1] : ((choice3 == 3)? evSpA += order[2] : evSpA)))/4f)*LVL)/100f + 5)*1/*<-Nature*/);            
            SpD = (short)Round((((2*bSpD + ivSpD + ((choice == 4)? evSpD += order[0] : ((choice2 == 4)? evSpD += order[1] : ((choice3 == 4)? evSpD += order[2] : evSpD)))/4f)*LVL)/100f + 5)*1/*<-Nature*/);            
            Spe = (short)Round((((2*bSpe + ivSpe + ((choice == 5)? evSpe += order[0] : ((choice2 == 5)? evSpe += order[1] : ((choice3 == 5)? evSpe += order[2] : evSpe)))/4f)*LVL)/100f + 5)*1/*<-Nature*/);
        }
    }
    
    protected static byte[] StatGenerator(){
        evSpread();
        return order;
    }
    
    /* Fill w/ proper data */
    private float Nature(String token){
        return 1f;
    }
    
    private int Round(double num){
        if( num/((int)num) >= .5 ) return (int)Math.ceil(num);
        else return (int)Math.floor(num);
    }
    
    private String stat(int num){
        if(num == 0) return "HP";
        else if(num == 1) return "Atk";
        else if(num == 2) return "Def";
        else if(num == 3) return "SpA";
        else if(num == 4) return "SpD";
        else if(num == 5) return "Spe";
        else return "";
    }
    
    private static void evSpread(){
        byte num1, num2, num3, sup1, sup2 = 0, sup3 = 0, temp, temp2;
        
        /* Generate three random ints */
        num1 = random();
        num2 = random();
        num3 = random();

        /* Determine if there needs to be addition values to supplement the 
         * values of the random numbers */
        if(num1 + num2 + num3 >= 6){
            arrange(num1, num2, num3); // Rearrange the random numbers, once more
            System.out.println(order[0] + ", " + order[1] + ", " + order[2]);
            return;
        } else sup1 = random();

        if(num1 + num2 + num3 + sup1 < 6) sup2 = random();

        if(num1 + num2 + num3 + sup1 + sup2 < 6) sup3 = random();

        if(sup3 == 1) order[0] = order[1] = order[2] = 2;
        else {
            arrange(num1, num2, num3);

            /* Assign arrangement of values from highest to lowest */
            num1 = order[0];
            num2 = order[1];
            num3 = order[2];

            arrange(sup1, sup2, sup3);

            /* Determine if the highest supplement value can fill in A SINGLE 
             * random value up to four */
            while(true){
                if(num1 + order[0] <= 4){ 
                    num1 += order[0]; 
                    break; 
                } else if(num2 + order[0] <= 4){ 
                    num2 += order[0]; 
                    break; 
                } else if(num3 + order[0] <= 4){ 
                    num3 += order[0]; 
                    break; 
                } else { 
                    if(order[0] != 0) order[0]--;
                    if(order[1] != 0) order[1]--;
                    if(order[2] != 0) order[2]--;
                }
            }

            /* @ THIS POINT: highest supplement value was able to add to a 
             * random value. Store the remaining two supplemental numbers */
            temp = order[1];
            temp2 = order[2];

            arrange(num1, num2, num3); // Rearrange the random numbers again

            /* Assign arrangement of values from highest to lowest */
            num1 = order[0];
            num2 = order[1];
            num3 = order[2];

            /* Restore the two remaining supplemental values */
            order[1] = temp;
            order[2] = temp2;

            if(num2 + order[1] <= 4){
                num2 += order[1];
                if(num3 + order[2] <= 4) num3 += order[2]; 
            } else if(num3 + order[1] <= 4){
                num3 += order[1];
                if(num2 + order[2] <= 4) num2 += order[2];
            }

            arrange(num1, num2, num3); // Rearrange the random numbers, once more
        }

        System.out.println(order[0] + ", " + order[1] + ", " + order[2]);
    }
    
    private static byte random(){
        double num = gen.nextGaussian();
        
        if(num <= 0.25) return 1;
        else if(num <= 0.5) return 2;
        else if(num <= 0.75) return 3;
        else return 4;
    }

    private static void arrange(byte token1, byte token2, byte token3) {
        order[2] = (byte)Math.min(token1, Math.min(token2, token3));
        order[0] = (byte)Math.max(token1, Math.max(token2, token3));
        
        if(order[0] == token1) token1 = 0;
        else if(order[0] == token2) token2 = 0;
        else token3 = 0;
        
        if(order[2] == token1) token1 = 0;
        else if(order[2] == token2) token2 = 0;
        else token3 = 0;
        
        if(token1 != 0) order[1] = token1;
        else if(token2 != 0) order[1] = token2;
        else order[1] = token3;
    }
}