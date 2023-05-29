package com.example.colortoaim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    //如果imageView改变，那么其中有一处 725需要改变
    private final int IMAGE_WIDTH_SET=725;
    private ImageView imageView;
    private View aim_view;
    private Button button_confirm;
    private Button button_back;
    private Button button_RGB;
    private Button button_HSV;
    private Button button_16;
    private Button button_mol;
    private int color_aim=0;
    private TextView textView_show_mol;
    private int lastX, lastY;
    private int aimX_image;
    private int aimY_image;
    private Bitmap bitmap;
    private Bitmap aim_view_image;
    private double percent_px_image_max;
    private int imageView_height;
    private int imageView_width;
    SoundPool sp_confirm;
    SoundPool sp_long_confirm;
    SoundPool sp_choose_photo;
    SoundPool sp_back;
    SoundPool sp_color;
    private boolean exitCount = true;
    private boolean stepCount=true;
    private boolean flag_photo=false;

    //每个功能的requestCode设置为不同的值，在onActivityResult中根据不同的requestCode执行不同的操作,这样，每个功能都有自己独立的requestCode
    private static final int REQUEST_CODE_ONE = 1;
    private static final int REQUEST_CODE_TWO = 2;
    private final int DEFINE_HSV_NUMBER=2;
    private final int DEFINE_RGB_NUMBER=1;
    private final int DEFINE_16_NUMBER=3;
    private final int DEFINE_MOL_NUMBER=4;
    private final int DEFINE_AIMVIEW_W=30;
    private final int DEFINE_AIMVIEW_H=30;
    private final int DEFINE_ORIGIN_RED=198;
    private final int DEFINE_ORIGIN_GREEN=109;
    private final int DEFINE_ORIGIN_BLUE=103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button my_button1 = findViewById(R.id.my_button1);
        Button my_button2 = findViewById(R.id.my_button2);

        //修改陕科大图标背景为透明
        View sust_view=findViewById(R.id.sust_symbol);
        Bitmap sust_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_sust);
        Bitmap  sust_bitmap_copy= sust_bitmap.copy(Bitmap.Config.ARGB_8888, true);
        for (int x = 0; x < sust_bitmap_copy.getWidth(); x++) {
            for (int y = 0; y < sust_bitmap_copy.getHeight(); y++) {
                int pixel = sust_bitmap_copy.getPixel(x, y);
                if (Color.red(pixel) >= 240 && Color.green(pixel) >= 240 && Color.blue(pixel) >= 240) {
                    sust_bitmap_copy.setPixel(x, y, Color.rgb(255, 192, 203)); //将接近白色的像素变成透明
                }
            }
        }
        Drawable drawable = new BitmapDrawable(getResources(), sust_bitmap_copy);
        sust_view.setBackground(drawable);

        sp_choose_photo = new SoundPool.Builder().build();
        int sound_choose_photo=sp_choose_photo.load(this,R.raw.sound_view_choose_photo,1);

        my_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Button1 被点击的处理
                Toast.makeText(MainActivity.this, "当前功能正在建设中，K正在设计中......", Toast.LENGTH_SHORT).show();
                //myButtonClickHandler1(v);
            }
        });

        my_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sp_choose_photo.play(sound_choose_photo,1,1,0,0,1);
                myButtonClickHandler2(v);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ONE) {
            // 处理功能1的结果
            if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                // 对照片进行处理
                bitmap = BitmapFactory.decodeFile(picturePath);
                // ... 进行处理
            }
        } else if (requestCode == REQUEST_CODE_TWO) {
            // 处理功能2的结果
            if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                // 对照片进行处理
                bitmap = BitmapFactory.decodeFile(picturePath);
                // ... 进行处理
                setContentView(R.layout.image_show);
                stepCount=false;

                // 将Bitmap对象显示到ImageView组件中
                imageView = findViewById(R.id.imageView);
                aim_view=findViewById(R.id.aim_view);
                button_confirm=findViewById(R.id.button_confirm);
                button_back=findViewById(R.id.button_back);
                textView_show_mol=findViewById(R.id.textView_show_mol);
                button_16=findViewById(R.id.button_16);
                button_RGB=findViewById(R.id.button_RGB);
                button_HSV=findViewById(R.id.button_HSV);
                button_mol=findViewById(R.id.button_mol);

                imageView.setImageBitmap(bitmap);

                //计算max比例问题
                imageView_height=dpToPx(this,IMAGE_WIDTH_SET);

                //获取屏幕密度，计算image width
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                imageView_width= metrics.widthPixels;
                //提前算图
                double bitmap_w=bitmap.getWidth();
                double bitmap_h=bitmap.getHeight();
                double percent_px_image_w=bitmap_w/imageView_width;
                double percent_px_image_h=bitmap_h/imageView_height;
                percent_px_image_max=Math.max(percent_px_image_w,percent_px_image_h);
                Log.i("TAG","percent : ("+bitmap_w+","+ bitmap_h+")---"+percent_px_image_max+")..("+imageView_width+","+imageView_height);
                FrameLayout.LayoutParams layoutParams =(FrameLayout.LayoutParams) aim_view.getLayoutParams();

                if(percent_px_image_w>percent_px_image_h){
                    flag_photo=true;
                    int aim_top = 0;
                    aim_top= (int) ((imageView_height-(bitmap_h/percent_px_image_w))/2);
                    layoutParams.leftMargin = 0;
                    layoutParams.topMargin = aim_top;
                }else{
                    int aim_left=0;
                    aim_left=(int)((imageView_width-(bitmap_w/percent_px_image_h))/2);
                    layoutParams.leftMargin = aim_left;
                }
                aim_view.setLayoutParams(layoutParams);


                aimX_image = 0;
                aimY_image = 0;

                // 创建 ShapeDrawable 对象
                ShapeDrawable shapeDrawable = new ShapeDrawable();
                shapeDrawable.setShape(new RectShape());
                shapeDrawable.getPaint().setColor(Color.BLACK);
                shapeDrawable.getPaint().setStrokeWidth(3);
                shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
                aim_view.setBackground(shapeDrawable);

                // 裁剪Bitmap对象  初始
                aim_view_image = Bitmap.createBitmap(bitmap,
                        (int)(0*percent_px_image_max),
                        (int)(0*percent_px_image_max),
                        (int)(dpToPx(this,DEFINE_AIMVIEW_W)*percent_px_image_max),
                        (int)(dpToPx(this,DEFINE_AIMVIEW_H)*percent_px_image_max));

                //声音特效
                sp_confirm = new SoundPool.Builder().build();
                sp_back = new SoundPool.Builder().build();
                sp_long_confirm = new SoundPool.Builder().build();
                sp_color=new SoundPool.Builder().build();

                // 加载音频文件
                int sound_confirm = sp_confirm.load(this, R.raw.sound_button_confirm, 1);
                int sound_back = sp_back.load(this, R.raw.sound_button_back, 1);
                int sound_long_confirm=sp_long_confirm.load(this,R.raw.sound_button_long_confirm,1);
                int sound_color=sp_color.load(this,R.raw.sound_view_choose_photo,1);


                Toast.makeText(this, "请将左上角的方框，拖到识别颜色识别区域", Toast.LENGTH_LONG).show();

                aim_view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                lastX = (int) event.getRawX();
                                lastY = (int) event.getRawY();
                                //Log.i("TAG","imageView:,getLeft(): "+imageView.getLeft()+" ,getRight(): "+imageView.getRight()+",getTop(): "+imageView.getTop()+",getBottom(): "+imageView.getBottom());
                                //Log.i("TAG","aim "+aim_view.getLeft()+","+aim_view.getTop()+","+aim_view.getWidth());

                                break;
                            case MotionEvent.ACTION_MOVE:
                                //对下面越界问题针对dx正负& getLeft() 判断，判断出相聚左边距离是否小于移动距离
                                int dx = (int) event.getRawX() - lastX;
                                int dy = (int) event.getRawY() - lastY;
                                //在这为了防止aim_view 超过imageVIew视图，code运行时ms，过快的滑动就会超过视图

                                int left = aim_view.getLeft() + dx;
                                int right = aim_view.getRight() + dx;
                                int top = aim_view.getTop() + dy;
                                int bottom = aim_view.getBottom() + dy;

                                if(flag_photo){
                                    if(left<0){
                                        left=0;
                                        right=aim_view.getWidth();
                                    }
                                    if(right>imageView.getRight()){
                                        right=imageView.getRight();
                                        left=imageView.getRight()-(aim_view.getWidth());
                                    }

                                    if(top<(int) ((imageView_height-(bitmap_h/percent_px_image_w))/2)){
                                        top=(int) ((imageView_height-(bitmap_h/percent_px_image_w))/2);
                                        bottom=top+aim_view.getHeight();
                                    }
                                    if(bottom > (imageView.getHeight()-(int)((imageView_height-(bitmap_h/percent_px_image_w))/2))){
                                        bottom=(imageView.getHeight()-(int)((imageView_height-(bitmap_h/percent_px_image_w))/2));
                                        top=bottom-(aim_view.getHeight());
                                    }
                                }else{
                                    if(left<(int)((imageView_width-(bitmap_w/percent_px_image_h))/2)){
                                        left=(int)((imageView_width-(bitmap_w/percent_px_image_h))/2);
                                        right=left+aim_view.getWidth();
                                    }
                                    if(right>(imageView.getRight()-(int)((imageView_width-(bitmap_w/percent_px_image_h))/2))){
                                        right=(imageView.getRight()-(int)((imageView_width-(bitmap_w/percent_px_image_h))/2));
                                        left=right-(aim_view.getWidth());
                                    }

                                    if(top<0){
                                        top=0;
                                        bottom=aim_view.getHeight();
                                    }
                                    if(bottom>imageView.getBottom()){
                                        bottom=imageView.getBottom();
                                        top=imageView.getBottom()-(aim_view.getHeight());
                                    }
                                }

                                aim_view.layout(left, top, right, bottom);
                                lastX = (int) event.getRawX();
                                lastY = (int) event.getRawY();
                                Log.i("TAG", "Touch at MotionEvent.ACTION_MOVE [ left: "+left+", top: "+top+",right: "+right+",buttom: "+bottom+"]");
                                if(flag_photo){
                                    aimX_image=(int)aim_view.getX();
                                    aimY_image=(int)aim_view.getY()-(int) ((imageView_height-(bitmap_h/percent_px_image_w))/2);
                                }else{
                                    aimX_image=(int)aim_view.getX()-(int)((imageView_width-(bitmap_w/percent_px_image_h))/2);
                                    aimY_image=(int)aim_view.getY();
                                }
                                break;
                            default:
                                // 裁剪Bitmap对象
                                aim_view_image = Bitmap.createBitmap(bitmap,
                                        (int)(aimX_image*percent_px_image_max),
                                        (int)(aimY_image*percent_px_image_max),
                                        (int)(aim_view.getWidth()*percent_px_image_max),
                                        (int)(aim_view.getHeight()*percent_px_image_max));
                                //Log.i("TAG","UPUPUPUPUPUP");
                                break;
                        }
                        return true;
                    }
                });

                button_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sp_confirm.play(sound_confirm,1,1,0,0,1);
                        analyzeColorView();
                    }
                });

                button_confirm.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        sp_long_confirm.play(sound_long_confirm,1,1,0,0,1);
                        imageView.setImageBitmap(aim_view_image);
                        return true;
                    }
                });

                button_confirm.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //Log.i("TAG","onTouch"+aim_view.getLeft());
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            imageView.setImageBitmap(bitmap);
                        }
                        return false;
                    }
                });

                button_back.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        sp_back.play(sound_back,1,1,0,0,1);

                        changeImage();
                    }
                });

                button_HSV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sp_color.play(sound_color,1,1,0,0,1);
                        color_aim=DEFINE_HSV_NUMBER;
                    }
                });

                button_RGB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sp_color.play(sound_color,1,1,0,0,1);
                        color_aim=DEFINE_RGB_NUMBER;
                    }
                });

                button_16.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sp_color.play(sound_color,1,1,0,0,1);
                        color_aim=DEFINE_16_NUMBER;
                    }
                });

                button_mol.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sp_color.play(sound_color,1,1,0,0,1);
                        color_aim=DEFINE_MOL_NUMBER;
                    }
                });

            }
        }
    }

    //直接用相机的功能
    public void myButtonClickHandler1(View view) {
        //编写事件处理

        //创建一个SurfaceView对象
        Context context = getApplicationContext();
        SurfaceView surfaceView = new SurfaceView(context);
        //获取布局
        RelativeLayout layout = findViewById(androidx.constraintlayout.widget.R.id.layout);
        //创建布局参数LayoutParams，并设置宽高以及位置等信息
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        //上面的代码设置了SurfaceView控件的宽高为MATCH_PARENT，即与父容器一样大，并将其设置在布局的中心位置。
        //将SurfaceView控件添加到布局中
        layout.addView(surfaceView, layoutParams);


        // 创建一个Intent对象，用于启动系统相机
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 判断系统是否支持相机
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // 启动相机，并获取拍摄的照片
            //startActivityForResult(takePictureIntent, 1);
        }
    }

    //照片采集
    public void myButtonClickHandler2(View view) {
        // Button 2被点击时的逻辑处理
        //在Activity中创建一个Intent，用来打开相册并选择照片：
        //在startActivityForResult()方法中传入一个requestCode，用于在后续处理中区分不同的请求
        //在Activity中重写onActivityResult()方法，获取选择的照片并进行处理：
        //确保在您的代码中请求这些权限，以便在应用程序运行时向用户请求访问权限。
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            // Permission has already been granted 权限被允许
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }


    }

    //这段代码是处理权限请求结果的方法，如果请求的权限被授予，则使用Intent打开相册，否则可以显示一条消息或者做一些其他的事情。
    //在这个方法中，我们首先调用super.onRequestPermissionsResult()方法，以确保父类的方法正常运行。然后，我们检查请求代码是否匹配我们的请求，并检查结果是否为授予状态。如果是这样，我们创建一个Intent对象打开相册，并在startActivityForResult()方法中启动它。如果不是，我们可以显示一条消息或者做一些其他的事情。
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, use it
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            } else {
                // Permission denied, show a message or do something else
                //使用一个AlertDialog来显示一个更加详细的消息，给用户更多的解释和选项：
                new AlertDialog.Builder(this)
                        .setTitle("快把存储权限打开哦，乖")
                        .setMessage("大胆乱贼，我需要访问你的存储空间来选择照片。再请求一次，再不允许存储权限，就别用了哦，快快地！！。")
                        .setPositiveButton("马上就开", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(
                                        MainActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        })
                        .setNegativeButton("就是不开", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                                Toast.makeText(MainActivity.this, "不开权限我也没法使用哦", Toast.LENGTH_SHORT)
                                         .show();
                            }
                        })
                        .show();
            }
        }
    }

    private void analyzeColorView() {
        Map<Integer, Integer> colorCounts = new HashMap<>();
        // 遍历裁剪后的Bitmap对象像素并记录颜色值
        //for (int x = 0; x < aim_view_image.getWidth(); x++) {
        //    for (int y = 0; y < aim_view_image.getHeight(); y++) {
        //      int pixel=aim_view_image.getPixel(x,y);
        for (int x = (int)(aimX_image*percent_px_image_max+1); x < ((aimX_image+aim_view.getWidth())*percent_px_image_max)-1; x++) {
            for (int y = (int)(aimY_image*percent_px_image_max+1); y <((aimY_image + aim_view.getHeight())*percent_px_image_max)-1; y++) {
                int pixel = bitmap.getPixel(x, y);
                // 统计颜色出现次数
                if (colorCounts.containsKey(pixel)) {
                    colorCounts.put(pixel, colorCounts.get(pixel) + 1);

                } else {
                    colorCounts.put(pixel, 1);
                }
            }
        }

        // 分析颜色值，例如得到最常出现的颜色
        int maxCount = 0;
        int maxColor = 0;
        for (Map.Entry<Integer, Integer> entry : colorCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxColor = entry.getKey();
            }
        }

        String colorString = null;
        if(color_aim == DEFINE_HSV_NUMBER){
            float[] hsv = new float[3];
            Color.RGBToHSV((maxColor >> 16) & 0xFF, (maxColor >> 8) & 0xFF, maxColor & 0xFF, hsv);
            colorString="HSV: (" + hsv[0] + ", " + hsv[1] + ", " + hsv[2]; // 输出HSV值
        }else if(color_aim == DEFINE_16_NUMBER){
            // 将颜色值转换成颜色字符串并输出
            colorString = String.format("#%06X", (0xFFFFFF & maxColor));
        }else if(color_aim == DEFINE_MOL_NUMBER){
            //R不变，GB越高颜色约淡浓度月高
            // 定义RGB值
            int red_orgin =DEFINE_ORIGIN_RED;
            int red = (maxColor >> 16) & 0xFF; // 获取红色分量
            int green = (maxColor >> 8) & 0xFF; // 获取绿色分量
            int blue = maxColor & 0xFF; // 获取蓝色分量

            if(Math.abs(red-red_orgin)<15){
                // 计算平均值
                int average_origin = (DEFINE_ORIGIN_GREEN + DEFINE_ORIGIN_BLUE) / 2;
                int average=(green+blue)/2;
                // 计算差值
                int diff = Math.abs(average-average_origin);

                // 根据差值范围确定result的值
                String result="0";
                if (diff >= 8 && diff < 20) {
                    result = "10";
                } else if (diff >= 20 && diff < 30) {
                    result = "10^2";
                } else if (diff >= 30 && diff < 40) {
                    result = "10^4";
                } else if (diff >= 40 && diff < 50) {
                    result = "10^6";
                } else if (diff >= 50) {
                    result = "10^8";
                } else {
                    result = "0"; // 差值不在范围内，result为原来值
                }
                // 输出result的值
                colorString="mol:("+result+" CFU/mL)";
            }else{
                Toast.makeText(this, "请针对性的选择目标，如有疑问请联系开发者K......", Toast.LENGTH_SHORT)
                        .show();
                colorString="mol:(0 CFU/mL)";
            }
        }else{
            int red = (maxColor >> 16) & 0xFF; // 获取红色分量
            int green = (maxColor >> 8) & 0xFF; // 获取绿色分量
            int blue = maxColor & 0xFF; // 获取蓝色分量
            colorString = "RGB: (" + red + "," + green + "," + blue + ")";
        }

        if(((maxColor>>16) & 0xFF)<=60 && ((maxColor>>8) & 0xFF)<=60 && (maxColor & 0xFF)<=60){
            //黑色背景，字体用白色
            textView_show_mol.setTextColor(Color.WHITE); // 设置字体颜色为白色
        }else{
            //非黑色，字体用黑色
            textView_show_mol.setTextColor(Color.BLACK);
        }
        textView_show_mol.setText(String.format("%s",colorString));
        textView_show_mol.setBackgroundColor(maxColor);
    }

    private void changeImage(){
        int color=Color.RED;
        int startX = aimX_image; // 起始 x 坐标
        int startY = aimY_image; // 起始 y 坐标
        int width = aim_view.getWidth();   // 区域宽度
        int height = aim_view.getHeight();  // 区域高度

        Bitmap  bitmap_copy= bitmap.copy(Bitmap.Config.ARGB_8888, true);
        //Log.i("TAG","changeImage : "+startX+","+startY);
        // 循环遍历指定区域内的每个像素
        for (int x = (int)(startX*percent_px_image_max+1); x < ((startX+width)*percent_px_image_max)-1; x++) {
            for (int y = (int)(startY*percent_px_image_max+1); y < ((startY + height)*percent_px_image_max)-1; y++) {
                try {
                    bitmap_copy.setPixel(x, y, color); // 设置像素颜色
                } catch (java.lang.IllegalArgumentException e) {
                    Log.i("TAG", "set pixel error: " + e.getMessage());
                }
            }
        }

        Log.i("TAG","图片大小"+getResources().getDisplayMetrics().density+",, ("+bitmap_copy.getWidth()+","+bitmap_copy.getHeight()+").  (" +percent_px_image_max+")");
        Log.i("TAG","imageView 大小w,h: ("+imageView.getWidth()+","+imageView.getHeight()+")");
        // 更新 ImageView 显示的位图
        imageView.setImageBitmap(bitmap_copy);
    }

    //dx to px
    public static int dpToPx(Context context, float dpValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (dpValue * metrics.density + 0.5f);
    }

    @Override
    public void onBackPressed() {

        if (exitCount) {
            if(stepCount){
                Toast.makeText(this,"再按一次退出当前程序哦",Toast.LENGTH_SHORT)
                        .show();
            }else {
                Toast.makeText(this, "再按一次返回", Toast.LENGTH_SHORT)
                        .show();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitCount=true;
                }
            }, 2500);

        } else if (!exitCount) {
            if(stepCount) {
                finish();
            }else{
                //销毁当前Activity的所有数据并退出

                //重新开始当前Activity，并清除Activity栈中该Activity之上的所有Activity实例
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                finish();

            }
        } else {
            finish();
        }
        exitCount=false;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        //释放声音资源
        if(sp_back != null){
            sp_back.release();
            sp_back=null;
        }
        if(sp_choose_photo != null){
            sp_choose_photo.release();
            sp_choose_photo=null;
        }
        if(sp_confirm != null){
            sp_confirm.release();
            sp_confirm=null;
        }
        if(sp_long_confirm != null){
            sp_long_confirm.release();
            sp_long_confirm=null;
        }

        //释放bitmap
        if(bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap=null;
        }
        if(aim_view_image != null && !aim_view_image.isRecycled()){
            aim_view_image.recycle();
            aim_view_image=null;
        }

    }
}