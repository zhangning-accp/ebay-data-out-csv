package dao;

import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import util.ApplicationCache;

import java.util.*;

/**
 * Created by zn on 2018/6/28.
 */
@Slf4j
public class MultiDataSource {
    // key : db-server
    private Map<String, List<DataSource>> dataSources = new LinkedHashMap();
    private Map<String,HikariDataSource> poolDataSourceMap = new HashMap();
    private static MultiDataSource instance = null;
    private long oldLastModify = 0;
    private long newLastModify = 0;

    private MultiDataSource() {
        new Thread(()->{
            while(true) {
                if (checkChange()) {
                    log.warn("data sourde is change, reloading ..... ");
                    readerXMLBuilderDataSources();
                    initPoolDataSourceMap();
                    log.warn("data sourde reloading finished ..... ");
                }
                try {
                    Thread.sleep(60 * 1000);
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

    public DataSource getSimpleDataSource(String fullDBName) {
        String server = fullDBName.substring(0,fullDBName.indexOf("."));
        String dbName = fullDBName.substring(fullDBName.indexOf(".") + 1);
        List<DataSource> list = dataSources.get(server);
        return list.stream().filter(p->p.getDbName().equals(dbName)).findFirst().get();
    }
    public Connection getConnection(String fullDBName) {
        try {
            return poolDataSourceMap.get(fullDBName).getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private synchronized boolean checkChange() {
        File file = new File(ApplicationCache.DEFAULT_DATA_SOURCE_XML_FILE_PAH);
        newLastModify = file.lastModified();
        if(newLastModify > oldLastModify) {
            oldLastModify = newLastModify;
            return true;
        }
        return false;
    }

    private synchronized void readerXMLBuilderDataSources() {
        File file = new File(ApplicationCache.DEFAULT_DATA_SOURCE_XML_FILE_PAH);
        log.info("file path:{}",file.getAbsolutePath());
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(ApplicationCache.DEFAULT_DATA_SOURCE_XML_FILE_PAH);
            Element root = document.getRootElement();
            List<Element> baseSourceElements = root.elements("base-source");
            for (int i = 0; i < baseSourceElements.size(); i++) {
                Element baseSource = baseSourceElements.get(i);
                String id = baseSource.attributeValue("id");
                String url = baseSource.attributeValue("url") + "?useUnicode=true&autoReconnect=true&useSSL=false";
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
                    String isView = dataSourceElement.attributeValue("isView");
                    isView = StringUtils.defaultString(isView,"false");
                    isView = StringUtils.trimToEmpty(isView);
                    String isExport = dataSourceElement.attributeValue("isExport");
                    isExport = StringUtils.defaultString(isExport,"false");
                    isExport = StringUtils.trimToEmpty(isExport);
                    String isCurrent = dataSourceElement.attributeValue("isCurrent");
                    isCurrent = StringUtils.defaultString(isCurrent,"false");
                    isCurrent = StringUtils.trimToEmpty(isCurrent);

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
                    dataSource.setView(BooleanUtils.toBoolean(isView));
                    dataSource.setExport(BooleanUtils.toBoolean(isExport));
                    dataSource.setCurrent(BooleanUtils.toBoolean(isCurrent));

                    dataSourceList.add(dataSource);
                }
                dataSources.put(id, dataSourceList);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
    private synchronized void initPoolDataSourceMap() {
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
                // 避免修改文件后，重新创建无意义的sourde对象
                if(!poolDataSourceMap.containsKey(fullDBName)) {
                    // 创建连接池对象
                    HikariDataSource hikariDataSource = new HikariDataSource();
                    hikariDataSource.setJdbcUrl(url);
                    hikariDataSource.setUsername(userName);
                    hikariDataSource.setPassword(password);
                    //hikariDataSource.setConnectionTestQuery("select count(1) from crawler_machine");
                    hikariDataSource.setDriverClassName(driverClass);
                    hikariDataSource.setMaximumPoolSize(10);
                    hikariDataSource.setMinimumIdle(1);
                    // 启用监控统计功能  dataSource.setFilters("stat");// for mysql  dataSource.setPoolPreparedStatements(false);
                    poolDataSourceMap.put(fullDBName, hikariDataSource);
                }
            }
        }
        log.info("Init data source map finished.... ");
    }
    public synchronized void saveExport(String fullDBName) {
        String server = fullDBName.substring(0,fullDBName.indexOf("."));
        String dbName = fullDBName.substring(fullDBName.indexOf(".") + 1);

        File file = new File(ApplicationCache.DEFAULT_DATA_SOURCE_XML_FILE_PAH);
        log.info("file path:{}",file.getAbsolutePath());
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(ApplicationCache.DEFAULT_DATA_SOURCE_XML_FILE_PAH);
            Element root = document.getRootElement();
            List<Element> baseSourceElements = root.elements("base-source");
            for (int i = 0; i < baseSourceElements.size(); i++) {
                Element baseSource = baseSourceElements.get(i);
                String id = baseSource.attributeValue("id");
                if (id.equals(server.trim())) {// 如果当前的
                    List<Element> dataSourceElements = baseSource.elements("data-source");
                    for (int j = 0; j < dataSourceElements.size(); j++) {
                        Element dataSourceElement = dataSourceElements.get(j);
                        String name = dataSourceElement.attributeValue("db-name");
                        if(name.equals(dbName)) {
                            dataSourceElement.addAttribute("isExport","true");
                            break;
                        }
                    }
                    break;
                }
            }
            //指定文件输出的位置
            FileOutputStream out = new FileOutputStream(ApplicationCache.DEFAULT_DATA_SOURCE_XML_FILE_PAH);
            //1.创建写出对象
            XMLWriter writer = new XMLWriter(out);
            //2.写出Document对象
            writer.write(document);
            //3.关闭流
            writer.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
