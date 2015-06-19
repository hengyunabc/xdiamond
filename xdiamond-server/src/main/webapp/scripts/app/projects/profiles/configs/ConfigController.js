/**
 * Created by hengyunabc on 15-5-20.
 */

'use strict';

angular.module('xdiamondApp').controller("ConfigController",
    ['$scope', '$state', 'ConfigService', 'configs', 'project', 'profile', '$modal', 'resolvedConfigs',
        function ($scope, $state, ConfigService, configs, project, profile, $modal, resolvedConfigs) {
            console.log('ConfigController....')
            $scope.bShowAddConfigForm = 0;

            $scope.configs = configs;
            $scope.project = project;
            $scope.profile = profile;
            var params = {
                groupId: project.groupId,
                artifactId: project.artifactId,
                version: project.version,
                profile: profile.name,
                secretKey: profile.secretKey,
                format: "properties"
            }
            $scope.propertiesUrl = 'clientapi/config?' + $.param(params);
            params.format = "json";
            $scope.jsonUrl = 'clientapi/config?' + $.param(params);

            $scope.resolvedConfigs = resolvedConfigs;

            $scope.config = {profileId: profile.id};

            $scope.create = function () {
                ConfigService.create($scope.config).then(function () {
                    $state.reload();
                })
                $scope.config = {profileId: profile.id};
            }

            $scope.delete = function (configId) {
                ConfigService.delete(configId).then(function () {
                    $state.reload();
                });
            };

            $scope.popUpdateConfigModal = function (config, size) {
                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'scripts/app/projects/profiles/configs/configs.update.html',
                    controller: 'ConfigUpdateController',
                    size: size,
                    resolve: {
                        config: function () {
                            return angular.copy(config);
                        }
                    }
                });
            }

        }]);

angular.module('xdiamondApp').controller("ConfigUpdateController",
    ['$scope', '$state', '$modal', '$modalInstance', 'ConfigService', 'config',
        function ($scope, $state, $modal, $modalInstance, ConfigService, config) {
            $scope.config = config;

            $scope.update = function () {
                ConfigService.patch($scope.config).then(function () {
                    $state.reload();
                });
                $modalInstance.close();
            }

            $scope.ok = function () {
                $modalInstance.close();
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };

        }]);
