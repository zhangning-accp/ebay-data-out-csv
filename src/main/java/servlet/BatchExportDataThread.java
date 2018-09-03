package servlet;

import dao.ECommerceProductDetail;
import dao.ECommerceProductDetailDao;
import dao.MultiDataSource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jodd.mail.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import util.ApplicationCache;
import util.CsvOut;
import util.EmailUtils;
import util.Utils;

/**
 * Created by zn on 2018/8/3.
 */
@Slf4j
public class BatchExportDataThread implements Runnable {
    private String dbName = "";
    private int target = 0;
    private String [] dbNames;
    public  final static int BATCH_EXPORT_SOLD_DATA = 1;

    /**
     *
     * @param fullDBName
     */
    public BatchExportDataThread(String fullDBName) {
        this.dbName = fullDBName;
        this.target = 0;
    }
    /**
     *
     * @param fullDBNames 数据库完整名
     *
     */
    public BatchExportDataThread(String [] fullDBNames,int target) {
        this.dbNames = fullDBNames;
        this.target = target;

    }
    @Override
    public void run() {
        MultiDataSource dataSource = MultiDataSource.getInstance();
        if(target > 0) {//导出销量数据
            exportSoldData();
        } else {
            dataSource.getSimpleDataSource(dbName).setCurrent(true);
            exportSimpleData();
            dataSource.saveExport(dbName);
        }
    }


    /**
     * 导出单库所有销量数据
     */
    private void exportSoldData(){
        log.info("选择了 {} 库,库名:{}",dbNames.length);
        for(String dbName : dbNames) {
            exportSoldData(dbName,ApplicationCache.DEFAULT_SOLD_CSV_FILE_PATH + Utils.getRelativeFilePathByFullDBName(dbName));
        }

    }

    /**
     * 导出单个库的数据
     */
    private void exportSimpleData(){
        exportSimpleData(dbName,ApplicationCache.DEFAULT_CSV_FILE_PATH + Utils.getRelativeFilePathByFullDBName(dbName));

    }
    private void exportSoldData(String dbName,String csvFilePath) {
        long start = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String startDate = format.format(date);
        log.info("开始执行导出 {} 操作..",dbName);
        //int id = 0;
        //每50个打包一个zip
        String zipFolder = csvFilePath;
        File file = new File(zipFolder);
        if(!file.exists()) {
            file.mkdirs();
        }
        //导出前先删除以前的文件
        File [] childer = file.listFiles();
        for(File f:childer) {
            f.delete();
        }
        //1.获取数据库数据数据
        log.info("正在创建ECommerceProductDetailDao 对象..");
        ECommerceProductDetailDao dao = new ECommerceProductDetailDao(dbName);
        int realDtatTotal = 0;//实际导出的数据总量
        List<ECommerceProductDetail> list = null;
        log.info("正在查找单品销量前10w的数据.....");
        list = dao.findProductDetailBySold(100000);
        log.info("单品准备导出为csv，数据数量:{}",list.size());
        if(list != null && list.size() > 0) {
            date = new Date();
            String fileName = zipFolder + format.format(date) + "-sold.csv";
            CsvOut.saveSoldDataToCSV(list, fileName);
            realDtatTotal += list.size();
        }
        list.clear();

        log.info("正在查找店铺销量前10w的数据.....");
        list = dao.findProductDetailByFeedbackCount(100000);
        log.info("店铺数据完成，准备导出为csv，数据数量:{}",list.size());
        if(list != null && list.size() > 0) {
            date = new Date();
            String fileName = zipFolder + format.format(date) + "-feedback.csv";
            CsvOut.saveSoldDataToCSV(list, fileName);
            realDtatTotal += list.size();
        }
        log.info("数据导出csv完毕...");
        list.clear();
        //2. 读取目录下的文件，
        List<File> csvFiles = Utils.csvFiles(zipFolder);
        csvFiles = csvFiles.stream().filter(p->p.getName().endsWith("csv")).sorted((f1,f2)->{
            Long d1 = f1.lastModified();
            Long d2 = f2.lastModified();
            return d2.compareTo(d1);
        }).collect(Collectors.toList());
        if(csvFiles != null && csvFiles.size() > 0) {
            log.info("开始将csv压缩成zip...");
            Utils.csvToZip(zipFolder, csvFiles, 50);
            //---- 压缩结束 ----
            //删除csv文件
            for (File f : csvFiles) {
                f.delete();
            }
        }
        date = new Date();
        String finishedDate = format.format(date);
        long end = System.currentTimeMillis();
        log.info("发送邮件...");
        EmailUtils.sendEmail("909604945@qq.com","Database " + dbName + " export finished",
                "realDtatTotal:" + realDtatTotal
                + "start date:" + startDate +
                        ", finished date:" + finishedDate +
                        ",elapsed time:" + (end - start) /1000 + "s");

        log.info("export {} data finished...,realDtatTotal:{},datatime:{}, folder path : {},elapsed time:{} s",
                dbName,realDtatTotal,format.format(new Date()),zipFolder,(end - start)/1000);
    }



