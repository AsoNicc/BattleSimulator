package Game.Pokemon;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
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
    protected static int ARENABOX;
    private Animated surface;
    private static Pokemon pokemon;
    private boolean activeMove = true, error = false,  held = false, initialState = true, 
            killHold = false, newTouch = true, touchClear = true, touched_user, touched_opponent;
    private Button pokeball, buff, cheer, close, moves, forfeit, move1, move2, move3, move4, pkmn1, pkmn2, pkmn3, pkmn4, pkmn5, pkmn6, swap;
    private Configuration config; 
    private Float delta_x, delta_y, event_x, event_y, x, y;
    private FrameLayout layout;
    private Handler handler;
    private ImageView viewer;
    private int frame, PKMN1, PKMN2, PKMN3, PKMN4, PKMN5, PKMN6; /*PKMN? holds the dexNos*/
    private static int BACKGROUND_ID, CHEER_ID, SWAP_AND_FORFEIT_ID, BUFF_ID, FORFEIT_ID, MOVES_AND_BUFF_ID, MOVES_ID, SWAP_ID;
    private final int ANGLE = 45, BOUND = 75, MARGIN = 25, MOVES_ROW1_ID = 2131165202, 
            MOVES_ROW2_ID = 2131165203, TEAM_ROW1_ID = 2131165204, TEAM_ROW2_ID = 2131165205, 
            MOVE1_ID = 2131165206, MOVE2_ID = 2131165207, MOVE3_ID = 2131165208, 
            MOVE4_ID = 2131165209, PKMN1_ID = 2131165210, PKMN2_ID = 2131165211, 
            PKMN3_ID = 2131165212, PKMN4_ID = 2131165213, PKMN5_ID = 2131165214, 
            PKMN6_ID = 2131165215, X_ID = 2131165216, TOUCH_LATENCY_TIME = 500, ILLEGAL_HOLD_TIMER = 500;
    private LinearLayout drawer;
    private final LinearLayout.LayoutParams FULL_MATCH_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    private static final Pokemon[] team = new Pokemon[6];
    private Pokemon temp;
    private final Random gen = new Random();
    private static final String STATE_POKEMON = "pokemon", STATE_FRAME = "frame", 
            STATE_IMAGE_ID = "iv";
    public static TextView text;
    private static View vMoves, vSwap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        try {
            super.onCreate(savedInstanceState);
            View root = LayoutInflater.from(this).inflate(R.layout.battlefield, null); //Holds reference data of entire layout
            setContentView(root); //Sets view
            
            vMoves = root.findViewById(R.id.bMoves);
            vSwap = root.findViewById(R.id.bSwap); 
            text = (TextView)findViewById(R.id.tvException); //Prep for debugging use
            
            /* Used in determining postions of sprites, and sprtie frames */
            WindowManager window = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            Display screen = window.getDefaultDisplay();
            Point size = new Point();
            screen.getSize(size);
            ARENABOX = Math.min(size.x, size.y);
            
            /* Used to clear EVERY SharedPreferences */
//            if(true){
//                Settings load = new Settings(this);
//                load.clearAll();
//                load.setAll();
//            }
            
            surface = new Animated(this);
            
            // Check whether we're recreating a previously destroyed instance
            if(savedInstanceState != null){
                initialState = false;

                // Restore value of members from saved state
                surface.setPokemon(savedInstanceState.getString(STATE_POKEMON));
                frame = savedInstanceState.getInt(STATE_FRAME);
            } else {
                // Probably initialize members with default values for a new instance
                team[0] = new Pokemon(this);
                team[1] = new Pokemon(this);
                team[2] = new Pokemon(this);
                team[3] = new Pokemon(this);
                team[4] = new Pokemon(this);
                team[5] = new Pokemon(this);
                pokemon = team[0];
                Animated.pokemon_HP = pokemon.HP;
                Animated.pokemon_lvl = pokemon.level;        
            }        

            initialize();
        } catch(Exception e) {
            text.setText(e.toString());
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        // Save the user's current game state, FIRST...
        savedInstanceState.putString(STATE_POKEMON, Animated.pokemon);
        savedInstanceState.putInt(STATE_FRAME, frame);
        savedInstanceState.putInt(STATE_IMAGE_ID, BACKGROUND_ID);
        // ...THEN, this Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        // FIRST, Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // THEN, Restore state members from saved instance
        surface.setPokemon(savedInstanceState.getString(STATE_POKEMON));
        frame = savedInstanceState.getInt(STATE_FRAME);
        BACKGROUND_ID = savedInstanceState.getInt(STATE_IMAGE_ID);
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
                //if(initialState) surface.setPokemon(items[position]);
                if(initialState) surface.setPokemon(items[pokemon.dexNo - 1]);
                else initialState = true;
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
        moves.setOnClickListener(this);
        swap.setOnClickListener(this);
        buff.setOnClickListener(this);
        forfeit.setOnClickListener(this);        
        
        try { // Set up onTouch Listener
            viewer.setOnTouchListener(this);
        } catch(Exception e) { // onCreate in landscape orientation
            rebuildBackground(); // Restore background ImageView
            viewer = (ImageView)findViewById(R.id.ivBackground); // Assign ID
            BACKGROUND_ID = R.id.ivBackground; // Store ID value
            viewer.setOnTouchListener(this); // Retry
        }
        
//        startDrawerAnimation();*/
    }

    private void startDrawerAnimation(){
        handler = new Handler();
        
        final Runnable thread = new Runnable(){
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
                handler.postDelayed(this, 30);
            }
        };

        handler.postDelayed(thread, 30);
    }

    public void onClick(View v){
        if(v.getId() == R.id.bMoves){
            drawer.removeAllViews();
            config = getResources().getConfiguration();
            
            if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) buildMovesLayoutLandscape();
            else buildMovesLayoutPortait();
        } else if(v.getId() == R.id.bSwap){
            drawer.removeAllViews();
            config = getResources().getConfiguration();
            
            if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) buildTeamLayoutLandscape();
            else buildTeamLayoutPortait();    
        } else if(v.getId() == R.id.bBuff){
            // NOTHING YET
            //text.setText("Buff applied!");
            text.setText("Inst: " + pokemon + " DexNo: " + pokemon.dexNo + " Name: " + pokemon.name + " HP: " + pokemon.HP + " Atk: " + pokemon.Atk + " Def: " + pokemon.Def + " SpA: " + pokemon.SpA + " SpD: " + pokemon.SpD + " Spe: " + pokemon.Spe + " TYPE1: " + pokemon.TYPE1 + " TYPE2: " + pokemon.TYPE2 + " Nature: " + pokemon.nature);
            /* delete this later. this is a test case of external influence on Animated class */
            Animated.user_speed_percentage = 0;
            Animated.opponent_speed_percentage = 0;
        } else if(v.getId() == R.id.bForfeit){
            finish();
        } else if(v.getId() == X_ID){
            drawer.removeAllViews();
            config = getResources().getConfiguration();
            
            if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) restoreDrawerLayoutLandscape();
            else restoreDrawerLayoutPortrait();
        } else if(v.getId() == MOVE1_ID) action(0);
        else if(v.getId() == MOVE2_ID) action(1);
        else if(v.getId() == MOVE3_ID) action(2);
        else if(v.getId() == MOVE4_ID) action(3);
        else if(v.getId() == PKMN1_ID){
            // NOTHING YET
//            text.setText("Pokemon 1");
            swap(0);
        } else if(v.getId() == PKMN2_ID){
            // NOTHING YET
//            text.setText("Pokemon 2");
            swap(1);
        } else if(v.getId() == PKMN3_ID){
            // NOTHING YET
            swap(2);
        } else if(v.getId() == PKMN4_ID){
            // NOTHING YET
//            text.setText("Pokemon 4");
            swap(3);
        } else if(v.getId() == PKMN5_ID){
            // NOTHING YET
//            text.setText("Pokemon 5");
            swap(4);
        } else if(v.getId() == PKMN6_ID){
            // NOTHING YET
//            text.setText("Pokemon 6");
            swap(5);
        } else if(v.getId() == CHEER_ID){
            // NOTHING YET
            text.setText("Go!!!");
        }
    }

    private void restoreDrawerLayoutLandscape(){
        drawer.setWeightSum(0f);
        
        LinearLayout movesRow = new LinearLayout(this);
        movesRow.setId(MOVES_ROW1_ID);
        FULL_MATCH_PARAMS.gravity = Gravity.CENTER;
        movesRow.setLayoutParams(FULL_MATCH_PARAMS);
        movesRow.setOrientation(LinearLayout.HORIZONTAL);
        movesRow.setWeightSum(4f);
        
        moves = new Button(this);
        moves.setId(MOVES_ID);
        moves.setBackgroundResource(R.drawable.normal_bubble);
        LinearLayout.LayoutParams WRAP_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f);
        moves.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        moves.setPadding(0, 0, 0, 0);
        moves.setText(R.string.moves);
        moves.setTextColor(Color.argb(255, 0, 0, 0));
        moves.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(moves);
        
        swap = new Button(this);
        swap.setId(SWAP_ID);
        swap.setBackgroundResource(R.drawable.normal_bubble);
        swap.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        swap.setPadding(0, 0, 0, 0);
        swap.setText(R.string.swap);
        swap.setTextColor(Color.argb(255, 0, 0, 0));
        swap.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(swap);
        
        buff = new Button(this);
        buff.setId(BUFF_ID);
        buff.setBackgroundResource(R.drawable.normal_bubble);
        buff.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        buff.setPadding(0, 0, 0, 0);
        buff.setText(R.string.buff);
        buff.setTextColor(Color.argb(255, 0, 0, 0));
        buff.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(buff);
        
        forfeit = new Button(this);
        forfeit.setId(FORFEIT_ID);
        forfeit.setBackgroundResource(R.drawable.normal_bubble);
        forfeit.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        forfeit.setPadding(0, 0, 0, 0);
        forfeit.setText(R.string.forfeit);
        forfeit.setTextColor(Color.argb(255, 0, 0, 0));
        forfeit.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(forfeit);
        
        // Add layout
        drawer.addView(movesRow);
        
        // Set up onClick listeners
        moves.setOnClickListener(this);
        swap.setOnClickListener(this);
        buff.setOnClickListener(this);
        forfeit.setOnClickListener(this);
    }
    
    private void restoreDrawerLayoutPortrait(){
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
        moves.setBackgroundResource(R.drawable.normal_bubble);
        LinearLayout.LayoutParams WRAP_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f);
        moves.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        moves.setPadding(0, 0, 0, 0);
        moves.setText(R.string.moves);
        moves.setTextColor(Color.argb(255, 0, 0, 0));
        moves.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesAndBuff.addView(moves);
        
        buff = new Button(this);
        buff.setId(BUFF_ID);
        buff.setBackgroundResource(R.drawable.normal_bubble);
        buff.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        buff.setPadding(0, 0, 0, 0);
        buff.setText(R.string.buff);
        buff.setTextColor(Color.argb(255, 0, 0, 0));
        buff.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesAndBuff.addView(buff);
        
        LinearLayout swapAndForfeit = new LinearLayout(this);
        swapAndForfeit.setId(SWAP_AND_FORFEIT_ID);
        swapAndForfeit.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        swapAndForfeit.setOrientation(LinearLayout.HORIZONTAL);
        swapAndForfeit.setWeightSum(2f);
        
        swap = new Button(this);
        swap.setId(SWAP_ID);
        swap.setBackgroundResource(R.drawable.normal_bubble);
        swap.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        swap.setPadding(0, 0, 0, 0);
        swap.setText(R.string.swap);
        swap.setTextColor(Color.argb(255, 0, 0, 0));
        swap.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        swapAndForfeit.addView(swap);
        
        forfeit = new Button(this);
        forfeit.setId(FORFEIT_ID);
        forfeit.setBackgroundResource(R.drawable.normal_bubble);
        forfeit.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        forfeit.setPadding(0, 0, 0, 0);
        forfeit.setText(R.string.forfeit);
        forfeit.setTextColor(Color.argb(255, 0, 0, 0));
        forfeit.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        swapAndForfeit.addView(forfeit);
        
        // Add layouts
        drawer.addView(movesAndBuff);
        drawer.addView(swapAndForfeit);
        
        // Set up onClick listeners
        moves.setOnClickListener(this);
        swap.setOnClickListener(this);
        buff.setOnClickListener(this);
        forfeit.setOnClickListener(this); 
    }
    
    private void buildMovesLayoutLandscape(){ try{       
        drawer.setWeightSum(10f);
        
        LinearLayout goBack = new LinearLayout(this);
        LinearLayout.LayoutParams FULL_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 2f);
        FULL_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        goBack.setLayoutParams(FULL_WRAP_WEIGHT_PARAMS);
        goBack.setOrientation(LinearLayout.VERTICAL);
        goBack.setWeightSum(1f);
        
        close = new Button(this);
        close.setId(X_ID);
        close.setBackgroundResource(R.drawable.normal_bubble);
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
        
        if(pokemon.moves[0] != null && pokemon.moves[0]/**/[1] != null && pokemon.moves[0]/**/[2] != null && pokemon.moves[0]/**/[5] != null){
            move1.setBackgroundResource(buttonBackground(pokemon.moves[0][2]));
            styledText = "<font color='#000000'><b>"
            + pokemon.moves[0][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + pokemon.moves[0][2] + "\t\tPWR:" + ((pokemon.moves[0][5].equals("null"))? "---" : pokemon.moves[0][5]) + "</font></small>";
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
            
        if(pokemon.moves[1] != null && pokemon.moves[1]/**/[1] != null && pokemon.moves[1]/**/[2] != null && pokemon.moves[1]/**/[5] != null){
            move2.setBackgroundResource(buttonBackground(pokemon.moves[1][2]));
            styledText = "<font color='#000000'><b>"
            + pokemon.moves[1][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + pokemon.moves[1][2] + "\t\tPWR:" + ((pokemon.moves[1][5].equals("null"))? "---" : pokemon.moves[1][5]) + "</font></small>";
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
        
        if(pokemon.moves[2] != null&& pokemon.moves[2]/**/[1] != null && pokemon.moves[2]/**/[2] != null && pokemon.moves[2]/**/[5] != null){
            move3.setBackgroundResource(buttonBackground(pokemon.moves[2][2]));
            styledText = "<font color='#000000'><b>"
            + pokemon.moves[2][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + pokemon.moves[2][2] + "\t\tPWR:" + ((pokemon.moves[2][5].equals("null"))? "---" : pokemon.moves[2][5]) + "</font></small>";
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
        
        if(pokemon.moves[3] != null&& pokemon.moves[3]/**/[1] != null && pokemon.moves[3]/**/[2] != null && pokemon.moves[3]/**/[5] != null){
            move4.setBackgroundResource(buttonBackground(pokemon.moves[3][2]));
            styledText = "<font color='#000000'><b>"
            + pokemon.moves[3][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + pokemon.moves[3][2] + "\t\tPWR:" + ((pokemon.moves[3][5].equals("null"))? "---" : pokemon.moves[3][5]) + "</font></small>";
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
        
        // Set up onClick listeners
        close.setOnClickListener(this);
        if(pokemon.moves[0][1] != null) move1.setOnClickListener(this);
        if(pokemon.moves[1][1] != null) move2.setOnClickListener(this);
        if(pokemon.moves[2][1] != null) move3.setOnClickListener(this);
        if(pokemon.moves[3][1] != null) move4.setOnClickListener(this); } catch(Exception e) { text.setText(e.toString()); }
    }

    private void buildMovesLayoutPortait(){ try {
        drawer.setWeightSum(10f);
        
        LinearLayout goBack = new LinearLayout(this);
        LinearLayout.LayoutParams FULL_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
        FULL_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        goBack.setLayoutParams(FULL_WRAP_WEIGHT_PARAMS);
        goBack.setOrientation(LinearLayout.VERTICAL);
        goBack.setWeightSum(1f);
        
        close = new Button(this);
        close.setId(X_ID);
        close.setBackgroundResource(R.drawable.normal_bubble);
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
        
        if(pokemon.moves[0] != null && pokemon.moves[0]/**/[1] != null && pokemon.moves[0]/**/[2] != null && pokemon.moves[0]/**/[5] != null){
            move1.setBackgroundResource(buttonBackground(pokemon.moves[0][2]));
            styledText = "<font color='#000000'><b>"
            + pokemon.moves[0][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + pokemon.moves[0][2] + "\t\tPWR:" + ((pokemon.moves[0]/**/[5] == null)? "Error @move1/**/" : ((pokemon.moves[0][5].equals("null"))? "---" : pokemon.moves[0][5]) + "</font></small>");
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
        
        if(pokemon.moves[1] != null && pokemon.moves[1]/**/[1] != null && pokemon.moves[1]/**/[2] != null && pokemon.moves[1]/**/[5] != null){
            move2.setBackgroundResource(buttonBackground(pokemon.moves[1][2]));
            styledText = "<font color='#000000'><b>"
            + pokemon.moves[1][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + pokemon.moves[1][2] + "\t\tPWR:" + ((pokemon.moves[1][5].equals("null"))? "---" : pokemon.moves[1][5] + "</font></small>");
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
        
        if(pokemon.moves[2] != null && pokemon.moves[2]/**/[1] != null && pokemon.moves[2]/**/[2] != null && pokemon.moves[2]/**/[5] != null){
            move3.setBackgroundResource(buttonBackground(pokemon.moves[2][2]));
            styledText = "<font color='#000000'><b>"
            + pokemon.moves[2][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + pokemon.moves[2][2] + "\t\tPWR:" + ((pokemon.moves[2][5].equals("null"))? "---" : pokemon.moves[2][5] + "</font></small>");
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
        
        if(pokemon.moves[3] != null && pokemon.moves[3]/**/[1] != null && pokemon.moves[3]/**/[2] != null && pokemon.moves[3]/**/[5] != null){
            move4.setBackgroundResource(buttonBackground(pokemon.moves[3][2]));
            styledText = "<font color='#000000'><b>"
            + pokemon.moves[3][1] + "</b></font>" + "<br/><small><font color='#FFFFFF'>" 
            + pokemon.moves[3][2] + "\t\tPWR:" + ((pokemon.moves[3][5].equals("null"))? "---" : pokemon.moves[3][5] + "</font></small>");
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
        
        // Set up onClick listeners
        close.setOnClickListener(this);
        if(pokemon.moves[0][1] != null) move1.setOnClickListener(this);
        if(pokemon.moves[1][1] != null) move2.setOnClickListener(this);
        if(pokemon.moves[2][1] != null) move3.setOnClickListener(this);
        if(pokemon.moves[3][1] != null) move4.setOnClickListener(this); } catch(Exception e) { text.setText(e.toString()); }
    }

    private void buildTeamLayoutLandscape(){
        drawer.setWeightSum(10f);
        
        LinearLayout goBack = new LinearLayout(this);
        LinearLayout.LayoutParams FULL_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 2f);
        FULL_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        goBack.setLayoutParams(FULL_WRAP_WEIGHT_PARAMS);
        goBack.setOrientation(LinearLayout.VERTICAL);
        goBack.setWeightSum(1f);
        
        close = new Button(this);
        close.setId(X_ID);
        close.setBackgroundResource(R.drawable.normal_bubble);
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
        pkmn1.setBackgroundResource(R.drawable.normal_bubble);
        LinearLayout.LayoutParams FULL_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
        pkmn1.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn1.setPadding(0, 0, 0, 0);
        
        if(team[0].nickname != null){
            styledText = "<font color='#000000'><b>" + team[0].nickname + "</b></font>";
            pkmn1.setText(Html.fromHtml(styledText));
        } else pkmn1.setText(R.string.pkmn1);
        
        pkmn1.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn1);
        
        pkmn2 = new Button(this);
        pkmn2.setId(PKMN2_ID);
        pkmn2.setBackgroundResource(R.drawable.normal_bubble);
        pkmn2.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn2.setPadding(0, 0, 0, 0);
        
        if(team[1].nickname != null){
            styledText = "<font color='#000000'><b>" + team[1].nickname + "</b></font>";
            pkmn2.setText(Html.fromHtml(styledText));
        } else pkmn2.setText(R.string.pkmn2);
        
        pkmn2.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn2);
        
        pkmn3 = new Button(this);
        pkmn3.setId(PKMN3_ID);
        pkmn3.setBackgroundResource(R.drawable.normal_bubble);
        pkmn3.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn3.setPadding(0, 0, 0, 0);
        
        if(team[2].nickname != null){
            styledText = "<font color='#000000'><b>" + team[2].nickname + "</b></font>";
            pkmn3.setText(Html.fromHtml(styledText));
        } else pkmn3.setText(R.string.pkmn3);
        
        pkmn3.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn3.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn3);
        
        pkmn4 = new Button(this);
        pkmn4.setId(PKMN4_ID);
        pkmn4.setBackgroundResource(R.drawable.normal_bubble);
        pkmn4.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn4.setPadding(0, 0, 0, 0);
        
        if(team[3].nickname != null){
            styledText = "<font color='#000000'><b>" + team[3].nickname + "</b></font>";
            pkmn4.setText(Html.fromHtml(styledText));
        } else pkmn4.setText(R.string.pkmn4);
        
        pkmn4.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn4.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn4);
        
        pkmn5 = new Button(this);
        pkmn5.setId(PKMN5_ID);
        pkmn5.setBackgroundResource(R.drawable.normal_bubble);
        pkmn5.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn5.setPadding(0, 0, 0, 0);
        
        if(team[4].nickname != null){
            styledText = "<font color='#000000'><b>" + team[4].nickname + "</b></font>";
            pkmn5.setText(Html.fromHtml(styledText));
        } else pkmn5.setText(R.string.pkmn5);
        
        pkmn5.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn5.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn5);
        
        pkmn6 = new Button(this);
        pkmn6.setId(PKMN6_ID);
        pkmn6.setBackgroundResource(R.drawable.normal_bubble);
        pkmn6.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn6.setPadding(0, 0, 0, 0);
        
        if(team[5].nickname != null){
            styledText = "<font color='#000000'><b>" + team[5].nickname + "</b></font>";
            pkmn6.setText(Html.fromHtml(styledText));
        } else pkmn6.setText(R.string.pkmn6);
        
        pkmn6.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn6.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn6);
        
        // Add layouts
        drawer.addView(goBack);
        drawer.addView(teamRow);
        
        // Set up onClick listeners
        close.setOnClickListener(this);
        pkmn1.setOnClickListener(this);
        pkmn2.setOnClickListener(this);
        pkmn3.setOnClickListener(this);
        pkmn4.setOnClickListener(this);
        pkmn5.setOnClickListener(this);
        pkmn6.setOnClickListener(this);
    }

    private void buildTeamLayoutPortait(){
        drawer.setWeightSum(10f);
        
        LinearLayout goBack = new LinearLayout(this);
        LinearLayout.LayoutParams FULL_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
        FULL_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        goBack.setLayoutParams(FULL_WRAP_WEIGHT_PARAMS);
        goBack.setOrientation(LinearLayout.VERTICAL);
        goBack.setWeightSum(1f);
        
        close = new Button(this);
        close.setId(X_ID);
        close.setBackgroundResource(R.drawable.normal_bubble);
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
        pkmn1.setBackgroundResource(R.drawable.normal_bubble);
        LinearLayout.LayoutParams FULL_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
        pkmn1.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn1.setPadding(0, 0, 0, 0);
        
        if(team[0].nickname != null){
            styledText = "<font color='#000000'><b>" + team[0].nickname + "</b></font>";
            pkmn1.setText(Html.fromHtml(styledText));
        } else pkmn1.setText(R.string.pkmn1);
        
        pkmn1.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow1.addView(pkmn1);
        
        pkmn2 = new Button(this);
        pkmn2.setId(PKMN2_ID);
        pkmn2.setBackgroundResource(R.drawable.normal_bubble);
        pkmn2.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn2.setPadding(0, 0, 0, 0);
        
        if(team[1].nickname != null){
            styledText = "<font color='#000000'><b>" + team[1].nickname + "</b></font>";
            pkmn2.setText(Html.fromHtml(styledText));
        } else pkmn2.setText(R.string.pkmn2);
        
        pkmn2.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow1.addView(pkmn2);
        
        pkmn3 = new Button(this);
        pkmn3.setId(PKMN3_ID);
        pkmn3.setBackgroundResource(R.drawable.normal_bubble);
        pkmn3.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn3.setPadding(0, 0, 0, 0);
        
        if(team[2].nickname != null){
            styledText = "<font color='#000000'><b>" + team[2].nickname + "</b></font>";
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
        pkmn4.setBackgroundResource(R.drawable.normal_bubble);
        pkmn4.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn4.setPadding(0, 0, 0, 0);
        
        if(team[3].nickname != null){
            styledText = "<font color='#000000'><b>" + team[3].nickname + "</b></font>";
            pkmn4.setText(Html.fromHtml(styledText));
        } else pkmn4.setText(R.string.pkmn4);
        
        pkmn4.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn4.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow2.addView(pkmn4);
        
        pkmn5 = new Button(this);
        pkmn5.setId(PKMN5_ID);
        pkmn5.setBackgroundResource(R.drawable.normal_bubble);
        pkmn5.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn5.setPadding(0, 0, 0, 0);
        
        if(team[4].nickname != null){
            styledText = "<font color='#000000'><b>" + team[4].nickname + "</b></font>";
            pkmn5.setText(Html.fromHtml(styledText));
        } else pkmn5.setText(R.string.pkmn5);
        
        pkmn5.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn5.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow2.addView(pkmn5);
        
        pkmn6 = new Button(this);
        pkmn6.setId(PKMN6_ID);
        pkmn6.setBackgroundResource(R.drawable.normal_bubble);
        pkmn6.setLayoutParams(FULL_MATCH_WEIGHT_PARAMS);
        pkmn6.setPadding(0, 0, 0, 0);
        
        if(team[5].nickname != null){
            styledText = "<font color='#000000'><b>" + team[5].nickname + "</b></font>";
            pkmn6.setText(Html.fromHtml(styledText));
        } else pkmn6.setText(R.string.pkmn6);
        
        pkmn6.setTextColor(Color.argb(255, 0, 0, 0));
        pkmn6.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow2.addView(pkmn6);
        
        // Add layouts
        drawer.addView(goBack);
        drawer.addView(teamRow1);
        drawer.addView(teamRow2);
        
        // Set up onClick listeners
        close.setOnClickListener(this);
        pkmn1.setOnClickListener(this);
        pkmn2.setOnClickListener(this);
        pkmn3.setOnClickListener(this);
        pkmn4.setOnClickListener(this);
        pkmn5.setOnClickListener(this);
        pkmn6.setOnClickListener(this);
    }

    public boolean onTouch(View v, MotionEvent event){
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
                            Animated.user_shift_x = (int)(x - event_x); // Shift user sprite

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
        } else if(event.getAction() == MotionEvent.ACTION_UP){
            touchClear = true;
            
            /* Classify the touchEvent */
            if(touched_opponent){ // Determine what type of attack
                if(!held){
                    if(pokemon.PQI[0] != null) action(pokemon.PQI[0]);
                    else text.setText("Low-Mid Power (Physical | Special) Attack");
                }
                else {
                    if(pokemon.PQI[1] != null) action(pokemon.PQI[1]);
                    else text.setText("Mid-High Power (Physical | Special) Attack");
                }
            } else if(touched_user){ // Determine what type of defense or Dodge
                if(held) {
                    if(pokemon.PQI[3] != null) action(pokemon.PQI[3]);
                    else text.setText("Hard defensive/healing move");
                }
                else if(killHold) {
                    text.setText("Dodge attempt");
                }
                else if(!error) {
                    if(pokemon.PQI[2] != null) action(pokemon.PQI[2]);
                    else text.setText("Soft defensive/tactical move");
                }
                else text.setText("Nothing");
            } else text.setText("Nothing");
            
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
    
    private void action(int move){
        if(move == 0){
           text.setText("Button1: " + pokemon.moves[0][0] + ", " + pokemon.moves[0][1] + ", " + pokemon.moves[0][2] + ", " + pokemon.moves[0][3] + ", " + pokemon.moves[0][4] + ", " + pokemon.moves[0][5] + ", " + pokemon.moves[0][6] + ", " + pokemon.moves[0][7]);
        } else if(move == 1){
            text.setText("Button2: " + pokemon.moves[1][0] + ", " + pokemon.moves[1][1] + ", " + pokemon.moves[1][2] + ", " + pokemon.moves[1][3] + ", " + pokemon.moves[1][4] + ", " + pokemon.moves[1][5] + ", " + pokemon.moves[1][6] + ", " + pokemon.moves[1][7]);
        } else if(move == 2){
            text.setText("Button3: " + pokemon.moves[2][0] + ", " + pokemon.moves[2][1] + ", " + pokemon.moves[2][2] + ", " + pokemon.moves[2][3] + ", " + pokemon.moves[2][4] + ", " + pokemon.moves[2][5] + ", " + pokemon.moves[2][6] + ", " + pokemon.moves[2][7]);
        } else if(move == 3){
            text.setText("Button4: " + pokemon.moves[3][0] + ", " + pokemon.moves[3][1] + ", " + pokemon.moves[3][2] + ", " + pokemon.moves[3][3] + ", " + pokemon.moves[3][4] + ", " + pokemon.moves[3][5] + ", " + pokemon.moves[3][6] + ", " + pokemon.moves[3][7]);
        }
    }
    
    private void swap(int button){
        if(pokemon != team[button]){
            temp = pokemon;
            pokemon = team[button];
            team[button] = temp;
            team[0] = pokemon;
            Animated.pokemon_HP = pokemon.HP;
            Animated.pokemon_lvl = pokemon.level;
            surface.setPokemon((pokemon.name).toLowerCase());
            /* Then you load their moves here */
            onClick(vMoves);
            onClick(vSwap);
        } //else text.setText("Same Pokemon");
    }
}