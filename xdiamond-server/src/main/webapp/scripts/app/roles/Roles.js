/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('roles', {
                parent: 'main',
                abstract: true,
                url: '/roles',
                templateUrl: 'scripts/app/roles/roles.html',
                resolve: {
                    roles: ['RoleService', '$stateParams', function (RoleService, $stateParams) {
                        return RoleService.list();
                    }]
                },
                controller: 'RoleController'
            })
            .state('roles.list', {
                parent: 'roles',
                url: '',
                templateUrl: 'scripts/app/roles/roles.list.html'
            })
            .state('roles.permissions', {
                parent: 'roles',
                url: '/:roleId/permissions',
                templateUrl: 'scripts/app/roles/roles.permissions.list.html',
                resolve: {
                    roleId: ['$stateParams', function ($stateParams) {
                        return $stateParams.roleId;
                    }],
                    permissions: ['RoleService', '$stateParams', function (RoleService, $stateParams) {
                        return RoleService.getPermissions($stateParams.roleId);
                    }],
                    allPermissions: ['PermissionService', function (PermissionService) {
                        return PermissionService.list();
                    }]
                },
                controller: 'RolePermissionController'
            })
            .state('roles.users', {
                parent: 'roles',
                url: '/:roleId/users',
                templateUrl: 'scripts/app/roles/roles.users.list.html',
                resolve: {
                    roleId: ['$stateParams', function ($stateParams) {
                        return $stateParams.roleId;
                    }],
                    users: ['RoleService', '$stateParams', function (RoleService, $stateParams) {
                        return RoleService.getUsers($stateParams.roleId);
                    }],
                    allUsers: ['UserService', function (UserService) {
                        return UserService.list();
                    }]
                },
                controller: 'RoleUserController'
            })
            .state('roles.groups', {
                parent: 'roles',
                url: '/:roleId/groups',
                templateUrl: 'scripts/app/roles/roles.groups.list.html',
                resolve: {
                    roleId: ['$stateParams', function ($stateParams) {
                        return $stateParams.roleId;
                    }],
                    groups: ['RoleService', '$stateParams', function (RoleService, $stateParams) {
                        return RoleService.getGroups($stateParams.roleId);
                    }],
                    allGroups: ['GroupService', function (GroupService) {
                        return GroupService.list();
                    }]
                },
                controller: 'RoleGroupController'
            })
    });