package dao;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zn on 2018/6/28.
 */
public class ECommerceProductDetailDao {
    Sql2o sql2o = null;
    public ECommerceProductDetailDao(String dbName) {
        sql2o = MultiDataSource.getInstance().getConnection(dbName);
    }
    public List<ECommerceProductDetail> findProductByLimit(int startLimit,int count) {
        Map<String, String> maping = new HashMap();
        maping.put("id", "id");
        maping.put("ecommerce_category_id", "ecommerceCategoryId");
        maping.put("ecommerce_category_full_path", "ecommerceCategoryFullPath");
        maping.put("url", "url");
        maping.put("product_name","productName");
        maping.put("current_price", "currentPrice");
        maping.put("main_picture_url", "mainPictureUrl");
        maping.put("category_levels", "categoryLevels");
        maping.put("product_sub_name", "productSubName");
        maping.put("product_specification", "productSpecification");
        maping.put("item_condition", "itemCondition");
        maping.put("rest_pictures_url", "restPicturesUrl");
        maping.put("original_price", "originalPrice");
        maping.put("attribute1", "attribute1");
        maping.put("attribute2", "attribute2");
        maping.put("attribute3", "attribute3");
        maping.put("sold", "sold");
        maping.put("sold_history_url","soldHistoryUrl");
        maping.put("member_id", "memberId");
        maping.put("mbg_link", "mbgLink");
        maping.put("feedback_count", "feedbackCount");
        maping.put("feedback_count_link", "feedbackCountLink");
        maping.put("item_specifics", "itemSpecifics");
        maping.put("product_description", "productDescription");
        maping.put("crawler_task_id", "crawlerTaskId");
        maping.put("created_time", "createdTime");
        maping.put("sort_index", "sortIndex");
        maping.put("crawler_status","crawlerStatus");
        String sql = "SELECT * FROM ecommerce_product_detail where product_name is not null LIMIT " + startLimit + "," + count;
        sql2o.setDefaultColumnMappings(maping);
        List<ECommerceProductDetail> list = null;
        try (Connection con = sql2o.beginTransaction()) {
            list = con.createQuery(sql).executeAndFetch(ECommerceProductDetail.class);
        }
        return list;
    }

    public int findCountIsProductNameNotNull() {
        String sql = "select count(*) from ecommerce_product_detail where product_name is not null";
        try(Connection con = sql2o.beginTransaction()) {
            return (Integer)con.createQuery(sql).executeScalar();
        }
    }
}
