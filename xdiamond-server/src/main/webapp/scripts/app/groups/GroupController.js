/**
 * Created by hengyunabc on 15-5-21.
 */

'use strict';

angular.module('xdiamondApp').controller("GroupController", ['$scope', 'groups', 'GroupService', '$modal', '$state',
    function ($scope, groups, GroupService, $modal, $state) {
        console.log('GroupContoller....');
        //TODO group要排好序
        $scope.groups = groups;

        $scope.group = {};

        $scope.create = function () {
            GroupService.create($scope.group).then(function () {
                $state.reload();
            })
            $scope.group = {};
        }

        $scope.delete = function (groupId) {
            GroupService.delete(groupId).then(function () {
                $state.reload();
            });
        }
    }]);


angular.module('xdiamondApp').controller("GroupUserController",
    ['$scope', '$state', '$stateParams', 'GroupService', 'allUsers', 'users', 'group', 'AccessLevels',
        function ($scope, $state, $stateParams, GroupService, allUsers, users, group, AccessLevels) {
            $scope.selected = {};

            $scope.accessArray = [];

            for (var access in AccessLevels) {
                $scope.accessArray.push(access);
            }

            $scope.group = group;

            $scope.allUsers = allUsers;
            $scope.users = users;

            $scope.addUser = function (userId, access) {
                GroupService.addUser(group.id, userId, access).then(function () {
                    $state.reload();
                })
            }
            $scope.deleteUser = function (userId) {
                GroupService.deleteUser(group.id, userId).then(function () {
                    $state.reload();
                })
            }
        }]);