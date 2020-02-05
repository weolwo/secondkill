package com.poplar.bean;

import lombok.Data;

import java.util.Date;

/**
 * by poplar created on 2020/2/5
 */
@Data
public class User {
    private Long id;

    private String nickname;

    private String password;

    private String salt;

    private String head;

    private Date registerDate;

    private Date lastLoginDate;

    private Integer loginCount;
}
