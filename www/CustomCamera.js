var exec = require('cordova/exec');

exports.cam = function(arg0, success, error) {
    exec(success, error, "CustomCamera", "cam", [arg0]);
};
