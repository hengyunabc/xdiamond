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
            GroupService.delete(groupId).then(function(){
                $state.reload();
            });
        }

        //$scope.popGroupUserListModal = function(group){
        //    var modalInstance = $modal.open({
        //        animation: $scope.animationsEnabled,
        //        templateUrl: 'scripts/app/groups/groups.users.list.html',
        //        controller: 'GroupUserController',
        //        size: size,
        //        resolve:{
        //            users: function(){
        //                return GroupService.getUsers(group.id);
        //            },
        //            group: group
        //        }
        //    });
        //};

        //$scope.popNewGroupModal = function (size) {
        //    var modalInstance = $modal.open({
        //        animation: $scope.animationsEnabled,
        //        templateUrl: 'scripts/app/groups/groups.new.html',
        //        controller: 'GroupNewController',
        //        size: size
        //    });
        //};
        //
        //$scope.popUpdateGroupModal = function (index, size) {
        //    var modalInstance = $modal.open({
        //        animation: $scope.animationsEnabled,
        //        templateUrl: 'scripts/app/groups/groups.update.html',
        //        controller: 'GroupUpdateController',
        //        size: size,
        //        resolve: {
        //            group: function () {
        //                return $scope.groups[index];
        //            }
        //        }
        //    });
        //}

    }]);


angular.module('xdiamondApp').controller("GroupUserController",
    ['$scope', '$state', '$stateParams', 'GroupService', 'allUsers', 'users', 'groupId', 'AccessLevels',
        function ($scope, $state, $stateParams, GroupService, allUsers, users, groupId, AccessLevels) {
            $scope.selected = {};

            $scope.accessArray = [];

            for (var access in AccessLevels) {
                $scope.accessArray.push(access);
            }

            $scope.groupId = groupId;

            $scope.allUsers = allUsers;
            $scope.users = users;

            $scope.addUser = function (userId, access) {
                GroupService.addUser(groupId, userId, access).then(function () {
                    $state.reload();
                })
            }
            $scope.deleteUser = function (userId) {
                GroupService.deleteUser(groupId, userId).then(function () {
                    $state.reload();
                })
            }
        }]);