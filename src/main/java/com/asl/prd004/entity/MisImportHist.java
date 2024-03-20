package com.asl.prd004.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "MIS_IMPORT_HIST", schema = "MIS", catalog = "")
public class MisImportHist {
    @Id
    @Column(name = "MIS_IMPORT_ID", nullable = false, length = 20)
    private String misImportId;
    @Column(name = "MIS_IMPORT_FILENAME", nullable = true, length = 64)
    private String misImportFilename;
    @Column(name = "MIS_EXCEL_TYPE", nullable = true, length = 6)
    private String misExcelType;
    @Column(name = "MIS_EXPORT_FILENAME", nullable = true, length = 32)
    private String misExportFilename;
    @Column(name = "MIS_DECRYPT_PASS", nullable = true, length = 100)
    private String misDecryptPass;
    @Column(name = "MIS_CONFIRM_PASS", nullable = true, length = 100)
    private String misConfirmPass;
    @Column(name = "MIS_IMPORT_DATE", nullable = true, length = 32)
    private String misImportDate;
    @Column(name = "MIS_IMPORT_USER_ID", nullable = true, length = 32)
    private String misImportUserId;
}
