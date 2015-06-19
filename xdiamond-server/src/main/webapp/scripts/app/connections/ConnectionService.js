/**
 * Created by hengyunabc on 15-6-5.
 */

'use strict';

angular.module('xdiamondApp')
    .factory('ConnectionService', ['$http', '$log', function ($http, $log) {
        var service = {};

        service.list = function () {
            return $http.get('api/connections').then(function (response) {
                console.log('connections:' + response.data);

                return response.data.result.connections;
            })
        };

        return service;
    }])