    /**
     * 导出指定库的数据
     * @param dbName
     * @param csvFilePath
     */
    private void exportSimpleData(String dbName,String csvFilePath) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String startDate = format.format(date);
        log.info("开始执行导出 {} 操作..",dbName);
        int id = 0;
        //每50个打包一个zip
        String zipFolder = csvFilePath;
        File file = new File(zipFolder);
        if(!file.exists()) {
            file.mkdirs();
        }
        //导出前先删除以前的文件
        File [] childer = file.listFiles();
        for(File f:childer) {
            f.delete();
        }

        //1.获取数据库数据数据
        ECommerceProductDetailDao dao = new ECommerceProductDetailDao(dbName);

        int [] minMax = dao.findMinAndMaxSortIndex();
        int dataTotal = dao.findCountIsProductNameNotNull(); // 数据库里的数据总量
        int realDtatTotal = 0;//实际导出的数据总量
        //先获取min 和 max.
        int minSortIndex = minMax[0];
        int maxSortIndex = minMax[1];
        int count = 20000;
        int start = minSortIndex;
        int to = start + count;
        List<ECommerceProductDetail> list = null;
        while(true) {// 当start + count > maxSortIndex 循环结束
            list = dao.findProductNameIsNotNullProductsBySortIndex(start, to);
            if(list != null && list.size() > 0) {
                date = new Date();
                String fileName = zipFolder + format.format(date) + "-" + id + ".csv";
                CsvOut.saveDataToCsv(list, fileName);
                realDtatTotal += list.size();
            }
            if(to > maxSortIndex) {
                break;
            }
            start = to;
            to = start + count;
            id ++;
            list.clear();
        }
        //2. 读取目录下的文件，
        List<File> csvFiles = Utils.csvFiles(zipFolder);
        csvFiles = csvFiles.stream().filter(p->p.getName().endsWith("csv")).sorted((f1,f2)->{
            Long d1 = f1.lastModified();
            Long d2 = f2.lastModified();
            return d2.compareTo(d1);
        }).collect(Collectors.toList());
        if(csvFiles != null && csvFiles.size() > 0) {
            //log.info("开始将csv压缩成zip...");
            Utils.csvToZip(zipFolder, csvFiles, 50);
            //---- 压缩结束 ----
            //删除csv文件
            for (File f : csvFiles) {
                f.delete();
            }
        }
        date = new Date();
        String finishedDate = format.format(date);
        EmailUtils.sendEmail("909604945@qq.com","Database " + dbName + " export finished ","dataTotal:" + dataTotal +
                ",realDtatTotal:" + realDtatTotal + ",difference value:" + (realDtatTotal - dataTotal)
        + "start date:" + startDate + ", finished date:" + finishedDate);
        log.info("export {} data finished... dataTotal:{}, realDtatTotal:{},datatime:{}, folder path : {}",
                dbName,dataTotal,realDtatTotal,format.format(new Date()),zipFolder);
    }
}
