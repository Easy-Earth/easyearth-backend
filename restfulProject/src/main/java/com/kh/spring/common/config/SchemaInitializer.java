package com.kh.spring.common.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking for INTEGRATED_HISTORY table...");

        try {
            jdbcTemplate.queryForObject("SELECT count(*) FROM INTEGRATED_HISTORY WHERE 1=0", Integer.class);
            log.info("INTEGRATED_HISTORY table already exists.");
        } catch (Exception e) {
            log.info("INTEGRATED_HISTORY table not found. Creating table...");

            jdbcTemplate.execute("CREATE TABLE INTEGRATED_HISTORY (" +
                    "HISTORY_ID NUMBER PRIMARY KEY, " +
                    "MEMBER_ID NUMBER NOT NULL, " +
                    "ACTIVITY_TYPE VARCHAR2(20) NOT NULL, " +
                    "REF_ID NUMBER NOT NULL, " +
                    "STATUS CHAR(1) DEFAULT 'P', " +
                    "IMAGE_URL VARCHAR2(500), " +
                    "CREATED_AT DATE DEFAULT SYSDATE)");

            try {
                jdbcTemplate.execute("CREATE SEQUENCE SEQ_INTEGRATED_HISTORY_ID START WITH 1");
            } catch (Exception seqE) {
                log.info("Sequence SEQ_INTEGRATED_HISTORY_ID might already exist.");
            }

            try {
                jdbcTemplate.execute(
                        "CREATE INDEX IDX_HISTORY_MEMBER_DATE ON INTEGRATED_HISTORY(MEMBER_ID, TRUNC(CREATED_AT))");
            } catch (Exception idxE) {
                log.info("Index IDX_HISTORY_MEMBER_DATE might already exist.");
            }

            log.info("INTEGRATED_HISTORY table created successfully.");
        }

        try {
            jdbcTemplate.execute("CREATE SEQUENCE SEQ_ATTENDANCE_ID START WITH 1");
            log.info("SEQ_ATTENDANCE_ID created.");
        } catch (Exception e) {
        }
    }
}
