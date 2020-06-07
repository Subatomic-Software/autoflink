package org.slotterback.serdes.serialization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvSerializationSchema extends GenericSerializationSchema{

    @Override
    public byte[] serialize(Map<String, Object> element) {
        List list = new ArrayList();
        buildValueList(element, list);
        String csv = (String) list.stream().map(str -> str.toString()).collect(Collectors.joining(","));
        return csv.getBytes();
    }

    private void buildValueList(Map map, List list){
        for(Object key : map.keySet()){
            if(!(map.get(key) instanceof Map)){
                list.add(map.get(key));
            }else{
                buildValueList((Map) map.get(key), list);
            }
        }
    }
}
