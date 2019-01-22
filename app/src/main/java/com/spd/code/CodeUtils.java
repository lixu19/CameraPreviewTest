package com.spd.code;
//package com.example.sdembeddeddemo;


//import android.graphics.Bitmap;

public class CodeUtils {

	public static boolean SD_Loaded = false;
	
    //public static native int DecodeImageSD(Bitmap bitmap);
	public static native int CodeEnable(int prop);
	public static native int CodeDisable(int prop);
    public static native int DecodeImageSD(byte[] a, int w, int h);
//    public static native int ComputeAGCSD(int qualitythreshold,int h,int w);
//    public static native int ProgressiveDecodeSD(byte[] j,int h,int w,int phase);
    public static native int LoadSD();
    public static native int UnloadSD();
    public static native byte[][] GetResultSD();
//    public static native int GetResultNumber();
//    public static native int SetBarCodeType(int type, int en);
    public static native int ActivateAPI(byte[] key, byte[] path);
    public static native int IsActivated(byte[] key, byte[] path);

    public static native int[][] GetBounds();

    public static native int ActivateAPIWithLocalServer(byte[] key, byte[] path, byte[] url, byte[] ImageBuffer, int size);
    public static native int ActivateAPIWithLocalServer(byte[] key, byte[] path, byte[] url);
    public static native int ActivateAPIWithLocalServer(String filename);
    public static native int ActivateAPIWithLocalServer();

    static {
		System.loadLibrary("nativedecoder");
    }

	

	
}
