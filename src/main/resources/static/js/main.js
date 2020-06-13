console.log("Begin main.js..")

var editor;
var jsonDriver = "";
var autoSocket = new Rete.Socket('autoflink');
var componentsMap = {};
var dockCount = 0;
var test = {};
var rawflink = "";

var heightbuff = 150;
var widthbuff = 500;

var fileInput = document.getElementById('fileInput');
fileInput.addEventListener('change', function(e) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function(e) {
        rawflink = reader.result;
        console.log(rawflink);
    }
    reader.readAsText(file);
});


console.log("Loading UI driver json..")
$.get( "http://localhost:8080/load", function( result ) {
    console.log("UI driver json loaded");
    jsonObj = JSON.parse(result)
    startEditor(jsonObj);
});

function clearEditor(){
    console.log("Clearing editor...");
    var data = editor.toJSON();
    var nodes = editor.nodes;
    var i = nodes.length;
    while(i > 0){
        i--;
        editor.removeNode(nodes[i]);
    }

    test = data;
    console.log(test);
}

function loadEditor(){
    console.log("Loading editor...");
    //clearEditor();
    (async () => {

        if(!rawflink){
            console.log("Nothing loaded...");
            return;
        }
        console.log(JSON.parse(rawflink));
        flinkObj = JSON.parse(rawflink);


        id = dockCount+1;
        var level = 0;
        var y = 0;
        var level = 0;
        var reteNodes = {};

        var rootKeys = Object.keys(flinkObj);
        console.log(rootKeys);
        for(keyIndex in rootKeys){
            var tmp = {};
            tmp["position"] = [level*widthbuff, y*heightbuff];
            var localId = id;
            id++;

            var sourceObj = flinkObj[rootKeys[keyIndex]];
            var sourceType = sourceObj["type"];
            var sourceKeys = Object.keys(sourceObj)
            var outputs = [];
            var data = {};
            for(sourceIndex in sourceKeys){
                var sourceKey = sourceKeys[sourceIndex];
                if(sourceKey !== sourceType && sourceKey !== "function" && sourceKey !== "type"){
                    outputs.push(generateReteNode(localId, sourceKey, sourceObj[sourceKey], level+1));
                }else if(sourceKey === sourceType){
                    var dataObj = sourceObj[sourceKey];
                    var dataKeys = Object.keys(dataObj);
                    for(dataIndex in dataKeys){
                        var dataKey = dataKeys[dataIndex];
                        if(dataObj[dataKey].constructor == Object){
                            var subdataObj = sourceObj[sourceKey];
                            var subdataKeys = Object.keys(dataObj[dataKey]);
                            for(subdataIndex in subdataKeys){
                                var subdataKey = subdataKeys[subdataIndex];
                                data[dataKey+"."+subdataKey] = subdataObj[dataKey][subdataKey];
                            }
                        }else{
                            data[dataKey] = dataObj[dataKey];
                        }
                    }
                }
            }
            data["name"] = rootKeys[keyIndex];
            tmp["name"] = sourceObj["function"]+":"+sourceObj["type"];
            tmp["id"] = localId;
            tmp["data"] = data;
            tmp["inputs"] = {};
            if(outputs.length > 0){
                var outputStr = "";
                for(i in outputs){
                    outputStr = outputStr + '{"node":' + outputs[i] + ',"input":"in","data":{}},';
                }
                outputStr = outputStr.slice(0, -1);
                tmp["outputs"] = JSON.parse('{"out":{"connections":[' + outputStr + ']}}');
            }else{
                tmp["outputs"] = {};
            }
            y = y + 1;
            reteNodes[localId] = tmp;
        }

        function generateReteNode(inputId, name, obj, level){
            var tmp = {};
            tmp["position"] = [level*widthbuff, y*heightbuff];
            var localId = id;
            id++;

            var sourceType = obj["type"];
            var sourceKeys = Object.keys(obj)
            var outputs = [];
            var data = {};
            for(sourceIndex in sourceKeys){
                var sourceKey = sourceKeys[sourceIndex];
                if(sourceKey !== sourceType && sourceKey !== "function" && sourceKey !== "type"){
                    outputs.push(generateReteNode(localId, sourceKey, obj[sourceKey], level+1));
                }else if(sourceKey === sourceType){
                    var dataObj = obj[sourceKey];
                    var dataKeys = Object.keys(dataObj);
                    for(dataIndex in dataKeys){
                        var dataKey = dataKeys[dataIndex];
                        if(dataObj[dataKey].constructor == Object){
                            var subdataObj = obj[sourceKey];
                            var subdataKeys = Object.keys(dataObj[dataKey]);
                            for(subdataIndex in subdataKeys){
                                var subdataKey = subdataKeys[subdataIndex];
                                data[dataKey+"."+subdataKey] = subdataObj[dataKey][subdataKey];
                            }
                        }else{
                            data[dataKey] = dataObj[dataKey];
                        }
                    }
                }
            }
            data["name"] = name;
            tmp["name"] = obj["function"]+":"+obj["type"];
            tmp["id"] = localId;
            tmp["data"] = data;
            if(obj["function"] !== "source"){
                tmp["inputs"] = JSON.parse('{"in":{"connections":[{"node":'+inputId+',"output":"out","data":{}}]}}');
            }else{
                tmp["inputs"] = {};
            }
            if(outputs.length > 0){
                var outputStr = "";
                for(i in outputs){
                    outputStr = outputStr + '{"node":' + outputs[i] + ',"input":"in","data":{}},';
                }
                outputStr = outputStr.slice(0, -1);
                tmp["outputs"] = JSON.parse('{"out":{"connections":[' + outputStr + ']}}');
            }else{
                tmp["outputs"] = {};
            }
            reteNodes[localId] = tmp;
            y = y + 1;
            return localId;
        }

/*
'{"id":"demo@0.1.0",
"nodes":{
"8":{
    "id":8,
    "data":{"name":"name1","format.schema":"schema1","format.type":"tupl1","directory":"direct1"},
    "inputs":{},
    "outputs":{"out":{"connections":[{"node":9,"input":"in","data":{}}]}},
    "position":[-347,-97],
    "name":"source:file"
    },
"9":{"id":9,"data":{"name":"naem2","format.schema":"shcmea2","format.type":"typoku2","directory":"cijrect2"},"inputs":{"in":{"connections":[{"node":8,"output":"out","data":{}}]}},"outputs":{},"position":[-28,-121],"name":"sink:file"}},"comments":[]}';
*/

        var reteObj = {};
        reteObj["nodes"] = reteNodes;
        reteObj["id"] = "demo@0.1.0";
        reteObj["comments"] = [];
        console.log(JSON.stringify(reteObj));
        editor.fromJSON(reteObj);
    })();
}

