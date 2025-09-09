package com.sky.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;

    private String username;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;

}
