/**
 * Created by hengyunabc on 15-6-10.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('apidoc', {
                parent: 'main',
                url: '/apidoc',
                templateUrl: 'scripts/app/apidoc/apidoc.html'
            })

    });