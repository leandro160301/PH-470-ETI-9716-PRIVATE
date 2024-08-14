
// const fs = require("fs"); //ESTE VA
// import * as fs from 'fs'
// import * as exceljs from 'exceljs';
// const fs = require('fs');
const fs = window.FileSystemWritableFileStream
window.jQuery = window.$ = jQuery; 


var pdf= window.PDFDocument
console.log(window)
// // const gridjs = require('jsGrid')

// var doc = new jspdf()

let pc;
let autoTable;
let remoteStream;
let turnReady;
var values=[]
var heads=[]
let dataWebSocket;
miStorage = window.localStorage;
let remoteVideo;
//const ip="http://10.42.0.45:8001/"
const ip=""
let pcConfig = {
    'iceServers': [{
        'urls': 'stun:stun.l.google.com:19302'
    }]
}
//

window.onload = init;
window.onbeforeunload = uninit;


function init() {
    console.log('Main: init.');

    // document.getElementById("changi2").style.visibility="collapse"
    var elements = document.getElementsByClassName("z");
for(var i = 0; i < elements.length; i++){
    elements[i].style.visibility = "collapse";
}
    document.getElementById('mySidenav').style.borderRadius="0% 0% 60% 0%"

        var textoo = getraw(ip+"getVersion")
        console.log(textoo)
        if(textoo!=null){
            document.getElementById("Version").textContent=textoo;
        }
    
    varInit();
    webSocketInit();
    turnInit();
}

window.addEventListener('resize', function() {

    var mainnav = document.getElementById('mySidenav');
    mainnav.style.height = "100px";
    mainnav.style.width = "100px";
    document.getElementById("changi").style.visibility="visible"
    document.getElementById("changi2").style.visibility="collapse"
    var elements = document.getElementsByClassName("z");
    for(var i = 0; i < elements.length; i++){
        elements[i].style.visibility = "collapse";
    }
  });
  
function opencloseNav() {

    var mainnav = document.getElementById('mySidenav');
   if( window.innerWidth < window.innerHeight){

    if( mainnav.style.height == "100%"){
        mainnav.style.height = "100px";

        mainnav.style.borderRadius="0% 0% 60% 0%"
        document.getElementById("changi").style.visibility="visible"
        document.getElementById("changi2").style.visibility="collapse"
        var elements = document.getElementsByClassName("z");
        for(var i = 0; i < elements.length; i++){
            elements[i].style.visibility = "collapse";
        }

    }else{
    mainnav.style.height = "100%";
    
    mainnav.style.borderRadius="0% 0%  60px 0%"    
    document.getElementById("changi").style.visibility="collapse"
    document.getElementById("changi2").style.visibility="visible"
   
    var elements = document.getElementsByClassName("z");
    for(var i = 0; i < elements.length; i++){
        elements[i].style.visibility = "visible";
    }
    
    }
   }else{
    if( mainnav.style.width == "100%"){
        mainnav.style.width = "100px";
        
        mainnav.style.borderRadius="0% 0% 60% 0%"
    document.getElementById("changi").style.visibility="visible"
    document.getElementById("changi2").style.visibility="collapse"
    var elements = document.getElementsByClassName("z");
    for(var i = 0; i < elements.length; i++){
        elements[i].style.visibility = "collapse";
    }
    }else{
    mainnav.style.width = "100%";
    mainnav.style.borderRadius="0% 0%  60px 0%"
    document.getElementById("changi").style.visibility="collapse"
    document.getElementById("changi2").style.visibility="visible"
    var elements = document.getElementsByClassName("z");
    for(var i = 0; i < elements.length; i++){
        elements[i].style.visibility = "visible";
    }
    
    }
}
    
}

function home(){
    fbody4()
    
    var mainnav = document.getElementById('mySidenav');
 if( mainnav.style.width == "100%"){
    opencloseNav();
 }
    getraw(ip+"inicio")
}
function uninit() {
    console.log('Main: uninit');

    webSocketUninit();
    varUninit();
}

function varInit() {
    remoteVideo = document.querySelector('#screen');
}

function varUninit() {
    remoteStream = null;
}

// https://stackoverflow.com/questions/247483/http-get-request-in-javascript
// https://stackoverflow.com/questions/14999927/insert-th-in-thead#comment21070057_14999927
// https://stackoverflow.com/questions/18333427/how-to-insert-a-row-in-an-html-table-body-in-javascript
//https://stackoverflow.com/questions/37134433/convert-input-file-to-byte-array

function  BuscarJSON(texto,buscada){
    var array=[]
    array = texto.split(buscada).toTypedArray()
    var res=""
    if(array.size>1){
        array = array[1].split("\"").toTypedArray()
        if(array.size>2){
            res = array[2]
        }
        else{
            res= ""
        }
    }
    else{
        res= ""
    }

    res= res.replace("\\","")
    return res
}


 
    window.addEventListener("consultascroll", function (event) {
        let scroll_y = this.scrollY;
        let scroll_x = this.scrollX;
        var limit = Math.max( document.body.scrollHeight, document.body.offsetHeight, 
            document.documentElement.clientHeight, document.documentElement.scrollHeight, document.documentElement.offsetHeight );
        console.log(scroll_x, scroll_y);
        if(scroll_x==0){

        }
        if(scroll_x==limit){
            
        }else{

        }
    });


