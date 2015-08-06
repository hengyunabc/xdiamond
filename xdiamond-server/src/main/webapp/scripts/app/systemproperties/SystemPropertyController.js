/**
 * Created by hengyunabc on 15-8-6.
 */

'use strict';

angular.module('xdiamondApp').controller("SystemPropertyController",
    ['$scope', 'properties',
        function ($scope, properties) {
            console.log('properties:')
            console.log(properties)
            $scope.properties = properties;
        }]);