/**
 * Created by hengyunabc on 15-6-3.
 */

'use strict';

angular.module('xdiamondApp').controller("MainController",
    ['$scope', '$state', '$log', 'UserService',
        function ($scope, $state, $log, UserService) {
            console.log('MainController....')

            UserService.session();

            $scope.isLoggedIn = function () {
                //return true;
                return UserService.isLoggedIn;
            }
        }]);