package cn.edu.hbpu.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 用户收藏
 * @TableName favorite
 */
@Data
public class Favorite implements Serializable {
    /**
     * 收藏编号
     */
    private Integer fid;

    /**
     * 用户编号
     */
    private Integer uid;

    /**
     * 书籍编号
     */
    private Integer bid;

    /**
     * 收藏时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    /**
     * 关联书籍（列表展示用）
     */
    private Book book;

    private static final long serialVersionUID = 1L;
}