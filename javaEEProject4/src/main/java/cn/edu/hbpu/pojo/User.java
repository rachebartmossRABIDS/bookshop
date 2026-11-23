package cn.edu.hbpu.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 
 * @TableName user
 */
@Data
public class User implements Serializable {
    /**
     * 用户编号
     */
    private Integer uid;

    /**
     * 用户姓名
     */
    private String username;

    /**
     * 用户密码
     */
    @JsonIgnore
    private String password;

    /**
     * 用户电话
     */
    private String tel;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户类型（int类型）（1普通用户 2 管理员用户）
     */
    private Integer type;

    /**
     * 用户头像
     */
    private String image;

    /**
     * 信誉等级（星级）
     */
    private String reputation;

    /**
     * 用户状态（int类型）（1封禁/0解封）
     */
    private Integer state;

    /**
     * JWT 令牌版本：改密、封禁时递增，使旧 token 立即失效。
     */
    @JsonIgnore
    private Integer tokenVersion;

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
        User other = (User) that;
        return (this.getUid() == null ? other.getUid() == null : this.getUid().equals(other.getUid()))
            && (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
            && (this.getTel() == null ? other.getTel() == null : this.getTel().equals(other.getTel()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getImage() == null ? other.getImage() == null : this.getImage().equals(other.getImage()))
            && (this.getReputation() == null ? other.getReputation() == null : this.getReputation().equals(other.getReputation()))
            && (this.getState() == null ? other.getState() == null : this.getState().equals(other.getState()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUid() == null) ? 0 : getUid().hashCode());
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getTel() == null) ? 0 : getTel().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getImage() == null) ? 0 : getImage().hashCode());
        result = prime * result + ((getReputation() == null) ? 0 : getReputation().hashCode());
        result = prime * result + ((getState() == null) ? 0 : getState().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", uid=").append(uid);
        sb.append(", username=").append(username);
        sb.append(", password=").append(password);
        sb.append(", tel=").append(tel);
        sb.append(", email=").append(email);
        sb.append(", type=").append(type);
        sb.append(", image=").append(image);
        sb.append(", reputation=").append(reputation);
        sb.append(", state=").append(state);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
