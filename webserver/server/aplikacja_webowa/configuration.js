const IP_NAME = "IP_ADDRES"
const CZUJNIKI_NAME = "CZUJNIKI"
const LEDY_NAME = "LEDY"
const INTERVAL_NAME = "INTERVAL"

function init() {
    var ip_addres = "192.168.56.15";
    var czujniki = "czujniki.php";
    var ledy = "led_display.php";
    var interval = 500;
    sessionStorage.setItem(IP_NAME, ip_addres)
    sessionStorage.setItem(CZUJNIKI_NAME, czujniki)
    sessionStorage.setItem(LEDY_NAME, ledy)
    sessionStorage.setItem(INTERVAL_NAME, interval)
}


const width = 8;
const height = 8;
var hPlot = 0;
var hPlot2 = 0;
var plot_series_r = [];
var plot_series_p = [];
var plot_series_y = [];
var plot_series_temp = [];
var a = 0;
var plot_size = 100;


function onLoadF() {
    document.getElementById("ipa").innerHTML = sessionStorage.getItem(IP_NAME);
    document.getElementById("sampl").innerHTML = sessionStorage.getItem(INTERVAL_NAME);
    document.getElementById("patt1").innerHTML = sessionStorage.getItem(CZUJNIKI_NAME);
    document.getElementById("patt2").innerHTML = sessionStorage.getItem(LEDY_NAME);

}

function changeIP() {
    var new_ip = document.getElementById("new_ip").value;
    sessionStorage.setItem(IP_NAME, new_ip)
    document.getElementById("ipa").innerHTML = new_ip;

    // console.log("Old ip: " + sessionStorage.getItem("IP_ADDRES"))
}

function changeSampleTime() {
    var new_interval = document.getElementById("new_sample").value;
    sessionStorage.setItem(INTERVAL_NAME, parseInt(new_interval))
    document.getElementById("sampl").innerHTML = new_interval;
}

function changePatch1() {
    var new_patch = document.getElementById("new_path1").value;
    sessionStorage.setItem(CZUJNIKI_NAME, new_patch)
    document.getElementById("patt1").innerHTML = new_patch;

}

function changePatch2() {
    var new_patch = document.getElementById("new_path2").value;
    sessionStorage.setItem(LEDY_NAME, new_patch)
    document.getElementById("patt2").innerHTML = new_patch;

}


function refresh_table() {
    // console.log(sessionStorage.getItem("IP_ADDRES"))
    // document.getElementById("error").innerHTML = ""
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("GET", "http://" + sessionStorage.getItem(IP_NAME) + "/" + sessionStorage.getItem(CZUJNIKI_NAME), false);
    try {

        xmlhttp.send();
    }
    catch (error) {
        $("#error").show();
        document.getElementById("error").innerHTML = "Connectionn error";

    }

    var sensorData = xmlhttp.responseText;
    var myObj = JSON.parse(sensorData);
    // document.getElementById("x1").innerHTML = myObj;
    let tableRef = document.getElementById("tablica_danych");
    tableRef.innerHTML = "";
    var nr = tableRef.insertRow(0);
    nr.insertCell().appendChild(document.createTextNode("Parameter"));
    nr.insertCell().appendChild(document.createTextNode("Value"));
    nr.insertCell().appendChild(document.createTextNode("Unit"));
    for (let i = 0; i < myObj.length; i++) {
        // var obj = JSON.parse(myObj[i]);
        var obj = myObj[i];
        let newRow = tableRef.insertRow(-1);
        let name = newRow.insertCell(0);
        let val = newRow.insertCell(1);
        let unit = newRow.insertCell(2);

        name.appendChild(document.createTextNode(obj.name));
        if(obj.name == "m" || obj.name == "x" || obj.name == "y") {
            val.appendChild(document.createTextNode(obj.value));
        } else {
            val.appendChild(document.createTextNode(obj.value.toFixed(2)));
            
        }
        
        unit.appendChild(document.createTextNode(obj.unit));


    }

}

function updateTextInput() {
    var r = document.getElementById("R").value;
    var g = document.getElementById("G").value;
    var b = document.getElementById("B").value;
    document.getElementById("wyswietl_rgb").innerHTML = "RGB: (" + r + ", " + g + ", " + b + ")";

}

function toggle() {
    var r = parseInt(document.getElementById("R").value);
    var g = parseInt(document.getElementById("G").value);
    var b = parseInt(document.getElementById("B").value);
    var data = {}
    for (let y = 0; y < height; y++) {
        for (let x = 0; x < width; x++) {
            const id = `LED${x}${y}`
            var checkbox = document.getElementById(id)
            if (checkbox.checked) {
                console.log(`Id ${id} is on`)
                var entry = [x, y, r, g, b]
                data[`LED${x}${y}`] = JSON.stringify(entry);
            }

        }
    }
        $.ajax({
            type: "post",  //type of method
            url: "http://" + sessionStorage.getItem(IP_NAME) + "/" + sessionStorage.getItem(LEDY_NAME),  //your page
            data: data,// passing the values
            success: function (res) {
                console.log(res)
            },
            error: function (res) {
                $("#error").show();
                // toastr.info("Network error");
                document.getElementById("error").innerHTML = "Connection error"
            }
        });

   

}

