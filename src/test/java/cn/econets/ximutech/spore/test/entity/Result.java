package cn.econets.ximutech.spore.test.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private int code;
    private String msg;
    private T data;
}