function toggleReg(){
    var subir= document.getElementById("subir")
    var bajar= document.getElementById("bajar")
    var tabs= document.getElementById("subirtab")
    var tabb= document.getElementById("bajartab")
    if(subir.classList.contains("on")){
        subir.classList.remove("on")
        tabb.style.display="block"
        tabs.style.display="none"
        subir.classList.add("off")
        bajar.classList.remove("off")
        bajar.classList.add("on")
        tabs.style.visibility="collapse"
        tabb.style.visibility="visible"
        exportingDB.style.visibility="visible"
    }else{
        subir.classList.remove("off")
        subir.classList.add("on")
        bajar.classList.remove("on")
        bajar.classList.add("off")
        tabb.style.display="none"
        tabs.style.display="block"
        
        exportingDB.style.visibility="hidden"
        tabs.style.visibility="visible"
        tabb.style.visibility="collapse"
    }
}
function terceraForma(h,v){
    console.log(h)
    console.log(v)
    const body = document.getElementById("wrapper")
    tbl = document.createElement('table');
    tbl.style.width = '100%';
    tbl.setAttribute("id", "multitable");
    tbl.style.border = '1px solid black';
let i = 0
for (i; i <= v.length; i++) {
const tr = tbl.insertRow();
for (let j = 0; j < h.length; j++) {
    console.log(j+""+""+i)
//   if (i === v.length && j === h.length) {
//     break;
//   } else {
    const td = tr.insertCell();
   td.style.padding="20px"
    if(i===0){
        td.appendChild(document.createTextNode(h[j]));
    }else{
        td.appendChild(document.createTextNode(v[i-1][j]));
    }
    td.style.border = '1px solid black';
}
}
body.appendChild(tbl);
}


var csvContent=""
function arrayToCsv(data){
    
    csvContent = "data:text/csv;charset=utf-8,";
    csvContent += data.map(function(d){
        return d.join();
    }).join("\n");
    console.log(csvContent)
    return csvContent
  // rows starting on new lines
  }
function getraw(urls){    

    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "GET", urls, false ); // false for synchronous request
    xmlHttp.send( null );
    var res= xmlHttp.responseText.toString();
    xmlHttp=null
    return res
}
function sendcsv(){
    console.log(valeglob)
    
    csvContent = arrayToCsv(valeglob)
    console.log(csvContent)
  if(csvContent!=""){
    var t= csvContent.toString().replace(/ /g,'')
    console.log(t)
    var encodedUri = encodeURI(t);
    location.href=encodedUri
   // window.open(encodedUri,statusbar=1);
    csvContent=""
  }else{
    alert("ya se descargo el archivo, vuelva a cargar la tabla")
  }
}

const body1=document.getElementById("body1")
const body2=document.getElementById("body2")
const body3=document.getElementById("body3")
const body4=document.getElementById("body4")

   let vales=[]
   var mapp = new Map([
]);
var vals=[]
var valeglob=[]
function getabla(a){
    cleartable()
    console.log(a)
    
    var res= getraw(ip+a)
    var wrapperElement = document.getElementById("wrapper");
    if(res=="[]"){
        wrapperElement.innerHTML="No hay datos en la tabla"

    }else{
        wrapperElement.innerHTML=""
    }
  
  //  var res = getraw("http://10.41.0.78:8080/GetUltimasPesadas")
    var z=0;
    var limit=0;
    var limax=0
    for(i=0;i<res.length;i++){
        if(res[i]==":" &&  res[i-1]=="\""){
            limax++;
         }
    }
    console.log(limax)
    var headers=[]

    cw=0;
    limit= res.indexOf("\":",limit) 
    while(cw<(limax*2) && limit>=0 && limit<res.length){
   var text=""
  var x=res.indexOf("\"",z+1);
    z=res.indexOf("\"",x+1)
    
    if(x<=limit){
        text=res.substring(x+1,res.indexOf("\"",z))
    
        if(!headers.includes(text)) headers.push(text) //
    
    }else{
        
        text=res.substring(x+1,res.indexOf("\"",z))
        limit= res.indexOf("\":",limit+1) 
        
        if(!headers.includes(text)) vals.push(text.replace(/\\/g, '')) // 
      
        }       
    

      console.log(x,z, limit, limax,text)
      cw++;
  }
  console.log("vals"+vals)
    // console.log("JED:"+headers.length, "VALS:"+vals.length)
   
    // var table = document.getElementById('tablemagico')
    // var theadd=table.createTHead()
    // var tr= theadd.insertRow(0)
    // let count=headers.length
    // var tamanormal=theadd.style.width/count
    // lastword=3;
    let count=headers.length;
    let vueltas =  vals.length/count    
    console.log(vueltas)
    var arrays=[]
 
  
        let vueltax=0;
    while (vueltax<vueltas){
        let countx=0; 
        
        var Col=0;

        vales.push(vals.splice(0, count));
        
        valeglob=vales
         while(countx<count){
            
            mapp.set(vueltax+"_"+headers[Col], vales[vueltax][countx])
            
            //console.log("was"+headers[Col]+vales[vueltax][countx])
            Col++;
            countx++;
         }
         


         vueltax++;
    }   

    if(headers.length>0 && vales.length>0){
        heads=headers
        values=JSON.parse(res)
        }

        headpdf=headers
        valuepdf=vales
  terceraForma(headers,vales);

  vales=[]
  headers=[]
}
function hfs(bytes, dp=2) {
    if (Math.abs(bytes) < 1024) {
      return bytes + ' B';
    }
  
    const units = ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    let u = -1;
    const r = 10**dp;
  
    do {
      bytes /= 1024;
      ++u;
    } while (Math.round(Math.abs(bytes) * r) / r >= 1024 && u < units.length - 1);
  
    return bytes.toFixed(dp) + ' ' + units[u];
  }

