/**
 * Created by hengyunabc on 15-5-15.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('projects', {
                parent: 'main',
                abstract: true,
                url: '/projects',
                templateUrl: 'scripts/app/projects/projects.html',

                resolve: {
                    projects: ['ProjectService', function (ProjectService) {
                        return ProjectService.list();
                    }],
                    allGroups: ['GroupService', function (GroupService) {
                        return GroupService.list();
                    }]
                },
                controller: 'ProjectController'
            })
            .state('projects.list', {
                parent: 'projects',
                url: '',
                templateUrl: 'scripts/app/projects/projects.list.html'
            })

    });