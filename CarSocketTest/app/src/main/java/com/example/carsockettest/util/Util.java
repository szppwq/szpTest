package com.example.carsockettest.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.carsockettest.AppCarApplication;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;

public class Util {

    public static int dp2px(float dpValue) {
        float scale = AppCarApplication.getContext().getResources()
                .getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(float pxValue) {
        float scale = AppCarApplication.getContext().getResources()
                .getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getSystemTime() {
        return transferLongToDate("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()) + "";
    }

    public static String getSystemTimeHHmm() {
        return transferLongToDate("HH:mm", System.currentTimeMillis()) + "";
    }

    /**
     * 把毫秒转化成日期
     */
    public static String transferLongToDate(String dateFormat, Long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    /**
     * 将毫秒转时分秒
     */
    public static String generateTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date(time);
        return sdf.format(date);
    }

    /**
     * 带时区字符串转Data
     */
    public static Date ZoneStringToDate(String strTime) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = formatter.parse(strTime);
        return date;
    }

    /**
     * 带时区字符串去掉时区
     */
    public static String ZoneStringToTimeString(String strZone){
        Date date = null;
        try {
            date = ZoneStringToDate(strZone);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 算Android设备的一个唯一标志符
     */
    public static String getSerialNumber(){
        return Build.SERIAL;
    }

    /**
     * 获取手机型号
     */
    public static String getMobileModel() {
        return Build.MODEL;
    }


    /**
     * 获得屏幕宽度
     */
    public static int getScreenWidth(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     */
    public static int getScreenHeight(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获取应用程序VersionCode
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取应用程序versionName
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Base64字符串转Bitmap
     */
    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * 保留小数点后n位
     */
    public static String formatDecimal(double value, int n) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(n, RoundingMode.HALF_UP);
        return bd.toString();
    }


    /**获得本机IP地址，分为两种情况，一是wifi下，二是移动网络下，得到的ip地址是不一样的*/
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //调用方法将int转换为地址字符串
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }
    /**
     * 将得到的int类型的IP转换为String类型
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }






    /**
     * 本机连接wifi热点，获取wifi热点的ip地址
     */
    public static String getWifiIp(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return null;
        }
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            return null;
        }
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        DhcpInfo dhcpinfo = wifiManager.getDhcpInfo();
        String serverAddress = getCorrectIPAddress(dhcpinfo.serverAddress);

        return serverAddress;
    }
    /**
     * 将获取的int转为真正的ip地址
     **/
    private static String getCorrectIPAddress(int iPAddress) {
        StringBuilder sb = new StringBuilder();
        sb.append(iPAddress & 0xFF).append(".");
        sb.append((iPAddress >> 8) & 0xFF).append(".");
        sb.append((iPAddress >> 16) & 0xFF).append(".");
        sb.append((iPAddress >> 24) & 0xFF);
        return sb.toString();
    }










    /**
     * 删除开头字符串
     */
    public static String trimStart(String str, String prefix) {
        if (str.startsWith(prefix)) {
            return (str.substring(prefix.length()));
        }
        return str;
    }

    /**
     * 删除末尾字符串
     */
    public static String trimEnd(String str, String suffix) {
        if (str.endsWith(suffix)) {
            return (str.substring(0,str.length()-suffix.length()));
        }
        return str;
    }


    /**
     * byte数组转int
     */
    public static int byteArrayToInt(byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.BIG_ENDIAN);  //大小端
        return bb.getInt();
    }


    /**
     * 把多个byte数组根据入参顺序合并为一个byte数组
     * first：第一个数组
     */
    public static byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }


    /**
     * 用于Socket应用层传输的数据帧封装
     * 格式:帧头-数据类型-数据长度-数据内容-帧尾
     * 帧头:6BEE8CA0 (固定的数据，十六进制两位占一个字节，一共四个字节)
     * 帧尾:8D04A8B2 (固定的数据，十六进制两位占一个字节，一共四个字节)
     * 数据类型:一共四个字节，由实际的类型确定一个值
     * 数据长度:一共四个字节，有实际的数据长度确定
     * 数据内容:实际的类容转化为byte
     * 例：6BEE8CA0AAAAAAAABBBBBBBBCCCCCCCCCCCCCCCCCCCCCCC8D04A8B2
     */
    public static byte[] buildSocketBody(int type, String str){
        byte[] h = ByteBuffer.allocate(4).putInt(SocketConstants.frameHeader).array();   //帧头
        byte[] t = ByteBuffer.allocate(4).putInt(type).array();  //数据类型
        byte[] d = str.getBytes(Charset.defaultCharset());  //数据内容
        byte[] l = ByteBuffer.allocate(4).putInt(d.length).array();  //数据长度
        byte[] e = ByteBuffer.allocate(4).putInt(SocketConstants.frameEnd).array();   //帧尾

        byte[] body = Util.concatAll(h,t,l,d,e);

        return body;
    }

}
