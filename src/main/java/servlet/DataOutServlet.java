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
            log.info("action:{}",action);
            switch (action) {
                case "out_data":
                    String dbName = request.getParameter("dbName");
                    int startIndex = Integer.parseInt(request.getParameter("startIndex"));
                    int count = Integer.parseInt(request.getParameter("count"));
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date date = new Date();
                    String fileName = ApplicationCache.DEFAULT_CSV_FILE_PATH + format.format(date) + ".csv";
                    try {
                        CsvOut.saveDataToCsv(startIndex, count, fileName, dbName);
                        String zipFileName = fileName.substring(0,fileName.lastIndexOf(".")) + "-" + count + ".zip";
                        log.info("Extracting files. Please wait ...");
                        ApplicationCache.PROGRESS_BAR.add("Extracting files. Please wait ....");
                        Utils.zip(fileName,zipFileName);
                        File file = new File(fileName);
                        file.delete();
                        zipFileName = zipFileName.substring(zipFileName.lastIndexOf("/") + 1);
                        ApplicationCache.PROGRESS_BAR.add("Compressed file completion ....");
                        log.info("out_data,startIndex:{},count:{}", startIndex, count);
                        out.write("export/" + zipFileName);
                                return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        out.write("error: export data error." + e);
                        return;
                    }
                case "delete":
                    String name = request.getParameter("n");
                    File file = new File(ApplicationCache.DEFAULT_CSV_FILE_PATH + name);
                    file.delete();
                    break;
                case "batch":
                    String [] names = request.getParameterValues("dbName");
                    for(String db : names) {

                    }
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
