package com.ahhf.chen.datasource.transaction;

/**
 * 事务合并处理，将需要处理的sql最小范围内一起执行
 */
@FunctionalInterface
public interface TransactionService {

    public Object doInTransaction(TransactionProcesser processer, Object... params) throws Exception;

}
