package cn.edu.hbpu.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OperationLog implements Serializable {
    private Integer id;
    private Integer uid;
    private String action;
    private String detail;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    private static final long serialVersionUID = 1L;
}
