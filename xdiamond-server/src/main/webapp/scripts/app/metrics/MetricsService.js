/**
 * Created by hengyunabc on 15-6-5.
 */

'use strict';

angular.module('xdiamondApp')
    .factory('MetricsService', ['$http', '$log', function ($http, $log) {
        var service = {};

        service.list = function () {
            return $http.get('api/metrics').then(function (response) {
                console.log('metrics:' + response.data);

                return response.data;
            })
        };

        return service;
    }])