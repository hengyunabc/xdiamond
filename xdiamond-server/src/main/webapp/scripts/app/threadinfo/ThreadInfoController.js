/**
 * Created by hengyunabc on 15-6-5.
 */

'use strict';

angular.module('xdiamondApp').controller("ThreadInfoController",
    ['$scope', '$modal', 'threadInfos',
        function ($scope, $modal, threadInfos) {
            $scope.threadInfos = threadInfos;

            $scope.tempUrl = 'xxx.thml';


            $scope.threadRunnable = 0;
            $scope.threadWaiting = 0;
            $scope.threadTimedWaiting = 0;
            $scope.threadBlocked = 0;

            angular.forEach(threadInfos, function (value) {
                if (value.threadState === 'RUNNABLE') {
                    $scope.threadRunnable += 1;
                } else if (value.threadState === 'WAITING') {
                    $scope.threadWaiting += 1;
                } else if (value.threadState === 'TIMED_WAITING') {
                    $scope.threadTimedWaiting += 1;
                } else if (value.threadState === 'BLOCKED') {
                    $scope.threadBlocked += 1;
                }
            });

            $scope.threadAll = $scope.threadRunnable + $scope.threadWaiting +
                $scope.threadTimedWaiting + $scope.threadBlocked;

            $scope.getLabelClass = function (threadState) {
                if (threadState === 'RUNNABLE') {
                    return 'label-success';
                } else if (threadState === 'WAITING') {
                    return 'label-info';
                } else if (threadState === 'TIMED_WAITING') {
                    return 'label-warning';
                } else if (threadState === 'BLOCKED') {
                    return 'label-danger';
                }
            };


        }]);