console.log("Begin main.js..")
var editor;
var jsonDriver = "";
var autoSocket = new Rete.Socket('autoflink');
var componentsMap = {};

var test;

console.log("Loading UI driver json..")
$.get( "http://localhost:8080/load", function( result ) {
    console.log("UI driver json loaded");
    jsonObj = JSON.parse(result)
    startEditor(jsonObj);
});

function clearEditor(){
    var data = editor.toJSON();
    console.log(data);
    var nodes = editor.nodes;
    var i = nodes.length;
    while(i > 0){
        i--;
        editor.removeNode(nodes[i]);
    }

    test = data;
}

function loadEditor(){
    //clearEditor();
    (async () => {

/*
        console.log(componentsMap)
        var source = await componentsMap["sourcefile"].createNode({name: "test"});
        editor.addNode(source);
        var sink = await componentsMap["sinkprint"].createNode();
        editor.addNode(sink);
        editor.connect(source.outputs.get('out'), sink.inputs.get('in'));
*/

        editor.fromJSON(test);


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
            console.log("silent?");
            await engine.abort();
            //await engine.process(editor.toJSON());
        });

        editor.view.resize();
        AreaPlugin.zoomAt(editor);
        editor.trigger('process');
        console.log("Editor started");
    })();
}
