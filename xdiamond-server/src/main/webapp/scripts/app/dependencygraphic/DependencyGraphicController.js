/**
 * Created by hengyunabc on 15-8-6.
 */

'use strict';

angular.module('xdiamondApp').controller("DependencyGraphicController",
    ['$scope', 'projects', 'dependencies', function ($scope, projects, dependencies) {

        // 基于准备好的dom，初始化echarts图表
        var myChart = echarts.init(document.getElementById('dependenciesechart'));

        var nodes = [];
        var links = [];

        var rootNode = {
            name: 'ROOT',
            //value: 111,
            // Custom properties
            id: 0,
            depth: 0,
            category: 2,
            ignore: true
        }
        nodes.push(rootNode);

        projects.forEach(function (project) {
            var node = {
                name: project.artifactId + '|' + project.version,
                //value: 0,
                id: project.id,
                depth: 0,
                category: 0
            }
            nodes.push(node);
        });

        dependencies.forEach(function (dependency) {
            var link = {
                source: dependency.projectId,
                target: dependency.dependencyProjectId,
                weight: 1
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
                formatter: '{b}'
            },
            toolbox: {
                show: true,
                feature: {
                    restore: {show: true},
                    magicType: {show: true, type: ['force', 'chord']},
                    saveAsImage: {show: true}
                }
            },
            legend: {
                x: 'left',
                data: ['叶子节点', '非叶子节点', '根节点']
            },
            series: [
                {
                    type: 'force',
                    name: "依赖关系",
                    ribbonType: false,
                    categories: [
                        {
                            name: '叶子节点'
                        },
                        {
                            name: '非叶子节点'
                        },
                        {
                            name: '根节点'
                        }
                    ],
                    itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                textStyle: {
                                    color: '#333'
                                }
                            },
                            nodeStyle: {
                                brushType: 'both',
                                borderColor: 'rgba(255,215,0,0.4)',
                                borderWidth: 1
                            }
                        }
                    },
                    minRadius: 10,
                    maxRadius: 2,
                    coolDown: 0.995,
                    linkSymbol: 'arrow',
                    nodes: nodes,
                    links: links,
                    steps: 1
                }
            ]
        };

        // 为echarts对象加载数据
        myChart.setOption(option);

    }]);