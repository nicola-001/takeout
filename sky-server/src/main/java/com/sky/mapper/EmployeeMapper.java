package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.sky.annotaton.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    //分页查询
    Page<Employee> page(EmployeePageQueryDTO employeePageQueryDTO);

    //更新操作
    @AutoFill(OperationType.UPDATE)
    void update(Employee employee);

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
//    @Select("select * from employee where username = #{username}")
//    Employee getByUsername(String username);

//    @Insert("insert into employee (username, name, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
//            "values (#{username}, #{name}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
//    void insert(Employee employee);
}
