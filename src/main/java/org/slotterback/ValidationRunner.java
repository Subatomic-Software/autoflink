package org.slotterback;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidationRunner {

    public static void main(String[] args) throws IllegalAccessException {


        StreamBuilderUtil streamBuilderUtil = new StreamBuilderUtil();
        Class base = streamBuilderUtil.getClass().getClasses()[0];

        Field[] baseFields = base.getFields();
        Class[] baseClasses = base.getClasses();

        Map baseFunctionMap = new HashMap();
        for (Class clazz: baseClasses) {
            Field[] tmp = clazz.getFields();
            Class[] tmp2 = clazz.getClasses();
            baseFunctionMap.put(tmp[0], tmp2);
        }

        Map functionMap = new HashMap();

        for(Object clazzes : baseFunctionMap.keySet()) {
            for (Class clazz : (Class[]) baseFunctionMap.get(clazzes)){
                Field[] fields = clazz.getFields();
                ArrayList list = new ArrayList(Arrays.asList(fields));

                Class[] subclazzes = clazz.getClasses();
                if(subclazzes != null){
                    for(Class subclazz : subclazzes){
                        list.addAll(Arrays.asList(subclazz.getFields()));
                    }
                }

                functionMap.put(clazz, list);
                int i = 1;
            }
        }
        System.exit(1);
    }
}
