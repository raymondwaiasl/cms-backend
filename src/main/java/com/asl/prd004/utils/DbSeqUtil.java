package com.asl.prd004.utils;

import com.asl.prd004.dao.MisSequenceDao;
import com.asl.prd004.entity.MisSequence;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Optional;

@Component
public class DbSeqUtil {

    @Resource
    private MisSequenceDao sequenceDao;

    @Resource
    private JdbcTemplate jdbcTemplate;

    public static DbSeqUtil dbSeqUtil;

    @PostConstruct
    public void init() {
        dbSeqUtil = this;
    }

    public static void updateDbSeq(String tableName, int seq) {
        MisSequence sequence = null;
        Optional<MisSequence> opt = dbSeqUtil.sequenceDao.findById(tableName);
        if (opt.isPresent()) {
            sequence = opt.get();
            sequence.setCurNo(seq);
            dbSeqUtil.jdbcTemplate.execute("update mis_sequence set cur_no = " + seq + " where seq_name = '" + tableName + "';");
        }

    }

    public static int getDbSeq(String tableName) {
        MisSequence sequence = null;
        Optional<MisSequence> opt = dbSeqUtil.sequenceDao.findById(tableName);
        if (opt.isPresent()) {
            sequence = opt.get();
            return sequence.getCurNo();
        } else {
            return -1;
        }
    }

    public static String getDbPrefix(String tableName) {
        MisSequence sequence = null;
        Optional<MisSequence> opt = dbSeqUtil.sequenceDao.findById(tableName);
        if (opt.isPresent()) {
            sequence = opt.get();
            return sequence.getPrefix();
        } else {
            return null;
        }
    }

    public static void initSeq(String tableName) {

        MisSequence sequence = new MisSequence();
        sequence.setSeqName(tableName);
        sequence.setCurNo(1);
        sequence.setCreateTime(new Timestamp(System.currentTimeMillis()));
        sequence.setPrefix(SerialNumberUtils.getPrefixSequence());
        sequence.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        dbSeqUtil.sequenceDao.saveAndFlush(sequence);
    }
}
