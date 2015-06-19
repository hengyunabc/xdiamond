/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('projects.profiles', {
                parent: 'projects',
                abstract: true,
                url: '/:projectId/profiles',
                templateUrl: 'scripts/app/projects/profiles/profiles.html',
                resolve: {
                    project: ['ProjectService', '$stateParams', function (ProjectService, $stateParams) {
                        return ProjectService.get($stateParams.projectId);
                    }],
                    profiles: ['ProfileService', '$stateParams', function (ProfileService, $stateParams) {
                        console.log('resolve!!!');
                        return ProfileService.list($stateParams.projectId);
                    }]
                },
                controller: 'ProfileController'
            })
            .state('projects.profiles.list', {
                parent: 'projects.profiles',
                url: '',
                templateUrl: 'scripts/app/projects/profiles/profiles.list.html'
            })
    });