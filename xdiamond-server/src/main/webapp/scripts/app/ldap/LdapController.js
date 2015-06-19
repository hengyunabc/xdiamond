/**
 * Created by hengyunabc on 15-5-19.
 */

'use strict';

angular.module('xdiamondApp').controller("LdapController",
    ['$scope', 'LdapService', 'ldapGroups', '$modal', '$state', 'toaster',
        function ($scope, LdapService, ldapGroups, $modal, $state, toaster) {
            console.log('LdapController....')
            $scope.ldapGroups = ldapGroups;

            $scope.addGroupAndUser = function (ldapGroup) {
                LdapService.addGroupAndUser(ldapGroup).then(function (result) {
                    toaster.pop('success', result.message, "text");
                });
            }

        }]);
