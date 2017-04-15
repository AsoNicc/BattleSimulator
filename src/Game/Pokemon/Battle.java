package Game.Pokemon;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import static java.lang.Thread.sleep;
import java.util.Random;

public class Battle extends Activity implements OnClickListener, OnTouchListener {
    protected static boolean u_actChosen = false, o_actChosen = false, u_unfocusedEnergy = true, o_unfocusedEnergy = true, buffPressed = false;
    protected static byte OPPONENT_ATK_STAGE = 1, OPPONENT_DEF_STAGE = 1, OPPONENT_SpA_STAGE = 1, 
            OPPONENT_SpD_STAGE = 1, OPPONENT_SPE_STAGE = 1, OPPONENT_ACC_STAGE = 1, 
            OPPONENT_EVADE_STAGE = 1, USER_ATK_STAGE = 1, USER_DEF_STAGE = 1, 
            USER_SpA_STAGE = 1, USER_SpD_STAGE = 1, USER_SPE_STAGE = 1, 
            USER_ACC_STAGE = 1, USER_EVADE_STAGE = 1;
    protected static int ARENABOX, SCREEN_HEIGHT, SCREEN_WIDTH;
    private Animated surface;
    protected static Pokemon o_pokemon, u_pokemon;
    private boolean activeMove = true, error = false,  held = false, initialState = true, 
            killHold = false, newTouch = true, touchClear = true, touched_user, touched_opponent;
    private Button pokeball, buff, cheer, close, moves, forfeit, move1, move2, move3, move4, pkmn1, pkmn2, pkmn3, pkmn4, pkmn5, pkmn6, swap;
    private Configuration config;
    private final Drawable layers[] = new Drawable[2];
    protected static float scaledDensity;
    private Float delta_x, delta_y, event_x, event_y, x, y;
    private FrameLayout layout;
    private Handler globalHandler;
    private ImageView viewer;
    private int frame, index;
    private Integer lastPos = -1;
    private LayerDrawable layeredDrawable;
    private static int BACKGROUND_ID, CHEER_ID, SWAP_AND_FORFEIT_ID, BUFF_ID, FORFEIT_ID, MOVES_AND_BUFF_ID, MOVES_ID, SWAP_ID;
    private final int ANGLE = 45, BOUND = 75, BUTTONCLICK = R.raw.choose, MARGIN = 25, MOVES_ROW1_ID = 2131165202, 
            MOVES_ROW2_ID = 2131165203, TEAM_ROW1_ID = 2131165204, TEAM_ROW2_ID = 2131165205, 
            MOVE1_ID = 2131165206, MOVE2_ID = 2131165207, MOVE3_ID = 2131165208, 
            MOVE4_ID = 2131165209, PKMN1_ID = 2131165210, PKMN2_ID = 2131165211, 
            PKMN3_ID = 2131165212, PKMN4_ID = 2131165213, PKMN5_ID = 2131165214, 
            PKMN6_ID = 2131165215, X_ID = 2131165216, TOUCH_LATENCY_TIME = 500, ILLEGAL_HOLD_TIMER = 500;
    private LinearLayout drawer;
    private final LinearLayout.LayoutParams FULL_MATCH_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    private static MediaPlayer sfx;
    protected Motion act, ai_act;
    private static final Pokemon[] team = new Pokemon[6];
    private Pokemon temp;
    private final Random gen = new Random();
    private static final String STATE_U_POKEMON = "user-pokemon", STATE_O_POKEMON = "opponent-pokemon", STATE_FRAME = "frame", STATE_ACTIONBAR_POS = "index",
            STATE_IMAGE_ID = "iv", STATE_MOTION_RATIO_X = "x-ratio", STATE_MOTION_RATIO_Y = "y-ratio", 
            STATE_INITIAL_SHIFT = "initial-xy", STATE_SHIFT_BLtoTR_LOCK = "angled-shift-bottom-up",
            STATE_O_MAXSPEED = "o-speed", STATE_O_SPEEDSUM = "o-speed+", STATE_O_SPEEDPERCENT = "o-speed%",
            STATE_U_MAXSPEED = "u-speed", STATE_U_SPEEDSUM = "u-speed+", STATE_U_SPEEDPERCENT = "u-speed%",
            STATE_O_ACTIONREADY = "o-cmdEnd", STATE_U_ACTIONREADY = "u-cmdEnd";
    public static TextView text;
    protected static String drawerState = "drawer-layout";
    private WrappingSlidingDrawer handle;
    private static View vMoves, vSwap, vCheer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        try {
            super.onCreate(savedInstanceState);
            View root = LayoutInflater.from(this).inflate(R.layout.battlefield, null); //Holds reference data of entire layout
            setContentView(root); //Sets view

            vMoves = root.findViewById(R.id.bMoves);
            vSwap = root.findViewById(R.id.bSwap);
            vCheer = root.findViewById(R.id.bCheer); //temporary delete @ later time
            text = (TextView)findViewById(R.id.tvException); //Prep for debugging use
            
            /* Used in determining postions of sprites, and sprtie frames */
            scaledDensity = getResources().getDisplayMetrics().scaledDensity;
            WindowManager window = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            Display screen = window.getDefaultDisplay();
            Point size = new Point();
            screen.getSize(size);
            SCREEN_HEIGHT = size.y;
            SCREEN_WIDTH = size.x;
            ARENABOX = Math.min(size.x, size.y);
            
            /* Used to clear EVERY SharedPreferences */
//            if(true){
//                Settings load = new Settings(this);
//                load.setAbilities();
//                load.clear();
//                load.setAbilities();
//            }
            
            surface = new Animated(this);
            ai_act = new Motion(this, (byte)1);
            act = new Motion(this, (byte)2);
            ai_act.setInst(act);
            act.setInst(ai_act);
            
            /* Used to setup SharedPreferences, if it has not been set */
            SharedPreferences stat = getSharedPreferences("genOneBaseStatList", MODE_PRIVATE);
            if(!stat.getBoolean("setState", false)){
                Settings load = new Settings(this);
                load.setOrient();
                stat = getSharedPreferences("genOneBaseStatList", MODE_PRIVATE);
                ////text.setText("Stats have not been analyzed");    
            } //else //text.setText("Stats analyzed");

            Animated.AVG_SPEED = stat.getFloat("avg_speed", 0); //Defaults to 0 if not found
      
            // Check whether we're recreating a previously destroyed instance
            if(savedInstanceState != null){
                initialState = false;

                // Restore value of members from saved state
                surface.setPokemon(2, savedInstanceState.getString(STATE_U_POKEMON));
                frame = savedInstanceState.getInt(STATE_FRAME);
                
                /* Opponent speed states */
                Animated.opponent_speed_inc = Double.parseDouble(savedInstanceState.getString(STATE_O_SPEEDSUM));
                Animated.opponent_actReady = savedInstanceState.getBoolean(STATE_O_ACTIONREADY);
                Animated.OPPONENT_SPEED_LOCK = savedInstanceState.getBoolean(STATE_O_MAXSPEED);
                
                if(Animated.OPPONENT_SPEED_LOCK) Animated.opponent_speedbar = Animated.speedbar_end_x;                    
                
                if(Animated.opponent_actReady){
                    Animated.opponent_speedbar_percentage = (Animated.commandEnd_startAct_x - Animated.speedbar_start_x)/
                            ((SCREEN_HEIGHT > SCREEN_WIDTH)? 
                            (SCREEN_WIDTH*11/20.0 + surface.LARGEST_WIDTH*2.0)
                            : (SCREEN_WIDTH*11/20.0 + surface.LARGEST_WIDTH*3.5));
                } else Animated.opponent_speedbar_percentage = Double.parseDouble(savedInstanceState.getString(STATE_O_SPEEDPERCENT));
                
                /* User speed states */
                Animated.user_speed_inc = Double.parseDouble(savedInstanceState.getString(STATE_U_SPEEDSUM));
                Animated.user_actReady = savedInstanceState.getBoolean(STATE_U_ACTIONREADY);
                Animated.USER_SPEED_LOCK = savedInstanceState.getBoolean(STATE_U_MAXSPEED);
                
                if(Animated.USER_SPEED_LOCK) Animated.user_speedbar = Animated.speedbar_end_x;                    
                
                if(Animated.user_actReady){
                    Animated.user_speedbar_percentage = (Animated.commandEnd_startAct_x - Animated.speedbar_start_x)/
                            ((SCREEN_HEIGHT > SCREEN_WIDTH)? 
                            (SCREEN_WIDTH*11/20.0 + surface.LARGEST_WIDTH*2.0)
                            : (SCREEN_WIDTH*11/20.0 + surface.LARGEST_WIDTH*3.5));
                } else Animated.user_speedbar_percentage = Double.parseDouble(savedInstanceState.getString(STATE_U_SPEEDPERCENT));
            } else {
                // Probably initialize members with default values for a new instance
                team[0] = new Pokemon(this, (byte)5, Short.valueOf("35")); //I temporarily specified a lead
                team[1] = new Pokemon(this, (byte)5, Short.valueOf("6"));
                team[2] = new Pokemon(this, (byte)5, Short.valueOf("18"));
                team[3] = new Pokemon(this, (byte)5, Short.valueOf("146"));
                team[4] = new Pokemon(this);
                team[5] = new Pokemon(this);
                u_pokemon = team[0];
                surface.setPokemon(2, u_pokemon.name.toLowerCase());
                Animated.user_pokemon_HP = u_pokemon.HP;
                Animated.user_pokemon_lvl = u_pokemon.level;        
            }        

            initialize();
        } catch(Exception e) {
            //text.setText(e.toString());
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        // Save the user's current game state, FIRST...
        savedInstanceState.putString(STATE_O_POKEMON, Animated.opponent_pokemon);
        savedInstanceState.putString(STATE_U_POKEMON, Animated.user_pokemon);
        savedInstanceState.putInt(STATE_FRAME, frame);
        savedInstanceState.putInt(STATE_ACTIONBAR_POS, lastPos);
        savedInstanceState.putInt(STATE_IMAGE_ID, BACKGROUND_ID);
        
        savedInstanceState.putBoolean(STATE_O_MAXSPEED, Animated.OPPONENT_SPEED_LOCK);
        savedInstanceState.putBoolean(STATE_U_MAXSPEED, Animated.USER_SPEED_LOCK);
        
        savedInstanceState.putBoolean(STATE_O_ACTIONREADY, Animated.opponent_actReady);
        savedInstanceState.putBoolean(STATE_U_ACTIONREADY, Animated.user_actReady);
        /* Opponent speed states */
        savedInstanceState.putString(STATE_O_SPEEDSUM , String.valueOf(Animated.opponent_speed_inc));
        savedInstanceState.putString(STATE_O_SPEEDPERCENT , String.valueOf(Animated.opponent_speedbar_percentage));
        /* User speed states */
        savedInstanceState.putString(STATE_U_SPEEDSUM, String.valueOf(Animated.user_speed_inc));
        savedInstanceState.putString(STATE_U_SPEEDPERCENT, String.valueOf(Animated.user_speedbar_percentage));
        
        savedInstanceState.putBoolean(STATE_INITIAL_SHIFT, (Animated.user_shift_x == 0 && Animated.user_shift_y == 0));
        savedInstanceState.putBoolean(STATE_SHIFT_BLtoTR_LOCK, Motion.SHIFT_BLtoTR_LOCK);
        savedInstanceState.putString(STATE_MOTION_RATIO_X, String.valueOf((double)Animated.USER_FRAME_BOTTOMLEFT_X/SCREEN_WIDTH));
        savedInstanceState.putString(STATE_MOTION_RATIO_Y, String.valueOf((double)Animated.USER_FRAME_BOTTOMLEFT_Y/SCREEN_HEIGHT));
        
        Animated.user_shift_x = 0;
        Animated.user_shift_y = 0;
        // ...THEN, this Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        // FIRST, Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // THEN, Restore state members from saved instance
        lastPos = Math.max(savedInstanceState.getInt(STATE_ACTIONBAR_POS), 0);
        surface.setPokemon(1, savedInstanceState.getString(STATE_O_POKEMON));
        surface.setPokemon(2, savedInstanceState.getString(STATE_U_POKEMON));
        frame = savedInstanceState.getInt(STATE_FRAME);
        BACKGROUND_ID = savedInstanceState.getInt(STATE_IMAGE_ID);
        
        if(savedInstanceState.getBoolean(STATE_SHIFT_BLtoTR_LOCK)){
            double user_bottomside_midpoint_X = (((SCREEN_WIDTH/6.0 - Animated.user.getWidth()/2.0)*Animated.USER_PLACEMENT_X + Animated.user_shift_x + Animated.user.getWidth()) - ((SCREEN_WIDTH/6.0 - Animated.user.getWidth()/2.0)*Animated.USER_PLACEMENT_X + Animated.user_shift_x))/2 + ((SCREEN_WIDTH/6.0 - Animated.user.getWidth()/2.0)*Animated.USER_PLACEMENT_X + Animated.user_shift_x);
            double opponent_bottomside_midpoint_X = (((SCREEN_WIDTH*5/6.0 - Animated.opponent.getWidth()/2.0)*Animated.OPPONENT_PLACEMENT_X + Animated.opponent_shift_x + Animated.opponent.getWidth()) - ((SCREEN_WIDTH*5/6.0 - Animated.opponent.getWidth()/2.0)*Animated.OPPONENT_PLACEMENT_X + Animated.opponent_shift_x))/2 + ((SCREEN_WIDTH*5/6.0 - Animated.opponent.getWidth()/2.0)*Animated.OPPONENT_PLACEMENT_X + Animated.opponent_shift_x);

            Animated.user_shift_x = (int)Animated.Round(opponent_bottomside_midpoint_X - user_bottomside_midpoint_X, 0);
            Animated.user_shift_y = (int)Animated.Round(-(((SCREEN_HEIGHT*2/3.0 + Animated.user.getHeight()/2.0)*Animated.USER_PLACEMENT_Y + Animated.user_shift_y) - ((SCREEN_HEIGHT/3.0 + Animated.opponent.getHeight()/2.0)*Animated.OPPONENT_PLACEMENT_Y + Animated.opponent_shift_y)), 0);   
            Animated.user_shift_y += (Animated.OPPONENT_FRAME_BOTTOMLEFT_Y - Animated.USER_FRAME_BOTTOMLEFT_Y);
        }
        else if(!savedInstanceState.getBoolean(STATE_INITIAL_SHIFT)){
            Animated.user_shift_x = (int)Animated.Round(SCREEN_WIDTH*Double.parseDouble(savedInstanceState.getString(STATE_MOTION_RATIO_X)) - (SCREEN_WIDTH/6.0 - Animated.user.getWidth()/2.0)*Animated.USER_PLACEMENT_X, 0);
            Animated.user_shift_y = (int)Animated.Round(SCREEN_HEIGHT*Double.parseDouble(savedInstanceState.getString(STATE_MOTION_RATIO_Y)) - (SCREEN_HEIGHT*2/3.0 + Animated.user.getHeight()/2.0)*Animated.USER_PLACEMENT_Y, 0);
            //Animated.user_shift_y += (Animated.OPPONENT_FRAME_BOTTOMLEFT_Y - Animated.USER_FRAME_BOTTOMLEFT_Y);
        }
    }
    