function processQueue() {
    while (concurrentUploads < maxConcurrentUploads) {
        if (dirQueue.length > 0) {
          //  processDirQueue();
            continue;
        }
        if (fileQueue.length == 0)
            break;
        var f = fileQueue.shift();
        sendFile(f);
    }
    document.getElementById("headerizador").innerText = filesUploaded + " files uploaded,  " + fileQueue.length + " in queue";
}

function updateUploadPercent() {
    var totalPercent = bytesUploaded / uploadTotal * 100;
    document.getElementById("headerizador").innerText = "Upload (" + totalPercent.toFixed(2) + "%)";
}
function onFinishedUpload() {
    if (dirQueue.length + fileQueue.length + concurrentUploads == 0) {
        if (errors > 0) {
            alert(errors + " items were not uploaded. Please refresh the page and try again.");
            errors = 0;
        }
        else location.reload();
    }
    //processQueue(); //From callback, so no recursion
}

function filter(event,url){
    
    let filasCoincidentes = [];
    var valorBuscado=event.target.value
    var headers=headpdf
    if(valorBuscado!=""){//&& valorBuscado.length>3){
    var tabla= valuepdf//getraw(ip+url)
    tabla.forEach(function(item) {
        console.log(item)
        item.forEach(function(element , index) {
            var str =element.toString().toLowerCase()
            // var l= str.toString().toLowercase()
            console.log(element)
             if(str.indexOf(valorBuscado.toLowerCase())!=-1){
                if(!filasCoincidentes.includes(item)){
                 filasCoincidentes.push(item)
                 }
            }
             
        
        });
    });
    }else{
        filasCoincidentes=valuepdf
    }

    cleartable()
   
    terceraForma(headers,filasCoincidentes)
    }

function scrollTo(hash) {

    // if(turnReady){
    document.getElementById(hash).scrollIntoView({
        behavior: 'auto',
        block: 'center',
        inline: 'center'
    });
  
}

  var B1 =document.getElementById("navb1")
  
  var B2 =document.getElementById("navb2")
  
  var B3 =document.getElementById("navb3")
  
  var B4 =document.getElementById("navb4")
function fbody1(){
    B1.classList.add("act")
    B2.classList.remove("act")
    B3.classList.remove("act")
    B4.classList.remove("act")
    cleartable()
    body1.style.visibility="visible";
    body1.style.display= "block";   
    body2.style.visibility="collapse"; 
    body2.style.display= "none";   
    body3.style.visibility="collapse";
    body3.style.display= "none";   
    body4.style.visibility="collapse";
    body4.style.display= "none";   
    
    
    if( window.innerWidth < window.innerHeight){
        opencloseNav()
        }
    // opencloseNav()
    
    var res= getraw(ip+"getConsultas") 
    vales=[]
    var obj= JSON.parse(res);
    
    var combo = document.getElementById("navbarTogglerDemo01");

    var text = //<div class="collapse navbar-collapse">  <a class="navbar-brand" href="#">${obj[0].Nombre}</a> </div>
    ` <ul class="navbar-nav me-auto mb-2 mb-lg-0"> <li class=" nav-item  active  "  id="li0" ><a id="a0"  onclick="navclickgetabla('${obj[0].Nombre}','${obj[0].GET}',0,${obj.length})"  class="nav-link active " href="#">${obj[0].Nombre}</a></li>`   

    //  for(let i=0;i<7;i++){
    obj.forEach((elementt,i) =>{  
        if(i>0){
            console.log("mondongo",i)  
            text=text+`<li   id="li${i}" class="nav-item"><a   id="a${i}"   onclick="navclickgetabla('${obj[i].Nombre}','${obj[i].GET}',${i},${obj.length})"    class="nav-link" href="#" > ${elementt.Nombre}</a></li>`
        } })
    //  }
        text=text+"</ul>"
    combo.innerHTML=text
    filenamepdf=obj[0].Nombre
    lasturltable=obj[0].GET
    getabla(obj[0].GET)
    document.getElementById("filter").addEventListener("change", function(ev){
        filter(ev,lasturltable);
    })
}
function crearxlsx(){
   
const table = document.getElementById('multitable');
  const wb = XLSX.utils.table_to_book(table, {sheet: 'sheet-1'});
  XLSX.writeFile(wb, filenamepdf+'.xlsx');

}
var filenamepdf=""
var headpdf=[]
var valuepdf=[]
// function crearPdf(){
//     let doc = new pdf({ margin: 30, size: 'A4' });
//     // save document
    
//     //doc.pipe(fs.writeFile("./"+filenamepdf+".pdf"));
//     (async function createTable(){
//       //table
//       const table = { 
//         title: filenamepdf,
//         headers: headpdf,
//         rows:valuepdf,
//       };
  
//     //   the magic (async/await)
//       await doc.table(table, { /* options */ });
//     //   -- or --
//       doc.table(table).then(() => { doc.end() }).catch((err) => { })
  
//     //   if your run express.js server
//     //   to show PDF on navigator
//     doc.pipe(blobStream);
//     doc.end();
//     blobStream.on('finish', function() {
//         // Establecer el enlace de descarga
//         enlaceDescarga.href = URL.createObjectURL(blobStream.toBlob('application/pdf'));
//         enlaceDescarga.download = 'documento.pdf'; // Nombre del archivo a descargar
    
//         // Simular un clic en el enlace para iniciar la descarga
//         enlaceDescarga.click();
//     });
//     })();} //pdfkit (es mas para node.js)


