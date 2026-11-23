package cn.edu.hbpu.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Notification implements Serializable {
    private Integer nid;
    private Integer uid;
    private String title;
    private String content;
    private Integer isRead;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    private static final long serialVersionUID = 1L;
}
