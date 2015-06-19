/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp')
    .factory('LdapService', ['$http', '$log', function ($http, $log) {
        var service = {};

        service.list = function () {
            return $http.get('api/ldap/groups').then(function (response) {
                console.log('ldapGroups:' + response.data);

                if (response.data.success) {
                    console.log(response.data.result.ldapGroups);
                    return response.data.result.ldapGroups;
                }
            })
        };

        service.addGroupAndUser = function (ldapGroup) {
            return $http.post('api/ldap/groups', ldapGroup).then(function (response) {
                if (response.data.success) {
                    $log.info('create ldapGroup success');
                    return response.data.result;
                }
            })
        };

        return service;
    }])