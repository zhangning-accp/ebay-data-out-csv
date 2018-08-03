package servlet;

/**
 * Created by zn on 2018/8/3.
 */
public class BatchExportDataThread implements Runnable {
    private String dbName = "";
    public BatchExportDataThread(String dbName) {
        this.dbName = dbName;
    }
    @Override
    public void run() {
        //1.
        while(true) {

        }
    }
}
