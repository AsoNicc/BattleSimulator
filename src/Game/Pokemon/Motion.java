/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.Pokemon;

import android.content.Context;
import static android.content.Context.MODE_PRIVATE;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Nick
 * w:73 h:70
 */
public class Motion {
    private Bitmap attackSprite;
    private Handler handler = new Handler();
    private InputStream stream;
    private Runnable thread;
    private String[] move, info;
    private boolean targeted = true, MOVE_THREAD_RUNNING = false, SHIFT_TRtoBL_THREAD_RUNNING = false,
            SHIFT_LorR_THREAD_RUNNING = false, ELLIPSE_RtoL_THREAD_RUNNING = false,
            BLINK_THREAD_RUNNING = false, TERMINATED_SEQUENCE = false, DAMAGE_THREAD = false;
    private final byte attacker;
    private double BOTTOMLEFT_X, BOTTOMLEFT_Y, BOTTOMRIGHT_X, BOTTOMRIGHT_Y, 
            TOPLEFT_X, TOPLEFT_Y, TOPRIGHT_X, TOPRIGHT_Y, user_bottomside_midpoint_X,
            opponent_bottomside_midpoint_X;
        
        ;
    private float damage = 0f;
    private final AssetManager asset;
    private static Context context;
    private final int INC_RATE = 2, ARC_RATE = 30, TIME = 3000/*ms*/;
    private int i = 0, INDEX = 1 /*sequence starts at 1, index[0] = move#*/, soundByteCnt = 0;
    private static Options Opponent_Space_Options;
    protected int incX = 0, incY = 0;
    protected static boolean SHIFT_BLtoTR_LOCK = false;
    
    Motion(Context tContext, byte contender){
        context = tContext;
        Battle.context = context;
        asset = context.getAssets(); //Link assets  
        attacker = contender;
    }
    
    protected void initialize(String[] tokenMove){
        BOTTOMLEFT_X = (attacker == 1)? (Battle.SCREEN_WIDTH*5/6.0 - Animated.opponent.getWidth()/2.0)*Animated.OPPONENT_PLACEMENT_X// +(0) := opponent_shift_x
                : (Battle.SCREEN_WIDTH/6.0 - Animated.user.getWidth()/2.0)*Animated.USER_PLACEMENT_X;// +(0) := user_shift_x;
        BOTTOMLEFT_Y = (attacker == 1)? (Battle.SCREEN_HEIGHT/3.0 + Animated.opponent.getHeight()/2.0)*Animated.OPPONENT_PLACEMENT_Y// +(0) := opponent_shift_y;
                : (Battle.SCREEN_HEIGHT*2/3.0 + Animated.user.getHeight()/2.0)*Animated.USER_PLACEMENT_Y;// +(0) := user_shift_y;
        TOPLEFT_X = BOTTOMLEFT_X;
        TOPLEFT_Y = (attacker == 1)? BOTTOMLEFT_Y - Animated.opponent.getHeight()
                : BOTTOMLEFT_Y - Animated.user.getHeight();
        TOPRIGHT_X = (attacker == 1)? TOPLEFT_X + Animated.opponent.getWidth()
                : TOPLEFT_X + Animated.user.getWidth();
        TOPRIGHT_Y = TOPLEFT_Y;
        BOTTOMRIGHT_X = TOPRIGHT_X;
        BOTTOMRIGHT_Y = BOTTOMLEFT_Y;
        move = tokenMove;
        
        /* Obtain Settings for base stats of any u_pokemon */
        SharedPreferences details = context.getSharedPreferences("order", MODE_PRIVATE);
        if(!details.getBoolean("setState", false)){
            Settings load = new Settings(context);
            load.setMoveSequence();
            details = context.getSharedPreferences("order", MODE_PRIVATE);   
        }
        
        try {
            Set<String>tempSet = details.getStringSet(move[0], null);
            Iterator position = tempSet.iterator();
            info = new String[tempSet.size()];

            String data, index;
            int a;
            /* Unscramble hashset data */
            while(position.hasNext()){
                index = "";
                data = position.next().toString();
                /* Builds index of array out of string form */            
                for(a = 0; a < data.length(); a++){
                    if(data.charAt(a) == '_') break;
                    else index += data.charAt(a);
                }
                /* Load into the array */
                info[Integer.parseInt(index)] = data.substring(a + 1);
            }
            
            handler = new Handler();
        
            thread = new Runnable(){
                public void run(){
                    if(!threadRunning()){
                        try {
                            sequence(info[INDEX++]);                    
                        } catch(NotFoundException e) {
                            Battle.text.setText(e.toString());
                        } catch(ArrayIndexOutOfBoundsException e) { //Reached end of sequence
                            INDEX = 1;
                            refresh();
                            return;
                        } //catch(Exception e) {
//                            Battle.text.setText(e.toString());
//                        } 
                    }
                    handler.postDelayed(this, 0);
                }
            };

            handler.postDelayed(thread, 0);            
        } catch(Exception e) {
            Battle.text.setText(e.toString());
        }
    }
    
