package Game.Pokemon;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import static java.lang.Thread.sleep;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class Battle extends Activity implements OnClickListener, OnTouchListener {
    private Animated surface;
    private boolean activeMove = true, error = false,  held = false, initialState = true, 
            killHold = false, newTouch = true, touchClear = true, touched_user, touched_opponent;
    private Button pokeball, buff, close, moves, forfeit, move1, move2, move3, move4, pkmn1, pkmn2, pkmn3, pkmn4, pkmn5, pkmn6, swap;
    private Configuration config; 
    private Float delta_x, delta_y, event_x, event_y, x, y;
    private FrameLayout layout;
    private Handler handler;
    private ImageView viewer;
    private int frame;
    private static int BACKGROUND_ID, SWAP_AND_FORFEIT_ID, BUFF_ID, FORFEIT_ID, MOVES_AND_BUFF_ID, MOVES_ID, SWAP_ID;
    private final int ANGLE = 45, BOUND = 75, MARGIN = 25, MOVES_ROW1_ID = 2131165202, 
            MOVES_ROW2_ID = 2131165203, TEAM_ROW1_ID = 2131165204, TEAM_ROW2_ID = 2131165205, 
            MOVE1_ID = 2131165206, MOVE2_ID = 2131165207, MOVE3_ID = 2131165208, 
            MOVE4_ID = 2131165209, PKMN1_ID = 2131165210, PKMN2_ID = 2131165211, 
            PKMN3_ID = 2131165212, PKMN4_ID = 2131165213, PKMN5_ID = 2131165214, 
            PKMN6_ID = 2131165215, X_ID = 2131165216, TOUCH_LATENCY_TIME = 500, ILLEGAL_HOLD_TIMER = 500;
    private LinearLayout drawer;
    private final LinearLayout.LayoutParams FULL_MATCH_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    private Moves load; // Temp var used to init. SharedPref data
    private final Random gen = new Random();
    private static SharedPreferences moveData;
    private static final String STATE_POKEMON = "pokemon", STATE_FRAME = "frame", STATE_IMAGE_ID = "iv";
    private static String MOVE1[], MOVE2[], MOVE3[], MOVE4[];
    public static TextView text;
    private static SharedPreferences.Editor edit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.battlefield);
            
            surface = new Animated(this);
            
            text = (TextView)findViewById(R.id.tvException);
        
