package util;

import dao.ECommerceProductDetail;
import dao.ECommerceProductDetailDao;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;
import org.apache.commons.lang.BooleanUtils;

/**
 * Created by zn on 2018/6/28.
 * 将数据导出为csv
 */
@Slf4j
public class CsvOut {
//    public static void saveDataToCsv(int startLimit,int count,String filePath,String dabaseName) {
//        ECommerceProductDetailDao dao = new ECommerceProductDetailDao(dabaseName);
//        ApplicationCache.PROGRESS_BAR.add("正在读取数据库数据");
//        List<ECommerceProductDetail> details = dao.findProductNameIsNotNullProductsBySortIndex(startLimit,count);
//
//        saveDataToCsv(details,filePath);
//    }

    public static void saveDataToCsv(List<ECommerceProductDetail> details,String filePath) {
        String[] heads = {"产品名称", "产品长描述", "产品图片", "产品特价", "产品原始价格",
                "属性1", "属性2", "属性3", "分类", "主图备用", "pageUrl"};
        File f = new File(filePath);
        if(!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        StringBuffer buffer = new StringBuffer();
        for(String head : heads) {
            buffer.append(head + ",");
        }
        buffer.deleteCharAt(buffer.lastIndexOf(","));
        buffer.append(System.lineSeparator());
        ApplicationCache.PROGRESS_BAR.add("正在处理" + details.size() + "条数据");
        details.stream().forEach(p -> {
            String productName = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getProductName()));
            String itemSpecifics = Utils.trimToEmpty(p.getItemSpecifics());
            itemSpecifics = Utils.stripTagsSpace(itemSpecifics);
            itemSpecifics = Utils.stripBlank(itemSpecifics);
            itemSpecifics = addSemicolonAtBothEnds(itemSpecifics);

            String restPicturesUrl = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getRestPicturesUrl()));
            restPicturesUrl = Utils.replacePicSize600(restPicturesUrl);

