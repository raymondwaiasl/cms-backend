package com.asl.prd004.entity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2023/2/24 13:41
 */
@Entity
@Table(name = "mis_chunk", schema = "MIS", catalog = "")
public class ChunkEntity  implements Serializable{
    /**
     * 主键ID
     */
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    private String id;
    /**
     * 文件块编号，从1开始
     */

    private Integer chunkNumber;
    /**
     * 每块大小
     */
    private Long chunkSize;
    /**
     * 当前分块大小
     */
    private Long currentChunkSize;
    /**
     * 总大小
     */
    private Long totalSize;
    /**
     * 文件标识MD5
     */
    private String identifier;
    /**
     * 文件名
     */
    private String filename;
    /**
     * 相对路径
     */
    private String relativePath;
    /**
     * 总块数
     */
    private Integer totalChunks;

    /**
     * 块内容
     */

    private byte[] upfile;
    @Id
    @Column(name = "id", length = 20)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @Column(name = "chunk_number", length = 20)
    public Integer getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(Integer chunkNumber) {
        this.chunkNumber = chunkNumber;
    }
    @Column(name = "chunk_size", length = 20)
    public Long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Long chunkSize) {
        this.chunkSize = chunkSize;
    }
    @Column(name = "current_chunk_size", length = 20)
    public Long getCurrentChunkSize() {
        return currentChunkSize;
    }

    public void setCurrentChunkSize(Long currentChunkSize) {
        this.currentChunkSize = currentChunkSize;
    }
    @Column(name = "total_size", length = 20)
    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }
    @Column(name = "identifier", length = 20)
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    @Column(name = "filename", length = 20)
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    @Column(name = "relative_path", length = 20)
    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
    @Column(name = "total_chunks", length = 20)
    public Integer getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(Integer totalChunks) {
        this.totalChunks = totalChunks;
    }
    @Column(name = "upfile", columnDefinition = "MediumBlob")
    public byte[] getUpfile() {
        return upfile;
    }

    public void setUpfile(byte[] upfile) {
        this.upfile = upfile;
    }
}


