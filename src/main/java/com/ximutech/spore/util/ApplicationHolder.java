package com.ximutech.spore.util;

/**
 * @author ximu
 */
public class ApplicationHolder {
    private static String springApplicationName = "application";


    public static String getSpringApplicationName() {
        return springApplicationName;
    }

    public static void setSpringApplicationName(String springApplicationName) {
        ApplicationHolder.springApplicationName = springApplicationName;
    }
}
