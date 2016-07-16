package com.example.duanxudong.imageaudio;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity {

    ImageView img;
    private static final int PROCESSING = 1;
    private static final int FAILURE = -1;
    Player player;
    SeekBar seekBar;
    private int lengthh = 100, widthh = 100;

    private final class UIHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESSING: // 更新进度
                    seekBar.setProgress(msg.getData().getInt("size"));
                    float num = (float) seekBar.getProgress()
                            / (float) seekBar.getMax();
                    int result = (int) (num * 100); // 计算进度
//                    resultView.setText(result + "%");
                    if (seekBar.getProgress() == seekBar.getMax()) { // 下载完成
                        Toast.makeText(getApplicationContext(), "成功",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case FAILURE: // 下载失败
                    Toast.makeText(getApplicationContext(), "失误",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String imageUrl = "http://ww2.sinaimg.cn/mw690/78e15432gw1f5vho8zpbsj20bj06bjta.jpg";
        final String musicUrl = "http://online.24en.com/voa/standardenglish/201607/get-up-and-do-it-says-high-school-dropout-headed-for-college.mp3?key=566c4b5fb56cae75ba167f7d4cb36916";
        initview();

//        Glide.with(this).load("http://goo.gl/gEgYUd").into(img); //加载网络图片

        //读取图片
        Glide
                .with(this)

                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)     /** Caches with both {@link #SOURCE} and {@link #RESULT}. */
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(this)) //可以自定义动画效果
                .override(widthh, lengthh)
                .into(img)
        ;


        //可以使用后台线程上传图片
//        Glide.with(this)
//                .load("/user/profile/photo/path")
//                .asBitmap()
//                .toBytes()
//                .centerCrop()
//         to upload the bytes of a 250px by 250px profile photo for a user
//                .into(new SimpleTarget<byte[]>(250, 250) {
//                    @Override
//                    public void onResourceReady(byte[] data, GlideAnimation anim) {
//                        // Post your bytes to a background thread and upload them here.
//                    }
//                });


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        player.playUrl(musicUrl);
                    }
                }).start();

            }
        });

    }

    public void initview() {
        img = (ImageView) findViewById(R.id.pic);
        seekBar = (SeekBar) findViewById(R.id.music_progress);
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        player = new Player(seekBar);


    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            this.progress = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            player.mediaPlayer.seekTo(progress);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player = null;
        }
    }

}
