package filter;

import dao.MultiDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.PropertyConfigurator;
import util.ApplicationCache;

import javax.servlet.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by zn on 2018/7/31.
 */
@Slf4j
public class DataSourceFilter implements Filter {
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        ServletContext application = config.getServletContext();
        String realPath = application.getRealPath("/");

        ApplicationCache.REAL_PATH = realPath;
        ApplicationCache.DEFAULT_DATA_SOURCE_XML_FILE_PAH = realPath + "WEB-INF/classes/data-source.xml";
        ApplicationCache.DEFAULT_CSV_FILE_PATH = realPath + File.separator + "export" + File.separator;

        System.getProperties().setProperty("logFilesPath", realPath);
        PropertyConfigurator.configure(realPath + "WEB-INF/classes/log4j.properties");

        log.info("realPath:{}",realPath);

        log.info("初始化 multi data source.....");
        MultiDataSource multiDataSource = MultiDataSource.getInstance();
        application.setAttribute("dataSource", MultiDataSource.getInstance().getDataSource());
        //log.info(multiDataSource.getDataSourceString().toString());
    }

}