    private void initialize(){
        /* Setup ActionBar, presentation of actionBar varies w/ target SDK */
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
        
        // Adapter
        SpinnerAdapter adapter = ArrayAdapter.createFromResource(this, R.array.pokemon,
            android.R.layout.simple_spinner_dropdown_item);
        
        // Callback
        // What I Learned: Listeners are the last methods to run
        OnNavigationListener callback = new OnNavigationListener(){
            String[] items = getResources().getStringArray(R.array.pokemon); // List items from res
            
            @Override
            public boolean onNavigationItemSelected(int position, long id){                
                // Do stuff when navigation item is selected
                if(!initialState) initialState = true;
                
                if(lastPos != position){
                    lastPos = position;
                    surface.setPokemon(1, items[position]);
                    o_pokemon = new Pokemon(Battle.this, (byte)5, (short)(position + 1)); //I temporarily specified a lead
                    Animated.opponent_pokemon_HP = o_pokemon.HP;
                    Animated.opponent_pokemon_lvl = o_pokemon.level;
                    Animated.opponent_speed_inc = 0;
                    Animated.opponent_speedbar = 0;
                    Animated.opponent_speedbar_percentage = 0;
                    Animated.opponent_actReady = false;

                    /* Delete later */
                    Animated.user_speed_inc = 0;
                    Animated.user_speedbar = 0;
                    Animated.user_speedbar_percentage = 0;
                    Animated.user_actReady = false;
                }
                //Log.d("NavigationItemSelected", items[position]); // Debug
                return true;
            }
        };
        
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setListNavigationCallbacks(adapter, callback);
        
        frame = 0;
        
        layout = (FrameLayout)findViewById(R.id.flScreen);

        // Link IDs w/ variables
        viewer = (ImageView)findViewById(R.id.ivBackground);
        cheer = (Button)findViewById(R.id.bCheer);
        pokeball = (Button)findViewById(R.id.bHandle);
        drawer = (LinearLayout)findViewById(R.id.llDrawerContents);
        handle = (WrappingSlidingDrawer)findViewById(R.id.WrappingSlidingDrawer);
        
        // Assign ids to Button variables
        moves = (Button)findViewById(R.id.bMoves);
        swap = (Button)findViewById(R.id.bSwap);
        buff = (Button)findViewById(R.id.bBuff);
        forfeit = (Button)findViewById(R.id.bForfeit);        
        
        // Hold Button & Layout id constants for reuse
        BACKGROUND_ID = R.id.ivBackground;
        CHEER_ID = R.id.bCheer;
        MOVES_AND_BUFF_ID = R.id.llMovesAndBuff;
        SWAP_AND_FORFEIT_ID = R.id.llSwapAndForfeit;
        MOVES_ID = R.id.bMoves;
        SWAP_ID = R.id.bSwap;
        BUFF_ID = R.id.bBuff;
        FORFEIT_ID = R.id.bForfeit;
        
        // Set up onClick listeners
        cheer.setOnClickListener(this);
        // Set up onTouch listeners
        handle.setOnTouchListener(new View.OnTouchListener(){
            boolean init = false;
            
            private void initialize(){
                config = getResources().getConfiguration();
                moves.setOnTouchListener(newListener(1));
                buff.setOnTouchListener(newListener(2));
                swap.setOnTouchListener(newListener(3));
                forfeit.setOnTouchListener(newListener(4));
                init = true;
            }
            
            public boolean onTouch(View v, MotionEvent event) {
                if(!init) initialize();
                handle = null;
                return false;
            }
        });                   
        
        try { // Set up onTouch Listener
            viewer.setOnTouchListener(this);
        } catch(Exception e) { // onCreate in landscape orientation
            rebuildBackground(); // Restore background ImageView
            viewer = (ImageView)findViewById(R.id.ivBackground); // Assign ID
            BACKGROUND_ID = R.id.ivBackground; // Store ID value
            viewer.setOnTouchListener(this); // Retry
        }
        
//        startDrawerAnimation();
    }

