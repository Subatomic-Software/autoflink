# autoflink

## TODO:  
### UI:  
add join  
add schemas loading  
add drop downs for types  
required vs non required  
auto generate names for operators  


### Server:  
add join  
arrays in data  
keyby+aggregation  
regex filter  
filter compare var to var  
flatmap  


## json parts
#### Sources:  

KafkaSource:  
"function": "source",
"type": "kafka",  
"kafka": {  
  "broker": String  
  "topic": String,  
  "groupId": String,  
  "format": {  
    "type": FORMAT_TYPE,  
    "schema": String  
  }  
}  

FileSource:  
"function": "source",  
"type": "file",  
"file": {  
  "directory": String,  
  "format": {  
    "type": FORMAT_TYPE,  
    "schema": String  
  }  
}  

#### Operations:  
Map:  
"function": "operator",  
"type": "map",  
"map": {  
  "operation": MAP_OPERATION,  
  "target": String,  
  "eval": String  
}  

Filter:  
"function": "operator",  
"type": "filter",  
"filter": {  
  "function": String,  
  "value": String,  
  "target": String|Numeric  
}

#### Sinks:  
KafkaSink:  
"function": "sink",  
"type": "kafka",  
"kafka": {  
  "broker": String,  
  "topic": String,  
  "format": {  
      "type": FORMAT_TYPE,  
      "schema": String  
  }  
}

Print:
"function": "sink",
"type": "print",
"print": {}
