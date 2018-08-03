package dao;

import lombok.Data;

import java.util.Date;

/**
 * 电商平台产品详情
 * @author wangh
 */
@Data
public class ECommerceProductDetail {

    /**
     * id，用mongodb的objectId生成
     */
    private String id;

    /**
     * 电商品类表ID
     */
    private String eCommerceCategoryId;

    /**
     * 产品分类层级
     */

    private String ecommerceCategoryFullPath = "";
    /**
     * 产品对应的URL
     */

    private String url;

    /**
     * 产品名称
     */

    private String productName;

    /**
     * 产品当前价格
     */

    private String currentPrice;

    /**
     * 产品当前主图地址
     */

    private String mainPictureUrl;

    /**
     * 产品分类层级
     */

    private String categoryLevels;

    /**
     * 产品的附加名称
     */

    private String productSubName;

    /**
     * 产品规格
     */

    private String itemCondition;

    /**
     * 产品小图url，多个小图以","分隔
     */

    private String restPicturesUrl;

    /**
     * 产品原始价格
     */

    private String originalPrice;

    /**
     * 详情里边的item specifics
     */

    private String itemSpecifics;

    /**
     * 销量
     */

    private String sold;

    /**
     * 销量历史url
     */

    private String soldHistoryUrl;


    private String memberId;


    private String mbgLink;


    private String feedbackCount;


    private String feedbackCountLink;


    private String attribute1;


    private String attribute2;


    private String attribute3;

    /**
     * 详情里边的产品描述
     */

    private String productDescription;

    /**
     * 数据创建日期
     */

    private Date createdTime;

    /**
     * 对应爬虫任务ID，为空则表示还未分配给爬虫任务
     */
    private String crawlerTaskId;

    /**
     * 用来排序的自增号
     */
    private int sortIndex;

    /**
     * 爬虫状态。 true:已爬，false：未爬
     */
    private boolean crawlerStatus;

}
