/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp').controller("RoleController",
    ['$scope', 'RoleService', 'roles', '$modal', '$state',
        function ($scope, RoleService, roles, $modal, $state) {
            console.log('RoleController....')
            $scope.roles = roles;
            $scope.role = {};

            $scope.create = function () {
                RoleService.create($scope.role).then(function () {
                    $state.reload();
                });
                $scope.role = {};
            }


            $scope.delete = function (roleId) {
                RoleService.delete(roleId).then(function () {
                    $state.reload();
                });
            }

        }]);

angular.module('xdiamondApp').controller("RolePermissionController",
    ['$scope', '$state', '$stateParams', 'RoleService', 'allPermissions', 'permissions', 'roleId',
        function ($scope, $state, $stateParams, RoleService, allPermissions, permissions, roleId) {
            $scope.selected = {};

            $scope.roleId = roleId;

            $scope.allPermissions = allPermissions;
            $scope.permissions = permissions;

            $scope.addPermission = function (permissionId) {
                RoleService.addPermission(roleId, permissionId).then(function () {
                    $state.reload();
                })
            }
            $scope.deletePermission = function (permissionId) {
                RoleService.deletePermission(roleId, permissionId).then(function () {
                    $state.reload();
                })
            }
        }]);


angular.module('xdiamondApp').controller("RoleUserController",
    ['$scope', '$state', '$stateParams', 'RoleService', 'allUsers', 'users', 'roleId',
        function ($scope, $state, $stateParams, RoleService, allUsers, users, roleId) {
            $scope.selected = {};

            $scope.roleId = roleId;

            $scope.allUsers = allUsers;
            $scope.users = users;

            $scope.addUser = function (userId, access) {
                RoleService.addUser(roleId, userId, access).then(function () {
                    $state.reload();
                })
            }
            $scope.deleteUser = function (userId) {
                RoleService.deleteUser(roleId, userId).then(function () {
                    $state.reload();
                })
            }
        }]);

angular.module('xdiamondApp').controller("RoleGroupController",
    ['$scope', '$state', '$stateParams', 'RoleService', 'allGroups', 'groups', 'roleId',
        function ($scope, $state, $stateParams, RoleService, allGroups, groups, roleId) {
            $scope.selected = {};

            $scope.roleId = roleId;

            $scope.allGroups = allGroups;
            $scope.groups = groups;

            $scope.addGroup = function (groupId, access) {
                RoleService.addGroup(roleId, groupId, access).then(function () {
                    $state.reload();
                })
            }
            $scope.deleteGroup = function (groupId) {
                RoleService.deleteGroup(roleId, groupId).then(function () {
                    $state.reload();
                })
            }
        }]);