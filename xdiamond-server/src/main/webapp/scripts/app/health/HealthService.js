/**
 * Created by hengyunabc on 15-7-22.
 */


'use strict';

angular.module('xdiamondApp')
    .factory('HealthService', ['$http', '$log', function ($http, $log) {
        var service = {};

        service.get = function () {
            return $http.get('api/health').then(function (response) {
                console.log('healthInfo:' + response.data);

                return response.data.result;
            })
        };

        return service;
    }])