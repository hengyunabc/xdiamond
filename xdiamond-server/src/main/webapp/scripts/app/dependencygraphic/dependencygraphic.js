/**
 * Created by hengyunabc on 15-8-6.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('dependencygraphic', {
                parent: 'main',
                url: '/dependencygraphic',
                templateUrl: 'scripts/app/dependencygraphic/dependencygraphic.html',

                resolve: {
                    projects: ['ProjectService', function (ProjectService) {
                        return ProjectService.list();
                    }],
                    dependencies: ['DependencyService', function (DependencyService) {
                        return DependencyService.all();
                    }]
                },
                controller: 'DependencyGraphicController'
            })

    });