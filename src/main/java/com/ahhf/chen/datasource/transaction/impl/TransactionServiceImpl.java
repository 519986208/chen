package com.ahhf.chen.datasource.transaction.impl;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.ahhf.chen.datasource.transaction.TransactionProcesser;
import com.ahhf.chen.datasource.transaction.TransactionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("transactionService")
public class TransactionServiceImpl implements TransactionService {

    private DataSourceTransactionManager transactionManager;

    @Resource
    public void setDataSource(DataSource dataSource) {
        if (transactionManager != null) {
            return;
        }
        transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
    }

    @Override
    public Object doInTransaction(TransactionProcesser processer) throws RuntimeException {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            Object obj = processer.execute();
            transactionManager.commit(status);
            log.info("=====transaction commit success ====");
            return obj;
        } catch (Exception ex) {
            log.error("=====transaction error! ====", ex);
            try {
                transactionManager.rollback(status);
                log.info("=====transaction rollback success! ====");
            } catch (TransactionException re) {
                re.initCause(ex);
                log.error("=====transaction rollback error! ====", re);
                throw re;
            }
            throw new RuntimeException(ex);
        }
    }

}
