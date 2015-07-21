/**
 * Created by hengyunabc on 15-6-5.
 */

'use strict';

angular.module('xdiamondApp').controller("MetricsController",
    ['$scope', '$modal', 'metrics',
        function ($scope, $modal, metrics) {
            $scope.metrics = metrics;

            $scope.servicesStats = {};
            $scope.cachesStats = {};

            angular.forEach(metrics.timers, function (value, key) {
                //TODO 这里的service的统计不对
                if (key.indexOf('io.github.xdiamond.web') !== -1 || key.indexOf('client.controller') !== -1 || key.indexOf('io.github.xdiamond.service') !== -1) {
                    $scope.servicesStats[key] = value;
                }

                if (key.indexOf('net.sf.ehcache.Cache') !== -1) {
                    // remove gets or puts
                    var index = key.lastIndexOf('.');
                    var newKey = key.substr(0, index);

                    // Keep the name of the domain
                    index = newKey.lastIndexOf('.');
                    $scope.cachesStats[newKey] = {
                        'name': newKey.substr(index + 1),
                        'value': value
                    };
                }
            });
        }]);