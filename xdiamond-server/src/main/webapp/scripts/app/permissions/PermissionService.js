/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp')
    .factory('PermissionService', ['$http', '$log', function ($http, $log) {
        var service = {};

        service.list = function () {
            return $http.get('api/permissions').then(function (response) {
                console.log('permissions:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.permissions);
                    return response.data.result.permissions;
                }
            })
        };

        service.create = function (permission) {
            return $http.post('api/permissions', permission).then(function (response) {
                if (response.data.success) {
                    $log.info('create permission success');
                }
            })
        };

        service.delete = function (id) {
            return $http.delete('api/permissions/' + id).then(function (response) {
                if (response.data.success) {
                    $log.info('delete permission success, id:' + id);
                }
            })
        }

        service.patch = function (permission) {
            return $http.patch('api/permissions/' + permission.id, permission).then(function (response) {
                if (response.data.success) {
                    $log.info('patch permission success, id:' + permission.id);
                }
            })
        }

        return service;
    }])