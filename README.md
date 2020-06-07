# autoflink

####Sources:
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

####Operations:
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

####Sinks:  
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

#TODO:  

p0  
file sink  
params  
regex filter  
cleanup if blocks/imports  
filter compare var to var  

p1  
keyby+aggregation  
join  
flatmap  
multiple functions per function ie map ops  
arrays in data  

p3  
json validator  