import dao.MultiDataSource;
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

        zipFile();
    }
}
