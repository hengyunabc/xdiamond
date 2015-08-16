/**
 * Created by hengyunabc on 15-8-6.
 */

'use strict';

angular.module('xdiamondApp').controller("DependencyGraphicController",
    ['$scope', 'projects', 'dependencies', function ($scope, projects, dependencies) {

        var echartConfig = {
            width: 800,
            height: 800,
            leftBorder: 0,
            topBorder: 80,
            bShowLabel: true,
            bForceNodePos: true
        }
        $scope.echartConfig = echartConfig;

        var setChartOption = function () {
            // 基于准备好的dom，初始化echarts图表
            var parentElement = document.getElementById('parentChart');
            while (parentElement.firstChild) {
                parentElement.removeChild(parentElement.firstChild);
            }

            var echartElement = document.createElement("div");
            parentElement.appendChild(echartElement);

            angular.element(echartElement).css('height', echartConfig.height + 'px').css('width', echartConfig.width + 'px');

            var myChart = echarts.init(echartElement);

            var nodes = [];
            var links = [];

            var projectIdToProject = {};
            projects.forEach(function (project, index) {
                projectIdToProject[project.id] = project;
            })

            var projectIdToNodeIndex = {};

            var projectIdMaxLevelMap = {};

            //对于每一个project，查找它的上一级的依赖，每找到一个level + 1，最终最大level就是这个project的level
            var findMaxLevel = function (project) {
                console.log("findMaxLevel, project.artifactId:" + project.artifactId);
                var maxLevel = 0;
                dependencies.forEach(function (dependency, index) {
                    if (dependency.projectId === project.id) {
                        if (maxLevel === 0) {
                            maxLevel = 1;
                        }
                        var parentMaxLevel = findMaxLevel(projectIdToProject[dependency.dependencyProjectId]);
                        if (parentMaxLevel + 1 > maxLevel) {
                            maxLevel = parentMaxLevel + 1;
                        }
                    }
                });
                return maxLevel;
            };

            //记录每一个level的node的总数
            var levelNodeSizeMap = {}
            var treeLevel = 0;
            projects.forEach(function (project, index) {
                var maxLevel = findMaxLevel(project);
                if (maxLevel > treeLevel) {
                    treeLevel = maxLevel;
                }
                projectIdMaxLevelMap[project.id] = maxLevel;
                console.log('project artifactId:' + project.artifactId + ", maxLevel:" + maxLevel);
                if (maxLevel in levelNodeSizeMap) {
                    levelNodeSizeMap[maxLevel] = levelNodeSizeMap[maxLevel] + 1;
                } else {
                    levelNodeSizeMap[maxLevel] = 1;
                }
            })

            // key是level, value是当前这一level已放了的node的数量
            var currentLevelNodeSize = {};
            projects.forEach(function (project, index) {
                var projectMaxLevel = projectIdMaxLevelMap[project.id];
                if (!currentLevelNodeSize[projectMaxLevel]) {
                    currentLevelNodeSize[projectMaxLevel] = 0;
                }

                //计算出当前这一行的结点的平均距离，再计算出这个结点的具体X坐标
                var x = echartConfig.leftBorder + (echartElement.clientWidth - echartConfig.leftBorder * 2) / (levelNodeSizeMap[projectMaxLevel] + 1) * (currentLevelNodeSize[projectMaxLevel] + 1);
                currentLevelNodeSize[projectMaxLevel] = currentLevelNodeSize[projectMaxLevel] + 1;

                var y = (echartElement.clientHeight - echartConfig.topBorder * 2) / treeLevel * projectMaxLevel + echartConfig.topBorder;

                projectIdToNodeIndex[project.id] = index;
                var node = {
                    name: project.artifactId,
                    initial: [x, y],
                    fixY: echartConfig.bForceNodePos,
                    fixX: echartConfig.bForceNodePos,
                    project: project
                }
                nodes.push(node);
            });

            dependencies.forEach(function (dependency) {
                var link = {
                    source: projectIdToNodeIndex[dependency.dependencyProjectId],
                    target: projectIdToNodeIndex[dependency.projectId],
                    weight: 1,
                    name: '依赖于'
                };
                links.push(link);
            });

            var option = {
                title: {
                    text: '依赖图',
                    subtext: '项目之间的依赖关系',
                    x: 'right',
                    y: 'bottom'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: function (params) {
                        if (params.indicator2) {    // is edge
                            return params.indicator2 + ' ' + params.name + ' ' + params.indicator;
                        } else {    // is node
                            return params.data.project.artifactId + '|' + params.data.project.version
                        }
                    }
                },
                toolbox: {
                    show: true,
                    feature: {
                        restore: {show: true},
                        magicType: {show: true, type: ['force', 'chord']},
                        saveAsImage: {show: true}
                    }
                },
                series: [
                    {
                        type: 'chord',
                        sort: 'ascending',
                        sortSub: 'descending',
                        ribbonType: false,
                        radius: '60%',
                        itemStyle: {
                            normal: {
                                label: {
                                    show: echartConfig.bShowLabel,
                                    textStyle: {
                                        color: '#333'
                                    }
                                }
                            }
                        },
                        minRadius: 7,
                        maxRadius: 20,
                        linkSymbol: 'arrow',
                        nodes: nodes,
                        links: links,
                        steps: 1
                    }
                ]
            };

            // 为echarts对象加载数据
            myChart.setOption(option);
        }

        setChartOption();

        $scope.changeDivConfig = function () {
            setChartOption();
        }

    }]);