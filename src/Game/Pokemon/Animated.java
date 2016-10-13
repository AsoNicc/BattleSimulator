package Game.Pokemon;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.io.InputStream;

public class Animated extends View {
    protected static float HEALTH_BAR_SIZE, OPPONENT_FRAME_BOTTOMLEFT_X, 
            OPPONENT_FRAME_BOTTOMLEFT_Y, OPPONENT_FRAME_BOTTOMRIGHT_X, 
            OPPONENT_FRAME_BOTTOMRIGHT_Y, OPPONENT_FRAME_TOPLEFT_X, 
            OPPONENT_FRAME_TOPLEFT_Y, OPPONENT_FRAME_TOPRIGHT_X, 
            OPPONENT_FRAME_TOPRIGHT_Y, USER_FRAME_BOTTOMLEFT_X, 
            USER_FRAME_BOTTOMLEFT_Y, USER_FRAME_BOTTOMRIGHT_X, 
            USER_FRAME_BOTTOMRIGHT_Y, USER_FRAME_TOPLEFT_X, USER_FRAME_TOPLEFT_Y,
            USER_FRAME_TOPRIGHT_X, USER_FRAME_TOPRIGHT_Y;
    protected static String pokemon;
    protected static int shift_x = 0, shift_x2 = 0, shift_y = 0, shift_y2 = 0;
    /*protected int LARGEST_HEIGHT = 0, LARGEST_WIDTH = 0;/*/protected int LARGEST_HEIGHT = 181, LARGEST_WIDTH = 217; //*/
    private final float PLACEMENT_X = 1.25f, PLACEMENT_X2 = 0.5f, 
            PLACEMENT_Y = 1.0f, PLACEMENT_Y2 = 1.0f;
    private AssetManager asset;
    private Battle battle;
    private final Paint brush = new Paint();
//    private final int FIXED_FRAME = Battle.ARENABOX;
    private int contender, height, time, width, x, y;
    private boolean draw;
    private String[] frames, files, folders;
    private Bitmap opponent, user;
    private Integer i, j;
    private Options Opponent_Options, User_Options;
    private String orientation, temp, Largest_Width_Details, Largest_Height_Details;
    private InputStream stream;
    private float AVG_WIDTH, AVG_HEIGHT;

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
        asset = context.getAssets();
        setLargest();
        
        /* Init. default vars */
        setPokemon("bulbasaur");
        i = 0;
        j = 0;
        time = 15;
        draw = true;
        HEALTH_BAR_SIZE = LARGEST_WIDTH*1.5f;
        
