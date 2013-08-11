var application_root = __dirname,
    express = require('express'),
    path = require("path");

app = express();

var server = require('http').createServer(app);
var io = require('socket.io').listen(server);

app.configure(function () {
//    app.use(express.logger('dev'));
    app.use(express.bodyParser());
    app.use(express.static(path.join(application_root, "public")));
});

var timeData = new Array();

for (var i = 0; i < 10; i++) {
    timeData[i] = 0;
}

var lastSecond = null;

var zmq = require('zmq');
var sock = zmq.socket('sub');
sock.subscribe("");
sock.connect("host");
//sock.bindSync('tcp://*:5300');
console.log('Consumer bound to port 5300');

var lastMa = null;

io.sockets.on('connection', function (socket) {

});

sock.on('message', function (msg) {
    var jsonData = JSON.parse(msg);
    //if (jsonData.type === 'metric' && jsonData.subType == 'login_event') {
        var data = jsonData.data;
        if (data !== null && data !== undefined) {

            var second = new Date().getSeconds() % timeData.length;
            if (second === 0) {
                second = 10;
            }
            var index = second - 1;

            if (lastSecond === null) {
                lastSecond = second;
            }

            if (lastSecond !== second) {
                //clear out missing records
                for (var i = lastSecond + 1; i < second; i++) {
                    timeData[i] = 0;
                }
                timeData[index] = 0;
            }
            timeData[index]++;
            lastSecond = second;
        }
//    }
//    else {
//        console.log("no equal");
//    }
});

setInterval(function () {
    var j = 0;
    for (var i = 0; i < timeData.length; i++) {
        j += timeData[i];
    }
    var ma = j !== 0 ? Math.ceil(j / timeData.length) : 0;
    if (lastMa == null)
    {
        console.log("setting first lastMa to: " + ma);
        lastMa = ma;
    }
    lastMa = ((lastMa * .1) + (ma *.9));
    console.log("sending ma: " + ma + " vs lastMa:" + lastMa);
    io.sockets.emit('pushdata', lastMa);
}, 1500);

server.listen(8070);
console.log('Server is listening on port 8070...');


//data: { "type":"metric","subType":"login_event", "data":1 }

