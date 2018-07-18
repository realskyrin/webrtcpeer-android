//package fi.vtt.nubomedia.webrtcpeerandroid;
//
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.graphics.ImageFormat;
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraManager;
//import android.os.Build;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.webrtc.CameraEnumerationAndroid;
//import org.webrtc.CameraEnumerator;
//import org.webrtc.Logging;
//
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Iterator;
//import java.util.List;
//
//@TargetApi(Build.VERSION_CODES.LOLLIPOP)
//public class Camera2EnumerationAndroid {
//    private static final String TAG = "Camera2EnumerationAndroid";
//    private static CameraEnumerationAndroid.Enumerator enumerator = new CameraEnumerator();
//    private final CameraManager cameraManager;
//
//    public Camera2EnumerationAndroid(Context context) {
//        cameraManager = (CameraManager)context.getSystemService("camera");
//    }
//
//    public static synchronized void setEnumerator(CameraEnumerationAndroid.Enumerator enumerator) {
//        enumerator = enumerator;
//    }
//
//    public static synchronized List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats(int cameraId) {
//        return enumerator.getSupportedFormats(cameraId);
//    }
//
//    public String[] getDeviceNames() throws CameraAccessException {
//        String[] names = cameraManager.getCameraIdList();
//
//        for(int i = 0; i < cameraManager.getCameraIdList().length; ++i) {
//            names[i] = getDeviceName(i);
//        }
//        return names;
//    }
//
//    public int getDeviceCount() {
//        return Camera.getNumberOfCameras();
//    }
//
//    public static String getDeviceName(int index) {
//        Camera.CameraInfo info = new CameraInfo();
//
//        try {
//            Camera.getCameraInfo(index, info);
//        } catch (Exception var3) {
//            Logging.e("CameraEnumerationAndroid", "getCameraInfo failed on index " + index, var3);
//            return null;
//        }
//
//        String facing = info.facing == 1 ? "front" : "back";
//        return "Camera " + index + ", Facing " + facing + ", Orientation " + info.orientation;
//    }
//
//    public String getNameOfFrontFacingDevice() {
//        return getNameOfDevice(1);
//    }
//
//    public String getNameOfBackFacingDevice() {
//        return getNameOfDevice(0);
//    }
//
//    public String getSupportedFormatsAsJson(int id) throws JSONException {
//        List<CameraEnumerationAndroid.CaptureFormat> formats = getSupportedFormats(id);
//        JSONArray json_formats = new JSONArray();
//        Iterator i$ = formats.iterator();
//
//        while(i$.hasNext()) {
//            CameraEnumerationAndroid.CaptureFormat format = (CameraEnumerationAndroid.CaptureFormat)i$.next();
//            JSONObject json_format = new JSONObject();
//            json_format.put("width", format.width);
//            json_format.put("height", format.height);
//            json_format.put("framerate", (format.maxFramerate + 999) / 1000);
//            json_formats.put(json_format);
//        }
//
//        Logging.d("CameraEnumerationAndroid", "Supported formats for camera " + id + ": " + json_formats.toString(2));
//        return json_formats.toString();
//    }
//
//    public int[] getFramerateRange(Parameters parameters, final int framerate) {
//        List<int[]> listFpsRange = parameters.getSupportedPreviewFpsRange();
//        if (listFpsRange.isEmpty()) {
//            Logging.w("CameraEnumerationAndroid", "No supported preview fps range");
//            return new int[]{0, 0};
//        } else {
//            return (int[]) Collections.min(listFpsRange, new CameraEnumerationAndroid.ClosestComparator<int[]>() {
//                int diff(int[] range) {
//                    int maxFpsWeight = true;
//                    return range[0] + 10 * Math.abs(framerate - range[1]);
//                }
//            });
//        }
//    }
//
//    public Size getClosestSupportedSize(List<Size> supportedSizes, final int requestedWidth, final int requestedHeight) {
//        return (Size)Collections.min(supportedSizes, new CameraEnumerationAndroid.ClosestComparator<Size>() {
//            int diff(Size size) {
//                return Math.abs(requestedWidth - size.width) + Math.abs(requestedHeight - size.height);
//            }
//        });
//    }
//
//    private String getNameOfDevice(int facing) {
//        CameraInfo info = new CameraInfo();
//
//        for(int i = 0; i < Camera.getNumberOfCameras(); ++i) {
//            try {
//                Camera.getCameraInfo(i, info);
//                if (info.facing == facing) {
//                    return getDeviceName(i);
//                }
//            } catch (Exception var4) {
//                Logging.e("CameraEnumerationAndroid", "getCameraInfo() failed on index " + i, var4);
//            }
//        }
//
//        return null;
//    }
//
//    private abstract static class ClosestComparator<T> implements Comparator<T> {
//        private ClosestComparator() {
//        }
//
//        abstract int diff(T var1);
//
//        public int compare(T t1, T t2) {
//            return this.diff(t1) - this.diff(t2);
//        }
//    }
//
//    public static class CaptureFormat {
//        public final int width;
//        public final int height;
//        public final int maxFramerate;
//        public final int minFramerate;
//        public final int imageFormat = 17;
//
//        public CaptureFormat(int width, int height, int minFramerate, int maxFramerate) {
//            this.width = width;
//            this.height = height;
//            this.minFramerate = minFramerate;
//            this.maxFramerate = maxFramerate;
//        }
//
//        public int frameSize() {
//            return frameSize(this.width, this.height, 17);
//        }
//
//        public static int frameSize(int width, int height, int imageFormat) {
//            if (imageFormat != 17) {
//                throw new UnsupportedOperationException("Don't know how to calculate the frame size of non-NV21 image formats.");
//            } else {
//                return width * height * ImageFormat.getBitsPerPixel(imageFormat) / 8;
//            }
//        }
//
//        public String toString() {
//            return this.width + "x" + this.height + "@[" + this.minFramerate + ":" + this.maxFramerate + "]";
//        }
//
//        public boolean isSameFormat(CameraEnumerationAndroid.CaptureFormat that) {
//            if (that == null) {
//                return false;
//            } else {
//                return this.width == that.width && this.height == that.height && this.maxFramerate == that.maxFramerate && this.minFramerate == that.minFramerate;
//            }
//        }
//    }
//
//    public interface Enumerator {
//        List<Camera2EnumerationAndroid.CaptureFormat> getSupportedFormats(int var1);
//    }
//}
