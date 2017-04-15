package Game.Pokemon;

import android.content.Context;
import static android.content.Context.MODE_PRIVATE;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import java.io.InputStream;

public class Animated extends View {
    private AssetManager asset;
    private static Configuration config; 
    private InputStream stream;
    private Integer i, j;
    private Path draw;
    private Point leftPoint, rightPoint, tip;
    private String orientation, temp;
    private boolean nextFrame;
    private final double oneRadian = 0.0174533;
    private final Paint brush = new Paint();
    private float AVG_SPRITE_WIDTH, AVG_SPRITE_HEIGHT, textWidth;
    private int contender, time;
    private static float speedbar_start_y, speedbar_end_y, action_start_x;
    private final int FIXED_FRAME = (int)Round(Battle.ARENABOX/3f, 0), GAP = 5,
            POINTER_SIDE = 10, SPEED_TEXT_SIZE = 25, TEXTSIZE_SP_UNITS = 12;
    private static Options Opponent_Options, User_Options; 
    protected final static float USER_PLACEMENT_X = 1.0f, OPPONENT_PLACEMENT_X = 0.9f, 
            USER_PLACEMENT_Y = 0.9f, OPPONENT_PLACEMENT_Y = 1f;
    protected int LARGEST_HEIGHT, LARGEST_WIDTH;
    protected static Bitmap opponent, opponent_icon, user, user_icon, opponent_backSpace,
            opponent_frontSpace, user_frontSpace, user_backSpace;
    protected static boolean opponent_actReady = false, user_actReady = false, 
            USER_SPEED_LOCK = false, OPPONENT_SPEED_LOCK = false;
    protected static byte o_depth, u_depth;
    protected static double HEALTH_BAR_LENGTH, OPPONENT_FRAME_BOTTOMLEFT_X, 
            OPPONENT_FRAME_BOTTOMLEFT_Y, OPPONENT_FRAME_BOTTOMRIGHT_X, 
            OPPONENT_FRAME_BOTTOMRIGHT_Y, OPPONENT_FRAME_TOPLEFT_X, 
            OPPONENT_FRAME_TOPLEFT_Y, OPPONENT_FRAME_TOPRIGHT_X, 
            OPPONENT_FRAME_TOPRIGHT_Y, USER_FRAME_BOTTOMLEFT_X, 
            USER_FRAME_BOTTOMLEFT_Y, USER_FRAME_BOTTOMRIGHT_X, 
            USER_FRAME_BOTTOMRIGHT_Y, USER_FRAME_TOPLEFT_X, USER_FRAME_TOPLEFT_Y,
            USER_FRAME_TOPRIGHT_X, USER_FRAME_TOPRIGHT_Y,
            user_speed_inc = 0, opponent_speed_inc = 0,
            user_speedbar_percentage = 0, opponent_speedbar_percentage = 0;
    protected static String opponent_pokemon, user_pokemon;
    protected static float opponent_pokemon_HP = 1, opponent_current_HP, user_pokemon_HP, user_current_HP;
    protected static float scaleFactor, opponent_damage_percentage = 0, user_damage_percentage = 0, 
            opponent_speedbar, user_speedbar, commandEnd_startAct_x, speedbar_start_x, speedbar_end_x, AVG_SPEED;
    /* All _shifts_ should be ints because they reference a physical change in 
     * sprite positioning/orientation, whereby it cannot be defined by a fraction */
    protected static int user_shift_x = 0, opponent_shift_x = 0,
            user_shift_y = 0, opponent_shift_y = 0, user_closeHeight, opponent_closeHeight,
            user_closeWidth, opponent_closeWidth, user_farHeight, opponent_farHeight,
            user_farWidth, opponent_farWidth;
    protected static short opponent_pokemon_lvl = 0, user_pokemon_lvl;
    
    public Animated(Context context){
        super(context);
        initialize(context);
    }

    /* For the utilization of custom views, provide these following two case
     * constructors parameters 'Context, AttributeSet'. Also, give the 
     * constructor(s) public access. */
    public Animated(Context context, AttributeSet attribs){
        super(context, attribs);
        initialize(context);
    }

    public Animated(Context context, AttributeSet attribs, int defStyle){
        super(context, attribs, defStyle);
        initialize(context);
    }

