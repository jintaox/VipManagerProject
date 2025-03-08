package com.jintao.vipmanager.utils;

import android.content.Context;
import android.os.Environment;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

public class StreamUtils {

    public static String getSdcardDatabasePath() {
        String sdcardDatabasePath =
                Environment.getExternalStorageDirectory().getPath() + "/Android/.vip_user_table.db";
        return sdcardDatabasePath;
    }
    public static String getSdcardUserInfoPath() {
        String sdcardDatabasePath =
                Environment.getExternalStorageDirectory().getPath() + "/Android/.user_manager_info";
        return sdcardDatabasePath;
    }

    public static String getBackupFilePath(Context context) {
        File saveAllFileDir = getSaveAllFileDir(context);
        if (saveAllFileDir != null && saveAllFileDir.exists()) {
            return saveAllFileDir.getPath() + File.separator + AppConfig.BACKUP_DATA_NAME;
        }
        return "";
    }

    public static File getSaveAllFileDir(Context context) {
        File externalAllJsonDir = context.getExternalFilesDir("text");
        if (externalAllJsonDir != null && externalAllJsonDir.exists()) {//先获取sd卡中自身文件目录
            return externalAllJsonDir;
        }else {//不存在，创建外部文件夹
            boolean mkdirsAllJsonDir1 = externalAllJsonDir.mkdirs();
            if (mkdirsAllJsonDir1) {//先sd卡文件目录创建失败，就获取内置存储的目录
                return externalAllJsonDir;
            }else {//创建失败，使用内置存储
                return context.getFilesDir();
            }
        }
    }

    public static String readerFile(String filePath) {
        File file = new File(filePath);
        if (file==null||!file.exists()) {
            return "";
        }
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader buffReader = new BufferedReader(new FileReader(file));
            String line = "";
            while((line = buffReader.readLine())!=null){
                sb.append(line);
            }
            buffReader.close();
            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public static void writeFile(String path, String data) {
        File file = new File(path);
        if (file.exists()) file.delete();
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter writer = null;
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");
            writer = new BufferedWriter(outputStreamWriter);
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (writer!=null) {
                    writer.close();
                }
                if (outputStreamWriter!=null) {
                    outputStreamWriter.close();
                }
                if (fileOutputStream!=null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {

            }
        }
    }
    public static void copyFileStream(File source, File dest){
        try {
            InputStream input = new FileInputStream(source);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest));
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                bos.write(buf, 0, bytesRead);
            }
            input.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重命名文件
     *
     * @param oldPath 原来的文件地址
     * @param newPath 新的文件地址
     */
    public static void renameFile(String oldPath, String newPath) {
        File oleFile = new File(oldPath);
        File newFile = new File(newPath);
        //执行重命名
        oleFile.renameTo(newFile);
    }

    public static ArrayList<String> generatePhoneNum(int size) {
        ArrayList<String> arrayList = new ArrayList<>();
        Random random = new Random();
        // 中国移动号段
        String[] cmccPrefix = {"134", "135", "136", "137", "138", "139", "150", "151", "152", "157", "158", "159", "178", "182", "183", "184", "187", "188"};
        // 中国联通号段
        String[] cuccPrefix = {"130", "131", "132", "145", "155", "156", "166", "175", "176", "185", "186"};
        // 中国电信号段
        String[] ctcPrefix = {"133", "149", "153", "173", "177", "180", "181", "189", "199"};

        for (int i = 0; i < size; i++) {
            String prefix = "";
            int index = random.nextInt(3);
            switch (index) {
                case 0:
                    prefix = cmccPrefix[random.nextInt(cmccPrefix.length)];
                    break;
                case 1:
                    prefix = cuccPrefix[random.nextInt(cuccPrefix.length)];
                    break;
                case 2:
                    prefix = ctcPrefix[random.nextInt(ctcPrefix.length)];
                    break;
            }

            StringBuilder builder = new StringBuilder();
            builder.append(prefix);
            for (int j = 0; j < 8; j++) {
                builder.append(random.nextInt(10));
            }
            arrayList.add(builder.toString());
        }

        return arrayList;
    }
}
