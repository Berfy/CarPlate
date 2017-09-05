package cn.berfy.framework.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.berfy.framework.common.Constants;

/**
 * 创建文件 删除文件 下载文件
 *
 * @author yuepengfei
 */
public class FileUtils {

    private static final String FD_FLASH = "-ext";
    private static long filesize = 0;
    public static boolean mBl_stop = false;

    /**
     * 下载文件
     *
     * @param loadUrl
     * @param sdcardPath
     * @param saveName
     * @return
     */
    public static boolean download(String loadUrl, String sdcardPath,
                                   String saveName) {
        try {
            URL url = new URL(loadUrl);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(sdcardPath + saveName);
            byte data[] = new byte[1024];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
                output.flush();
            }
            output.close();
            input.close();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static void downLoadImage(final Context context, final String imageURl, final OnDownloadImageListener onDownloadImageListener) {
        Constants.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(imageURl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != onDownloadImageListener)
                                onDownloadImageListener.finish(bitmap);
                        }
                    });
                } catch (final Exception e) {
                    // TODO: handle exception
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != onDownloadImageListener)
                                onDownloadImageListener.error(e.getMessage());
                        }
                    });
                }
            }
        });
    }

    public interface OnDownloadImageListener {
        void error(String error);

        void finish(Bitmap bitmap);
    }

    /**
     * 将bitmap存为字节
     *
     * @param character 品质 1-100
     */
    public static byte[] bitmap2Bytes(Bitmap bm, int character) {
        if (bm != null) {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, character, bas);
            byte[] byteArray = bas.toByteArray();
            try {
                bas.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return byteArray;
        }
        return null;
    }

    /**
     * 保存照相图片到系统默认相册
     *
     * @param path   :图片名称
     * @param bitmap :图片对象
     * @return
     */
    public static String saveBitmapToFile(String path, Bitmap bitmap) {
        try {
            String status = Environment.getExternalStorageState();
            if (status.equals(Environment.MEDIA_MOUNTED)) {
                // 将图片保存到系统默认相册
                File myFile = new File(path);
                if (!myFile.exists()) {
                    myFile.createNewFile();
                }
                FileOutputStream fOut = null;
                fOut = new FileOutputStream(myFile);
                int jiema = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, jiema, fOut);
                fOut.flush();
                fOut.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * @param context
     * @param dir
     * @param url
     * @return
     */

    // dir为路径如�?aa/bb）url为下载地�?
    public static String getFilePath(Context context, String dir, String url) {
        String filename = (url.substring((url.lastIndexOf("/") + 1)));
        if (!dir.startsWith("/", dir.length() - 1)) {
            dir = dir + "/";
        }
        return getFilePath(context, dir + filename);
    }

    // derectory为想用存储的完整自定义路径如(/aa/bb/cc.apk)
    // 此方法可以判断是存储在sdk下还是data/data/包名
    public static String getFilePath(Context context, String derectory) {
        // if (TextUtils.isEmpty(derectory)) {
        // return "";
        // }

        if (derectory.startsWith("/")) {
            derectory = derectory.substring(derectory.indexOf("/") + 1);
        }

        String path = "";
//        if (avaiableMedia(context) || !Environment.isExternalStorageRemovable()) {
//            path = context.getExternalCacheDir().getPath();
//        }
//        else {
//            path = context.getCacheDir().getPath();
//        }
        if (avaiableMedia(context)) {
            if (isHasFlashMemory()) {
                path = Environment.getExternalStorageDirectory().getPath()
                        + FD_FLASH + "/" + derectory;
            } else {
                path = Environment.getExternalStorageDirectory().getPath()
                        + "/" + derectory;
            }
        } else {
            if (null != context) {
                path = context.getCacheDir().getPath() + "/" + derectory;
            }
        }
        return path;
    }

    /**
     * 判断位置可用空间
     *
     * @param path
     * @return
     */
    public static long getAvaiableSize(String path) {
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long blockCount = stat.getBlockCount();
        return blockCount * blockSize;
    }

    /**
     * 获取文件夹大小 包含里面文件
     */
    public static long getDirSize(String dir) {
        filesize = 0;
        File file = new File(dir);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
                getDirFileSize(file2, filesize);
            }
        } else if (file.isFile()) {
            filesize = getFileSize(dir);
        }
        return filesize;
    }

    private static void getDirFileSize(File dir, long size) {
        File[] files = dir.listFiles();
        for (File file2 : files) {
            if (file2.isFile()) {
                filesize += getFileSize(file2.getAbsolutePath());
                LogUtil.e("文件大小",
                        "==========>" + getFileSize(file2.getAbsolutePath()));
            } else if (file2.isDirectory()) {
                getDirFileSize(file2, size);
            }
        }
    }

    /**
     * 获取文件大小
     *
     * @param path
     * @return
     */
    public static long getFileSize(String path) {
        File file = new File(path);
        return file.length();
    }

    /**
     * @return
     */
    public static boolean avaiableMedia(Context context) {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            ToastUtil.getInstance().showToast("没有内存卡");
            return false;
        }
    }

    /**
     * @return
     */
    private static boolean isHasFlashMemory() {
        String path = Environment.getExternalStorageDirectory().getPath()
                + FD_FLASH;
        File file = new File(path);
        if (file.exists()) {
            if (getAvaiableSize(path) == 0) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    // 创建文件
    public static boolean createFile(File file) {
        LogUtil.i("创建文件", "===========>" + file.getPath());
        try {
            if (file.exists()) {
                return true;
            } else {
                if (file.getParentFile().isDirectory()) {
                    file.getParentFile().mkdirs();
                }
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }

            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void saveFile(Context context, String content) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(getFilePath(context, DeviceUtil.getAppName(context) + "error.txt"), true));//主要就是这个true
            out.newLine();
            out.write(TimeUtil.getCurrentTime() + "====>" + content);
            out.close();
        } catch (IOException e) {
        }
    }

    // 只创建文�?
    public static boolean createFolder(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return true;
            } else {
                File parentFile = file.getParentFile();
                LogUtil.d("目录地址", "========>" + parentFile);
                if (!parentFile.exists()) {
                    LogUtil.d("目录", "========>" + parentFile);
                    parentFile.mkdirs();
                }
                file.mkdir();
            }
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // 只创建文�?
    public static boolean createFolder(File file) {
        try {
            if (file.exists()) {
                return true;
            } else {
                File parentFile = file.getParentFile();
                LogUtil.d("目录地址", "========>" + parentFile);
                if (!parentFile.exists()) {
                    LogUtil.d("目录", "========>" + parentFile);
                    parentFile.mkdirs();
                }
                file.mkdir();
            }
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // 创建文件并写入字节
    public static boolean createFile(File file, byte[] buffer) {
        if (null == file) {
            return false;
        }
        if (createFile(file)) {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                fos.write(buffer);
                fos.flush();
                fos.close();
                buffer = null;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 文件存储 创建文件并写入流
     */
    public static boolean createFile(Context context, File file, InputStream inputStream) {
        try {
            if (createFile(file)) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    byte buffer[] = new byte[1024];
                    while (true) {
                        int stream = inputStream.read(buffer);
                        if (stream == -1) {
                            break;
                        }
                        fos.write(buffer, 0, stream);
                    }
                    fos.flush();
                    fos.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param tag 做判断标记，可以传空 创建文件监听进度
     */
    public static boolean createFile(Context ctx, final String tag, File file,
                                     InputStream inputStream,
                                     final DownLoadFileInterface downLoadFileInterface, int TotalCount) {
        if (createFile(file)) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                byte buffer[] = new byte[1024];
                int count = 0;
                int downloadSize = 0;
                long totalLength = Long.parseLong(TotalCount + "");
                mBl_stop = false;
                while (true) {
                    if (mBl_stop) {
                        fos.flush();
                        fos.close();
                        inputStream.close();
                    } else {
                        int stream = inputStream.read(buffer);
                        count = stream;
                        if (stream == -1) {
                            break;
                        }
                        fos.write(buffer, 0, stream);
                        downloadSize += count;
                        int progressSize = (int) ((downloadSize * 0.1)
                                / (totalLength * 0.1) * 100);
                        if (progressSize >= 100) {
                            progressSize = 100;
                        }
                        final int pro = progressSize;
                        ((Activity) ctx).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                downLoadFileInterface.onUpdate(tag, pro);
                            }
                        });
                    }
                    inputStream.close();
                }
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
                ((Activity) ctx).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        downLoadFileInterface.onUpdate(tag, -1);
                    }
                });
                return false;
            }
        }
        return true;
    }

    /**
     * 判断文件存在
     */
    public static boolean exists(String imagePath) {
        if (imagePath != null && !imagePath.equals("")) {
            File file = new File(imagePath);
            return exists(file);
        }
        return false;
    }

    /**
     * 判断文件存在
     */
    public static boolean exists(File file) {
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static byte[] streamToBytes(FileInputStream is) {
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
        } catch (Exception e) {
        } catch (OutOfMemoryError e2) {
            System.gc();
        }
        return os.toByteArray();
    }

    /**
     * 删除指定文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        try {
            if (file.exists())
                return file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除文件�?
     *
     * @param path
     * @return
     */
    public static void deleteAllFile(String path) {
        LogUtil.d("删除文件", "=========>" + path);
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                deleteAllFile(path + "/" + tempList[i]);
                deleteFolder(path + "/" + tempList[i]);
            }
        }
    }

    /**
     * 只搜索根目录
     *
     * @param searchPath 要搜索的目录
     * @param searchKey  查询关键�?搜索包含关键字的文件)
     */
    public static ArrayList<File> searchFile(Context context,
                                             String searchPath, String searchKey) {
        ArrayList<File> files = new ArrayList<File>();
        File dirFile = new File(searchPath);
        if (null != dirFile.list()) {
            for (String path : dirFile.list()) {
                File file = new File(FileUtils.getFilePath(context, path));
                if (!file.isDirectory() && file.getPath().contains(searchKey)) {
                    files.add(file);
                }
            }
        }
        return files;

    }

    public static void deleteFolder(String folderPath) {
        try {
            deleteAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 通知系统sd卡有新文件，请刷新 扫描指定文件，刷新后系统可识别出来，包括相册
     *
     * @param path 文件地址
     */
    public static void notifyFile(Context context, String path) {
        Uri localUri = Uri.fromFile(new File(path));
        Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                localUri);
        context.sendBroadcast(localIntent);
    }

    public static String getSportJson(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("sports.json");
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            inputReader.close();
            inputStream.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static File createTmpFile(Context context) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 已挂载
            File pic = Environment.getExternalStorageDirectory();//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "multi_image_" + timeStamp + "";
            File tmpFile = new File(pic, fileName + ".jpg");
            return tmpFile;
        } else {
            File cacheDir = context.getCacheDir();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "multi_image_" + timeStamp + "";
            File tmpFile = new File(cacheDir, fileName + ".jpg");
            return tmpFile;
        }

    }
}
