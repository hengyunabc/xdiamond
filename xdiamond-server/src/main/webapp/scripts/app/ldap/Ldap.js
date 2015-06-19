/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('ldap', {
                parent: 'main',
                abstract: true,
                url: '/ldap',
                templateUrl: 'scripts/app/ldap/ldap.html',
                resolve: {
                    ldapGroups: ['LdapService', '$stateParams', function (LdapService, $stateParams) {
                        return LdapService.list();
                    }]
                },
                controller: 'LdapController'
            })
            .state('ldap.list', {
                parent: 'ldap',
                url: '',
                templateUrl: 'scripts/app/ldap/ldap.list.html'
            })
    });