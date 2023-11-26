package io.github.ximutech.spore.test.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author ximu
 */
@Data
@Accessors(chain = true)
public class HitokotoVO implements Serializable {

    private Long id;
    private String uuid;
    private String hitokoto;
}
