package util;

import com.google.common.hash.Hashing;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.*;


import static java.util.regex.Pattern.compile;

/**
 * Created by zn on 2018/5/15.
 */
@Slf4j
public class Utils extends StringUtils {

    /**
     * 将字符串加密成32位MD5并返回。
     *
     * @param value
     * @return
     */
    public static String MD5(String value) {
        return Hashing.md5().hashBytes(value.getBytes()).toString();
    }

    /**
     * 获得url里参数列表
     *
     * @param url
     * @return
     */
    public static Map<String, String> getParametersByUrl(String url) {
        Map<String, String> map = new HashMap<>();
        if (!url.contains("?")) {
            return null;
        }
        String tmpUrl = url.substring(url.indexOf("?") + 1);
        String[] arrays = tmpUrl.split("&");
        for (String parameter : arrays) {
            String[] key = parameter.split("=");
            if (key.length >= 2) {
                String name = key[0];
                String value = key[1];
                map.put(name, value);
            }
        }
        return map;
    }

    /**
     * 返回2位小数精度的double。
     *
     * @param value
     * @return
     */
    public static double scaleDouble(double value) {
        BigDecimal decimal = new BigDecimal(value);
        value = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return value;
    }

//    public static String removeNodeAllAttribute(String nodeString) {
//        nodeString = nodeString.replaceAll("/<([a-zA-Z1-6]+)(\\s*[^>]*)?>/g", "");
//        return nodeString;
//    }
//
//    public static void parseEbayAjaxResponse(String responseJson) {
//        responseJson = responseJson.replaceAll("\r\n","");
//        responseJson = responseJson.replaceAll("\t","");
//
//    }

