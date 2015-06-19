/**
 * Created by hengyunabc on 15-6-5.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('connections', {
                parent: 'main',
                url: '/connections',
                templateUrl: 'scripts/app/connections/connections.html',

                resolve: {
                    connections: ['ConnectionService', function (ConnectionService) {
                        return ConnectionService.list();
                    }]
                },
                controller: 'ConnectionController'
            })

    });