function generateJson(){
    json = generateJsonFromEditor()
    alert("Stream driver generated:\n\n" + json);
    jsonDriver = json;
    $("#logger").text("json created");
}

function startStream(){
    if(jsonDriver == ""){
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
            data: jsonDriver,
            contentType: "application/json; charset=utf-8",
            dataType   : "json",
            success: function(result) {
                console.log("started with json driver");
                $("#logger").text(result);
            }
        });
    }
}

function stopStream(){
    $.get( "http://localhost:8080/stop", function( result ) {
        console.log("stopped");
        $("#logger").text(result);
    });
}


//TODO get value to change inside object on update
class MessageControl extends Rete.Control {
    constructor(emitter, val) {
        super(val);
        //console.log(emitter)
        this.template = '<input :value="val" @input="change($event)" placeholder="'+val+'"/>';
        this.id = val;
        this.scope = {
            val: "",
            change: this.change.bind(this)
        };
    }
    change(e) {
        this.msg = e.target.value;
        this.update();
    }
    update() {
        this.putData(this.id, this.msg)
        this.scope.val = this.msg;
        this._alight.scan();
    }
    mounted() {
        if(this.getData(this.id) !== undefined){
            this.msg = this.getData(this.id);
        }else{
            this.msg = "";
        }
        this.update();
    }
    setValue(msg) {
        this.msg = msg;
        this._alight.scan()
    }
}