    public static void save2File(String content, String filePath, boolean append) {
        FileWriter writer = null;
        try {
            File file = new File(filePath);
            log.info("writer file:{}", file.getAbsolutePath());
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (append) {
                writer = new FileWriter(file, append);
            } else {
                if (file.exists()) {
                    file.delete();
                }
                writer = new FileWriter(file);
            }
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 替换图片大小。将产品的图替换成600宽。
     *
     * @param picUrl：https://i.ebayimg.com/images/g/TE4AAOSwo4pYEctQ/s-l100.jpg
     * @return https://i.ebayimg.com/images/g/TE4AAOSwo4pYEctQ/s-l600.jpg
     */
    public static String replacePicSize600(String picUrl) {
        //String pName = picUrl.substring(picUrl.lastIndexOf("/") + 1);
        String replace = picUrl.replaceAll("/s-l\\d+", "/s-l600");
        //picUrl = picUrl.replaceAll(pName,replace);
        return replace;
    }


    public static String getNotParameterURL(String url) {
        if (url.contains("?")) {
            String tmpUrl = url.substring(0, url.indexOf("?"));
            return tmpUrl;
        } else {
            return url;
        }
    }

    public static String buliderURL(String notParameterURL, Map<String, String> parameters) {
        String url = notParameterURL;
        String parameter = "";
        Iterator<String> iterator = parameters.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = parameters.get(key);
            if (!parameter.contains("?")) {
                parameter += "?";
            } else {
                parameter += "&";
            }
            parameter += key + "=" + value;
        }
        url += parameter;

        return url;
    }


    /**
     * 将类似这样的价格:USD $27.99变成 27.99 .只保留数字部分.
     *
     * @param price
     * @return 如果字符串为空白字符或null，返回原字符串。 否则返回替换后的数字价格
     */
    public static String leaveThePrice(String price) {
        if (isNotBlank(price)) {
            String priceRexp = "[^0-9\\.]";
            price = price.replaceAll(priceRexp, "");
        }
        return price;
    }

    /**
     * 修剪字符串里所有的回车换行　\r\n.如果需要去除字符串里所有的空白符（含\r\n），请使用
     *
     * @param str
     * @return 如果字符串为空白字符或null，返回原字符串
     */
    public static String stripNewLine(String str) {
        if (isNotBlank(str)) {
            Pattern p = compile("\r|\n|\t");
            Matcher m = p.matcher(str);
            str = m.replaceAll("");
        }
        return str;
    }

    /**
     * 去除字符串里所有的空白符，包括\r\n\t等
     *
     * @param str
     * @return 如果字符串为空白字符或null，返回原字符串
     */
    public static String stripBlank(String str) {
        if (isNotBlank(str)) {
            //Pattern p = compile("\\s*");
            Pattern p = compile("[\r\n\t]*");
            Matcher m = p.matcher(str);
            str = m.replaceAll("");
        }
        return str;
    }

    /**
     * 删除字符串里[out of stock](含中括号)的内容
     *
     * @param str
     * @return 如果字符串为空白字符或null，返回原字符串
     */
    public static String stripOutOfStock(String str) {
        if (StringUtils.isNotBlank(str)) {
            str = str.replaceAll("\\[out of stock\\]", "");
        }
        return str;
    }

    /**
     * 去除html 注释
     *
     * @param str
     * @return 如果字符串为空白字符或null，返回原字符串
     */
    public static String stripHTMLAnnotation(String str) {
        if (isNotBlank(str)) {
            String regExp = "<\\!--.*-->";//html注释
            Pattern pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(str);
            str = matcher.replaceAll("");
        }

        return str;
    }

    public static String stripScript(String str) {
        if (isNotBlank(str)) {
            String regExp = "<script[^>]*?>[\\s\\S]*?<\\/script>";  //脚本
            Pattern pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(str);
            str = matcher.replaceAll("");
        }
        return str;
    }


    /**
     * 删除节点之间的空格。
     *
     * @param str
     * @return
     */
    public static String stripTagsSpace(String str) {
        String reg = ">\\s+([^\\s<]*)\\s+<";
        str = str.replaceAll(reg, ">$1<");
        return str;
    }
//    public static String stripCSS(String str) {
//        if(isNotBlank(str)) {
//            String regExp = "<style[^>]*?>[\\\\s\\\\S]*?<\\\\/style>";  //脚本
//            Pattern pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
//            Matcher matcher = pattern.matcher(str);
//            str = matcher.replaceAll("");
//        }
//        return str;
//    }

    /**
     * 使用gzip进行压缩
     */

    public static String gZip(String primStr) {
        if (primStr == null || primStr.length() == 0) {
            return primStr;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(primStr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return new sun.misc.BASE64Encoder().encode(out.toByteArray());
    }

    /**
     * <p>Description:使用gzip进行解压缩</p>
     *
     * @param compressedStr
     * @return
     */
    public static String gunzip(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        byte[] compressed = null;
        String decompressed = null;
        try {
            compressed = new sun.misc.BASE64Decoder().decodeBuffer(compressedStr);
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }

        return decompressed;
    }

    /**
     * 使用zip进行压缩
     *
     * @param str 压缩前的文本
     * @return 返回压缩后的文本
     */
    public static final String zipString(String str) {
        if (str == null) {
            return null;
        }
        byte[] compressed;
        ByteArrayOutputStream out = null;
        ZipOutputStream zout = null;
        String compressedStr = null;
        try {
            out = new ByteArrayOutputStream();
            zout = new ZipOutputStream(out);
            zout.putNextEntry(new ZipEntry("0"));
            zout.write(str.getBytes());
            zout.closeEntry();
            compressed = out.toByteArray();
            compressedStr = new sun.misc.BASE64Encoder().encodeBuffer(compressed);
        } catch (IOException e) {
            compressed = null;
        } finally {
            if (zout != null) {
                try {
                    zout.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return compressedStr;
    }

    /**
     * 将单个文件压缩到zip文件里
     *
     * @param srcFile
     * @param descFile
     */
//    public static void singleFileTozip(String srcFile, String descFile) {
//        File src = new File(srcFile);
//        if (!src.exists()) {
//            log.error("file {} is not found");
//            return;
//        } else {
//            String fileName = srcFile.substring(srcFile.lastIndexOf("/") + 1);
//            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(descFile));
//                 FileInputStream inputStream = new FileInputStream(src);) {
//                zipOutputStream.putNextEntry(new ZipEntry(fileName));
//
//                byte[] data = new byte[1];
//                while (inputStream.read(data) != -1) {
//                    zipOutputStream.write(data);
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 将多文件压缩到一个zip文件里
     *
     * @param descFile
     * @param srcFile  需要压缩的文件
     */
    public static void zip(String descFile, String... srcFile) {
        long start = System.currentTimeMillis();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(descFile));) {
            for (String file : srcFile) {
                File src = new File(file);
                String fileName = file.substring(file.lastIndexOf(File.separator) + 1);
                zipOutputStream.putNextEntry(new ZipEntry(fileName));
                try (FileInputStream inputStream = new FileInputStream(src);) {
                    byte[] data = new byte[(int)src.length()];
                    while (inputStream.read(data) != -1) {
                        zipOutputStream.write(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("time : " + (end - start) + "毫秒");
    }

    /**
     * 使用zip进行解压缩
     *
     * @param compressedStr 压缩后的文本
     * @return 解压后的字符串
     */
    public static final String unzip(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }
        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        ZipInputStream zin = null;
        String decompressed = null;
        try {
            byte[] compressed = new sun.misc.BASE64Decoder().decodeBuffer(compressedStr);
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(compressed);
            zin = new ZipInputStream(in);
            zin.getNextEntry();
            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = zin.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            decompressed = null;
        } finally {
            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return decompressed;
    }

    /**
     * 根据数据库的完整名创建对应的目录
     *
     * @param fullDbNames 格式为： server_id.db_name
     */
    public static void mkdirsByFullDBName(String rootPath, String... fullDbNames) {
        if (rootPath.lastIndexOf("/") == rootPath.length()) {
            rootPath = rootPath.substring(0, rootPath.lastIndexOf("/"));
        }
        for (String fullDbName : fullDbNames) {
            String parent = fullDbName.substring(0, fullDbName.indexOf("."));
            String childer = fullDbName.substring(fullDbName.indexOf(".") + 1);
            String fulPath = rootPath + "/" + parent + "/" + childer;

            File file = new File(fulPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    /**
     * 拿到 db 导出数据下的文件。
     *
     * @param folder
     * @return String : db_server.db_name.
     * List : 具体某个db_name 下的 zip文件
     * 目录结构 db_server
     * |_ db_name
     * |_db_server-db_name-time-s-e.zip
     * |_ db_name
     * |_db_server-db_name-time-s-e.zip
     */
    public static Map<String, List<File>> getZipFiles(String folder) {
        File file = new File(folder);
        Map<String, List<File>> map = new HashMap<>();
        if (file.isDirectory()) {
            File[] dbServers = file.listFiles();// db_server
            for (File s : dbServers) {
                if (s.isDirectory()) {
                    File[] dbNames = s.listFiles();// db_name
                    for (File f : dbNames) {
                        if (f.isDirectory()) {
                            String key = s.getName() + "." + f.getName();
                            File[] zips = f.listFiles(new FileFilter() {
                                @Override
                                public boolean accept(File pathname) {
                                    String fileName = pathname.getName();
                                    return pathname.isFile() && fileName.endsWith("zip");
                                }
                            });
                            List<File> list = map.get(key);
                            if (list == null) {
                                list = new ArrayList<>();
                            }
                            for (File zip : zips) {
                                list.add(zip);
                            }
                            map.put(key, list.stream().sorted((f1,f2)->{
                                Long d1 = f1.lastModified();
                                Long d2 = f2.lastModified();
                                return d2.compareTo(d1);
                            }).collect(Collectors.toList()));
                        }
                    }
                }
            }
        }
        return map;
    }

    public static List<File> csvFiles(String csvFolder) {
        File file = new File(csvFolder);
        List<File> fileList = new ArrayList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile() && pathname.getName().endsWith("csv");
                }
            });
            for (File f : files) {
                fileList.add(f);
            }
        }
        fileList = fileList.stream().sorted((f1, f2) -> {
            Long d1 = f1.lastModified();
            Long d2 = f2.lastModified();
            return d2.compareTo(d1);
        }).collect(Collectors.toList());

        return fileList;
    }

    public static List<File> allFiles(String folder) {
        File file = new File(folder);
        List<File> fileList = new ArrayList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                fileList.add(f);
            }
        } else {
            try {
                throw new Exception("error: folder -> " + folder + " is not folder!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fileList = fileList.stream().sorted((f1, f2) -> {
            Long d1 = f1.lastModified();
            Long d2 = f2.lastModified();
            return d2.compareTo(d1);
        }).collect(Collectors.toList());
        return fileList;
    }

    /**
     * 根据完整的数据库名，返回一个相对路径。路径最左边没有/,最右边有/,但/根据不同的系统，采用不同的符号，如windows上就是\。如 docker-1/db20/
     *@param fullDBName: 格式 server_id.db_name.如 doceker-1.db20
     */
    public static String getRelativeFilePathByFullDBName(String fullDBName) {
        String server = fullDBName.substring(0,fullDBName.indexOf("."));
        String dbName = fullDBName.substring(fullDBName.indexOf(".") + 1);

        return server + File.separator + dbName + File.separator;

    }
    /**
     *
     * @param zipFolder zip文件所在的目录，必须要有/结尾。 如C:/export/docker-1/db1_1/
     * @param csvFiles 需要压缩的csv文件集合
     * @param rows: 多少个文件压缩成一个zip文件，如果 < 1，则默认为50.
     *            zip 文件默认已0...n.zip命名
     *
     */
    public static void csvToZip(String zipFolder,List<File> csvFiles,String fullDbName,int rows) {
        if(csvFiles == null || csvFiles.size() < 1) {
            return;
        }
        File zipFile = new File(zipFolder);
        if(!zipFile.exists()) {
            zipFile.mkdirs();
        }
        if(csvFiles != null && csvFiles.size() > 0) {
            List<String> files = csvFiles.stream().map(f -> {
                String file = f.getAbsolutePath();
                return file;
            }).collect(Collectors.toList());
            if (rows < 1) {
                rows = 50;
            }
            //---- 开始压缩csv ----
            int size = csvFiles.size();
            int loop = 0;
            int remainder = size % rows;// 取余数
            if (remainder == 0) {
                loop = size / rows;
            } else {
                loop = (size / rows) + 1;
            }
            for (int i = 0; i < loop; i++) {
                // 创建 zip 文件名
                int fromIndex = i * rows;
                int toIndex = fromIndex + rows;
                if (toIndex > size) {
                    toIndex = size;
                }
                List<String> tmp = files.subList(fromIndex, toIndex);
                Utils.zip(zipFolder + fullDbName + "-" + i + ".zip", tmp.toArray(new String[tmp.size()]));
            }
        }

    }

    private static void testCsvToZip() {
        String base = "d:/export/docker-1/db1_1/";
        List<File> csvFiles = Utils.allFiles(base).stream()
                .filter(p->p.getName().endsWith("csv")).collect(Collectors.toList());
        csvToZip(base,csvFiles,"",50);
    }

    public static void main(String[] args) {
        //Utils.mkdirsByFullDBName("C:/export","docker-1.db1_1","docker-1.db1_2","docker-1.db1_3","docker-2.db1_1");
        //Utils.testCsvToZip();
        Map<String,List<File>> map = Utils.getZipFiles("D:\\project\\idea\\ebay-data-out-csv\\out\\artifacts\\ebay_data_out_csv_war_exploded\\export");
        System.out.println(map);
    }
}
