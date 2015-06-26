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
    ['$scope', '$state', '$stateParams', '$modal', 'GroupService', 'allUsers', 'users', 'group', 'AccessLevels',
        function ($scope, $state, $stateParams, $modal, GroupService, allUsers, users, group, AccessLevels) {
            $scope.selected = {};

            var accessArray = [];
            for (var access in AccessLevels) {
                accessArray.push(access);
            }
            $scope.accessArray = accessArray;

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

            $scope.popUpdateGroupUserModal = function (group, user, size) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'scripts/app/groups/groups.users.update.html',
                    controller: 'GroupUserUpdateController',
                    size: size,
                    resolve: {
                        group: function () {
                            return angular.copy(group);
                        },
                        user: function () {
                            return angular.copy(user);
                        },
                        accessArray: function () {
                            return angular.copy(accessArray);
                        }
                    }
                });
            }
        }]);

angular.module('xdiamondApp').controller("GroupUserUpdateController",
    ['$scope', '$state', '$modal', '$modalInstance', 'GroupService', 'group', 'user', 'accessArray',
        function ($scope, $state, $modal, $modalInstance, GroupService, group, user, accessArray) {
            $scope.group = group;
            $scope.user = user;
            $scope.accessArray = accessArray;

            $scope.update = function () {
                GroupService.changeUserAccess(group.id, user.id, user.access).then(function () {
                    $state.reload();
                })
                $modalInstance.close();
            }

            $scope.ok = function () {
                $modalInstance.close();
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };

        }]);