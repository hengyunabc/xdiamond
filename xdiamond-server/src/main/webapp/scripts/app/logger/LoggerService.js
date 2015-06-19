/**
 * Created by hengyunabc on 15-6-7.
 */

'use strict';

angular.module('xdiamondApp')
    .factory('LoggerService', function ($resource) {
        return $resource('api/logs', {}, {
            'findAll': {method: 'GET', isArray: true},
            'changeLevel': {method: 'PUT'}
        });
    });