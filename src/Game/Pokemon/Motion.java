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
 width:73 height:70
 */
public class Motion {
    private Bitmap attackSprite;
    private Handler globalHandler = new Handler();
    private InputStream stream;
    private Runnable thread;
    private String[] move, info;
    private boolean MOVE_THREAD_RUNNING = false, BACKPEDAL_THREAD_RUNNING = false,
            SHIFT_LorR_THREAD_RUNNING = false, ELLIPSE_LorR_THREAD_RUNNING = false,
            BLINK_THREAD_RUNNING = false, TERMINATED_SEQUENCE = false, DAMAGE_THREAD_RUNNING = false,
            DELAY_THREAD_RUNNING = false, COLLISION_THREAD_RUNNING = false,
            DECODER_THREAD_RUNNING = false, isFROZEN_THREAD_RUNNING = false, Crit_Damage = false;
    private final byte attacker;
    private double BOTTOMLEFT_X, BOTTOMLEFT_Y, BOTTOMRIGHT_X, BOTTOMRIGHT_Y, 
            TOPLEFT_X, TOPLEFT_Y, TOPRIGHT_X, TOPRIGHT_Y, user_bottomside_midpoint_X,
            opponent_bottomside_midpoint_X;  
    private float damageAmount = 0f, Damage_received_effect = 0f;
    private final AssetManager asset;
    private static Context context;
    private Motion opponentInst;
    private final int INC_RATE = 2, ARC_RATE = 30, TIME = 3000;
    private static int pauser = 0; //Freeze flag that represents which contender yeilds the right to attack
    private int i = 0, INDEX = 1 /*sequence starts at 1, SEQUENCE[0] = move#*/, 
            soundByteCnt = 0, incX = 0, incY = 0;
    protected static boolean SHIFT_BLtoTR_LOCK = false;
    protected boolean frozen = false;
    
    
    public Motion(Context tContext, byte contender){
        context = tContext;
        asset = context.getAssets(); //Link assets  
        attacker = contender;
    }
    
    protected void setInst(Motion tMyOpponentInst){
        opponentInst = tMyOpponentInst;
    }
    
    protected void initialize(String[] tokenMove){
        /* Collect data of contender's current stance location on screen */
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
                Handler sequenceHandler = globalHandler;
                
                public void run(){
                    if(!threadRunning() && !isFROZEN_THREAD_RUNNING){ //Then no main threads are running & contender !isFrozen
                        try {
                            sequence(info[INDEX++]); //Call next part of sequence                   
                        } catch(NotFoundException e) {
                            Battle.text.setText(e.toString());
                        } catch(ArrayIndexOutOfBoundsException e) { //Reached end of sequence
                            INDEX = 1; //Reset INDEXER
                            refresh(); //Vars before leaving
                            return;
                        } 
                    }
                    
                    sequenceHandler.postDelayed(this, 0);
                }
            };

            isFrozen();
            globalHandler.postDelayed(thread, 0);
            
//            // Setup handler for uncaught exceptions.
//            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
//                @Override
//                public void uncaughtException(Thread thread, Throwable e){
//                    handleUncaughtException(thread, e);
//                }
//            });
        } catch(Exception e) {
            Battle.text.setText(e.toString());
        }
    }
    
