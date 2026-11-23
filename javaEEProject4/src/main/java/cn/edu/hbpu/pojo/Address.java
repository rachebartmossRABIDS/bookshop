package cn.edu.hbpu.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Address implements Serializable {
    private Integer aid;
    private Integer uid;
    private String name;
    private String tel;
    private String addr;
    private Integer isDefault;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    private static final long serialVersionUID = 1L;
}
