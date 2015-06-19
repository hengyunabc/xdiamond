/**
 * Created by hengyunabc on 15-6-5.
 */

'use strict';

angular.module('xdiamondApp')
    .factory('ThreadInfoService', ['$http', '$log', function ($http, $log) {
        var service = {};

        service.list = function () {
            return $http.get('api/threadinfo').then(function (response) {
                console.log('threadInfos:' + response.data);

                return response.data.result.threadInfos;
            })
        };

        return service;
    }])