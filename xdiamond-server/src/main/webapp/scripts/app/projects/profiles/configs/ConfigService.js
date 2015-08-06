/**
 * Created by hengyunabc on 15-5-20.
 */

'use strict';

angular.module('xdiamondApp')
    .factory('ConfigService', ['$http', '$log', function ($http, $log) {
        var service = {};

        service.all = function () {
            return $http.get('api/configs/all').then(function (response) {
                console.log('allconfigs:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.configs);
                    return response.data.result.configs;
                }
            })
        }

        service.list = function (projectId, profileId) {
            return $http.get('api/projects/' + projectId + '/profiles/' + profileId + '/configs').then(function (response) {
                console.log('configs:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.configs);
                    return response.data.result.configs;
                }
            })
        };

        service.create = function (config) {
            return $http.post('api/configs', config).then(function (response) {
                if (response.data.success) {
                    $log.info('create config success');
                }
            })
        };

        service.batch = function (batchConfigs) {
            return $http.post('api/configs/batch', batchConfigs).then(function (response) {
                if (response.data.success) {
                    $log.info('batch configs success');
                }
            })
        };

        service.delete = function (id) {
            return $http.delete('api/configs/' + id).then(function (response) {
                if (response.data.success) {
                    $log.info('delete config success, id:' + id);
                }
            })
        }

        service.patch = function (config) {
            return $http.patch('api/configs', config).then(function (response) {
                if (response.data.success) {
                    $log.info('patch config success, id:' + config.id);
                }
            })
        }

        service.listResolvedConfigs = function (profileId) {
            return $http.get('api/configs/resolvedConfigs/' + profileId).then(function (response) {
                console.log('ResolvedConfigs:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.resolvedConfigs);
                    return response.data.result.resolvedConfigs;
                }
            })
        }
        return service;
    }])