//            if(true){
//                edit = (getSharedPreferences("genOneMoveList", MODE_PRIVATE)).edit();
//                edit.clear();
//                edit.commit();
//            }

            moveData = getSharedPreferences("genOneMoveList", MODE_PRIVATE);
            if(!moveData.getBoolean("setState", false)){ 
                load = new Moves(this);
                moveData = getSharedPreferences("genOneMoveList", MODE_PRIVATE);
                text.setText("Moves have not been set");    
            } else text.setText("Moves already set");
            
            // Check whether we're recreating a previously destroyed instance
            if(savedInstanceState != null){
                initialState = false;

                // Restore value of members from saved state
                surface.setPokemon(savedInstanceState.getString(STATE_POKEMON));
                frame = savedInstanceState.getInt(STATE_FRAME);
            } else {
                // Probably initialize members with default values for a new instance
            }        

            initialize();
        } catch(Exception e) {
            text.setText(e.toString());
        }
    }
    
    @Override
    protected void onPause(){
        super.onPause();
        // Unregister any active listeners 
        /* No relative listeners @ this moment */
        
        // Adjust any synchronious variables here
        /* No synchronious variables @ this moment */
        
        // Tie together any running thread variables
        /* No running threads @ this moment */
        
        // Nullify the global thread variables for reuse
        /* No global thread variables @ this moment */
        
        /* NOTE: I would like to save the state of variables in Animated */
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

    @Override
    protected void onResume(){
        super.onResume();
        
        // Re-register any active listeners 
        /* No relative listeners @ this moment */
        
        // Re-adjust any synchronious variables here, as needed
        /* No synchronious variables @ this moment */
        
        // Create new instances of any running thread variables
        /* No running threads @ this moment */
        
        /* NOTE: I would like to reset the state of variables in Animated */
    }
    
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
    
    private void initialize(){
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
                if(initialState) surface.setPokemon(items[position]);
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
        pokeball = (Button)findViewById(R.id.bHandle);
        drawer = (LinearLayout)findViewById(R.id.llDrawerContents);
        
        // Assign ids to Button variables
        moves = (Button)findViewById(R.id.bMoves);
        swap = (Button)findViewById(R.id.bSwap);
        buff = (Button)findViewById(R.id.bBuff);
        forfeit = (Button)findViewById(R.id.bForfeit);
        
        // Hold Button & Layout id constants for reuse
        BACKGROUND_ID = R.id.ivBackground;
        MOVES_AND_BUFF_ID = R.id.llMovesAndBuff;
        SWAP_AND_FORFEIT_ID = R.id.llSwapAndForfeit;
        MOVES_ID = R.id.bMoves;
        SWAP_ID = R.id.bSwap;
        BUFF_ID = R.id.bBuff;
        FORFEIT_ID = R.id.bForfeit;
        
        // Set up onClick listeners
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
            text.setText("Buff applied!");
        } else if(v.getId() == R.id.bForfeit){
            finish();
        } else if(v.getId() == X_ID){
            drawer.removeAllViews();
            config = getResources().getConfiguration();
            
            if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) restoreDrawerLayoutLandscape();
            else restoreDrawerLayoutPortrait();
        } else if(v.getId() == MOVE1_ID){
            // NOTHING YET
//            text.setText("Move 1");
            action(1);
        } else if(v.getId() == MOVE2_ID){
            // NOTHING YET
//            text.setText("Move 2");
            action(2);
        } else if(v.getId() == MOVE3_ID){
            // NOTHING YET
//            text.setText("Move 3");
            action(3);
        } else if(v.getId() == MOVE4_ID){
            // NOTHING YET
//            text.setText("Move 4");
            action(4);
        } else if(v.getId() == PKMN1_ID){
            // NOTHING YET
            text.setText("Pokemon 1");
        } else if(v.getId() == PKMN2_ID){
            // NOTHING YET
            text.setText("Pokemon 2");
        } else if(v.getId() == PKMN3_ID){
            // NOTHING YET
            text.setText("Pokemon 3");
        } else if(v.getId() == PKMN4_ID){
            // NOTHING YET
            text.setText("Pokemon 4");
        } else if(v.getId() == PKMN5_ID){
            // NOTHING YET
            text.setText("Pokemon 5");
        } else if(v.getId() == PKMN6_ID){
            // NOTHING YET
            text.setText("Pokemon 6");
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
        moves.setBackgroundResource(R.drawable.silver_bubble);
        LinearLayout.LayoutParams WRAP_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f);
        moves.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        moves.setPadding(0, 0, 0, 0);
        moves.setText(R.string.moves);
        moves.setTextColor(Color.argb(255, 47, 79, 79));
        moves.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(moves);
        
        swap = new Button(this);
        swap.setId(SWAP_ID);
        swap.setBackgroundResource(R.drawable.silver_bubble);
        swap.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        swap.setPadding(0, 0, 0, 0);
        swap.setText(R.string.swap);
        swap.setTextColor(Color.argb(255, 47, 79, 79));
        swap.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(swap);
        
        buff = new Button(this);
        buff.setId(BUFF_ID);
        buff.setBackgroundResource(R.drawable.silver_bubble);
        buff.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        buff.setPadding(0, 0, 0, 0);
        buff.setText(R.string.buff);
        buff.setTextColor(Color.argb(255, 47, 79, 79));
        buff.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(buff);
        
        forfeit = new Button(this);
        forfeit.setId(FORFEIT_ID);
        forfeit.setBackgroundResource(R.drawable.silver_bubble);
        forfeit.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        forfeit.setPadding(0, 0, 0, 0);
        forfeit.setText(R.string.forfeit);
        forfeit.setTextColor(Color.argb(255, 47, 79, 79));
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
        moves.setBackgroundResource(R.drawable.silver_bubble);
        LinearLayout.LayoutParams WRAP_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f);
        moves.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        moves.setPadding(0, 0, 0, 0);
        moves.setText(R.string.moves);
        moves.setTextColor(Color.argb(255, 47, 79, 79));
        moves.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesAndBuff.addView(moves);
        
        buff = new Button(this);
        buff.setId(BUFF_ID);
        buff.setBackgroundResource(R.drawable.silver_bubble);
        buff.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        buff.setPadding(0, 0, 0, 0);
        buff.setText(R.string.buff);
        buff.setTextColor(Color.argb(255, 47, 79, 79));
        buff.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesAndBuff.addView(buff);
        
        LinearLayout swapAndForfeit = new LinearLayout(this);
        swapAndForfeit.setId(SWAP_AND_FORFEIT_ID);
        swapAndForfeit.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        swapAndForfeit.setOrientation(LinearLayout.HORIZONTAL);
        swapAndForfeit.setWeightSum(2f);
        
        swap = new Button(this);
        swap.setId(SWAP_ID);
        swap.setBackgroundResource(R.drawable.silver_bubble);
        swap.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        swap.setPadding(0, 0, 0, 0);
        swap.setText(R.string.swap);
        swap.setTextColor(Color.argb(255, 47, 79, 79));
        swap.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        swapAndForfeit.addView(swap);
        
        forfeit = new Button(this);
        forfeit.setId(FORFEIT_ID);
        forfeit.setBackgroundResource(R.drawable.silver_bubble);
        forfeit.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        forfeit.setPadding(0, 0, 0, 0);
        forfeit.setText(R.string.forfeit);
        forfeit.setTextColor(Color.argb(255, 47, 79, 79));
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
    
    private void buildMovesLayoutLandscape(){        
        drawer.setWeightSum(10f);
        
        LinearLayout goBack = new LinearLayout(this);
        LinearLayout.LayoutParams FULL_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 2f);
        FULL_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        goBack.setLayoutParams(FULL_WRAP_WEIGHT_PARAMS);
        goBack.setOrientation(LinearLayout.VERTICAL);
        goBack.setWeightSum(1f);
        
        close = new Button(this);
        close.setId(X_ID);
        close.setBackgroundResource(R.drawable.silver_bubble);
        LinearLayout.LayoutParams WRAP_HARD_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 60, 1f);
        close.setLayoutParams(WRAP_HARD_WEIGHT_PARAMS);
        close.setPadding(0, 0, 0, 0);
        close.setText("X");
        close.setTextColor(Color.argb(255, 47, 79, 79));
        close.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        goBack.addView(close);
        
        LinearLayout movesRow = new LinearLayout(this);
        movesRow.setId(MOVES_ROW1_ID);
        LinearLayout.LayoutParams MATCH_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 8f);
        MATCH_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        movesRow.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        movesRow.setOrientation(LinearLayout.HORIZONTAL);
        movesRow.setWeightSum(4f);
        
        move1 = new Button(this);
        move1.setId(MOVE1_ID);
        move1.setBackgroundResource(R.drawable.silver_bubble);
        LinearLayout.LayoutParams WRAP_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f);
        move1.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        move1.setPadding(0, 0, 0, 0);
        move1.setText(R.string.move1);
        move1.setTextColor(Color.argb(255, 47, 79, 79));
        move1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(move1);
        
        move2 = new Button(this);
        move2.setId(MOVE2_ID);
        move2.setBackgroundResource(R.drawable.silver_bubble);
        move2.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        move2.setPadding(0, 0, 0, 0);
        move2.setText(R.string.move2);
        move2.setTextColor(Color.argb(255, 47, 79, 79));
        move2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(move2);
        
        move3 = new Button(this);
        move3.setId(MOVE3_ID);
        move3.setBackgroundResource(R.drawable.silver_bubble);
        move3.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        move3.setPadding(0, 0, 0, 0);
        move3.setText(R.string.move3);
        move3.setTextColor(Color.argb(255, 47, 79, 79));
        move3.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(move3);
        
        move4 = new Button(this);
        move4.setId(MOVE4_ID);
        move4.setBackgroundResource(R.drawable.silver_bubble);
        move4.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        move4.setPadding(0, 0, 0, 0);
        move4.setText(R.string.move4);
        move4.setTextColor(Color.argb(255, 47, 79, 79));
        move4.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow.addView(move4);
        
        // Add layouts
        drawer.addView(goBack);
        drawer.addView(movesRow);
        
        // Set up onClick listeners
        close.setOnClickListener(this);
        move1.setOnClickListener(this);
        move2.setOnClickListener(this);
        move3.setOnClickListener(this);
        move4.setOnClickListener(this);
    }

    private void buildMovesLayoutPortait(){
        drawer.setWeightSum(10f);
        
        LinearLayout goBack = new LinearLayout(this);
        LinearLayout.LayoutParams FULL_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
        FULL_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        goBack.setLayoutParams(FULL_WRAP_WEIGHT_PARAMS);
        goBack.setOrientation(LinearLayout.VERTICAL);
        goBack.setWeightSum(1f);
        
        close = new Button(this);
        close.setId(X_ID);
        close.setBackgroundResource(R.drawable.silver_bubble);
        LinearLayout.LayoutParams WRAP_HARD_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 50, 1f);
        close.setLayoutParams(WRAP_HARD_WEIGHT_PARAMS);
        close.setPadding(0, 0, 0, 0);
        close.setText("X");
        close.setTextColor(Color.argb(255, 47, 79, 79));
        close.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        goBack.addView(close);
        
        LinearLayout movesRow1 = new LinearLayout(this);
        movesRow1.setId(MOVES_ROW1_ID);
        LinearLayout.LayoutParams MATCH_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 4.5f);
        MATCH_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        movesRow1.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        movesRow1.setOrientation(LinearLayout.HORIZONTAL);
        movesRow1.setWeightSum(2f);
        
        move1 = new Button(this);
        move1.setId(MOVE1_ID);
        move1.setBackgroundResource(R.drawable.silver_bubble);
        LinearLayout.LayoutParams WRAP_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f);
        move1.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        move1.setPadding(0, 0, 0, 0);
        move1.setText(R.string.move1);
        move1.setTextColor(Color.argb(255, 47, 79, 79));
        move1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow1.addView(move1);
        
        move2 = new Button(this);
        move2.setId(MOVE2_ID);
        move2.setBackgroundResource(R.drawable.silver_bubble);
        move2.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        move2.setPadding(0, 0, 0, 0);
        move2.setText(R.string.move2);
        move2.setTextColor(Color.argb(255, 47, 79, 79));
        move2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow1.addView(move2);
        
        LinearLayout movesRow2 = new LinearLayout(this);
        movesRow2.setId(MOVES_ROW2_ID);
        movesRow2.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        movesRow2.setOrientation(LinearLayout.HORIZONTAL);
        movesRow2.setWeightSum(2f);
        
        move3 = new Button(this);
        move3.setId(MOVE3_ID);
        move3.setBackgroundResource(R.drawable.silver_bubble);
        move3.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        move3.setPadding(0, 0, 0, 0);
        move3.setText(R.string.move3);
        move3.setTextColor(Color.argb(255, 47, 79, 79));
        move3.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow2.addView(move3);
        
        move4 = new Button(this);
        move4.setId(MOVE4_ID);
        move4.setBackgroundResource(R.drawable.silver_bubble);
        move4.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        move4.setPadding(0, 0, 0, 0);
        move4.setText(R.string.move4);
        move4.setTextColor(Color.argb(255, 47, 79, 79));
        move4.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        movesRow2.addView(move4);
        
        // Add layouts
        drawer.addView(goBack);
        drawer.addView(movesRow1);
        drawer.addView(movesRow2);
        
        // Set up onClick listeners
        close.setOnClickListener(this);
        move1.setOnClickListener(this);
        move2.setOnClickListener(this);
        move3.setOnClickListener(this);
        move4.setOnClickListener(this);
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
        close.setBackgroundResource(R.drawable.silver_bubble);
        LinearLayout.LayoutParams WRAP_HARD_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 60, 1f);
        close.setLayoutParams(WRAP_HARD_WEIGHT_PARAMS);
        close.setPadding(0, 0, 0, 0);
        close.setText("X");
        close.setTextColor(Color.argb(255, 47, 79, 79));
        close.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        goBack.addView(close);
        
        LinearLayout teamRow = new LinearLayout(this);
        teamRow.setId(TEAM_ROW1_ID);
        LinearLayout.LayoutParams MATCH_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 8f);
        MATCH_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        teamRow.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        teamRow.setOrientation(LinearLayout.HORIZONTAL);
        teamRow.setWeightSum(6f);
        
        pkmn1 = new Button(this);
        pkmn1.setId(PKMN1_ID);
        pkmn1.setBackgroundResource(R.drawable.silver_bubble);
        LinearLayout.LayoutParams WRAP_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f);
        pkmn1.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        pkmn1.setPadding(0, 0, 0, 0);
        pkmn1.setText(R.string.pkmn1);
        pkmn1.setTextColor(Color.argb(255, 47, 79, 79));
        pkmn1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn1);
        
        pkmn2 = new Button(this);
        pkmn2.setId(PKMN2_ID);
        pkmn2.setBackgroundResource(R.drawable.silver_bubble);
        pkmn2.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        pkmn2.setPadding(0, 0, 0, 0);
        pkmn2.setText(R.string.pkmn2);
        pkmn2.setTextColor(Color.argb(255, 47, 79, 79));
        pkmn2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn2);
        
        pkmn3 = new Button(this);
        pkmn3.setId(PKMN3_ID);
        pkmn3.setBackgroundResource(R.drawable.silver_bubble);
        pkmn3.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        pkmn3.setPadding(0, 0, 0, 0);
        pkmn3.setText(R.string.pkmn3);
        pkmn3.setTextColor(Color.argb(255, 47, 79, 79));
        pkmn3.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn3);
        
        pkmn4 = new Button(this);
        pkmn4.setId(PKMN4_ID);
        pkmn4.setBackgroundResource(R.drawable.silver_bubble);
        pkmn4.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        pkmn4.setPadding(0, 0, 0, 0);
        pkmn4.setText(R.string.pkmn4);
        pkmn4.setTextColor(Color.argb(255, 47, 79, 79));
        pkmn4.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn4);
        
        pkmn5 = new Button(this);
        pkmn5.setId(PKMN5_ID);
        pkmn5.setBackgroundResource(R.drawable.silver_bubble);
        pkmn5.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        pkmn5.setPadding(0, 0, 0, 0);
        pkmn5.setText(R.string.pkmn5);
        pkmn5.setTextColor(Color.argb(255, 47, 79, 79));
        pkmn5.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow.addView(pkmn5);
        
        pkmn6 = new Button(this);
        pkmn6.setId(PKMN6_ID);
        pkmn6.setBackgroundResource(R.drawable.silver_bubble);
        pkmn6.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        pkmn6.setPadding(0, 0, 0, 0);
        pkmn6.setText(R.string.pkmn6);
        pkmn6.setTextColor(Color.argb(255, 47, 79, 79));
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
        close.setBackgroundResource(R.drawable.silver_bubble);
        LinearLayout.LayoutParams WRAP_HARD_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 50, 1f);
        close.setLayoutParams(WRAP_HARD_WEIGHT_PARAMS);
        close.setPadding(0, 0, 0, 0);
        close.setText("X");
        close.setTextColor(Color.argb(255, 47, 79, 79));
        close.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        goBack.addView(close);
        
        LinearLayout teamRow1 = new LinearLayout(this);
        teamRow1.setId(TEAM_ROW1_ID);
        LinearLayout.LayoutParams MATCH_WRAP_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 4.5f);
        MATCH_WRAP_WEIGHT_PARAMS.gravity = Gravity.CENTER;
        teamRow1.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        teamRow1.setOrientation(LinearLayout.HORIZONTAL);
        teamRow1.setWeightSum(3f);
        
        pkmn1 = new Button(this);
        pkmn1.setId(PKMN1_ID);
        pkmn1.setBackgroundResource(R.drawable.silver_bubble);
        LinearLayout.LayoutParams WRAP_MATCH_WEIGHT_PARAMS = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f);
        pkmn1.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        pkmn1.setPadding(0, 0, 0, 0);
        pkmn1.setText(R.string.pkmn1);
        pkmn1.setTextColor(Color.argb(255, 47, 79, 79));
        pkmn1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow1.addView(pkmn1);
        
        pkmn2 = new Button(this);
        pkmn2.setId(PKMN2_ID);
        pkmn2.setBackgroundResource(R.drawable.silver_bubble);
        pkmn2.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        pkmn2.setPadding(0, 0, 0, 0);
        pkmn2.setText(R.string.pkmn2);
        pkmn2.setTextColor(Color.argb(255, 47, 79, 79));
        pkmn2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow1.addView(pkmn2);
        
        pkmn3 = new Button(this);
        pkmn3.setId(PKMN3_ID);
        pkmn3.setBackgroundResource(R.drawable.silver_bubble);
        pkmn3.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        pkmn3.setPadding(0, 0, 0, 0);
        pkmn3.setText(R.string.pkmn3);
        pkmn3.setTextColor(Color.argb(255, 47, 79, 79));
        pkmn3.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow1.addView(pkmn3);
        
        LinearLayout teamRow2 = new LinearLayout(this);
        teamRow2.setId(TEAM_ROW2_ID);
        teamRow2.setLayoutParams(MATCH_WRAP_WEIGHT_PARAMS);
        teamRow2.setOrientation(LinearLayout.HORIZONTAL);
        teamRow2.setWeightSum(3f);
        
        pkmn4 = new Button(this);
        pkmn4.setId(PKMN4_ID);
        pkmn4.setBackgroundResource(R.drawable.silver_bubble);
        pkmn4.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        pkmn4.setPadding(0, 0, 0, 0);
        pkmn4.setText(R.string.pkmn4);
        pkmn4.setTextColor(Color.argb(255, 47, 79, 79));
        pkmn4.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow2.addView(pkmn4);
        
        pkmn5 = new Button(this);
        pkmn5.setId(PKMN5_ID);
        pkmn5.setBackgroundResource(R.drawable.silver_bubble);
        pkmn5.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        pkmn5.setPadding(0, 0, 0, 0);
        pkmn5.setText(R.string.pkmn5);
        pkmn5.setTextColor(Color.argb(255, 47, 79, 79));
        pkmn5.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        teamRow2.addView(pkmn5);
        
        pkmn6 = new Button(this);
        pkmn6.setId(PKMN6_ID);
        pkmn6.setBackgroundResource(R.drawable.silver_bubble);
        pkmn6.setLayoutParams(WRAP_MATCH_WEIGHT_PARAMS);
        pkmn6.setPadding(0, 0, 0, 0);
        pkmn6.setText(R.string.pkmn6);
        pkmn6.setTextColor(Color.argb(255, 47, 79, 79));
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
                            Animated.shift_x2 = (int)(x - event_x); // Shift user sprite

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
                        Animated.shift_x2 = 0; // Reset shift
                        delta_x = null;
                        delta_y = null;
                        error = true; // Invalid touch event
                    } // else not moving right-to-left, NEVER back-tracked left-to-right
                } // else treat as though it is still a held ACTION_DOWN event
            } else Animated.shift_x2 = 0; // Reset shift because ACTION_MOVE lasted too long
        } else if(event.getAction() == MotionEvent.ACTION_UP){
            touchClear = true;
            
            /* Classify the touchEvent */
            if(touched_opponent){ // Determine what type of attack
                if(held) text.setText("Special Attack");
                else text.setText("Physical Attack");
            } else if(touched_user){ // Determine what type of defense or Dodge
                if(held) text.setText("Hard defensive/healing move");
                else if(killHold) text.setText("Dodge attempt");
                else if(!error) text.setText("Soft defensive/healing move");
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
            Animated.shift_x2 = 0;            
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
    
    private void action(int move){
        if(move == 1){
            if(MOVE1 == null){
                try {
                    Set<String> tempSet = moveData.getStringSet(String.valueOf(gen.nextInt(164) + 1), null); // Retrieve HashSet & store in Set var
                    Iterator index = tempSet.iterator(); // Create an interator
                    MOVE1 = new String[tempSet.size()]; // Instantiate MOVE1 array
                    
                    String values; // temp var
                    while(index.hasNext()){ // Populate details into MOVE1 array
                        values = index.next().toString(); // Capture obj & convert to String-- iterator then moves to next
                        
                        if(values.length() > 2) MOVE1[Integer.parseInt(values.substring(0, 1))] = values.substring(2);
                        else MOVE1[Integer.parseInt(values.substring(0, 1))] = "";
                    }
                } catch(Exception cet){
                    text.setText(cet.toString());
                }
            }
            
            text.setText(MOVE1[0] + ", " + MOVE1[1] + ", " + MOVE1[2] + ", " + MOVE1[3] + ", " + MOVE1[4] + ", " + MOVE1[5] + ", " + MOVE1[6] + ", " + MOVE1[7]);
        } else if(move == 2) {
            if(MOVE2 == null){
                try {
                    Set<String> tempSet = moveData.getStringSet(String.valueOf(gen.nextInt(164) + 1), null); // Retrieve HashSet & store in Set var
                    Iterator index = tempSet.iterator(); // Create an interator
                    MOVE2 = new String[tempSet.size()]; // Instantiate MOVE2 array
                    
                    String values; // temp var
                    while(index.hasNext()){ // Populate details into MOVE2 array
                        values = index.next().toString(); // Capture obj & convert to String-- iterator then moves to next
                        
                        if(values.length() > 2) MOVE2[Integer.parseInt(values.substring(0, 1))] = values.substring(2);
                        else MOVE2[Integer.parseInt(values.substring(0, 1))] = "";
                    }
                } catch(Exception cet){
                    text.setText(cet.toString());
                }
            }
            
            text.setText(MOVE2[0] + ", " + MOVE2[1] + ", " + MOVE2[2] + ", " + MOVE2[3] + ", " + MOVE2[4] + ", " + MOVE2[5] + ", " + MOVE2[6] + ", " + MOVE2[7]);
        } else if(move == 3) {
            if(MOVE3 == null){
                try {
                    Set<String> tempSet = moveData.getStringSet(String.valueOf(gen.nextInt(164) + 1), null); // Retrieve HashSet & store in Set var
                    Iterator index = tempSet.iterator(); // Create an interator
                    MOVE3 = new String[tempSet.size()]; // Instantiate MOVE3 array
                    
                    String values; // temp var
                    while(index.hasNext()){ // Populate details into MOVE3 array
                        values = index.next().toString(); // Capture obj & convert to String-- iterator then moves to next
                        
                        if(values.length() > 2) MOVE3[Integer.parseInt(values.substring(0, 1))] = values.substring(2);
                        else MOVE3[Integer.parseInt(values.substring(0, 1))] = "";
                    }
                } catch(Exception cet){
                    text.setText(cet.toString());
                }
            }
            
            text.setText(MOVE3[0] + ", " + MOVE3[1] + ", " + MOVE3[2] + ", " + MOVE3[3] + ", " + MOVE3[4] + ", " + MOVE3[5] + ", " + MOVE3[6] + ", " + MOVE3[7]);
        } else if(move == 4) {
            if(MOVE4 == null){
                try {
                    Set<String> tempSet = moveData.getStringSet(String.valueOf(gen.nextInt(164) + 1), null); // Retrieve HashSet & store in Set var
                    Iterator index = tempSet.iterator(); // Create an interator
                    MOVE4 = new String[tempSet.size()]; // Instantiate MOVE4 array
                    
                    String values; // temp var
                    while(index.hasNext()){ // Populate details into MOVE4 array
                        values = index.next().toString(); // Capture obj & convert to String-- iterator then moves to next
                        
                        if(values.length() > 2) MOVE4[Integer.parseInt(values.substring(0, 1))] = values.substring(2);
                        else MOVE4[Integer.parseInt(values.substring(0, 1))] = "";
                    }
                } catch(Exception cet){
                    text.setText(cet.toString());
                }
            }
            
            text.setText(MOVE4[0] + ", " + MOVE4[1] + ", " + MOVE4[2] + ", " + MOVE4[3] + ", " + MOVE4[4] + ", " + MOVE4[5] + ", " + MOVE4[6] + ", " + MOVE4[7]);
        }
    }
}