function generateJsonFromEditor(){
    //console.log(editor.nodes);
    var nodes = editor.nodes;
    var nodesToJson = {};
    var nodesToId = {}
    var name = "";
    for(node in nodes){
        nodesToId[nodes[node].id] = nodes[node];
        var nodeObj = nodes[node];
        //console.log(nodeObj);
        //console.log(nodeObj._alight.children)
        var children = nodeObj._alight.children;
        var nodeMap = {};
        var embeddedJson = {};
        for(child in children){
            var control = children[child].locals.control;
            //console.log("control");
            //console.log(control);
            if(control != null){
                //console.log(control.key+":"+control.msg);
                if(control.key === "name"){
                    name = control.msg;
                }else if(control.msg === "" || control.key === "name"){
                    //sockets, not inputs
                }else if(control.key.includes(".")){
                    var split = control.key.split(".");
                    var key = split[1];
                    embeddedJson[split[0]] = Object.assign({}, embeddedJson[split[0]], {[key]: control.msg});
                }else{
                    nodeMap[control.key] = control.msg
                }
            }
        }

        //console.log("name:"+name)
        //console.log(nodeObj);
        var split = nodeObj.name.split(":");
        for(embed in embeddedJson){
            nodeMap[embed] = embeddedJson[embed];
        }
        for(node in nodeMap){
            //console.log(node + " " + nodeMap[node]);
            nodeMap[split[1]] = Object.assign({}, nodeMap[split[1]], {[node]: nodeMap[node]});
            delete nodeMap[node];
        }
        nodeMap["function"] = split[0];
        nodeMap["type"] = split[1];
        nodeMap = {[name]: nodeMap}
        nodesToJson[nodeObj.id] = nodeMap
        //console.log(JSON.stringify(nodeMap));
    }
    //console.log(JSON.stringify(nodesToJson));

    var jsonMap = {};
    for(nodeId in nodesToJson){
        var node = nodesToJson[nodeId];
        var keys = Object.keys(node);
        //console.log(node[keys[0]]["function"]);
        if(node[keys[0]]["function"] === "source"){
            //console.log(node[keys[0]]["function"]);
            var json = generateSubJson(nodeId, nodesToId, nodesToJson);
            var keys = Object.keys(json);
            jsonMap[keys[0]] = json[keys[0]];
        }
    }

    var finalJson = JSON.stringify(jsonMap);
    //console.log(finalJson);
    return finalJson;
}

function generateSubJson(id, nodesToId, nodesToJson){
    var idString = JSON.stringify(nodesToId[id]);
    if(idString === undefined){
        idString = "";
    }
    var idStrings = idString.match(/"node":[0-9]{1,2},"input"/g);
    var ids = [];
    for(index in idStrings){
        ids.push(idStrings[index].replace(/[^0-9]+/g,''));
    }
    if(ids.length == 0){
        return nodesToJson[id];
    }
    var json = nodesToJson[id];
    var keys = Object.keys(json);
    for(index in ids){
        var subJson = generateSubJson(ids[index], nodesToId, nodesToJson)
        var subkeys = Object.keys(subJson);
        json[keys[0]][subkeys[0]] = subJson[subkeys[0]];
        //console.log(subJson);
    }
    return json;
}

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
        var in1 = new Rete.Input('in', "", autoSocket);
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
        var in1 = new Rete.Input('in', "", autoSocket);
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
        }else{
          componentClasses[type+jsonType[subtype]["name"]] = getOperator(type, jsonType[subtype]["name"], jsonType[subtype])
          //console.log("op");
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
        //editor.use(ConnectionMasteryPlugin.default);
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
