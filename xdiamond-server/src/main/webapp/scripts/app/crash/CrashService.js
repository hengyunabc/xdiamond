/**
 * Created by hengyunabc on 15-8-15.
 */
'use strict';

angular.module('xdiamondApp')
    .factory('CrashService', ['$http', '$log', function ($http, $log) {
        var service = {};

        service.token = function () {
            return $http.get('api/crash/token').then(function (response) {
                return response.data.result.token;
            })
        };

        return service;
    }])