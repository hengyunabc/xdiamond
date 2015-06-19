/**
 * Created by hengyunabc on 15-6-5.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('metrics', {
                parent: 'main',
                url: '/metrics',
                templateUrl: 'scripts/app/metrics/metrics.html',

                resolve: {
                    metrics: ['MetricsService', function (MetricsService) {
                        return MetricsService.list();
                    }]
                },
                controller: 'MetricsController'
            })

    });