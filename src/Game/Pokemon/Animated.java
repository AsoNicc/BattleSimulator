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
import android.util.AttributeSet;
import android.view.View;
import java.io.InputStream;

public class Animated extends View {
    protected static int HEALTH_BAR_LENGTH, OPPONENT_FRAME_BOTTOMLEFT_X, 
            OPPONENT_FRAME_BOTTOMLEFT_Y, OPPONENT_FRAME_BOTTOMRIGHT_X, 
            OPPONENT_FRAME_BOTTOMRIGHT_Y, OPPONENT_FRAME_TOPLEFT_X, 
            OPPONENT_FRAME_TOPLEFT_Y, OPPONENT_FRAME_TOPRIGHT_X, 
            OPPONENT_FRAME_TOPRIGHT_Y, USER_FRAME_BOTTOMLEFT_X, 
            USER_FRAME_BOTTOMLEFT_Y, USER_FRAME_BOTTOMRIGHT_X, 
            USER_FRAME_BOTTOMRIGHT_Y, USER_FRAME_TOPLEFT_X, USER_FRAME_TOPLEFT_Y,
            USER_FRAME_TOPRIGHT_X, USER_FRAME_TOPRIGHT_Y,
            user_shift_x = 0, opponent_shift_x = 0, 
            user_shift_y = 0, opponent_shift_y = 0;
    protected static String pokemon;
    protected static float user_speed_inc = 6f/*:= 1sec :. make 6f a final float later*/, opponent_speed_inc = 1f,
            user_speed_percentage = 0f, opponent_speed_percentage = 0f;
    protected int LARGEST_HEIGHT, LARGEST_WIDTH;
    protected static short pokemon_HP, pokemon_lvl;
    private final float USER_PLACEMENT_X = 1.0f, OPPONENT_PLACEMENT_X = 0.9f, 
            USER_PLACEMENT_Y = 0.9f, OPPONENT_PLACEMENT_Y = 1f;
    private AssetManager asset;
    private static Configuration config; 
    private Point leftPoint, rightPoint, tip;
    private final Paint brush = new Paint();
    private Path draw;
    private float AVG_SPRITE_WIDTH, AVG_SPRITE_HEIGHT;
    private static float scaleFactor, commandEnd_startAct_x, speedbar_start_y,
            speedbar_end_x, speedbar_end_y, speedbar_start_x, action_start_x,
            opponent_speed, user_speed;
    private int contender, time;
    private final int FIXED_FRAME = Round(Battle.ARENABOX/3f), GAP = 5,
            POINTER_SIDE = 10, SPEED_TEXT_SIZE = 25;
    private boolean nextFrame;
    private static Bitmap opponent, opponent_icon, user, user_icon;
    private Integer i, j;
    private static Options Opponent_Options, User_Options;
    private String orientation, temp;
    private InputStream stream;
    
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
        scaleFactor = (tempVar > 1)? Round(Math.sqrt(tempVar)) : tempVar;
        
        setPokemon("bulbasaur");
        i = 0;
        j = 0;
        time = 0;
        nextFrame = true;
        HEALTH_BAR_LENGTH = Round(LARGEST_WIDTH*1.4);
        