    private void initialize(Context context){
        asset = context.getAssets(); //Link assets
        config = getResources().getConfiguration();
          
        /* Used to setup SharedPreferences, if it has not been set */
        SharedPreferences objects = context.getSharedPreferences("objects", MODE_PRIVATE);
        if(!objects.getBoolean("setState", false)){
            Settings load = new Settings(context);
            load.setOrient();
            objects = context.getSharedPreferences("objects", MODE_PRIVATE);
            //Battle.text.setText("Objects have not been analyzed");    
        } //else Battle.text.setText("Objects analyzed");
         
        LARGEST_WIDTH = objects.getInt("max_width", 0); //Defaults to 0 if not found
        LARGEST_HEIGHT = objects.getInt("max_height", 0); //Defaults to 0 if not found
        AVG_SPRITE_WIDTH = objects.getFloat("avg_width", 0); //Defaults to 0 if not found
        AVG_SPRITE_HEIGHT = objects.getFloat("avg_height", 0); //Defaults to 0 if not found
        
        float tempVar = (FIXED_FRAME/((AVG_SPRITE_WIDTH + AVG_SPRITE_HEIGHT)/2));
        scaleFactor = (tempVar > 1)? (float)Round(Math.sqrt(tempVar), 2) : tempVar;
        
        setPokemon(1, "bulbasaur");
        setPokemon(2, "bulbasaur");
        i = 0;
        j = 0;
        time = 0;
        nextFrame = true;
        HEALTH_BAR_LENGTH = (int)Round(LARGEST_WIDTH*1.4, 0);
        
        try {
            orientation = "front";
            contender = 1;
            stream = asset.open("sprites/" + opponent_pokemon + "/" + orientation + "/frame_" + i + ".png");
            Opponent_Options = new Options();
            decodeResizedBitmapFromAssets();
            
            orientation = "back";
            contender = 2;
            stream = asset.open("sprites/" + user_pokemon + "/" + orientation + "/frame_" + j + ".png");
            User_Options = new Options();
            decodeResizedBitmapFromAssets();
        } catch (Exception e) {      
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //Evaluate opponent hitbox
        OPPONENT_FRAME_BOTTOMLEFT_X = (Battle.SCREEN_WIDTH*5/6.0 - opponent.getWidth()/2.0)*OPPONENT_PLACEMENT_X + opponent_shift_x;
        OPPONENT_FRAME_BOTTOMLEFT_Y = (Battle.SCREEN_HEIGHT/3.0 + opponent.getHeight()/2.0)*OPPONENT_PLACEMENT_Y + opponent_shift_y;
        OPPONENT_FRAME_TOPLEFT_X = OPPONENT_FRAME_BOTTOMLEFT_X;
        OPPONENT_FRAME_TOPLEFT_Y = OPPONENT_FRAME_BOTTOMLEFT_Y - opponent.getHeight();
        OPPONENT_FRAME_TOPRIGHT_X = OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth();
        OPPONENT_FRAME_TOPRIGHT_Y = OPPONENT_FRAME_TOPLEFT_Y;
        OPPONENT_FRAME_BOTTOMRIGHT_X = OPPONENT_FRAME_TOPRIGHT_X;
        OPPONENT_FRAME_BOTTOMRIGHT_Y = OPPONENT_FRAME_BOTTOMLEFT_Y; 
        
        //Draw bitmap from TopLeft, downward
        if(opponent_backSpace != null) canvas.drawBitmap(opponent_backSpace, (float)(OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0 - opponent_backSpace.getWidth()/2.0), (float)(OPPONENT_FRAME_TOPLEFT_Y + opponent.getHeight() - opponent_backSpace.getHeight()), null);
        
        //Draw bitmap from TopLeft, downward
        canvas.drawBitmap(opponent, (float)OPPONENT_FRAME_TOPLEFT_X, (float)OPPONENT_FRAME_TOPLEFT_Y, null);
        
        //Draw bitmap from TopLeft, downward
        if(opponent_frontSpace != null) canvas.drawBitmap(opponent_frontSpace, (float)(OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0 - opponent_frontSpace.getWidth()/2.0), (float)(OPPONENT_FRAME_TOPLEFT_Y + opponent.getHeight() - opponent_frontSpace.getHeight()), null);
        
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(Color.argb(128, 255, 255, 255));
        brush.setStrokeWidth(10f);
        //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        canvas.drawRect((float)(OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0), 
                (float)((OPPONENT_FRAME_TOPLEFT_Y - 40)*adjust(opponent_pokemon)), 
                (float)(OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0 + HEALTH_BAR_LENGTH/2.0), 
                (float)((OPPONENT_FRAME_TOPRIGHT_Y - 30)*adjust(opponent_pokemon)), 
                brush);
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(getHealthBarColor((int)Round((1 - Math.min(opponent_damage_percentage, 1))*100, 0)));
        brush.setStrokeWidth(10f);
        //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        canvas.drawRect((float)(OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0), 
                (float)((OPPONENT_FRAME_TOPLEFT_Y - 40)*adjust(opponent_pokemon)), 
                (float)(OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0 + HEALTH_BAR_LENGTH/2.0 - (Math.min(opponent_damage_percentage, 1)*HEALTH_BAR_LENGTH)), 
                (float)((OPPONENT_FRAME_TOPRIGHT_Y - 30)*adjust(opponent_pokemon)), 
                brush);
        brush.setColor(Color.argb(255, 255, 255, 255));
        brush.setStyle(Paint.Style.FILL);
        brush.setTextSize(25f);
        brush.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC));        
        
        canvas.drawText("HP", 
                (float)(OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0 - 50/*px*/), 
                (float)((OPPONENT_FRAME_TOPLEFT_Y - 25/*px*/)*adjust(opponent_pokemon)), 
                brush);
        brush.setTextSize(TEXTSIZE_SP_UNITS*Battle.scaledDensity);
        
        /* Get opponent Pokemon name text */
        if(opponent_pokemon.equals("nidorang")) temp = "Nidoran♀";
        else if(opponent_pokemon.equals("nidoranb")) temp = "Nidoran♂";
        else if(opponent_pokemon.equals("mr_mime")) temp = "Mr. Mime";
        else temp = opponent_pokemon.substring(0, 1).toUpperCase() + opponent_pokemon.substring(1);
        
        try {
            if(!(temp.equals("Nidoran♀") || temp.equals("Nidoran♂")) && Battle.o_pokemon.gender != '\0') temp += " | " + Battle.o_pokemon.gender;
        } catch(NullPointerException n){}
        
        temp += " | LVL" + opponent_pokemon_lvl;

        //Draw opponent name text
        if(!(opponent_pokemon.equals("nidorang") || opponent_pokemon.equals("nidoranb")) && Battle.o_pokemon.gender != '\0'){
            canvas.drawText(temp,
                0, //Beginning of string
                opponent_pokemon.length() + 2, //end of opponent name + space + |
                (float)(OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0), 
                (float)((OPPONENT_FRAME_TOPLEFT_Y - 50/*px*/)*adjust(opponent_pokemon)),
                brush);
            //Capture width of text with its current configuration <- sequence significant
            textWidth = brush.measureText(opponent_pokemon.substring(0, 1).toUpperCase() + opponent_pokemon.substring(1) + " | " + Battle.o_pokemon.gender);
            canvas.drawText(temp,
                opponent_pokemon.length() + 5, //Latter end of string
                temp.length(), //end of string
                textWidth + (float)(OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0), 
                (float)((OPPONENT_FRAME_TOPLEFT_Y - 50/*px*/)*adjust(opponent_pokemon)),
                brush);
            //Capture width of text up to writing gender character with its current configuration <- sequence significant
            textWidth = brush.measureText(opponent_pokemon.substring(0, 1).toUpperCase() + opponent_pokemon.substring(1) + " | ");
            brush.setColor((Battle.o_pokemon.gender == '♀')? Color.argb(255, 255, 20, 147) : Color.argb(255, 0, 0, 255));
            brush.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)); //<-Configuration change by font
            canvas.drawText(Battle.o_pokemon.gender + "",               
                    textWidth + (float)(OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0), 
                    (float)((OPPONENT_FRAME_TOPLEFT_Y - 50/*px*/)*adjust(opponent_pokemon)),
                    brush);
            brush.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC)); //Reset font configuration
            brush.setColor(Color.argb(255, 255, 255, 255)); //Reset color
        } else {
            canvas.drawText(temp, 
                (float)(OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0), 
                (float)((OPPONENT_FRAME_TOPLEFT_Y - 50/*px*/)*adjust(opponent_pokemon)), 
                brush);
        }
        
        //Update opponent current HP state
        opponent_current_HP = opponent_pokemon_HP*(1 - Math.min(opponent_damage_percentage, 1));
        
        //Draw opponent HP number
        canvas.drawText(String.valueOf(Round((opponent_current_HP/opponent_pokemon_HP)*100/*percentage*/, 1)) + '%', 
                (float)(OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0), 
                (float)((OPPONENT_FRAME_TOPLEFT_Y - 5/*px*/)*adjust(opponent_pokemon)), 
                brush);
        
        //Evaluate user hitbox
        USER_FRAME_BOTTOMLEFT_X = (Battle.SCREEN_WIDTH/6.0 - user.getWidth()/2.0)*USER_PLACEMENT_X + user_shift_x;
        USER_FRAME_BOTTOMLEFT_Y = (Battle.SCREEN_HEIGHT*2/3.0 + user.getHeight()/2.0)*USER_PLACEMENT_Y + user_shift_y;
        USER_FRAME_TOPLEFT_X = USER_FRAME_BOTTOMLEFT_X;
        USER_FRAME_TOPLEFT_Y = USER_FRAME_BOTTOMLEFT_Y - user.getHeight();
        USER_FRAME_TOPRIGHT_X = USER_FRAME_TOPLEFT_X + user.getWidth();
        USER_FRAME_TOPRIGHT_Y = USER_FRAME_TOPLEFT_Y;
        USER_FRAME_BOTTOMRIGHT_X = USER_FRAME_TOPRIGHT_X;
        USER_FRAME_BOTTOMRIGHT_Y = USER_FRAME_BOTTOMLEFT_Y;
        
        //Draw bitmap from TopLeft, downward
        if(user_frontSpace != null) canvas.drawBitmap(user_frontSpace, (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - user_frontSpace.getWidth()/2.0), (float)(USER_FRAME_TOPLEFT_Y + user.getHeight() - user_frontSpace.getHeight()), null);
        
        //Draw bitmap from TopLeft, downward        
        canvas.drawBitmap(user, (float)USER_FRAME_TOPLEFT_X, (float)USER_FRAME_TOPLEFT_Y, null);
        
        //Draw bitmap from TopLeft, downward
        if(user_backSpace != null) canvas.drawBitmap(user_backSpace, (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - user_backSpace.getWidth()/2.0), (float)(USER_FRAME_TOPLEFT_Y + user.getHeight() - user_backSpace.getHeight()), null);
        
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(Color.argb(128, 255, 255, 255));
        brush.setStrokeWidth(10);
        //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        canvas.drawRect(((config.orientation == Configuration.ORIENTATION_PORTRAIT)?
                (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/4.0)
                : (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0)), 
                (float)((USER_FRAME_TOPLEFT_Y - 40)*adjust(user_pokemon)), 
                ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                        (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 + HEALTH_BAR_LENGTH*3/4.0)
                        : (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 + HEALTH_BAR_LENGTH/2.0)), 
                (float)((USER_FRAME_TOPRIGHT_Y - 30)*adjust(user_pokemon)), 
                brush);
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(getHealthBarColor((int)Round((1 - Math.min(user_damage_percentage, 1))*100, 0)));
        brush.setStrokeWidth(10);
        //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        canvas.drawRect(((config.orientation == Configuration.ORIENTATION_PORTRAIT)?
                (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/4.0)
                : (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0)), 
                (float)((USER_FRAME_TOPLEFT_Y - 40)*adjust(user_pokemon)), 
                ((config.orientation == Configuration.ORIENTATION_PORTRAIT)?
                        (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 + HEALTH_BAR_LENGTH*3/4.0 - (Math.min(user_damage_percentage, 1)*HEALTH_BAR_LENGTH))
                        : (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 + HEALTH_BAR_LENGTH/2.0 - (Math.min(user_damage_percentage, 1)*HEALTH_BAR_LENGTH))), 
                (float)((USER_FRAME_TOPRIGHT_Y - 30)*adjust(user_pokemon)), 
                brush);
        brush.setColor(Color.argb(255, 255, 255, 255));
        brush.setStyle(Paint.Style.FILL);
        brush.setTextSize(25);
        canvas.drawText("HP", ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/4.0 - 50/*px*/)
                : (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0 - 50/*px*/)), 
                (float)((USER_FRAME_TOPLEFT_Y - 25/*px*/)*adjust(user_pokemon)), 
                brush);
        brush.setTextSize(TEXTSIZE_SP_UNITS*Battle.scaledDensity);
        
        /* Get user Pokemon name text */
        if(user_pokemon.equals("nidorang")) temp = "Nidoran♀";
        else if(user_pokemon.equals("nidoranb")) temp = "Nidoran♂";
        else if(user_pokemon.equals("mr_mime")) temp = "Mr. Mime";
        else temp = user_pokemon.substring(0, 1).toUpperCase() + user_pokemon.substring(1);
        
        try {
            if(!(temp.equals("Nidoran♀") || temp.equals("Nidoran♂")) && Battle.u_pokemon.gender != '\0') temp += " | " + Battle.u_pokemon.gender;
        } catch(NullPointerException n){}
              
        temp += " | LVL" + user_pokemon_lvl;
        
        //Draw user name text        
        if(!(user_pokemon.equals("nidorang") || user_pokemon.equals("nidoranb")) && Battle.u_pokemon.gender != '\0'){
            canvas.drawText(temp,
                0, //Beginning of string
                user_pokemon.length() + 2, //end of user name + space + |
                ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? //x
                (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/4.0)
                : (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0)), 
                (float)((USER_FRAME_TOPLEFT_Y - 50/*px*/)*adjust(user_pokemon)), //y
                brush);
            //Capture width of text with its current configuration <- sequence significant
            textWidth = brush.measureText(user_pokemon.substring(0, 1).toUpperCase() + user_pokemon.substring(1) + " | " + Battle.u_pokemon.gender);
            canvas.drawText(temp,
                user_pokemon.length() + 5, //Latter end of string
                temp.length(), //end of string
                textWidth + ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? //x
                (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/4.0)
                : (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0)),
                (float)((USER_FRAME_TOPLEFT_Y - 50/*px*/)*adjust(user_pokemon)), //y
                brush);
            //Capture width of text up to writing gender character with its current configuration <- sequence significant
            textWidth = brush.measureText(user_pokemon.substring(0, 1).toUpperCase() + user_pokemon.substring(1) + " | ");
            brush.setColor((Battle.u_pokemon.gender == '♀')? Color.argb(255, 255, 20, 147) : Color.argb(255, 0, 0, 255));
            brush.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)); //<-Configuration change by font
            canvas.drawText(Battle.u_pokemon.gender + "",               
                    textWidth + ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? //Width of old config + x-location
                    (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/4.0)
                    : (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0)), 
                    (float)((USER_FRAME_TOPLEFT_Y - 50/*px*/)*adjust(user_pokemon)), //y
                    brush);
            brush.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC)); //Reset font configuration
            brush.setColor(Color.argb(255, 255, 255, 255)); //Reset color
        } else {
            canvas.drawText(temp,
                ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/4.0)
                : (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0)), 
                (float)((USER_FRAME_TOPLEFT_Y - 50/*px*/)*adjust(user_pokemon)),
                brush);
        }
        
        //Update opponent current HP state
        user_current_HP = user_pokemon_HP*(1 - Math.min(user_damage_percentage, 1));
        
        //Draw user HP numbers
        canvas.drawText(String.valueOf(Round(user_current_HP, 1)) + '/' + String.valueOf(user_pokemon_HP), 
                ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/4.0)
                : (float)(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0 - HEALTH_BAR_LENGTH/2.0)), 
                (float)((USER_FRAME_TOPLEFT_Y - 5/*px*/)*adjust(user_pokemon)), 
                brush);
        
        /** This block draws the speed meter **/
        /* Begin speed meter for opponent */
        speedbar_start_x = Battle.SCREEN_WIDTH*11/20f;
        speedbar_end_x = (config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                Battle.SCREEN_WIDTH*11/20f + LARGEST_WIDTH*2
                : Battle.SCREEN_WIDTH*11/20f + LARGEST_WIDTH*3.5f;
        speedbar_start_y = Battle.SCREEN_HEIGHT*11/20f;
        speedbar_end_y = Battle.SCREEN_HEIGHT*11/20f + 10;
        commandEnd_startAct_x = (config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                        (Battle.SCREEN_WIDTH*11/20f + LARGEST_WIDTH*2) - (LARGEST_WIDTH*2)*0.2f 
                        : (Battle.SCREEN_WIDTH*11/20f + LARGEST_WIDTH*3.5f) - (LARGEST_WIDTH*3.5f)*0.2f;
        action_start_x = commandEnd_startAct_x;
        //COMMAND portion of speed meter
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(Color.argb(128, 255, 255, 255));
        brush.setStrokeWidth(10f);
        //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        canvas.drawRect(speedbar_start_x, 
                speedbar_start_y, 
                commandEnd_startAct_x, 
                speedbar_end_y, 
                brush);
        //ACTION portion of speed meter
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(Color.argb(192, 255, 255, 0));
        brush.setStrokeWidth(10f);
        
        while(action_start_x < speedbar_end_x){ //Draws [][][][][][]...
            //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
            canvas.drawRect(action_start_x += GAP, 
                    speedbar_start_y, 
                    action_start_x += GAP, 
                    speedbar_end_y, 
                    brush);
        }
        
        //Reset action_start_x variable
        action_start_x = commandEnd_startAct_x;
        
        //COMMAND SPEED BAR (opponent)
        if(!OPPONENT_SPEED_LOCK) opponent_speedbar = (config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                        (float)((Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*2.0)*opponent_speedbar_percentage)
                        : (float)((Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*3.5)*opponent_speedbar_percentage);
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(Color.argb(128, 0, 0, 139));
        brush.setStrokeWidth(10f);
        //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        canvas.drawRect(speedbar_start_x, 
                speedbar_start_y, 
                Math.min(speedbar_start_x + opponent_speedbar, commandEnd_startAct_x),                        
                speedbar_end_y, 
                brush);
        
        if(speedbar_start_x + opponent_speedbar > commandEnd_startAct_x){
            brush.setStyle(Paint.Style.FILL);
            brush.setColor(Color.argb(255, 139, 0, 0));
            brush.setStrokeWidth(10f);
            //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        
            while(action_start_x < speedbar_start_x + opponent_speedbar && action_start_x < speedbar_end_x){ //Draws [][][][][][]...
                //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
                canvas.drawRect(action_start_x += GAP, 
                        speedbar_start_y, 
                        Math.min((action_start_x + GAP), (speedbar_start_x + opponent_speedbar)), 
                        speedbar_end_y, 
                        brush);
                
                action_start_x += GAP;
            }
        }
        
        try {
            if(speedbar_start_x + opponent_speedbar < commandEnd_startAct_x && !opponent_actReady){
                opponent_speed_inc += ((double)(Battle.o_pokemon.Spe*Battle.OPPONENT_SPE_STAGE)/(Battle.u_pokemon.Spe*Battle.USER_SPE_STAGE))*(AVG_SPEED/100f);
                
                if(((config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                        (float)((Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*2.0)*(opponent_speed_inc/AVG_SPEED))
                        : (float)((Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*3.5)*(opponent_speed_inc/AVG_SPEED)))                        
                        + speedbar_start_x < commandEnd_startAct_x)
                    opponent_speedbar_percentage = opponent_speed_inc/AVG_SPEED;
                else {
                    opponent_speedbar_percentage = (commandEnd_startAct_x - speedbar_start_x)/
                            ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                            (float)(Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*2.0)
                            : (float)(Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*3.5));
                    opponent_actReady = true;
                }
            } else {
                if(opponent_actReady && Battle.o_actChosen /*make distinct to opponent later*/){
                    opponent_speed_inc += ((double)(Battle.o_pokemon.Spe*Battle.OPPONENT_SPE_STAGE)/(Battle.u_pokemon.Spe*Battle.USER_SPE_STAGE))*(AVG_SPEED/100f);
                    opponent_speedbar_percentage = opponent_speed_inc/AVG_SPEED;
                } else {
                    opponent_speedbar_percentage = (commandEnd_startAct_x - speedbar_start_x)/
                            ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                            (float)(Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*2.0)
                            : (float)(Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*3.5));
                    opponent_actReady = true;
                }
            }
        } catch(NullPointerException n){}
        /* End speed meter for opponent */
        
        /* Opponent pointer & icon */
        brush.setStrokeWidth(4);
        brush.setColor(Color.argb(255, 135, 206, 235));
        brush.setStyle(Paint.Style.FILL_AND_STROKE);
        brush.setAntiAlias(true);

        tip = new Point((int)Round(Math.min(speedbar_start_x + opponent_speedbar, action_start_x), 0), (int)Round(speedbar_start_y - 10, 0));
        leftPoint = new Point((int)Round(tip.x - POINTER_SIDE*Math.sin(30*oneRadian), 0), (int)Round(tip.y - POINTER_SIDE*Math.cos(30*oneRadian), 0));
        rightPoint = new Point((int)Round(tip.x + POINTER_SIDE*Math.sin(30*oneRadian), 0), leftPoint.y);
        
        draw = new Path();
        draw.setFillType(FillType.EVEN_ODD);
        draw.moveTo(tip.x, tip.y);
        draw.lineTo(leftPoint.x, leftPoint.y);
        draw.lineTo(rightPoint.x, rightPoint.y);
        draw.close();

        canvas.drawPath(draw, brush);
        
        //Draw icon bitmap from TopLeft, downward
        canvas.drawBitmap(opponent_icon, 
                tip.x - opponent_icon.getWidth()/2f, 
                leftPoint.y - opponent_icon.getHeight() - 5, 
                null);
        /* End of pointer & icon */
        
        /* Beginning of speed meter textual info */
        brush.setColor(Color.argb(255, 255, 255, 255));
        brush.setStyle(Paint.Style.FILL);
        brush.setTextSize(SPEED_TEXT_SIZE);
        canvas.drawText("COMMAND > > >", 
                speedbar_start_x, 
                speedbar_start_y + SPEED_TEXT_SIZE + 10, 
                brush);
        canvas.drawText("ACTION", 
                commandEnd_startAct_x, 
                speedbar_start_y + SPEED_TEXT_SIZE + 10, 
                brush);
        /* End of speed meter textual info */
        
        /* Begin speed meter for user */
        speedbar_start_y += SPEED_TEXT_SIZE + 10 + 10;
        speedbar_end_y = speedbar_start_y + 10;
        action_start_x = commandEnd_startAct_x;
        //COMMAND portion of speed meter
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(Color.argb(128, 255, 255, 255));
        brush.setStrokeWidth(10f);
        //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        canvas.drawRect(speedbar_start_x, 
                speedbar_start_y, 
                commandEnd_startAct_x, 
                speedbar_end_y, 
                brush);
        //ACTION portion of speed meter
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(Color.argb(192, 255, 255, 0));
        brush.setStrokeWidth(10f);
        
        while(action_start_x < speedbar_end_x){ //Draws [][][][][][]...
            //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
            canvas.drawRect(action_start_x += GAP, 
                    speedbar_start_y, 
                    action_start_x += GAP, 
                    speedbar_end_y, 
                    brush);
        }
        
        //Reset action_start_x variable
        action_start_x = commandEnd_startAct_x;
        
        //COMMAND SPEED BAR (opponent)
        if(!USER_SPEED_LOCK) user_speedbar = (config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                (float)((Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*2.0)*user_speedbar_percentage)
                : (float)((Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*3.5)*user_speedbar_percentage);
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(Color.argb(128, 0, 0, 139));
        brush.setStrokeWidth(10f);
        //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        canvas.drawRect(speedbar_start_x, 
                speedbar_start_y, 
                Math.min(speedbar_start_x + user_speedbar, commandEnd_startAct_x),                        
                speedbar_end_y, 
                brush);
        
        if(speedbar_start_x + user_speedbar > commandEnd_startAct_x){
            brush.setStyle(Paint.Style.FILL);
            brush.setColor(Color.argb(255, 139, 0, 0));
            brush.setStrokeWidth(10f);
            //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        
            while(action_start_x < speedbar_start_x + user_speedbar && action_start_x < speedbar_end_x){ //Draws [][][][][][]...
                //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
                canvas.drawRect(action_start_x += GAP, 
                        speedbar_start_y, 
                        Math.min((action_start_x + GAP), (speedbar_start_x + user_speedbar)), 
                        speedbar_end_y, 
                        brush);
                
                action_start_x += GAP;
            }
        }
        
        try {
            if(speedbar_start_x + user_speedbar < commandEnd_startAct_x && !user_actReady){
                user_speed_inc += ((double)(Battle.u_pokemon.Spe*Battle.USER_SPE_STAGE)/(Battle.o_pokemon.Spe*Battle.OPPONENT_SPE_STAGE))*(AVG_SPEED/100f);
                
                if( ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                        (float)((Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*2.0)*(user_speed_inc/AVG_SPEED))
                        : (float)((Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*3.5)*(user_speed_inc/AVG_SPEED)))                        
                        + speedbar_start_x < commandEnd_startAct_x)
                    user_speedbar_percentage = user_speed_inc/AVG_SPEED;
                else {
                    user_speedbar_percentage = (commandEnd_startAct_x - speedbar_start_x)/
                            ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                            (Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*2.0)
                            : (Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*3.5));
                    
                    user_actReady = true;
                }
            } else {
                if(user_actReady && Battle.u_actChosen){
                    user_speed_inc += ((double)(Battle.u_pokemon.Spe*Battle.USER_SPE_STAGE)/(Battle.o_pokemon.Spe*Battle.OPPONENT_SPE_STAGE))*(AVG_SPEED/100f);
                    user_speedbar_percentage = user_speed_inc/AVG_SPEED;
                } else {
                    user_speedbar_percentage = (commandEnd_startAct_x - speedbar_start_x)/
                            ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                            (Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*2.0)
                            : (Battle.SCREEN_WIDTH*11/20.0 + LARGEST_WIDTH*3.5));
                    user_actReady = true;
                }
            }