function crearPdf(){
        var doc = new jsPDF('p', 'pt', 'letter')
        var table = document.getElementById("multitable")
        // Supply data via script
        // var body = [
        //            ['SL.No', 'Product Name', 'Price', 'Model'],
        //            [1, 'I-phone', 75000, '2021'],
        //            [2, 'Realme', 25000, '2022'],
        //            [3, 'Oneplus', 30000, '2021'],
        //            ]
        // generate auto table with body
        var y = 10;
        doc.setLineWidth(2);
        doc.text(filenamepdf,300, y = y + 30, 'center');
        doc.autoTable({
            html:table,
            startY: 70,
            theme: 'grid',
                     })
        // save the data to this file
        doc.save(filenamepdf);
    }

function navclickgetabla(nom,get,i,lim){
    // console.log(globalgridjs)

    cleartable()
     document.getElementById("a0").classList.remove("active")
console.log("navcicker")
if(!document.getElementById("a"+i).classList.contains("active")){
    for(let x=0;x<lim;x++){
        if(document.getElementById("li"+x).classList.contains("active")){ 
            document.getElementById("li"+x).classList.remove("active") 
            document.getElementById("a"+x).classList.remove("active") 
            document.getElementById("li"+x).removeAttribute("aria-current")
            document.getElementById("a"+x).removeAttribute("aria-current")
            }
    }
    var id="li"+i
    var id2="a"+i
    document.getElementById(id).classList.add("active")
    document.getElementById(id).setAttribute("aria-current","page")
    document.getElementById(id2).classList.add("active")
    document.getElementById(id2).setAttribute("aria-current","page")
    filenamepdf=nom
    lasturltable=get
  getabla(get)
}

}
var lasturltable=""
function BuscarJSON(texto, buscada) {
    var array = texto.split(buscada);
    var res = "";

    if (array.length > 1) {
        array = array[1].split("\"");
        if (array.length > 2) {
            res = array[2];
        } else {
            res = "";
        }
    } else {
        res = "";
    }

    res = res.replace("\\", "");
    return res;
}

function fbody2(){
    B1.classList.remove("act")
    B2.classList.add("act")
    B3.classList.remove("act")
    B4.classList.remove("act")
    cleartable()
    body1.style.visibility="collapse";
    body1.style.display= "none";   
    body2.style.visibility="visible"; 
    body2.style.display= "block";   
    body3.style.visibility="collapse";
    body3.style.display= "none";   
    body4.style.visibility="collapse";
    body4.style.display= "none";   
    // opencloseNav()
  
    if( window.innerWidth < window.innerHeight){
        opencloseNav()
        }
}
 var objetotabla
 function getrawarch(urls,nom){
    // console.log("la url:"+urls)
    // console.log("archivo:"+nom)
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "POST", urls, true ); // false for synchronous request
  
    xmlHttp.setRequestHeader("Content-Type","text/plain; charset=UTF-8")
    xmlHttp.responseType = "blob"; // Importante: indicar que esperamos un blob como respuesta
    document.getElementById("headerizador").style.visibility="visible"
    var progresstext = document.createElement("a");
    progresstext.style.padding="10px";
    var pbar = document.createElement("progress");
    document.getElementById("headerizador").appendChild(progresstext);
    document.getElementById("headerizador").appendChild(pbar);
    progresstext.innerText = nom

        var loadedB = 0;
        var bytesUploaded=""
    xmlHttp.onload = function (event) {
        bytesUploaded += event.loaded - loadedB;
        loadedB = event.loaded;
        pbar.max = event.total
        pbar.value = event.loaded;
      // updateUploadPercent();
      
       var blob = xmlHttp.response;
        var fileName = nom// Nombre que quieras dar al archivo descargado
        var link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = fileName;
        link.click();
    };
    
    xmlHttp.onreadystatechange = function () {
        if(xmlHttp.readyState === 4) {
            pbar.remove();
            if (xmlHttp.status != 200) {
                progresstext.innerText = "File " + nom + " failed to upload. Status: " + xmlHttp.status + "\n" + xmlHttp.responseText;
              //  errors++;
            }  else {
        document.getElementById("spinnerload").style.visibility="hidden"
                progresstext.remove();
    document.getElementById("headerizador").style.visibility="hidden"
                //filesUploaded++;
            }
            //File completed
            xmlHttp.onreadystatechange = null; //Don't call twice
           // bytesUploaded += file.size - loadedB;
            //updateUploadPercent();
            //onFinishedUpload();
        }
    };
    xmlHttp.send(encodeURIComponent(nom));
        
    var res= xmlHttp.response
    setTimeout(() => {
        document.getElementById("headerizador").style.visibility="hidden"
        document.getElementById("spinnerload").style.visibility="hidden"
    }, 1000);
   
    return res
 }
 async function descargardb(){
   // var file = getrawarch(ip+"descargarDB","")
   document.getElementById("headerizador").style.visibility="visible"
   document.getElementById("spinnerload").style.visibility="visible"
   var xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "POST", ip+"descargarDB", true ); // false for synchronous request
    xmlHttp.responseType="blob"
    xmlHttp.setRequestHeader("Content-Type","text/plain; charset=UTF-8")
    document.getElementById("headerizador").style.visibility="visible"
    var progresstext = document.createElement("a");
    progresstext.style.padding="10px";
    var pbar = document.createElement("progress");
    document.getElementById("headerizador").appendChild(progresstext);
    document.getElementById("headerizador").appendChild(pbar);
    progresstext.innerText = "Database"

        var loadedB = 0;
        var bytesUploaded=""
 
 // Importante: indicar que esperamos un blob como respuesta

 
    xmlHttp.onload = function (event) {
        bytesUploaded += event.loaded - loadedB;
        loadedB = event.loaded;
        pbar.max = event.total
        pbar.value = event.loaded;
        var blob = xmlHttp.response;
        var fileName = "Database"// Nombre que quieras dar al archivo descargado
        var link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = fileName;
        link.click();
    };
    
    xmlHttp.onreadystatechange = function () {
        if(xmlHttp.readyState === 4) {
            pbar.remove();
            if (xmlHttp.status != 200) {
                progresstext.innerText = "File Database failed to upload. Status: " + xmlHttp.status + "\n" + xmlHttp.responseText;
              //  errors++;
            }  else {
        document.getElementById("spinnerload").style.visibility="hidden"
                progresstext.remove();
    document.getElementById("headerizador").style.visibility="hidden"
                //filesUploaded++;
            }
            //File completed
            xmlHttp.onreadystatechange = null; //Don't call twice
           // bytesUploaded += file.size - loadedB;
            //updateUploadPercent();
            //onFinishedUpload();
        }
    };
    xmlHttp.send();
        
    var res= xmlHttp.response
    setTimeout(() => {
        document.getElementById("headerizador").style.visibility="hidden"
        document.getElementById("spinnerload").style.visibility="hidden"
    }, 1000);
   
    //encodeURIComponent(nom)
   
        
    var res= xmlHttp.response
    return res
 }
