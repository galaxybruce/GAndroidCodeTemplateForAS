package com.galaxybruce.util;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {

    public static String readFile(Class<?> cls, String filename) {
        InputStream in = null;
        in = cls.getResourceAsStream("/templates/" + filename);
        String content = "";
        try {
            content = new String(readStream(in));
        } catch (Exception e) {
        }
        return content;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
                System.out.println(new String(buffer));
            }

        } catch (IOException e) {
        } finally {
            outSteam.close();
            inStream.close();
        }
        return outSteam.toByteArray();
    }

    public static void writeToFile(String content, String filepath, String filename) {
        try {
            File folder = new File(filepath);
            // if file doesnt exists, then create it
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = new File(filepath + "/" + filename);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 驼峰法转下划线
     * @param line 源字符串
     * @return 转换后的字符串
     */
    public static String camelToUnderline(String line){
        if (line == null || "".equals(line)) {
            return "";
        }
        line = String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(word.toUpperCase());
            sb.append(matcher.end() == line.length() ? "" : "_");
        }
        return sb.toString().toLowerCase();
    }

    public static String makePackageString(String filePath) {
        return "package " + pathToPackage(filePath) + ";\n";
    }

    public static String pathToPackage(String filePath) {
        int javaIndex = filePath.indexOf("java/");
        String packageStr = filePath.substring(javaIndex + "java/".length());
        if(packageStr.endsWith(File.separator)) {
            packageStr = packageStr.substring(0, packageStr.length() - 1);
        }
        packageStr = packageStr.replaceAll(File.separator, ".");
        return packageStr;
    }

}
