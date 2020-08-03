var preloadedSchema;

function loadSchemaFile(){
    $('input[id^=schemaInput]').click();
}
schemaInput.addEventListener('change', function(e) {
    var file = schemaInput.files[0];
    var reader = new FileReader();
    reader.onload = function(e) {
        schema = reader.result;
        //addSchema(file.name.split(".").pop(), schema);
        preloadSchema(file.name.split(".").pop(), schema)
    }
    reader.readAsText(file);
});


function preloadSchema(filename, schema){
    if(schema !== undefined){
        document.getElementById("schemaTextBox").value = schema;
    }
    preloadedSchema = document.getElementById("schemaTextBox").value;
}

function addSchemaClick(){
    var schema = document.getElementById("schemaTextBox").value;
    if(schema !== ""){
        console.log("add");

        try {
            var schemaJson = JSON.parse(schema);
            if(schemaJson.type !== undefined){
                addSchema("avro", schema);
                return;
            }else{
                addSchema("json", schema);
                return;
            }
        } catch (e) {
            console.log("csv?");
        }
    }
}

function addSchema(type, schema){
    var name;
    var schemaJson;
    var tmpVariables = [];
    if(type === "avro"){
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
        name = "JsonSchema";
        schemas[name] = schemaJson;
        for(var field in schemaJson){
            var val = schemaJson[field];
            buildJsonSchemaValues("", field, val);
        }
    }

    schemaToValues[name] = tmpVariables;
    variables.push(...tmpVariables);

    generateAutoComplete();
    populateSchemaSelect();

    function buildAvroSchemaValues(prefix, field){
        if(typeof field.type === 'object'){
            for(var subFieldIndex in field.type.fields){
                buildAvroSchemaValues(prefix+field.type.name+embedSeperator, field.type.fields[subFieldIndex]);
            }
        }else{
            tmpVariables.push(prefix+field.name);
        }
    }
    function buildJsonSchemaValues(prefix, key, val){
        if(typeof val === 'object'){
            for(var subKey in val){
                var subval = val[subKey];
                buildJsonSchemaValues(prefix+key+embedSeperator, subKey, subval);
            }
        }else{
            tmpVariables.push(prefix + key);
        }
    }
}

function populateSchemaSelect(){
    $('#schemaSelect').empty();
    for(var key in schemas){
        $('#schemaSelect').append($('<option></option>').val(key).html(key));
    }
}

function deleteSchemaSelect(){
    var deleteSchema = $('#schemaSelect option:selected').text();
    if(deleteSchema !== ""){

        var deleteVariables = schemaToValues[deleteSchema];
        for(var index in deleteVariables){
            var idx = variables.findIndex( p => p == deleteVariables[index] );
            variables.splice(idx, 1);
        }

        delete schemas[deleteSchema];
        delete schemasToSend[deleteSchema];
        delete schemaToValues[deleteSchema];

        generateAutoComplete();
        populateSchemaSelect();
    }
}