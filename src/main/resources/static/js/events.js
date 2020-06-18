//LOAD NODE DRIVER JSON FROM SERVER
console.log("Loading UI driver json..")
$.get( "http://localhost:8080/load", function( data ) {
    console.log("UI driver json loaded");
    var response = JSON.parse(data)
    logger.value = logger.value + response.logAppend;
    startEditor(JSON.parse(response.jsonOperators));

    if(response.isRunning == true){
        jobState.style.display = "block";
        jsonDriver = response.jsonDriver;
        loadEditorFromJson(jsonDriver);
        logger.value = response.log;
    }else{
        jobState.style.display = "none";
        if(response.jsonEditor !== undefined && response.jsonEditor !== null){
            jsonEditor = JSON.parse(response.jsonEditor);
            undoClear();
            logger.value = response.log;
        }
    }
});

//save state on leave
window.onbeforeunload = function(){
    var jsonEditor = clearEditor();
    var request = {};
    request["jsonEditor"] = JSON.stringify(jsonEditor);
    request["log"] = logger.value;
    $.ajax({
        url: 'http://localhost:8080/saveEditor',
        type: 'PUT',
        data: JSON.stringify(request),
        contentType: "application/json; charset=utf-8",
        dataType : "json",
        async : false
    });
};

//load file into editor
function loadEditor(){
    $('input[id^=file]').click();
}
fileInput.addEventListener('change', function(e) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function(e) {
        jsonDriver = reader.result;
        loadEditorFromJson(jsonDriver);
        //console.log(jsonDriver);
    }
    reader.readAsText(file);
});

//clear editor, saving editor
function clearEditor(){
    console.log("Clearing editor...");
    var data = editor.toJSON();
    var nodes = editor.nodes;
    var i = nodes.length;
    while(i > 0){
        i--;
        editor.removeNode(nodes[i]);
    }
    jsonEditor = data;
    return jsonEditor;
}

//undo clear editor
function undoClear(){
    console.log("Loading from last clear...");
    editor.fromJSON(jsonEditor);
}

//redraws whats on editor
function redrawEditor(){
    console.log("Redrawing editor...");
    jsonDriver = editorToStreamJson(false);
    loadEditorFromJson(jsonDriver);
}

//load editor driver function
function loadEditorFromJson(jsonDriver){
    console.log("Loading editor...");

        if(!jsonDriver){
            console.log("Nothing loaded...");
            return;
        }
        streamJson = JSON.parse(jsonDriver);
        var reteJson = {};

        var nameToId = {};
        var newIndex = dockCount+1;
        for(var nodeId in streamJson){
            var streamNode = streamJson[nodeId];
            nameToId[nodeId] = newIndex;
            newIndex++;
        }

        var streamJsonOrdered = {};
        for(var nodeName in streamJson){
            var outputs = streamJson[nodeName].outputs;
            console.log();
            //nodeId = nameToId[nodeName];
            if(streamJson[nodeName].function === "source"){
                streamJsonOrdered[nodeName] = streamJson[nodeName];
                buildOrderedMap(outputs);
            }
        }
        function buildOrderedMap(outputs){
            for(var outputIndex in outputs){
                var nodeName = outputs[outputIndex];
                streamJsonOrdered[nodeName] = streamJson[nodeName];
                var nextOutputs = streamJson[nodeName].outputs;
                if(nextOutputs !== undefined){
                    buildOrderedMap(nextOutputs);
                }
            }
        }

        var idToOutputs = {};
        var idToInputs = {};
        for(var nodeName in streamJsonOrdered){
            var streamNode = streamJsonOrdered[nodeName];
            var outputs = streamNode.outputs;
            var id = nameToId[nodeName];

            idToOutputs[id] = [];
            for(var i in outputs){
                outputId = nameToId[outputs[i]];
                idToOutputs[id].push(outputId);
                if(idToInputs[outputId] === undefined){
                    idToInputs[outputId] = [];
                }
                idToInputs[outputId].push(id);
            }
        }

        var reteNodes = {};
        for(var nodeName in streamJsonOrdered){
            var streamNode = streamJsonOrdered[nodeName];
            var nodeId = nameToId[nodeName];
            var reteNode = {};

            reteNode["name"] = streamNode["function"]+":"+streamNode["type"];
            reteNode["id"] = nameToId[nodeName];

            //outputs
            if(idToOutputs[nodeId].length > 0){
                var outputStr = "";
                for(var i in idToOutputs[nodeId]){
                    var outputInputs = idToInputs[idToOutputs[nodeId][i]];
                    var inId = "in" + outputInputs.findIndex((id) => id === nodeId);

                    outputStr = outputStr + '{"node":' + idToOutputs[nodeId][i] + ',"input":"'+inId+'","data":{}},';
                }
                outputStr = outputStr.slice(0, -1);
                reteNode["outputs"] = JSON.parse('{"out":{"connections":[' + outputStr + ']}}');
            }else{
                reteNode["outputs"] = {};
            }

            //inputs
            if(idToInputs[nodeId] !== undefined && idToInputs[nodeId].length > 0){
                if(idToInputs[nodeId] !== undefined && idToInputs[nodeId].length > 0){
                    var inputStr = '{';
                    for(index in idToInputs[nodeId]){
                        var inputId = idToInputs[nodeId][index]
                        var inId = "in"+index;
                        var inputStr = inputStr+'"'+inId+'":{"connections":[{"node":'+inputId+',"output":"out","data":{}}]},';
                    }
                    inputStr = inputStr.slice(0, -1);
                    reteNode["inputs"] = JSON.parse(inputStr+'}');
                }else{
                    reteNode["inputs"] = {};
                }
                console.log();
            }

            //data
            var streamData = streamNode[streamNode["type"]];

            var reteData = {};
            reteData["name"] = nodeName;
            for(var dataKey in streamData){
                var dataVal = streamData[dataKey];

                if(dataVal.constructor == Object){
                    var subData = dataVal;
                    var subDataKeys = Object.keys(subData);
                    for(subDataIndex in subDataKeys){
                        var subDataKey = subDataKeys[subDataIndex];
                        reteData[dataKey+"."+subDataKey] = subData[subDataKey];
                    }
                }else{
                    reteData[dataKey] = dataVal;
                }
            }
            reteNode["data"] = reteData;
            reteNode["position"] = [0,0];

            reteNodes[nodeId] = reteNode;
        }

        //position
        var level = 0;
        var y = 0;
        var nodePosition = {};
        for(var nodeName in streamJsonOrdered){
            nodeId = nameToId[nodeName];
            if(idToInputs[nodeId] === undefined){
                reteNodes[nodeId]["position"] = [level*widthbuff, y*heightbuff];
                setPosition(idToOutputs[nodeId], level+1);
            }
        }
        function setPosition(nodeIds, level){
            for(var nodeIdIndex in nodeIds){
                var nodeId = nodeIds[nodeIdIndex];
                if(nodePosition[nodeId] === undefined){
                    reteNodes[nodeId]["position"] = [level*widthbuff, y*heightbuff];
                    nodePosition[nodeId] = reteNodes[nodeId]["position"];
                }else{
                    if(reteNodes[nodeId]["position"][0] < level){
                        reteNodes[nodeId]["position"] = [level*widthbuff, y*heightbuff];
                        nodePosition[nodeId] = reteNodes[nodeId]["position"];
                    }else if(reteNodes[nodeId]["position"][1] < y*heightbuff){
                        reteNodes[nodeId]["position"][1] = y*heightbuff;
                    }
                }

                if(idToOutputs[nodeId].length > 0){
                    setPosition(idToOutputs[nodeId], level+1);
                }else{
                    y = y + 1;
                }
                if(nodeIdIndex != nodeIds.length-1){
                    y = y + 1;
                }
            }
        }

        reteJson["nodes"] = reteNodes;
        reteJson["id"] = "demo@0.1.0";
        reteJson["comments"] = [];

        console.log(JSON.stringify(reteJson));
        editor.fromJSON(reteJson);

        return;
}

