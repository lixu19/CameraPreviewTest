package com.speedata.camerapreviewtest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.spd.code.CodeUtils.ActivateAPIWithLocalServer;

/**
 * @author xuyan
 */
public class Utils {
    /**
     * 激活
     *
     * @param context 上下文
     */
    public static void inputId(Context context) {

        byte[] frameBuffer = new byte[3980];
        try {
            AssetManager assetManager = context.getResources().getAssets();
            InputStream inputStream = null;

            try {
                inputStream = assetManager.open("IdentityClient.bin");
                if (inputStream != null) {
                    Logcat.d("It worked!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (inputStream != null) {
                int len = inputStream.read(frameBuffer);
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        @SuppressLint("SdCardPath") int result = ActivateAPIWithLocalServer("/data/data/com.speedata.camerapreviewtest/IdentityClient.bin");
        //0失败,1成功
        Logcat.d("result:" + result);
    }


    /**
     * 将文件从assets目录，考贝到 /data/data/ 目录中。assets 目录中的文件，会不经压缩打包至APK包中，使用时还应从apk包中导出来
     *
     * @param activity 调用的activity
     */
    public static void copyAssetsFile2Phone(Activity activity) {
        try {
            InputStream inputStream = activity.getAssets().open("IdentityClient.bin");
            //getFilesDir() 获得当前APP的安装路径 /data/data/ 目录
            @SuppressLint("SdCardPath") File file = new File("/data/data/com.speedata.camerapreviewtest/IdentityClient.bin");
            if (!file.exists() || file.length() == 0) {
                //如果文件不存在，FileOutputStream会自动创建文件
                FileOutputStream fos = new FileOutputStream(file);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                //刷新缓存区
                fos.flush();
                inputStream.close();
                fos.close();
                Logcat.d("模型文件复制完毕");
            } else {
                Logcat.d("模型文件已存在，无需复制");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
