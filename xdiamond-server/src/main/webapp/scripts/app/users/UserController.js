/**
 * Created by hengyunabc on 15-5-21.
 */

'use strict';

angular.module('xdiamondApp').controller("UserController", ['$scope', '$state', 'users', 'UserService', '$modal',
    function ($scope, $state, users, UserService, $modal) {
        console.log('UserContoller....');
        //TODO user要排好序
        $scope.users = users;

        $scope.user = {provider: 'standard'};

        $scope.create = function () {
            UserService.create($scope.user).then(function(){
                $state.reload();
            });
            $scope.user = {provider: 'standard'};
        }

        $scope.delete = function (userId) {
            UserService.delete(userId).then(function(){
                $state.reload();
            });
        }

        $scope.popNewUserModal = function (size) {
            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'scripts/app/users/users.new.html',
                controller: 'UserNewController',
                size: size
            });
        };

        $scope.popUpdateUserModal = function (index, size) {
            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'scripts/app/users/users.update.html',
                controller: 'UserUpdateController',
                size: size,
                resolve: {
                    user: function () {
                        return $scope.users[index];
                    }
                }
            });
        }

    }]);


angular.module('xdiamondApp').controller("UserUpdateController",
    ['$scope', '$state', '$modal', '$modalInstance', 'UserService', 'user',
        function ($scope, $state, $modal, $modalInstance, UserService, user) {
            $scope.user = user;

            $scope.update = function () {
                UserService.patch(user).then(function () {
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