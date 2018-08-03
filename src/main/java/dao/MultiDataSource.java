package dao;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.sql2o.Sql2o;
import util.ApplicationCache;

import java.util.*;

/**
 * Created by zn on 2018/6/28.
 */
@Slf4j
public class MultiDataSource {
    private Map<String, List<DataSource>> dataSources = new HashMap();
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

    /**
     * @param dataBaseName 需要查找的数据库名全称。由base-source-id.db-name构成。举例，如
     *                     <base-source id="ds1"/>
     *                     <data-source db-name="db1" base-source="ds1"/>
     *                     要找db1，则ds1.db1
     * @return
     */
    public Sql2o getConnection(String dataBaseName) {
        if (dataBaseName.contains(".")) {
            String[] split = dataBaseName.split("\\.");
            String sourceId = split[0];
            String dbName = split[1];
            if (dataSources.containsKey(sourceId)) {
                List<DataSource> dataSourceList = dataSources.get(sourceId);
                if (dataSourceList != null && dataSourceList.size() > 0) {
                    for (int i = 0; i < dataSourceList.size(); i++) {
                        DataSource dataSource = dataSourceList.get(i);
                        if (dataSource.getDbName().equals(dbName)) {
                            try {
                                Class.forName(dataSource.getDriverClass());
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            Sql2o sql2o = new Sql2o(dataSource.getUrl(),
                                    dataSource.getUserName(), dataSource.getPassword());
                            return sql2o;
                        }
                    }
                } else {
                    log.error("id:{} base souce not any db. db list size:{}",sourceId,dataSourceList.size());
                    return null;
                }
            } else {
                log.error("not find db source id is:{},please check data-source.xml <base-source> node id attribute value.",sourceId);
                return null;
            }
        } else {
            log.error("data base name format error. exsample: id.db name");
            return null;
        }
        log.error("not find any db");
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
}
