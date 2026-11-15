package cn.edu.hbpu.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName book
 */
@Data
public class Book implements Serializable {
    /**
     * 书籍编号
     */
    private Integer bid;

    /**
     * 书籍名称
     */
    private String name;

    /**
     * 出版时间（XX年XX月）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date time;

    /**
     * 出版社
     */
    private String press;

    /**
     * 作者
     */
    private String author;

    /**
     * ISBN号
     */
    private String isbn;

    /**
     * 图片
     */
    private String image;

    /**
     * 书籍目录
     */
    private String backlist;

    /**
     * 库存
     */
    private String inventory;

    /**
     * 销量
     */
    private String volume;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 简介
     */
    private String description;

    /**
     * 所属商家ID
     */
    private Integer sid;

    /**
     * 书籍状态（int类型）（0上架/1下架）
     */
    private Integer state;

    /**
     * 
     */
    private Integer kid;

    /**
     * 
     */
    private String content;

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
        Book other = (Book) that;
        return (this.getBid() == null ? other.getBid() == null : this.getBid().equals(other.getBid()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getTime() == null ? other.getTime() == null : this.getTime().equals(other.getTime()))
            && (this.getPress() == null ? other.getPress() == null : this.getPress().equals(other.getPress()))
            && (this.getAuthor() == null ? other.getAuthor() == null : this.getAuthor().equals(other.getAuthor()))
            && (this.getIsbn() == null ? other.getIsbn() == null : this.getIsbn().equals(other.getIsbn()))
            && (this.getImage() == null ? other.getImage() == null : this.getImage().equals(other.getImage()))
            && (this.getBacklist() == null ? other.getBacklist() == null : this.getBacklist().equals(other.getBacklist()))
            && (this.getInventory() == null ? other.getInventory() == null : this.getInventory().equals(other.getInventory()))
            && (this.getVolume() == null ? other.getVolume() == null : this.getVolume().equals(other.getVolume()))
            && (this.getPrice() == null ? other.getPrice() == null : this.getPrice().equals(other.getPrice()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getSid() == null ? other.getSid() == null : this.getSid().equals(other.getSid()))
            && (this.getState() == null ? other.getState() == null : this.getState().equals(other.getState()))
            && (this.getKid() == null ? other.getKid() == null : this.getKid().equals(other.getKid()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getBid() == null) ? 0 : getBid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getTime() == null) ? 0 : getTime().hashCode());
        result = prime * result + ((getPress() == null) ? 0 : getPress().hashCode());
        result = prime * result + ((getAuthor() == null) ? 0 : getAuthor().hashCode());
        result = prime * result + ((getIsbn() == null) ? 0 : getIsbn().hashCode());
        result = prime * result + ((getImage() == null) ? 0 : getImage().hashCode());
        result = prime * result + ((getBacklist() == null) ? 0 : getBacklist().hashCode());
        result = prime * result + ((getInventory() == null) ? 0 : getInventory().hashCode());
        result = prime * result + ((getVolume() == null) ? 0 : getVolume().hashCode());
        result = prime * result + ((getPrice() == null) ? 0 : getPrice().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getSid() == null) ? 0 : getSid().hashCode());
        result = prime * result + ((getState() == null) ? 0 : getState().hashCode());
        result = prime * result + ((getKid() == null) ? 0 : getKid().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", bid=").append(bid);
        sb.append(", name=").append(name);
        sb.append(", time=").append(time);
        sb.append(", press=").append(press);
        sb.append(", author=").append(author);
        sb.append(", isbn=").append(isbn);
        sb.append(", image=").append(image);
        sb.append(", backlist=").append(backlist);
        sb.append(", inventory=").append(inventory);
        sb.append(", volume=").append(volume);
        sb.append(", price=").append(price);
        sb.append(", description=").append(description);
        sb.append(", sid=").append(sid);
        sb.append(", state=").append(state);
        sb.append(", kid=").append(kid);
        sb.append(", content=").append(content);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}