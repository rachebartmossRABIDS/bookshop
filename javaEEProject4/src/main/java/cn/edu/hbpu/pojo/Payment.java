package cn.edu.hbpu.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Payment implements Serializable {
    private Integer pid;
    private Integer oid;
    private String channel;
    private BigDecimal amount;
    private String status;
    private String tradeNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    private static final long serialVersionUID = 1L;
}
