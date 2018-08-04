package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.stream.Collectors;
import util.Utils;

/**
 * Created by zn on 2018/6/28.
 */
public class ECommerceProductDetailDao {
    //Sql2o sql2o = null;
    private String dbName;
    public ECommerceProductDetailDao(String fullDBName) {
        this.dbName = fullDBName;
        //sql2o = MultiDataSource.getInstance().getConnection(dbName);
    }
//    public List<ECommerceProductDetail> findProductByLimit(int startLimit,int count) {
//        Map<String, String> maping = new HashMap();
//        maping.put("id", "id");
//        maping.put("ecommerce_category_id", "ecommerceCategoryId");
//        maping.put("ecommerce_category_full_path", "ecommerceCategoryFullPath");
//        maping.put("url", "url");
//        maping.put("product_name","productName");
//        maping.put("current_price", "currentPrice");
//        maping.put("main_picture_url", "mainPictureUrl");
//        maping.put("category_levels", "categoryLevels");
//        maping.put("product_sub_name", "productSubName");
//        maping.put("product_specification", "productSpecification");
//        maping.put("item_condition", "itemCondition");
//        maping.put("rest_pictures_url", "restPicturesUrl");
//        maping.put("original_price", "originalPrice");
//        maping.put("attribute1", "attribute1");
//        maping.put("attribute2", "attribute2");
//        maping.put("attribute3", "attribute3");
//        maping.put("sold", "sold");
//        maping.put("sold_history_url","soldHistoryUrl");
//        maping.put("member_id", "memberId");
//        maping.put("mbg_link", "mbgLink");
//        maping.put("feedback_count", "feedbackCount");
//        maping.put("feedback_count_link", "feedbackCountLink");
//        maping.put("item_specifics", "itemSpecifics");
//        maping.put("product_description", "productDescription");
//        maping.put("crawler_task_id", "crawlerTaskId");
//        maping.put("created_time", "createdTime");
//        maping.put("sort_index", "sortIndex");
//        maping.put("crawler_status","crawlerStatus");
//        String sql = "SELECT * FROM ecommerce_product_detail where product_name is not null LIMIT " + startLimit + "," + count;
//        sql2o.setDefaultColumnMappings(maping);
//        List<ECommerceProductDetail> list = null;
//        try (Connection con = sql2o.beginTransaction()) {
//            list = con.createQuery(sql).executeAndFetch(ECommerceProductDetail.class);
//        }
//        return list;
//    }

