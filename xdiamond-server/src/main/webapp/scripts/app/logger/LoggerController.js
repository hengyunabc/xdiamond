/**
 * Created by hengyunabc on 15-6-7.
 */
'use strict';

angular.module('xdiamondApp')
    .controller('LoggerController', function ($scope, LoggerService) {
        $scope.loggers = LoggerService.findAll();

        $scope.changeLevel = function (name, level) {
            LoggerService.changeLevel({name: name, level: level}, function () {
                $scope.loggers = LogsService.findAll();
            });
        };
    });