//GENERATE jsonDriver FROM EDITOR, if true(from button) try to save
function editorToStreamJson(toFile){
    console.log(toFile);
    json = JSON.stringify(generateStreamJsonFromEditor());
    jsonDriver = json;
    if(toFile){
        //TODO write to file
        alert("Stream driver generated:\n\n" + json);
    }
    //$("#logger").text("json created");
    return json;

    function generateStreamJsonFromEditor(){
        var streamNodes = {};
        var nodes = editor.nodes;
        console.log(nodes);
        console.log(JSON.stringify(nodes));
        var name = "";
        var idToName = {};

        for(nodeIndex in nodes){
            node = nodes[nodeIndex];
            idToName[node.id] = node.data["name"]
        }

        for(nodeIndex in nodes){

            node = nodes[nodeIndex];
            nodeData = node.data;
            nodeName = nodeData.name;
            nodeSplit = node.name.split(":");
            nodeFunction = nodeSplit[0];
            nodeType = nodeSplit[1];

            streamNode = {};
            streamNode["function"] = nodeFunction;
            streamNode["type"] = nodeType;

            outputs = [];
            for (const [index, entry] of node.outputs.entries()) {
                var connections = entry.connections;
                for (connIndex in connections) {
                    connection = connections[connIndex];
                    id = connection.input.node["id"];
                    outputs.push(idToName[id]);
                }
            }
            if(outputs.length > 0){
                streamNode["outputs"] = outputs;
            }

            var embedData = {};
            var subStreamNode = {};
            for (nodeDataKey in nodeData) {
                var nodeDataVal = nodeData[nodeDataKey];
                if(nodeDataKey === "name"){
                    //zzz
                }else if(nodeDataKey.includes(".")){
                    var split = nodeDataKey.split(".");
                    var key = split[1];
                    embedData[split[0]] = Object.assign({}, embedData[split[0]], {[key]: nodeDataVal});
                }else{
                    subStreamNode[nodeDataKey] = nodeDataVal;
                }
            }
            for(embed in embedData){
                subStreamNode[embed] = embedData[embed];
            }
            streamNode[nodeType] = subStreamNode;
            streamNodes[nodeName] = streamNode;
        }
        return streamNodes;
    }
}


//STARTS STREAM ON SERVER
function startStream(){
    console.log("json");
    console.log(jsonDriver);
    $.ajax({
        url: 'http://localhost:8080/start',
        type: 'PUT',
        data: jsonDriver,
        contentType: "application/json; charset=utf-8",
        dataType   : "json",
        success: function(result) {
            console.log("started with json driver");
            $("#logger").text(result);
        }
    });
}

//STOPS STREAM ON SERVER
function stopStream(){
    $.get( "http://localhost:8080/stop", function( result ) {
        console.log("stopped");
        $("#logger").text(result);
    });
}

//opens/closes log tab
function openLog() {
    document.getElementById("logForm").style.display = "block";
    }

    function closeLog() {
    document.getElementById("logForm").style.display = "none";
}

//gets status from server
function getStatus() {
  $.ajax({
    url: 'http://localhost:8080/status',
    success: function(data) {
        var response = JSON.parse(data);
        if(response.isRunning == true){
            jobState.style.display = "block";
        }else{
            jobState.style.display = "none";
        }
        logger.value = logger.value + response.logAppend;
    },
    complete: function() {
      setTimeout(getStatus, 5000);
    }
  });
}
getStatus();