        try {
            orientation = "front";
            contender = 1;
            stream = asset.open("sprites/" + pokemon + "/" + orientation + "/frame_" + i + ".png");
            Opponent_Options = new Options();
            decodeResizedBitmapFromAssets();
            
            orientation = "back";
            contender = 2;
            stream = asset.open("sprites/" + pokemon + "/" + orientation + "/frame_" + j + ".png");
            User_Options = new Options();
            decodeResizedBitmapFromAssets();
        } catch (Exception e) {      
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        
        //Evaluate opponent hitbox
        OPPONENT_FRAME_BOTTOMLEFT_X = Round( ((canvas.getWidth()*5/6f) - (opponent.getWidth()/2f))*OPPONENT_PLACEMENT_X ) + opponent_shift_x;
        OPPONENT_FRAME_BOTTOMLEFT_Y = Round( ((canvas.getHeight()/3f) + (opponent.getHeight()/2f))*OPPONENT_PLACEMENT_Y ) + opponent_shift_y;
        OPPONENT_FRAME_TOPLEFT_X = OPPONENT_FRAME_BOTTOMLEFT_X;
        OPPONENT_FRAME_TOPLEFT_Y = OPPONENT_FRAME_BOTTOMLEFT_Y - opponent.getHeight();
        OPPONENT_FRAME_TOPRIGHT_X = OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth();
        OPPONENT_FRAME_TOPRIGHT_Y = OPPONENT_FRAME_TOPLEFT_Y;
        OPPONENT_FRAME_BOTTOMRIGHT_X = OPPONENT_FRAME_TOPRIGHT_X;
        OPPONENT_FRAME_BOTTOMRIGHT_Y = OPPONENT_FRAME_BOTTOMLEFT_Y;
        
        //Draw bitmap from TopLeft, downward
        canvas.drawBitmap(opponent, OPPONENT_FRAME_TOPLEFT_X, OPPONENT_FRAME_TOPLEFT_Y, null);
        
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(Color.argb(128, 255, 255, 255));
        brush.setStrokeWidth(10f);
        //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        canvas.drawRect(Round( OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2f - HEALTH_BAR_LENGTH/2f ), 
                Round( (OPPONENT_FRAME_TOPLEFT_Y - 40)*adjust(pokemon) ), 
                Round( OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2f + HEALTH_BAR_LENGTH/2f ), 
                Round( (OPPONENT_FRAME_TOPRIGHT_Y - 30)*adjust(pokemon) ), 
                brush);
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(getHealthBarColor(50));
        brush.setStrokeWidth(10f);
        //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        canvas.drawRect(Round( OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2f - HEALTH_BAR_LENGTH/2f ), 
                Round( (OPPONENT_FRAME_TOPLEFT_Y - 40)*adjust(pokemon) ), 
                Round( OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2f + HEALTH_BAR_LENGTH/2f - (0.5f*HEALTH_BAR_LENGTH) ), 
                Round( (OPPONENT_FRAME_TOPRIGHT_Y - 30)*adjust(pokemon) ), 
                brush);
        brush.setColor(Color.argb(255, 255, 255, 255));
        brush.setStyle(Paint.Style.FILL);
        brush.setTextSize(25f);
        canvas.drawText("HP", 
                Round( OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2f - HEALTH_BAR_LENGTH/2f - 50/*px*/ ), 
                Round( (OPPONENT_FRAME_TOPLEFT_Y - 25/*px*/)*adjust(pokemon) ), 
                brush);
        brush.setTextSize(35);
        
        /* Get opponent Pokemon name text */
        if(pokemon.equals("nidorang")) temp = "Nidoran♀";
        else if(pokemon.equals("nidoranb")) temp = "Nidoran♂";
        else temp = pokemon.substring(0, 1).toUpperCase() + pokemon.substring(1); // + ' ' + genderString
        
        temp += " | LVL" + pokemon_lvl;

        //Draw opponent name text
        canvas.drawText(temp, 
                Round( OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2f - HEALTH_BAR_LENGTH/2f ), 
                Round( (OPPONENT_FRAME_TOPLEFT_Y - 50/*px*/)*adjust(pokemon) ), 
                brush);
        
        //Draw opponent HP number
        canvas.drawText(String.valueOf(Round( ((pokemon_HP/2f)/pokemon_HP)*100 /*damage percentage*/ )) + '%', 
                Round( OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2f - HEALTH_BAR_LENGTH/2f ), 
                Round( (OPPONENT_FRAME_TOPLEFT_Y - 5/*px*/)*adjust(pokemon) ), 
                brush);
        
        //Evaluate user hitbox
        USER_FRAME_BOTTOMLEFT_X = Round( ((canvas.getWidth()/6f) - (user.getWidth()/2f))*USER_PLACEMENT_X ) - user_shift_x;
        USER_FRAME_BOTTOMLEFT_Y = Round( ((canvas.getHeight()*2/3f) + (user.getHeight()/2f))*USER_PLACEMENT_Y) + user_shift_y;
        USER_FRAME_TOPLEFT_X = USER_FRAME_BOTTOMLEFT_X;
        USER_FRAME_TOPLEFT_Y = USER_FRAME_BOTTOMLEFT_Y - user.getHeight();
        USER_FRAME_TOPRIGHT_X = USER_FRAME_TOPLEFT_X + user.getWidth();
        USER_FRAME_TOPRIGHT_Y = USER_FRAME_TOPLEFT_Y;
        USER_FRAME_BOTTOMRIGHT_X = USER_FRAME_TOPRIGHT_X;
        USER_FRAME_BOTTOMRIGHT_Y = USER_FRAME_BOTTOMLEFT_Y;
                
        //Draw bitmap from TopLeft, downward        
        canvas.drawBitmap(user, USER_FRAME_TOPLEFT_X, USER_FRAME_TOPLEFT_Y, null);
        
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(Color.argb(128, 255, 255, 255));
        brush.setStrokeWidth(10);
        //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        canvas.drawRect(((config.orientation == Configuration.ORIENTATION_PORTRAIT)?
                Round( USER_FRAME_TOPLEFT_X + user.getWidth()/2f - HEALTH_BAR_LENGTH/4f )
                : Round( USER_FRAME_TOPLEFT_X + user.getWidth()/2f - HEALTH_BAR_LENGTH/2f) ), 
                Round( (USER_FRAME_TOPLEFT_Y - 40)*adjust(pokemon) ), 
                ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                        Round( USER_FRAME_TOPLEFT_X + user.getWidth()/2f + HEALTH_BAR_LENGTH*3/4f )
                        : Round( USER_FRAME_TOPLEFT_X + user.getWidth()/2f + HEALTH_BAR_LENGTH/2f) ), 
                Round( (USER_FRAME_TOPRIGHT_Y - 30)*adjust(pokemon) ), 
                brush);
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(getHealthBarColor(75));
        brush.setStrokeWidth(10);
        //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        canvas.drawRect(((config.orientation == Configuration.ORIENTATION_PORTRAIT)?
                Round( USER_FRAME_TOPLEFT_X + user.getWidth()/2f - HEALTH_BAR_LENGTH/4f )
                : Round( USER_FRAME_TOPLEFT_X + user.getWidth()/2f - HEALTH_BAR_LENGTH/2f) ), 
                Round( (USER_FRAME_TOPLEFT_Y - 40)*adjust(pokemon) ), 
                ((config.orientation == Configuration.ORIENTATION_PORTRAIT)?
                        Round( USER_FRAME_TOPLEFT_X + user.getWidth()/2f + HEALTH_BAR_LENGTH*3/4f - (0.25f*HEALTH_BAR_LENGTH) )
                        : Round( USER_FRAME_TOPLEFT_X + user.getWidth()/2f + HEALTH_BAR_LENGTH/2f - (0.25f*HEALTH_BAR_LENGTH)) ), 
                Round( (USER_FRAME_TOPRIGHT_Y - 30)*adjust(pokemon) ), 
                brush);
        brush.setColor(Color.argb(255, 255, 255, 255));
        brush.setStyle(Paint.Style.FILL);
        brush.setTextSize(25);
        canvas.drawText("HP", ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                Round( USER_FRAME_TOPLEFT_X + user.getWidth()/2f - HEALTH_BAR_LENGTH/4f - 50/*px*/ )
                : Round( USER_FRAME_TOPLEFT_X + user.getWidth()/2f - HEALTH_BAR_LENGTH/2f - 50/*px*/) ), 
                Round( (USER_FRAME_TOPLEFT_Y - 25/*px*/)*adjust(pokemon) ), 
                brush);
        brush.setTextSize(35);
        
        /* Get user Pokemon name text */
        if(pokemon.equals("nidorang")) temp = "Nidoran♀";
        else if(pokemon.equals("nidoranb")) temp = "Nidoran♂";
        else temp = pokemon.substring(0, 1).toUpperCase() + pokemon.substring(1);
        
        temp += " | LVL???";
        
        //Draw user name text
        canvas.drawText(temp, ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                Round( USER_FRAME_TOPLEFT_X + user.getWidth()/2f - HEALTH_BAR_LENGTH/4f )
                : Round( USER_FRAME_TOPLEFT_X + user.getWidth()/2f - HEALTH_BAR_LENGTH/2f) ), 
                Round( (USER_FRAME_TOPLEFT_Y - 50/*px*/)*adjust(pokemon) ), 
                brush);
        
        //Draw user HP numbers
        canvas.drawText("???/???", ((config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                Round( USER_FRAME_TOPLEFT_X + user.getWidth()/2f - HEALTH_BAR_LENGTH/4f )
                : Round( USER_FRAME_TOPLEFT_X + user.getWidth()/2f - HEALTH_BAR_LENGTH/2f) ), 
                Round( (USER_FRAME_TOPLEFT_Y - 5/*px*/)*adjust(pokemon) ), 
                brush);
        
        /** This block draws the speed meter **/
        /* Begin speed meter for opponent */
        speedbar_start_x = Round( canvas.getWidth()*11/20f );
        speedbar_end_x = (config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                Round( (canvas.getWidth()*11/20f + LARGEST_WIDTH*2) )
                : Round( (canvas.getWidth()*11/20f + LARGEST_WIDTH*3.5f) );
        speedbar_start_y = Round( canvas.getHeight()*11/20f );
        speedbar_end_y = Round( canvas.getHeight()*11/20f + 10 );
        commandEnd_startAct_x = (config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                        Round( (canvas.getWidth()*11/20f + LARGEST_WIDTH*2) - (LARGEST_WIDTH*2)*0.2 ) 
                        : Round( (canvas.getWidth()*11/20f + LARGEST_WIDTH*3.5f) - (LARGEST_WIDTH*3.5f)*0.2 );
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
        opponent_speed = (config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                        Round( canvas.getWidth()*11/20f + LARGEST_WIDTH*2 )*(opponent_speed_percentage/100)
                        : Round( canvas.getWidth()*11/20f + LARGEST_WIDTH*3.5f )*(opponent_speed_percentage/100);
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(Color.argb(128, 0, 0, 139));
        brush.setStrokeWidth(10f);
        //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        canvas.drawRect(speedbar_start_x, 
                speedbar_start_y, 
                Math.min( speedbar_start_x + opponent_speed, commandEnd_startAct_x ),                        
                speedbar_end_y, 
                brush);
        
        if(speedbar_start_x + opponent_speed > commandEnd_startAct_x){
            brush.setStyle(Paint.Style.FILL);
            brush.setColor(Color.argb(255, 139, 0, 0));
            brush.setStrokeWidth(10f);
            //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        
            while(action_start_x < speedbar_start_x + opponent_speed && action_start_x < speedbar_end_x){ //Draws [][][][][][]...
                //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
                canvas.drawRect(action_start_x += GAP, 
                        speedbar_start_y, 
                        Math.min( (action_start_x + GAP), (speedbar_start_x + opponent_speed) ), 
                        speedbar_end_y, 
                        brush);
                
                action_start_x += GAP;
            }
        }
        
        if(opponent_speed_percentage < 100) opponent_speed_percentage = Math.min(opponent_speed_percentage + opponent_speed_inc, 100);
        /* End speed meter for opponent */
        
        /* Opponent pointer & icon */
        brush.setStrokeWidth(4);
        brush.setColor(Color.argb(255, 135, 206, 235));
        brush.setStyle(Paint.Style.FILL_AND_STROKE);
        brush.setAntiAlias(true);

        tip = new Point(Round( Math.min(speedbar_start_x + opponent_speed, action_start_x) ), Round( speedbar_start_y - 10));
        leftPoint = new Point(Round( tip.x - POINTER_SIDE*Math.sin((30/*degrees*/*Math.PI)/180/*radians*/) ), Round(tip.y - POINTER_SIDE*Math.cos((30/*degrees*/*Math.PI)/180/*radians*/)));
        rightPoint = new Point(Round( tip.x + POINTER_SIDE*Math.sin((30/*degrees*/*Math.PI)/180/*radians*/) ), leftPoint.y);
        
        draw = new Path();
        draw.setFillType(FillType.EVEN_ODD);
        draw.moveTo(tip.x, tip.y);
        draw.lineTo(leftPoint.x, leftPoint.y);
        draw.lineTo(rightPoint.x, rightPoint.y);
        draw.close();

        canvas.drawPath(draw, brush);
        
        //Draw icon bitmap from TopLeft, downward
        canvas.drawBitmap(opponent_icon, 
                Round( tip.x - opponent_icon.getWidth()/2f ), 
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
        canvas.drawText("ACTION", (config.orientation == Configuration.ORIENTATION_PORTRAIT)?
                        commandEnd_startAct_x
                        : commandEnd_startAct_x, 
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
        user_speed = (config.orientation == Configuration.ORIENTATION_PORTRAIT)? 
                        ( canvas.getWidth()*11/20f + LARGEST_WIDTH*2 )*(user_speed_percentage/100)
                        : ( canvas.getWidth()*11/20f + LARGEST_WIDTH*3.5f )*(user_speed_percentage/100);
        brush.setStyle(Paint.Style.FILL);
        brush.setColor(Color.argb(128, 0, 0, 139));
        brush.setStrokeWidth(10f);
        //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        canvas.drawRect(speedbar_start_x, 
                speedbar_start_y, 
                Math.min( speedbar_start_x + user_speed, commandEnd_startAct_x ),                        
                speedbar_end_y, 
                brush);
        
        if(speedbar_start_x + user_speed > commandEnd_startAct_x){
            brush.setStyle(Paint.Style.FILL);
            brush.setColor(Color.argb(255, 139, 0, 0));
            brush.setStrokeWidth(10f);
            //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
        
            while(action_start_x < speedbar_start_x + user_speed && action_start_x < speedbar_end_x){ //Draws [][][][][][]...
                //canvas.drawRect(left (x-val), top (y-val), right (x-val), bottom (y-val), Paint)
                canvas.drawRect(action_start_x += GAP, 
                        speedbar_start_y, 
                        Math.min( (action_start_x + GAP), (speedbar_start_x + user_speed) ), 
                        speedbar_end_y, 
                        brush);
                
                action_start_x += GAP;
            }
        }
        
        if(user_speed_percentage < 100) user_speed_percentage = Math.min(user_speed_percentage + user_speed_inc, 100);
        /* End speed meter for user */
        
        /* User pointer & icon */
        brush.setStrokeWidth(4);
        brush.setColor(Color.argb(255, 135, 206, 235));
        brush.setStyle(Paint.Style.FILL_AND_STROKE);
        brush.setAntiAlias(true);

        tip = new Point(Round( Math.min(speedbar_start_x + user_speed, action_start_x) ), Round( speedbar_start_y + 20));
        leftPoint = new Point(Round( tip.x - POINTER_SIDE*Math.sin((30/*degrees*/*Math.PI)/180/*radians*/) ), Round(tip.y + POINTER_SIDE*Math.cos((30/*degrees*/*Math.PI)/180/*radians*/)));
        rightPoint = new Point(Round( tip.x + POINTER_SIDE*Math.sin((30/*degrees*/*Math.PI)/180/*radians*/) ), leftPoint.y);
        
        draw = new Path();
        draw.setFillType(FillType.EVEN_ODD);
        draw.moveTo(tip.x, tip.y);
        draw.lineTo(leftPoint.x, leftPoint.y);
        draw.lineTo(rightPoint.x, rightPoint.y);
        draw.close();

        canvas.drawPath(draw, brush);
        
        //Draw icon bitmap from TopLeft, downward
        canvas.drawBitmap(user_icon, 
                Round( tip.x - user_icon.getWidth()/2f ), 
                leftPoint.y + 5, 
                null);
        /* End of pointer & icon */
        /** End block that draws speed meter **/
        
        /* This block changes the frame of all sprites */
        if(nextFrame){
            orientation = "front";
            contender = 1;

            try {
                temp = "sprites/" + pokemon + "/" + orientation + "/frame_";
                i += 1;
                stream = asset.open(temp + i + ".png");
                decodeResizedBitmapFromAssets();
            } catch (Exception var30) {
                try {
                    i = 0;
                    stream = asset.open("sprites/" + pokemon + "/" + orientation + "/frame_" + i + ".png");
                    decodeResizedBitmapFromAssets();
                } catch (Exception var29) {
                    try {
                       i = 1;
                       stream = asset.open("sprites/" + pokemon + "/" + orientation + "/frame_" + i + ".png");
                       decodeResizedBitmapFromAssets();
                    } catch (Exception var28) {
                        //Battle.text.setText("Cannot find opponent sprite!");
                    }
                }
            }

            orientation = "back";
            contender = 2;

            try {
                temp = "sprites/" + pokemon + "/" + orientation + "/frame_";
                j += 1;
                stream = asset.open(temp + j + ".png");
                decodeResizedBitmapFromAssets();
            } catch (Exception var26) {
                try {
                    j = 0;
                    stream = asset.open("sprites/" + pokemon + "/" + orientation + "/frame_" + j + ".png");
                    decodeResizedBitmapFromAssets();
                } catch (Exception var25) {
                    try {
                        j = 1;
                        stream = asset.open("sprites/" + pokemon + "/" + orientation + "/frame_" + j + ".png");
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
            unscaledBitmap = BitmapFactory.decodeStream(stream, null, Opponent_Options);
            opponent = Bitmap.createScaledBitmap(unscaledBitmap, Round(Opponent_Options.outWidth*scaleFactor), Round(Opponent_Options.outHeight*scaleFactor), true);
            
            try {
                stream = asset.open("sprites/" + /*opponent*/pokemon + "/front/frame_0.png");
                unscaledBitmap = BitmapFactory.decodeStream(stream, null, Opponent_Options);
                opponent_icon = Bitmap.createScaledBitmap(unscaledBitmap, Round(Opponent_Options.outWidth/2f), Round(Opponent_Options.outHeight/2f), true);
            } catch(Exception e1){
                try {
                    stream = asset.open("sprites/" + /*opponent*/pokemon + "/front/frame_1.png");
                    unscaledBitmap = BitmapFactory.decodeStream(stream, null, Opponent_Options);
                    opponent_icon = Bitmap.createScaledBitmap(unscaledBitmap, Round(Opponent_Options.outWidth/2f), Round(Opponent_Options.outHeight/2f), true);
                } catch(Exception e2) {
                    
                }   
            }            
        } else {        
            unscaledBitmap = BitmapFactory.decodeStream(stream, null, User_Options);
            user = Bitmap.createScaledBitmap(unscaledBitmap, Round(User_Options.outWidth*(scaleFactor + 1)), Round(User_Options.outHeight*(scaleFactor + 1)), true);
            
            try {
                stream = asset.open("sprites/" + /*user*/pokemon + "/front/frame_0.png");
                unscaledBitmap = BitmapFactory.decodeStream(stream, null, Opponent_Options);
                user_icon = Bitmap.createScaledBitmap(unscaledBitmap, Round(User_Options.outWidth/2f), Round(User_Options.outHeight/2f), true);
            } catch(Exception e){
                try {
                    stream = asset.open("sprites/" + /*user*/pokemon + "/front/frame_1.png");
                    unscaledBitmap = BitmapFactory.decodeStream(stream, null, Opponent_Options);
                    user_icon = Bitmap.createScaledBitmap(unscaledBitmap, Round(User_Options.outWidth/2f), Round(User_Options.outHeight/2f), true);
                } catch(Exception e2) {
                    
                }    
            }
        }
    }

    protected final void setPokemon(String name){
        if(name.charAt(name.length() - 1) == '♀') pokemon = "nidorang";
        else if(name.charAt(name.length() - 1) == '♂') pokemon = "nidoranb";
        else pokemon = name;
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
    
    private int Round(double num){
        if(num/((int)num) >= .5 ) return (int)Math.ceil(num);
        else return (int)Math.floor(num);
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