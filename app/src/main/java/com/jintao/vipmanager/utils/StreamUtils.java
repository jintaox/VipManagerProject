package com.jintao.vipmanager.utils;

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

    public static String readerFile(String path) {
        File file = new File(path);
        StringBuilder result = new StringBuilder();
        try {
            //构造一个BufferedReader类来读取文件
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            //使用readLine方法，一次读一行
            while ((s = br.readLine()) != null) {
                result.append(System.lineSeparator() + s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static void writeFile(String path, String data) {
        File file = new File(path);
        if (file.exists()) file.delete();
        try {
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
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
