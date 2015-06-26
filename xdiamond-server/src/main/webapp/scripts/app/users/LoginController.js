/**
 * Created by hengyunabc on 15-5-12.
 */

'use strict';

angular.module('xdiamondApp')
    .controller('LoginController', ['UserService', '$scope', '$location', '$log', '$state',
        function (UserService, $scope, $location, $log, $state) {
            //var authenticateInfo = {};
            //$scope.authenticateInfo = authenticateInfo;
            $scope.user = {name: '', password: '', provider: 'ldap'};
            $scope.userService = UserService;
            UserService.session();

            $scope.login = function () {
                UserService.login($scope.user).then(function (success) {
                    console.log(success);
                    $log.info('login:' + success);
                    $state.go('main')
                }, function (error) {
                    $scope.errorMessage = error.data.msg;
                })
                user.password = "";
            };
            $scope.logout = function () {
                UserService.logout().then(function (success) {
                    console.log(success);
                    $log.info('logout:' + success);
                    $state.go('main');
                })
            }

            $scope.isLoggedIn = function () {
                //return true;
                return UserService.isLoggedIn;
            }

            //UserService.authenticateInfo().then(function (data) {
            //    authenticateInfo.authc = data.authc;
            //})

        }])