    /**
     * 返回产品名不为空的的产品详情。
     * @param fromIndex 开始sort_index
     * @param toIndex   结束的sort_index
     * @return 包含fromIndex 不包含toIndex
     */
    public List<ECommerceProductDetail> findProductNameIsNotNullProductsBySortIndex(int fromIndex,int toIndex) {
        List<ECommerceProductDetail> list = new ArrayList<ECommerceProductDetail>();
        Connection connection = MultiDataSource.getInstance().getConnection(dbName);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql = "SELECT * FROM ecommerce_product_detail where sort_index >= ? and sort_index < ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,fromIndex);
            preparedStatement.setInt(2,toIndex);
            resultSet = preparedStatement.executeQuery();
            ECommerceProductDetail eCommerceProductDetail = null;
            while(resultSet.next()) {
                eCommerceProductDetail = new ECommerceProductDetail();
                String id = resultSet.getString("id");
                eCommerceProductDetail.setId(id);

                String eCommerceCategoryId = resultSet.getString("ecommerce_category_id");
                eCommerceProductDetail.setECommerceCategoryId(eCommerceCategoryId);

                String eCommerceCategoryFullPath = resultSet.getString("ecommerce_category_full_path");
                eCommerceProductDetail.setEcommerceCategoryFullPath(eCommerceCategoryFullPath);

                String url = resultSet.getString("url");
                eCommerceProductDetail.setUrl(url);

                String productName = resultSet.getString("product_name");
                eCommerceProductDetail.setProductName(productName);

                String currentPrice = resultSet.getString("current_price");
                eCommerceProductDetail.setCurrentPrice(currentPrice);

                String mainPictureUrl = resultSet.getString("main_picture_url");
                eCommerceProductDetail.setMainPictureUrl(mainPictureUrl);

                String categoryLevels = resultSet.getString("category_levels");
                eCommerceProductDetail.setCategoryLevels(categoryLevels);

                String productSubName = resultSet.getString("product_sub_name");
                eCommerceProductDetail.setProductSubName(productSubName);

                String itemCondition = resultSet.getString("item_condition");
                eCommerceProductDetail.setItemCondition(itemCondition);

                String restPicturesUrl = resultSet.getString("rest_pictures_url");
                eCommerceProductDetail.setRestPicturesUrl(restPicturesUrl);

                String originalPrice = resultSet.getString("original_price");
                eCommerceProductDetail.setOriginalPrice(originalPrice);

                String itemSpecifics = resultSet.getString("item_specifics");
                eCommerceProductDetail.setItemSpecifics(itemSpecifics);

                String productDescription = resultSet.getString("product_description");
                eCommerceProductDetail.setProductDescription(productDescription);

                String crawlerTaskId = resultSet.getString("crawler_task_id");
                eCommerceProductDetail.setCrawlerTaskId(crawlerTaskId);

                Date createdTime = resultSet.getDate("created_time");
                eCommerceProductDetail.setCreatedTime(createdTime);

                int sortIndex = resultSet.getInt("sort_index");
                eCommerceProductDetail.setSortIndex(sortIndex);

                String attribute1 = resultSet.getString("attribute1");
                eCommerceProductDetail.setAttribute1(attribute1);

                String attribute2 = resultSet.getString("attribute2");
                eCommerceProductDetail.setAttribute2(attribute2);

                String attribute3 = resultSet.getString("attribute3");
                eCommerceProductDetail.setAttribute3(attribute3);

                String memberId = resultSet.getString("member_id");
                eCommerceProductDetail.setMemberId(memberId);

                String sold = resultSet.getString("sold");
                eCommerceProductDetail.setSold(sold);

                String mbgLink = resultSet.getString("mbg_link");
                eCommerceProductDetail.setMbgLink(mbgLink);

                String feedbackCount = resultSet.getString("feedback_count");
                eCommerceProductDetail.setFeedbackCount(feedbackCount);

                String feedbackCountLink = resultSet.getString("feedback_count_link");
                eCommerceProductDetail.setFeedbackCountLink(feedbackCountLink);

                String soldHistoryUrl = resultSet.getString("sold_history_url");
                eCommerceProductDetail.setSoldHistoryUrl(soldHistoryUrl);

                boolean crawlerStatus = resultSet.getBoolean("crawler_status");
                eCommerceProductDetail.setCrawlerStatus(crawlerStatus);

                list.add(eCommerceProductDetail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
                if(preparedStatement != null) {
                    preparedStatement.close();
                }
                if(connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(list.size() > 0) {
            list = list.stream().filter(p-> Utils.isNotBlank(p.getProductName())).collect(Collectors.toList());
        }
        return list;
    }

    public int [] findMinAndMaxSortIndex() {
        List<ECommerceProductDetail> list = new ArrayList<ECommerceProductDetail>();
        Connection connection = MultiDataSource.getInstance().getConnection(dbName);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int [] minMax = new int[2];
        String sql = "SELECT MIN(sort_index) AS min_index,MAX(sort_index) AS max_index FROM ecommerce_product_detail";
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
               int min = resultSet.getInt("min_index");
               int max = resultSet.getInt("max_index");
               minMax[0] = min;
               minMax[1] = max;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
                if(preparedStatement != null) {
                    preparedStatement.close();
                }
                if(connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return minMax;
    }

//    public int findCountIsProductNameNotNull() {
//        String sql = "select count(*) from ecommerce_product_detail where product_name is not null";
//        try(Connection con = sql2o.beginTransaction()) {
//            return (Integer)con.createQuery(sql).executeScalar();
//        }
//    }
}
