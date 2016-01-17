require('cloud/app.js');

var secretPasswordToken = 'asdlfjasldkfjasdlfkasdjflji';

Parse.Cloud.define("authorize", function(req, res) {
    var phoneNumber = req.params.phone + "";

    var query = new Parse.Query(Parse.User);
    query.equalTo('username', phoneNumber);
    	query.first().then(function(result) {
    		var min = 1000; var max = 9999;
    		var num = Math.floor(Math.random() * (max - min + 1)) + min;

    		if (result) {
    			result.setPassword(secretPasswordToken + num);
    			result.save().then(function() {
    				return DigitsAuth(req, res)
    			}).then(function() {
    				res.success({});
    			}, function(err) {
    				res.error(err);
    			});
    		} else {
    			var user = new Parse.User();
    			user.setUsername(phoneNumber);
    			user.setPassword(secretPasswordToken + num);
    			user.setACL({});
    			user.save().then(function(a) {
    				return DigitsAuth(req, res)
    			}).then(function() {
    				res.success({});
    			}, function(err) {
    				res.error(err);
    			});
    		}
    	}, function (err) {
    		res.error(err);
    	});
});

function DigitsAuth(req, res) {
    var headers = ["X-Auth-Service-Provider", "X-Verify-Credentials-Authorization"];

    Parse.Cloud.httpRequest({
        method: 'GET',
        url: req.get(headers[0]),
        headers: {'Authorization': req.get(headers[1])},

        success: function(httpResponse) {
            var obj = JSON.parse(httpResponse.text);
            res.status(httpResponse.status).send("success");
        },
        error: function(httpResponse) {
            res.status(400).json({
                error: 'Unable to make a twitter request'
            });
            console.log('Twitter error', httpResponse.text);
        }
    });
}