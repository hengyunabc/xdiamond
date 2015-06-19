/**
 * Created by hengyunabc on 15-6-2.
 */

'use strict';

angular.module('xdiamondApp')
    .factory('AuthService', ['$http', '$log', function ($http, $log) {
        var service = {
            authenticateInfo: function () {
                return $http.get('api/authenticate')
                    .then(function (response) {
                        if (response.data.success) {
                            console.log(response.data.result);
                            return response.data.result.info;
                        }
                    })
            }
        }
        return service;
    }])