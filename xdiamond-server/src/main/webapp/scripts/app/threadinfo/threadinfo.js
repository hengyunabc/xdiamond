/**
 * Created by hengyunabc on 15-6-5.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('threadinfo', {
                parent: 'main',
                url: '/threadinfo',
                templateUrl: 'scripts/app/threadinfo/threadinfo.html',

                resolve: {
                    threadInfos: ['ThreadInfoService', function (ThreadInfoService) {
                        return ThreadInfoService.list();
                    }]
                },
                controller: 'ThreadInfoController'
            })

    });