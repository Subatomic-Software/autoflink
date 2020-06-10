
var jsonComponents = '{"sink":{"print":{"name":"print"},"file":{"name":"file","format":{"schema":"schema","allowed":{"type":["json","avro","csv"]},"name":"format","type":"type","req":["type"]},"directory":"directory","req":["directory","format"]},"kafka":{"name":"kafka","format":{"schema":"schema","allowed":{"type":["json","avro","csv"]},"name":"format","type":"type","req":["type"]},"topic":"topic","broker":"broker","req":["broker","topic","format"]}},"source":{"file":{"name":"file","format":{"schema":"schema","allowed":{"type":["json","avro","csv"]},"name":"format","type":"type","req":["type"]},"directory":"directory","req":["directory","format"]},"kafka":{"groupId":"groupId","name":"kafka","format":{"schema":"schema","allowed":{"type":["json","avro","csv"]},"name":"format","type":"type","req":["type"]},"topic":"topic","broker":"broker","req":["broker","topic","groupId","format"]}},"operation":{"filter":{"allowed":{"function":["==","!=","<",">"]},"function":"function","name":"filter","value":"value","target":"target","req":["target","function","value"]},"map":{"eval":"eval","allowed":{"operation":["calc","remove","replace"]},"name":"map","operation":"operation","target":"target","req":["operation","target"]}}}'
var jsonObj = JSON.parse(jsonComponents)
console.log(jsonObj)

var editor;

function generateJson(){
    //console.log(editor.nodes);
    nodes = editor.nodes;
    nodesToJson = {};
    nodesToId = {}
    name = "";
    for(node in nodes){
        nodesToId[nodes[node].id] = nodes[node];
        nodeObj = nodes[node];
        //console.log(nodeObj);
        //console.log(nodeObj._alight.children)
        children = nodeObj._alight.children;
        nodeMap = {};
        embeddedJson = {};
        for(child in children){
            control = children[child].locals.control
            if(control != null){
                //console.log(control.key+":"+control.msg);
                if(control.key === "name"){
                    name = control.msg;
                }else if(control.msg === "" || control.key === "name"){
                    //console.log("EMPTY");
                }else if(control.key.includes(".")){
                    split = control.key.split(".");
                    key = split[1];
                    embeddedJson[split[0]] = Object.assign({}, embeddedJson[split[0]], {[key]: control.msg});
                }else{
                    nodeMap[control.key] = control.msg
                }
            }
        }
        console.log(nodeObj);
        split = nodeObj.name.split(":");
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
        console.log("name:"+name)
        nodeMap = {[name]: nodeMap}
        nodesToJson[nodeObj.id] = nodeMap
        //console.log(JSON.stringify(nodeMap));

    }

    console.log(JSON.stringify(nodesToJson));


    jsonMap = {};
    for(nodeId in nodesToJson){
        node = nodesToJson[nodeId];
        keys = Object.keys(node);
        //console.log(node[keys[0]]["function"]);
        if(node[keys[0]]["function"] === "source"){
            //console.log(node[keys[0]]["function"]);

            subNodesToJson = nodesToJson[nodeId];
            subNodesToId = nodesToId[nodeId];


            json = generateSubJson(subNodesToId, subNodesToJson, nodeId, nodesToId, nodesToJson);

            die

        }
    }

    die;
}

function generateSubJson(subNodesToId, subNodesToJson, id, nodesToId, nodesToJson){

    var idString = JSON.stringify(nodesToId[id]);
    if(idString === undefined){
        idString = "";
    }
    var idStrings = idString.match(/"node":[0-9]{1,2},"input"/g);
    var ids = [];
    for(id in idStrings){
        ids.push(idStrings[id].replace(/[^0-9]+/g,''));
    }

    if(ids.length == 0){
        return nodesToJson[id];
    }

    var json = {}
    for(id in ids){
        var subJson = generateSubJson(subNodesToId, subNodesToJson, ids[id], nodesToId, nodesToJson)
        var json = Object.assign({}, json, subJson);
        console.log(subJson);
    }
    return json;

}

//TODO get value to change inside object on update
class MessageControl extends Rete.Control {
    constructor(emitter, msg) {
        super(msg);
        console.log(emitter)
        this.template = '<input @input="change($event)" placeholder="'+msg+'"/>';
        this.id = msg;
        this.scope = {
            change: this.change.bind(this)
        };
    }

    change(e) {
        this.msg = e.target.value;
        this.update();
    }

    update() {
        this.putData('msg', this.msg)
        this._alight.scan();
    }

    mounted() {
        //this.scope.value = this.getData('msg');
        this.msg = "";
        //this.update();
    }

    setValue(msg) {
        this.msg = msg;
        this._alight.scan()
    }
}

var componentMap = {};
for (type in jsonObj) {
  //console.log(jsonObj[type])
  var jsonType = jsonObj[type]
  for (subtype in jsonType){
    //console.log("key:"+type+jsonType[subtype]["name"])
    if(type == "source"){
      componentMap[type+jsonType[subtype]["name"]] = getSource(type, jsonType[subtype]["name"], jsonType[subtype])
      //console.log("source");
    }else if(type == "sink"){
      componentMap[type+jsonType[subtype]["name"]] = getSink(type, jsonType[subtype]["name"], jsonType[subtype])
      //console.log("sink");
    }else{
      componentMap[type+jsonType[subtype]["name"]] = getOperator(type, jsonType[subtype]["name"], jsonType[subtype])
      //console.log("op");
    }
  }
}

var autoSocket = new Rete.Socket('autoflink');

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
        outputs['num'] = node.data.num;
      }
      get name() {
          return this._name;
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




(async () => {
    var container = document.querySelector('#rete');

    var componentsDynamic = [];
    Object.keys(componentMap).forEach(function(key) {
        var tmp = new componentMap[key];
        componentsDynamic.push(tmp);
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

    componentsDynamic.map(c => {
        editor.register(c);
        engine.register(c);
    });

    editor.on('zoom', ({ source }) => {
        return source !== 'dblclick';
    });

    editor.on('process nodecreated noderemoved connectioncreated connectionremoved', async () => {
        await engine.abort();
        await engine.process(editor.toJSON());
    });

    editor.view.resize();
    AreaPlugin.zoomAt(editor);
    editor.trigger('process');
})();

