/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.Pokemon;

/**
 *
 * @author Nick
 */
public class Abilities {
    protected static void get(String methodNo){
        if(Integer.valueOf(methodNo) == 1) Pixilate();
    }
    
    private static void Pixilate(){
        if(Battle.u_pokemon.moves[0][2] != null){
            if((Battle.u_pokemon.moves[0][2]).equals("NORMAL")){
                Battle.u_pokemon.moves[0][2] = "FAIRY";
                if(!((Battle.u_pokemon.moves[0][5]).equals("null"))) 
                    Battle.u_pokemon.moves[0][5] = String.valueOf((int)(Integer.valueOf(Battle.u_pokemon.moves[0][5])*1.2));
            }
        }
        if(Battle.u_pokemon.moves[1][2] != null){
            if((Battle.u_pokemon.moves[1][2]).equals("NORMAL")){
                Battle.u_pokemon.moves[1][2] = "FAIRY";
                if(!((Battle.u_pokemon.moves[1][5]).equals("null"))) 
                    Battle.u_pokemon.moves[1][5] = String.valueOf((int)(Integer.valueOf(Battle.u_pokemon.moves[1][5])*1.2));
            }
        }
        if(Battle.u_pokemon.moves[2][2] != null){
            if((Battle.u_pokemon.moves[2][2]).equals("NORMAL")){
                Battle.u_pokemon.moves[2][2] = "FAIRY";
                if(!((Battle.u_pokemon.moves[2][5]).equals("null"))) 
                    Battle.u_pokemon.moves[2][5] = String.valueOf((int)(Integer.valueOf(Battle.u_pokemon.moves[2][5])*1.2));
            }
        }
        if(Battle.u_pokemon.moves[3][2] != null){
            if((Battle.u_pokemon.moves[3][2]).equals("NORMAL")){
                Battle.u_pokemon.moves[3][2] = "FAIRY";
                if(!((Battle.u_pokemon.moves[3][5]).equals("null"))) 
                    Battle.u_pokemon.moves[3][5] = String.valueOf((int)(Integer.valueOf(Battle.u_pokemon.moves[3][5])*1.2));
            }
        }
    }
}
