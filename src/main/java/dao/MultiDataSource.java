package dao;

import com.alibaba.druid.pool.DruidDataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import util.ApplicationCache;

import java.util.*;

/**
 * Created by zn on 2018/6/28.
 */
@Slf4j
public class MultiDataSource {
    // key :
    private Map<String, List<DataSource>> dataSources = new HashMap();
    // key: server_name.db_name.
    private Map<String,DruidDataSource> druidDataSourceMap = new HashMap();
    private static MultiDataSource instance = null;
    //public static String dataSourceXML = ApplicationCache.DEFAULT_DATA_SOURCE_XML_FILE_PAH;
    private long oldLastModify = 0;
    private long newLastModify = 0;

    private MultiDataSource() {
        new Thread(()->{
            while(true) {
                if (checkChange()) {
                    log.warn("data sourde is change, reloading ..... ");
                    readerXMLBuilderDataSources();
                    initDruidDataSourceMap();
                    log.warn("data sourde reloading finished ..... ");
                }
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static MultiDataSource getInstance() {
        if (instance == null) {
            instance = new MultiDataSource();
        }
        return instance;
    }

    /**
     * 返回的key是 db server id。 value 是当前key下的 id.dbname的字符串。主要提供给前端页面使用
     * @return
     */
    public Map<String, List<DataSource>> getDataSource() {
        return dataSources;
    }

    public Connection getConnection(String fullDBName) {
        try {
            return druidDataSourceMap.get(fullDBName).getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean checkChange() {
        File file = new File(ApplicationCache.DEFAULT_DATA_SOURCE_XML_FILE_PAH);
        newLastModify = file.lastModified();
        if(newLastModify > oldLastModify) {
            oldLastModify = newLastModify;
            return true;
        }
        return false;
    }

    private void readerXMLBuilderDataSources() {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(ApplicationCache.DEFAULT_DATA_SOURCE_XML_FILE_PAH);
            Element root = document.getRootElement();
            List<Element> baseSourceElements = root.elements("base-source");
            for (int i = 0; i < baseSourceElements.size(); i++) {
                Element baseSource = baseSourceElements.get(i);
                String id = baseSource.attributeValue("id");
                String url = baseSource.attributeValue("url");
                String userName = baseSource.attributeValue("user-name");
                String password = baseSource.attributeValue("password");
                String driverClass = baseSource.attributeValue("driver-class");
                List<Element> dataSourceElements = baseSource.elements("data-source");
                List<DataSource> dataSourceList = new ArrayList<DataSource>();
                for (int j = 0; j < dataSourceElements.size(); j++) {
                    DataSource dataSource = new DataSource();
                    Element dataSourceElement = dataSourceElements.get(j);
                    String dbName = dataSourceElement.attributeValue("db-name");
                    String count = dataSourceElement.attributeValue("count");
                    if(StringUtils.isNotBlank(count)) {
                        try {
                            dataSource.setCount(Integer.parseInt(count));
                        } catch (NumberFormatException e) {
                            dataSource.setCount(0);
                        }
                    }
                    dataSource.setId(id);
                    dataSource.setUrl(url.replaceAll("\\{db-name\\}", dbName));
                    dataSource.setUserName(userName);
                    dataSource.setDriverClass(driverClass);
                    dataSource.setDbName(dbName);
                    dataSource.setPassword(password);
                    dataSourceList.add(dataSource);
                }
                dataSources.put(id, dataSourceList);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
    private void initDruidDataSourceMap() {
        Iterator<String> iterator = dataSources.keySet().iterator();
        while(iterator.hasNext()) {
            String key = iterator.next();
            List<DataSource> dataSourceList = dataSources.get(key);
            for(DataSource dataSource : dataSourceList) {
                String url = dataSource.getUrl();
                String userName = dataSource.getUserName();
                String password = dataSource.getPassword();
                String driverClass = dataSource.getDriverClass();
                String dataBaseName = dataSource.getDbName();

                String fullDBName = key + "." + dataBaseName;
                // 创建连接池对象
                DruidDataSource druidDataSource = new DruidDataSource();
                druidDataSource.setDriverClassName(driverClass);
                druidDataSource.setUsername(userName);
                druidDataSource.setPassword(password);
                druidDataSource.setUrl(url);
                druidDataSource.setInitialSize(2);
                druidDataSource.setMinIdle(1);
                druidDataSource.setMaxActive(5);
                druidDataSource.setValidationQuery("select count(1) from crawler_machine");
                druidDataSource.setTestWhileIdle(true);
                // 启用监控统计功能  dataSource.setFilters("stat");// for mysql  dataSource.setPoolPreparedStatements(false);
                druidDataSourceMap.put(fullDBName,druidDataSource);
            }
        }
        log.info("initDruidDataSourceMap finished.... ");
    }
}
