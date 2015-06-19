/**
 * Created by hengyunabc on 15-5-12.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('login', {
                parent: "site",
                url: '/login',
                templateUrl: 'scripts/app/users/login.html',
                //resolve: {
                //    xxx: 'yyy'
                //},
                //resolve: {
                //    authenticateInfo: ['UserService', function (UserService) {
                //        console.log("ppppppppppppppppppppppppppppppp")
                //        return UserService.authenticateInfo();
                //    }]
                //},
                //resolve:{
                //    roles:['RoleService', '$stateParams', function(RoleService, $stateParams){
                //        console.log("rrrrrrrrrrrrrrrrrrrrrrrrrr")
                //        return RoleService.list();
                //    }]
                //},
                controller: 'LoginController'
                //views:{
                //    '':{
                //        templateUrl: 'scripts/app/users/login.html'
                //    }
                //}
            });
    });
