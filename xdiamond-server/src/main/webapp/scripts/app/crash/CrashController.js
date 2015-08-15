/**
 * Created by hengyunabc on 15-8-15.
 */

'use strict';

angular.module('xdiamondApp').controller("CrashController",
    ['$scope', 'token', 'CrashService', function ($scope, token, CrashService) {
        $scope.token = token;

        $scope.connect = function () {
            // Create web socket url
            var path = window.location.pathname;
            var ctx = path.substring(0, path.indexOf('/', 1));
            var protocol;
            if (window.location.protocol == 'http:') {
                protocol = 'ws';
            } else {
                protocol = 'wss';
            }

            var url = protocol + '://' + window.location.host + ctx + '/crash?token=' + token;
            var crash = new CRaSH($('#crash_console'));
            crash.connect(url);
        }

    }]);