function clear_matrix() {
    var data = {}
    for (let y = 0; y < height; y++) {
        for (let x = 0; x < width; x++) {
            var entry = [x, y, 0, 0, 0]
            const id = `LED${x}${y}`
            data[`LED${x}${y}`] = JSON.stringify(entry)
            var checkbox = document.getElementById(id)
            if (checkbox.checked) {
                checkbox.checked = false;
                console.log(checkbox)
            }




        }
    }
    // document.getElementById("error").innerHTML = ""

    
        $.ajax({
            type: "post",  //type of method
            url: "http://" + sessionStorage.getItem(IP_NAME) + "/" + sessionStorage.getItem(LEDY_NAME),  //your page
            data: data,// passing the values
            success: function (res) {
                console.log(res)
            },
            error: function (res) {
                // alert(res.status);
                $("#error").show();
                // toastr.info("Network error");
                document.getElementById("error").innerHTML = "Connection error"
            }
        });



}

function onLoadFcn() {
    $("#error").hide();
    document.getElementById("R").value = 0;
    document.getElementById("G").value = 0;
    document.getElementById("B").value = 0;

    var div = document.getElementById("led-matrix");
    for (var y = 0; y < height; y++) {
        for (var x = 0; x < width; x++) {
            var checkbox = document.createElement("input");
            checkbox.type = "checkbox";
            checkbox.name = `LED${x}${y}`
            checkbox.id = `LED${x}${y}`
            div.appendChild(checkbox);
        }
        div.appendChild(document.createElement("br"));
    }



}

function refresh_plot(accelerometer_data) {
    var r = accelerometer_data[0];
    console.log(accelerometer_data)
    var p = accelerometer_data[1];
    var y = accelerometer_data[2];
    a = a + 1;
    plot_series_r.push([a, r]);
    plot_series_p.push([a, p]);
    plot_series_y.push([a, y]);

    if (a > plot_size) { plot_series_r.shift(); plot_series_p.shift(); plot_series_y.shift(); }
    $.plot($("#figure1"), [{ data: plot_series_r }, { data: plot_series_p }, { data: plot_series_y }], { yaxes: [{}, { position: "right" }] });
}

function refresh_plot_temp(temp) {
    a = a + 1;
    plot_series_temp.push([a, temp]);

    if (a > plot_size) { plot_series_temp.shift() }
    $.plot($("#figure2"), [{ data: plot_series_temp }], { yaxes: [{}, { position: "right" }] });
}

function refresh_function() {
    var xmlhttp = new XMLHttpRequest();
    document.getElementById("error").innerHTML = ""
    xmlhttp.open("GET", "http://" + sessionStorage.getItem(IP_NAME) + "/" + sessionStorage.getItem(CZUJNIKI_NAME), false);
    try {

        xmlhttp.send();
    }
    catch (error) {
        $("#error").show();
        document.getElementById("error").innerHTML = "Connectionn error";

    }


    var sensorData = xmlhttp.responseText;
    var myObj = JSON.parse(sensorData);
    var data_rpy = []
    var temp;
    console.log(myObj);

    for (let i = 0; i < myObj.length; i++) {

        if (myObj[i].name == "roll") {
            data_rpy[0] = myObj[i].value;
        }
        if (myObj[i].name == "pitch") {
            data_rpy[1] = myObj[i].value;
        }
        if (myObj[i].name == "yaw") {
            data_rpy[2] = myObj[i].value;
        }
        if (myObj[i].name == "temperatura") {
            temp = myObj[i].value;
        }

    }

    refresh_plot(data_rpy);
    refresh_plot_temp(temp);
}

function setSample() {
    $("#error").hide();
    setInterval(refresh_table, sessionStorage.getItem(CZUJNIKI_NAME));
}

function wykresyOnLoad() {
    $("#error").hide();
    // toastr.info("Flot test");
    hPlot = $.plot($("#figure1"),
        [
            { data: plot_series_r },
            { data: plot_series_p },
            { data: plot_series_y }
        ],
        {
            yaxes: [{}, { position: "right" }]
        });

    hPlot2 = $.plot($("#figure2"),
        [
            { data: plot_series_temp },

        ],
        {
            yaxes: [{}, { position: "right" }]
        });
    setInterval(refresh_function, sessionStorage.getItem(INTERVAL_NAME));
}
