var http = require('http');
var fs = require('fs');

var port = 80;

function prettyJSON(string) {
  	string = decodeURIComponent(string);
		console.log(string)
		fs.writeFile('test.json', string);
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
        prettyJSON(body);
    });
		res.end('Post Received');
	}
});

s.listen(port);
console.log('Listen to http://127.0.0.1:' + port);
