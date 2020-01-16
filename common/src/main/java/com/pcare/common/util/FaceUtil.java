package com.pcare.common.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;

import com.pcare.common.entity.NetResponse;
import com.pcare.common.net.Api;
import com.pcare.common.net.RetrofitHelper;
import com.pcare.common.net.url.RetrofitUrlManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.http.Field;

/**
 * @Author: gl
 * @CreateDate: 2019/11/21
 * @Description: 用来进行人脸识别的类
 */
public class FaceUtil {
    private final String TAG = "Face++";
    private HandlerThread mCameraThread;//
    private Handler mCameraHandler;
    private Activity lookActivity;
    private TextureView textureView;

    private String mCameraId;
    private Size mPreviewSize;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mCaptureRequestBuilder;//实时图像的输出目标
    private CaptureRequest mCaptureRequest;
    private CameraCaptureSession mCameraCaptureSession;//不断捕获的图像
    private SurfaceTexture mSurfaceTexture;//从activity界面获取
    private ImageReader mImageReader;
    private Handler timerHandler = new Handler();//执行循环定时的任务
    private Runnable timerRunnable;
    private String imgBase64;
    private FaceDetectListener faceDetectListener;
    private FaceCompareListener faceCompareListener;
    private String faceUrl, userId;
    private String ugroup = "ASDFG1234";
    private volatile boolean isOpen = true;
    private final int DELAYTIME = 1000;
    private int oritentation = 1; // 1表示竖屏，2表示横屏
    private int mLength;
    private CountDownTimer mCountDownTimer;


    public interface FaceDetectListener {
        void detectSucess();

        void detectFail();
    }

