/**
 * Created by hengyunabc on 15-6-7.
 */


'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('logger', {
                parent: 'main',
                url: '/logger',
                templateUrl: 'scripts/app/logger/logger.html',
                controller: 'LoggerController'
            })

    });