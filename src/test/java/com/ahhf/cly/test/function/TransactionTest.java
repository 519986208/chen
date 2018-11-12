package com.ahhf.cly.test.function;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ahhf.chen.ChenApp;
import com.ahhf.chen.datasource.sequence.Sequence;
import com.ahhf.chen.datasource.transaction.TransactionService;
import com.ahhf.chen.test.bean.Student;
import com.ahhf.chen.test.dao.StudentDao;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ChenApp.class)
public class TransactionTest {

    @Resource
    private TransactionService transactionService;

    @Resource
    private StudentDao         studentDao;

    @Resource
    private Sequence           personSequence;

    @Test
    public void fdasfdsa() {
        try {
            long nextValue = personSequence.nextValue();
            transactionService.doInTransaction(() -> {
                Student student = new Student();
                student.setId(nextValue);
                student.setAge(34);
                student.setSource("weixin");
                student.setIp("127.0.0.1");
                student.setName("fdafrgt65432");
                studentDao.insert(student);
                System.out.println(111111);
                //                studentDao.deleteByPrimaryKey(nextValue);
                student.setSource("wechat");
                //                int i = 1 / 0;
                //                System.out.println(i);
                studentDao.updateByPrimaryKeySelective(student);
                return 11;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
