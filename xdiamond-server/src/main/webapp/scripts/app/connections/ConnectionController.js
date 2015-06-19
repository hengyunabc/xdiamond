/**
 * Created by hengyunabc on 15-6-5.
 */

'use strict';

angular.module('xdiamondApp').controller("ConnectionController",
    ['$scope', '$modal', 'connections',
        function ($scope, $modal, connections) {
            $scope.connections = connections;

        }]);