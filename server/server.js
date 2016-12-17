var http = require('http');
var fs = require('fs');

var port = 80;

function jsonOBJ(string) {
  	string = decodeURIComponent(string);
		//console.log(string)
		json = JSON.parse(string);
		console.log(json.timestamp);

		fs.writeFile('test.json', JSON.stringify(json));
}

var s = http.createServer( function( req, res){

	res.setHeader('Content-Type', "application/json");
	//res.setEncoding('utf8');

	if (req.method == 'POST') {
		var body = '';

    req.on('data', function (data) {
        body += data;
        //console.log("Partial body: " + body);
    });
    req.on('end', function () {
        jsonOBJ(body);
    });
		res.end('Post Received');
	}
});

s.listen(port);
console.log('Listen to http://127.0.0.1:' + port);
