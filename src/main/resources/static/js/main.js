console.log("Begin main.js..");

var jobState = document.getElementById("jobState");
var fileInput = document.getElementById('fileInput');
var logger = document.getElementById("logTextBox");

var editor;
var jsonDriver = "";
var jsonEditor;
var autoSocket = new Rete.Socket('autoflink');
var componentsMap = {};
var dockCount = 0;
var lastCleared = {};

var heightbuff = 300;
var widthbuff = 500;
