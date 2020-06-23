function loadSchemaFile(){
    $('input[id^=schemaInput]').click();
}
schemaInput.addEventListener('change', function(e) {
    var file = schemaInput.files[0];
    var reader = new FileReader();
    reader.onload = function(e) {
        schema = reader.result;
        addSchema(file.name.split(".").pop(), schema);
    }
    reader.readAsText(file);
});

function addSchema(filename, schema){
    var fileparts = filename.split('.');
    var type = fileparts.pop();
    var name;
    var schemaJson;
    if(type === "avsc"){
        schemaJson = JSON.parse(schema);
        name = schemaJson.name;
        schemas[name] = schemaJson;
        schemasToSend[name] = schemaJson;
        for(var fieldIndex in schemaJson.fields){
            var field = schemaJson.fields[fieldIndex];
            buildAvroSchemaValues("", field);
        }
        //schemaToValues[name] =
    }else if(type === "json"){
        schemaJson = JSON.parse(schema);
        name = fileparts.pop();
        schemas[name] = schemaJson;
        for(var field in schemaJson){
            var val = schemaJson[field];
            buildJsonSchemaValues("", field, val);
        }
    }

    var inputs = $("input#variable");
    for(var inputindex in inputs){
        if(inputs[inputindex].parentElement !== undefined){
            inputs[inputindex].parentElement.classList.add("ui-front");
        }
    }
    inputs.autocomplete({
      source: variables
    });

    function buildAvroSchemaValues(prefix, field){
        if(typeof field.type === 'object'){
            for(var subFieldIndex in field.type.fields){
                buildAvroSchemaValues(prefix+field.type.name+embedSeperator, field.type.fields[subFieldIndex]);
            }
        }else{
            variables.push(prefix+field.name);
        }
    }
    function buildJsonSchemaValues(prefix, key, val){
        if(typeof val === 'object'){
            for(var subKey in val){
                var subval = val[subKey];
                buildJsonSchemaValues(prefix+key+embedSeperator, subKey, subval);
            }
        }else{
            variables.push(prefix + key);
        }
    }
}