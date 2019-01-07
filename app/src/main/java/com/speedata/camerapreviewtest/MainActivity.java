package com.speedata.camerapreviewtest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static com.speedata.camerapreviewtest.Utils.copyAssetsFile2Phone;
import static com.speedata.camerapreviewtest.Utils.inputId;

/**
 * @author xuyan
 */
public class MainActivity extends Activity implements OnClickListener, Callback {

    Camera camera = null;
    SurfaceView surfaceview;
    SurfaceHolder surfaceholder;
    TextView mShowMessage;
    Button mScan, mAutoZoom, mSetPic, mSwCam, mZoom, test;
    boolean mScaning = false;
    List<Camera.Size> mPictureSize = null;
    List<Camera.Size> mPreviewSize = null;
    Camera.Parameters mParams = null;
    String[] mPictureSizeString = null;
    String[] mPreviewSizeString = null;
    //选择camera
    int cam = 0;
    boolean cansw = false;
    List<Integer> mZoomRatios = null;
    int[] zoom_ratio = {10, 15, 20, 30};
    int zoom_index = 0;
    ImageView imageview = null;

    SoundPool soundPool;
    int soundId;

    int max_zoom = 0;

    int g_w, g_h;
    boolean isdecode = false;

    //Aztec Code,二维码制
    static final int SD_PROP_AZ_ENABLED = 0x40011201;
    //Codabar
    static final int SD_PROP_CB_ENABLED = 0x40010101;
    //Codablock A ,二维码制
    static final int SD_PROP_CODABLOCK_A_ENABLED = 0x40010305;
    //Codablock F ,二维码制
    static final int SD_PROP_CODABLOCK_F_ENABLED = 0x40010205;
    //Code 11
    static final int SD_PROP_C11_ENABLED = 0x40011801;
    //Code 128
    static final int SD_PROP_C128_ENABLED = 0x40010201;
    //Code 39
    static final int SD_PROP_C39_ENABLED = 0x40010301;
    //Code 93
    static final int SD_PROP_C93_ENABLED = 0x40011101;
    //Composite Code(混合码)
    static final int SD_PROP_CC_ENABLED = 0x40011401;
    //Data Matrix (二维码)
    static final int SD_PROP_DM_ENABLED = 0x40010401;
    //Hong Kong 2 of 5（没有）

    //Interleaved 2 of 5
    static final int SD_PROP_I25_ENABLED = 0x40010501;
    //Korea Post(没有)

    //Matrix 2 of 5
    static final int SD_PROP_M25_ENABLED = 0x40011901;
    //MaxiCode（二维）
    static final int SD_PROP_MC_ENABLED = 0x40010601;
    //MicroPDF417 （二维）
    static final int SD_PROP_MICROPDF_ENABLED = 0x40010702;
    //MSI Plessey
    static final int SD_PROP_MSIP_ENABLED = 0x40011601;
    //NEC 2 of 5
    static final int SD_PROP_NEC25_ENABLED = 0x40012201;
    //PDF417（二维）
    static final int SD_PROP_PDF_ENABLED = 0x40010701;
    //Pharmacode(没有)

    //Postal 类似（Matrix 2 of 5 Code）
    static final int SD_PROP_POSTAL_ENABLED = 0x40010801;
    //QR Code
    static final int SD_PROP_QR_ENABLED = 0x40010901;
    //Reduced Space Symbology (RSS)
    static final int SD_PROP_RSS_ENABLED = 0x40011301;
    //Straight 2 of 5 (with 2 bar start/stop codes)
    static final int SD_PROP_S25_2SS_ENABLED = 0x40011501;
    //Straight 2 of 5 (with 3 bar start/stop codes)
    static final int SD_PROP_S25_3SS_ENABLED = 0x40011503;
    //Telepen
    static final int SD_PROP_TP_ENABLED = 0x40012101;
    //Trioptic Code 39
    static final int SD_PROP_TRIOPTIC_ENABLED = 0x40010307;
    //UPC/EAN/JAN
    static final int SD_PROP_UPC_ENABLED = 0x40011001;


    private static final String TAG = "jk";


