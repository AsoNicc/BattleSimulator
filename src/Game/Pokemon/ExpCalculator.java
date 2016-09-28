/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.Pokemon;

/**
 * @author Nick
 */
public class ExpCalculator {
    /* Generate total experience for a newly acquired pokemon */
    protected static short ExpCalculator(byte rate, byte level){
        byte group = rate; // Receive group value from outside class 
        
        /* Calculate amount of TOTAL experience needed to reach the NEXT LEVEL */        
        if(group == 1) return erratic(level);
        else if(group == 2) return (short)Round(4*Math.pow(level, 3)/5f);
        else if(group == 3) return (short)Math.pow(level, 3);
        else if(group == 4) return (short)Round((6/5f)*Math.pow(level, 3) - 15*Math.pow(level, 2) + 100*level - 140);
        else if(group == 5) return (short)Round(5*Math.pow(level, 3)/4f);
        else if(group == 6) return flux(level);
        else return 0; 
    }
    
    private static int Round(double num){
        if(num/((int)num) >= .5 ) return (int)Math.ceil(num);
        else return (int)Math.floor(num);
    }

    /* Unused until PKMN Gen III */
    private static short erratic(byte n){
        if(n <= 50) return (short)Round(Math.pow(n, 3)*(100 - n)/50f);
        else if(n <= 68) return (short)Round(Math.pow(n, 3)*(150 - n)/100f);
        else if(n <= 98) return (short)Round(Math.pow(n, 3)*((1911 - 10*n)/3f)/500f);
        else return (short)Round(Math.pow(n, 3)*(160 - n)/100f);
    }

    /* Unused until PKMN Gen III */
    private static short flux(byte n){
        if(n <= 15) return (short)Round(Math.pow(n, 3)*((((n + 1)/3f) + 24)/50f));
        else if(n <= 36) return (short)Round(Math.pow(n, 3)*((n + 14)/50f));
        else return (short)Round(Math.pow(n, 3)*(((n/2f) + 32)/50f));        
    }
    
    protected static int delta(float a, byte b, byte L, byte s, byte Lp, float t, float e){
        return Round((((a*b*L)/(5*s))*(Math.pow(2L + 10, 2.5)/Math.pow(L + Lp + 10, 2.5)) + 1)*t*e);
    }
}