    private void move(){
        handler = new Handler();
        
        thread = new Runnable(){
            public void run(){
                if(attacker == 1){
                    if(Animated.USER_FRAME_BOTTOMLEFT_Y - 1 > Animated.OPPONENT_FRAME_BOTTOMLEFT_Y){
                        if(targeted || move[6].equals("TRACKING")){
                            targeted = false;
                            tracking(targeted);
                        }
                        shift_BLtoTR();                  
                    } else {
                        SHIFT_BLtoTR_LOCK = true;
                        MOVE_THREAD_RUNNING = false;
                        collision();
                        return;
                    }
                } else {
                    if(Animated.USER_FRAME_BOTTOMLEFT_Y - 1 > Animated.OPPONENT_FRAME_BOTTOMLEFT_Y){
                        if(targeted || move[6].equals("TRACKING")){
                            targeted = false;
                            tracking(targeted);
                        }
                        shift_BLtoTR();                    
                    } else {
                        SHIFT_BLtoTR_LOCK = true;
                        MOVE_THREAD_RUNNING = false;
                        collision();
                        return;
                    }
                }
                
//                Battle.text.setText("User_BL-X: " + Round(Animated.USER_FRAME_BOTTOMLEFT_X, 2) + " | Opponent_BL-X: " + Round(Animated.OPPONENT_FRAME_BOTTOMLEFT_X, 2) + " | User_Sprite_Width: " + Animated.user.getWidth() + " | Opponent_Sprite_Width: " + Animated.opponent.getWidth());
                handler.postDelayed(this, 0);
            }
        };

        if(Animated.USER_FRAME_BOTTOMLEFT_Y - 1 > Animated.OPPONENT_FRAME_BOTTOMLEFT_Y){
            handler.postDelayed(thread, 0);
            MOVE_THREAD_RUNNING = true;
        } else SHIFT_BLtoTR_LOCK = true;
    }
    
    private void shift_BLtoTR(){
        if(attacker == 1){
            Animated.opponent_shift_x -= incX;

            if(Animated.OPPONENT_FRAME_BOTTOMLEFT_Y - (-1*incY) >= Animated.USER_FRAME_BOTTOMLEFT_Y)
                incY = (int)Round((Animated.OPPONENT_FRAME_BOTTOMLEFT_Y - (-1*incY)) - Animated.USER_FRAME_BOTTOMLEFT_Y, 0);                    

            Animated.opponent_shift_y -= (-1*incY);  

            Battle.text.setText("Opponent_shift_x: " + Animated.opponent_shift_x + " | Opponent_shift_y: " + Animated.opponent_shift_y + " | IncX: " + incX + " | IncY: " + incY + 
                    " | OPPONENT_BOTTOMLEFT_Y: " + Animated.OPPONENT_FRAME_BOTTOMLEFT_Y + " | USER_BOTTOMLEFT_Y: " + Animated.USER_FRAME_BOTTOMLEFT_Y + " | Opponent_midpoint_x: " + opponent_bottomside_midpoint_X + " | User_midpoint_x: " + user_bottomside_midpoint_X);
            if(Round(Animated.OPPONENT_FRAME_BOTTOMLEFT_Y, 0) >= Round(Animated.USER_FRAME_BOTTOMLEFT_Y, 0)) SHIFT_TRtoBL_THREAD_RUNNING = false;
        } else {
            Animated.user_shift_x += incX;

            if(((Battle.SCREEN_HEIGHT*2/3.0 + Animated.user.getHeight()/2.0)*Animated.USER_PLACEMENT_Y + Animated.user_shift_y + -1*incY) < Animated.OPPONENT_FRAME_BOTTOMLEFT_Y) 
               incY = (int)(incY - (Animated.OPPONENT_FRAME_BOTTOMLEFT_Y - ((Battle.SCREEN_HEIGHT*2/3.0 + Animated.user.getHeight()/2.0)*Animated.USER_PLACEMENT_Y + Animated.user_shift_y + -1*incY)));                         

            Animated.user_shift_y += (-1*incY);
        }
    }
    
