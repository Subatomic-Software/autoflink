//starts editor
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
    editor.use(ReadonlyPlugin.default, { enabled: false });
    editor.use(DockPlugin.default, {
      container: document.querySelector('.dock'),
      itemClass: 'dock-item', // default: dock-item
      plugins: [VueRenderPlugin.default] // render plugins
    });
    editor.use(MinimapPlugin.default);


    components.map(c => {
        editor.register(c);
    });

    editor.on('zoom', ({ source }) => {
        return source !== 'dblclick';
    });

    editor.view.resize();
    //AreaPlugin.zoomAt(editor);
    editor.trigger('process');
    console.log("Editor started");
}

//inputs in nodes
class MessageControl extends Rete.Control {
    constructor(emitter, val, {name=null, allowed=null, data=null}) {
        super(val);
        this.template = getTemplate(name, val, allowed, data);
        this.scope = {
            change: this.change.bind(this)
        };
        if(name != null){
            this.type = name;
        }
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
        }else if(this.key === "name"){
            this.value = generateName(this.type);
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
function getTemplate(name, val, allowed, data){
    if(allowed == null || !(val in allowed)){
        if(name != null){
            return '<input :value="val" @input="change($event)" disabled="disabled"/>';
        }else{
            if(val === "value" || val === "target"){
                return '<input id="variable" :value="val" @input="change($event)" placeholder="'+val+'"/>';
            }
            return '<input :value="val" @input="change($event)" placeholder="'+val+'"/>';
        }
    }else{
        var select = $('<select required @input="change($event)" ></select>');
        select.append($('<option disabled selected hidden></option>').text(val))
        select.attr('onfocus', 'lockEditor(true)');
        select.attr('onfocusout', 'lockEditor(false)');
        for(index in allowed[val]){
            var option = $('<option></option>').attr('value', allowed[val][index]).text(allowed[val][index]);
            if(data[val] !== undefined && data[val] === allowed[val][index]){
                option.attr('selected', 'selected')
            }
            select.append(option);
        }
        return select.prop('outerHTML');
    }
}
function generateName(type){
    var index = 0
    var nodes = editor.nodes;
    while(true){
        if(!editor.nodes.some(e => e.data.name === type+index)){
            return type+index;
        }
        index++;
    }
}
function lockEditor(lock){
    editor.trigger('readonly', lock);
}

//node controller
function getNodeControllers(subtype, node, name){
    var ctrl = new MessageControl(this.editor, 'name', {name: name});
    node = node.addControl(ctrl);
    var allowed = subtype.allowed;
    for(val in subtype){
        if(val != "name" && val != "req" && val != "allowed"){
            var data = node.data;
            if(!(subtype[val] instanceof Object)){
                var ctrl = new MessageControl(this.editor, val, {allowed: allowed, data: data});
                node = node.addControl(ctrl)
            }else{
                for(subval in subtype[val]){
                    if(subval != "name" && subval != "req" && subval != "allowed"){
                        var ctrl = new MessageControl(this.editor, val+"."+subval, {allowed: allowed, data: data});
                        node = node.addControl(ctrl)
                    }
                }
            }
        }
    }
    return node
}

//source node
function getSource(type, name, subtype) {
    return class extends Rete.Component {
      constructor(){
        super(type+":"+name);
        this._type = type;
        this._name = name;
      }
      builder(node) {
        var out1 = new Rete.Output('out', "", autoSocket);
        return getNodeControllers(subtype, node, name+type).addOutput(out1);
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

//sink node
function getSink(type, name, subtype) {
    return class extends Rete.Component {
      constructor(){
        super(type+":"+name);
        this._type = type;
        this._name = name;
      }
      builder(node) {
        var in1 = new Rete.Input('in0', "", autoSocket);
        return getNodeControllers(subtype, node, name+type).addInput(in1);
      }
      worker(node, inputs, outputs) {
        outputs['num'] = node.data.num;
      }
      get name() {
          return this._name;
      }
    }
}

//operator node
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
        return getNodeControllers(subtype, node, name+type).addInput(in1).addOutput(out1);
      }
      worker(node, inputs, outputs) {
        outputs['num'] = node.data.num;
      }
      get name() {
          return this._name;
      }
    }
}

//join node
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
        return getNodeControllers(subtype, node, name+type).addInput(in1).addInput(in2).addOutput(out1);
      }
      worker(node, inputs, outputs) {
        outputs['num'] = node.data.num;
      }
      get name() {
          return this._name;
      }
    }
}