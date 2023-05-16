package com.example.phonesockettest.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.example.carsockettest.AppPhoneApplication;
import com.example.carsockettest.bean.CallLogBean;
import com.example.carsockettest.bean.ContactBean;
import com.example.carsockettest.bean.LocalMusicBean;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;

public class Util {

    public static int dp2px(float dpValue) {
        float scale = AppPhoneApplication.getContext().getResources()
                .getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(float pxValue) {
        float scale = AppPhoneApplication.getContext().getResources()
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
    public static String ZoneStringToTimeString(String strZone) {
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
    public static String getSerialNumber() {
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
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     */
    public static int getScreenHeight(Context context) {
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
     * 获得本机IP地址，分为两种情况，一是wifi下，二是移动网络下，得到的ip地址是不一样的
     */
    public String getIPAddress(Context context) {
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
     *
     * @param ip
     * @return
     */
    public String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    /**
     * 获取手机通讯录
     */
    public static List<ContactBean> readContacts(Context context) {
        List<ContactBean> contactList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                int i_name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String displayName = cursor.getString(i_name);
                int i_number = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(i_number);
                ContactBean contactBean = new ContactBean();
                contactBean.name = displayName;
                contactBean.number = number;
                contactList.add(contactBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return contactList;
    }




    /***
     * 获取手机通话记录
     * num:要读取的通话记录数量
     */
    public static List<CallLogBean> readCallLogs(Context context, int num) {
        List<CallLogBean> callLogList = new ArrayList<>();
        Cursor cs = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                new String[]{
                        //姓名
                        CallLog.Calls.CACHED_NAME,
                        //号码
                        CallLog.Calls.NUMBER,
                        //呼入/呼出(2)/未接
                        CallLog.Calls.TYPE,
                        //拨打时间
                        CallLog.Calls.DATE,
                        //通话时长
                        CallLog.Calls.DURATION,
                }, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        int i = 0;
        if (cs != null && cs.getCount() > 0) {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date_today = simpleDateFormat.format(date);
            for (cs.moveToFirst(); (!cs.isAfterLast()) && i < num; cs.moveToNext(), i++) {
                //名称
                String callName = cs.getString(0);
                //号码//如果名字为空，在通讯录查询一次有没有对应联系人
                String callNumber = cs.getString(1);
                if (callName == null || callName.equals("")) {
                    //设置查询条件
                    String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME};
                    String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "='" + callNumber + "'";
                    Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            cols, selection, null, null);
                    int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        callName = cursor.getString(nameFieldColumnIndex);
                    }
                    cursor.close();
                }
                //通话类型
                int callType = Integer.parseInt(cs.getString(2));
                String callTypeStr = "";
                switch (callType) {
                    case CallLog.Calls.INCOMING_TYPE:
                        callTypeStr = "CALLIN";
                        break;
                    case CallLog.Calls.OUTGOING_TYPE:
                        callTypeStr = "CALLOUT";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        callTypeStr = "CALLMISS";
                        break;
                    //其他类型的，例如新增号码等记录不算进通话记录里，直接跳过
                    default:
                        Log.e("==callType==", "" + callType);
                        i--;
                        continue;

                }
                //拨打时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date callDate = new Date(Long.parseLong(cs.getString(3)));
                String callDateStr = sdf.format(callDate);
                if (callDateStr.equals(date_today)) { //判断是否为今天
                    sdf = new SimpleDateFormat("HH:mm");
                    callDateStr = sdf.format(callDate);
                } else if (date_today.contains(callDateStr.substring(0, 7))) {
                    //判断是否为当月
                    sdf = new SimpleDateFormat("dd");
                    int callDay = Integer.valueOf(sdf.format(callDate));
                    int day = Integer.valueOf(sdf.format(date));
                    if (day - callDay == 1) {
                        callDateStr = "昨天";
                    } else {
                        sdf = new SimpleDateFormat("MM-dd");
                        callDateStr = sdf.format(callDate);
                    }

                } else if (date_today.contains(callDateStr.substring(0, 4))) {
                    //判断是否为当年
                    sdf = new SimpleDateFormat("MM-dd");
                    callDateStr = sdf.format(callDate);
                }
                //通话时长
                int callDuration = Integer.parseInt(cs.getString(4));
                int min = callDuration / 60;
                int sec = callDuration % 60;
                String callDurationStr = "";
                if (sec > 0) {
                    if (min > 0) {
                        callDurationStr = min + "分" + sec + "秒";
                    } else {
                        callDurationStr = sec + "秒";
                    }
                }

                CallLogBean callLogBean = new CallLogBean();
                callLogBean.callName = TextUtils.isEmpty(callName) ? "未知" : callName;
                callLogBean.callNumber = callNumber;
                callLogBean.callTypeStr = callTypeStr;
                callLogBean.callDateStr = callDateStr;
                callLogBean.callDurationStr = callDurationStr;
                callLogList.add(callLogBean);
            }
        }
        return callLogList;
    }


    /**
     * 保留小数点后n位
     */
    public static String formatDecimal(double value, int n) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(n, RoundingMode.HALF_UP);
        return bd.toString();
    }




    /**
     * 获取本地的音乐信息
     */
    public static List<LocalMusicBean> getLocalMusic(Context context) {
        List<LocalMusicBean> localMusicList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                    if(duration > 0 && size >10){  //时间、大小
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                        String singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                        long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                        LocalMusicBean localMusicBean = new LocalMusicBean();
                        localMusicBean.duration = duration;
                        localMusicBean.size = size;
                        localMusicBean.name = name;
                        localMusicBean.id = id;
                        localMusicBean.singer = singer;
                        localMusicBean.path = path;
                        localMusicBean.albumId = albumId;

                        localMusicList.add(localMusicBean);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return localMusicList;
    }




    public static void callPhone(Context context, String phoneNum){
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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

    /**
     * 用于处理投屏时视频帧的传输
     */
    public static byte[] buildSocketBody(int type, byte[] data){
        byte[] h = ByteBuffer.allocate(4).putInt(SocketConstants.frameHeader).array();   //帧头
        byte[] t = ByteBuffer.allocate(4).putInt(type).array();  //数据类型
        byte[] d = data;  //视频帧数据内容
        byte[] l = ByteBuffer.allocate(4).putInt(d.length).array();  //数据长度
        byte[] e = ByteBuffer.allocate(4).putInt(SocketConstants.frameEnd).array();   //帧尾
        byte[] body = Util.concatAll(h,t,l,d,e);
        return body;
    }


    /**
     * byte数组转int
     */
    public static int byteArrayToInt(byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.BIG_ENDIAN);  //大小端
        return bb.getInt();
    }

}
