/**
 * Created by hengyunabc on 15-8-6.
 */

'use strict';


angular.module('xdiamondApp')
    .factory('SystemPropertyService', ['$http', '$log', function ($http, $log) {
        var service = {};

        service.list = function () {
            return $http.get('api/systemproperties').then(function (response) {
                console.log('systemproperties:' + response.data);

                return response.data.result.properties;
            })
        };

        return service;
    }])