        try {
            orientation = "front";
            contender = 1;
            stream = asset.open("sprites/" + pokemon + "/" + orientation + "/frame_" + i + ".png");
            decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
            
            orientation = "back";
            contender = 2;
            stream = asset.open("sprites/" + pokemon + "/" + orientation + "/frame_" + j + ".png");
            decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
        } catch (Exception e) {      
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        
        OPPONENT_FRAME_TOPLEFT_X = 1.25f*(2*canvas.getWidth()/3.0f - User_Options.outWidth/2.0f) + shift_x;
        OPPONENT_FRAME_TOPRIGHT_X = OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth();
        OPPONENT_FRAME_TOPLEFT_Y = 1.0f*(9*canvas.getHeight()/20.0f + LARGEST_HEIGHT/2.0f - User_Options.outHeight);
        OPPONENT_FRAME_TOPRIGHT_Y = OPPONENT_FRAME_TOPLEFT_Y;
        OPPONENT_FRAME_BOTTOMLEFT_X = OPPONENT_FRAME_TOPLEFT_X;
        OPPONENT_FRAME_BOTTOMRIGHT_X = OPPONENT_FRAME_TOPRIGHT_X;
        OPPONENT_FRAME_BOTTOMLEFT_Y = OPPONENT_FRAME_TOPLEFT_Y + (float)opponent.getHeight();
        OPPONENT_FRAME_BOTTOMRIGHT_Y = OPPONENT_FRAME_BOTTOMLEFT_Y;
        
        canvas.drawBitmap(opponent, OPPONENT_FRAME_TOPLEFT_X, OPPONENT_FRAME_TOPLEFT_Y, null);
        brush.setStyle(Paint.Style.STROKE);
        brush.setColor(Color.argb(128, 255, 255, 255));
        brush.setStrokeWidth(10.0f);
        canvas.drawRect(OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f, 
                OPPONENT_FRAME_TOPLEFT_Y - 40.0f, 
                OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0f + HEALTH_BAR_SIZE/2.0f, 
                OPPONENT_FRAME_TOPRIGHT_Y - 30.0f, 
                brush);
        brush.setStyle(Paint.Style.STROKE);
        brush.setColor(getHealthBarColor(50));
        brush.setStrokeWidth(10.0f);
        canvas.drawRect(OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f, 
                OPPONENT_FRAME_TOPLEFT_Y - 40.0f, 
                OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0f + HEALTH_BAR_SIZE/2.0F - 0.5f*HEALTH_BAR_SIZE, 
                OPPONENT_FRAME_TOPRIGHT_Y - 30.0f, 
                brush);
        brush.setColor(Color.argb(255, 255, 255, 255));
        brush.setStyle(Paint.Style.FILL);
        brush.setTextSize(25.0f);
        canvas.drawText("HP", OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f - 50.0f, OPPONENT_FRAME_TOPLEFT_Y - 25.0f, brush);
        brush.setTextSize(35.0f);
        
        if(pokemon.equals("nidorang")) temp = "Nidoran♀";
        else if(pokemon.equals("nidoranb")) temp = "Nidoran♂";
        else temp = pokemon.substring(0, 1).toUpperCase() + pokemon.substring(1) + "\tLVL???";

        canvas.drawText(temp, OPPONENT_FRAME_TOPLEFT_X + opponent.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f, OPPONENT_FRAME_TOPLEFT_Y - 50.0f, brush);
        
        USER_FRAME_TOPLEFT_X = 0.5f*(canvas.getWidth()/3.0f - Opponent_Options.outWidth/2.0f) - shift_x2;
        USER_FRAME_TOPRIGHT_X = USER_FRAME_TOPLEFT_X + user.getWidth();
        USER_FRAME_TOPLEFT_Y = 1.0f*(13*canvas.getHeight()/20.0f + LARGEST_HEIGHT/2.0f - Opponent_Options.outHeight);
        USER_FRAME_TOPRIGHT_Y = USER_FRAME_TOPLEFT_Y;
        USER_FRAME_BOTTOMLEFT_X = USER_FRAME_TOPLEFT_X;
        USER_FRAME_BOTTOMRIGHT_X = USER_FRAME_TOPRIGHT_X;
        USER_FRAME_BOTTOMLEFT_Y = USER_FRAME_TOPLEFT_Y + user.getHeight();
        USER_FRAME_BOTTOMRIGHT_Y = USER_FRAME_BOTTOMLEFT_Y;
        
        canvas.drawBitmap(user, USER_FRAME_TOPLEFT_X, USER_FRAME_TOPLEFT_Y, null);
        
        brush.setStyle(Paint.Style.STROKE);
        brush.setColor(Color.argb(128, 255, 255, 255));
        brush.setStrokeWidth(10.0f);
        canvas.drawRect(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f, 
                USER_FRAME_TOPLEFT_Y - 40.0f, 
                USER_FRAME_TOPLEFT_X + user.getWidth()/2.0f + HEALTH_BAR_SIZE/2.0f, 
                USER_FRAME_TOPRIGHT_Y - 30.0f, 
                brush);
        brush.setStyle(Paint.Style.STROKE);
        brush.setColor(getHealthBarColor(75));
        brush.setStrokeWidth(10.0f);
        canvas.drawRect(USER_FRAME_TOPLEFT_X + user.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f, 
                USER_FRAME_TOPLEFT_Y - 40.0f, 
                USER_FRAME_TOPLEFT_X + user.getWidth()/2.0f + HEALTH_BAR_SIZE/2.0f - 0.25f*HEALTH_BAR_SIZE, 
                USER_FRAME_TOPRIGHT_Y - 30.0f, 
                brush);
        brush.setColor(Color.argb(255, 255, 255, 255));
        brush.setStyle(Paint.Style.FILL);
        brush.setTextSize(25.0f);
        canvas.drawText("HP", USER_FRAME_TOPLEFT_X + user.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f - 50.0f, USER_FRAME_TOPLEFT_Y - 25.0f, brush);
        brush.setTextSize(35.0f);
        
        if(pokemon.equals("nidorang")) temp = "Nidoran♀";
        else if(pokemon.equals("nidoranb")) temp = "Nidoran♂";
        else temp = pokemon.substring(0, 1).toUpperCase() + pokemon.substring(1) + "\tLVL???";
        
        canvas.drawText(temp, USER_FRAME_TOPLEFT_X + user.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f, USER_FRAME_TOPLEFT_Y - 50.0f, brush);
        
        if(draw){
            orientation = "front";
            contender = 1;

            try {
                temp = "sprites/" + pokemon + "/" + orientation + "/frame_";
                i += 1;
                stream = asset.open(temp + i + ".png");
                decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
            } catch (Exception var30) {
                try {
                    i = 0;
                    stream = asset.open("sprites/" + pokemon + "/" + orientation + "/frame_" + i + ".png");
                    decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
                } catch (Exception var29) {
                    try {
                       i = 1;
                       stream = asset.open("sprites/" + pokemon + "/" + orientation + "/frame_" + i + ".png");
                       decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
                    } catch (Exception var28) {
                        Battle.text.setText("Cannot find opponent sprite!");
                    }
                }
            }

            orientation = "back";
            contender = 2;

            try {
                temp = "sprites/" + pokemon + "/" + orientation + "/frame_";
                j += 1;
                stream = asset.open(temp + j + ".png");
                decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
            } catch (Exception var26) {
                try {
                    j = 0;
                    stream = asset.open("sprites/" + pokemon + "/" + orientation + "/frame_" + j + ".png");
                    decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
                } catch (Exception var25) {
                    try {
                        j = 1;
                        stream = asset.open("sprites/" + pokemon + "/" + orientation + "/frame_" + j + ".png");
                        decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
                    } catch (Exception var24) {
                        Battle.text.setText("Cannot find user sprite!");
                    }
                }
            } finally {
                draw = false;
                delay();
            }
        }

        invalidate();
    }

