package com.galaxybruce.android.codegenerator.plugin.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

    public static String readFile(Class<?> cls, String filename) {
        InputStream in = null;
        in = cls.getResourceAsStream("/templates/" + filename);
        String content = "";
        try {
            content = new String(readStream(in), "utf-8");
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
            File file = new File(filepath + File.separator + filename);
            if (!file.exists()) {
                file.createNewFile();
            }

//            FileWriter fw = new FileWriter(file.getAbsoluteFile());
//            BufferedWriter bw = new BufferedWriter(fw);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()),"UTF-8"));
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

    public static String makePackageString(String filePath, boolean isKoltin) {
        return "package " + pathToPackage(filePath) + (isKoltin ? "\n" : ";\n");
    }

    public static String pathToPackage(String filePath) {
        final String javaDir = "java" + File.separator;
        int javaIndex = filePath.indexOf(javaDir);
        String packageStr = filePath.substring(javaIndex + javaDir.length());
        if(packageStr.endsWith(File.separator)) {
            packageStr = packageStr.substring(0, packageStr.length() - 1);
        }

        packageStr = packageStr.replace(File.separator, ".");
        return packageStr;
    }

    public static String readPackageName(String javaParentPath) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            byte[] bytes = org.apache.commons.io.FileUtils.readFileToByteArray(new File(javaParentPath + "AndroidManifest.xml"));
            Document doc = db.parse(new InputSource(new ByteArrayInputStream(bytes)));
            NodeList dogList = doc.getElementsByTagName("manifest");
            for (int i = 0; i < dogList.getLength(); i++) {
                Node dog = dogList.item(i);
                Element elem = (Element) dog;
                String str = elem.getAttribute("package");
                if(str != null && !"".equals(str)) {
                    return str;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long fileLength = file.length();
        byte[] fileContent = new byte[fileLength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(fileContent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 复制一个目录及其子目录、文件到另外一个目录
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdirs();
            }
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // 递归复制
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    /**
     * 替换文件中的字符串
     *
     * @param filePath
     * @param oldStr
     * @param replaceStr
     */
    public static void replaceFileText(String filePath, String oldStr, String replaceStr) {
        int len = oldStr.length();
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder buf = new StringBuilder();

            // 替换所有匹配的字符串
            for (String temp = null; (temp = br.readLine()) != null; temp = null) {
                if (temp.contains(oldStr)) {
                    temp = temp.replace(oldStr, replaceStr);
                }
                buf.append(temp);
                buf.append(System.getProperty("line.separator"));
            }

            br.close();
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