    private static final String KEY = "trial-speed-tjian-02282018";
    private static final String PATH = "/data/data/com.example.cameradec";
    private TextView tvTimes;
    private int times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        surfaceview = findViewById(R.id.surfaceView1);
        mShowMessage = findViewById(R.id.textView1);
        tvTimes = findViewById(R.id.tv_1);
//		test = findViewById(R.id.test);
//		test.setOnClickListener(this);
        mScan = findViewById(R.id.bl);
        mScan.setOnClickListener(this);
        mSetPic = findViewById(R.id.br);
        mSetPic.setOnClickListener(this);
        mZoom = findViewById(R.id.brpv);
        mZoom.setOnClickListener(this);
        mAutoZoom = findViewById(R.id.bm);
        mAutoZoom.setOnClickListener(this);
        mAutoZoom.setEnabled(false);
        mSwCam = findViewById(R.id.bc);
        mSwCam.setEnabled(false);

        imageview = findViewById(R.id.imageView1);

        surfaceholder = surfaceview.getHolder();
        surfaceholder.addCallback(this);

        int numofcam = Camera.getNumberOfCameras();
        if (numofcam > 1) {
            cansw = true;
            mSwCam.setOnClickListener(this);
            mSwCam.setEnabled(true);
        }

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load("/system/media/audio/ui/VideoRecord.ogg", 0);

/*		int ka = com.spd.code.CodeUtils.ActivateAPI("trial-speed-tjian-01222018".getBytes(), getFilesDir().getAbsolutePath().getBytes());
		Log.e("jk", "path is " + getFilesDir().getAbsolutePath() + " result is " + ka);
		
		ka = com.spd.code.CodeUtils.IsActivated("trial-speed-tjian-01222018".getBytes(), getFilesDir().getAbsolutePath().getBytes());
		Log.e("jk", "path is " + getFilesDir().getAbsolutePath() + " result is " + ka);
*/
        //if (!com.example.sdembeddeddemo.MainActivity.SD_Loaded)
//		int code = CodeUtils.ActivateAPI(KEY.getBytes(), PATH.getBytes());
//		Log.d(TAG, "onCreate: code is::" + code);

        //todo 判断id文件是否存在,不存在就从软件中copy一个过去
        copyAssetsFile2Phone(this);
        inputId(this);


        if (!com.spd.code.CodeUtils.SD_Loaded) {
            //if (com.example.sdembeddeddemo.MainActivity.LoadSD() == 1)
            if (com.spd.code.CodeUtils.LoadSD() == 1) {
                Toast.makeText(getApplicationContext(), "SwiftDecoder Loaded", Toast.LENGTH_LONG).show();
                Log.e(TAG, "SwiftDecoder Loaded");
//                if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_NEC25_ENABLED)) {
//                    Log.e(TAG, "SD_PROP_NEC25_ENABLED" + "code disenable");
//                }
//                if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_TP_ENABLED)) {
//                    Log.e(TAG, "SD_PROP_TP_ENABLED" + "code disenable");
//                }
//                if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_M25_ENABLED)) {
//                    Log.e(TAG, "SD_PROP_M25_ENABLED" + "code disenable");
//                }
//                if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_C11_ENABLED)) {
//                    Log.e(TAG, "SD_PROP_C11_ENABLED" + "code disenable");
//                }
//                if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_MSIP_ENABLED)) {
//                    Log.e(TAG, "SD_PROP_MSIP_ENABLED" + "code disenable");
//                }
//			if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_CC_ENABLED)) {
//				Log.e(TAG, "SD_PROP_CC_ENABLED" + "code disenable");
//			}
//                if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_RSS_ENABLED)) {
//                    Log.e(TAG, "SD_PROP_RSS_ENABLED" + "code disenable");
//                }
//                if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_C93_ENABLED)) {
//                    Log.e(TAG, "SD_PROP_C93_ENABLED" + "code disenable");
//                }
                if (1 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_UPC_ENABLED)) {
//                    int result = CodeUtils.CodeDisable(SD_PROP_UPC_ENABLED);
//                    Log.e(TAG, "SD_PROP_UPC_ENABLED" + result);
                }
//			if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_QR_ENABLED)) {
//				Log.e(TAG, "SD_PROP_QR_ENABLED" + "code disenable");
//			}
//			if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_PDF_ENABLED)) {
//				Log.e(TAG, "SD_PROP_PDF_ENABLED" + "code disenable");
//			}
//                if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_I25_ENABLED)) {
//                    Log.e(TAG, "SD_PROP_I25_ENABLED" + "code disenable");
//                }
//			if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_DM_ENABLED)) {
//				Log.e(TAG, "SD_PROP_DM_ENABLED" + "code disenable");
//			}
//                if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_TRIOPTIC_ENABLED)) {
//                    Log.e(TAG, "SD_PROP_TRIOPTIC_ENABLED" + "code disenable");
//                }
//                if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_C39_ENABLED)) {
//                    Log.e(TAG, "SD_PROP_C39_ENABLED" + "code disenable");
//                }
                if (1 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_C128_ENABLED)) {
//                    int result = CodeUtils.CodeDisable(SD_PROP_C128_ENABLED);
//                    Log.e(TAG, "SD_PROP_C128_ENABLED" + result);
                }
//                if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_CB_ENABLED)) {
//                    Log.e(TAG, "SD_PROP_CB_ENABLED" + "code disenable");
//                }
//			if (0 == com.spd.code.CodeUtils.CodeEnable(SD_PROP_AZ_ENABLED)) {
//				Log.e(TAG, "SD_PROP_AZ_ENABLED" + "code disenable");
//			}

            } else {
                Toast.makeText(getApplicationContext(), "SwiftDecoder Not Loaded", Toast.LENGTH_LONG).show();
                Log.e(TAG, "SwiftDecoder Not Loaded");
            }
        }
        com.spd.code.CodeUtils.SD_Loaded = true;
    }

    private int CodeEnable(int qr) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
		
