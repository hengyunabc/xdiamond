/**
 * Created by hengyunabc on 15-8-13.
 */


'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('crash', {
                parent: 'main',
                url: '/crash',
                templateUrl: 'scripts/app/crash/crash.html',
                resolve: {
                    token: ['CrashService', function (CrashService) {
                        return CrashService.token();
                    }]
                },
                controller: 'CrashController'
            })

    });