    public Bitmap overlay(Bitmap base, Bitmap bitmap, int layer){
        Bitmap bmOverlay = Bitmap.createBitmap(base.getWidth(), base.getHeight(), base.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(base, 0.0f, 0.0f, null);
        canvas.drawBitmap(bitmap, placement(layer), null);
        return bmOverlay;
    }

    public Matrix placement(int layer){
        Matrix matrix = new Matrix();
        if (layer == 1) matrix.setTranslate((width*2/3.0f - User_Options.outWidth/2.0f)*1.25f, ((height/4.0f + LARGEST_HEIGHT/2.0f) -  User_Options.outHeight)*1.0f);
        else if (layer == 2) matrix.setTranslate((width/3.0f - Opponent_Options.outWidth/2.0f)*0.5f, (((height*2/3.0f) +  LARGEST_HEIGHT/2.0f) - Opponent_Options.outHeight)*1.0f);
        
        return matrix;
    }

    private void delay(){
        new Thread(){
            @Override
            public void run(){
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                } finally {
                    draw = true;
                }
            }
        }.start();
    }

    private void calculateDecSampleSize(int reqWidth, int reqHeight){
        int decSampleSize = 1;
        if(Opponent_Options.outHeight > reqHeight || Opponent_Options.outWidth > reqWidth){
            y = Opponent_Options.outHeight/2;
            x = Opponent_Options.outWidth/2;
            
            while(y/decSampleSize >= reqHeight && x/decSampleSize >= reqWidth) decSampleSize *= 2;            
        }
        Opponent_Options.inSampleSize = decSampleSize;
    }

    private void calculateIncSampleSize(int reqWidth, int reqHeight){
        int incSampleSize = 1;
        if(Opponent_Options.outHeight < reqHeight || Opponent_Options.outWidth < reqWidth){
            y = Opponent_Options.outHeight*2;
            x = Opponent_Options.outWidth*2;
            
            while(y*incSampleSize < reqHeight && x*incSampleSize < reqWidth) incSampleSize *= 2;            
        }
        Opponent_Options.inDensity = 1;
        Opponent_Options.inTargetDensity = 4;
        Opponent_Options.inScaled = true;
    }

    private void decodeResizedBitmapFromAssets(int reqWidth, int reqHeight){
        if(contender == 1){
            Opponent_Options = new Options();

            opponent = BitmapFactory.decodeStream(stream, null, Opponent_Options);
        } else {        
            User_Options = new Options();

            user = BitmapFactory.decodeStream(stream, null, User_Options);
        }
    }

    /* Method used for designing the UI, arranging elements throughout */
    private void setLargest(){
        Options options;
        int sum_width = 0, sum_height = 0, count = 0;
        
        try {
            folders = asset.list("sprites");
            
            for(String folder : folders){
                files = asset.list("sprites/" + folder);
                
                for(String fileFolder : files){
                    frames = asset.list("sprites/" + folder + "/" + fileFolder);
                    
                    for(String file : frames){
                        options = new Options();
                        opponent = BitmapFactory.decodeStream(asset.open("sprites/" + folder + "/" + fileFolder + "/" + file), null, options);
                        count++;
                        
                        sum_width += options.outWidth;
                        if(options.outWidth > LARGEST_WIDTH){
                            LARGEST_WIDTH = options.outWidth;
                            Largest_Width_Details = "sprites/" + folder + "/" + fileFolder + "/" + file + " " + String.valueOf(LARGEST_WIDTH);
                        }
                        
                        sum_height += options.outHeight;
                        if(options.outHeight > LARGEST_HEIGHT){
                            LARGEST_HEIGHT = options.outHeight;
                            Largest_Height_Details = "sprites/" + folder + "/" + fileFolder + "/" + file + " " + String.valueOf(LARGEST_HEIGHT);
                        }
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            AVG_WIDTH = ((float)sum_width)/count;
            AVG_HEIGHT = ((float)sum_height)/count;
            Battle.text.setText("Avg. width = " + AVG_WIDTH + " | Avg. height = " + AVG_HEIGHT);
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
}