package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import util.Utils;

/**
 * Created by zn on 2018/6/28.
 */
@Slf4j
public class ECommerceProductDetailDao {
    private String dbName;
    public ECommerceProductDetailDao(String fullDBName) {
        this.dbName = fullDBName;
    }
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
                eCommerceProductDetail = builderECommerceProductDetail(resultSet);
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

    /**
     * 查询前10w有单品销量和店铺销量的数据。
     * @return
     */
    public List<ECommerceProductDetail> findProductDetailBySold(int limit) {
        List<ECommerceProductDetail> list = new ArrayList<ECommerceProductDetail>();
        String sql = "SELECT * FROM ecommerce_product_detail WHERE sold IS NOT NULL and product_name is not null order by sold desc limit " + limit;
        log.info("sql:{}",sql);
        Connection connection = MultiDataSource.getInstance().getConnection(dbName);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            ECommerceProductDetail eCommerceProductDetail = null;
            while(resultSet.next()) {
                eCommerceProductDetail = builderECommerceProductDetail(resultSet);
                list.add(eCommerceProductDetail);
            }
            log.info("list size:{}",list.size());
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
        return list;
    }

    /**
     * 查询前10w有单品销量和店铺销量的数据。
     * @return
     */
    public List<ECommerceProductDetail> findProductDetailByFeedbackCount(int limit) {
        List<ECommerceProductDetail> list = new ArrayList<ECommerceProductDetail>();
        String sql = "SELECT * FROM ecommerce_product_detail WHERE feedback_count IS NOT NULL and product_name is not null order by feedback_count desc limit " + limit;
        log.info("sql:{}",sql);
        Connection connection = MultiDataSource.getInstance().getConnection(dbName);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            ECommerceProductDetail eCommerceProductDetail = null;
            while(resultSet.next()) {
                eCommerceProductDetail = builderECommerceProductDetail(resultSet);
                list.add(eCommerceProductDetail);
            }
            log.info("list size:{}",list.size());
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

    public int findCountIsProductNameNotNull() {
        String sql = "select count(1) as data_total from ecommerce_product_detail where product_name is not null";
        Connection connection = MultiDataSource.getInstance().getConnection(dbName);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                int dataTotal = resultSet.getInt("data_total");
                return dataTotal;
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
        return 0;
    }

    private ECommerceProductDetail builderECommerceProductDetail(ResultSet resultSet) throws SQLException{
        ECommerceProductDetail eCommerceProductDetail = new ECommerceProductDetail();
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

        return eCommerceProductDetail;
    }
}