async function descarga(obj){
    var nom = objetotabla[obj].Nombre+"."+objetotabla[obj].Tipo
   // console.log(nom)
    document.getElementById("headerizador").style.visibility="visible"
    document.getElementById("spinnerload").style.visibility="visible"
    var file = getrawarch(ip+"descargarArchivo",nom)
    //console.log("archivo:"+file)
  
    // switch(objetotabla[obj].Tipo){
    //     case ".png":{
    //         //const bufferFromArray = Buffer.from(file);
    //        // var a=fs.writeFile(objetotabla[obj].Nombre, bufferFromArray)
    //         var blob = new Blob([file], {type: "application/png"});
    //         var objectUrl = URL.createObjectURL(blob);
    //         console.log(objectUrl)
    //         window.open(objectUrl);
    
    //         //a.save()
    //         // x_canvas.toBlob(function(blob) {
    //         //     saveAs(bufferFromArray,objetotabla[obj].Nombre);
    //         //     }, "image/png");
    //     }
    //     case".pdf":{

    //     }
    //     case".xlsx",".xls":{


    //     }
    // }
    
      }


function fbody3(){
    B1.classList.remove("act")
    B2.classList.remove("act")
    B3.classList.add("act")
    B4.classList.remove("act")
    cleartable()
    body1.style.visibility="collapse";
    body1.style.display= "none";   
    body2.style.visibility="collapse"; 
    body2.style.display= "none";   
    body3.style.visibility="visible";
    body3.style.display= "block";   
    body4.style.visibility="collapse";
    body4.style.display= "none";  
    var res= getraw(ip+"getArchivos")
    const obj = JSON.parse(res);
    objetotabla=JSON.parse(res);
    console.log(obj)
    
    if( window.innerWidth < window.innerHeight){
        opencloseNav()
        }
    var headers= ["Tipo","Nombre","Fecha"]
    var table = document.getElementById('tablemagico')
    var theadd=table.createTHead()
    var tr= theadd.insertRow(0)
    headers.forEach((element,i) => {
        var cel=tr.insertCell(-1)
      // cel.style.width=tamanormal*2
      cel.outerHTML =`<th scope="col" style="border:2px;border-color:white" class="col">`+element+"</th>";
    })
          var cel=tr.insertCell(-1)
      // cel.style.width=tamanormal*2
      cel.outerHTML =`<th scope="col" style="border:2px;border-color:white" class="col"></th>`;
    var tbodyy=table.createTBody()
    var tg= tbodyy.insertRow(0)
    
    obj.forEach((element,i) => { 
       
        cel=tg.insertCell(-1)
        cel.outerHTML =`<td  style="border:2px;border-color:white" class="col"
        >`+element.Tipo+"</td>";
        cel=tg.insertCell(-1)
        cel.outerHTML =`<td  style="border:2px;border-color:white" class="col"
        >`+element.Nombre+"</td>";
        cel=tg.insertCell(-1)
        cel.outerHTML =`<td  style="border:2px;border-color:white" class="col"
        >`+element.Fecha+"</td>";
        cel=tg.insertCell(-1)
       cel.outerHTML =`<td >       
       <button type="button" class="btn btn-light"style="width:100%" id="descarga"  onclick="descarga(${i})"><svg xmlns="http://www.w3.org/2000/svg" height="15" width="15" viewBox="0 0 512 512"><!--!Font Awesome Free 6.5.1 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.--><path d="M288 32c0-17.7-14.3-32-32-32s-32 14.3-32 32V274.7l-73.4-73.4c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3l128 128c12.5 12.5 32.8 12.5 45.3 0l128-128c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0L288 274.7V32zM64 352c-35.3 0-64 28.7-64 64v32c0 35.3 28.7 64 64 64H448c35.3 0 64-28.7 64-64V416c0-35.3-28.7-64-64-64H346.5l-45.3 45.3c-25 25-65.5 25-90.5 0L165.5 352H64zm368 56a24 24 0 1 1 0 48 24 24 0 1 1 0-48z"/></svg> 
         </td>`
             tbodyy=table.createTBody()
             tg= tbodyy.insertRow(0)  
    });
    // opencloseNav()
}
function fbody4(){
    B1.classList.remove("act")
    B2.classList.remove("act")
    B3.classList.remove("act")
    B4.classList.add("act")
    cleartable()
    body1.style.visibility="collapse";
    body1.style.display= "none";   
    body2.style.visibility="collapse"; 
    body2.style.display= "none";   
    body3.style.visibility="collapse";
    body3.style.display= "none";   
    body4.style.visibility="visible";
    body4.style.display= "block";   
    // opencloseNav()
    scrollTo("screen")
    
    if( window.innerWidth < window.innerHeight){
        opencloseNav()
        }
}