    private void collision(){
        if(!threadRunning()){
            if(true /*later check if attack is a contact attack that requires sprite-to-sprite collision 
            otherwise... it is projectile-to-sprite collision which is harder to determine */){
                if(attacker == 1){
                    TERMINATED_SEQUENCE = (
                            Math.abs((Animated.OPPONENT_FRAME_BOTTOMLEFT_X + Animated.OPPONENT_FRAME_BOTTOMRIGHT_X)/2 -
                            (Animated.USER_FRAME_BOTTOMLEFT_X + Animated.USER_FRAME_BOTTOMRIGHT_X)/2) /
                            ((Animated.USER_FRAME_BOTTOMLEFT_X + Animated.USER_FRAME_BOTTOMRIGHT_X)/2) > 0.1
                            &&
                            Math.abs(Animated.OPPONENT_FRAME_BOTTOMLEFT_Y - Animated.USER_FRAME_BOTTOMLEFT_Y) /
                            Animated.USER_FRAME_BOTTOMLEFT_Y <= 0.1
                    );
                } else {
                    TERMINATED_SEQUENCE = (
                            Math.abs((Animated.USER_FRAME_BOTTOMLEFT_X + Animated.USER_FRAME_BOTTOMRIGHT_X)/2 -
                            (Animated.OPPONENT_FRAME_BOTTOMLEFT_X + Animated.OPPONENT_FRAME_BOTTOMRIGHT_X)/2) /
                            ((Animated.OPPONENT_FRAME_BOTTOMLEFT_X + Animated.OPPONENT_FRAME_BOTTOMRIGHT_X)/2) > 0.1
                            &&
                            Math.abs(Animated.USER_FRAME_BOTTOMLEFT_Y - Animated.OPPONENT_FRAME_BOTTOMLEFT_Y) /
                            Animated.OPPONENT_FRAME_BOTTOMLEFT_Y <= 0.1
                    );
                }

    //            if(!TERMINATED_SEQUENCE){
    //                if(attacker == 1) ; //Freeze user's movements
    //                else ; //Freeze opponent's movements
    //            }
            }

            if(TERMINATED_SEQUENCE){
                int x = 0, y = 0;
                String message = "";

                if(attacker == 1){
                    x = (int)Round((Animated.USER_FRAME_TOPLEFT_X + Animated.USER_FRAME_TOPRIGHT_X)/2 - Battle.SCREEN_WIDTH/2f, 0);
                    y = (int)Round((Animated.USER_FRAME_TOPLEFT_Y + Animated.USER_FRAME_BOTTOMLEFT_Y)/2 - Battle.SCREEN_HEIGHT/2f + Animated.user.getHeight()/2f, 0);
                    message = "Dodged!";
                } else if(attacker == 2){
                    x = (int)Round((Animated.OPPONENT_FRAME_TOPLEFT_X + Animated.OPPONENT_FRAME_TOPRIGHT_X)/2 - Battle.SCREEN_WIDTH/2f, 0);
                    y = (int)Round((Animated.OPPONENT_FRAME_TOPLEFT_Y + Animated.OPPONENT_FRAME_BOTTOMLEFT_Y)/2 - Battle.SCREEN_HEIGHT/2f + Animated.opponent.getHeight()/2f, 0);
                    message = "Missed!";
                }            

                Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, x, y);
                toast.setMargin(0, 0);
                toast.show();
            }
        }    
    }
    
    private void shift_TRtoBL(){
        handler = new Handler();
        
        thread = new Runnable(){
            public void run(){
                if(attacker == 1){                    
                    Animated.opponent_shift_x = Math.min(Animated.opponent_shift_x + incX, 0);

                    if(((Battle.SCREEN_HEIGHT*2/3.0 + Animated.opponent.getHeight()/2.0)*Animated.OPPONENT_PLACEMENT_Y + Animated.opponent_shift_y + -1*incY) < BOTTOMLEFT_Y) 
                       incY = (int)(incY - (BOTTOMLEFT_Y - ((Battle.SCREEN_HEIGHT*2/3.0 + Animated.opponent.getHeight()/2.0)*Animated.OPPONENT_PLACEMENT_Y + Animated.opponent_shift_y + -1*incY)));                         

                    Animated.opponent_shift_y = Math.max(Animated.opponent_shift_y + (-1*incY), 0);
                    
                    Battle.text.setText("Opponent_shift_x: " + Animated.opponent_shift_x + " | Floor(x - 0.5): " + Math.floor(Animated.opponent_shift_x - 0.5) + " | Opponent_shift_y: " + Animated.opponent_shift_y + " | Floor(y + 0.5): " + Math.floor(Animated.opponent_shift_y + 0.5));
                    if(Animated.opponent_shift_x <= 0 && Animated.opponent_shift_y <= 0){
                        Animated.opponent_shift_x = 0;
                        Animated.opponent_shift_y = 0;
                        SHIFT_TRtoBL_THREAD_RUNNING = false;
                        return;
                    }
                } else {
                    if(Animated.user_shift_x - incX < 0) incX += (int)Round(Animated.user_shift_x - incX, 0);

                    Animated.user_shift_x -= incX;

                    if(Animated.user_shift_y - (-1*incY) > 0) incY -= (int)Round(Animated.user_shift_y - (-1*incY), 0);

                    Animated.user_shift_y -= (-1*incY);

                    Battle.text.setText("User_shift_x: " + Animated.user_shift_x + " | Floor(x - 0.5): " + Math.floor(Animated.user_shift_x - 0.5) + " | User_shift_y: " + Animated.user_shift_y + " | Floor(y + 0.5): " + Math.floor(Animated.user_shift_y + 0.5));
                    if(Math.floor(Animated.user_shift_x - 0.5) <= 0 && Math.floor(Animated.user_shift_y + 0.5) <= 0){
                        Animated.user_shift_x = 0;
                        Animated.user_shift_y = 0;
                        SHIFT_TRtoBL_THREAD_RUNNING = false;
                        return;
                    }
                }
                
                tracking(true);
                handler.postDelayed(this, 0);
            }
        };
                
        tracking(true);
        handler.postDelayed(thread, 0);
        SHIFT_TRtoBL_THREAD_RUNNING = true;
    }
    
    private void shift_LorR(int shift){
        if(TERMINATED_SEQUENCE) return;
        
        incX = shift;
        handler = new Handler();
        
        thread = new Runnable(){
            int x = 0;
            public void run(){
                if(x < incX && incX > 0){
                    if(attacker == 1) Animated.opponent_shift_x += 25;
                    else Animated.user_shift_x += 25/*px*/;
                    x += 25/*px*/;
                } else if(x > incX && incX < 0){
                    if(attacker == 1) Animated.opponent_shift_x -= 25;
                    else Animated.user_shift_x -= 25/*px*/;
                    x -= 25/*px*/;
                } else {
                    SHIFT_LorR_THREAD_RUNNING = false;
                    return;
                }
                
                Battle.text.setText("x: " + x + " | incX: " + incX + " | Sound count: " + soundByteCnt);
                handler.postDelayed(this, 0);
            }
        };
        
        handler.postDelayed(thread, 0);
        SHIFT_LorR_THREAD_RUNNING = true;
    }
    
    private void ellipseRtoL(final int originX, final int originY, final boolean displayEvent){
        if(TERMINATED_SEQUENCE) return;
        
        handler = new Handler();
        
        thread = new Runnable(){
            double step = 0.0174533; // damage (in radians) to add to theta each time (:= 1°)
            double theta = 0.0; // angle (radians) that will be increased each loop
            int a = (int)Math.ceil(attackSprite.getWidth()/2.0); // farthest distance from center on x-axis
            int b = (int)Math.ceil(attackSprite.getHeight()/2.0); // farthest distance from center on y-axis
            int X = originX; // X-origin
            int Y = originY; // Y-origin
            
            public void run(){
                if(theta - step*ARC_RATE/*[0°..180°]*/ <= Math.PI /* a.k.a (theta*180/Math.PI) <= 180° */){ // Half arc
                    if(displayEvent){
                        if(Round(theta*180/Math.PI, 0) == 90/*°*/ || Round(theta*180/Math.PI, 0) == 180/*°*/){
                            setAttackSprite(move[1]);
                            setAttackFrontside();
                        }
                    }
                    
                    if(attacker == 1){
                        Animated.opponent_shift_x = (a*Math.cos(theta) + X) - (Battle.SCREEN_WIDTH*5/6.0 - Animated.opponent.getWidth()/2.0)*Animated.OPPONENT_PLACEMENT_X + Animated.user.getWidth();
                        Animated.opponent_shift_y = (b*Math.sin(-theta) + Y) - (Battle.SCREEN_HEIGHT/3.0 + Animated.opponent.getHeight()/2.0)*Animated.OPPONENT_PLACEMENT_Y;
                    } else {
                        Animated.user_shift_x = (a*Math.cos(theta) + X) - (Battle.SCREEN_WIDTH/6.0 - Animated.user.getWidth()/2.0)*Animated.USER_PLACEMENT_X;// + Animated.opponent.getWidth();
                        Animated.user_shift_y = (b*Math.sin(-theta) + Y) - (Battle.SCREEN_HEIGHT*2/3.0 + Animated.user.getHeight()/2.0)*Animated.USER_PLACEMENT_Y;
                    }
                    
                    theta += step*ARC_RATE; /* Add step to theta */
                } else {
                    ELLIPSE_RtoL_THREAD_RUNNING = false;
                    return;                   
                }
                
                Battle.text.setText("User_BL-X: " + Animated.USER_FRAME_BOTTOMLEFT_X + " | User_BL-Y: " + Animated.USER_FRAME_BOTTOMLEFT_Y);
                handler.postDelayed(this, 0);
            }
        };
        
        handler.postDelayed(thread, 0);
        ELLIPSE_RtoL_THREAD_RUNNING = true;
    }
    
    private void tracking(boolean backtracking){
        Integer deltaX = null, deltaY = null;
        
        try {
            if(backtracking){
                user_bottomside_midpoint_X = (BOTTOMRIGHT_X - BOTTOMLEFT_X)/2 + BOTTOMLEFT_X;
                opponent_bottomside_midpoint_X = (Animated.USER_FRAME_BOTTOMRIGHT_X - Animated.USER_FRAME_BOTTOMLEFT_X)/2 + Animated.USER_FRAME_BOTTOMLEFT_X;                
                deltaX = (int)Round(opponent_bottomside_midpoint_X - user_bottomside_midpoint_X, 0);
                deltaY = (int)Round(BOTTOMLEFT_Y - Animated.USER_FRAME_BOTTOMLEFT_Y, 0);
            } else {
                user_bottomside_midpoint_X = (Animated.USER_FRAME_BOTTOMRIGHT_X - Animated.USER_FRAME_BOTTOMLEFT_X)/2 + Animated.USER_FRAME_BOTTOMLEFT_X;
                opponent_bottomside_midpoint_X = (Animated.OPPONENT_FRAME_BOTTOMRIGHT_X - Animated.OPPONENT_FRAME_BOTTOMLEFT_X)/2 + Animated.OPPONENT_FRAME_BOTTOMLEFT_X;
                deltaX = (int)Round(opponent_bottomside_midpoint_X - user_bottomside_midpoint_X, 0);
                deltaY = (int)Round(Animated.USER_FRAME_BOTTOMLEFT_Y - Animated.OPPONENT_FRAME_BOTTOMLEFT_Y, 0);
            }
            
            int gcd;
            
            do {
                gcd = GCD(deltaX, deltaY);            

                incX = deltaX/gcd;
                incY = deltaY/gcd;
                
                deltaX = (int)Math.ceil(deltaX/2.0);
                deltaY = (int)Math.ceil(deltaY/2.0);
                
            } while(((incX > 45*INC_RATE || incY > 74*INC_RATE) && Battle.SCREEN_HEIGHT > Battle.SCREEN_WIDTH) ||
                    ((incX > 74*INC_RATE || incY > 45*INC_RATE) && Battle.SCREEN_WIDTH > Battle.SCREEN_HEIGHT));
        } catch(Exception e) {
            incX = 0;
            incY = 0;
            //Battle.text.setText(e.toString() + " w/ DeltaX: " + deltaX + " | DeltaY: " + deltaY);
        }
    }
    
    private void setAttackSprite(String token){       
        try {
            stream = asset.open("moves/" + token.toLowerCase() + "/frame_" + i++ + ".png");
            Opponent_Space_Options = new Options();
            Bitmap unscaledBitmap = BitmapFactory.decodeStream(stream, null, Opponent_Space_Options);
            attackSprite = Bitmap.createScaledBitmap(unscaledBitmap, (int)Round(Opponent_Space_Options.outWidth*(Animated.scaleFactor), 0), (int)Round(Opponent_Space_Options.outHeight*Animated.scaleFactor, 0), true);
        } catch(Exception e) {
            Battle.text.setText(e.toString());
        }           
    }
    
    private void hit(){
        if(TERMINATED_SEQUENCE) return;
        
        clearAttackFrontside();
        
        if(damage > 0){
            i = 0;
            setAttackSprite("hit");
            setAttackFrontside();

            handler = new Handler();

            thread = new Runnable(){
                public void run(){
                    clearAttackFrontside();
                }
            };

            handler.postDelayed(thread, 333);
        }
    }
    
    private void blink(){
        if(TERMINATED_SEQUENCE) return;
        
        if(damage > 0){
            i = 0; //Reset i
            setAttackSprite("null");
            attackSprite = decodeSubsetBitmapFromSprite(attackSprite,
                    (attacker == 1)? Animated.user.getWidth() : Animated.opponent.getWidth(),
                    (attacker == 1)? Animated.user.getHeight() : Animated.opponent.getHeight());

            handler = new Handler();

            thread = new Runnable(){
                public void run(){
                    overrideSprite();

                    if(BLINK_THREAD_RUNNING) handler.postDelayed(this, 333);
                }
            };

            delay();
            BLINK_THREAD_RUNNING = true;
            handler.postDelayed(thread, 333);
        }
    }
    
    private void damage(){
        if(TERMINATED_SEQUENCE) return;
        
        handler = new Handler();
        damage = Damage.Calculator(move, attacker);
            
        thread = new Runnable(){
            float limit = (attacker == 1)? Animated.user_current_HP - damage
                    : ((Animated.opponent_current_HP - damage)/Animated.opponent_pokemon_HP)*100/*percentage*/;
            
            public void run(){
                Battle.text.setText("(" + limit + " < " + (((Animated.opponent_pokemon_HP*(1 - Animated.opponent_damage_percentage))/Animated.opponent_pokemon_HP)*100) + 
                        ") | Damage: " + damage + " | OpponentHP: " + Animated.opponent_pokemon_HP +
                        " | Opponent_type: " + Battle.o_pokemon.TYPE1 + "/" + Battle.o_pokemon.TYPE2 + 
                        " | User_type: " + Battle.u_pokemon.TYPE1 + "/" + Battle.u_pokemon.TYPE2 + 
                        " | STAB: " + Damage.s + " | Effectiveness: " + Damage.effect + 
                        " | Critical: " + Damage.c + " | Random: " + Damage.r);
                
                if(damage > 0){
                    if(attacker == 1){
                        Animated.user_damage_percentage += 0.01f;
                        if(limit < Animated.user_pokemon_HP*(1 - Animated.user_damage_percentage)) handler.postDelayed(this, 0);
                        else DAMAGE_THREAD = false;                        
                    } else if(attacker == 2) {
                        Animated.opponent_damage_percentage += 0.01f;
                        if(limit < (((Animated.opponent_pokemon_HP*(1 - Animated.opponent_damage_percentage))/Animated.opponent_pokemon_HP)*100)) handler.postDelayed(this, 0);
                        else DAMAGE_THREAD = false;                        
                    }
                } else DAMAGE_THREAD = false;                                    
            }
        };
        
        handler.postDelayed(thread, 0);
        DAMAGE_THREAD = true;
    }
    
    private void delay(){
        new Thread(){
            @Override
            public void run(){
                try {
                    Thread.sleep(TIME);
                    BLINK_THREAD_RUNNING = false;
                } catch (InterruptedException e) {
                } 
            }
        }.start();
    }
    
    private void overrideSprite(){ 
        if(attacker == 1) Animated.user = attackSprite;
        else Animated.opponent = attackSprite; 
    }
    
    private void setAttackFrontside(){ 
        if(TERMINATED_SEQUENCE) return;
        
        if(attacker == 1) Animated.user_frontSpace = attackSprite; 
        else Animated.opponent_frontSpace = attackSprite;
    }
    
    private void clearAttackFrontside(){ 
        if(attacker == 1) Animated.user_frontSpace = null; 
        else Animated.opponent_frontSpace = null;
    }
    
    private void setAttackBackside(){ 
        if(attacker == 1) Animated.user_backSpace = attackSprite; 
        else Animated.opponent_backSpace = attackSprite;
    }
    
    private void clearAttackBackside(){ 
        if(attacker == 1) Animated.user_frontSpace = null; 
        else Animated.opponent_frontSpace = null;
    }
    
    private void playSoundEffect(int soundbyte){
        if(TERMINATED_SEQUENCE) return;
        
        if(soundbyte == R.raw.punch){
            if(Damage.effect == 0) soundbyte = -1;
            else if(Damage.effect > 1) soundbyte = R.raw.superdamage;
            else if(Damage.effect < 1) soundbyte = R.raw.notverydamage;
        }
        
        if(soundbyte >= 0){
            MediaPlayer sfx = MediaPlayer.create(context, soundbyte);
            sfx.start(); //Play sound
        }
    }
    
    private void displayMessage(){
        if(TERMINATED_SEQUENCE) return;
        
        if(!Damage.IsaCrit && Damage.effect == 1) return;
        
        String message = "";
        int x = 0, y = 0;
        
        if(Damage.IsaCrit && Damage.effect != 0) message += "Critical hit! ";
                    
        if(Damage.effect == 0) message += "Immune";
        else if(Damage.effect > 1) message += "It's super effective!";
        else if(Damage.effect < 1) message += "It's not very effective!";        
        
        if(attacker == 1){
            x = (int)Round((Animated.USER_FRAME_TOPLEFT_X + Animated.USER_FRAME_TOPRIGHT_X)/2 - Battle.SCREEN_WIDTH/2f, 0);
            y = (int)Round((Animated.USER_FRAME_TOPLEFT_Y + Animated.USER_FRAME_BOTTOMLEFT_Y)/2 - Battle.SCREEN_HEIGHT/2f + Animated.user.getHeight()/2f, 0);
        } else if(attacker == 2){
            x = (int)Round((Animated.OPPONENT_FRAME_TOPLEFT_X + Animated.OPPONENT_FRAME_TOPRIGHT_X)/2 - Battle.SCREEN_WIDTH/2f, 0);
            y = (int)Round((Animated.OPPONENT_FRAME_TOPLEFT_Y + Animated.OPPONENT_FRAME_BOTTOMLEFT_Y)/2 - Battle.SCREEN_HEIGHT/2f + Animated.opponent.getHeight()/2f, 0);
        }            
        
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, x, y);
        toast.setMargin(0, 0);
        toast.show();
    }
    
    private Bitmap decodeSubsetBitmapFromSprite(Bitmap bmp, int width, int height){
        return Bitmap.createBitmap(bmp, 0/*starting-x TL*/, 0/*starting-y TL*/, width/*ending-x BR*/, height/*ending-y BR*/);
    }
    
    private void refresh(){
        handler = new Handler();
        
        thread = new Runnable(){
            public void run(){
                if(!threadRunning() && !DAMAGE_THREAD){
                    targeted = true;
                    MOVE_THREAD_RUNNING = false;
                    SHIFT_LorR_THREAD_RUNNING = false;
                    ELLIPSE_RtoL_THREAD_RUNNING = false;
                    BLINK_THREAD_RUNNING = false;
                    TERMINATED_SEQUENCE = false;
                    damage = 0f;
                    i = 0;
                    soundByteCnt = 0;

                    if(attacker == 1){
                        Battle.o_actChosen = false;
                        Animated.opponent_speed_inc = 0;
                        Animated.opponent_speedbar = 0;
                        Animated.opponent_speedbar_percentage = 0;
                        Animated.opponent_actReady = false;
                        Animated.OPPONENT_SPEED_LOCK = false;
                    } else {
                        Battle.u_actChosen = false;
                        Animated.user_speed_inc = 0;
                        Animated.user_speedbar = 0;
                        Animated.user_speedbar_percentage = 0;
                        Animated.user_actReady = false;
                        Animated.USER_SPEED_LOCK = false;
                    }

                    return;
                }
                
                handler.postDelayed(this, 0);
            }
        };
        
        handler.postDelayed(thread, 0);
    }
    
    protected static double Round(double number, int placeAfterDecimal){        
        double doa = Math.pow(10, placeAfterDecimal); //degree of accuracy
        
        if(((number*doa) - (int)(number*doa)) >= .5) return ((int)(number*doa) + 1)/doa;
        else {
            if(number < 0) return ((int)(number*doa) - 1)/doa;
            else return ((int)(number*doa))/doa;
        }
    }
    
    private int GCD(int dividend, int divisor) {
        int remainder = dividend%divisor;

        if (remainder == 0) return divisor;
        else return GCD(divisor, remainder);
    }
    
    private void sequence(String token){
        if(token.equals("0")) move();
        else if(token.equals("1")) setAttackSprite(move[1]);
        else if(token.equals("2")) shift_LorR((Integer.parseInt(formula(token, move[0], null))));
        else if(token.equals("3")) playSoundEffect(Integer.parseInt(formula(token, move[0], soundByteCnt++)));
        else if(token.equals("4")) setAttackFrontside();
        else if(token.equals("5")) ellipseRtoL(Integer.parseInt(formula(token, move[0], 0)), Integer.parseInt(formula(token, move[0], 1)), Boolean.parseBoolean(formula(token, move[0], 2)));
        else if(token.equals("6")) damage();
        else if(token.equals("7")) hit();
        else if(token.equals("8")) blink();
        else if(token.equals("9")) displayMessage();
        else if(token.equals("10")) shift_TRtoBL();
    }
    
    private String formula(String seqNo, String moveNo, Integer paramNo){
        if(seqNo.equals("2") && moveNo.equals("1") && paramNo == null) return String.valueOf((int)Math.ceil(attackSprite.getWidth()/2.0));
        if(seqNo.equals("3") && moveNo.equals("1") && paramNo == 0) return String.valueOf(R.raw.swing);
        if(seqNo.equals("3") && moveNo.equals("1") && paramNo == 1) return String.valueOf(R.raw.punch);
        if(seqNo.equals("5") && moveNo.equals("1") && paramNo == 0) return String.valueOf((int)(Animated.USER_FRAME_BOTTOMLEFT_X - Math.ceil(attackSprite.getWidth()/2.0)));
        if(seqNo.equals("5") && moveNo.equals("1") && paramNo == 1) return String.valueOf((int)Animated.USER_FRAME_BOTTOMLEFT_Y);
        if(seqNo.equals("5") && moveNo.equals("1") && paramNo == 2) return "true";
        return null;
    }
    
    private boolean threadRunning(){
        return (MOVE_THREAD_RUNNING == true || SHIFT_LorR_THREAD_RUNNING == true ||
            ELLIPSE_RtoL_THREAD_RUNNING == true || SHIFT_TRtoBL_THREAD_RUNNING == true);
    }
}