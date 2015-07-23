'use strict';

var xdiamondApp = angular.module('xdiamondApp', ['ngResource', 'ui.router', 'ui.utils', 'ui.bootstrap',
    'ngCookies', 'ngTouch', 'ui.select', 'ngSanitize', 'toaster', 'ngAnimate', 'monospaced.elastic', 'ct.ui.router.extras.previous']);

xdiamondApp.constant("AccessLevels", {
    10: "Guest",
    20: "Reporter",
    30: "Developer",
    40: "Master",
    50: "Owner"
})

xdiamondApp.run(
    ['$rootScope', '$state', '$stateParams', '$timeout',
        function ($rootScope, $state, $stateParams, $timeout) {

            // It's very handy to add references to $state and $stateParams to the $rootScope
            // so that you can access them from any scope within your applications.For example,
            // <li ng-class="{ active: $state.includes('contacts.list') }"> will set the <li>
            // to active whenever 'contacts.list' or one of its decendents is active.
            $rootScope.$state = $state;
            $rootScope.$stateParams = $stateParams;

            // previous state handling
            $rootScope.previousState = {};
            $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
                // store previous state in $rootScope
                $rootScope.previousState.name = fromState.name;
                $rootScope.previousState.params = fromParams;
            });
        }
    ]
)

xdiamondApp.factory('AlertInterceptor', ['$q', '$rootScope', 'toaster', function ($q, $rootScope, toaster) {
    return {

        responseError: function (rejection) {
            console.log('Error in response ', rejection);
            // Continue to ensure that the next promise chain
            // sees an error
            // Can check auth status code here if need to
            // if (rejection.status === 403) {
            //   Show a login dialog
            //   return a value to tell controllers it has
            // been handled
            // }
            // Or return a rejection to continue the
            // promise failure chain
            if (rejection.data.error && rejection.data.error.message) {
                toaster.pop('error', rejection.data.error.message);
            }
            if (rejection.status === 401) {
                $rootScope.$state.go('login');
            }

            return $q.reject(rejection);
        }
    };
}])

xdiamondApp.config(function ($stateProvider, $urlRouterProvider, $httpProvider) {
    $httpProvider.defaults.xsrfCookieName = 'CSRF-TOKEN';
    $httpProvider.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';

    $httpProvider.interceptors.push('AlertInterceptor');

    $urlRouterProvider.otherwise("/");

    $stateProvider
        .state('site', {
            'abstract': true,
            views: {
                'header@': {
                    templateUrl: 'scripts/app/header/header.html',
                    controller: 'HeaderController'
                },
                "": {
                    template: "<div ui-view></div>"
                }
            }
            //TODO 这里的resolve，在state切换时，不会重新获取，只会获取到一次
            ,
            resolve: {
                authenticateInfo: ['AuthService', function (AuthService) {
                    console.log("resolve authenticateInfo .................")
                    return AuthService.authenticateInfo();
                }]
            }
        })

    $stateProvider.state('main', {
        url: "",
        parent: "site",
        //templateUrl: "scripts/app/main.html"
        views: {
            "": {
                templateUrl: "scripts/app/main/main.html",
                controller: "MainController"
            },
            'header@': {
                templateUrl: 'scripts/app/header/header.html',
                controller: 'HeaderController'
            }
        }
    });
})

