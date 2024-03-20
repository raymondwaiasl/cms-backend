package com.asl.prd004.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.Transient;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2023/2/24 13:49
 */
@Entity
@NoArgsConstructor
@DynamicInsert
@Data
@ApiModel(value = "文件对象", description = "文件对象")
@Table(name = "mis_file_list", schema = "MIS", catalog = "")
public class FileListEntity implements Serializable {
    /**
     * 主键ID
     */
    @Id
    @ApiModelProperty(value = "主键ID")
    private String id;

    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    private String filename;

    /**
     * 文件标识MD5
     */
    @ApiModelProperty(value = "文件标识MD5")
    private String identifier;

    /**
     * 总大小
     */
    @ApiModelProperty(value = "总大小")
    private Long totalSize;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String location;

    /**
     * 是否删除: 0.否 1.是
     */
    @ApiModelProperty(value = "是否删除: 0.否 1.是")
    private Integer delFlag;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 文件大小带单位
     */
    @Transient
    @ApiModelProperty(value = "文件大小带单位")
    private String totalSizeName;

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
        if(1024*1024 > this.totalSize && this.totalSize >= 1024 ) {
            this.totalSizeName = String.format("%.2f",this.totalSize.doubleValue()/1024) + "KB";
        }else if(1024*1024*1024 > this.totalSize && this.totalSize >= 1024*1024 ) {
            this.totalSizeName = String.format("%.2f",this.totalSize.doubleValue()/(1024*1024)) + "MB";
        }else if(this.totalSize >= 1024*1024*1024 ) {
            this.totalSizeName = String.format("%.2f",this.totalSize.doubleValue()/(1024*1024*1024)) + "GB";
        }else {
            this.totalSizeName = this.totalSize.toString() + "B";
        }
    }
}


