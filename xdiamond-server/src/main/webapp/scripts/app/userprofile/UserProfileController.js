/**
 * Created by hengyunabc on 15-6-3.
 */

'use strict';

angular.module('xdiamondApp').controller("UserProfileController",
    ['$scope', '$modal', '$state', 'authenticateInfo',
        function ($scope, $modal, $state, authenticateInfo) {
            console.log('UserProfileController....')
            console.log('authenticateInfo:' + authenticateInfo)
            $scope.authenticateInfo = authenticateInfo;
        }]);
