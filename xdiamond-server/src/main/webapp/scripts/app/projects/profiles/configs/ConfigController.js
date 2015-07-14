/**
 * Created by hengyunabc on 15-5-20.
 */

'use strict';

angular.module('xdiamondApp').controller("ConfigController",
    ['$scope', '$state', '$filter', 'ConfigService', 'configs', 'project', 'profile', '$modal', 'resolvedConfigs',
        function ($scope, $state, $filter, ConfigService, configs, project, profile, $modal, resolvedConfigs) {
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

            $scope.batchConfigs = [];
            $scope.exampleBatchConfigs = JSON.stringify([
                {
                    "key": "key1",
                    "value": "value1",
                    "description": "key1"
                },
                {
                    "key": "host",
                    "value": "localhost",
                    "description": "host"
                }
            ], undefined, 2);

            $scope.checkBatchConfig = function (configArray) {
                console.log(configArray);
                if (Array.isArray(configArray) && configArray.length > 0) {
                    //TODO 完善这里的判断
                    $scope.batchConfigChecked = true;
                } else {
                    $scope.batchConfigChecked = false;
                }
                console.log("batchConfigChecked" + $scope.batchConfigChecked);
            }

            $scope.create = function () {
                ConfigService.create($scope.config).then(function () {
                    $state.reload();
                })
                $scope.config = {profileId: profile.id};
            }

            $scope.batch = function (batchConfigs) {
                batchConfigs.forEach(function (config, index, array) {
                    config.profileId = profile.id;
                })
                ConfigService.batch(batchConfigs).then(function () {
                    $state.reload();
                })
                $scope.batchConfigs = [];
            }

            $scope.delete = function (configId) {
                ConfigService.delete(configId).then(function () {
                    $state.reload();
                });
            };

            var filters = {};
            filters.bOnlyShowCurrentProfileConfig = false;
            filters.bNotShowCurrentProjectConfig = false;
            filters.bOnlyShowCurrentProjectConfig = false;
            filters.bNotShowCurrentProfileConfig = false;

            $scope.filters = filters;

            $scope.filtersFunc = function (resolvedConfig) {
                if (filters.bOnlyShowCurrentProfileConfig) {
                    if (resolvedConfig.config.profileId === profile.id) {
                        return resolvedConfig;
                    } else {
                        return null;
                    }
                }
                if (filters.bNotShowCurrentProfileConfig) {
                    if (resolvedConfig.config.profileId != profile.id) {
                        return resolvedConfig;
                    } else {
                        return null;
                    }
                }

                if (filters.bOnlyShowCurrentProjectConfig) {
                    if (resolvedConfig.fromProject.id === project.id) {
                        return resolvedConfig;
                    } else {
                        return null;
                    }
                }
                if (filters.bNotShowCurrentProjectConfig) {
                    if (resolvedConfig.fromProject.id != project.id) {
                        return resolvedConfig;
                    } else {
                        return null;
                    }
                }
                return resolvedConfig;
            }

            $scope.popUpdateConfigModal = function (config, size) {
                var modalInstance = $modal.open({
                    animation: true,
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

            $scope.showConfigsJSONModal = function (size) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'scripts/app/projects/profiles/configs/configs.showConfigsJSON.html',
                    controller: 'ShowConfigsJSONController',
                    size: size,
                    resolve: {
                        configs: function () {
                            return angular.copy(configs);
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


angular.module('xdiamondApp').controller("ShowConfigsJSONController",
    ['$scope', '$state', '$modal', '$modalInstance', '$filter', 'configs',
        function ($scope, $state, $modal, $modalInstance, $filter, configs) {
            $scope.configs = [];

            configs.forEach(function (config, index, array) {
                var tempConfig = {};
                tempConfig.key = config.key;
                tempConfig.value = config.value;
                tempConfig.description = config.description;
                $scope.configs.push(tempConfig);
            })
            $scope.configsJSONString = $filter('json')($scope.configs);

            $scope.ok = function () {
                $modalInstance.close();
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };

        }]);