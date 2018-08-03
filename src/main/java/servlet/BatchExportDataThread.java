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
        String server = dbName.substring(0,dbName.indexOf("."));
        String db = dbName.substring(dbName.indexOf(".") + 1);
        String zipFolder = ApplicationCache.DEFAULT_CSV_FILE_PATH + server + File.separator + db + File.separator;
        File file = new File(zipFolder);
        if(!file.exists()) {
            file.mkdirs();
        }
        //1.获取数据库数据数据
        int start = 0;
        int count = 20000;
        ECommerceProductDetailDao dao = new ECommerceProductDetailDao(dbName);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        log.info("从数据库 {} 获取数据",dbName);
        List<ECommerceProductDetail> list = dao.findProductByLimit(start,count);
        log.info("从数据库 {} 获取数据完毕，数量:{},开始导出为csv文件",list.size());
        while(list != null && list.size() > 0) {
            Date date = new Date();
            String fileName = zipFolder + format.format(date) + ".csv";
            CsvOut.saveDataToCsv(list,fileName);
            log.info("导出csv文件成功，文件信息：{}",fileName);
            start += count;
            log.info("从数据库 {} 获取数据",dbName);
            list.clear();
            list = dao.findProductByLimit(start,count);
            log.info("从数据库 {} 获取数据完毕，数量:{},开始导出为csv文件",list.size());
        }
        //2. 读取目录下的文件，

        List<File> csvFiles = Utils.allFiles(ApplicationCache.DEFAULT_CSV_FILE_PATH);
        csvFiles = csvFiles.stream().filter(p->p.getName().endsWith("csv")).sorted((f1,f2)->{
            Long d1 = f1.lastModified();
            Long d2 = f2.lastModified();
            return d2.compareTo(d1);
        }).collect(Collectors.toList());
        log.info("开始将csv压缩成zip...");
        Utils.csvToZip(zipFolder,csvFiles,50);
        //---- 压缩结束 ----
        // 删除csv文件
        for(File f : csvFiles) {
            f.delete();
        }
    }
}
