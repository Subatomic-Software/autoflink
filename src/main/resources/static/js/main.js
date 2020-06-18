console.log("Begin main.js..");

var jobState = document.getElementById("jobState");
var fileInput = document.getElementById('fileInput');
var logger = document.getElementById("logTextBox");

var editor;
var jsonDriver = "";
var jsonEditor;
var jsonLoader = '{"jsonEditor":null,"jsonOperators":"{\"sink\":{\"print\":{\"name\":\"print\"},\"file\":{\"name\":\"file\",\"format\":{\"schema\":\"schema\",\"allowed\":{\"type\":[\"json\",\"avro\",\"csv\"]},\"name\":\"format\",\"type\":\"type\",\"req\":[\"type\"]},\"directory\":\"directory\",\"req\":[\"directory\",\"format\"]},\"kafka\":{\"name\":\"kafka\",\"format\":{\"schema\":\"schema\",\"allowed\":{\"type\":[\"json\",\"avro\",\"csv\"]},\"name\":\"format\",\"type\":\"type\",\"req\":[\"type\"]},\"topic\":\"topic\",\"broker\":\"broker\",\"req\":[\"broker\",\"topic\",\"format\"]}},\"source\":{\"file\":{\"name\":\"file\",\"format\":{\"schema\":\"schema\",\"allowed\":{\"type\":[\"json\",\"avro\",\"csv\"]},\"name\":\"format\",\"type\":\"type\",\"req\":[\"type\"]},\"directory\":\"directory\",\"req\":[\"directory\",\"format\"]},\"kafka\":{\"groupId\":\"groupId\",\"name\":\"kafka\",\"format\":{\"schema\":\"schema\",\"allowed\":{\"type\":[\"json\",\"avro\",\"csv\"]},\"name\":\"format\",\"type\":\"type\",\"req\":[\"type\"]},\"topic\":\"topic\",\"broker\":\"broker\",\"req\":[\"broker\",\"topic\",\"groupId\",\"format\"]}},\"join\":{\"union\":{\"name\":\"union\"}},\"operation\":{\"filter\":{\"allowed\":{\"function\":[\"==\",\"!=\",\"<\",\">\"]},\"function\":\"function\",\"name\":\"filter\",\"value\":\"value\",\"target\":\"target\",\"req\":[\"target\",\"function\",\"value\"]},\"map\":{\"eval\":\"eval\",\"allowed\":{\"operation\":[\"calc\",\"remove\",\"replace\"]},\"name\":\"map\",\"operation\":\"operation\",\"target\":\"target\",\"req\":[\"operation\",\"target\"]}}}","log":"","isRunning":false}';
var autoSocket = new Rete.Socket('autoflink');
var componentsMap = {};
var dockCount = 0;
var lastCleared = {};
var headless = true;

var heightbuff = 300;
var widthbuff = 500;
