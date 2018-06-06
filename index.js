"use strict";

import { Platform, NativeModules } from "react-native";

let noIOS = Platform.OS !== "ios";
let NativeIntentModule = NativeModules.NativeIntentModule;
let noop = () => {};

const intentModule = {
  openNativeModule(viewName, data) {
    return new Promise((resolve, reject) => {
      NativeIntentModule.openNativeModule(
        viewName,
        data,
        result => {
          resolve(result);
        },
        err => {
          reject(err);
        }
      );
    });
  },

  getDataFromIntent(key) {
    if (noIOS) {
      return NativeIntentModule.getDataFromIntent(key);
    } else {
      return null;
    }
  },

  getStoreForKey(key) {
    return new Promise((resolve, reject) => {
      NativeIntentModule.getStoreForKey(
        key,
        result => {
          resolve(result);
        },
        err => {
          reject(err);
        }
      );
    });
  },

  setStore(key, value) {
    return new Promise((resolve, reject) => {
      NativeIntentModule.setStore(
        key,
        value,
        result => {
          resolve(result);
        },
        err => {
          reject(err);
        }
      );
    });
  },

  removeStoreForKey(key) {
    return new Promise((resolve, reject) => {
      NativeIntentModule.removeStoreForKey(
        key,
        result => {
          resolve(result);
        },
        err => {
          reject(err);
        }
      );
    });
  },

  openScheme(scheme, successCallback = noop, errorCallback = noop) {
    NativeIntentModule.openScheme(scheme, successCallback, errorCallback);
  }
};

module.exports = intentModule;
