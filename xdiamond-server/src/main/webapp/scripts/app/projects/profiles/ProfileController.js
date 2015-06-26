/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp').controller("ProfileController",
    ['$scope', '$state', 'ProfileService', 'profiles', '$modal', 'AccessLevels', 'project',
        function ($scope, $state, ProfileService, profiles, $modal, AccessLevels, project) {
            console.log('ProfileController....')
            $scope.profiles = profiles;
            $scope.project = project;
            $scope.profile = {
                projectId: project.id,
                secretKey: Math.random().toString(36).substring(2)
            };

            $scope.accessArray = [];

            for (var access in AccessLevels) {
                $scope.accessArray.push(access);
            }

            $scope.randomSecretKey = function () {
                $scope.profile.secretKey = Math.random().toString(36).substring(2);
            }

            $scope.create = function () {
                ProfileService.create($scope.profile).then(function () {
                    $state.reload();
                });
                $scope.profile = {
                    projectId: project.id,
                    secretKey: Math.random().toString(36).substring(2)
                };
            }

            $scope.popUpdateProfileModal = function (profile, size) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'scripts/app/projects/profiles/profiles.update.html',
                    controller: "ProfileUpdateController",
                    size: size,
                    resolve: {
                        profile: function () {
                            return angular.copy(profile);
                        }
                    }
                });
            }

            $scope.delete = function (profileId) {
                ProfileService.delete(profileId).then(function () {
                    $state.reload();
                });
            }

        }]);

angular.module('xdiamondApp').controller("ProfileUpdateController",
    ['$scope', '$state', '$modal', '$modalInstance', 'ProfileService', 'profile', 'AccessLevels',
        function ($scope, $state, $modal, $modalInstance, ProfileService, profile, AccessLevels) {
            $scope.profile = profile;

            $scope.accessArray = [];
            for (var access in AccessLevels) {
                $scope.accessArray.push(access);
            }

            $scope.randomSecretKey = function () {
                $scope.profile.secretKey = Math.random().toString(36).substring(2);
            }

            $scope.update = function () {
                ProfileService.patch($scope.profile).then(function () {
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