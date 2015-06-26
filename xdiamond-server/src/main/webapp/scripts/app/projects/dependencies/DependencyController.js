/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp').controller("DependencyController",
    ['$scope', '$state', 'DependencyService', 'dependencies', 'project', 'projects', '$modal', '$filter',
        function ($scope, $state, DependencyService, dependencies, project, projects, $modal, $filter) {
            console.log('DependencyController....')

            $scope.uniqueFilter = $filter('unique');

            $scope.project = project;
            $scope.dependencies = dependencies;

            $scope.selected = {};

            //删掉和当前projectId相同的project，因为不能依赖自身
            //TODO 过滤掉已经依赖的project
            //TODO 过滤掉不允许被依赖的project
            $scope.projects = projects.filter(function (value, index, array) {
                return (value.id.toString() != project.id.toString());
            })

            //$scope.projects = projects;

            $scope.dependencyProjects = [];
            for (var i in dependencies) {
                for (var j in projects) {
                    if (dependencies[i].dependencyProjectId === projects[j].id) {
                        projects[j].dependencyId = dependencies[i].id;
                        $scope.dependencyProjects.push(projects[j]);
                    }
                }
            }


            $scope.addDependency = function () {
                DependencyService.create({
                    projectId: project.id,
                    dependencyProjectId: $scope.selected.byVersion.id
                }).then(function () {
                    $state.reload();
                })
            }

            $scope.delete = function (id) {
                DependencyService.delete(id).then(function () {
                    $state.reload();
                });
            };

            $scope.openNewProjectModal = function (size) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'scripts/app/projects/dependencies/dependencies.new.html',
                    controller: 'DependencyNewController',
                    size: size,
                    resolve: {
                        items: function () {
                            return $scope.items;
                        }
                    }
                });
            }

        }]);