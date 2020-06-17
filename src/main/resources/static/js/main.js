console.log("Begin main.js..")

var editor;
var applicationJson = "";
var autoSocket = new Rete.Socket('autoflink');
var componentsMap = {};
var dockCount = 0;
var lastCleared = {};

var heightbuff = 300;
var widthbuff = 500;

var fileInput = document.getElementById('fileInput');
fileInput.addEventListener('change', function(e) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function(e) {
        applicationJson = reader.result;
        loadEditorFromJson();
        //console.log(applicationJson);
    }
    reader.readAsText(file);
});

function loadEditor(){
    $('input[id^=file]').click();
}

//LOAD NODE DRIVER JSON FROM SERVER
console.log("Loading UI driver json..")
$.get( "http://localhost:8080/load", function( result ) {
    console.log("UI driver json loaded");
    jsonObj = JSON.parse(result)
    startEditor(jsonObj);
});

//CLEAR EDITOR BUTTON
function clearEditor(){
    console.log("Clearing editor...");
    var data = editor.toJSON();
    var nodes = editor.nodes;
    var i = nodes.length;
    while(i > 0){
        i--;
        editor.removeNode(nodes[i]);
    }
    lastCleared = data;
}

//undo clear button
function undoClear(){
    console.log("Loading from last clear...");
    editor.fromJSON(lastCleared);
}

//redraws whats on editor
function redrawEditor(){
    console.log("Redrawing editor...");
    applicationJson = editorToStreamJson(false);
    loadEditorFromJson();
}

