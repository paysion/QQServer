package com.zoc.qqcommon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    // 保证兼容性
    private static final long serialVersionUID = 1L;
    private String userId;//用户Id/用户名
    private String passwd;//用户密码
}
