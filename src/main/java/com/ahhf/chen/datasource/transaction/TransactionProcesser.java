package com.ahhf.chen.datasource.transaction;

@FunctionalInterface
public interface TransactionProcesser {

    public Object execute(Object... params) throws Exception;

}