//    public void handleUncaughtException(Thread thread, Throwable e){
//        Battle.text.setText(e.toString());
//    }
    
    private void isFrozen(){
        globalHandler = new Handler();
        
        /* A separate non-global Runnable var that continues to execute, 
         * regardless of any other instance of another Runnable */
        Runnable frozenThread = new Runnable(){
            Handler frozenHandler = globalHandler;
            
            public void run(){                
                if(pauser != attacker && pauser != 0) frozenHandler.postDelayed(this, 0);
                else isFROZEN_THREAD_RUNNING = false;                
            }
        };
        
        globalHandler.postDelayed(frozenThread, 0);
        isFROZEN_THREAD_RUNNING = true;
    }
    
    private void move(){
        globalHandler = new Handler();
        
        thread = new Runnable(){
            boolean x_exit = false, y_exit = false, targeted = true;
            
            public void run(){
                if(!isFROZEN_THREAD_RUNNING){
                    if(attacker == 1){
                        if(targeted /*@ least once*/ || move[6].equals("TRACKING")){
                            targeted = false; //Everytime after initial pass-thru
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
                                collision();
                                return;
                            }
                        } else*/ if(y_exit || Animated.OPPONENT_FRAME_BOTTOMRIGHT_X < 0){
                            MOVE_THREAD_RUNNING = false;
                            collision();
                            return;
                        }
                    } else {
                        if(targeted /*@ least once*/ || move[6].equals("TRACKING")){
                            targeted = false; //Everytime after initial pass-thru
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
                                collision();
                                return;
                            }
                        } else if(y_exit || Animated.USER_FRAME_BOTTOMRIGHT_X > Battle.SCREEN_WIDTH){
                            MOVE_THREAD_RUNNING = false;
                            collision();
                            return;
                        }
                    }
                    isFrozen();
                }
                globalHandler.postDelayed(this, 0);
            }
        };
        
        isFrozen();
        globalHandler.postDelayed(thread, 0);
        MOVE_THREAD_RUNNING = true;        
    }
    
    private void collision(){
        globalHandler = new Handler();
        
        thread = new Runnable(){
            Double cond1, cond2;
            
            @Override
            public void run(){
                if(COLLISION_THREAD_RUNNING && !DELAY_THREAD_RUNNING && !isFROZEN_THREAD_RUNNING){
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

                    if(!TERMINATED_SEQUENCE){ //Contender wins collision
                        if(!frozen){ //Cannot execute instructions below if contender is frozen
                            pauser = attacker;
                            opponentInst.frozen = true;
                        }
                    } else if(!frozen && !Crit_Damage && Damage_received_effect <= 1 && Damage_received_effect > 0){ //Damage received is not super effective, continue command
                        INDEX--; //Decrement sequence INDEX to execute last motion again
                        isFrozen(); //Start thread to see if contender is frozen
                    } else { //Contender missed attack
                        if(!frozen){ //Cannot execute instructions below if contender is frozen
                            int x, y;
                            String message = "Cancelled"; //Default, meaning if displayed, contender received a super effective hit while in motion

                            if(attacker == 2 || Crit_Damage || Damage_received_effect > 1){
                                x = (int)Round((Animated.OPPONENT_FRAME_TOPLEFT_X + Animated.OPPONENT_FRAME_TOPRIGHT_X)/2 - Battle.SCREEN_WIDTH/2f, 0);
                                y = (int)Round((Animated.OPPONENT_FRAME_TOPLEFT_Y + Animated.OPPONENT_FRAME_BOTTOMLEFT_Y)/2 - Battle.SCREEN_HEIGHT/2f + Animated.opponent.getHeight()/2f, 0);
                                if(!Crit_Damage && Damage_received_effect <= 1) message = "Missed!";
                            } else {
                                x = (int)Round((Animated.USER_FRAME_TOPLEFT_X + Animated.USER_FRAME_TOPRIGHT_X)/2 - Battle.SCREEN_WIDTH/2f, 0);
                                y = (int)Round((Animated.USER_FRAME_TOPLEFT_Y + Animated.USER_FRAME_BOTTOMLEFT_Y)/2 - Battle.SCREEN_HEIGHT/2f + Animated.user.getHeight()/2f, 0);
                                if(!Crit_Damage && Damage_received_effect <= 1) message = "Dodged!";
                            }             

                            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, x, y);
                            toast.setMargin(0, 0);
                            toast.show();
                        }
                    }

                    COLLISION_THREAD_RUNNING = false;
                    return;                    
                }
                globalHandler.postDelayed(this, 0);
            }
        };
        
        isFrozen();
        globalHandler.postDelayed(thread, 0);
        COLLISION_THREAD_RUNNING = true;
    }
    
    private void backpedal(){
        opponentInst.frozen = false; //Attack has finished @tp, unfreeze opponent
        pauser = 0; //Reset freeze flag
            
        globalHandler = new Handler();
        
        thread = new Runnable(){
            boolean x_exit = false, y_exit = false;
            
            public void run(){
                if(!isFROZEN_THREAD_RUNNING){ //Then contender !isFrozen
                    if(attacker == 1){                    
                        if(Round(Animated.OPPONENT_FRAME_BOTTOMLEFT_X + incX + Animated.opponent.getWidth()/2.0, 0) >= Round(BOTTOMLEFT_X + Animated.opponent.getWidth()/2.0, 0)){
                            incX -= (int)Round((Animated.OPPONENT_FRAME_BOTTOMLEFT_X + incX + Animated.opponent.getWidth()/2.0) - (BOTTOMLEFT_X + Animated.opponent.getWidth()/2.0), 0);
                            Animated.opponent_shift_x += incX;
                            x_exit = true;
                        }
                        /* opponent_shift_x is already negative to start off width/, 
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
                    isFrozen(); //Check to see if contender itself isFrozen
                }
                globalHandler.postDelayed(this, 0);
            }
        };
                
        tracking(true); //Always true in this context
        isFrozen(); //Check to see if contender itself isFrozen
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
                if(!isFROZEN_THREAD_RUNNING){
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
                    
                    /* If executed, its the case where its opponent has frozen contender's 
                     * movements while this thread was running. Start another isFrozen check */
                    if(frozen) isFrozen(); 
                }
                Battle.text.setText("Count: " + cnt + " | incX: " + incX + " | Sound count: " + soundByteCnt);
                globalHandler.postDelayed(this, 0);
            }
        };
        
        isFrozen(); //Start self-check before this thread executes
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
                if(!isFROZEN_THREAD_RUNNING){
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
                    /* If executed, its the case where its opponent has frozen contender's 
                     * movements while this thread was running. Start another isFrozen check.
                     * NOTE2SELF: Should not be possible, considering that if this particular
                     * motion is executed, opponeent should already be frozen */
                    if(frozen) isFrozen();
                }
                globalHandler.postDelayed(this, 0);
            }
        };
        isFrozen(); //Start self-check before this thread executes
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
            Options Opponent_Space_Options = new Options();
            Bitmap unscaledBitmap = BitmapFactory.decodeStream(stream, null, Opponent_Space_Options);
            attackSprite = Bitmap.createScaledBitmap(unscaledBitmap, 
                    (int)Round(Opponent_Space_Options.outWidth*(Animated.scaleFactor + /*1),0),//*/((attacker == 1)? Animated.u_depth : Animated.o_depth)), 0), 
                    (int)Round(Opponent_Space_Options.outHeight*(Animated.scaleFactor + /*1),0),//*/((attacker == 1)? Animated.u_depth : Animated.o_depth)), 0), 
                    true);
        } catch(Exception e) {
            Battle.text.setText(e.toString());
        }           
    }
    
    private void hit(){
        if(TERMINATED_SEQUENCE) return;
        
        clearAttackFrontside(); //Of opponent
        
        if(damageAmount > 0){
            i = 0; //Reset sprite frame variable
            setAttackSprite("hit", false);
            setAttackFrontside();

            Handler hitHandler = new Handler();

            /* Runnable waits 1/3s before executing simple statement */
            Runnable hitThread = new Runnable(){
                public void run(){
                    clearAttackFrontside();
                }
            };

            hitHandler.postDelayed(hitThread, 333);
        }
    }
    
    private void blink(){
        if(TERMINATED_SEQUENCE) return;
        
        if(damageAmount > 0){
            i = 0; //Reset i
            setAttackSprite("null", false);
            decodeNullFieldBitmapFromSprite();
            
            globalHandler = new Handler();

            /* Runnable competes with onDraw() in Animated, to constantly overrideSprite */
            Runnable blinkThread = new Runnable(){
                Handler blinkHandler = globalHandler;
                public void run(){
                    overrideSprite();

                    if(DELAY_THREAD_RUNNING) blinkHandler.postDelayed(this, 333);                        
                    else BLINK_THREAD_RUNNING = false;
                }
            };

            delay();
            BLINK_THREAD_RUNNING = true;
            globalHandler.postDelayed(blinkThread, 0);
        }
    }
    
    private void damage(){
        if(TERMINATED_SEQUENCE) return;
        
        globalHandler = new Handler();
        damageAmount = Damage.Calculator(move, attacker);
        opponentInst.Damage_received_effect = Damage.effect;
        opponentInst.Crit_Damage = Damage.IsaCrit;
        
        Runnable damageThread = new Runnable(){
            Handler ref = globalHandler;
            float amount = damageAmount, //Local var to deduct damage
            limit = (attacker == 1)? Animated.user_current_HP - amount
                    : ((Animated.opponent_current_HP - amount)/Animated.opponent_pokemon_HP)*100/*percentage*/;
            
            public void run(){
//                Battle.text.setText("(" + limit + " < " + (((Animated.opponent_pokemon_HP*(1 - Animated.opponent_damage_percentage))/Animated.opponent_pokemon_HP)*100) + 
//                        ") | Damage: " + damageAmount + " | OpponentHP: " + Animated.opponent_pokemon_HP +
//                        " | Opponent_type: " + Battle.o_pokemon.TYPE1 + "/" + Battle.o_pokemon.TYPE2 + 
//                        " | User_type: " + Battle.u_pokemon.TYPE1 + "/" + Battle.u_pokemon.TYPE2 + 
//                        " | STAB: " + Damage.s + " | Effectiveness: " + Damage.effect + 
//                        " | Critical: " + Damage.c + " | Random: " + Damage.r);
                
                if(amount > 0){
                    if(attacker == 1){
                        Animated.user_damage_percentage += 0.01f;
                        if(limit < Animated.user_pokemon_HP*(1 - Animated.user_damage_percentage)) ref.postDelayed(this, 0);
                        else DAMAGE_THREAD_RUNNING = false;                        
                    } else if(attacker == 2) {
                        Animated.opponent_damage_percentage += 0.01f;
                        if(limit < (((Animated.opponent_pokemon_HP*(1 - Animated.opponent_damage_percentage))/Animated.opponent_pokemon_HP)*100)) ref.postDelayed(this, 0);
                        else DAMAGE_THREAD_RUNNING = false;                        
                    }
                } else DAMAGE_THREAD_RUNNING = false;                                    
            }
        };
        
        globalHandler.postDelayed(damageThread, 0);
        DAMAGE_THREAD_RUNNING = true;
    }
    
    private void delay(){
        Thread delayThread = new Thread(){
            @Override
            public void run(){
                try {
                    Thread.sleep(TIME);
                    DELAY_THREAD_RUNNING = false;
                } catch (InterruptedException e) {
                } 
            }
        };
        
        delayThread.start();        
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
        //Normal damage, neither super (in)effective, display nothing
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
    
    private void decodeNullFieldBitmapFromSprite(){
        globalHandler = new Handler();

        Runnable decoderThread = new Runnable(){
            byte opponentDepth = (attacker == 1)? Animated.u_depth : Animated.o_depth;
            int width = (attacker == 1)? Animated.user.getWidth() : Animated.opponent.getWidth(),
                height = (attacker == 1)? Animated.user.getHeight() : Animated.opponent.getHeight();
            Bitmap bmp = Bitmap.createBitmap(attackSprite, 0/*starting-cnt TL*/, 0/*starting-y TL*/, width/*ending-cnt BR*/, height/*ending-y BR*/);
            Handler decoder = globalHandler;
            
            public void run(){
                if((attacker == 2 && Animated.o_depth != opponentDepth) || (attacker == 1 && Animated.u_depth != opponentDepth)){
                    setAttackSprite("null", false);
                    opponentDepth = (attacker == 1)? Animated.u_depth : Animated.o_depth;
                    
                    if(attacker == 1){
                        if(opponentDepth == 0){
                            width = Animated.user_farWidth;
                            height = Animated.user_farHeight;
                        } else {
                            width = Animated.user_closeWidth;
                            height = Animated.user_closeHeight;
                        }
                    } else {
                        if(opponentDepth == 0){
                            width = Animated.opponent_farWidth;
                            height = Animated.opponent_farHeight;
                        } else {
                            width = Animated.opponent_closeWidth;
                            height = Animated.opponent_closeHeight;
                        }
                    }
                    
                    bmp = Bitmap.createScaledBitmap(attackSprite, width, height, false);
                }
                
                attackSprite = bmp;

                if(BLINK_THREAD_RUNNING) decoder.postDelayed(this, 333);                        
                else DECODER_THREAD_RUNNING = false;
            }
        };
        
        DECODER_THREAD_RUNNING = true;
        globalHandler.postDelayed(decoderThread, 0);
    }
    
    private void refresh(){
        globalHandler = new Handler();
        
        thread = new Runnable(){
            Handler refreshHandler = globalHandler;
            
            public void run(){
                if(!threadRunning()){ //Should always be TRUE @tp, but added for additional security
                    TERMINATED_SEQUENCE = false;
                    damageAmount = 0f;
                    i = 0;
                    soundByteCnt = 0;
                    Damage_received_effect = 0;
                    Crit_Damage = false;
                    opponentInst.frozen = false;
                    pauser = 0;
                    
                    if(attacker == 1){
                        /* Reset exterior class variables */
                        Battle.o_actChosen = false;
                        Animated.opponent_speed_inc = 0;
                        Animated.opponent_speedbar = 0;
                        Animated.opponent_speedbar_percentage = 0;
                        Animated.opponent_actReady = false;
                        Animated.OPPONENT_SPEED_LOCK = false;                            
                    } else {                        
                        /* Reset exterior class variables */
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
        if(!frozen){
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
        } else {
            INDEX--; //Decrement INDEX to try again
            isFrozen(); //start isFozen check
        } 
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
            ELLIPSE_LorR_THREAD_RUNNING == true || //BACKPEDAL_THREAD_RUNNING == true ||
            COLLISION_THREAD_RUNNING == true);
    }
}