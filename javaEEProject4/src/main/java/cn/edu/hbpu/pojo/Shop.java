package cn.edu.hbpu.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName shop
 */
@Data
public class Shop implements Serializable {
    /**
     * 店铺编号
     */
    private Integer sid;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺Logo
     */
    private String logo;

    /**
     * 店铺图片
     */
    private String picture;

    /**
     * 店铺星级评分
     */
    private String score;

    /**
     * 店铺简介
     */
    private String description;

    /**
     * 状态（int类型）（0上架/1下架）
     */
    private String state;

    /**
     * 
     */
    private Integer kid;

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
        Shop other = (Shop) that;
        return (this.getSid() == null ? other.getSid() == null : this.getSid().equals(other.getSid()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getLogo() == null ? other.getLogo() == null : this.getLogo().equals(other.getLogo()))
            && (this.getPicture() == null ? other.getPicture() == null : this.getPicture().equals(other.getPicture()))
            && (this.getScore() == null ? other.getScore() == null : this.getScore().equals(other.getScore()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getState() == null ? other.getState() == null : this.getState().equals(other.getState()))
            && (this.getKid() == null ? other.getKid() == null : this.getKid().equals(other.getKid()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getSid() == null) ? 0 : getSid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getLogo() == null) ? 0 : getLogo().hashCode());
        result = prime * result + ((getPicture() == null) ? 0 : getPicture().hashCode());
        result = prime * result + ((getScore() == null) ? 0 : getScore().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getState() == null) ? 0 : getState().hashCode());
        result = prime * result + ((getKid() == null) ? 0 : getKid().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", sid=").append(sid);
        sb.append(", name=").append(name);
        sb.append(", logo=").append(logo);
        sb.append(", picture=").append(picture);
        sb.append(", score=").append(score);
        sb.append(", description=").append(description);
        sb.append(", state=").append(state);
        sb.append(", kid=").append(kid);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}