            String currentPrice = Utils.leaveThePrice(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getCurrentPrice())));

            String originalPrice = Utils.leaveThePrice(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getOriginalPrice())));

            String attribute1 = Utils.stripOutOfStock(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getAttribute1())));

            String attribute2 = Utils.stripOutOfStock(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getAttribute2())));

            String attribute3 = Utils.stripOutOfStock(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getAttribute3())));

            String categoryLevels = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getCategoryLevels()));

            String mainPictureUrl = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getMainPictureUrl()));
            mainPictureUrl = Utils.replacePicSize600(mainPictureUrl);

            String url = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getUrl()));

            buffer.append(productName + ",").append(itemSpecifics + ",").append(restPicturesUrl + ",")
                    .append(currentPrice + ",").append(originalPrice + ",").append(attribute1 + ",")
                    .append(attribute2 + ",").append(attribute3 + ",").append(categoryLevels + ",")
                    .append(mainPictureUrl + ",").append(url + System.lineSeparator());
        });
        File file = new File(filePath);
        log.info("file path:{}", file.getAbsolutePath());
        //ApplicationCache.PROGRESS_BAR.add("正在导出" + details.size() + "条数据到csv");
        Utils.save2File(buffer.toString(), file.getAbsolutePath(), false);


    }

    public static void saveSoldDataToCSV(List<ECommerceProductDetail> details,String filePath) {
        String[] heads = {"id", "ecommerce_category_id","ecommerce_category_full_path", "url", "product_name",
                "current_price", "main_picture_url", "category_levels", "product_sub_name", "item_condition",
                "rest_pictures_url","original_price","item_specifics","product_description","crawler_task_id",
                "created_time","attribute1","attribute2","attribute3","sold","member_id","mbg_link",
                "feedback_count","feedback_count_link","sold_history_url","crawler_status"};

//        String[] heads = {"id", "ecommerce_category_id","ecommerce_category_full_path", "url","product_name",
//                "current_price", "main_picture_url", "category_levels","item_condition",
//                "rest_pictures_url","original_price","item_specifics","product_description","crawler_task_id",
//                "created_time","attribute1","attribute2","attribute3","sold","member_id","mbg_link",
//                "feedback_count","feedback_count_link","sold_history_url","crawler_status"};
        File f = new File(filePath);
        if(!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        StringBuffer buffer = new StringBuffer();
        for(String head : heads) {
            buffer.append(head + ",");
        }
        buffer.deleteCharAt(buffer.lastIndexOf(","));
        buffer.append(System.lineSeparator());
        int end = buffer.length();
        log.info("开始循环数据，拼装 buffer ...... {}",filePath);
        if(details.size() > 20000) {
            int loop = details.size() / 20000;
            for(int i = 0; i < loop; i ++) {
                ECommerceProductDetail p = details.get(i);
                String id = p.getId();
                String eCommerceCategoryId = p.getECommerceCategoryId();

                String eCommerceCategoryFullPath = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getEcommerceCategoryFullPath()));

                String url = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getUrl()));

                String productName = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getProductName()));

                String currentPrice = Utils.leaveThePrice(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getCurrentPrice())));

                String mainPictureUrl = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getMainPictureUrl()));
                mainPictureUrl = Utils.replacePicSize600(mainPictureUrl);

                String categoryLevels = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getCategoryLevels()));

                String productSubName = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getProductSubName()));


                String itemCondition = Utils.trimToEmpty(p.getItemCondition());
                itemCondition = Utils.stripTagsSpace(itemCondition);
                itemCondition = Utils.stripBlank(itemCondition);
                itemCondition = addSemicolonAtBothEnds(itemCondition);


                String restPicturesUrl = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getRestPicturesUrl()));
                restPicturesUrl = Utils.replacePicSize600(restPicturesUrl);

                String originalPrice = Utils.leaveThePrice(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getOriginalPrice())));

                String itemSpecifics = Utils.trimToEmpty(p.getItemSpecifics());
                itemSpecifics = Utils.stripTagsSpace(itemSpecifics);
                itemSpecifics = Utils.stripBlank(itemSpecifics);
                itemSpecifics = addSemicolonAtBothEnds(itemSpecifics);

                String productDescription = Utils.trimToEmpty(p.getProductDescription());
                productDescription = Utils.stripTagsSpace(productDescription);
                productDescription = Utils.stripBlank(productDescription);
                productDescription = addSemicolonAtBothEnds(productDescription);

                String crawlerTaskId = p.getCrawlerTaskId();

                Date createdTime = p.getCreatedTime();

                String attribute1 = Utils.stripOutOfStock(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getAttribute1())));

                String attribute2 = Utils.stripOutOfStock(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getAttribute2())));

                String attribute3 = Utils.stripOutOfStock(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getAttribute3())));

                String sold = p.getSold();

                String memberId = p.getMemberId();

                if(Utils.isBlank(sold)) {
                    sold = "0";
                }
                String mbgLink = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getMbgLink()));

                String feedbackCount = p.getFeedbackCount();
                if(Utils.isBlank(feedbackCount)) {
                    feedbackCount = "0";
                }
                String feedbackCountLink = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getFeedbackCountLink()));

                String soldHistoryUrl = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getSoldHistoryUrl()));

                int crawlerStatus = BooleanUtils.toInteger(p.isCrawlerStatus());

                buffer.append(id + ",").append(eCommerceCategoryId + ",").append(eCommerceCategoryFullPath + ",")
                        .append(url + ",").append(productName + ",").append(currentPrice + ",").append(mainPictureUrl + ",")
                        .append(categoryLevels + ",").append(productSubName + ",").append(itemCondition + ",")
                        .append(restPicturesUrl + ",").append(originalPrice + ",").append(itemSpecifics + ",")
                        .append(productDescription + ",").append(crawlerTaskId + ",").append(createdTime + ",")
                        .append(attribute1 + ",").append(attribute2 + ",").append(attribute3 + ",").append(sold + ",")
                        .append(memberId + ",").append(mbgLink + ",").append(feedbackCount + ",").append(feedbackCountLink + ",")
                        .append(soldHistoryUrl + ",").append(crawlerStatus + System.lineSeparator());

                File file = new File(filePath);
                Utils.save2File(buffer.toString(), file.getAbsolutePath(), false);
                buffer.delete(0,end);
            }
        }

