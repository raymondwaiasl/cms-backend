package com.asl.prd004.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "mis_user", schema = "MIS", catalog = "")
public class MisUser extends BaseModel  {
    private String misUserId;
    private String misUserName;
    private String misUserLoginId;
    private String misUserType;
    private String misEmail;
    private String misUserPassword;
    private String misUserStatus;
    private String delFlag;
    private String misUserLocation;
    private Timestamp loginDate;
    private String remark;
    private String avatar;
    private String isAdmin;
    private String passwordHistory;
    private int loginAttempts ;
    private boolean isLocked ;
    private boolean isChange ;
    private Timestamp lastPasswordChange ;
    private String currentGroup;
    private String surnameEng;
    private String givenNameEng;
    private String otherNameEng;
    private String district;
    private String tel;
    private String fax;

    @Id
   @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_user_id", nullable = false)
    public String getMisUserId() {
        return misUserId;
    }

    public void setMisUserId(String misUserId) {
        this.misUserId = misUserId;
    }

    @Basic
    @Column(name = "mis_user_name", nullable = false, length = 30)
    public String getMisUserName() {
        return misUserName;
    }

    public void setMisUserName(String misUserName) {
        this.misUserName = misUserName;
    }

    @Basic
    @Column(name = "mis_user_login_id", nullable = false, length = 30)
    public String getMisUserLoginId() {
        return misUserLoginId;
    }

    public void setMisUserLoginId(String misUserLoginId) {
        this.misUserLoginId = misUserLoginId;
    }

    @Basic
    @Column(name = "mis_user_type", nullable = true, length = 2)
    public String getMisUserType() {
        return misUserType;
    }

    public void setMisUserType(String misUserType) {
        this.misUserType = misUserType;
    }

    @Basic
    @Column(name = "mis_email", nullable = true, length = 50)
    public String getMisEmail() {
        return misEmail;
    }

    public void setMisEmail(String misEmail) {
        this.misEmail = misEmail;
    }

    @Basic
    @Column(name = "mis_user_password", nullable = true, length = 100)
    public String getMisUserPassword() {
        return misUserPassword;
    }

    public void setMisUserPassword(String misUserPassword) {
        this.misUserPassword = misUserPassword;
    }

    @Basic
    @Column(name = "mis_user_status", nullable = true, length = 1)
    public String getMisUserStatus() {
        return misUserStatus;
    }

    public void setMisUserStatus(String misUserStatus) {
        this.misUserStatus = misUserStatus;
    }

    @Basic
    @Column(name = "del_flag", nullable = true, length = 1)
    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Basic
    @Column(name = "mis_user_location", nullable = true, length = 128)
    public String getMisUserLocation() {
        return misUserLocation;
    }

    public void setMisUserLocation(String misUserLocation) {
        this.misUserLocation = misUserLocation;
    }

    @Basic
    @Column(name = "login_date", nullable = true)
    public Timestamp getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Timestamp loginDate) {
        this.loginDate = loginDate;
    }


    @Basic
    @Column(name = "remark", nullable = true, length = 500)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Basic
    @Column(name = "avatar", nullable = true, length = 255)
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Basic
    @Column(name = "is_admin", nullable = true, length = 2)
    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Basic
    @Column(name = "password_history", nullable = true, length = 800)
    public String getPasswordHistory() {
        return passwordHistory;
    }

    public void setPasswordHistory(String passwordHistory) {
        this.passwordHistory = passwordHistory;
    }

    @Basic
    @Column(name = "login_attempts", nullable = true)
    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    @Basic
    @Column(name = "is_locked", nullable = true)
    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    @Basic
    @Column(name = "is_change", nullable = true)
    public boolean isChange() {
        return isChange;
    }

    public void setChange(boolean change) {
        isChange = change;
    }

    @Basic
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone= "GMT+8")
    @Column(name = "last_password_change", nullable = true)
    public Timestamp getLastPasswordChange() {
        return lastPasswordChange;
    }

    public void setLastPasswordChange(Timestamp lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }

    @Basic
    @Column(name = "current_group", nullable = true, length = 16)
    public String getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(String currentGroup) {
        this.currentGroup = currentGroup;
    }

    @Basic
    @Column(name = "surname_eng", nullable = true, length = 100)
    public String getSurnameEng() {
        return surnameEng;
    }

    public void setSurnameEng(String surnameEng) {
        this.surnameEng = surnameEng;
    }

    @Basic
    @Column(name = "given_name_eng", nullable = true, length = 100)
    public String getGivenNameEng() {
        return givenNameEng;
    }

    public void setGivenNameEng(String givenNameEng) {
        this.givenNameEng = givenNameEng;
    }

    @Basic
    @Column(name = "other_name_eng", nullable = true, length = 100)
    public String getOtherNameEng() {
        return otherNameEng;
    }

    public void setOtherNameEng(String otherNameEng) {
        this.otherNameEng = otherNameEng;
    }

    @Basic
    @Column(name = "district", nullable = true, length = 50)
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Basic
    @Column(name = "tel", nullable = true, length = 50)
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Basic
    @Column(name = "fax", nullable = true, length = 50)
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    private String userPost;

    @Column(name = "user_post", length = 100)
    public String getUserPost() {
        return userPost;
    }

    public void setUserPost(String userPost) {
        this.userPost = userPost;
    }

    private String office;

    @Column(name = "office", length = 10)
    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MisUser misUser = (MisUser) o;
        return Objects.equals(misUserId, misUser.misUserId) && Objects.equals(misUserName, misUser.misUserName) && Objects.equals(misUserLoginId, misUser.misUserLoginId) && Objects.equals(misUserType, misUser.misUserType) && Objects.equals(misEmail, misUser.misEmail) && Objects.equals(misUserPassword, misUser.misUserPassword) && Objects.equals(misUserStatus, misUser.misUserStatus) && Objects.equals(delFlag, misUser.delFlag) && Objects.equals(misUserLocation, misUser.misUserLocation) && Objects.equals(loginDate, misUser.loginDate) && Objects.equals(remark, misUser.remark) && Objects.equals(avatar, misUser.avatar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), misUserId, misUserName, misUserLoginId, misUserType, misEmail, misUserPassword, misUserStatus, delFlag, misUserLocation, loginDate, remark, avatar);
    }
}