/*		if(camera != null)
		{
			if(mScaning)
			{
				camera.stopPreview();
			}
			camera.release();
			camera = null;
		}
*/
        soundPool.release();
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        if (v == mAutoZoom) {
		/*camera.setOneShotPreviewCallback(new Camera.PreviewCallback() {

			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				// TODO Auto-generated method stub
				YuvImage img = new YuvImage(data, ImageFormat.NV21, mParams.getPreviewSize().width, mParams.getPreviewSize().height, null);
				if(img != null)
				{
		            ByteArrayOutputStream stream = new ByteArrayOutputStream(data.length);
		            img.compressToJpeg(new Rect(0, 0, img.getWidth(), img.getHeight()), 100, stream);

		            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
		            imageview.setImageBitmap(bmp);
		            try {
		                stream.close();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
			}

		});*/

            camera.autoFocus(new Camera.AutoFocusCallback() {

                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        mShowMessage.setText("auto foucs ok");
                    }
                }
            });

			/*camera.autoFocus(new Camera.AutoFocusCallback() {
			
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					// TODO Auto-generated method stub
					if(success)
					{
						mShowMessage.setText("auto foucs ok");
						camera.takePicture(null, null, new Camera.PictureCallback() {
			
							@Override
							public void onPictureTaken(byte[] data, Camera camera) {
								// TODO Auto-generated method stub
								Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
								if (com.example.sdembeddeddemo.MainActivity.DecodeImageSD(bmp) == 0) {
									mShowMessage.setText("decode failed");
								}
								else
								{
									String str = new String(com.example.sdembeddeddemo.MainActivity.GetResultSD());
									if((str != null) && (!str.equalsIgnoreCase("")))
									{
										mShowMessage.setText("result: " + str);
									}
									else
									{
										mShowMessage.setText("decode failed");
									}
								}

								camera.startPreview();
							}
						});
					}
				}
			});*/
