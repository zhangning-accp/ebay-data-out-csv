package servlet;

import dao.ECommerceProductDetail;
import dao.ECommerceProductDetailDao;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import util.ApplicationCache;
import util.CsvOut;
import util.Utils;

/**
 * Created by zn on 2018/8/3.
 */
@Slf4j
public class BatchExportDataThread implements Runnable {
    private String dbName = "";

    /**
     *
     * @param fullDBName
     */
    public BatchExportDataThread(String fullDBName) {
        this.dbName = fullDBName;

    }
    @Override
    public void run() {
        //每50个打包一个zip
        String zipFolder = Utils.getRelativeFilePathByFullDBName(dbName);
        File file = new File(zipFolder);
        if(!file.exists()) {
            file.mkdirs();
        }
        //1.获取数据库数据数据

        ECommerceProductDetailDao dao = new ECommerceProductDetailDao(dbName);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        int [] minMax = dao.findMinAndMaxSortIndex();
        //先获取min 和 max.
        int minSortIndex = minMax[0];
        int maxSortIndex = minMax[1];
        int count = 233;
        int start = minSortIndex;
        int to = start + count;
        List<ECommerceProductDetail> list = null;
        while(true) {// 当start + count > maxSortIndex 循环结束
            log.info("从数据库 {} 获取数据",dbName);
            list = dao.findProductNameIsNotNullProductsBySortIndex(start,to);
            log.info("从数据库 {} 获取数据完毕，数量:{},开始导出为csv文件",list.size());
            if(list != null && list.size() > 0) {
                Date date = new Date();
                String fileName = zipFolder + format.format(date) + ".csv";
                CsvOut.saveDataToCsv(list, fileName);
                log.info("导出csv文件成功，文件信息：{}",fileName);
            }
            if(to > maxSortIndex) {
                break;
            }
            start += count;
            to = start + count;
            log.info("从数据库 {} 获取数据",dbName);
            list.clear();
            log.info("从数据库 {} 获取数据完毕，数量:{},开始导出为csv文件",list.size());
        }
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
            // 删除csv文件
            for (File f : csvFiles) {
                f.delete();
            }
        }
    }
}