//        details.stream().forEach(p -> {
//            String id = p.getId();
//            String eCommerceCategoryId = p.getECommerceCategoryId();
//
//            String eCommerceCategoryFullPath = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getEcommerceCategoryFullPath()));
//
//            String url = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getUrl()));
//
//            String productName = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getProductName()));
//
//            String currentPrice = Utils.leaveThePrice(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getCurrentPrice())));
//
//            String mainPictureUrl = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getMainPictureUrl()));
//            mainPictureUrl = Utils.replacePicSize600(mainPictureUrl);
//
//            String categoryLevels = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getCategoryLevels()));
//
//           String productSubName = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getProductSubName()));
//
//
//            String itemCondition = Utils.trimToEmpty(p.getItemCondition());
//            itemCondition = Utils.stripTagsSpace(itemCondition);
//            itemCondition = Utils.stripBlank(itemCondition);
//            itemCondition = addSemicolonAtBothEnds(itemCondition);
//
//
//            String restPicturesUrl = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getRestPicturesUrl()));
//            restPicturesUrl = Utils.replacePicSize600(restPicturesUrl);
//
//            String originalPrice = Utils.leaveThePrice(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getOriginalPrice())));
//
//            String itemSpecifics = Utils.trimToEmpty(p.getItemSpecifics());
//            itemSpecifics = Utils.stripTagsSpace(itemSpecifics);
//            itemSpecifics = Utils.stripBlank(itemSpecifics);
//            itemSpecifics = addSemicolonAtBothEnds(itemSpecifics);
//
//            String productDescription = Utils.trimToEmpty(p.getProductDescription());
//            productDescription = Utils.stripTagsSpace(productDescription);
//            productDescription = Utils.stripBlank(productDescription);
//            productDescription = addSemicolonAtBothEnds(productDescription);
//
//            String crawlerTaskId = p.getCrawlerTaskId();
//
//            Date createdTime = p.getCreatedTime();
//
//            String attribute1 = Utils.stripOutOfStock(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getAttribute1())));
//
//            String attribute2 = Utils.stripOutOfStock(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getAttribute2())));
//
//            String attribute3 = Utils.stripOutOfStock(addSemicolonAtBothEnds(Utils.trimToEmpty(p.getAttribute3())));
//
//            String sold = p.getSold();
//
//            String memberId = p.getMemberId();
//
//            if(Utils.isBlank(sold)) {
//                sold = "0";
//            }
//            String mbgLink = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getMbgLink()));
//
//            String feedbackCount = p.getFeedbackCount();
//            if(Utils.isBlank(feedbackCount)) {
//                feedbackCount = "0";
//            }
//            String feedbackCountLink = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getFeedbackCountLink()));
//
//            String soldHistoryUrl = addSemicolonAtBothEnds(Utils.trimToEmpty(p.getSoldHistoryUrl()));
//
//            int crawlerStatus = BooleanUtils.toInteger(p.isCrawlerStatus());
////            buffer.append(id + ",").append(eCommerceCategoryId + ",").append(eCommerceCategoryFullPath + ",")
////                    .append(url + ",").append(productName + ",").append(currentPrice + ",").append(mainPictureUrl + ",")
////                    .append(categoryLevels + ",").append(itemCondition + ",")
////                    .append(restPicturesUrl + ",").append(originalPrice + ",").append(itemSpecifics + ",")
////                    .append(productDescription + ",").append(crawlerTaskId + ",").append(createdTime + ",")
////                    .append(attribute1 + ",").append(attribute2 + ",").append(attribute3 + ",").append(sold + ",")
////                    .append(memberId + ",").append(mbgLink + ",").append(feedbackCount + ",").append(feedbackCountLink + ",")
////                    .append(soldHistoryUrl + ",").append(crawlerStatus + System.lineSeparator());
//
//
//            buffer.append(id + ",").append(eCommerceCategoryId + ",").append(eCommerceCategoryFullPath + ",")
//                    .append(url + ",").append(productName + ",").append(currentPrice + ",").append(mainPictureUrl + ",")
//                    .append(categoryLevels + ",").append(productSubName + ",").append(itemCondition + ",")
//                    .append(restPicturesUrl + ",").append(originalPrice + ",").append(itemSpecifics + ",")
//                    .append(productDescription + ",").append(crawlerTaskId + ",").append(createdTime + ",")
//                    .append(attribute1 + ",").append(attribute2 + ",").append(attribute3 + ",").append(sold + ",")
//                    .append(memberId + ",").append(mbgLink + ",").append(feedbackCount + ",").append(feedbackCountLink + ",")
//                    .append(soldHistoryUrl + ",").append(crawlerStatus + System.lineSeparator());
//        });

        log.info("buffer 准备完毕.... 准备写入文件......{}",filePath);


    }

//    private static String replaceComma(String str) {
//        return str.replace(",","\",\"");
//    }
//
    /**
     * 在传入的字符串两头加 " 号.如果原字符串里有" 符号，则替换为'符号。
     * @param str
     * @return
     */
    private static String addSemicolonAtBothEnds(String str){
        str = str.replace("\"","'");
        str = "\"" + str + "\"";
        return str;
    }

    private static void writeCsvOutToFile(String content,String filePath, boolean append) {
        Utils.save2File(content, filePath, append);
    }
    private static List<CsvOutRecord> readListByFile(String filePath){
        return null;
    }
    private static String allCsvOutRecord() {
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            reader = new BufferedReader(
                    new FileReader(ApplicationCache.DEFAULT_DATA_SOURCE_XML_FILE_PAH));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    private CsvOutRecord getCsvOutRecordByString(String str) {
        CsvOutRecord record =  new CsvOutRecord();
        String [] split = str.split("\t");
        for(int i = 0; i < split.length; i ++) {
            switch (i) {
                case 0:
                record.fileName = split[0];
                    break;
                case 1:
                    record.fileURLPath = split[1];
                    break;
                case 2:
                    record.fileLocalPath = split[2];
                    break;
                case 3:
                    record.outTime = split[3];
                    break;
                case 4:
                    record.number = split[4];
                    break;
                case 5:
                    record.dbName = split[5];
                    break;
            }
        }
        return record;
    }
    class CsvOutRecord {
        private String index;
        private String fileName;
        private String fileURLPath;
        private String fileLocalPath;
        private String outTime;
        private String number;
        private String dbName;

        private String saveString(){
            return fileName + "\t" + fileURLPath + "\t" + fileLocalPath + "\t"
                    + outTime + "\t" + number + "\t" + dbName;
        }

    }
}
