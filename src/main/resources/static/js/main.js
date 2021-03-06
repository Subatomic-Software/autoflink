console.log("Begin main.js..");

var jobState = document.getElementById("jobState");
var fileInput = document.getElementById('fileInput');
var schemaInput = document.getElementById('schemaInput');
var logger = document.getElementById("logTextBox");

var startStream;
var stopStream;
var getStatus;

var editor;
var jsonDriver = "";
var jsonEditor;
var jsonLoader = '{\"sink\":{\"print\":{\"name\":\"print\"},\"file\":{\"name\":\"file\",\"format\":{\"schema\":\"schema\",\"allowed\":{\"type\":[\"json\",\"avro\",\"csv\"]},\"name\":\"format\",\"type\":\"type\",\"req\":[\"type\"]},\"directory\":\"directory\",\"req\":[\"directory\",\"format\"]},\"kafka\":{\"name\":\"kafka\",\"format\":{\"schema\":\"schema\",\"allowed\":{\"type\":[\"json\",\"avro\",\"csv\"]},\"name\":\"format\",\"type\":\"type\",\"req\":[\"type\"]},\"topic\":\"topic\",\"broker\":\"broker\",\"req\":[\"broker\",\"topic\",\"format\"]}},\"source\":{\"file\":{\"name\":\"file\",\"format\":{\"schema\":\"schema\",\"allowed\":{\"type\":[\"json\",\"avro\",\"csv\"]},\"name\":\"format\",\"type\":\"type\",\"req\":[\"type\"]},\"directory\":\"directory\",\"req\":[\"directory\",\"format\"]},\"kafka\":{\"groupId\":\"groupId\",\"name\":\"kafka\",\"format\":{\"schema\":\"schema\",\"allowed\":{\"type\":[\"json\",\"avro\",\"csv\"]},\"name\":\"format\",\"type\":\"type\",\"req\":[\"type\"]},\"topic\":\"topic\",\"broker\":\"broker\",\"req\":[\"broker\",\"topic\",\"groupId\",\"format\"]}},\"join\":{\"union\":{\"name\":\"union\"}},\"operation\":{\"filter\":{\"allowed\":{\"function\":[\"==\",\"!=\",\"<\",\">\"]},\"function\":\"function\",\"name\":\"filter\",\"value\":\"value\",\"target\":\"target\",\"req\":[\"target\",\"function\",\"value\"]},\"map\":{\"eval\":\"eval\",\"allowed\":{\"operation\":[\"calc\",\"remove\",\"replace\"]},\"name\":\"map\",\"operation\":\"operation\",\"target\":\"target\",\"req\":[\"operation\",\"target\"]}}}';
var autoSocket = new Rete.Socket('autoflink');
var componentsMap = {};
var dockCount = 0;
var lastCleared = {};

var heightbuff = 300;
var widthbuff = 500;

var variables = [];
var schemas = {};
var schemasToSend = {};
var schemaToValues = {};
var embedSeperator = "."

if (!$("link[href='css/light-theme.css']").length)
    $('<link href="css/light-theme.css" rel="stylesheet">').appendTo("head");