/*		
			camera.takePicture(null, null, new Camera.PictureCallback() {
			
				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
				// TODO Auto-generated method stub
					Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
					
					Log.e(TAG, "take picture is called");
					if (com.example.sdembeddeddemo.MainActivity.DecodeImageSD(bmp) == 0) {
						mShowMessage.setText("decode failed");
					}
					else
					{
						String str = new String(com.example.sdembeddeddemo.MainActivity.GetResultSD());
						if((str != null) && (!str.equalsIgnoreCase("")))
						{
							mShowMessage.setText("result: " + str);
						}
						else
						{
							mShowMessage.setText("decode failed");
						}
					}

					Log.e(TAG, "take picture is over");
					camera.startPreview();
				}
			});
*/
        } else if (v == mSetPic) {
            SizeSelectDialog sl = new SizeSelectDialog(this);
            sl.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    Camera.Size cs = mParams.getPreviewSize();
                    Camera.Size dfs = mParams.getPictureSize();
                    mShowMessage.setText(String.format("%s camera config used now\npreview %d x %d\npic %d x %d", (cam == 0 ? "back" : "front"), cs.width, cs.height, dfs.width, dfs.height));
                    //mShowMessage.refreshDrawableState();
                }

            });
            sl.show();
        } else if (v == mZoom) {
            if (mScaning) {
                if ((camera != null) && (max_zoom > 0)) {
                    if (++zoom_index >= zoom_ratio.length) {
                        zoom_index = 0;
                    }
                    mParams.setZoom(zoom_ratio[zoom_index]);
                    Log.e("jk", "now value is " + zoom_ratio[zoom_index]);
                    camera.setParameters(mParams);
                    mZoom.setText("zoom ratio: " + ((float) mZoomRatios.get(zoom_ratio[zoom_index]) / 100));
                }
            } else {
                PvSelectDialog pl = new PvSelectDialog(this);
                pl.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        Camera.Size cs = mParams.getPreviewSize();
                        Camera.Size dfs = mParams.getPictureSize();
                        mShowMessage.setText(String.format("%s camera config used now\npreview %d x %d\npic %d x %d", (cam == 0 ? "back" : "front"), cs.width, cs.height, dfs.width, dfs.height));
                        //mShowMessage.refreshDrawableState();
                    }
                });
                pl.show();
            }
        } else if (v == mScan) {
            if (mScaning) {
                mScaning = false;
                camera.stopPreview();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mScan.setText("start");
                mAutoZoom.setEnabled(false);
                mSetPic.setEnabled(true);
                //mZoom.setEnabled(true);
                mZoom.setText("set\npreview");
                if (cansw) {
                    mSwCam.setEnabled(true);
                }
                openCloseFlash("off");
                tvTimes.setText(String.valueOf(times));

            } else {
                mScaning = true;
                camera.startPreview();
                mScan.setText("stop");
                mAutoZoom.setEnabled(true);
                mSetPic.setEnabled(false);
                //mZoom.setEnabled(false);
                mZoom.setText("zoom ratio: " + ((float) mZoomRatios.get(zoom_ratio[zoom_index]) / 100));
                if (cansw) {
                    mSwCam.setEnabled(false);
                }
                openCloseFlash("en");
                openCloseFlash("on");
                times = 0;
                tvTimes.setText("0");
            }
        } else if (v == mSwCam) {
            if (camera != null) {
                if (mScaning) {
                    camera.stopPreview();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                camera.release();
//				cam = (cam == 0 ? 1 : 0);
                if (cam == 0) {
                    cam = 1;
                } else if (cam == 1) {
                    cam = 2;
                } else if (cam == 2) {
                    cam = 0;
                }
                camera = init_camera(cam);
                if (mScaning) {
                    if (camera != null) {
                        camera.startPreview();
                    }
                }
            }
        }
//		else if(v == test)
//		{
//			int q = 10;
//			int ret;
//			ret = com.spd.code.CodeUtils.ComputeAGCSD(q,g_h,g_w);
//			if (ret == 1)
//			{
//				Log.e(TAG, "swiftdecoder is ok");
//				Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
//			}
//			else
//			{
//				Log.e(TAG, "swiftdecoder is wrong");
//				Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
//			}
//		}
    }

    class PvSelectDialog extends Dialog implements OnClickListener, OnItemClickListener {

        public PvSelectDialog(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        private ListView lv;
        private Button bs;
        private ArrayAdapter<String> ap = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, mPreviewSizeString);

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            setContentView(R.layout.ss);

            lv = (ListView) findViewById(R.id.listView1);
            lv.setAdapter(ap);
            lv.setOnItemClickListener(this);
            bs = (Button) findViewById(R.id.bs);
            bs.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            dismiss();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO Auto-generated method stub
            Log.e(TAG, "pos is " + position + " id is " + id);
            int lvp = position;
            Toast.makeText(getApplicationContext(), ("select is " + mPreviewSize.get(lvp).width + " x " + mPreviewSize.get(lvp).height), Toast.LENGTH_LONG).show();
            mParams.setPreviewSize(mPreviewSize.get(lvp).width, mPreviewSize.get(lvp).height);
            g_w = mPreviewSize.get(lvp).width;
            g_h = mPreviewSize.get(lvp).height;
            camera.setParameters(mParams);
            dismiss();
        }
    }

    class SizeSelectDialog extends Dialog implements OnClickListener, OnItemClickListener {

        public SizeSelectDialog(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        private ListView lv;
        private Button bs;
        private ArrayAdapter<String> ap = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, mPictureSizeString);

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            setContentView(R.layout.ss);
            //调用handler
            handler = new Handler();

            lv = (ListView) findViewById(R.id.listView1);
            lv.setAdapter(ap);
            lv.setOnItemClickListener(this);
            bs = (Button) findViewById(R.id.bs);
            bs.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            dismiss();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO Auto-generated method stub
            Log.e(TAG, "pos is " + position + " id is " + id);
            int lvp = position;
            Toast.makeText(getApplicationContext(), ("select is " + mPictureSize.get(lvp).width + " x " + mPictureSize.get(lvp).height), Toast.LENGTH_LONG).show();
            mParams.setPictureSize(mPictureSize.get(lvp).width, mPictureSize.get(lvp).height);
            camera.setParameters(mParams);
            dismiss();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if (camera == null) {
            camera = init_camera(cam);
        }
    }

    private Camera init_camera(int id) {
        Camera tca = Camera.open(id);
        if (tca == null) {
            return null;
        }
        mParams = tca.getParameters();
        mPictureSize = mParams.getSupportedPictureSizes();
        Camera.Size tcs = mParams.getPictureSize();
        mPictureSizeString = new String[mPictureSize.size()];
        Log.e(TAG, "dismission size is " + mPictureSize.size());
        Camera.Size dfs = mParams.getPictureSize();
        Log.e(TAG, "now use pic size " + dfs.width + " x " + dfs.height);
        for (int i = 0; i < mPictureSize.size(); i++) {
            tcs = mPictureSize.get(i);
            Log.e(TAG, String.format("%d: h:%d w:%d\n", i, tcs.height, tcs.width));
            mPictureSizeString[i] = String.format("%d x %d", tcs.width, tcs.height);
        }
        mParams.setPictureSize(tcs.width, tcs.height);
        List<Integer> li = mParams.getSupportedPreviewFormats();
        Log.e(TAG, "now use preview format is " + mParams.getPreviewFormat());
        for (int i = 0; i < li.size(); i++) {
            Log.e(TAG, "supported preview format " + String.format("%x", li.get(i)));
        }
        mParams.setPreviewFormat(ImageFormat.YV12);
        mPreviewSize = mParams.getSupportedPreviewSizes();
        Camera.Size cs = mParams.getPreviewSize();
        Log.e(TAG, "now use preview size is " + cs.width + " x " + cs.height);
        mPreviewSizeString = new String[mPreviewSize.size()];
        for (int i = 0; i < mPreviewSize.size(); i++) {
            tcs = mPreviewSize.get(i);
            Log.e(TAG, "supported preview size " + tcs.width + " x " + tcs.height);
            mPreviewSizeString[i] = String.format("%d x %d", tcs.width, tcs.height);
        }
        mParams.setPreviewSize(tcs.width, tcs.height);
        g_w = tcs.width;
        g_h = tcs.height;


        /*
         *  霍尼部分camera的设置
         */
        mParams.set("iso", "800");
        List<String> focusModes = mParams.getSupportedFocusModes();
        if (focusModes != null) {
            if (focusModes.contains("macro")) {
                mParams.setFocusMode("macro");
            } else if (focusModes.contains("auto")) {
                mParams.setFocusMode("auto");
            }
        }
        mParams.setJpegQuality(100);
        mParams.setColorEffect(Camera.Parameters.EFFECT_MONO);
        mParams.setExposureCompensation(0);


        mShowMessage.setText("");
        if (mParams.isZoomSupported()) {
            max_zoom = mParams.getMaxZoom();
            mShowMessage.append("max support zoom is " + max_zoom + "\n");
            mZoomRatios = mParams.getZoomRatios();
            if (mZoomRatios != null) {
                StringBuilder xsss = new StringBuilder();
                for (int i = 0, j = 0; i < mZoomRatios.size(); i++) {
                    xsss.append((float) mZoomRatios.get(i) / 100).append(", ");
                    if ((j < zoom_ratio.length) && (mZoomRatios.get(i) / 10 == zoom_ratio[j])) {
                        zoom_ratio[j] = i;
                        Log.e("jk", "zoom_ratio[" + j + "] is " + ((float) mZoomRatios.get(i) / 100));
                        j++;
                    }
                }
                mShowMessage.append("ratio list is [" + xsss + "]\n");
            }
        } else {
            mShowMessage.append("don't support zoom\n");
        }

        if (mParams.isAutoExposureLockSupported()) {
            //mParams.setAutoExposureLock(true);
            mShowMessage.append("auto exposure lock is set to " + mParams.getAutoExposureLock());
            Log.e("jk", "auto exposure lock is set to " + mParams.getAutoExposureLock());
        } else {
            mShowMessage.append("not support to lock auto exposure");
            Log.e("jk", "not support to lock auto exposure");
        }

        if (mParams.isAutoWhiteBalanceLockSupported()) {
            mParams.setAutoWhiteBalanceLock(true);
            mShowMessage.append("auto wb lock is set to " + mParams.getAutoWhiteBalanceLock());
            Log.e("jk", "auto wb lock is set to " + mParams.getAutoWhiteBalanceLock());
        } else {
            mShowMessage.append("not support to lock auto wb");
            Log.e("jk", "not support to lock auto wb");
        }
		
/*		if(mParams.isSmoothZoomSupported())
		{
			int zv = mParams.getMaxZoom();
			mShowMessage.append("max support s zoom is " + zv);
			mParams.setZoom(zv);
		}
		else
		{
			mShowMessage.append("don't support s zoom");
		}*/
		/*try {
			Method gSzsd = mParams.getClass().getDeclaredMethod("getSupportedZSDMode");
			Method szsd = mParams.getClass().getDeclaredMethod("setZSDMode", String.class);
			if(gSzsd != null)
			{
					List<String> x = (List<String>) gSzsd.invoke(mParams);
					if(x != null)
					{
						for(int i = 0; i < x.size(); i++)
						{
							Log.e(TAG, "supported zsd mode is " + x.get(i));
						}
					}
			}
			
			if(szsd != null)
			{
				szsd.invoke(mParams, "on");
			}
			
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        tca.setParameters(mParams);
        tca.setPreviewCallback(new Camera.PreviewCallback() {

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                // TODO Auto-generated method stub
                //Log.e("jk", "jk data array size is " + data.length + " w is " + g_w + " h is " + g_h + "w x h is " + (g_w * g_h));
                if (!isdecode) {
                    isdecode = true;
                    mShowMessage.setText("");
                    if (com.spd.code.CodeUtils.DecodeImageSD(data, g_w, g_h) == 0) {

                        mShowMessage.setText("decode failed");
                        Log.e("jk", "jk decode failed");
                    } else {
                        byte[][] resary;
                        resary = com.spd.code.CodeUtils.GetResultSD();
                        if (resary != null) {
                            if (soundPool != null) {
                                soundPool.play(soundId, 1, 1, 0, 0, 1);
                                times++;
                                tvTimes.setText(String.valueOf(times));

                            }
                            int i = 0;
                            for (byte[] x : resary) {
                                Log.e("jk", "jk length of x is " + x.length);
                                String str = new String(x);
                                if (!"".equalsIgnoreCase(str)) {
                                    mShowMessage.append("result: " + i + " : " + str + '\n');
                                    Log.e("jk", "jk result " + i + " : " + str);
                                    i++;
                                }
                            }
                        } else {
                            mShowMessage.append("decode failed\n");
                            Log.e("jk", "jk decode str null");
                        }
                    }
                    isdecode = false;
                } else {
                    Log.e("jk", "jk skip this frame");
                }
            }
        });
        mParams = tca.getParameters();
        dfs = mParams.getPictureSize();
        Log.e(TAG, "max expose is " + mParams.getMaxExposureCompensation() + " min expose is " + mParams.getMinExposureCompensation());
        Log.e(TAG, "now use pic size " + dfs.width + " x " + dfs.height);
        Log.e(TAG, "now use preview format is " + mParams.getPreviewFormat());
        cs = mParams.getPreviewSize();
        Log.e(TAG, "now use preview size is " + cs.width + " x " + cs.height);
		/*String zsdv = "off";
		try {
			Method gzsd = mParams.getClass().getDeclaredMethod("getZSDMode");
			if(gzsd != null)
			{
				zsdv = (String)gzsd.invoke(mParams);
				Log.e(TAG, "zsd mode is " + zsdv);
			}
			
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

        //setCameraDisplayOrientation(this, id, tca);
        tca.setDisplayOrientation(90);

        //mShowMessage.setText(String.format("%s camera config used now\npreview %d x %d\npic %d x %d", (id == 0 ? "back" : "front"), cs.width, cs.height, dfs.width, dfs.height));
        mShowMessage.append(String.format("%s camera config used now\npreview %d x %d\npic %d x %d", (id == 0 ? "back" : "front"), cs.width, cs.height, dfs.width, dfs.height));

        try {
            tca.setPreviewDisplay(surfaceholder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            tca.release();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tca;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        Log.e(TAG, "surfaceChanged is called");
        if (camera == null) {
            camera = init_camera(cam);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Log.e(TAG, "surfaceDestroyed is called");
        if (camera != null) {
            if (mScaning) {
                Log.e(TAG, "sfdestory stop preview");
                camera.stopPreview();
                mScaning = false;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            camera.release();
            isdecode = false;
            camera = null;
        }
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


    private static BufferedWriter TorchFileWrite;

    /**
     * @param str
     */
    public void openCloseFlash(String str) {
        switch (cam) {
            case 0:
                //后置
                if (mParams != null) {
                    if ("on".equals(str)) {
                        mParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(mParams);
                    } else {
                        mParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(mParams);
                    }
                }
                break;
            case 1:
                //前置

                break;
            case 2:
                //扫头
                Log.d(TAG, "-print-rece-" + "openCloseFlash " + str + " start");
                File TorchFileName = new File("/sys/class/misc/lm3642/torch");
                try {
                    TorchFileWrite = new BufferedWriter(new FileWriter(TorchFileName, false));
                    TorchFileWrite.write(str);
                    TorchFileWrite.flush();
                    TorchFileWrite.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, e.getMessage());
                }
                Log.d(TAG, "-print-rece-" + "openCloseFlash end");
                break;
            default:
                break;
        }
    }


    Handler handler;

    /**
     * camera2 api.使用新camera2 api方法
     */
    public void init(){
        //定义宽高
        int width = 2560;
        int height = 1440;


        //预览数据流最好用非JPEG
        ImageReader imageReader = ImageReader.newInstance(width, height, ImageFormat.YV12, 1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                //最后一帧
                Image image = reader.acquireLatestImage();
                //do something
                int len = image.getPlanes().length;
                byte[][] bytes = new byte[len][];
                int count = 0;
                for (int i = 0; i < len; i++) {
                    ByteBuffer buffer = image.getPlanes()[i].getBuffer();
                    int remaining = buffer.remaining();
                    byte[] data = new byte[remaining];
                    byte[] _data = new byte[remaining];
                    buffer.get(data);
                    System.arraycopy(data, 0, _data, 0, remaining);
                    bytes[i] = _data;
                    count += remaining;
                }
                //数据流都在 bytes[][] 中，关于有几个plane，可以看查看 ImageUtils.getNumPlanesForFormat(int format);
                // ...
                //一定要关闭
                image.close();
            }
        }, handler);
    }


}
