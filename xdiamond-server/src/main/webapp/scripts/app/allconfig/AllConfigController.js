/**
 * Created by hengyunabc on 15-8-6.
 */


'use strict';

angular.module('xdiamondApp').controller("AllConfigController",
    ['$scope', 'configs', function ($scope, configs) {
        $scope.configs = configs;

    }]);