function webSocketInit() {
    console.log('WebSocket: init.');
    

    if(ip.replace("http://","") ==""){
    dataWebSocket = new WebSocket ('ws://'+ window.location.host) //window.location.host);
    }else{
    dataWebSocket = new WebSocket('ws://'+ (ip.replace("http://",""))) //window.location.host);
}
    // dataWebSocket = new WebSocket('ws://' + window.location.host);
    dataWebSocket.onopen = onWsOpen;
        //dataWebSocket.onclose = onWsClose;
        dataWebSocket.onclose = onWsClose;
        dataWebSocket.onclose = function(){
            webSocketInit()
            turnInit()
            };
    dataWebSocket.onerror = onWsError;
    dataWebSocket.onmessage = onWsMessage;
}

function webSocketUninit() {
    console.log('WebSocket: uninit.');

    sendMessage('{type:bye}');
    dataWebSocket.close();
    dataWebSocket = null;
}

function onWsOpen(event) {
    console.log("WebSocket: opened");

    sendMessage('{type:join}');
    
    mouseInit(dataWebSocket);
}

function onWsClose(event) {
    console.log('WebSocket: closed.');

    mouseUninit();
    destroyPeerConnection();
}

function onWsError(error) {
    console.log("WebSocket: error: " + error.message);
}

function onWsMessage(event) {
    console.log('WebSocket: received message:', event);

    let message = JSON.parse(event.data);
    if (message.type === 'sdp')
        handleSdpMessage(message);
    else if (message.type === 'ice')
        handleIceMessage(message);
    else if (message.type === 'bye')
        handleRemoteHangup();
}

function handleSdpMessage(message) {
    if (message.sdp.type === 'offer') {
        createPeerConnection();
        pc.setRemoteDescription(new RTCSessionDescription(message.sdp));
        doAnswer();
    }
}

function createPeerConnection() {
    console.log('WebRTC: create RTCPeerConnnection.');

    try {
        pc = new RTCPeerConnection(null);
        pc.onicecandidate = handleIceCandidate;
        pc.onaddstream = handleRemoteStreamAdded;
        pc.onremovestream = handleRemoteStreamRemoved;
    } catch (e) {
        console.log('WebRTC: Failed to create PeerConnection, exception: ' + e.message);
        return;
    }
}


function sendapk(event){

  
    if(event.target.files[0]!=null){
    var file=event.target.files[0]
    filejojo=file
    let bytes = [];
    var reader = new FileReader();
    document.getElementById("parraf").innerText="Esta zona contiene:"+file.name+" arrastra el apk en esta zona"

    // reader.onloadend = function () {
    //     bytes = reader.result;
    //     console.log(bytes);

    // };
    document.getElementById("buttonsend").style.visibility="visible"
    // var filePath = file.webkitRelativePath || file.mozFullPath || '';
    // console.log("Dirección del archivo: " + filePath);

    // bytes= reader.readAsArrayBuffer(file);
    // console.log("BUFFER "+file.size)
    // filebyt=file
    // console.log("mondongo"+file.mozFullPath)
    // size=file.size
    // namefile=file.name.toLowerCase()

   
}
      
}



