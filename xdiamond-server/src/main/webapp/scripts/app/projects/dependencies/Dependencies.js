/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('projects.dependencies', {
                parent: 'projects',
                abstract: true,
                url: '/:projectId/dependencies',
                templateUrl: 'scripts/app/projects/dependencies/dependencies.html',
                resolve: {
                    project: ['ProjectService', '$stateParams', function (ProjectService, $stateParams) {
                        return ProjectService.get($stateParams.projectId);
                    }],
                    dependencies: ['DependencyService', '$stateParams', function (DependencyService, $stateParams) {
                        console.log('resolve!!!');
                        return DependencyService.list($stateParams.projectId);
                    }],
                    projects: ['ProjectService', function (ProjectService) {
                        console.log('resolve!!!');
                        return ProjectService.list();
                    }]
                },
                controller: 'DependencyController'
            })
            .state('projects.dependencies.list', {
                parent: 'projects.dependencies',
                url: '',
                templateUrl: 'scripts/app/projects/dependencies/dependencies.list.html'
            })
    });