package cn.edu.hbpu.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class ResetCode {
    private String email;
    private String code;
    private Date expireTime;
    private Date time;
}
