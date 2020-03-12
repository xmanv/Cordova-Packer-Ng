var exec = require('cordova/exec');
module.exports = {
    getChannel: function (callback) {
        exec(callback, null, "Channel", "getChannel", []);
    }
}