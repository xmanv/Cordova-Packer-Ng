package com.weilai.channel;

import com.weilai.channel.packer.PackerNg;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;

public class Channel extends CordovaPlugin {

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    public String getChannel() {
        return PackerNg.getChannel(this.cordova.getActivity());
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getChannel")) {
            callbackContext.success(this.getChannel());
        } else {
            return false;
        }
        return true;
    }
}
