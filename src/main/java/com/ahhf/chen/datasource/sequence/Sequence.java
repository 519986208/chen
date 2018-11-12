package com.ahhf.chen.datasource.sequence;

/**
 * 类Sequence.java的实现描述：序列接口
 */
public interface Sequence {

    /**
     * 取得序列下一个值
     * 
     * @return 返回序列下一个值
     * @throws SequenceException
     */
    long nextValue();

}
