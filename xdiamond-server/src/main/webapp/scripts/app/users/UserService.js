/**
 * Created by hengyunabc on 15-5-12.
 */
'use strict';

angular.module('xdiamondApp')
    .factory('UserService', ['$http', '$log', function ($http, $log) {
        var service = {
            isLoggedIn: false,
            session: function () {
                return $http.get('api/session')
                    .then(function (response) {
                        if (response.data.success) {
                            service.isLoggedIn = true;
                        } else {
                            service.isLoggedIn = false;
                        }
                        return response;
                    });
            },
            login: function (user) {
                return $http.post('api/login', user)
                    .then(function (response) {
                        $log.log(response);
                        service.isLoggedIn = true;
                        return response;
                    });
            },
            logout: function () {
                return $http.post('api/logout')
                    .then(function (response) {
                        service.isLoggedIn = false;
                        return response;
                    })
            },

            list: function () {
                return $http.get('api/users').then(function (response) {
                    console.log('users:' + response.data);

                    if (response.data.success) {
                        console.log(response.data.result.users);
                        return response.data.result.users;
                    }
                })
            },

            create: function (user) {
                return $http.post('api/users', user).then(function (response) {
                    if (response.data.success) {
                        $log.info('create user success');
                    }
                })
            },

            delete: function (id) {
                return $http.delete('api/users/' + id).then(function (response) {
                    if (response.data.success) {
                        $log.info('delete user success, id:' + id);
                    }
                })
            },

            patch: function (user) {
                return $http.patch('api/users/', user).then(function (response) {
                    if (response.data.success) {
                        $log.info('patch user success');
                    }
                })
            }

        }
        return service;
    }])