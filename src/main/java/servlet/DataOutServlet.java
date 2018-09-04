package servlet;

import lombok.extern.slf4j.Slf4j;
import util.ApplicationCache;
import util.CsvOut;
import util.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zn on 2018/6/28.
 */
@Slf4j
public class DataOutServlet extends javax.servlet.http.HttpServlet {

    @Override
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        ApplicationCache.PROGRESS_BAR.clear();
        String action = request.getParameter("action");
        if(action != null) {
            PrintWriter out = response.getWriter();
            String [] names = null;
            log.info("action:{}",action);
            switch (action) {
                case "delete"://删除export下的文件
                    String name = request.getParameter("n");
                    String fullDbName = request.getParameter("dn");
                    String dbFolder = Utils.getRelativeFilePathByFullDBName(fullDbName);
                    String deltePath = ApplicationCache.DEFAULT_CSV_FILE_PATH + dbFolder + name;
                    File file = new File(deltePath);
                    file.delete();
                    break;
                case "ds"://删除sold下的文件
                     name = request.getParameter("n");
                     fullDbName = request.getParameter("dn");
                     dbFolder = Utils.getRelativeFilePathByFullDBName(fullDbName);
                     deltePath = ApplicationCache.DEFAULT_SOLD_CSV_FILE_PATH + dbFolder + name;
                     file = new File(deltePath);
                     file.delete();
                    response.sendRedirect("sold.jsp");
                    break;
                case "batch":
                    names = request.getParameterValues("dbName");
                    for(String db : names) {
                        Thread thread = new Thread(new BatchExportDataThread(db));
                        thread.setName("batch-export-data-thread-" + db );
                        thread.start();
                    }
                    break;
                case "sold"://导出销量数据
                    names = request.getParameterValues("dbName");
                    int threadCount = names.length / 2;
                    String [] names1 = new String[threadCount];
                    String [] names2 = new String[threadCount];
                    for(int i = 0; i < names1.length; i ++) {
                        names1[i] = names[i];
                        names2[i] = names[i + threadCount];
                    }
                    Thread thread1 = new Thread(new BatchExportDataThread(names1, BatchExportDataThread.BATCH_EXPORT_SOLD_DATA));
                    thread1.setName("batch-export-sold-thread-1");
                    thread1.start();

                    Thread thread2 = new Thread(new BatchExportDataThread(names2, BatchExportDataThread.BATCH_EXPORT_SOLD_DATA));
                    thread2.setName("batch-export-sold-thread-2");
                    thread2.start();

                    break;

            }
        }
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        this.doPost(request,response);
    }
}
