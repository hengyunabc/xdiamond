/**
 * Created by hengyunabc on 15-5-26.
 */

'use strict';

angular.module('xdiamondApp')
    .filter('accessFilter', ["AccessLevels", function (AccessLevels) {
        return function (input) {
            return AccessLevels[input];
        }
    }])
//.filter('accessFilter', function() {
//    return function(input, uppercase) {
//        input = input || '';
//        var out = "";
//        for (var i = 0; i < input.length; i++) {
//            out = input.charAt(i) + out;
//        }
//        // conditional based on optional argument
//        if (uppercase) {
//            out = out.toUpperCase();
//        }
//        return out;
//    };
//})