    private View.OnTouchListener newListener(final int num){
        return (new View.OnTouchListener(){
            private boolean init = false, moved = false;
            private float deltaX, deltaY, init_x, init_y;
            private int action, offset, THRESHOLD = 10;
                
            private void initialize(View v){
                config = getResources().getConfiguration();
                offset = (v.getId() == BUFF_ID && config.orientation == Configuration.ORIENTATION_LANDSCAPE)? 1 : 
                        (v.getId() == SWAP_ID && config.orientation == Configuration.ORIENTATION_LANDSCAPE)? -1: 0;
                init = true;
            }
            
            public boolean onTouch(View v, MotionEvent event){
                if(!init) initialize(v);
                
                action = event.getAction();
                
                if(action == MotionEvent.ACTION_DOWN){ // Initial touch
                    init_x = event.getX();
                    init_y = event.getY();
                    
                    drawer.removeAllViews();
                    config = getResources().getConfiguration();
    
                    if(drawerState.equals("drawer-layout")){
                        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) restoreDrawerLayoutLandscape(num + offset); 
                        else restoreDrawerLayoutPortrait(num);
                    } else if(drawerState.equals("moves-layout")){
                        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) buildMovesLayoutLandscape(num); 
                        else buildMovesLayoutPortrait(num);
                    } else if(drawerState.equals("team-layout")){
                        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) buildTeamLayoutLandscape(num); 
                        else buildTeamLayoutPortrait(num);
                    }
                } else if(action == MotionEvent.ACTION_MOVE){
                    deltaX = Math.abs(init_x - event.getX());
                    deltaY = Math.abs(init_y - event.getY());
                    if(//Math.sqrt(Math.pow(deltaX, 2)*Math.pow(deltaY, 2)) > THRESHOLD*5 ||
                            (deltaX >= Math.pow(THRESHOLD, 2)*2 || deltaY >= Math.pow(THRESHOLD, 2)*2)) moved = true;
                } else if(action == MotionEvent.ACTION_UP){
                    drawer.removeAllViews();
                    config = getResources().getConfiguration();
    
                    if(drawerState.equals("drawer-layout")){
                        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) restoreDrawerLayoutLandscape(0); 
                        else restoreDrawerLayoutPortrait(0);
                    } else if(drawerState.equals("moves-layout")){
                        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) buildMovesLayoutLandscape(0); 
                        else buildMovesLayoutPortrait(0);
                    } else if(drawerState.equals("team-layout")){
                        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) buildTeamLayoutLandscape(0); 
                        else buildTeamLayoutPortrait(0);
                    }

                    if(drawerState.equals("drawer-layout")){
                        if(num == 1) moves.setOnTouchListener(newListener(num));
                        else if(num + offset == 2) buff.setOnTouchListener(newListener(2));
                        else if(num + offset == 3) swap.setOnTouchListener(newListener(3));
                        else if(num == 4) forfeit.setOnTouchListener(newListener(num));
                    } else if(drawerState.equals("moves-layout")){
                        if(num == 1) move1.setOnTouchListener(newListener(num));
                        else if(num == 2) move2.setOnTouchListener(newListener(num));
                        else if(num == 3) move3.setOnTouchListener(newListener(num));
                        else if(num == 4) move4.setOnTouchListener(newListener(num));
                        else if(num == 7) close.setOnTouchListener(newListener(num));
                    } if(drawerState.equals("team-layout")){
                        if(num == 1) pkmn1.setOnTouchListener(newListener(num));
                        else if(num == 2) pkmn2.setOnTouchListener(newListener(num));
                        else if(num == 3) pkmn3.setOnTouchListener(newListener(num));
                        else if(num == 4) pkmn4.setOnTouchListener(newListener(num));
                        else if(num == 5) pkmn5.setOnTouchListener(newListener(num));
                        else if(num == 6) pkmn6.setOnTouchListener(newListener(num));
                        else if(num == 7) close.setOnTouchListener(newListener(num));
                    }
                    
                    if(!moved) onClick(v);
                } else {
                    drawer.removeAllViews();
                    config = getResources().getConfiguration();
    
                    if(drawerState.equals("drawer-layout")){
                        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) restoreDrawerLayoutLandscape(0); 
                        else restoreDrawerLayoutPortrait(0);
                    } else if(drawerState.equals("moves-layout")){
                        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) buildMovesLayoutLandscape(0); 
                        else buildMovesLayoutPortrait(0);
                    } else if(drawerState.equals("team-layout")){
                        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) buildTeamLayoutLandscape(0); 
                        else buildTeamLayoutPortrait(0);
                    }
                }

                return true;
            }
        });
    }
    
    private void startDrawerAnimation(){
        Handler handler = new Handler();
        globalHandler = handler;
        
        final Runnable thread = new Runnable(){
            Handler handler = globalHandler;
            
            public void run(){
                frame += 30;
                
                if(frame == 30) pokeball.setBackgroundResource(R.drawable.frame_30);
                else if(frame == 60) pokeball.setBackgroundResource(R.drawable.frame_60);
                else if(frame == 90) pokeball.setBackgroundResource(R.drawable.frame_90);
                else if(frame == 120) pokeball.setBackgroundResource(R.drawable.frame_120);
                else if(frame == 150) pokeball.setBackgroundResource(R.drawable.frame_150);
                else if(frame == 180) pokeball.setBackgroundResource(R.drawable.frame_180);
                else if(frame == 210) pokeball.setBackgroundResource(R.drawable.frame_210);
                else if(frame == 240) pokeball.setBackgroundResource(R.drawable.frame_240);
                else if(frame == 270) pokeball.setBackgroundResource(R.drawable.frame_270);
                else if(frame == 300) pokeball.setBackgroundResource(R.drawable.frame_300);
                else if(frame == 330) pokeball.setBackgroundResource(R.drawable.frame_330);
                else {
                    pokeball.setBackgroundResource(R.drawable.frame_0);
                    frame = 0;
                }
                
                handler.postDelayed(this, 0);
            }
        };

        handler.postDelayed(thread, 0);
        globalHandler = null;
    }

    public void onClick(View v){
        if(v.getId() == R.id.bMoves){
            playSoundEffect(BUTTONCLICK);
            drawer.removeAllViews();
            config = getResources().getConfiguration();
            
            if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) buildMovesLayoutLandscape(0);
            else buildMovesLayoutPortrait(0);
        } else if(v.getId() == R.id.bSwap){
            playSoundEffect(BUTTONCLICK);
            drawer.removeAllViews();
            config = getResources().getConfiguration();
            
            if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) buildTeamLayoutLandscape(0);
            else buildTeamLayoutPortrait(0);    
        } else if(v.getId() == R.id.bForfeit){
            playSoundEffect(BUTTONCLICK);
//            finish();
        } else if(v.getId() == X_ID || v.getId() == R.id.bBuff){
            playSoundEffect(BUTTONCLICK);
            if(v.getId() == R.id.bBuff){
                buffPressed = true;
                if(u_pokemon.buff[1] != null) Abilities.get(u_pokemon.buff[1], 2);
                //text.setText(u_pokemon.buff[0] + ", " + u_pokemon.buff[1] + ", " + u_pokemon.buff[2]);
            }
            drawer.removeAllViews();
            config = getResources().getConfiguration();
            
            if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) restoreDrawerLayoutLandscape(0);
            else restoreDrawerLayoutPortrait(0);
        } else if(v.getId() == MOVE1_ID){ 
            if(!u_actChosen && Animated.user_actReady){ 
                playSoundEffect(BUTTONCLICK);
                action(0); 
            }
        } else if(v.getId() == MOVE2_ID){ 
            if(!u_actChosen && Animated.user_actReady){ 
                playSoundEffect(BUTTONCLICK);
                action(1); 
            }
        } else if(v.getId() == MOVE3_ID){ 
            if(!u_actChosen && Animated.user_actReady){ 
                playSoundEffect(BUTTONCLICK);
                action(2); 
            }
        } else if(v.getId() == MOVE4_ID){ 
            if(!u_actChosen && Animated.user_actReady){ 
                playSoundEffect(BUTTONCLICK);
                action(3); 
            }
        } else if(v.getId() == PKMN1_ID){ 
            if(!u_actChosen){ 
                playSoundEffect(BUTTONCLICK);
                swap(0); 
            }
        } else if(v.getId() == PKMN2_ID){ 
            if(!u_actChosen){ 
                playSoundEffect(BUTTONCLICK);
                swap(1); 
            }
        } else if(v.getId() == PKMN3_ID){ 
            if(!u_actChosen){ 
                playSoundEffect(BUTTONCLICK);
                swap(2); 
            }
        } else if(v.getId() == PKMN4_ID){ 
            if(!u_actChosen){ 
                playSoundEffect(BUTTONCLICK);
                swap(3); 
            }
        } else if(v.getId() == PKMN5_ID){ 
            if(!u_actChosen){ 
                playSoundEffect(BUTTONCLICK);
                swap(4); 
            }
        } else if(v.getId() == PKMN6_ID){ 
            if(!u_actChosen){ 
                playSoundEffect(BUTTONCLICK);
                swap(5); 
            }
        } else if(v.getId() == CHEER_ID){
            // NOTHING YET
            if(/*!u_actChosen && */Animated.opponent_actReady){ 
                o_actChosen = true;
                index = 1;

                if(u_pokemon.moves[index][4].equals("1") || u_pokemon.moves[index][4].equals("3")){ 
                    Animated.opponent_speedbar = Animated.speedbar_end_x - Animated.speedbar_start_x + 10;
                    Animated.OPPONENT_SPEED_LOCK = true;
                }
                
                globalHandler = new Handler();

                final Runnable thread = new Runnable(){
                    Handler handler = globalHandler;

                    public void run(){
                        if(Animated.opponent_speedbar >= Animated.speedbar_end_x - Animated.speedbar_start_x){
                            playSoundEffect(getVoice());
                            ai_act.initialize(u_pokemon.moves[index]);
                            return;
                        }
                        handler.postDelayed(this, 0);
                    }
                };

                globalHandler.postDelayed(thread, 0);
            }
        }
    }

    private void restoreDrawerLayoutLandscape(int pressedButton){
        drawerState = "drawer-layout";
        drawer.setWeightSum(0f);
        
        LinearLayout movesRow = new LinearLayout(this);
        movesRow.setId(MOVES_ROW1_ID);
        FULL_MATCH_PARAMS.gravity = Gravity.CENTER;
        movesRow.setLayoutParams(FULL_MATCH_PARAMS);
        movesRow.setOrientation(LinearLayout.HORIZONTAL);
        movesRow.setWeightSum(4f);
        
        moves = new Button(this);
        moves.setId(MOVES_ID);
        if(pressedButton == 1){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            moves.setBackground(layeredDrawable);
        } else moves.setBackgroundResource(R.drawable.normal_bubble);
        LinearLayout.LayoutParams WRAP_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f);
        moves.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        moves.setPadding(0, 0, 0, 0);
        moves.setText(R.string.moves);
        moves.setTextColor(Color.argb(255, 0, 0, 0));
        moves.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(moves);
        
        swap = new Button(this);
        swap.setId(SWAP_ID);
        if(pressedButton == 2){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            swap.setBackground(layeredDrawable);
        } else swap.setBackgroundResource(R.drawable.normal_bubble);
        swap.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        swap.setPadding(0, 0, 0, 0);
        swap.setText(R.string.swap);
        swap.setTextColor(Color.argb(255, 0, 0, 0));
        swap.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(swap);
        
        buff = new Button(this);
        buff.setId(BUFF_ID);
        if(pressedButton == 3){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            buff.setBackground(layeredDrawable);
        } else buff.setBackgroundResource(R.drawable.normal_bubble);
        buff.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        buff.setPadding(0, 0, 0, 0);
        buff.setText(R.string.buff);
        buff.setTextColor((buffPressed)? Color.argb(255, 255, 140, 0) : Color.argb(255, 0, 0, 0));
        buff.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(buff);
        
        forfeit = new Button(this);
        forfeit.setId(FORFEIT_ID);
        if(pressedButton == 4){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            forfeit.setBackground(layeredDrawable);
        } else forfeit.setBackgroundResource(R.drawable.normal_bubble);
        forfeit.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        forfeit.setPadding(0, 0, 0, 0);
        forfeit.setText(R.string.forfeit);
        forfeit.setTextColor(Color.argb(255, 0, 0, 0));
        forfeit.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(forfeit);
        
        // Add layout
        drawer.addView(movesRow);
        
        // Set up onTouch listeners
        moves.setOnTouchListener(newListener(1));
        buff.setOnTouchListener(newListener(2));
        swap.setOnTouchListener(newListener(3));
        forfeit.setOnTouchListener(newListener(4));        
    }
    
    private void restoreDrawerLayoutPortrait(int pressedButton){
        drawerState = "drawer-layout";
        drawer.setWeightSum(2f);
        
        LinearLayout movesAndBuff = new LinearLayout(this);
        movesAndBuff.setId(MOVES_AND_BUFF_ID);
        LinearLayout.LayoutParams MATCH_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f);
        MATCH_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        movesAndBuff.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        movesAndBuff.setOrientation(LinearLayout.HORIZONTAL);
        movesAndBuff.setWeightSum(2f);
        
        moves = new Button(this);
        moves.setId(MOVES_ID);
        if(pressedButton == 1){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            moves.setBackground(layeredDrawable);
        } else moves.setBackgroundResource(R.drawable.normal_bubble);
        LinearLayout.LayoutParams WRAP_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f);
        moves.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        moves.setPadding(0, 0, 0, 0);
        moves.setText(R.string.moves);
        moves.setTextColor(Color.argb(255, 0, 0, 0));
        moves.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesAndBuff.addView(moves);
        
        buff = new Button(this);
        buff.setId(BUFF_ID);
        if(pressedButton == 2){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            buff.setBackground(layeredDrawable);
        } else buff.setBackgroundResource(R.drawable.normal_bubble);
        buff.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        buff.setPadding(0, 0, 0, 0);
        buff.setText(R.string.buff);
        buff.setTextColor((buffPressed)? Color.argb(255, 255, 140, 0) : Color.argb(255, 0, 0, 0));
        buff.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesAndBuff.addView(buff);
        
        LinearLayout swapAndForfeit = new LinearLayout(this);
        swapAndForfeit.setId(SWAP_AND_FORFEIT_ID);
        swapAndForfeit.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        swapAndForfeit.setOrientation(LinearLayout.HORIZONTAL);
        swapAndForfeit.setWeightSum(2f);
        
        swap = new Button(this);
        swap.setId(SWAP_ID);
        if(pressedButton == 3){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            swap.setBackground(layeredDrawable);
        } else swap.setBackgroundResource(R.drawable.normal_bubble);
        swap.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        swap.setPadding(0, 0, 0, 0);
        swap.setText(R.string.swap);
        swap.setTextColor(Color.argb(255, 0, 0, 0));
        swap.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        swapAndForfeit.addView(swap);
        
        forfeit = new Button(this);
        forfeit.setId(FORFEIT_ID);
        if(pressedButton == 4){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            forfeit.setBackground(layeredDrawable);
        } else forfeit.setBackgroundResource(R.drawable.normal_bubble);
        forfeit.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        forfeit.setPadding(0, 0, 0, 0);
        forfeit.setText(R.string.forfeit);
        forfeit.setTextColor(Color.argb(255, 0, 0, 0));
        forfeit.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        swapAndForfeit.addView(forfeit);
        
        // Add layouts
        drawer.addView(movesAndBuff);
        drawer.addView(swapAndForfeit);
        
        // Set up onTouch listeners
        moves.setOnTouchListener(newListener(1));
        buff.setOnTouchListener(newListener(2));
        swap.setOnTouchListener(newListener(3));
        forfeit.setOnTouchListener(newListener(4));
    }
    
    private void buildMovesLayoutLandscape(int pressedButton){
        drawerState = "moves-layout";
        drawer.setWeightSum(10f);
        
        LinearLayout goBack = new LinearLayout(this);
        LinearLayout.LayoutParams FULL_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 2f);
        FULL_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        goBack.setLayoutParams(FULL_WRAP_WEIGHT_PARAMS);
        goBack.setOrientation(LinearLayout.VERTICAL);
        goBack.setWeightSum(1f);
        
        close = new Button(this);
        close.setId(X_ID);
        if(pressedButton == 7){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            close.setBackground(layeredDrawable);
        } else close.setBackgroundResource(R.drawable.normal_bubble);
        LinearLayout.LayoutParams WRAP_HARD_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 60, 1f);
        close.setLayoutParams(WRAP_HARD_WEIGHT_PARAMS);
        close.setPadding(0, 0, 0, 0);
        close.setText("X");
        close.setTextColor(Color.argb(255, 0, 0, 0));
        close.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        goBack.addView(close);
        
        LinearLayout movesRow = new LinearLayout(this);
        movesRow.setId(MOVES_ROW1_ID);
        LinearLayout.LayoutParams MATCH_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 8f);
        MATCH_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        movesRow.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        movesRow.setOrientation(LinearLayout.HORIZONTAL);
        movesRow.setWeightSum(4f);
        LinearLayout.LayoutParams FULL_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
        
        String styledText;
        move1 = new Button(this);
        move1.setId(MOVE1_ID);
        move1.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        move1.setPadding(0, 0, 0, 0);
        
        if(u_pokemon.moves[0] != null && u_pokemon.moves[0]/**/[1] != null && u_pokemon.moves[0]/**/[2] != null && u_pokemon.moves[0]/**/[5] != null){
            if(pressedButton == 1/**/){
                layers[0] = getResources().getDrawable(buttonBackground(u_pokemon.moves[0/**/][2]));
                layers[1] = getResources().getDrawable(R.drawable.pressed);
                layeredDrawable = new LayerDrawable(layers);
                move1/**/.setBackground(layeredDrawable);
            } else move1.setBackgroundResource(buttonBackground(u_pokemon.moves[0][2]));
            styledText = "<font color='#000000'><b>"
            + u_pokemon.moves[0][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + u_pokemon.moves[0][2] + "\t\tPWR:" + ((u_pokemon.moves[0][5].equals("null"))? "---" : u_pokemon.moves[0][5]) + "</font></small>";
            move1.setText(Html.fromHtml(styledText));
        } else {
            move1.setBackgroundResource(R.drawable.normal_bubble);
            move1.setText("---");
        }
        
        move1.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(move1);
                
        move2 = new Button(this);
        move2.setId(MOVE2_ID);
        move2.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        move2.setPadding(0, 0, 0, 0);
            
        if(u_pokemon.moves[1] != null && u_pokemon.moves[1]/**/[1] != null && u_pokemon.moves[1]/**/[2] != null && u_pokemon.moves[1]/**/[5] != null){
            if(pressedButton == 2/**/){
                layers[0] = getResources().getDrawable(buttonBackground(u_pokemon.moves[1/**/][2]));
                layers[1] = getResources().getDrawable(R.drawable.pressed);
                layeredDrawable = new LayerDrawable(layers);
                move2/**/.setBackground(layeredDrawable);
            } else move2.setBackgroundResource(buttonBackground(u_pokemon.moves[1][2]));
            styledText = "<font color='#000000'><b>"
            + u_pokemon.moves[1][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + u_pokemon.moves[1][2] + "\t\tPWR:" + ((u_pokemon.moves[1][5].equals("null"))? "---" : u_pokemon.moves[1][5]) + "</font></small>";
            move2.setText(Html.fromHtml(styledText));
        } else {
            move2.setBackgroundResource(R.drawable.normal_bubble);
            move2.setText("---");
        }
        
        move2.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(move2);
                
        move3 = new Button(this);
        move3.setId(MOVE3_ID);
        move3.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        move3.setPadding(0, 0, 0, 0);
        
        if(u_pokemon.moves[2] != null&& u_pokemon.moves[2]/**/[1] != null && u_pokemon.moves[2]/**/[2] != null && u_pokemon.moves[2]/**/[5] != null){
            if(pressedButton == 3/**/){
                layers[0] = getResources().getDrawable(buttonBackground(u_pokemon.moves[2/**/][2]));
                layers[1] = getResources().getDrawable(R.drawable.pressed);
                layeredDrawable = new LayerDrawable(layers);
                move3/**/.setBackground(layeredDrawable);
            } else move3.setBackgroundResource(buttonBackground(u_pokemon.moves[2][2]));
            styledText = "<font color='#000000'><b>"
            + u_pokemon.moves[2][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + u_pokemon.moves[2][2] + "\t\tPWR:" + ((u_pokemon.moves[2][5].equals("null"))? "---" : u_pokemon.moves[2][5]) + "</font></small>";
            move3.setText(Html.fromHtml(styledText));
        } else {
            move3.setBackgroundResource(R.drawable.normal_bubble);
            move3.setText("---");
        }
        
        move3.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(move3);
                    
        move4 = new Button(this);
        move4.setId(MOVE4_ID);
        move4.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        move4.setPadding(0, 0, 0, 0);
        
        if(u_pokemon.moves[3] != null&& u_pokemon.moves[3]/**/[1] != null && u_pokemon.moves[3]/**/[2] != null && u_pokemon.moves[3]/**/[5] != null){
            if(pressedButton == 4/**/){
                layers[0] = getResources().getDrawable(buttonBackground(u_pokemon.moves[3/**/][2]));
                layers[1] = getResources().getDrawable(R.drawable.pressed);
                layeredDrawable = new LayerDrawable(layers);
                move4/**/.setBackground(layeredDrawable);
            } else move4.setBackgroundResource(buttonBackground(u_pokemon.moves[3][2]));
            styledText = "<font color='#000000'><b>"
            + u_pokemon.moves[3][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + u_pokemon.moves[3][2] + "\t\tPWR:" + ((u_pokemon.moves[3][5].equals("null"))? "---" : u_pokemon.moves[3][5]) + "</font></small>";
            move4.setText(Html.fromHtml(styledText));
        } else {
            move4.setBackgroundResource(R.drawable.normal_bubble);
            move4.setText("---");
        }
        
        move4.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(move4);
        // Add layouts
        drawer.addView(goBack);
        drawer.addView(movesRow);
        
        // Set up onTouch listeners
        close.setOnTouchListener(newListener(7));        
        if(u_pokemon.moves[0][1] != null) move1.setOnTouchListener(newListener(1));        
        if(u_pokemon.moves[1][1] != null) move2.setOnTouchListener(newListener(2));        
        if(u_pokemon.moves[2][1] != null) move3.setOnTouchListener(newListener(3));        
        if(u_pokemon.moves[3][1] != null) move4.setOnTouchListener(newListener(4));        
    }

    private void buildMovesLayoutPortrait(int pressedButton){
        drawerState = "moves-layout";
        drawer.setWeightSum(10f);
        
        LinearLayout goBack = new LinearLayout(this);
        LinearLayout.LayoutParams FULL_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
        FULL_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        goBack.setLayoutParams(FULL_WRAP_WEIGHT_PARAMS);
        goBack.setOrientation(LinearLayout.VERTICAL);
        goBack.setWeightSum(1f);
        
        close = new Button(this);
        close.setId(X_ID);
        if(pressedButton == 7/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            close/**/.setBackground(layeredDrawable);
        } else close.setBackgroundResource(R.drawable.normal_bubble);
        LinearLayout.LayoutParams WRAP_HARD_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 50, 1f);
        close.setLayoutParams(WRAP_HARD_WEIGHT_PARAMS);
        close.setPadding(0, 0, 0, 0);
        close.setText("X");
        close.setTextColor(Color.argb(255, 0, 0, 0));
        close.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        goBack.addView(close);
        
        LinearLayout movesRow1 = new LinearLayout(this);
        movesRow1.setId(MOVES_ROW1_ID);
        LinearLayout.LayoutParams MATCH_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 4.5f);
        MATCH_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        movesRow1.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        movesRow1.setOrientation(LinearLayout.HORIZONTAL);
        movesRow1.setWeightSum(2f);
        LinearLayout.LayoutParams FULL_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
        
        String styledText;
        move1 = new Button(this);
        move1.setId(MOVE1_ID);
        move1.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        move1.setPadding(0, 0, 0, 0);
        
        if(u_pokemon.moves[0] != null && u_pokemon.moves[0]/**/[1] != null && u_pokemon.moves[0]/**/[2] != null && u_pokemon.moves[0]/**/[5] != null){
            if(pressedButton == 1/**/){
                layers[0] = getResources().getDrawable(buttonBackground(u_pokemon.moves[0/**/][2]));
                layers[1] = getResources().getDrawable(R.drawable.pressed);
                layeredDrawable = new LayerDrawable(layers);
                move1/**/.setBackground(layeredDrawable);
            } else move1.setBackgroundResource(buttonBackground(u_pokemon.moves[0][2]));
            styledText = "<font color='#000000'><b>"
            + u_pokemon.moves[0][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + u_pokemon.moves[0][2] + "\t\tPWR:" + ((u_pokemon.moves[0]/**/[5] == null)? "Error @move1/**/" : ((u_pokemon.moves[0][5].equals("null"))? "---" : u_pokemon.moves[0][5]) + "</font></small>");
            move1.setText(Html.fromHtml(styledText));
        } else {
            move1.setBackgroundResource(R.drawable.normal_bubble);
            move1.setText("---");
        }
        
        move1.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        movesRow1.addView(move1);
        
        move2 = new Button(this);
        move2.setId(MOVE2_ID);
        move2.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        move2.setPadding(0, 0, 0, 0);
        
        if(u_pokemon.moves[1] != null && u_pokemon.moves[1]/**/[1] != null && u_pokemon.moves[1]/**/[2] != null && u_pokemon.moves[1]/**/[5] != null){
            if(pressedButton == 2/**/){
                layers[0] = getResources().getDrawable(buttonBackground(u_pokemon.moves[1/**/][2]));
                layers[1] = getResources().getDrawable(R.drawable.pressed);
                layeredDrawable = new LayerDrawable(layers);
                move2/**/.setBackground(layeredDrawable);
            } else move2.setBackgroundResource(buttonBackground(u_pokemon.moves[1][2]));
            styledText = "<font color='#000000'><b>"
            + u_pokemon.moves[1][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + u_pokemon.moves[1][2] + "\t\tPWR:" + ((u_pokemon.moves[1][5].equals("null"))? "---" : u_pokemon.moves[1][5] + "</font></small>");
            move2.setText(Html.fromHtml(styledText));
        } else {
            move2.setBackgroundResource(R.drawable.normal_bubble);
            move2.setText("---");
        }
        
        move2.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        movesRow1.addView(move2);
        
        LinearLayout movesRow2 = new LinearLayout(this);
        movesRow2.setId(MOVES_ROW2_ID);
        movesRow2.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        movesRow2.setOrientation(LinearLayout.HORIZONTAL);
        movesRow2.setWeightSum(2f);
        
        move3 = new Button(this);
        move3.setId(MOVE3_ID);
        move3.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        move3.setPadding(0, 0, 0, 0);
        
        if(u_pokemon.moves[2] != null && u_pokemon.moves[2]/**/[1] != null && u_pokemon.moves[2]/**/[2] != null && u_pokemon.moves[2]/**/[5] != null){
            if(pressedButton == 3/**/){
                layers[0] = getResources().getDrawable(buttonBackground(u_pokemon.moves[2/**/][2]));
                layers[1] = getResources().getDrawable(R.drawable.pressed);
                layeredDrawable = new LayerDrawable(layers);
                move3/**/.setBackground(layeredDrawable);
            } else move3.setBackgroundResource(buttonBackground(u_pokemon.moves[2][2]));
            styledText = "<font color='#000000'><b>"
            + u_pokemon.moves[2][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + u_pokemon.moves[2][2] + "\t\tPWR:" + ((u_pokemon.moves[2][5].equals("null"))? "---" : u_pokemon.moves[2][5] + "</font></small>");
            move3.setText(Html.fromHtml(styledText));
        } else {
            move3.setBackgroundResource(R.drawable.normal_bubble);
            move3.setText("---");
        }
        
        move3.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        movesRow2.addView(move3);
        
        move4 = new Button(this);
        move4.setId(MOVE4_ID);
        move4.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        move4.setPadding(0, 0, 0, 0);
        
        if(u_pokemon.moves[3] != null && u_pokemon.moves[3]/**/[1] != null && u_pokemon.moves[3]/**/[2] != null && u_pokemon.moves[3]/**/[5] != null){
            if(pressedButton == 4/**/){
                layers[0] = getResources().getDrawable(buttonBackground(u_pokemon.moves[3/**/][2]));
                layers[1] = getResources().getDrawable(R.drawable.pressed);
                layeredDrawable = new LayerDrawable(layers);
                move4/**/.setBackground(layeredDrawable);
            } else move4.setBackgroundResource(buttonBackground(u_pokemon.moves[3][2]));
            styledText = "<font color='#000000'><b>"
            + u_pokemon.moves[3][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + u_pokemon.moves[3][2] + "\t\tPWR:" + ((u_pokemon.moves[3][5].equals("null"))? "---" : u_pokemon.moves[3][5] + "</font></small>");
            move4.setText(Html.fromHtml(styledText));
        } else {
            move4.setBackgroundResource(R.drawable.normal_bubble);
            move4.setText("---");
        }
        
        move4.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        movesRow2.addView(move4);
        // Add layouts
        drawer.addView(goBack);
        drawer.addView(movesRow1);
        drawer.addView(movesRow2);
        
        // Set up onTouch listeners
        close.setOnTouchListener(newListener(7));        
        if(u_pokemon.moves[0][1] != null) move1.setOnTouchListener(newListener(1));        
        if(u_pokemon.moves[1][1] != null) move2.setOnTouchListener(newListener(2));        
        if(u_pokemon.moves[2][1] != null) move3.setOnTouchListener(newListener(3));        
        if(u_pokemon.moves[3][1] != null) move4.setOnTouchListener(newListener(4));
    }

    private void buildTeamLayoutLandscape(int pressedButton){
        drawerState = "team-layout";
        drawer.setWeightSum(10f);
        
        LinearLayout goBack = new LinearLayout(this);
        LinearLayout.LayoutParams FULL_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 2f);
        FULL_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        goBack.setLayoutParams(FULL_WRAP_WEIGHT_PARAMS);
        goBack.setOrientation(LinearLayout.VERTICAL);
        goBack.setWeightSum(1f);
        
        close = new Button(this);
        close.setId(X_ID);
        if(pressedButton == 7/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            close/**/.setBackground(layeredDrawable);
        } else close.setBackgroundResource(R.drawable.normal_bubble);
        LinearLayout.LayoutParams WRAP_HARD_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 60, 1f);
        close.setLayoutParams(WRAP_HARD_WEIGHT_PARAMS);
        close.setPadding(0, 0, 0, 0);
        close.setText("X");
        close.setTextColor(Color.argb(255, 0, 0, 0));
        close.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        goBack.addView(close);
        
        LinearLayout teamRow = new LinearLayout(this);
        teamRow.setId(TEAM_ROW1_ID);
        LinearLayout.LayoutParams MATCH_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 8f);
        MATCH_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        teamRow.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        teamRow.setOrientation(LinearLayout.HORIZONTAL);
        teamRow.setWeightSum(6f);
        
        String styledText;
        pkmn1 = new Button(this);
        pkmn1.setId(PKMN1_ID);
        if(pressedButton == 1/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            pkmn1/**/.setBackground(layeredDrawable);
        } else pkmn1.setBackgroundResource(R.drawable.normal_bubble);
        LinearLayout.LayoutParams FULL_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
        pkmn1.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn1.setPadding(0, 0, 0, 0);
        
        if(team[0].nickname != null){
            styledText = "<font color='#000000'><b>" + ((team[0].nickname.equals("Mr_Mime"))? "Mr. Mime" : team[0].nickname) + "</b></font>";
            pkmn1.setText(Html.fromHtml(styledText));
        } else pkmn1.setText(R.string.pkmn1);
        
        pkmn1.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn1);
        
        pkmn2 = new Button(this);
        pkmn2.setId(PKMN2_ID);
        if(pressedButton == 2/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            pkmn2/**/.setBackground(layeredDrawable);
        } else pkmn2.setBackgroundResource(R.drawable.normal_bubble);
        pkmn2.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn2.setPadding(0, 0, 0, 0);
        
        if(team[1].nickname != null){
            styledText = "<font color='#000000'><b>" + ((team[1].nickname.equals("Mr_Mime"))? "Mr. Mime" : team[1].nickname) + "</b></font>";
            pkmn2.setText(Html.fromHtml(styledText));
        } else pkmn2.setText(R.string.pkmn2);
        
        pkmn2.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn2);
        
        pkmn3 = new Button(this);
        pkmn3.setId(PKMN3_ID);
        if(pressedButton == 3/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            pkmn3/**/.setBackground(layeredDrawable);
        } else pkmn3.setBackgroundResource(R.drawable.normal_bubble);
        pkmn3.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn3.setPadding(0, 0, 0, 0);
        
        if(team[2].nickname != null){
            styledText = "<font color='#000000'><b>" + ((team[2].nickname.equals("Mr_Mime"))? "Mr. Mime" : team[2].nickname) + "</b></font>";
            pkmn3.setText(Html.fromHtml(styledText));
        } else pkmn3.setText(R.string.pkmn3);
        
        pkmn3.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn3.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn3);
        
        pkmn4 = new Button(this);
        pkmn4.setId(PKMN4_ID);
        if(pressedButton == 4/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            pkmn4/**/.setBackground(layeredDrawable);
        } else pkmn4.setBackgroundResource(R.drawable.normal_bubble);
        pkmn4.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn4.setPadding(0, 0, 0, 0);
        
        if(team[3].nickname != null){
            styledText = "<font color='#000000'><b>" + ((team[3].nickname.equals("Mr_Mime"))? "Mr. Mime" : team[3].nickname) + "</b></font>";
            pkmn4.setText(Html.fromHtml(styledText));
        } else pkmn4.setText(R.string.pkmn4);
        
        pkmn4.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn4.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn4);
        
        pkmn5 = new Button(this);
        pkmn5.setId(PKMN5_ID);
        if(pressedButton == 5/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            pkmn5/**/.setBackground(layeredDrawable);
        } else pkmn5.setBackgroundResource(R.drawable.normal_bubble);
        pkmn5.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn5.setPadding(0, 0, 0, 0);
        
        if(team[4].nickname != null){
            styledText = "<font color='#000000'><b>" + ((team[4].nickname.equals("Mr_Mime"))? "Mr. Mime" : team[4].nickname) + "</b></font>";
            pkmn5.setText(Html.fromHtml(styledText));
        } else pkmn5.setText(R.string.pkmn5);
        
        pkmn5.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn5.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn5);
        
        pkmn6 = new Button(this);
        pkmn6.setId(PKMN6_ID);
        if(pressedButton == 6/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            pkmn6/**/.setBackground(layeredDrawable);
        } else pkmn6.setBackgroundResource(R.drawable.normal_bubble);
        pkmn6.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn6.setPadding(0, 0, 0, 0);
        
        if(team[5].nickname != null){
            styledText = "<font color='#000000'><b>" + ((team[5].nickname.equals("Mr_Mime"))? "Mr. Mime" : team[5].nickname) + "</b></font>";
            pkmn6.setText(Html.fromHtml(styledText));
        } else pkmn6.setText(R.string.pkmn6);
        
        pkmn6.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn6.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn6);
        
        // Add layouts
        drawer.addView(goBack);
        drawer.addView(teamRow);
        
        // Set up onTouch listeners
        close.setOnTouchListener(newListener(7));
        pkmn1.setOnTouchListener(newListener(1));
        pkmn2.setOnTouchListener(newListener(2));
        pkmn3.setOnTouchListener(newListener(3));
        pkmn4.setOnTouchListener(newListener(4));
        pkmn5.setOnTouchListener(newListener(5));
        pkmn6.setOnTouchListener(newListener(6));
    }

    private void buildTeamLayoutPortrait(int pressedButton){
        drawerState = "team-layout";
        drawer.setWeightSum(10f);
        
        LinearLayout goBack = new LinearLayout(this);
        LinearLayout.LayoutParams FULL_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
        FULL_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        goBack.setLayoutParams(FULL_WRAP_WEIGHT_PARAMS);
        goBack.setOrientation(LinearLayout.VERTICAL);
        goBack.setWeightSum(1f);
        
        close = new Button(this);
        close.setId(X_ID);
        if(pressedButton == 7/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            close/**/.setBackground(layeredDrawable);
        } else close.setBackgroundResource(R.drawable.normal_bubble);
        LinearLayout.LayoutParams WRAP_HARD_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 50, 1f);
        close.setLayoutParams(WRAP_HARD_WEIGHT_PARAMS);
        close.setPadding(0, 0, 0, 0);
        close.setText("X");
        close.setTextColor(Color.argb(255, 0, 0, 0));
        close.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        goBack.addView(close);
        
        LinearLayout teamRow1 = new LinearLayout(this);
        teamRow1.setId(TEAM_ROW1_ID);
        LinearLayout.LayoutParams MATCH_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 4.5f);
        MATCH_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        teamRow1.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        teamRow1.setOrientation(LinearLayout.HORIZONTAL);
        teamRow1.setWeightSum(3f);
        
        String styledText;
        pkmn1 = new Button(this);
        pkmn1.setId(PKMN1_ID);
        if(pressedButton == 1/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            pkmn1/**/.setBackground(layeredDrawable);
        } else pkmn1.setBackgroundResource(R.drawable.normal_bubble);
        LinearLayout.LayoutParams FULL_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
        pkmn1.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn1.setPadding(0, 0, 0, 0);
        
        if(team[0].nickname != null){
            styledText = "<font color='#000000'><b>" + ((team[0].nickname.equals("Mr_Mime"))? "Mr. Mime" : team[0].nickname) + "</b></font>";
            pkmn1.setText(Html.fromHtml(styledText));
        } else pkmn1.setText(R.string.pkmn1);
        
        pkmn1.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow1.addView(pkmn1);
        
        pkmn2 = new Button(this);
        pkmn2.setId(PKMN2_ID);
        if(pressedButton == 2/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            pkmn2/**/.setBackground(layeredDrawable);
        } else pkmn2.setBackgroundResource(R.drawable.normal_bubble);
        pkmn2.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn2.setPadding(0, 0, 0, 0);
        
        if(team[1].nickname != null){
            styledText = "<font color='#000000'><b>" + ((team[1].nickname.equals("Mr_Mime"))? "Mr. Mime" : team[1].nickname) + "</b></font>";
            pkmn2.setText(Html.fromHtml(styledText));
        } else pkmn2.setText(R.string.pkmn2);
        
        pkmn2.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow1.addView(pkmn2);
        
        pkmn3 = new Button(this);
        pkmn3.setId(PKMN3_ID);
        if(pressedButton == 3/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            pkmn3/**/.setBackground(layeredDrawable);
        } else pkmn3.setBackgroundResource(R.drawable.normal_bubble);
        pkmn3.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn3.setPadding(0, 0, 0, 0);
        
        if(team[2].nickname != null){
            styledText = "<font color='#000000'><b>" + ((team[2].nickname.equals("Mr_Mime"))? "Mr. Mime" : team[2].nickname) + "</b></font>";
            pkmn3.setText(Html.fromHtml(styledText));
        } else pkmn3.setText(R.string.pkmn3);
        
        pkmn3.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn3.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow1.addView(pkmn3);
        
        LinearLayout teamRow2 = new LinearLayout(this);
        teamRow2.setId(TEAM_ROW2_ID);
        teamRow2.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        teamRow2.setOrientation(LinearLayout.HORIZONTAL);
        teamRow2.setWeightSum(3f);
        
        pkmn4 = new Button(this);
        pkmn4.setId(PKMN4_ID);
        if(pressedButton == 4/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            pkmn4/**/.setBackground(layeredDrawable);
        } else pkmn4.setBackgroundResource(R.drawable.normal_bubble);
        pkmn4.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn4.setPadding(0, 0, 0, 0);
        
        if(team[3].nickname != null){
            styledText = "<font color='#000000'><b>" + ((team[3].nickname.equals("Mr_Mime"))? "Mr. Mime" : team[3].nickname) + "</b></font>";
            pkmn4.setText(Html.fromHtml(styledText));
        } else pkmn4.setText(R.string.pkmn4);
        
        pkmn4.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn4.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow2.addView(pkmn4);
        
        pkmn5 = new Button(this);
        pkmn5.setId(PKMN5_ID);
        if(pressedButton == 5/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            pkmn5/**/.setBackground(layeredDrawable);
        } else pkmn5.setBackgroundResource(R.drawable.normal_bubble);
        pkmn5.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn5.setPadding(0, 0, 0, 0);
        
        if(team[4].nickname != null){
            styledText = "<font color='#000000'><b>" + ((team[4].nickname.equals("Mr_Mime"))? "Mr. Mime" : team[4].nickname) + "</b></font>";
            pkmn5.setText(Html.fromHtml(styledText));
        } else pkmn5.setText(R.string.pkmn5);
        
        pkmn5.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn5.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow2.addView(pkmn5);
        
        pkmn6 = new Button(this);
        pkmn6.setId(PKMN6_ID);
        if(pressedButton == 6/**/){
            layers[0] = getResources().getDrawable(R.drawable.normal_bubble);
            layers[1] = getResources().getDrawable(R.drawable.pressed);
            layeredDrawable = new LayerDrawable(layers);
            pkmn6/**/.setBackground(layeredDrawable);
        } else pkmn6.setBackgroundResource(R.drawable.normal_bubble);
        pkmn6.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn6.setPadding(0, 0, 0, 0);
        
        if(team[5].nickname != null){
            styledText = "<font color='#000000'><b>" + ((team[5].nickname.equals("Mr_Mime"))? "Mr. Mime" : team[5].nickname) + "</b></font>";
            pkmn6.setText(Html.fromHtml(styledText));
        } else pkmn6.setText(R.string.pkmn6);
        
        pkmn6.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn6.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow2.addView(pkmn6);
        
        // Add layouts
        drawer.addView(goBack);
        drawer.addView(teamRow1);
        drawer.addView(teamRow2);
        
        // Set up onTouch listeners
        close.setOnTouchListener(newListener(7));
        pkmn1.setOnTouchListener(newListener(1));
        pkmn2.setOnTouchListener(newListener(2));
        pkmn3.setOnTouchListener(newListener(3));
        pkmn4.setOnTouchListener(newListener(4));
        pkmn5.setOnTouchListener(newListener(5));
        pkmn6.setOnTouchListener(newListener(6));
    }

    public boolean onTouch(View v, MotionEvent event){
        if(!u_actChosen){
            int action = event.getAction();

            /* Determine the type of action */
            if(action == MotionEvent.ACTION_DOWN){ // Initial touch
                touchClear = false; // ALWAYS false upon new touch event, until ACTION_UP
                held = false; // Ensure held is false, due to weird thread issues
                x = event.getX(); // Get initial x
                y = event.getY(); // Get initial y

                /* Determine if touchEvent is w/n bounds of opponent sprite */
                touched_opponent = (x >= Animated.OPPONENT_FRAME_TOPLEFT_X && x <= Animated.OPPONENT_FRAME_TOPRIGHT_X
                        && y >= Animated.OPPONENT_FRAME_TOPLEFT_Y && y <= Animated.OPPONENT_FRAME_BOTTOMLEFT_Y);

                /* Determine if touchEvent is w/n bounds of user sprite, w/n specified margin of error for smooth swiping */
                touched_user = (x >= Animated.USER_FRAME_TOPLEFT_X - MARGIN && x <= Animated.USER_FRAME_TOPRIGHT_X + MARGIN
                        && y >= Animated.USER_FRAME_TOPLEFT_Y - MARGIN && y <= Animated.USER_FRAME_BOTTOMLEFT_Y + MARGIN);

                if(touched_opponent || touched_user){ // then consider the touch as a new touch
                    if(newTouch){ // ALWAYS true unless already considering a touch that has not seen an ACTION_UP event
                        newTouch = false; // Now monitoring touch 
                        delta_x = x; // Latest x
                        delta_y = y; // Latest y

                        // Start hold thread
                        Thread timer = new Thread(){ 
                            @Override
                            public void run(){
                                try {
                                    sleep(TOUCH_LATENCY_TIME); //Approx. idles for 0.0x secs
                                    if(!killHold) held = true; // Determine hold, else point or drag
                                } catch(InterruptedException e) {                
                                }
                            }
                        };

                        timer.start(); //Exec. the embedded class above
                    } // else, touch already considered, thread already started
                } else { // Clear initial x, y
                    x = null;
                    y = null;

                    touched_opponent = false;
                    touched_user = false;
                }
            } else if(action == MotionEvent.ACTION_MOVE && touched_user){
                if(activeMove){ // ALWAYS true unless thread indicates MotionEvent stayed for too long
                    event_x = event.getX(); // Get moving x
                    event_y = event.getY(); // Get moving y

                    if(!(Math.abs(x - event_x) < BOUND && Math.abs(y - event_y) < BOUND) && delta_x != null){ // Not w/n ACTION_DOWN BOUNDS, dragging...
                        if(event_x < delta_x){ //...Right-to-left
                            // Capture delta
                            delta_x = event_x;
                            delta_y = event_x;

                            if(Math.atan2((y - event_y), (x - event_x))*(180/Math.PI) < ANGLE){ // Make sure delta motion has made less than ?° angle
                                if(!act.frozen) Animated.user_shift_x = -1*(int)(x - event_x); // Shift user sprite

                                killHold = true; // Kill determination of hold thread
                                Thread move = new Thread(){ 
                                    @Override
                                    public void run(){
                                        try {
                                            sleep(ILLEGAL_HOLD_TIMER); //Approx. idles for 0.0x secs
                                            if(!touchClear) activeMove = false; // Active move held for too long
                                        } catch(InterruptedException e) {                
                                        }
                                    }
                                };

                                move.start(); //Exec. the embedded class above
                            } // else, swipe angle is too high or low. Not a feasible R-to-L motion
                        } else if(event_x > delta_x){ //...started moving left-to-right, ERROR
                            Animated.user_shift_x = 0; // Reset shift
                            delta_x = null;
                            delta_y = null;
                            error = true; // Invalid touch event
                        } // else not moving right-to-left, NEVER back-tracked left-to-right
                    } // else treat as though it is still a held ACTION_DOWN event
                } else Animated.user_shift_x = 0; // Reset shift because ACTION_MOVE lasted too long
            } else if(action == MotionEvent.ACTION_UP){
                touchClear = true;

                /* Classify the touchEvent */
                if(touched_opponent){ // Determine what type of attack
                    if(!held){
                        if(u_pokemon.PQI[0] != null){
                            if(!u_actChosen && Animated.user_actReady) action(u_pokemon.PQI[0]);
                        } else ;//text.setText("Low-Mid Power (Physical | Special) Attack");
                    } else {
                        if(u_pokemon.PQI[1] != null){
                            if(!u_actChosen && Animated.user_actReady) action(u_pokemon.PQI[1]);
                        } else ;//text.setText("Mid-High Power (Physical | Special) Attack");
                    }
                } else if(touched_user){ // Determine what type of defense or Dodge
                    if(held){
                        if(u_pokemon.PQI[3] != null){
                            if(!u_actChosen && Animated.user_actReady) action(u_pokemon.PQI[3]);
                        } else ;//text.setText("Hard defensive/healing move");
                    } else if(killHold){
                        //text.setText("Dodge attempt");
                    } else if(!error){
                        if(u_pokemon.PQI[2] != null){
                            if(!u_actChosen && Animated.user_actReady) action(u_pokemon.PQI[2]);
                        } else ;//text.setText("Soft defensive/tactical move");
                    } else ;//text.setText("Nothing");
                } else ;//text.setText("Nothing");

                // Reset ALL variables
                touched_opponent = false;
                touched_user = false;
                delta_x = null;
                delta_y = null;
                x = null;
                y = null;
                newTouch = true;
                activeMove = true;
                killHold = false;
                error = false;
                Animated.user_shift_x = 0;            
            }
        }
        return true; // Allows for constant read of TouchEvents
    }

    private void rebuildBackground(){
        layout.removeViewAt(0); // '0' is the numerical position of the ImageView in FrameLayout
        
        /* rebuild background image, detail-for-detail */
        ImageView background = new ImageView(this);
        background.setId(BACKGROUND_ID);
        background.setLayoutParams(FULL_MATCH_PARAMS);
        background.setScaleType(ImageView.ScaleType.FIT_XY);
        background.setBackgroundResource(R.drawable.battleground);
        
        layout.addView(background, 0); // Add new ImageView to layout          
    }
    
    private int buttonBackground(String token){ 
        try {
            if(token.equals("BUG")) return R.drawable.bug_bubble;
            else if(token.equals("DARK")) return R.drawable.dark_bubble;
            else if(token.equals("DRAGON")) return R.drawable.dragon_bubble;
            else if(token.equals("ELECTRIC")) return R.drawable.electric_bubble;
            else if(token.equals("FAIRY")) return R.drawable.fairy_bubble;
            else if(token.equals("FIGHTING")) return R.drawable.fighting_bubble;
            else if(token.equals("FIRE")) return R.drawable.fire_bubble;
            else if(token.equals("FLYING")) return R.drawable.flying_bubble;
            else if(token.equals("GHOST")) return R.drawable.ghost_bubble;
            else if(token.equals("GRASS")) return R.drawable.grass_bubble;
            else if(token.equals("GROUND")) return R.drawable.ground_bubble;
            else if(token.equals("ICE")) return R.drawable.ice_bubble;
            else if(token.equals("POISON")) return R.drawable.poison_bubble;
            else if(token.equals("PSYCHIC")) return R.drawable.psychic_bubble;
            else if(token.equals("ROCK")) return R.drawable.rock_bubble;
            else if(token.equals("STEEL")) return R.drawable.steel_bubble;
            else if(token.equals("WATER")) return R.drawable.water_bubble;
            else return R.drawable.normal_bubble; 
        } catch(Exception e) { //Thrown if there is a null token due to empty move slot 
            return R.drawable.normal_bubble;
        }
    }
    
    protected static int getVoice(){
        if(u_pokemon.dexNo == 1) return R.raw._1;
        else if(u_pokemon.dexNo == 2) return R.raw._2;
        else if(u_pokemon.dexNo == 3) return R.raw._3;
        else if(u_pokemon.dexNo == 4) return R.raw._4;
        else if(u_pokemon.dexNo == 5) return R.raw._5;
        else if(u_pokemon.dexNo == 6) return R.raw._6;
        else if(u_pokemon.dexNo == 7) return R.raw._7;
        else if(u_pokemon.dexNo == 8) return R.raw._8;
        else if(u_pokemon.dexNo == 9) return R.raw._9;
        else if(u_pokemon.dexNo == 10) return R.raw._10;
        else if(u_pokemon.dexNo == 11) return R.raw._11;
        else if(u_pokemon.dexNo == 12) return R.raw._12;
        else if(u_pokemon.dexNo == 13) return R.raw._13;
        else if(u_pokemon.dexNo == 14) return R.raw._14;
        else if(u_pokemon.dexNo == 15) return R.raw._15;
        else if(u_pokemon.dexNo == 16) return R.raw._16;
        else if(u_pokemon.dexNo == 17) return R.raw._17;
        else if(u_pokemon.dexNo == 18) return R.raw._18;
        else if(u_pokemon.dexNo == 19) return R.raw._19;
        else if(u_pokemon.dexNo == 20) return R.raw._20;
        else if(u_pokemon.dexNo == 21) return R.raw._21;
        else if(u_pokemon.dexNo == 22) return R.raw._22;
        else if(u_pokemon.dexNo == 23) return R.raw._23;
        else if(u_pokemon.dexNo == 24) return R.raw._24;
        else if(u_pokemon.dexNo == 25) return R.raw._25;
        else if(u_pokemon.dexNo == 26) return R.raw._26;
        else if(u_pokemon.dexNo == 27) return R.raw._27;
        else if(u_pokemon.dexNo == 28) return R.raw._28;
        else if(u_pokemon.dexNo == 29) return R.raw._29;
        else if(u_pokemon.dexNo == 30) return R.raw._30;
        else if(u_pokemon.dexNo == 31) return R.raw._31;
        else if(u_pokemon.dexNo == 32) return R.raw._32;
        else if(u_pokemon.dexNo == 33) return R.raw._33;
        else if(u_pokemon.dexNo == 34) return R.raw._34;
        else if(u_pokemon.dexNo == 35) return R.raw._35;
        else if(u_pokemon.dexNo == 36) return R.raw._36;
        else if(u_pokemon.dexNo == 37) return R.raw._37;
        else if(u_pokemon.dexNo == 38) return R.raw._38;
        else if(u_pokemon.dexNo == 39) return R.raw._39;
        else if(u_pokemon.dexNo == 40) return R.raw._40;
        else if(u_pokemon.dexNo == 41) return R.raw._41;
        else if(u_pokemon.dexNo == 42) return R.raw._42;
        else if(u_pokemon.dexNo == 43) return R.raw._43;
        else if(u_pokemon.dexNo == 44) return R.raw._44;
        else if(u_pokemon.dexNo == 45) return R.raw._45;
        else if(u_pokemon.dexNo == 46) return R.raw._46;
        else if(u_pokemon.dexNo == 47) return R.raw._47;
        else if(u_pokemon.dexNo == 48) return R.raw._48;
        else if(u_pokemon.dexNo == 49) return R.raw._49;
        else if(u_pokemon.dexNo == 50) return R.raw._50;
        else if(u_pokemon.dexNo == 51) return R.raw._51;
        else if(u_pokemon.dexNo == 52) return R.raw._52;
        else if(u_pokemon.dexNo == 53) return R.raw._53;
        else if(u_pokemon.dexNo == 54) return R.raw._54;
        else if(u_pokemon.dexNo == 55) return R.raw._55;
        else if(u_pokemon.dexNo == 56) return R.raw._56;
        else if(u_pokemon.dexNo == 57) return R.raw._57;
        else if(u_pokemon.dexNo == 58) return R.raw._58;
        else if(u_pokemon.dexNo == 59) return R.raw._59;
        else if(u_pokemon.dexNo == 60) return R.raw._60;
        else if(u_pokemon.dexNo == 61) return R.raw._61;
        else if(u_pokemon.dexNo == 62) return R.raw._62;
        else if(u_pokemon.dexNo == 63) return R.raw._63;
        else if(u_pokemon.dexNo == 64) return R.raw._64;
        else if(u_pokemon.dexNo == 65) return R.raw._65;
        else if(u_pokemon.dexNo == 66) return R.raw._66;
        else if(u_pokemon.dexNo == 67) return R.raw._67;
        else if(u_pokemon.dexNo == 68) return R.raw._68;
        else if(u_pokemon.dexNo == 69) return R.raw._69;
        else if(u_pokemon.dexNo == 70) return R.raw._70;
        else if(u_pokemon.dexNo == 71) return R.raw._71;
        else if(u_pokemon.dexNo == 72) return R.raw._72;
        else if(u_pokemon.dexNo == 73) return R.raw._73;
        else if(u_pokemon.dexNo == 74) return R.raw._74;
        else if(u_pokemon.dexNo == 75) return R.raw._75;
        else if(u_pokemon.dexNo == 76) return R.raw._76;
        else if(u_pokemon.dexNo == 77) return R.raw._77;
        else if(u_pokemon.dexNo == 78) return R.raw._78;
        else if(u_pokemon.dexNo == 79) return R.raw._79;
        else if(u_pokemon.dexNo == 80) return R.raw._80;
        else if(u_pokemon.dexNo == 81) return R.raw._81;
        else if(u_pokemon.dexNo == 82) return R.raw._82;
        else if(u_pokemon.dexNo == 83) return R.raw._83;
        else if(u_pokemon.dexNo == 84) return R.raw._84;
        else if(u_pokemon.dexNo == 85) return R.raw._85;
        else if(u_pokemon.dexNo == 86) return R.raw._86;
        else if(u_pokemon.dexNo == 87) return R.raw._87;
        else if(u_pokemon.dexNo == 88) return R.raw._88;
        else if(u_pokemon.dexNo == 89) return R.raw._89;
        else if(u_pokemon.dexNo == 90) return R.raw._90;
        else if(u_pokemon.dexNo == 91) return R.raw._91;
        else if(u_pokemon.dexNo == 92) return R.raw._92;
        else if(u_pokemon.dexNo == 93) return R.raw._93;
        else if(u_pokemon.dexNo == 94) return R.raw._94;
        else if(u_pokemon.dexNo == 95) return R.raw._95;
        else if(u_pokemon.dexNo == 96) return R.raw._96;
        else if(u_pokemon.dexNo == 97) return R.raw._97;
        else if(u_pokemon.dexNo == 98) return R.raw._98;
        else if(u_pokemon.dexNo == 99) return R.raw._99;
        else if(u_pokemon.dexNo == 100) return R.raw._100;
        else if(u_pokemon.dexNo == 101) return R.raw._101;
        else if(u_pokemon.dexNo == 102) return R.raw._102;
        else if(u_pokemon.dexNo == 103) return R.raw._103;
        else if(u_pokemon.dexNo == 104) return R.raw._104;
        else if(u_pokemon.dexNo == 105) return R.raw._105;
        else if(u_pokemon.dexNo == 106) return R.raw._106;
        else if(u_pokemon.dexNo == 107) return R.raw._107;
        else if(u_pokemon.dexNo == 108) return R.raw._108;
        else if(u_pokemon.dexNo == 109) return R.raw._109;
        else if(u_pokemon.dexNo == 110) return R.raw._110;
        else if(u_pokemon.dexNo == 111) return R.raw._111;
        else if(u_pokemon.dexNo == 112) return R.raw._112;
        else if(u_pokemon.dexNo == 113) return R.raw._113;
        else if(u_pokemon.dexNo == 114) return R.raw._114;
        else if(u_pokemon.dexNo == 115) return R.raw._115;
        else if(u_pokemon.dexNo == 116) return R.raw._116;
        else if(u_pokemon.dexNo == 117) return R.raw._117;
        else if(u_pokemon.dexNo == 118) return R.raw._118;
        else if(u_pokemon.dexNo == 119) return R.raw._119;
        else if(u_pokemon.dexNo == 120) return R.raw._120;
        else if(u_pokemon.dexNo == 121) return R.raw._121;
        else if(u_pokemon.dexNo == 122) return R.raw._122;
        else if(u_pokemon.dexNo == 123) return R.raw._123;
        else if(u_pokemon.dexNo == 124) return R.raw._124;
        else if(u_pokemon.dexNo == 125) return R.raw._125;
        else if(u_pokemon.dexNo == 126) return R.raw._126;
        else if(u_pokemon.dexNo == 127) return R.raw._127;
        else if(u_pokemon.dexNo == 128) return R.raw._128;
        else if(u_pokemon.dexNo == 129) return R.raw._129;
        else if(u_pokemon.dexNo == 130) return R.raw._130;
        else if(u_pokemon.dexNo == 131) return R.raw._131;
        else if(u_pokemon.dexNo == 132) return R.raw._132;
        else if(u_pokemon.dexNo == 133) return R.raw._133;
        else if(u_pokemon.dexNo == 134) return R.raw._134;
        else if(u_pokemon.dexNo == 135) return R.raw._135;
        else if(u_pokemon.dexNo == 136) return R.raw._136;
        else if(u_pokemon.dexNo == 137) return R.raw._137;
        else if(u_pokemon.dexNo == 138) return R.raw._138;
        else if(u_pokemon.dexNo == 139) return R.raw._139;
        else if(u_pokemon.dexNo == 140) return R.raw._140;
        else if(u_pokemon.dexNo == 141) return R.raw._141;
        else if(u_pokemon.dexNo == 142) return R.raw._142;
        else if(u_pokemon.dexNo == 143) return R.raw._143;
        else if(u_pokemon.dexNo == 144) return R.raw._144;
        else if(u_pokemon.dexNo == 145) return R.raw._145;
        else if(u_pokemon.dexNo == 146) return R.raw._146;
        else if(u_pokemon.dexNo == 147) return R.raw._147;
        else if(u_pokemon.dexNo == 148) return R.raw._148;
        else if(u_pokemon.dexNo == 149) return R.raw._149;
        else if(u_pokemon.dexNo == 150) return R.raw._150;
        else return R.raw._151;
    }
    
    private static int getSoundByte(){
        if(u_pokemon.dexNo == 1) return R.raw.cry001;
        else if(u_pokemon.dexNo == 2) return R.raw.cry002;
        else if(u_pokemon.dexNo == 3) return R.raw.cry003;
        else if(u_pokemon.dexNo == 4) return R.raw.cry004;
        else if(u_pokemon.dexNo == 5) return R.raw.cry005;
        else if(u_pokemon.dexNo == 6) return R.raw.cry006;
        else if(u_pokemon.dexNo == 7) return R.raw.cry007;
        else if(u_pokemon.dexNo == 8) return R.raw.cry008;
        else if(u_pokemon.dexNo == 9) return R.raw.cry009;
        else if(u_pokemon.dexNo == 10) return R.raw.cry010;
        else if(u_pokemon.dexNo == 11) return R.raw.cry011;
        else if(u_pokemon.dexNo == 12) return R.raw.cry012;
        else if(u_pokemon.dexNo == 13) return R.raw.cry013;
        else if(u_pokemon.dexNo == 14) return R.raw.cry014;
        else if(u_pokemon.dexNo == 15) return R.raw.cry015;
        else if(u_pokemon.dexNo == 16) return R.raw.cry016;
        else if(u_pokemon.dexNo == 17) return R.raw.cry017;
        else if(u_pokemon.dexNo == 18) return R.raw.cry018;
        else if(u_pokemon.dexNo == 19) return R.raw.cry019;
        else if(u_pokemon.dexNo == 20) return R.raw.cry020;
        else if(u_pokemon.dexNo == 21) return R.raw.cry021;
        else if(u_pokemon.dexNo == 22) return R.raw.cry022;
        else if(u_pokemon.dexNo == 23) return R.raw.cry023;
        else if(u_pokemon.dexNo == 24) return R.raw.cry024;
        else if(u_pokemon.dexNo == 25) return R.raw.cry025;
        else if(u_pokemon.dexNo == 26) return R.raw.cry026;
        else if(u_pokemon.dexNo == 27) return R.raw.cry027;
        else if(u_pokemon.dexNo == 28) return R.raw.cry028;
        else if(u_pokemon.dexNo == 29) return R.raw.cry029;
        else if(u_pokemon.dexNo == 30) return R.raw.cry030;
        else if(u_pokemon.dexNo == 31) return R.raw.cry031;
        else if(u_pokemon.dexNo == 32) return R.raw.cry032;
        else if(u_pokemon.dexNo == 33) return R.raw.cry033;
        else if(u_pokemon.dexNo == 34) return R.raw.cry034;
        else if(u_pokemon.dexNo == 35) return R.raw.cry035;
        else if(u_pokemon.dexNo == 36) return R.raw.cry036;
        else if(u_pokemon.dexNo == 37) return R.raw.cry037;
        else if(u_pokemon.dexNo == 38) return R.raw.cry038;
        else if(u_pokemon.dexNo == 39) return R.raw.cry039;
        else if(u_pokemon.dexNo == 40) return R.raw.cry040;
        else if(u_pokemon.dexNo == 41) return R.raw.cry041;
        else if(u_pokemon.dexNo == 42) return R.raw.cry042;
        else if(u_pokemon.dexNo == 43) return R.raw.cry043;
        else if(u_pokemon.dexNo == 44) return R.raw.cry044;
        else if(u_pokemon.dexNo == 45) return R.raw.cry045;
        else if(u_pokemon.dexNo == 46) return R.raw.cry046;
        else if(u_pokemon.dexNo == 47) return R.raw.cry047;
        else if(u_pokemon.dexNo == 48) return R.raw.cry048;
        else if(u_pokemon.dexNo == 49) return R.raw.cry049;
        else if(u_pokemon.dexNo == 50) return R.raw.cry050;
        else if(u_pokemon.dexNo == 51) return R.raw.cry051;
        else if(u_pokemon.dexNo == 52) return R.raw.cry052;
        else if(u_pokemon.dexNo == 53) return R.raw.cry053;
        else if(u_pokemon.dexNo == 54) return R.raw.cry054;
        else if(u_pokemon.dexNo == 55) return R.raw.cry055;
        else if(u_pokemon.dexNo == 56) return R.raw.cry056;
        else if(u_pokemon.dexNo == 57) return R.raw.cry057;
        else if(u_pokemon.dexNo == 58) return R.raw.cry058;
        else if(u_pokemon.dexNo == 59) return R.raw.cry059;
        else if(u_pokemon.dexNo == 60) return R.raw.cry060;
        else if(u_pokemon.dexNo == 61) return R.raw.cry061;
        else if(u_pokemon.dexNo == 62) return R.raw.cry062;
        else if(u_pokemon.dexNo == 63) return R.raw.cry063;
        else if(u_pokemon.dexNo == 64) return R.raw.cry064;
        else if(u_pokemon.dexNo == 65) return R.raw.cry065;
        else if(u_pokemon.dexNo == 66) return R.raw.cry066;
        else if(u_pokemon.dexNo == 67) return R.raw.cry067;
        else if(u_pokemon.dexNo == 68) return R.raw.cry068;
        else if(u_pokemon.dexNo == 69) return R.raw.cry069;
        else if(u_pokemon.dexNo == 70) return R.raw.cry070;
        else if(u_pokemon.dexNo == 71) return R.raw.cry071;
        else if(u_pokemon.dexNo == 72) return R.raw.cry072;
        else if(u_pokemon.dexNo == 73) return R.raw.cry073;
        else if(u_pokemon.dexNo == 74) return R.raw.cry074;
        else if(u_pokemon.dexNo == 75) return R.raw.cry075;
        else if(u_pokemon.dexNo == 76) return R.raw.cry076;
        else if(u_pokemon.dexNo == 77) return R.raw.cry077;
        else if(u_pokemon.dexNo == 78) return R.raw.cry078;
        else if(u_pokemon.dexNo == 79) return R.raw.cry079;
        else if(u_pokemon.dexNo == 80) return R.raw.cry080;
        else if(u_pokemon.dexNo == 81) return R.raw.cry081;
        else if(u_pokemon.dexNo == 82) return R.raw.cry082;
        else if(u_pokemon.dexNo == 83) return R.raw.cry083;
        else if(u_pokemon.dexNo == 84) return R.raw.cry084;
        else if(u_pokemon.dexNo == 85) return R.raw.cry085;
        else if(u_pokemon.dexNo == 86) return R.raw.cry086;
        else if(u_pokemon.dexNo == 87) return R.raw.cry087;
        else if(u_pokemon.dexNo == 88) return R.raw.cry088;
        else if(u_pokemon.dexNo == 89) return R.raw.cry089;
        else if(u_pokemon.dexNo == 90) return R.raw.cry090;
        else if(u_pokemon.dexNo == 91) return R.raw.cry091;
        else if(u_pokemon.dexNo == 92) return R.raw.cry092;
        else if(u_pokemon.dexNo == 93) return R.raw.cry093;
        else if(u_pokemon.dexNo == 94) return R.raw.cry094;
        else if(u_pokemon.dexNo == 95) return R.raw.cry095;
        else if(u_pokemon.dexNo == 96) return R.raw.cry096;
        else if(u_pokemon.dexNo == 97) return R.raw.cry097;
        else if(u_pokemon.dexNo == 98) return R.raw.cry098;
        else if(u_pokemon.dexNo == 99) return R.raw.cry099;
        else if(u_pokemon.dexNo == 100) return R.raw.cry100;
        else if(u_pokemon.dexNo == 101) return R.raw.cry101;
        else if(u_pokemon.dexNo == 102) return R.raw.cry102;
        else if(u_pokemon.dexNo == 103) return R.raw.cry103;
        else if(u_pokemon.dexNo == 104) return R.raw.cry104;
        else if(u_pokemon.dexNo == 105) return R.raw.cry105;
        else if(u_pokemon.dexNo == 106) return R.raw.cry106;
        else if(u_pokemon.dexNo == 107) return R.raw.cry107;
        else if(u_pokemon.dexNo == 108) return R.raw.cry108;
        else if(u_pokemon.dexNo == 109) return R.raw.cry109;
        else if(u_pokemon.dexNo == 110) return R.raw.cry110;
        else if(u_pokemon.dexNo == 111) return R.raw.cry111;
        else if(u_pokemon.dexNo == 112) return R.raw.cry112;
        else if(u_pokemon.dexNo == 113) return R.raw.cry113;
        else if(u_pokemon.dexNo == 114) return R.raw.cry114;
        else if(u_pokemon.dexNo == 115) return R.raw.cry115;
        else if(u_pokemon.dexNo == 116) return R.raw.cry116;
        else if(u_pokemon.dexNo == 117) return R.raw.cry117;
        else if(u_pokemon.dexNo == 118) return R.raw.cry118;
        else if(u_pokemon.dexNo == 119) return R.raw.cry119;
        else if(u_pokemon.dexNo == 120) return R.raw.cry120;
        else if(u_pokemon.dexNo == 121) return R.raw.cry121;
        else if(u_pokemon.dexNo == 122) return R.raw.cry122;
        else if(u_pokemon.dexNo == 123) return R.raw.cry123;
        else if(u_pokemon.dexNo == 124) return R.raw.cry124;
        else if(u_pokemon.dexNo == 125) return R.raw.cry125;
        else if(u_pokemon.dexNo == 126) return R.raw.cry126;
        else if(u_pokemon.dexNo == 127) return R.raw.cry127;
        else if(u_pokemon.dexNo == 128) return R.raw.cry128;
        else if(u_pokemon.dexNo == 129) return R.raw.cry129;
        else if(u_pokemon.dexNo == 130) return R.raw.cry130;
        else if(u_pokemon.dexNo == 131) return R.raw.cry131;
        else if(u_pokemon.dexNo == 132) return R.raw.cry132;
        else if(u_pokemon.dexNo == 133) return R.raw.cry133;
        else if(u_pokemon.dexNo == 134) return R.raw.cry134;
        else if(u_pokemon.dexNo == 135) return R.raw.cry135;
        else if(u_pokemon.dexNo == 136) return R.raw.cry136;
        else if(u_pokemon.dexNo == 137) return R.raw.cry137;
        else if(u_pokemon.dexNo == 138) return R.raw.cry138;
        else if(u_pokemon.dexNo == 139) return R.raw.cry139;
        else if(u_pokemon.dexNo == 140) return R.raw.cry140;
        else if(u_pokemon.dexNo == 141) return R.raw.cry141;
        else if(u_pokemon.dexNo == 142) return R.raw.cry142;
        else if(u_pokemon.dexNo == 143) return R.raw.cry143;
        else if(u_pokemon.dexNo == 144) return R.raw.cry144;
        else if(u_pokemon.dexNo == 145) return R.raw.cry145;
        else if(u_pokemon.dexNo == 146) return R.raw.cry146;
        else if(u_pokemon.dexNo == 147) return R.raw.cry147;
        else if(u_pokemon.dexNo == 148) return R.raw.cry148;
        else if(u_pokemon.dexNo == 149) return R.raw.cry149;
        else if(u_pokemon.dexNo == 150) return R.raw.cry150;
        else return R.raw.cry151;
    }
    
    protected void playSoundEffect(int soundbyte){
        sfx = MediaPlayer.create(this, soundbyte);
        sfx.start(); //Play sound
    }
    
    private void action(int move){ 
        //text.setText("Button" + (move + 1) + ": " + u_pokemon.moves[move][0] + ", " + u_pokemon.moves[move][1] + ", " + u_pokemon.moves[move][2] + ", " + u_pokemon.moves[move][3] + ", " + u_pokemon.moves[move][4] + ", " + u_pokemon.moves[move][5] + ", " + u_pokemon.moves[move][6] + ", " + u_pokemon.moves[move][7]);
        u_actChosen = true;
        index = move;
        
        if(u_pokemon.moves[move][4].equals("1") || u_pokemon.moves[move][4].equals("3")){ 
            Animated.user_speedbar = Animated.speedbar_end_x - Animated.speedbar_start_x + 10;
            Animated.USER_SPEED_LOCK = true;
        }
        
        globalHandler = new Handler();
        
        final Runnable thread = new Runnable(){
            Handler handler = globalHandler;
            
            public void run(){
                if(Animated.user_speedbar >= Animated.speedbar_end_x - Animated.speedbar_start_x){
                    playSoundEffect(getVoice());
                    act.initialize(u_pokemon.moves[index]);
                    return;
                }
                handler.postDelayed(this, 0);
            }
        };

        globalHandler.postDelayed(thread, 0);
    }
    
    private void swap(int button){
        if(u_pokemon != team[button]){
            temp = u_pokemon;
            u_pokemon = team[button];
            team[button] = temp;
            team[0] = u_pokemon;
            Animated.user_pokemon_HP = u_pokemon.HP;
            Animated.user_pokemon_lvl = u_pokemon.level;
            surface.setPokemon(2, (u_pokemon.name).toLowerCase());
            /* Then you load their moves here */
            onClick(vMoves);
            onClick(vSwap);
            Animated.user_speed_inc = 0;
            Animated.user_speedbar = 0;
            Animated.user_speedbar_percentage = 0;
            Animated.user_actReady = false;
            
            /* Delete later */
            Animated.opponent_speed_inc = 0;
            Animated.opponent_speedbar = 0;
            Animated.opponent_speedbar_percentage = 0;
            Animated.opponent_actReady = false;
        } //else //text.setText("Same Pokemon");
    }
}