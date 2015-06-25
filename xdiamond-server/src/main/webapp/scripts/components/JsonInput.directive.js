/**
 * Created by hengyunabc on 15-6-25.
 */

'use strict';
//from http://stackoverflow.com/questions/17893708/angularjs-textarea-bind-to-json-object-shows-object-object
angular.module('xdiamondApp').directive('jsonInput', function () {
    'use strict';
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function (scope, elem, attr, ctrl) {
            ctrl.$parsers.push(function (input) {
                try {
                    var obj = JSON.parse(input);
                    ctrl.$setValidity('jsonInput', true);
                    return obj;
                } catch (e) {
                    ctrl.$setValidity('jsonInput', false);
                    return null;
                }
            });
            ctrl.$formatters.push(function (data) {
                if (data == null) {
                    ctrl.$setValidity('jsonInput', false);
                    return "";
                }
                try {
                    var str = JSON.stringify(data, undefined, 2);
                    ctrl.$setValidity('jsonInput', true);
                    return str;
                } catch (e) {
                    ctrl.$setValidity('codeme', false);
                    return "";
                }
            });
        }
    };

});