    //设置超时时间，需在开始识别前设置
    public FaceUtil setTimeOut(int millis) {
        if (millis > 0) {
            mCountDownTimer = new CountDownTimer(millis, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if (isOpen) {
                        isOpen = false;
                        if (!TextUtils.isEmpty(userId)) {
                            faceDetectListener.detectFail();
                        } else {
                            faceCompareListener.compareFail();
                        }
                    }
                    mCountDownTimer = null;
                }
            };
            mCountDownTimer.start();
        }
        return this;
    }

    public interface FaceCompareListener {
        void compareSucess(String userId);

        void compareFail();
    }

    public FaceUtil(Activity lookActivity, TextureView textureView, int oritentation) {
        this.lookActivity = lookActivity;
        this.textureView = textureView;
        this.oritentation = oritentation;
    }

    public FaceUtil setFaceDetectListener(FaceDetectListener listener) {
        this.faceDetectListener = listener;
        return this;
    }

    public FaceUtil setFaceCompareListener(FaceCompareListener listener) {
        this.faceCompareListener = listener;
        return this;
    }


    //监听摄像头的状态
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        //摄像头打开，可以创建会话，开始预览
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //当SurefaceTexture可用的时候，设置相机参数并打开相机
            setupCamera(width, height);
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireLatestImage();
            //我们可以将这帧数据转成字节数组，类似于Camera1的PreviewCallback回调的预览帧数据
            if (image == null) {
                return;
            }
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            //ImageFormat.JPEG格式直接转化为Bitmap格式。
            Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
            imgBase64 = encodeImage(temp);
            image.close();
            temp.recycle();
            temp = null;
            data = null;
        }


    };

    public void init(String faceUrl, String userId) {
        PermissionHelper.requestCameraPermission(lookActivity, true);
        mCameraThread = new HandlerThread("CameraThread");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
        textureView.setSurfaceTextureListener(surfaceTextureListener);
        this.faceUrl = faceUrl;
        this.userId = userId;
        initRequest();
    }

    public void openCamera() {
        CameraManager manager = (CameraManager) lookActivity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(lookActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.openCamera(mCameraId, mStateCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void startPreview() {
        mSurfaceTexture = textureView.getSurfaceTexture();
        mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(mSurfaceTexture);
        Surface imageSurface = mImageReader.getSurface();
        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //这里是指实时图像数据的输出目标，以后录制视频、直播等都需要在这里添加对应的Target
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCaptureRequestBuilder.addTarget(imageSurface);

            //创建捕获请求，在需要预览、拍照、再次预览的时候都需要通过创建请求来完成
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, imageSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        mCaptureRequest = mCaptureRequestBuilder.build();
                        mCameraCaptureSession = session;
                        //不断捕获图像，显示预览图像
                        mCameraCaptureSession.setRepeatingRequest(mCaptureRequest, null, mCameraHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //设置相机
    private void setupCamera(int width, int height) {
        CameraManager manager = (CameraManager) lookActivity.getSystemService(Context.CAMERA_SERVICE);
        try {
            //遍历所有的摄像头
            for (String cameraId : manager.getCameraIdList()) {
                //获取到每个相机的参数对象，包含前后摄像头，分辨率等
                CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
                //摄像头的方向
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                //此处默认打开前置置摄像头
                if (facing != null && facing != CameraCharacteristics.LENS_FACING_FRONT)
                    continue;

                //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                assert map != null;
                LogUtil.i("width:" + width + "  height:" + height + " ORIENTATION" + cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION));
                mLength = width > height ? height : width;
                //根据TextureView的尺寸设置预览尺寸
                mPreviewSize = new Size(mLength, mLength);
                mImageReader = ImageReader.newInstance(mLength, mLength,
                        ImageFormat.JPEG, 1);
                ViewGroup.LayoutParams lp = textureView.getLayoutParams();
                lp.width = mLength;
                lp.height = mLength;
                textureView.setLayoutParams(lp);
                //如果是横屏，需要逆时针旋转90度
                if (oritentation == Configuration.ORIENTATION_LANDSCAPE) {
                    textureView.setRotation(-90);
                }
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mCameraHandler);

                mCameraId = cameraId;
                break;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        rotateBitmap(bm).compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    private Bitmap rotateBitmap(Bitmap bitmap) {
        //如果是竖屏，需要旋转270度
        if (bitmap != null && oritentation == Configuration.ORIENTATION_PORTRAIT) {
            Matrix m = new Matrix();
            m.postRotate(270);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    public void initRequest() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(imgBase64)) {
                    timerHandler.postDelayed(this, DELAYTIME);
                    return;
                }
                post();
                timerHandler.postDelayed(this, DELAYTIME);
            }
        };
        timerHandler.postDelayed(timerRunnable, 200);
    }

    private void post() {
        if (!TextUtils.isEmpty(userId)) {
            RetrofitUrlManager.getInstance().putDomain(Api.URL_VALUE_FACE, Api.FACEURL);
            RetrofitHelper.getInstance()
                    .getRetrofit()
                    .create(Api.class)
                    .detectFace(userId, imgBase64, ugroup)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribeWith(new DisposableSingleObserver<NetResponse>() {

                        @Override
                        public void onSuccess(NetResponse response) {
                            if (!isOpen)
                                return;
                            Log.i(TAG, "Response:" + response.toString());
                            if (response.getStatus() == 1) {
                                timerHandler.removeCallbacks(timerRunnable);
                                faceDetectListener.detectSucess();
                                isOpen = false;
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    });
        } else {
            RetrofitUrlManager.getInstance().putDomain(Api.URL_VALUE_FACE, Api.FACEURL);
            RetrofitHelper.getInstance()
                    .getRetrofit()
                    .create(Api.class)
                    .compareFace(imgBase64, ugroup)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribeWith(new DisposableSingleObserver<NetResponse>() {

                        @Override
                        public void onSuccess(NetResponse response) {
                            if (!isOpen)
                                return;
                            JSONObject jsonObject;
                            try {
                                String result = response.toString();
                                Log.i(TAG, "Response:" + result);
                                if (response.getStatus() == 1) {
                                    jsonObject = new JSONObject(response.getData().toString());
                                    timerHandler.removeCallbacks(timerRunnable);
                                    faceCompareListener.compareSucess(jsonObject.optString("userId"));
                                    isOpen = false;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    public void closeSession() {
        isOpen = false;
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (null != mImageReader) {
            mImageReader.close();
            mImageReader = null;
        }
        if (null != timerHandler) {
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler = null;
            timerRunnable = null;
        }
        if (null != mCountDownTimer) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }


}