//            Battle.text.setText(Battle.u_pokemon.name + "'s SPEED: " + Battle.u_pokemon.Spe + " | " + Battle.o_pokemon.name + "'s SPEED: " + Battle.o_pokemon.Spe +
//                    " | speedbar beginning: " + speedbar_start_x + " | speedbar end: " + speedbar_end_x +
//                    " | User_speedbar: " + user_speedbar + " | Opponent_speedbar: " + opponent_speedbar /*+
//                    " | User_speedbar_percentage: " + user_speedbar_percentage + " | Opponent_speedbar_percentage: " + opponent_speedbar_percentage*/);
        } catch(NullPointerException n){}
        /* End speed meter for user */
        
        /* User pointer & icon */
        brush.setStrokeWidth(4);
        brush.setColor(Color.argb(255, 135, 206, 235));
        brush.setStyle(Paint.Style.FILL_AND_STROKE);
        brush.setAntiAlias(true);

        tip = new Point((int)Round(Math.min(speedbar_start_x + user_speedbar, action_start_x), 0), (int)Round(speedbar_start_y + 20, 0));
        leftPoint = new Point((int)Round(tip.x - POINTER_SIDE*Math.sin(30*oneRadian), 0), (int)Round(tip.y + POINTER_SIDE*Math.cos(30*oneRadian), 0));
        rightPoint = new Point((int)Round(tip.x + POINTER_SIDE*Math.sin(30*oneRadian), 0), leftPoint.y);
        
        draw = new Path();
        draw.setFillType(FillType.EVEN_ODD);
        draw.moveTo(tip.x, tip.y);
        draw.lineTo(leftPoint.x, leftPoint.y);
        draw.lineTo(rightPoint.x, rightPoint.y);
        draw.close();

        canvas.drawPath(draw, brush);
        
        //Draw icon bitmap from TopLeft, downward
        canvas.drawBitmap(user_icon, 
                tip.x - user_icon.getWidth()/2f, 
                leftPoint.y + 5, 
                null);
        /* End of pointer & icon */
        /** End block that draws speed meter **/
        
        /* This block changes the frame of all sprites */
        if(nextFrame){
            orientation = "front";
            contender = 1;

            try {
                temp = "sprites/" + opponent_pokemon + "/" + orientation + "/frame_";
                i += 1;
                stream = asset.open(temp + i + ".png");
                decodeResizedBitmapFromAssets();
            } catch (Exception var30) {
                try {
                    i = 0;
                    stream = asset.open("sprites/" + opponent_pokemon + "/" + orientation + "/frame_" + i + ".png");
                    decodeResizedBitmapFromAssets();
                } catch (Exception var29) {
                    try {
                       i = 1;
                       stream = asset.open("sprites/" + opponent_pokemon + "/" + orientation + "/frame_" + i + ".png");
                       decodeResizedBitmapFromAssets();
                    } catch (Exception var28) {
                    }
                }
            }

            orientation = "back";
            contender = 2;

            try {
                temp = "sprites/" + user_pokemon + "/" + orientation + "/frame_";
                j += 1;
                stream = asset.open(temp + j + ".png");
                decodeResizedBitmapFromAssets();
            } catch (Exception var26) {
                try {
                    j = 0;
                    stream = asset.open("sprites/" + user_pokemon + "/" + orientation + "/frame_" + j + ".png");
                    decodeResizedBitmapFromAssets();
                } catch (Exception var25) {
                    try {
                        j = 1;
                        stream = asset.open("sprites/" + user_pokemon + "/" + orientation + "/frame_" + j + ".png");
                        decodeResizedBitmapFromAssets();
                    } catch (Exception var24) {
                        //Battle.text.setText("Cannot find user sprite!");
                    }
                }
            } finally {
//                nextFrame = false;
//                delay();
            }
        }

        invalidate(); //Loop
    }

    private void delay(){
        new Thread(){
            @Override
            public void run(){
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                } finally {
                    nextFrame = true;
                }
            }
        }.start();
    }
    
    private void decodeResizedBitmapFromAssets(){
        Bitmap unscaledBitmap;
        
        if(contender == 1){
            o_depth = ((OPPONENT_FRAME_BOTTOMLEFT_Y - OPPONENT_FRAME_TOPLEFT_Y)/2 + OPPONENT_FRAME_TOPLEFT_Y <= Battle.SCREEN_HEIGHT*11/24.0)? (byte)0 : (byte)1;
            unscaledBitmap = BitmapFactory.decodeStream(stream, null, Opponent_Options);
            opponent = Bitmap.createScaledBitmap(unscaledBitmap, (int)Round(Opponent_Options.outWidth*(scaleFactor + o_depth), 0), (int)Round(Opponent_Options.outHeight*(scaleFactor + o_depth), 0), true);
            
            try { // Set opponent icon for speed bar
                stream = asset.open("sprites/" + opponent_pokemon + "/front/frame_0.png");
                unscaledBitmap = BitmapFactory.decodeStream(stream, null, Opponent_Options);
                opponent_icon = Bitmap.createScaledBitmap(unscaledBitmap, (int)Round(Opponent_Options.outWidth/2f, 0), (int)Round(Opponent_Options.outHeight/2f, 0), true);
            } catch(Exception e1){
                try {
                    stream = asset.open("sprites/" + opponent_pokemon + "/front/frame_1.png");
                    unscaledBitmap = BitmapFactory.decodeStream(stream, null, Opponent_Options);
                    opponent_icon = Bitmap.createScaledBitmap(unscaledBitmap, (int)Round(Opponent_Options.outWidth/2f, 0), (int)Round(Opponent_Options.outHeight/2f, 0), true);
                } catch(Exception e2) {
                    
                }   
            } finally {
                if(o_depth == 0){
                    opponent_farHeight = opponent.getHeight();
                    opponent_farWidth = opponent.getWidth();
                } else {
                    opponent_closeHeight = opponent.getHeight();
                    opponent_closeWidth = opponent.getWidth();
                }
            }           
        } else {
            u_depth = ((USER_FRAME_BOTTOMLEFT_Y - USER_FRAME_TOPLEFT_Y)/2 + USER_FRAME_TOPLEFT_Y <= Battle.SCREEN_HEIGHT*13/24.0)? (byte)0 : (byte)1;
            unscaledBitmap = BitmapFactory.decodeStream(stream, null, User_Options);
            user = Bitmap.createScaledBitmap(unscaledBitmap, (int)Round(User_Options.outWidth*(scaleFactor + u_depth), 0), (int)Round(User_Options.outHeight*(scaleFactor + u_depth), 0), true);
            
            try { // Set user icon for speed bar
                stream = asset.open("sprites/" + user_pokemon + "/front/frame_0.png");
                unscaledBitmap = BitmapFactory.decodeStream(stream, null, User_Options);
                user_icon = Bitmap.createScaledBitmap(unscaledBitmap, (int)Round(User_Options.outWidth/2f, 0), (int)Round(User_Options.outHeight/2f, 0), true);
            } catch(Exception e){
                try {
                    stream = asset.open("sprites/" + user_pokemon + "/front/frame_1.png");
                    unscaledBitmap = BitmapFactory.decodeStream(stream, null, User_Options);
                    user_icon = Bitmap.createScaledBitmap(unscaledBitmap, (int)Round(User_Options.outWidth/2f, 0), (int)Round(User_Options.outHeight/2f, 0), true);
                } catch(Exception e2) {
                    
                }    
            } finally {
                if(u_depth == 0){
                    user_farHeight = user.getHeight();
                    user_farWidth = user.getWidth();
                } else {
                    user_closeHeight = user.getHeight();
                    user_closeWidth = user.getWidth();
                }
            }    
        }
    }

    protected final void setPokemon(int contender, String name){
        if(contender == 1){
            if(name.charAt(name.length() - 1) == '♀') opponent_pokemon = "nidorang";
            else if(name.charAt(name.length() - 1) == '♂') opponent_pokemon = "nidoranb";
            else opponent_pokemon = name;
        } else if(contender == 2){
            if(name.charAt(name.length() - 1) == '♀') user_pokemon = "nidorang";
            else if(name.charAt(name.length() - 1) == '♂') user_pokemon = "nidoranb";
            else user_pokemon = name;
        }
    }

    private int getHealthBarColor(int percentage){
        if(percentage == 100) return Color.argb(255, 0, 128, 0);
        if(percentage > 75 && percentage < 100) return Color.argb(255, 34, 139, 34);
        if(percentage > 50 && percentage <= 75) return Color.argb(255, 50, 205, 50);
        if(percentage > 38 && percentage <= 50) return Color.argb(255, 173, 255, 47);
        if(percentage > 25 && percentage <= 38) return Color.argb(255, 255, 255, 0);        
        if(percentage > 17 && percentage <= 25) return Color.argb(255, 255, 69, 0);        
        if(percentage > 10 && percentage <= 17) return Color.argb(255, 255, 0, 0);        
        if(percentage <= 0 || percentage > 10) return Color.argb(255, 255, 255, 255);        
        return Color.argb(255, 139, 0, 0);
    }
    
    protected static double Round(double number, int placeAfterDecimal){        
        double doa = Math.pow(10, placeAfterDecimal); //degree of accuracy
        
        if(((number*doa) - (int)(number*doa)) >= .5) return ((int)(number*doa) + 1)/doa;
        else {
            if(number < 0) return ((int)(number*doa) - 1)/doa;
            else return ((int)(number*doa))/doa;
        }
    }
    
    private float adjust(String pkmn) {
        if(pkmn.equals("charizard") ||
            pkmn.equals("golbat") ||
            pkmn.equals("articuno") ||
            pkmn.equals("moltres")
        ) return 1.33f;
        else if(pkmn.equals("pidgeot") ||
            pkmn.equals("fearow") ||
            pkmn.equals("raichu") ||
            pkmn.equals("onix") ||
            pkmn.equals("aerodactyl") ||
            pkmn.equals("zapdos")
        ) return 1.25f;
        
        return 1;
    }
}