/**
 * Created by hengyunabc on 15-6-10.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('druid', {
                parent: 'main',
                url: '/druid',
                templateUrl: 'scripts/app/druid/druid.html'
            })

    });