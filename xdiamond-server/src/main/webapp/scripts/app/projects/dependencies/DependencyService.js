/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp')
    .factory('DependencyService', ['$http', '$log', function ($http, $log) {
        var service = {};

        service.all = function () {
            return $http.get('api/dependencies/all').then(function (response) {
                console.log('dependencies:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.dependencies);
                    return response.data.result.dependencies;
                }
            })
        };

        service.list = function (projectId) {
            return $http.get('api/projects/' + projectId + '/dependencies').then(function (response) {
                console.log('dependencies:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.dependencies);
                    return response.data.result.dependencies;
                }
            })
        };

        service.create = function (dependency) {
            return $http.post('api/dependencies', dependency).then(function (response) {
                if (response.data.success) {
                    $log.info('create dependency success');
                }
            })
        };

        service.delete = function (id) {
            return $http.delete('api/dependencies/' + id).then(function (response) {
                if (response.data.success) {
                    $log.info('delete dependency success, id:' + id);
                }
            })
        }

        service.patch = function (dependency) {
            return $http.patch('api/dependencies/' + dependency.id, dependency).then(function (response) {
                if (response.data.success) {
                    $log.info('patch dependency success, id:' + dependency.id);
                }
            })
        }

        return service;
    }])