function sendact(file){
    //concurrentUploads++;
        // var nombrarch = document.createElement("nombrarch");
        // nombrarch.innerHTML= file.name
        document.getElementById("headerizador").style.visibility="visible"
        var progresstext = document.createElement("a");
        progresstext.style.padding="10px";
        var pbar = document.createElement("progress");
        document.getElementById("headerizador").appendChild(progresstext);
        document.getElementById("headerizador").appendChild(pbar);
        var xhr = new XMLHttpRequest();
        // if (file.hasOwnProperty("path")){
            path = file.path;
            //xhr.open("PUT", window.location + encodeURIParts(path), true);
            //xhr.setRequestHeader("X-Last-Modified", file.lastModified);
            //xhr.overrideMimeType(file.type);
            xhr.open('POST', ip+'updateApk', true);
            xhr.setRequestHeader("Content-Type","text/plain; charset=UTF-8")
            // xhr.setRequestHeader("X-Last-Modified", file.lastModified);
            xhr.overrideMimeType(file.type);
            xhr.setRequestHeader("nombre",file.name)

            pbar.max = file.size;
            var loadedB = 0;

            var bytesUploaded=""
        xhr.upload.addEventListener("progress", function(e) {
            bytesUploaded += e.loaded - loadedB;
            loadedB = e.loaded;
            pbar.value = e.loaded;
          // updateUploadPercent();
            progresstext.innerText = file.name + " " + hfs(e.loaded) + "/" + hfs(e.total);
            if(e.loaded==e.total){
                document.getElementById("spinnerload").style.visibility="visible"
                }
        }, false);
    
        xhr.onreadystatechange = function () {
            if(xhr.readyState === 4) {
                pbar.remove();
                if (xhr.status != 200) {
                    progresstext.innerText = "File " + file.name + " failed to upload. Status: " + xhr.status + "\n" + xhr.responseText;
                    errors++;
                } else {
                    progresstext.innerText= xhr.responseText;
                    document.getElementById("spinnerload").style.visibility="hidden"
                    setTimeout(() => {
                    progresstext.remove();
                    
                    document.getElementById("headerizador").style.visibility="hidden"
                    }, 2000);
                    filesUploaded++;
                }

                //File completed
                xhr.onreadystatechange = null; //Don't call twice
                bytesUploaded += file.size - loadedB;
                updateUploadPercent();
              //  onFinishedUpload();
            }
        };
    
        xhr.send(file);
        // currentUpload = xhr;
    
    
    //--
    // var xhr = new XMLHttpRequest();
    
    // xhr.onreadystatechange = function() {
    //     if (xhr.readyState === XMLHttpRequest.DONE) {
    //         if (xhr.status === 200) {
    //             console.log('Éxito');
    //         } else {
    //             console.log('Error');
    //         }
    //     }
    // };
    
    // xhr.open('POST', ip+'updateApk', true);
    // xhr.setRequestHeader("Content-Type","text/plain; charset=UTF-8")
    // xhr.setRequestHeader("X-Last-Modified", file.lastModified);
    // xhr.overrideMimeType(file.type);
    // xhr.setRequestHeader("nombre",file.name)

    // xhr.send(file);
    
    
        
    }


    function sendact2(file){   
        document.getElementById("headerizador").style.visibility="visible"
        var progresstext = document.createElement("a");
        progresstext.style.padding="10px";
        var pbar = document.createElement("progress");
        document.getElementById("headerizador").appendChild(progresstext);
        document.getElementById("headerizador").appendChild(pbar);
        var xhr = new XMLHttpRequest();
        // if (file.hasOwnProperty("path")){
            path = file.path;
            //xhr.open("PUT", window.location + encodeURIParts(path), true);
            //xhr.setRequestHeader("X-Last-Modified", file.lastModified);
            //xhr.overrideMimeType(file.type);
            xhr.open('POST', ip+'sendFiles', true);
            // xhr.setRequestHeader("Content-Type","text/plain; charset=UTF-8")
            // xhr.setRequestHeader("X-Last-Modified", file.lastModified);
            xhr.overrideMimeType(file.type);
            xhr.setRequestHeader("nombre",encodeURI(file.name))

            pbar.max = file.size;
            var loadedB = 0;

            var bytesUploaded=""
        xhr.upload.addEventListener("progress", function(e) {
            bytesUploaded += e.loaded - loadedB;
            loadedB = e.loaded;
            pbar.value = e.loaded;
           
          // updateUploadPercent();
            progresstext.innerText = file.name + " " + hfs(e.loaded) + "/" + hfs(e.total);
            if(e.loaded==e.total){
            document.getElementById("spinnerload").style.visibility="visible"
            }
        }, false);
    
        xhr.onreadystatechange = function () {
            if(xhr.readyState === 4) {
                pbar.remove();
                if (xhr.status != 200) {
                    progresstext.innerText = "File " + file.name + " failed to upload. Status: " + xhr.status + "\n" + xhr.responseText;
                  //  errors++;
                }  else {
                    progresstext.innerText= xhr.responseText;
                    
                    
            document.getElementById("spinnerload").style.visibility="hidden"
                    setTimeout(() => {
                    progresstext.remove();
        document.getElementById("headerizador").style.visibility="hidden"
                    }, 2000);
                    filesUploaded++;
                }
                //File completed
                xhr.onreadystatechange = null; //Don't call twice
                bytesUploaded += file.size - loadedB;
                updateUploadPercent();
               // onFinishedUpload();
            }
        };
    
        xhr.send(file);
        
        
        
        // ---
        // var xhr = new XMLHttpRequest();
        
        // xhr.onreadystatechange = function() {
        //     if (xhr.readyState === XMLHttpRequest.DONE) {
        //         if (xhr.status === 200) {
        //             console.log('Éxito');
        //         } else {
        //             console.log('Error');
        //         }
        //     }
        // };
        
        // xhr.open('POST', ip+'sendFiles', true);
        // xhr.setRequestHeader("X-Last-Modified", file.lastModified);
        // xhr.setRequestHeader("Content-Type","text/plain; charset=UTF-8")
        // xhr.setRequestHeader("nombre",file.name)
        // xhr.overrideMimeType(file.type);
        // var formData = new FormData();
        // formData.append('file', file);
    
        // xhr.send(file);
        
        
            
        }

   


function sendarchReg(event){
   
    if(event.target.files[0]!=null){
    var file=event.target.files[0]
    filejajaja=file
    document.getElementById("buttonsendReg").style.visibility="visible"
    // var reader = new FileReader();
     document.getElementById("parrafReg").innerText="Esta zona contiene:"+file.name+" arrastra el archivo en esta zona"

    // reader.onloadend = function () {
    //     bytes = reader.result;
    //     console.log(bytes);

    // };
     document.getElementById("buttonsendReg").style.visibility="visible"
    // bytes= reader.readAsArrayBuffer(file);

    
}
      
}

