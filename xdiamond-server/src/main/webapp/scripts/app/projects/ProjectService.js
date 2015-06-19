/**
 * Created by hengyunabc on 15-5-14.
 */

'use strict';

angular.module('xdiamondApp')
    .factory('ProjectService', ['$http', '$log', function ($http, $log) {
        var service = {};

        service.get = function (id) {
            return $http.get('api/projects/' + id).then(function (response) {
                console.log('project:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.project);
                    return response.data.result.project;
                }
            })
        };

        service.list = function () {
            return $http.get('api/projects').then(function (response) {
                console.log('projects:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.projects);
                    return response.data.result.projects;
                }
            })
        };

        service.create = function (project) {
            return $http.post('api/projects', project).then(function (response) {
                if (response.data.success) {
                    $log.info('create project success');
                }
            })
        };

        service.delete = function (id) {
            return $http.delete('api/projects/' + id).then(function (response) {
                if (response.data.success) {
                    $log.info('delete project success, id:' + id);
                }
            })
        }

        service.patch = function (project) {
            return $http.patch('api/projects/' + project.id, project).then(function (response) {
                if (response.data.success) {
                    $log.info('patch project success, id:' + project.id);
                }
            })
        }

        return service;
    }])