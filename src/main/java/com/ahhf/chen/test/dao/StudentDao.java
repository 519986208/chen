package com.ahhf.chen.test.dao;

import com.ahhf.chen.test.bean.Student;

public interface StudentDao {

    public void insert(Student student);

    public Student selectByPrimaryKey(Long id);

    public void deleteByPrimaryKey(Long id);

    public void updateByPrimaryKeySelective(Student student);

}
