/**
 * Created by hengyunabc on 15-5-20.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('projects.profiles.configs', {
                parent: 'projects.profiles',
                abstract: true,
                url: '/:profileId/configs',
                templateUrl: 'scripts/app/projects/profiles/configs/configs.html',
                resolve: {
                    project: ['ProjectService', '$stateParams', function (ProjectService, $stateParams) {
                        return ProjectService.get($stateParams.projectId);
                    }],
                    profile: ['ProfileService', '$stateParams', function (ProfileService, $stateParams) {
                        return ProfileService.get($stateParams.profileId);
                    }],
                    configs: ['ConfigService', '$stateParams', function (ConfigService, $stateParams) {
                        return ConfigService.list($stateParams.projectId, $stateParams.profileId);
                    }],
                    resolvedConfigs: ['ConfigService', '$stateParams', function (ConfigService, $stateParams) {
                        return ConfigService.listResolvedConfigs($stateParams.profileId);
                    }]
                },
                controller: 'ConfigController'
            })
            .state('projects.profiles.configs.list', {
                parent: 'projects.profiles.configs',
                url: '',
                templateUrl: 'scripts/app/projects/profiles/configs/configs.list.html'
            })
    });