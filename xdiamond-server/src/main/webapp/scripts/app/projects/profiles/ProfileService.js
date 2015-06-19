/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp')
    .factory('ProfileService', ['$http', '$log', function ($http, $log) {
        var service = {};

        service.get = function (profileId) {
            return $http.get('api/profiles/' + profileId).then(function (response) {
                console.log('profile:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.profile);
                    return response.data.result.profile;
                }
            })
        };

        service.list = function (projectId) {
            return $http.get('api/projects/' + projectId + '/profiles').then(function (response) {
                console.log('profiles:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.profiles);
                    return response.data.result.profiles;
                }
            })
        };

        service.create = function (profile) {
            return $http.post('api/profiles', profile).then(function (response) {
                if (response.data.success) {
                    $log.info('create profile success');
                }
            })
        };

        service.delete = function (id) {
            return $http.delete('api/profiles/' + id).then(function (response) {
                if (response.data.success) {
                    $log.info('delete profile success, id:' + id);
                }
            })
        }

        service.patch = function (profile) {
            return $http.patch('api/profiles', profile).then(function (response) {
                if (response.data.success) {
                    $log.info('patch profile success, id:' + profile.id);
                }
            })
        }

        return service;
    }])