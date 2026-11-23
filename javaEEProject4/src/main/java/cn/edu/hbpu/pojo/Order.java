package cn.edu.hbpu.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName order
 */
@Data
public class Order implements Serializable {
    /** 订单状态：待付款 */
    public static final String STATE_UNPAID = "0";
    /** 订单状态：已付款 */
    public static final String STATE_PAID = "1";
    /** 订单状态：已发货 */
    public static final String STATE_SHIPPED = "2";
    /** 订单状态：已收货（终态） */
    public static final String STATE_RECEIVED = "3";
    /** 订单状态：交易成功（终态，历史演示数据） */
    public static final String STATE_COMPLETED = "4";
    /** 订单状态：已取消（终态） */
    public static final String STATE_CANCELLED = "5";
    /** 订单状态：已退款（终态） */
    public static final String STATE_REFUNDED = "6";

    /**
     * 订单编号
     */
    private Integer oid;

    /**
     * 用户编号
     */
    private Integer uid;

    /**
     * 书籍编号
     */
    private Integer bid;

    /**
     * 下单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * 订单金额（下单时单价×数量快照）
     */
    private BigDecimal amount;

    /**
     * 收件地址
     */
    private String addr;

    /**
     * 订单状态（0待付款、1已付款、2已发货、3已收货、4交易成功、5已取消）
     */
    private String state;

    /**
     * 收件人
     */
    private String name;

    /**
     * 收件人电话
     */
    private String tel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shipTime;

    private String logisticsCompany;

    private String logisticsNo;

    private User user;

    private Book book;
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Order other = (Order) that;
        return (this.getOid() == null ? other.getOid() == null : this.getOid().equals(other.getOid()))
            && (this.getUid() == null ? other.getUid() == null : this.getUid().equals(other.getUid()))
            && (this.getBid() == null ? other.getBid() == null : this.getBid().equals(other.getBid()))
            && (this.getTime() == null ? other.getTime() == null : this.getTime().equals(other.getTime()))
            && (this.getNum() == null ? other.getNum() == null : this.getNum().equals(other.getNum()))
            && (this.getAmount() == null ? other.getAmount() == null : this.getAmount().equals(other.getAmount()))
            && (this.getAddr() == null ? other.getAddr() == null : this.getAddr().equals(other.getAddr()))
            && (this.getState() == null ? other.getState() == null : this.getState().equals(other.getState()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getTel() == null ? other.getTel() == null : this.getTel().equals(other.getTel()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOid() == null) ? 0 : getOid().hashCode());
        result = prime * result + ((getUid() == null) ? 0 : getUid().hashCode());
        result = prime * result + ((getBid() == null) ? 0 : getBid().hashCode());
        result = prime * result + ((getTime() == null) ? 0 : getTime().hashCode());
        result = prime * result + ((getNum() == null) ? 0 : getNum().hashCode());
        result = prime * result + ((getAmount() == null) ? 0 : getAmount().hashCode());
        result = prime * result + ((getAddr() == null) ? 0 : getAddr().hashCode());
        result = prime * result + ((getState() == null) ? 0 : getState().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getTel() == null) ? 0 : getTel().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", oid=").append(oid);
        sb.append(", uid=").append(uid);
        sb.append(", bid=").append(bid);
        sb.append(", time=").append(time);
        sb.append(", num=").append(num);
        sb.append(", amount=").append(amount);
        sb.append(", addr=").append(addr);
        sb.append(", state=").append(state);
        sb.append(", name=").append(name);
        sb.append(", tel=").append(tel);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
