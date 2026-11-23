package cn.edu.hbpu.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 购物车条目
 * @TableName cart
 */
@Data
public class Cart implements Serializable {
    /**
     * 购物车编号
     */
    private Integer cid;

    /**
     * 用户编号
     */
    private Integer uid;

    /**
     * 书籍编号
     */
    private Integer bid;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 加入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    /**
     * 关联书籍（含库存/上下架状态，用于前端展示与结算校验）
     */
    private Book book;

    private static final long serialVersionUID = 1L;
}