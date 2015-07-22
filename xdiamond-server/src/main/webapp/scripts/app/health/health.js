/**
 * Created by hengyunabc on 15-7-22.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('health', {
                parent: 'main',
                url: '/health',
                templateUrl: 'scripts/app/health/health.html',

                resolve: {
                    healthInfo: ['HealthService', function (HealthService) {
                        return HealthService.get();
                    }]
                },
                controller: 'HealthController'
            })

    });