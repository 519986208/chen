package com.ahhf.chen.excel;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.read.context.AnalysisContext;
import com.alibaba.excel.read.event.AnalysisEventListener;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
public class ExcelListener<T extends BaseRowModel> extends AnalysisEventListener<T> {

    private List<T> datas = new ArrayList<>();

    /**
     * 通过 AnalysisContext 对象还可以获取当前 sheet，当前行等数据
     */
    @Override
    public void invoke(T object, AnalysisContext context) {
        //数据存储到list，供批量处理，或后续自己业务逻辑处理。
        datas.add(object);
        //根据自己业务做处理
        doSomething(object);
    }

    private void doSomething(Object object) {
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        Integer currentRowNum = context.getCurrentRowNum();
        log.info("easyexcel deal finished ! total size : " + currentRowNum);
    }

}
