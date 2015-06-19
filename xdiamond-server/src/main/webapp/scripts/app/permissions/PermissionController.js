/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp').controller("PermissionController",
    ['$scope', 'PermissionService', 'permissions', '$modal', '$state',
        function ($scope, PermissionService, permissions, $modal, $state) {
            console.log('PermissionController....')
            $scope.permissions = permissions;
            $scope.permission = {};

            $scope.create = function () {
                PermissionService.create($scope.permission).then(function () {
                    $state.reload();
                });
                $scope.permission = {};
            }


            $scope.delete = function (permissionId) {
                PermissionService.delete(permissionId).then(function () {
                    $state.reload();
                });
            }

        }]);
