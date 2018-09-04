import dao.MultiDataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import util.CsvOut;
import util.Utils;

/**
 * Created by zn on 2018/6/28.
 */
@Slf4j
public class Test {
    private static void zipFile() {
       String src = "d:/test.sql";
        String desc = "d:/test.zip";
        Utils.zip(src,desc);
    }


    private static void saveDataToCsv() {
        //MultiDataSource.dataSourceXML = "D:\\project\\idea\\ebay-data-out-csv\\src\\main\\resources\\data-source.xml";
        MultiDataSource multiDataSource = MultiDataSource.getInstance();
        //CsvOut.saveDataToCsv(1,-10000,"C:\\Users\\zn\\Desktop\\out-data.csv","local.crawler_database");

    }
    private static String replaceComma(String str) {
        return str.replace(",","\",\"");
    }

    /**
     * 在传入的字符串两头加 " 号
     * @param str
     * @return
     */
    private static String addSemicolonAtBothEnds(String str){
        str = "\"" + str + "\"";
        return str;
    }
    public static void main(String [] args) {
        String[] heads = {"id", "ecommerce_category_id","ecommerce_category_full_path", "url", "product_name",
                "current_price", "main_picture_url", "category_levels", "product_sub_name", "item_condition",
                "rest_pictures_url","original_price","item_specifics","product_description","crawler_task_id",
                "created_time","attribute1","attribute2","attribute3","sold","member_id","mbg_link",
                "feedback_count","feedback_count_link","sold_history_url","crawler_status"};
        StringBuffer buffer = new StringBuffer();
        for(String head : heads) {
            buffer.append(head + ",");
        }
        buffer.deleteCharAt(buffer.lastIndexOf(","));
        int end = buffer.length();
        System.out.println("1 end length:" + end);
        buffer.append(System.lineSeparator());
        end = buffer.length();
        System.out.println("2 end length:" + end);
        for(int i = 0; i < 10; i ++) {
            for (int j = 0; j < 10; j++) {
                buffer.append("[" + i + "-" +j + "]");
            }
            buffer.append(System.lineSeparator());
            try {
                FileWriter writer = new FileWriter("d:/t.txt", true);
                writer.write(buffer.toString());
                buffer.delete(0, end);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
