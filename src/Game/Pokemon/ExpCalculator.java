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
        else if(group == 2) return (short)Round(4*Math.pow(level, 3)/5f, 0);
        else if(group == 3) return (short)Math.pow(level, 3);
        else if(group == 4) return (short)Round((6/5f)*Math.pow(level, 3) - 15*Math.pow(level, 2) + 100*level - 140, 0);
        else if(group == 5) return (short)Round(5*Math.pow(level, 3)/4f, 0);
        else if(group == 6) return flux(level);
        else return 0; 
    }
    
    protected static double Round(double number, int placeAfterDecimal){        
        double doa = Math.pow(10, placeAfterDecimal); //degree of accuracy
        
        if(((number*doa) - (int)(number*doa)) >= .5) return ((int)(number*doa) + 1)/doa;
        else {
            if(number < 0) return ((int)(number*doa) - 1)/doa;
            else return ((int)(number*doa))/doa;
        }
    }

    /* Unused until PKMN Gen III */
    private static short erratic(byte n){
        if(n <= 50) return (short)Round(Math.pow(n, 3)*(100 - n)/50f, 0);
        else if(n <= 68) return (short)Round(Math.pow(n, 3)*(150 - n)/100f, 0);
        else if(n <= 98) return (short)Round(Math.pow(n, 3)*((1911 - 10*n)/3f)/500f, 0);
        else return (short)Round(Math.pow(n, 3)*(160 - n)/100f, 0);
    }

    /* Unused until PKMN Gen III */
    private static short flux(byte n){
        if(n <= 15) return (short)Round(Math.pow(n, 3)*((((n + 1)/3f) + 24)/50f), 0);
        else if(n <= 36) return (short)Round(Math.pow(n, 3)*((n + 14)/50f), 0);
        else return (short)Round(Math.pow(n, 3)*(((n/2f) + 32)/50f), 0);        
    }
    
    protected static double delta(float a, byte b, byte L, byte s, byte Lp, float t, float e){
        return Round((((a*b*L)/(5*s))*(Math.pow(2L + 10, 2.5)/Math.pow(L + Lp + 10, 2.5)) + 1)*t*e, 0);
    }
}