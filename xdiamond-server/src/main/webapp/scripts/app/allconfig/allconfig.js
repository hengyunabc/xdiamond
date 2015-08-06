/**
 * Created by hengyunabc on 15-8-6.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('allconfig', {
                parent: 'main',
                url: '/allconfig',
                templateUrl: 'scripts/app/allconfig/allconfig.html',

                resolve: {
                    configs: ['ConfigService', function (ConfigService) {
                        return ConfigService.all();
                    }]
                },
                controller: 'AllConfigController'
            })

    });