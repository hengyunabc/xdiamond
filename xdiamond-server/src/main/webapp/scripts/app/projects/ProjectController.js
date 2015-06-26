/**
 * Created by hengyunabc on 15-5-14.
 */

'use strict';

angular.module('xdiamondApp').controller("ProjectController", ['$scope', 'projects', 'ProjectService', '$modal', '$state', 'allGroups',
    function ($scope, projects, ProjectService, $modal, $state, allGroups) {
        console.log('ProjectContoller....');
        //TODO project要排好序
        $scope.projects = projects;
        $scope.allGroups = allGroups;

        //获取到project的ownerGroup的名字
        $scope.projects.forEach(function(project, index, projectArray){
            $scope.allGroups.every(function(group, index, groupArray){
                if(project.ownerGroup === group.id){
                    project.ownerGroupName = group.name;
                    return false;
                }
                return true;
            });
        });

        $scope.delete = function (projectId) {
            ProjectService.delete(projectId).then(function () {
                $state.reload();
            })
        }

        $scope.popNewProjectModal = function (size) {
            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'scripts/app/projects/projects.new.html',
                controller: 'ProjectNewController',
                size: size,
                resolve: {
                    allGroups: ['GroupService', function (GroupService) {
                        return GroupService.list();
                    }]
                }
            });
        };

        $scope.popUpdateProjectModal = function (project, size) {
            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'scripts/app/projects/projects.update.html',
                controller: 'ProjectUpdateController',
                size: size,
                resolve: {
                    project: function () {
                        return angular.copy(project);
                    },
                    allGroups: ['GroupService', function (GroupService) {
                        return GroupService.list();
                    }]
                }
            });
        }

    }]);

angular.module('xdiamondApp').controller("ProjectNewController",
    ['$scope', '$modal', '$modalInstance', 'ProjectService', '$state', 'allGroups',
        function ($scope, $modal, $modalInstance, ProjectService, $state, allGroups) {
            $scope.allGroups = allGroups;

            $scope.project = {
                'bAllowDependency': true,
                'bPublic': true
            };

            $scope.create = function () {
                ProjectService.create($scope.project).then(function () {
                    $scope.$state.reload();
                })
                $modalInstance.close();
            }

            $scope.ok = function () {
                $modalInstance.close();
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };

        }]);

angular.module('xdiamondApp').controller("ProjectUpdateController",
    ['$scope', '$state', '$modal', '$modalInstance', 'ProjectService', 'project', 'allGroups',
        function ($scope, $state, $modal, $modalInstance, ProjectService, project, allGroups) {
            $scope.project = project;
            $scope.allGroups = allGroups;

            allGroups.every(function (value, index, array) {
                if (value.id === project.ownerGroup) {
                    project.ownerGroup = value.id;
                    return false;
                }
                return true;
            })

            $scope.update = function () {
                ProjectService.patch($scope.project).then(function () {
                    $scope.$state.reload();
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
