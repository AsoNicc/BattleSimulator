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
    protected int LARGEST_HEIGHT = 181, LARGEST_WIDTH = 217;
    private final float PLACEMENT_X = 1.25f, PLACEMENT_X2 = 0.5f, 
            PLACEMENT_Y = 1.0f, PLACEMENT_Y2 = 1.0f;
    private AssetManager assets;
    private Battle battle;
    private final Paint brush = new Paint();
    private int contender, height, time, width, x, y;
    private boolean draw;
    private String[] files, folders;
    private Bitmap frame, frame2;
    private Integer i, j;
    private Options options, options2;
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
        assets = context.getAssets();
        setPokemon("bulbasaur");
        i = 0;
        j = 0;
        time = 10;
        draw = true;
        HEALTH_BAR_SIZE = LARGEST_WIDTH*1.5f;
        
        try {
            orientation = "front";
            contender = 1;
            stream = assets.open("sprites/" + pokemon + "/" + orientation + "/frame_" + i.toString() + ".png");
            decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
            
            orientation = "back";
            contender = 2;
            stream = assets.open("sprites/" + pokemon + "/" + orientation + "/frame_" + j.toString() + ".png");
            decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
        } catch (Exception e) {
            try {
                orientation = "front";
                contender = 1;
                stream = assets.open("sprites/" + pokemon + "/" + orientation + "/frame_" + i.toString() + ".png");
                decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
                
                orientation = "back";
                contender = 2;
                stream = assets.open("sprites/" + pokemon + "/" + orientation + "/frame_" + j.toString() + ".png");
                decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
            } catch (Exception e2) {
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        OPPONENT_FRAME_TOPLEFT_X = 1.25f*(2*canvas.getWidth()/3.0f - options2.outWidth/2.0f) + shift_x;
        OPPONENT_FRAME_TOPRIGHT_X = OPPONENT_FRAME_TOPLEFT_X + frame.getWidth();
        OPPONENT_FRAME_TOPLEFT_Y = 1.0f*(9*canvas.getHeight()/20.0f + LARGEST_HEIGHT/2.0f - options2.outHeight);
        OPPONENT_FRAME_TOPRIGHT_Y = OPPONENT_FRAME_TOPLEFT_Y;
        OPPONENT_FRAME_BOTTOMLEFT_X = OPPONENT_FRAME_TOPLEFT_X;
        OPPONENT_FRAME_BOTTOMRIGHT_X = OPPONENT_FRAME_TOPRIGHT_X;
        OPPONENT_FRAME_BOTTOMLEFT_Y = OPPONENT_FRAME_TOPLEFT_Y + (float)frame.getHeight();
        OPPONENT_FRAME_BOTTOMRIGHT_Y = OPPONENT_FRAME_BOTTOMLEFT_Y;
        
        canvas.drawBitmap(frame, OPPONENT_FRAME_TOPLEFT_X, OPPONENT_FRAME_TOPLEFT_Y, null);
        brush.setStyle(Paint.Style.STROKE);
        brush.setColor(Color.argb(128, 255, 255, 255));
        brush.setStrokeWidth(10.0f);
        canvas.drawRect(OPPONENT_FRAME_TOPLEFT_X + frame.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f, 
                OPPONENT_FRAME_TOPLEFT_Y - 40.0f, 
                OPPONENT_FRAME_TOPLEFT_X + frame.getWidth()/2.0f + HEALTH_BAR_SIZE/2.0f, 
                OPPONENT_FRAME_TOPRIGHT_Y - 30.0f, 
                brush);
        brush.setStyle(Paint.Style.STROKE);
        brush.setColor(getHealthBarColor(50));
        brush.setStrokeWidth(10.0f);
        canvas.drawRect(OPPONENT_FRAME_TOPLEFT_X + frame.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f, 
                OPPONENT_FRAME_TOPLEFT_Y - 40.0f, 
                OPPONENT_FRAME_TOPLEFT_X + frame.getWidth()/2.0f + HEALTH_BAR_SIZE/2.0F - 0.5f*HEALTH_BAR_SIZE, 
                OPPONENT_FRAME_TOPRIGHT_Y - 30.0f, 
                brush);
        brush.setColor(Color.argb(255, 255, 255, 255));
        brush.setStyle(Paint.Style.FILL);
        brush.setTextSize(25.0f);
        canvas.drawText("HP", OPPONENT_FRAME_TOPLEFT_X + frame.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f - 50.0f, OPPONENT_FRAME_TOPLEFT_Y - 25.0f, brush);
        brush.setTextSize(35.0f);
        
        if(pokemon.equals("nidorang")) temp = "Nidoran♀";
        else if(pokemon.equals("nidoranb")) temp = "Nidoran♂";
        else temp = pokemon.substring(0, 1).toUpperCase() + pokemon.substring(1) + "\tLVL???";

        canvas.drawText(temp, OPPONENT_FRAME_TOPLEFT_X + frame.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f, OPPONENT_FRAME_TOPLEFT_Y - 50.0f, brush);
        
        USER_FRAME_TOPLEFT_X = 0.5f*(canvas.getWidth()/3.0f - options.outWidth/2.0f) - shift_x2;
        USER_FRAME_TOPRIGHT_X = USER_FRAME_TOPLEFT_X + frame2.getWidth();
        USER_FRAME_TOPLEFT_Y = 1.0f*(13*canvas.getHeight()/20.0f + LARGEST_HEIGHT/2.0f - options.outHeight);
        USER_FRAME_TOPRIGHT_Y = USER_FRAME_TOPLEFT_Y;
        USER_FRAME_BOTTOMLEFT_X = USER_FRAME_TOPLEFT_X;
        USER_FRAME_BOTTOMRIGHT_X = USER_FRAME_TOPRIGHT_X;
        USER_FRAME_BOTTOMLEFT_Y = USER_FRAME_TOPLEFT_Y + frame2.getHeight();
        USER_FRAME_BOTTOMRIGHT_Y = USER_FRAME_BOTTOMLEFT_Y;
        
        canvas.drawBitmap(frame2, USER_FRAME_TOPLEFT_X, USER_FRAME_TOPLEFT_Y, null);
        
        brush.setStyle(Paint.Style.STROKE);
        brush.setColor(Color.argb(128, 255, 255, 255));
        brush.setStrokeWidth(10.0f);
        canvas.drawRect(USER_FRAME_TOPLEFT_X + frame2.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f, 
                USER_FRAME_TOPLEFT_Y - 40.0f, 
                USER_FRAME_TOPLEFT_X + frame2.getWidth()/2.0f + HEALTH_BAR_SIZE/2.0f, 
                USER_FRAME_TOPRIGHT_Y - 30.0f, 
                brush);
        brush.setStyle(Paint.Style.STROKE);
        brush.setColor(getHealthBarColor(75));
        brush.setStrokeWidth(10.0f);
        canvas.drawRect(USER_FRAME_TOPLEFT_X + frame2.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f, 
                USER_FRAME_TOPLEFT_Y - 40.0f, 
                USER_FRAME_TOPLEFT_X + frame2.getWidth()/2.0f + HEALTH_BAR_SIZE/2.0f - 0.25f*HEALTH_BAR_SIZE, 
                USER_FRAME_TOPRIGHT_Y - 30.0f, 
                brush);
        brush.setColor(Color.argb(255, 255, 255, 255));
        brush.setStyle(Paint.Style.FILL);
        brush.setTextSize(25.0f);
        canvas.drawText("HP", USER_FRAME_TOPLEFT_X + frame2.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f - 50.0f, USER_FRAME_TOPLEFT_Y - 25.0f, brush);
        brush.setTextSize(35.0f);
        
        if(pokemon.equals("nidorang")) temp = "Nidoran♀";
        else if(pokemon.equals("nidoranb")) temp = "Nidoran♂";
        else temp = pokemon.substring(0, 1).toUpperCase() + pokemon.substring(1) + "\tLVL???";
        
        canvas.drawText(temp, USER_FRAME_TOPLEFT_X + frame2.getWidth()/2.0f - HEALTH_BAR_SIZE/2.0f, USER_FRAME_TOPLEFT_Y - 50.0f, brush);
        
        if(draw){
            orientation = "front";
            contender = 1;

            try {
                temp = "sprites/" + pokemon + "/" + orientation + "/frame_";
                i += 1;
                stream = assets.open(temp + i + ".png");
                decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
            } catch (Exception var30) {
                try {
                    i = 0;
                    stream = assets.open("sprites/" + pokemon + "/" + orientation + "/frame_" + i + ".png");
                    decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
                } catch (Exception var29) {
                    try {
                       i = 1;
                       stream = assets.open("sprites/" + pokemon + "/" + orientation + "/frame_" + i + ".png");
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
                stream = assets.open(temp + j + ".png");
                decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
            } catch (Exception var26) {
                try {
                    j = 0;
                    stream = assets.open("sprites/" + pokemon + "/" + orientation + "/frame_" + j + ".png");
                    decodeResizedBitmapFromAssets(LARGEST_WIDTH, LARGEST_HEIGHT);
                } catch (Exception var25) {
                    try {
                        j = 1;
                        stream = assets.open("sprites/" + pokemon + "/" + orientation + "/frame_" + j + ".png");
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
        if (layer == 1) matrix.setTranslate((width*2/3.0f - options2.outWidth/2.0f)*1.25f, ((height/4.0f + LARGEST_HEIGHT/2.0f) -  options2.outHeight)*1.0f);
        else if (layer == 2) matrix.setTranslate((width/3.0f - options.outWidth/2.0f)*0.5f, (((height*2/3.0f) +  LARGEST_HEIGHT/2.0f) - options.outHeight)*1.0f);
        
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
        if(options.outHeight > reqHeight || options.outWidth > reqWidth){
            y = options.outHeight/2;
            x = options.outWidth/2;
            
            while(y/decSampleSize >= reqHeight && x/decSampleSize >= reqWidth) decSampleSize *= 2;            
        }
        options.inSampleSize = decSampleSize;
    }

    private void calculateIncSampleSize(int reqWidth, int reqHeight){
        int incSampleSize = 1;
        if(options.outHeight < reqHeight || options.outWidth < reqWidth){
            y = options.outHeight*2;
            x = options.outWidth*2;
            
            while(y*incSampleSize < reqHeight && x*incSampleSize < reqWidth) incSampleSize *= 2;            
        }
        options.inDensity = 1;
        options.inTargetDensity = 4;
        options.inScaled = true;
    }

    private void decodeResizedBitmapFromAssets(int reqWidth, int reqHeight){
        if(contender == 1){
            options = new Options();
//            options.inJustDecodeBounds = false;
//            options.inDensity = 1;
//            options.inTargetDensity = 4;
//            options.inScaled = true;
            frame = BitmapFactory.decodeStream(stream, null, options);
        } else {        
            options2 = new Options();
//            options2.inJustDecodeBounds = false;
//            options2.inDensity = 1;
//            options2.inTargetDensity = 5;
//            options2.inScaled = true;
            frame2 = BitmapFactory.decodeStream(stream, null, options2);
        }
    }

    private void setLargest(){
        try {
            folders = assets.list("fol");
            
            for(String folder : folders){
                files = assets.list("sprites/" + folder + "/" + orientation);
                options.inJustDecodeBounds = false;
                frame = BitmapFactory.decodeStream(assets.open("sprites/" + folder + "/" + orientation + "/" + files[0]), null, options);
                if(options.outWidth > LARGEST_WIDTH) LARGEST_WIDTH = options.outWidth;
                if(options.outHeight > LARGEST_HEIGHT) LARGEST_HEIGHT = options.outHeight;                
            }
        } catch (Exception e) {
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