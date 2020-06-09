
var jsonComponents = '{"sink":{"print":{"name":"print"},"file":{"name":"file","format":{"schema":"schema","allowed":{"type":["json","avro","csv"]},"name":"format","type":"type","req":["type"]},"directory":"directory","req":["directory","format"]},"kafka":{"name":"kafka","format":{"schema":"schema","allowed":{"type":["json","avro","csv"]},"name":"format","type":"type","req":["type"]},"topic":"topic","broker":"broker","req":["broker","topic","format"]}},"source":{"file":{"name":"file","format":{"schema":"schema","allowed":{"type":["json","avro","csv"]},"name":"format","type":"type","req":["type"]},"directory":"directory","req":["directory","format"]},"kafka":{"groupId":"groupId","name":"kafka","format":{"schema":"schema","allowed":{"type":["json","avro","csv"]},"name":"format","type":"type","req":["type"]},"topic":"topic","broker":"broker","req":["broker","topic","groupId","format"]}},"operation":{"filter":{"allowed":{"function":["==","!=","<",">"]},"function":"function","name":"filter","value":"value","target":"target","req":["target","function","value"]},"map":{"eval":"eval","allowed":{"operation":["calc","remove","replace"]},"name":"map","operation":"operation","target":"target","req":["operation","target"]}}}'
var jsonObj = JSON.parse(jsonComponents)
console.log(jsonObj)

//TODO get value to change inside object on update
class MessageControl extends Rete.Control {
    constructor(emitter, msg) {
        super(msg);
        this.emitter = emitter;
        this.template = '<input :value="msg" @input="change($event)"/>';

        this.scope = {
            msg,
            change: this.change.bind(this)
        };
    }

    change(e) {
        this.scope.value = +e.target.value;
        this.update();
    }

    update() {
        this.putData('msg', this.scope.value)
        this.emitter.trigger('process');
        this._alight.scan();
    }

    mounted() {
        this.scope.value = this.getData('msg') || 0;
        this.update();
    }

    setValue(val) {
        this.scope.value = val;
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
      componentMap[type+jsonType[subtype]["name"]] = getSource(type+jsonType[subtype]["name"], jsonType[subtype])
      //console.log("source");
    }else if(type == "sink"){
      componentMap[type+jsonType[subtype]["name"]] = getSink(type+jsonType[subtype]["name"], jsonType[subtype])
      //console.log("sink");
    }else{
      componentMap[type+jsonType[subtype]["name"]] = getOperator(type+jsonType[subtype]["name"], jsonType[subtype])
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
                console.log("val:"+val)
            }else{
                console.log("sub:"+val)
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

function getSource(name, subtype) {
    return class extends Rete.Component {
      constructor(){
        super(name);
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

function getSink(name, subtype) {

    return class extends Rete.Component {
      constructor(){
        super(name);
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

function getOperator(name, subtype) {
    return class extends Rete.Component {
      constructor(){
        super(name);
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
        //console.log(key)
        var tmp = new componentMap[key];
        //console.log(tmp)
        componentsDynamic.push(tmp);
    });

    var editor = new Rete.NodeEditor('demo@0.1.0', container);
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

