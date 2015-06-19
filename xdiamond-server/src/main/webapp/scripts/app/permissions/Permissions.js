/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('permissions', {
                parent: 'main',
                abstract: true,
                url: '/permissions',
                templateUrl: 'scripts/app/permissions/permissions.html',
                resolve: {
                    permissions: ['PermissionService', '$stateParams', function (PermissionService, $stateParams) {
                        return PermissionService.list();
                    }]
                },
                controller: 'PermissionController'
            })
            .state('permissions.list', {
                parent: 'permissions',
                url: '',
                templateUrl: 'scripts/app/permissions/permissions.list.html'
            })
    });