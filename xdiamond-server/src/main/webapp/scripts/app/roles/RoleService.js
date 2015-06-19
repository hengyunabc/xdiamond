/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp')
    .factory('RoleService', ['$http', '$log', function ($http, $log) {
        var service = {};

        service.list = function () {
            return $http.get('api/roles').then(function (response) {
                console.log('roles:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.roles);
                    return response.data.result.roles;
                }
            })
        };

        service.create = function (role) {
            return $http.post('api/roles', role).then(function (response) {
                if (response.data.success) {
                    $log.info('create role success');
                }
            })
        };

        service.delete = function (id) {
            return $http.delete('api/roles/' + id).then(function (response) {
                if (response.data.success) {
                    $log.info('delete role success, id:' + id);
                }
            })
        }

        service.patch = function (role) {
            return $http.patch('api/roles/' + role.id, role).then(function (response) {
                if (response.data.success) {
                    $log.info('patch role success, id:' + role.id);
                }
            })
        }

        //---------------Permission操作相关--------------------
        service.getPermissions = function (roleId) {
            return $http.get('api/roles/' + roleId + '/permissions').then(function (response) {
                console.log('PermissionRoles:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.permissions);
                    return response.data.result.permissions;
                }
            })
        };

        service.addPermission = function (roleId, permissionId) {
            return $http.post('api/roles/' + roleId + '/permissions/' + permissionId).then(function (response) {
                if (response.data.success) {
                    $log.info('add permission into role success');
                }
            })
        };

        service.deletePermission = function (roleId, permissionId) {
            return $http.delete('api/roles/' + roleId + '/permissions/' + permissionId).then(function (response) {
                if (response.data.success) {
                    $log.info('delete role success, roleId:' + roleId + ', permissionId:' + permissionId);
                }
            })
        };

        //---------------用户相关的------------
        service.getUsers = function (roleId) {
            return $http.get('api/roles/' + roleId + '/users').then(function (response) {
                console.log('UserRoles:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.users);
                    return response.data.result.users;
                }
            })
        };

        service.addUser = function (roleId, userId) {
            return $http.post('api/roles/' + roleId + '/users/' + userId).then(function (response) {
                if (response.data.success) {
                    $log.info('add user into role success');
                }
            })
        };

        service.deleteUser = function (roleId, userId) {
            return $http.delete('api/roles/' + roleId + '/users/' + userId).then(function (response) {
                if (response.data.success) {
                    $log.info('delete role success, roleId:' + roleId + ', userId:' + userId);
                }
            })
        };

        //---------------组相关的----------------------
        service.getGroups = function (roleId) {
            return $http.get('api/roles/' + roleId + '/groups').then(function (response) {
                console.log('GroupRoles:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.groups);
                    return response.data.result.groups;
                }
            })
        };

        service.addGroup = function (roleId, groupId) {
            return $http.post('api/roles/' + roleId + '/groups/' + groupId).then(function (response) {
                if (response.data.success) {
                    $log.info('add group into role success');
                }
            })
        };

        service.deleteGroup = function (roleId, groupId) {
            return $http.delete('api/roles/' + roleId + '/groups/' + groupId).then(function (response) {
                if (response.data.success) {
                    $log.info('delete role success, roleId:' + roleId + ', groupId:' + groupId);
                }
            })
        };

        return service;
    }])