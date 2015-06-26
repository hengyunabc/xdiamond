/**
 * Created by hengyunabc on 15-5-21.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('groups', {
                parent: 'main',
                abstract: true,
                url: '/groups',
                templateUrl: 'scripts/app/groups/groups.html',

                resolve: {
                    groups: ['GroupService', function (GroupService) {
                        return GroupService.list();
                    }]
                },
                controller: 'GroupController'
            })
            .state('groups.list', {
                parent: 'groups',
                url: '',
                templateUrl: 'scripts/app/groups/groups.list.html'
            })
            .state('groups.users', {
                parent: 'groups',
                url: '/:groupId/users',
                templateUrl: 'scripts/app/groups/groups.users.list.html',
                resolve: {
                    groupId: ['$stateParams', function ($stateParams) {
                        return $stateParams.groupId;
                    }],
                    group: ['GroupService', '$stateParams', function (GroupService, $stateParams) {
                        return GroupService.get($stateParams.groupId);
                    }],
                    users: ['GroupService', '$stateParams', function (GroupService, $stateParams) {
                        return GroupService.getUsers($stateParams.groupId);
                    }],
                    allUsers: ['UserService', function (UserService) {
                        return UserService.list();
                    }]
                },
                controller: 'GroupUserController'
            })

    });