/**
 * Created by hengyunabc on 15-6-3.
 */

'use strict';

//展示用户的Profile信息，组，权限等
angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('userprofile', {
                parent: 'main',
                url: '/userprofile',
                templateUrl: 'scripts/app/userprofile/userprofile.html',
                resolve: {
                    authenticateInfo: ['AuthService', function (AuthService) {
                        console.log("resolve authenticateInfo .................")
                        return AuthService.authenticateInfo();
                    }]
                },
                controller: 'UserProfileController'
            })
    });