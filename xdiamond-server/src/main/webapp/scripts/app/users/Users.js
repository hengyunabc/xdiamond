/**
 * Created by hengyunabc on 15-5-21.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('users', {
                parent: 'main',
                abstract: true,
                url: '/users',
                templateUrl: 'scripts/app/users/users.html',

                resolve: {
                    users: ['UserService', function (UserService) {
                        return UserService.list();
                    }]
                },
                controller: 'UserController'
            })
            .state('users.list', {
                parent: 'users',
                url: '',
                templateUrl: 'scripts/app/users/users.list.html'
            })

    });