/**
 * Created by hengyunabc on 15-5-12.
 */
'use strict';

angular.module('xdiamondApp')
    .factory('GroupService', ['$http', '$log', '$state', function ($http, $log, $state) {
        var service = {

            get: function (groupId) {
                return $http.get('api/groups/' + groupId).then(function (response) {
                    console.log('group:' + response.data);

                    if (response.data.success) {
                        console.log(response.data.result.group);
                        return response.data.result.group;
                    }
                })
            },

            list: function () {
                return $http.get('api/groups').then(function (response) {
                    console.log('groups:' + response.data);

                    if (response.data.success) {
                        console.log(response.data.result.groups);
                        return response.data.result.groups;
                    }
                })
            },

            create: function (group) {
                return $http.post('api/groups', group).then(function (response) {
                    if (response.data.success) {
                        $log.info('service create group success');
                    }
                })
            },

            delete: function (id) {
                return $http.delete('api/groups/' + id).then(function (response) {
                    if (response.data.success) {
                        $log.info('delete group success, id:' + id);
                    }
                })
            },

            patch: function (group) {
                return $http.patch('api/groups/' + group.id, group).then(function (response) {
                    if (response.data.success) {
                        $log.info('patch group success, id:' + group.id);
                    }
                })
            },

            //---------------用户操作相关--------------------
            getUsers: function (groupId) {
                return $http.get('api/groups/' + groupId + '/users').then(function (response) {
                    console.log('UserGroups:' + response.data);

                    if (response.data.success) {
                        console.log(response.data.result.users);
                        return response.data.result.users;
                    }
                })
            },

            addUser: function (groupId, userId, access) {
                return $http.post('api/groups/' + groupId + '/users/' + userId, {access: access}).then(function (response) {
                    if (response.data.success) {
                        $log.info('add user into group success');
                    }
                })
            },

            deleteUser: function (groupId, userId) {
                return $http.delete('api/groups/' + groupId + '/users/' + userId).then(function (response) {
                    if (response.data.success) {
                        $log.info('delete group success, groupId:' + groupId + ', userId:' + userId);
                    }
                })
            },

            changeUserAccess: function (groupId, userId, access) {
                return $http.patch('api/groups/' + groupId + '/users', {
                    groupId: groupId,
                    userId: userId,
                    access: access
                }).then(function (response) {
                    if (response.data.success) {
                        $log.info('change user access success, groupId:' + groupId + ', userId:' + userId);
                    }
                })
            }

        }
        return service;
    }])