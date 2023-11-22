package io.github.ximutech.spore.test;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ximu
 */
@Data
public class HitokotoVO implements Serializable {

    private Long id;
    private String uuid;
    private String hitokoto;
}
