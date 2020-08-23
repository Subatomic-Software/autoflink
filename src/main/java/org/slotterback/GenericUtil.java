package org.slotterback;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import java.util.*;

public class GenericUtil {

    public static Object getEmbeddedValue(Map<String, Object> map, String fullKey){
        List keys = new LinkedList(Arrays.asList(fullKey.split("_")));
        Object ref = map.get(keys.remove(0));
        for (Object key: keys){
            ref = ((Map) ref).get(key);
        }
        return ref;
    }

    public static void putEmbeddedValue(Map<String, Object> map, String target, Object value){
        List keys = new LinkedList(Arrays.asList(target.split("_")));
        String entry = keys.remove(0).toString();
        Object ref = map.get(entry);
        if(ref == null){
            map.put(entry, new LinkedHashMap<>());
            ref = map.get(entry);
        }
        while (keys.size() > 0){
            Object key = keys.remove(0);
            Map tmpRef = ((Map) ref);
            if (tmpRef.get(key) == null){
                if(keys.size() == 0){
                    tmpRef.put(key, value);
                    return;
                } else {
                    Map newMap = new LinkedHashMap<>();
                    tmpRef.put(key,newMap);
                }
            } else if(keys.size() == 0){
                ((Map) ref).put(key, value);
                return;
            }
            ref = ((Map) ref).get(key);
        }
        map.put(target, value);
    }

    public static void removeEmbeddedValue(Map map, String target) {
        List keys = new LinkedList(Arrays.asList(target.split("_")));
        Object ref = map.get(keys.remove(0));
        while (keys.size() > 0){
            Object key = keys.remove(0);
            Map tmpRef = ((Map) ref);
            if(keys.size() == 0){
                tmpRef.remove(key);
            }
            ref = ((Map) ref).get(key);
        }
        map.remove(target);
    }

    public static Map mapFromGeneric(GenericRecord record){
        Map<String, Object> map = new HashMap<>();

        for (Schema.Field field : record.getSchema().getFields()) {
            if(record.get(field.name()).getClass() == GenericData.Record.class){
                map.put(field.name(), mapFromGeneric((GenericRecord) record.get(field.name())));
            }else{
                map.put(field.name(), record.get(field.name()));
            }
        }

        return map;
    }
}
