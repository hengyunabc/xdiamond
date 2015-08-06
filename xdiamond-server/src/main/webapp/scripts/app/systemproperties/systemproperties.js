/**
 * Created by hengyunabc on 15-8-6.
 */


'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('systemproperties', {
                parent: 'main',
                url: '/systemproperties',
                templateUrl: 'scripts/app/systemproperties/systemproperties.html',
                resolve: {
                    properties: ['SystemPropertyService', function (SystemPropertyService) {
                        return SystemPropertyService.list();
                    }]
                },
                controller: 'SystemPropertyController'
            })

    });