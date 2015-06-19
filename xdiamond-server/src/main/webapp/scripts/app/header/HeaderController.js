/**
 * Created by hengyunabc on 15-6-2.
 */

'use strict';

angular.module('xdiamondApp').controller("HeaderController",
    ['$scope', '$state', '$log', 'authenticateInfo', 'AuthService', 'UserService',
        function ($scope, $state, $log, authenticateInfo, AuthService, UserService) {
            console.log('HeaderController....')

            $scope.authenticateInfo = authenticateInfo;

            AuthService.authenticateInfo().then(function (data) {
                $scope.authenticateInfo = data;
            })

            $scope.isLoggedIn = function () {
                //return true;
                return UserService.isLoggedIn;
            }

            $scope.logout = function () {
                UserService.logout().then(function (success) {
                    $scope.authenticateInfo = {};
                    console.log(success);
                    $log.info('logout:' + success);
                    $state.go('main');
                })
            }

        }]);
