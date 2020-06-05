package org.slotterback;

import java.lang.reflect.Field;

public class ValidationRunner {

    public static void main(String[] args) {
        StreamBuilderUtil streamBuilderUtil = new StreamBuilderUtil();
        Class<?>[] tmp = streamBuilderUtil.getClass().getClasses();

        Field[] fields = tmp[0].getFields();
        Class<?>[] classes = tmp[0].getClasses();
        
        System.exit(1);
    }
}