//LOAD EDITOR BUTTON
function loadEditorFromJson(){
    console.log("Loading editor...");
    //clearEditor();

        if(!applicationJson){
            console.log("Nothing loaded...");
            return;
        }
        streamJson = JSON.parse(applicationJson);
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

//GENERATE JSON FROM EDITOR
function editorToStreamJson(toFile){
    console.log(toFile);
    json = JSON.stringify(generateStreamJsonFromEditor());
    applicationJson = json;
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
    if(applicationJson == ""){
        $.ajax({
            url: 'http://localhost:8080/startWithoutJson',
            type: 'PUT',
            success: function(result) {
                console.log("started without json driver");
                $("#logger").text(result);
            }
        });
    }else{
        $.ajax({
            url: 'http://localhost:8080/startWithJson',
            type: 'PUT',
            data: applicationJson,
            contentType: "application/json; charset=utf-8",
            dataType   : "json",
            success: function(result) {
                console.log("started with json driver");
                $("#logger").text(result);
            }
        });
    }
}

//STOPS STREAM ON SERVER
function stopStream(){
    $.get( "http://localhost:8080/stop", function( result ) {
        console.log("stopped");
        $("#logger").text(result);
    });
}

//
//RETE STUFF
//
class MessageControl extends Rete.Control {
    constructor(emitter, val) {
        super(val);
        //console.log(emitter)
        this.template = '<input :value="val" @input="change($event)" placeholder="'+val+'"/>';
        this.scope = {
            change: this.change.bind(this)
        };
    }
    change(e) {
        this.value = e.target.value;
        this.update();
    }
    update() {
        this.putData(this.key, this.value)
        this.scope.val = this.value;
        this._alight.scan();
    }
    mounted() {
        if(this.getData(this.key) !== undefined){
            this.value = this.getData(this.key);
        }else{
            this.value = "";
        }
        this.update();
    }
    setValue(value) {
        this.value = value;
        this._alight.scan()
    }
}

///
//MORE RETE STUFF
///
function getNodeControllers(subtype, node){
    var ctrl = new MessageControl(this.editor, 'name');
    node = node.addControl(ctrl);
    for(val in subtype){
        if(val != "name" && val != "req" && val != "allowed"){
            if(!(subtype[val] instanceof Object)){
                var ctrl = new MessageControl(this.editor, val);
                node = node.addControl(ctrl)
            }else{
                for(subval in subtype[val]){
                    if(subval != "name" && subval != "req" && subval != "allowed"){
                        var ctrl = new MessageControl(this.editor, val+"."+subval);
                        node = node.addControl(ctrl)
                    }
                }
            }
        }
    }
    return node
}

function getSource(type, name, subtype) {
    return class extends Rete.Component {
      constructor(){
        super(type+":"+name);
        this._type = type;
        this._name = name;
      }
      builder(node) {
        var out1 = new Rete.Output('out', "", autoSocket);
        return getNodeControllers(subtype, node).addOutput(out1);
      }
      worker(node, inputs, outputs) {
        outputs['name'] = node.data._name;
      }
      get name() {
          return this._name;
      }
      set type(type) {
          this._type = type;
      }
    }
}

function getSink(type, name, subtype) {

    return class extends Rete.Component {
      constructor(){
        super(type+":"+name);
        this._type = type;
        this._name = name;
      }
      builder(node) {
        var in1 = new Rete.Input('in0', "", autoSocket);
        return getNodeControllers(subtype, node).addInput(in1);
      }
      worker(node, inputs, outputs) {
        outputs['num'] = node.data.num;
      }
      get name() {
          return this._name;
      }
    }
}

function getOperator(type, name, subtype) {
    return class extends Rete.Component {
      constructor(){
        super(type+":"+name);
        this._type = type;
        this._name = name;
      }
      builder(node) {
        var in1 = new Rete.Input('in0', "", autoSocket);
        var out1 = new Rete.Output('out', "", autoSocket);
        return getNodeControllers(subtype, node).addInput(in1).addOutput(out1);
      }
      worker(node, inputs, outputs) {
        outputs['num'] = node.data.num;
      }
      get name() {
          return this._name;
      }
    }
}

function getJoin(type, name, subtype) {
    return class extends Rete.Component {
      constructor(){
        super(type+":"+name);
        this._type = type;
        this._name = name;
      }
      builder(node) {
        var in1 = new Rete.Input('in0', "", autoSocket);
        var in2 = new Rete.Input('in1', "", autoSocket);
        var out1 = new Rete.Output('out', "", autoSocket);
        return getNodeControllers(subtype, node).addInput(in1).addInput(in2).addOutput(out1);
      }
      worker(node, inputs, outputs) {
        outputs['num'] = node.data.num;
      }
      get name() {
          return this._name;
      }
    }
}

//BOOTS THE EDITOR AFTER BUILDING RETE COMPONENTS
function startEditor(jsonObj){
    var componentClasses = {};
    console.log("Building component map..");
    for (type in jsonObj) {
      //console.log(jsonObj[type])
      var jsonType = jsonObj[type]
      for (subtype in jsonType){
        //console.log("key:"+type+jsonType[subtype]["name"])
        if(type == "source"){
          componentClasses[type+jsonType[subtype]["name"]] = getSource(type, jsonType[subtype]["name"], jsonType[subtype])
          //console.log("source");
        }else if(type == "sink"){
          componentClasses[type+jsonType[subtype]["name"]] = getSink(type, jsonType[subtype]["name"], jsonType[subtype])
          //console.log("sink");
        }else if(type == "operation"){
          componentClasses[type+jsonType[subtype]["name"]] = getOperator(type, jsonType[subtype]["name"], jsonType[subtype])
          //console.log("op");
        }else{
          componentClasses[type+jsonType[subtype]["name"]] = getJoin(type, jsonType[subtype]["name"], jsonType[subtype])
        }
      }
    }
    console.log("Component map built");

    console.log("Starting editor..");
    (async () => {
        var components = [];
        var container = document.querySelector('#rete');
        Object.keys(componentClasses).forEach(function(key) {
            var tmp = new componentClasses[key];
            components.push(tmp);
            componentsMap[key] = tmp;
            dockCount++;
        });

        editor = new Rete.NodeEditor('demo@0.1.0', container);
        editor.use(ConnectionPlugin.default);
        //editor.use(VueRenderPlugin.default);
        editor.use(ContextMenuPlugin.default);
        editor.use(AreaPlugin);
        editor.use(CommentPlugin.default);
        editor.use(HistoryPlugin);
        editor.use(ConnectionMasteryPlugin.default);
        editor.use(AlightRenderPlugin);
        editor.use(DockPlugin.default, {
              container: document.querySelector('.dock'),
              itemClass: 'dock-item', // default: dock-item
              plugins: [VueRenderPlugin.default] // render plugins
            });

        var engine = new Rete.Engine('demo@0.1.0');

        components.map(c => {
            editor.register(c);
            engine.register(c);
        });

        editor.on('zoom', ({ source }) => {
            return source !== 'dblclick';
        });

        editor.on('process nodecreated noderemoved connectioncreated connectionremoved', async () => {
            await engine.abort();
            //await engine.process(editor.toJSON());
        });

        editor.view.resize();
        AreaPlugin.zoomAt(editor);
        editor.trigger('process');
        console.log("Editor started");
    })();
}

function openLog() {
    document.getElementById("logForm").style.display = "block";
    }

    function closeLog() {
    document.getElementById("logForm").style.display = "none";
}

var jobState = document.getElementById("jobState");
function worker() {
  $.ajax({
    url: 'http://localhost:8080/status',
    success: function(data) {
        console.log("logger..");
        var response = JSON.parse(data);

        console.log(response);
        console.log(response.isRunning);
        console.log(response.logAppend);

        if(response.isRunning == true){
            jobState.style.display = "block";
        }else{
            jobState.style.display = "none";
        }

        document.getElementById("logTextBox").value = document.getElementById("logTextBox").value + response.logAppend;
    },
    complete: function() {
      setTimeout(worker, 5000);
    }
  });
}
worker();