//-------------------------------------- drag drop
function dropHandler(ev) {
    console.log("File(s) dropped");
  
    // Prevent default behavior (Prevent file from being opened)
    ev.preventDefault();
   var file= ev.dataTransfer.files[0]
   if(file.name.includes(".apk")){document.getElementById("parraf").innerText="Esta zona contiene:"+file.name+" arrastra el apk en esta zona"
        const dataTransfer = new DataTransfer();
        dataTransfer.items.add(file);
        var fileInput= document.getElementById("apkx")
        fileInput.files = dataTransfer.files;
        file=fileInput.files[0]
        document.getElementById("buttonsend").style.visibility="visible"
       // sendapk(ev,1)
       //let bytes=readAsArrayBuffer(file)
       //filebyt=file 
       //console.log("mondongo"+ file)
       //size=file.size
        //namefile=file.name.toLowerCase()
        //sendact(bytes,file.size,file.name.toLowerCase())
        filejojo=file
       console.log( file.size)
       // document.getElementById("buttonsend").addEventListener('click', function() {
       //     sendact(file)
      //  });
        

    
    }else{
        alert("En este apartado es necesario que el archivo sea un apk")
    }
  }
  var filejojo
  var filejajaja
  var filebyt
  var size
  var namefile
  function dragOverHandler(ev) {
   // console.log("File(s) in drop zone");
    // Prevent default behavior (Prevent file from being opened)
    ev.preventDefault(); }


    function dropHandlerReg(ev) {
        console.log("File(s) dropped");
        event=ev
        // Prevent default behavior (Prevent file from being opened)
        ev.preventDefault();
       var file= ev.dataTransfer.files[0]
        document.getElementById("parrafReg").innerText="Esta zona contiene:"+file.name+" arrastra el archivo en esta zona"
        const dataTransfer = new DataTransfer();
        dataTransfer.items.add(file);
        var fileInput= document.getElementById("apkxReg")
        fileInput.files = dataTransfer.files;

        filejajaja=fileInput.files[0]
        document.getElementById("buttonsendReg").style.visibility="visible"
        //sendact2(filejajaja)
      }
    
    
    
  //---------------------------------------------
function cleartable()   {
    var Table = document.getElementById("tablemagico");
    var Table2 = document.getElementById("multitable")

    
    if(Table2!=null){
        $("#multitable").remove(); 
   }
   if(Table!=null){
    $("#tablemagico").remove(); 
    // Table.innerHTML= ""
    // Table.outerHTML=""
     var bod=document.getElementById("bajartab")
    bod.innerHTML='<table id="tablemagico" class="table table-bordered" ></table>'
}
document.getElementById("apkx").value = "";
document.getElementById("apkxReg").value = "";
document.getElementById("parrafReg").innerText="arrastra el archivo en esta zona"
document.getElementById("parraf").innerText="arrastra el apk en esta zona"
document.getElementById("buttonsendReg").style.visibility="hidden"
document.getElementById("buttonsend").style.visibility="hidden"
filebyt=""
filejojo=""


}

function destroyPeerConnection() {
    console.log('WebRTC: destroy RTCPeerConnnection.');

    if (pc == null)
        return;
    pc.close();
    pc = null;
}

function doAnswer() {
    console.log('WebRTC: create answer.');

    pc.createAnswer().then(
        setLocalAndSendMessage,
        onCreateSessionDescriptionError
    );
}

function setLocalAndSendMessage(sessionDescription) {
    pc.setLocalDescription(sessionDescription);
    sendSdpMessage(sessionDescription);
}


function sendSdpMessage(message) {
    console.log('WebSocket: client sending message: ', message);
    sendMessage('{type=sdp,sdp=' + JSON.stringify(message) + '}');
   
}

function onCreateSessionDescriptionError(error) {
    console.log('WebRTC: failed to create session description: ' + error.toString());
}

function handleIceMessage(message) {
    if (message.ice.type === 'candidate') {
        let candidate = new RTCIceCandidate({
            sdpMLineIndex: message.ice.label,
            candidate: message.ice.candidate
        });
        pc.addIceCandidate(candidate).then(onAddIceCandidateSuccess, onAddIceCandidateError);
    }
}

function onAddIceCandidateSuccess() {
    console.log('WebRTC: Ice candidate successfully added.');
}

function onAddIceCandidateError(error) {
    console.log('WebRTC: failed to add ice candidate: ' + error.toString());
}

function sendIceMessage(message) {
    console.log('WebSocket: client sending message: ', message);

    sendMessage('{type=ice,ice=' + JSON.stringify(message) + '}')
}

function handleIceCandidate(event) {
    console.log('WebRTC: icecandidate event: ', event);

    if (event.candidate) {
        sendIceMessage({
            type: 'candidate',
            label: event.candidate.sdpMLineIndex,
            id: event.candidate.sdpMid,
            candidate: event.candidate.candidate
        });
    } else {
        console.log('WebRTC: end of candidates.');
        setTimeout(() => {
            scrollTo("screen")
        }, 1000);
       
    }
}

function turnInit() {
//TODO
//    if (location.hostname !== 'localhost') {
//        requestTurn(
//            'https://computeengineondemand.appspot.com/turn?username=41784574&key=4080218913'
//        );
//    }
}

function requestTurn(turnURL) {
    let turnExists = false;
    for (let i in pcConfig.iceServers) {
        if (pcConfig.iceServers[i].urls.substr(0, 5) === 'turn:') {
            turnExists = true;
            turnReady = true;
            break;
        }
    }
    if (!turnExists) {
        console.log('WebRTC: getting TURN server from ', turnURL);
        // No TURN server. Get one from computeengineondemand.appspot.com:
        let xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                let turnServer = JSON.parse(xhr.responseText);
                console.log('Got TURN server: ', turnServer);
                pcConfig.iceServers.push({
                    'urls': 'turn:' + turnServer.username + '@' + turnServer.turn,
                    'credential': turnServer.password
                });
                turnReady = true;
                scrollTo("screen")
                
            }
        };
        xhr.open('GET', turnURL, true);
        xhr.send();
     
    }
    
}

function handleRemoteStreamAdded(event) {
    console.log('WebRTC: remote stream added.');
    remoteStream = event.stream;
    remoteVideo.srcObject = remoteStream;
   
}

function handleRemoteStreamRemoved(event) {
    console.log('WebRTC: Remote stream removed. Event: ', event);
}

function handleRemoteHangup() {
    console.log('WebSocket: session terminated by remote party.');
    destroyPeerConnection();
   
}

function sendMessage(message) {
    if (dataWebSocket == null)
        return;

    console.log('WebSocket: client sending message: ', message);
    dataWebSocket.send(message);
}
