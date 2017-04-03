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
    private Handler globalHandler = new Handler();
    private InputStream stream;
    private Runnable thread;
    private String[] move, info;
    private static boolean MOVE_THREAD_RUNNING = false, BACKPEDAL_THREAD_RUNNING = false,
            SHIFT_LorR_THREAD_RUNNING = false, ELLIPSE_LorR_THREAD_RUNNING = false,
            BLINK_THREAD_RUNNING = false, TERMINATED_SEQUENCE = false, DAMAGE_THREAD = false,
            DELAY_THREAD_RUNNING = false, COLLISION_THREAD_RUNNING = false;
    private final byte attacker;
    private double BOTTOMLEFT_X, BOTTOMLEFT_Y, BOTTOMRIGHT_X, BOTTOMRIGHT_Y, 
            TOPLEFT_X, TOPLEFT_Y, TOPRIGHT_X, TOPRIGHT_Y, user_bottomside_midpoint_X,
            opponent_bottomside_midpoint_X;  
    private float damageAmount = 0f;
    private final AssetManager asset;
    private static Context context;
    private final int INC_RATE = 2, ARC_RATE = 30;
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
            
            globalHandler = new Handler();
        
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
                        } 
                    }
                    globalHandler.postDelayed(this, 0);
                }
            };

            globalHandler.postDelayed(thread, 0);            
        } catch(Exception e) {
            Battle.text.setText(e.toString());
        }
    }
    
    private void move(){
        globalHandler = new Handler();
        
        thread = new Runnable(){
            boolean x_exit = false, y_exit = false, targeted = true;
            
            public void run(){
                if(attacker == 1){
                    if(targeted /*@ least once*/ || move[6].equals("TRACKING")){
                        targeted = false; //Everytime after initial
                        tracking(targeted); //Towards target
                    }

                    if(move[6].equals("TRACKING")){
                        if(Round(Animated.OPPONENT_FRAME_BOTTOMLEFT_X - incX + Animated.opponent.getWidth()/2.0, 0) <= Round(Animated.USER_FRAME_BOTTOMLEFT_X + Animated.user.getWidth()/2.0, 0)){
                            incX -= (int)Round((Animated.USER_FRAME_BOTTOMLEFT_X + Animated.user.getWidth()/2.0) - (Animated.OPPONENT_FRAME_BOTTOMLEFT_X - incX + Animated.opponent.getWidth()/2.0), 0);
                            Animated.opponent_shift_x -= incX;
                            x_exit = true;
                        }
                    }
                    
                    if(!x_exit) Animated.opponent_shift_x -= incX;

                    if(Round(Animated.OPPONENT_FRAME_BOTTOMLEFT_Y - (-1*incY), 0) >= Round(Animated.USER_FRAME_BOTTOMLEFT_Y, 0)){
                        incY -= (int)Round((Animated.OPPONENT_FRAME_BOTTOMLEFT_Y - (-1*incY)) - Animated.USER_FRAME_BOTTOMLEFT_Y, 0);
                        Animated.opponent_shift_y -= (-1*incY);
                        y_exit = true;
                    }

                    if(!y_exit) Animated.opponent_shift_y -= (-1*incY);
                    
                    /*if(move[6].equals("TRACKING")){ //Save this clause for super-tracking, available for the opponent only
                        if(x_exit && y_exit){
                            MOVE_THREAD_RUNNING = false;
                            delay(0);
                            collision();
                            return;
                        }
                    } else*/ if(y_exit || Animated.OPPONENT_FRAME_BOTTOMRIGHT_X < 0){
                        MOVE_THREAD_RUNNING = false;
                        delay(0);
                        collision();
                        return;
                    }
                } else {
                    if(targeted /*@ least once*/ || move[6].equals("TRACKING")){
                        targeted = false; //Everytime after initial
                        tracking(targeted); //Towards target
                    }

                    if(move[6].equals("TRACKING")){
                        if(Round(Animated.USER_FRAME_BOTTOMLEFT_X + incX + Animated.user.getWidth()/2.0, 0) >= Round(Animated.OPPONENT_FRAME_BOTTOMLEFT_X + Animated.opponent.getWidth()/2.0, 0)){
                            incX -= (int)Round((Animated.USER_FRAME_BOTTOMLEFT_X + incX + Animated.user.getWidth()/2.0) - (Animated.OPPONENT_FRAME_BOTTOMLEFT_X + Animated.opponent.getWidth()/2.0), 0);
                            Animated.user_shift_x += incX;
                            x_exit = true;
                        }
                    }
                    
                    if(!x_exit) Animated.user_shift_x += incX;

                    if(Round(Animated.USER_FRAME_BOTTOMLEFT_Y + (-1*incY), 0) <= Round(Animated.OPPONENT_FRAME_BOTTOMLEFT_Y, 0)){
                        incY -= (int)Round(Animated.OPPONENT_FRAME_BOTTOMLEFT_Y - (Animated.USER_FRAME_BOTTOMLEFT_Y + (-1*incY)), 0);
                        Animated.user_shift_y += (-1*incY);
                        y_exit = true;
                    }

                    if(!y_exit) Animated.user_shift_y += (-1*incY);
                    
                    if(move[6].equals("TRACKING")){
                        if(x_exit && y_exit || Animated.USER_FRAME_TOPLEFT_Y + Animated.user.getHeight()/2.0 <= Battle.SCREEN_HEIGHT*0.25){
                            MOVE_THREAD_RUNNING = false;
                            delay(0);
                            collision();
                            return;
                        }
                    } else if(y_exit || Animated.USER_FRAME_BOTTOMRIGHT_X > Battle.SCREEN_WIDTH){
                        MOVE_THREAD_RUNNING = false;
                        delay(0);
                        collision();
                        return;
                    }
                }
                
                globalHandler.postDelayed(this, 0);
            }
        };
        
        globalHandler.postDelayed(thread, 0);
        MOVE_THREAD_RUNNING = true;        
    }
    
    private void collision(){
        globalHandler = new Handler();
        
        thread = new Runnable(){
            Double cond1, cond2;
            
            @Override
            public void run(){
                if(COLLISION_THREAD_RUNNING && !DELAY_THREAD_RUNNING){
                    if(true /*later check if attack is a contact attack that requires sprite-to-sprite collision 
                    otherwise... it is projectile-to-sprite collision which is harder to determine */){
                        if(attacker == 1){
                            TERMINATED_SEQUENCE = (
                                    (cond1 = Math.abs(Round(Math.abs((Animated.OPPONENT_FRAME_BOTTOMLEFT_X + Animated.opponent.getWidth()/2.0) /
                                    (Animated.USER_FRAME_BOTTOMLEFT_X + Animated.user.getWidth()/2.0)), 2) - 1)) > 0.1
                                    ||
                                    (cond2 = Math.abs(Round(Math.abs((Animated.USER_FRAME_BOTTOMLEFT_Y - Animated.OPPONENT_FRAME_BOTTOMLEFT_Y) /
                                    Animated.USER_FRAME_BOTTOMLEFT_Y), 2))) > 0.1
                            );

                            if(!TERMINATED_SEQUENCE) Animated.OPPONENT_FRAME_BOTTOMLEFT_Y = Animated.USER_FRAME_BOTTOMLEFT_Y;
                        } else {
                            TERMINATED_SEQUENCE = (
                                    (cond1 = Math.abs(Round(Math.abs((Animated.USER_FRAME_BOTTOMLEFT_X + Animated.user.getWidth()/2.0) /
                                    (Animated.OPPONENT_FRAME_BOTTOMLEFT_X + Animated.opponent.getWidth()/2.0)), 2) - 1)) > 0.1
                                    ||
                                    (cond2 = Math.abs(Round(Math.abs((Animated.USER_FRAME_BOTTOMLEFT_Y - Animated.OPPONENT_FRAME_BOTTOMLEFT_Y) /
                                    Animated.USER_FRAME_BOTTOMLEFT_Y), 2))) > 0.1
                            );

                            if(!TERMINATED_SEQUENCE) Animated.USER_FRAME_BOTTOMLEFT_Y = Animated.OPPONENT_FRAME_BOTTOMLEFT_Y;
                        }
                    }

                    if(!TERMINATED_SEQUENCE){
        //                if(attacker == 1){ ; //Freeze user's movements
        //                else ; //Freeze opponent's movements
                    } else {
                        int x, y;
                        String message;

                        if(attacker == 1){
                            x = (int)Round((Animated.USER_FRAME_TOPLEFT_X + Animated.USER_FRAME_TOPRIGHT_X)/2 - Battle.SCREEN_WIDTH/2f, 0);
                            y = (int)Round((Animated.USER_FRAME_TOPLEFT_Y + Animated.USER_FRAME_BOTTOMLEFT_Y)/2 - Battle.SCREEN_HEIGHT/2f + Animated.user.getHeight()/2f, 0);
                            message = "Dodged!";
                        } else {
                            x = (int)Round((Animated.OPPONENT_FRAME_TOPLEFT_X + Animated.OPPONENT_FRAME_TOPRIGHT_X)/2 - Battle.SCREEN_WIDTH/2f, 0);
                            y = (int)Round((Animated.OPPONENT_FRAME_TOPLEFT_Y + Animated.OPPONENT_FRAME_BOTTOMLEFT_Y)/2 - Battle.SCREEN_HEIGHT/2f + Animated.opponent.getHeight()/2f, 0);
                            message = "Missed!";
                        }            

                        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, x, y);
                        toast.setMargin(0, 0);
                        toast.show();
                    }
                    COLLISION_THREAD_RUNNING = false;
                    return;
                }
                globalHandler.postDelayed(this, 0);
            }
        };
        
        globalHandler.postDelayed(thread, 0);
        COLLISION_THREAD_RUNNING = true;
    }
    
    private void backpedal(){
        globalHandler = new Handler();
        
        thread = new Runnable(){
            boolean x_exit = false, y_exit = false;
            
            public void run(){
                if(attacker == 1){                    
                    if(Round(Animated.OPPONENT_FRAME_BOTTOMLEFT_X + incX + Animated.opponent.getWidth()/2.0, 0) >= Round(BOTTOMLEFT_X + Animated.opponent.getWidth()/2.0, 0)){
                        incX -= (int)Round((Animated.OPPONENT_FRAME_BOTTOMLEFT_X + incX + Animated.opponent.getWidth()/2.0) - (BOTTOMLEFT_X + Animated.opponent.getWidth()/2.0), 0);
                        Animated.opponent_shift_x += incX;
                        x_exit = true;
                    }
                    /* opponent_shift_x is already negative to start off w/, 
                     * relative to its initial stance, (-n, 0] */
                    if(!x_exit) Animated.opponent_shift_x += incX;

                    if(Round(Animated.OPPONENT_FRAME_BOTTOMLEFT_Y + (-1*incY), 0) <= Round(BOTTOMLEFT_Y, 0)){
                        incY -= (int)Round(BOTTOMLEFT_Y - (Animated.OPPONENT_FRAME_BOTTOMLEFT_Y + (-1*incY)), 0); 
                        Animated.opponent_shift_y += (-1*incY);
                        y_exit = true;
                    }

                    //opponent_shift_y, (+n, 0]
                    if(!y_exit) Animated.opponent_shift_y += (-1*incY);
                    
                    Battle.text.setText("Opponent_shift_x: " + Animated.opponent_shift_x + " | incX: " + incX + " | Opponent_shift_y: " + Animated.opponent_shift_y + " | incY: " + incY);
                    if(x_exit && y_exit){ //then, it is acceptable to leave thread
                        Animated.opponent_shift_x = 0; //Ensures proper reset
                        Animated.opponent_shift_y = 0; //Ensures proper reset
                        BACKPEDAL_THREAD_RUNNING = false; //Release main thread lock
                        return;
                    }
                } else {
                    if(Round(Animated.USER_FRAME_BOTTOMLEFT_X - incX + Animated.user.getWidth()/2.0, 0) <= Round(BOTTOMLEFT_X + Animated.user.getWidth()/2.0, 0)){
                        incX -= (int)Round((BOTTOMLEFT_X + Animated.user.getWidth()/2.0) - (Animated.USER_FRAME_BOTTOMLEFT_X - incX + Animated.user.getWidth()/2.0), 0);
                        Animated.user_shift_x -= incX;
                        x_exit = true;
                    }
                    
                    if(!x_exit) Animated.user_shift_x -= incX;

                    if(Round(Animated.USER_FRAME_BOTTOMLEFT_Y - (-1*incY), 0) >= Round(BOTTOMLEFT_Y, 0)){
                        incY -= (int)Round(BOTTOMLEFT_Y - (Animated.USER_FRAME_BOTTOMLEFT_Y - (-1*incY)), 0);
                        Animated.user_shift_y -= (-1*incY);
                        y_exit = true;
                    }

                    if(!y_exit) Animated.user_shift_y -= (-1*incY);

                    Battle.text.setText("User_shift_x: " + Animated.user_shift_x + " | incX: " + incX + " | User_shift_y: " + Animated.user_shift_y + " | incY: " + incY);
                    if(x_exit && y_exit){ //then, it is acceptable to leave thread
                        Animated.user_shift_x = 0; //Ensures proper reset
                        Animated.user_shift_y = 0; //Ensures proper reset
                        BACKPEDAL_THREAD_RUNNING = false; //Release main thread lock
                        return;
                    }
                }
                
                tracking(true); //Always true in this context
                globalHandler.postDelayed(this, 0);
            }
        };
                
        tracking(true); //Always true in this context
        globalHandler.postDelayed(thread, 0);
        BACKPEDAL_THREAD_RUNNING = true;
    }
    
    private void shift_LorR(int shift){
        if(TERMINATED_SEQUENCE) return;
        
        incX = shift;
        globalHandler = new Handler();
        
        thread = new Runnable(){
            int cnt = 0;
            public void run(){
                if(cnt < incX && incX > 0){
                    if(attacker == 1) Animated.opponent_shift_x -= 25;
                    else Animated.user_shift_x += 25/*px*/;
                    cnt += 25/*px*/;
                } else if(cnt > incX && incX < 0){
                    if(attacker == 1) Animated.opponent_shift_x += 25;
                    else Animated.user_shift_x -= 25/*px*/;
                    cnt -= 25/*px*/;
                } else {
                    SHIFT_LorR_THREAD_RUNNING = false;
                    return;
                }
                
                Battle.text.setText("Count: " + cnt + " | incX: " + incX + " | Sound count: " + soundByteCnt);
                globalHandler.postDelayed(this, 0);
            }
        };
        
        globalHandler.postDelayed(thread, 0);
        SHIFT_LorR_THREAD_RUNNING = true;
    }
    
    private void ellipseLorR(final int originX, final int originY, final boolean displayEvent){
        if(TERMINATED_SEQUENCE) return;
        
        globalHandler = new Handler();
        
        thread = new Runnable(){
            final double STEP = 0.0174533; // damageAmount (in radians) to add to theta each time (:= 1°)
            double theta = 0.0; // angle (radians) that will be increased each loop
            float a = attackSprite.getWidth()/2f; // farthest distance from center on cnt-axis
            float b = attackSprite.getHeight()/2f; // farthest distance from center on y-axis
            int X = originX; // X-origin
            int Y = originY; // Y-origin
            
            public void run(){
                if(theta - STEP*ARC_RATE/*[0°..180°]*/ <= Math.PI /* a.k.a (theta*180/Math.PI) <= 180° */){ // Half arc
                    if(displayEvent){
                        if(Round(theta*180/Math.PI, 0) == 90/*°*/ || Round(theta*180/Math.PI, 0) == 180/*°*/){
                            setAttackSprite(move[1], (attacker == 1));
                            setAttackFrontside();
                        }
                    }
                    
                    if(attacker == 1){
                        Animated.opponent_shift_x = (int)Round((a*Math.cos(Math.PI + theta) + X) - (Battle.SCREEN_WIDTH*5/6.0 - Animated.opponent.getWidth()/2.0)*Animated.OPPONENT_PLACEMENT_X, 0);
                        Animated.opponent_shift_y = (int)Round((b*Math.sin(-theta) + Y) - (Battle.SCREEN_HEIGHT/3.0 + Animated.opponent.getHeight()/2.0)*Animated.OPPONENT_PLACEMENT_Y, 0);
                        Battle.text.setText("Opponent_BL-X: " + Animated.OPPONENT_FRAME_BOTTOMLEFT_X + " | Shift_x: " + Animated.opponent_shift_x + " | Opponent_BL-Y: " + Animated.OPPONENT_FRAME_BOTTOMLEFT_Y + " | Shift_y: " + Animated.opponent_shift_y);
                    } else {
                        Animated.user_shift_x = (int)Round((a*Math.cos(theta) + X) - (Battle.SCREEN_WIDTH/6.0 - Animated.user.getWidth()/2.0)*Animated.USER_PLACEMENT_X, 0);
                        Animated.user_shift_y = (int)Round((b*Math.sin(-theta) + Y) - (Battle.SCREEN_HEIGHT*2/3.0 + Animated.user.getHeight()/2.0)*Animated.USER_PLACEMENT_Y, 0);
                        Battle.text.setText("User_BL-X: " + Animated.USER_FRAME_BOTTOMLEFT_X + " | Shift_x: " + Animated.user_shift_x + " | User_BL-Y: " + Animated.USER_FRAME_BOTTOMLEFT_Y + " | Shift_y: " + Animated.user_shift_y);
                    }
                    
                    theta += STEP*ARC_RATE; /* Add STEP to theta */
                } else {
                    ELLIPSE_LorR_THREAD_RUNNING = false;
                    return;                   
                }
                
                globalHandler.postDelayed(this, 0);
            }
        };
        
        globalHandler.postDelayed(thread, 0);
        ELLIPSE_LorR_THREAD_RUNNING = true;
    }
    
    private void tracking(boolean backtracking){
        /* Outputs of calculations that are assigned to these variables MUST
         * ALWAYS REPRESENTED AS + */
        Integer deltaX = null, deltaY = null;
        
        try {
            if(backtracking){
                user_bottomside_midpoint_X = (BOTTOMRIGHT_X - BOTTOMLEFT_X)/2 + BOTTOMLEFT_X;
                opponent_bottomside_midpoint_X = (Animated.USER_FRAME_BOTTOMRIGHT_X - Animated.USER_FRAME_BOTTOMLEFT_X)/2 + Animated.USER_FRAME_BOTTOMLEFT_X;                
                deltaX = (int)Math.abs(Round(opponent_bottomside_midpoint_X - user_bottomside_midpoint_X, 0));
                deltaY = (int)Math.abs(Round(BOTTOMLEFT_Y - Animated.USER_FRAME_BOTTOMLEFT_Y, 0));
            } else {
                user_bottomside_midpoint_X = (Animated.USER_FRAME_BOTTOMRIGHT_X - Animated.USER_FRAME_BOTTOMLEFT_X)/2 + Animated.USER_FRAME_BOTTOMLEFT_X;
                opponent_bottomside_midpoint_X = (Animated.OPPONENT_FRAME_BOTTOMRIGHT_X - Animated.OPPONENT_FRAME_BOTTOMLEFT_X)/2 + Animated.OPPONENT_FRAME_BOTTOMLEFT_X;
                deltaX = (int)Math.abs(Round(opponent_bottomside_midpoint_X - user_bottomside_midpoint_X, 0));
                deltaY = (int)Math.abs(Round(Animated.USER_FRAME_BOTTOMLEFT_Y - Animated.OPPONENT_FRAME_BOTTOMLEFT_Y, 0));
            }
            
            int gcd;
            
            do {
                gcd = GCD(deltaY, deltaX);            

                incX = deltaX/(int)Math.max(gcd, GCD(gcd, Battle.SCREEN_WIDTH));
                incY = deltaY/(int)Math.max(gcd, GCD(gcd, Battle.SCREEN_HEIGHT));

                if(incX < Math.ceil(((Battle.SCREEN_HEIGHT > Battle.SCREEN_WIDTH)? 45: 74)/2.0) 
                        && incY < Math.ceil(((Battle.SCREEN_HEIGHT > Battle.SCREEN_WIDTH)? 74: 45)/2.0)){
                    incX += (Battle.SCREEN_HEIGHT > Battle.SCREEN_WIDTH)? 37: 23;
                    incY += (Battle.SCREEN_HEIGHT > Battle.SCREEN_WIDTH)? 23: 0;
                }
                
                deltaX = (int)Math.ceil(deltaX/2.0);
                deltaY = (int)Math.ceil(deltaY/2.0);
                
            } while(((incX > 45*INC_RATE || incY > 74*INC_RATE) && Battle.SCREEN_HEIGHT > Battle.SCREEN_WIDTH) ||
                    ((incX > 74*INC_RATE || incY > 45*INC_RATE) && Battle.SCREEN_WIDTH > Battle.SCREEN_HEIGHT));
        } catch(ArithmeticException e) {
            Battle.text.setText(e.toString() + " w/ DeltaX: " + deltaX + " | DeltaY: " + deltaY);
        }
    }
    
    private void setAttackSprite(String token, boolean mirror){       
        try {
            stream = asset.open("moves/" + token.toLowerCase() + "/frame_" + ((mirror)? '-' : "") + i++ + ".png");
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
        
        if(damageAmount > 0){
            i = 0;
            setAttackSprite("hit", false);
            setAttackFrontside();

            Handler hitHandler = new Handler();

            thread = new Runnable(){
                public void run(){
                    clearAttackFrontside();
                }
            };

            hitHandler.postDelayed(thread, 333);
        }
    }
    
    private void blink(){
        if(TERMINATED_SEQUENCE) return;
        
        if(damageAmount > 0){
            i = 0; //Reset i
            setAttackSprite("null", false);
            attackSprite = decodeSubsetBitmapFromSprite(attackSprite,
                    (attacker == 1)? Animated.user.getWidth() : Animated.opponent.getWidth(),
                    (attacker == 1)? Animated.user.getHeight() : Animated.opponent.getHeight());

            globalHandler = new Handler();

            thread = new Runnable(){
                Handler blinkHandler = globalHandler;
                public void run(){
                    overrideSprite();

                    if(DELAY_THREAD_RUNNING) blinkHandler.postDelayed(this, 333);
                    else BLINK_THREAD_RUNNING = false;
                        
                }
            };

            delay(3000);
            BLINK_THREAD_RUNNING = true;
            globalHandler.postDelayed(thread, 0);
        }
    }
    
    private void damage(){
        if(TERMINATED_SEQUENCE) return;
        
        globalHandler = new Handler();
        damageAmount = Damage.Calculator(move, attacker);
            
        thread = new Runnable(){
            Handler ref = globalHandler;
            float limit = (attacker == 1)? Animated.user_current_HP - damageAmount
                    : ((Animated.opponent_current_HP - damageAmount)/Animated.opponent_pokemon_HP)*100/*percentage*/;
            
            public void run(){
//                Battle.text.setText("(" + limit + " < " + (((Animated.opponent_pokemon_HP*(1 - Animated.opponent_damage_percentage))/Animated.opponent_pokemon_HP)*100) + 
//                        ") | Damage: " + damageAmount + " | OpponentHP: " + Animated.opponent_pokemon_HP +
//                        " | Opponent_type: " + Battle.o_pokemon.TYPE1 + "/" + Battle.o_pokemon.TYPE2 + 
//                        " | User_type: " + Battle.u_pokemon.TYPE1 + "/" + Battle.u_pokemon.TYPE2 + 
//                        " | STAB: " + Damage.s + " | Effectiveness: " + Damage.effect + 
//                        " | Critical: " + Damage.c + " | Random: " + Damage.r);
                
                if(damageAmount > 0){
                    if(attacker == 1){
                        Animated.user_damage_percentage += 0.01f;
                        if(limit < Animated.user_pokemon_HP*(1 - Animated.user_damage_percentage)) ref.postDelayed(this, 0);
                        else DAMAGE_THREAD = false;                        
                    } else if(attacker == 2) {
                        Animated.opponent_damage_percentage += 0.01f;
                        if(limit < (((Animated.opponent_pokemon_HP*(1 - Animated.opponent_damage_percentage))/Animated.opponent_pokemon_HP)*100)) ref.postDelayed(this, 0);
                        else DAMAGE_THREAD = false;                        
                    }
                } else DAMAGE_THREAD = false;                                    
            }
        };
        
        globalHandler.postDelayed(thread, 0);
        DAMAGE_THREAD = true;
    }
    
    private void delay(final int millisecs){        
        globalHandler = new Handler();
            
        thread = new Runnable(){
            Handler timer = globalHandler;
            
            @Override
            public void run(){
                for(int i = 0; i < millisecs; i++){}
                
                DELAY_THREAD_RUNNING = false; 
            }
        };
        
        globalHandler.postDelayed(thread, 0);
        DELAY_THREAD_RUNNING = true;
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
        return Bitmap.createBitmap(bmp, 0/*starting-cnt TL*/, 0/*starting-y TL*/, width/*ending-cnt BR*/, height/*ending-y BR*/);
    }
    
    private void refresh(){
        globalHandler = new Handler();
        
        thread = new Runnable(){
            Handler refreshHandler = globalHandler;
            
            public void run(){
                if(!threadRunning() && !DAMAGE_THREAD){
                    MOVE_THREAD_RUNNING = false;
                    COLLISION_THREAD_RUNNING = false;
                    SHIFT_LorR_THREAD_RUNNING = false;
                    ELLIPSE_LorR_THREAD_RUNNING = false;
                    BLINK_THREAD_RUNNING = false;
                    DELAY_THREAD_RUNNING = false;
                    TERMINATED_SEQUENCE = false;
                    damageAmount = 0f;
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
                
                refreshHandler.postDelayed(this, 0);
            }
        };
        
        globalHandler.postDelayed(thread, 0);
    }
    
    protected static double Round(double number, int placeAfterDecimal){        
        double doa = Math.pow(10, placeAfterDecimal); //degree of accuracy
        
        if(((number*doa) - (int)(number*doa)) >= .5) return ((int)(number*doa) + 1)/doa;
        else {
            if(number < 0) return ((int)(number*doa) - 1)/doa;
            else return ((int)(number*doa))/doa;
        }
    }
    
    private int GCD(int dividend_Y, int divisor_X) {
        int remainder = dividend_Y%divisor_X;

        if (remainder == 0) return divisor_X;
        else return GCD(divisor_X, remainder);
    }
    
    private void sequence(String token){
        if(token.equals("0")) move();
        else if(token.equals("1")) setAttackSprite(move[1], (attacker == 1));
        else if(token.equals("2")) shift_LorR((Integer.parseInt(formula(token, move[0], null))));
        else if(token.equals("3")) playSoundEffect(Integer.parseInt(formula(token, move[0], soundByteCnt++)));
        else if(token.equals("4")) setAttackFrontside();
        else if(token.equals("5")) ellipseLorR(Integer.parseInt(formula(token, move[0], 0)), Integer.parseInt(formula(token, move[0], 1)), Boolean.parseBoolean(formula(token, move[0], 2)));
        else if(token.equals("6")) damage();
        else if(token.equals("7")) hit();
        else if(token.equals("8")) blink();
        else if(token.equals("9")) displayMessage();
        else if(token.equals("10")) backpedal();
    }
    
    private String formula(String seqNo, String moveNo, Integer paramNo){
        if(seqNo.equals("2") && moveNo.equals("1") && paramNo == null) return String.valueOf((int)Math.ceil(attackSprite.getWidth()/2.0));
        if(seqNo.equals("3") && moveNo.equals("1") && paramNo == 0) return String.valueOf(R.raw.swing);
        if(seqNo.equals("3") && moveNo.equals("1") && paramNo == 1) return String.valueOf(R.raw.punch);
        if(seqNo.equals("5") && moveNo.equals("1") && paramNo == 0) 
            return (attacker == 1)? String.valueOf((int)Round(Animated.OPPONENT_FRAME_BOTTOMLEFT_X + attackSprite.getWidth()/2.0, 0)) 
                : String.valueOf((int)Round(Animated.USER_FRAME_BOTTOMLEFT_X - attackSprite.getWidth()/2.0, 0));
        if(seqNo.equals("5") && moveNo.equals("1") && paramNo == 1) return String.valueOf((int)Animated.USER_FRAME_BOTTOMLEFT_Y); // := String.valueOf((int)Animated.OPPONENT_FRAME_BOTTOMLEFT_Y); if its a product of attack()
        if(seqNo.equals("5") && moveNo.equals("1") && paramNo == 2) return "true";
        return null;
    }
    
    private boolean threadRunning(){
        return (MOVE_THREAD_RUNNING == true || SHIFT_LorR_THREAD_RUNNING == true ||
            ELLIPSE_LorR_THREAD_RUNNING == true || BACKPEDAL_THREAD_RUNNING == true ||
            COLLISION_THREAD_RUNNING == true);
    }
}