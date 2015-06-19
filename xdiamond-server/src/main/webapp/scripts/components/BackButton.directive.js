/**
 * Created by hengyunabc on 15-6-9.
 */

'use strict';
//from https://github.com/angular-ui/ui-router/issues/92
angular.module('xdiamondApp')
    .directive('backButton', [
        '$rootScope',
        '$state',
        '$parse',
        function ($rootScope, $state, $parse) {
            return {
                restrict: 'EA',
                link: function (scope, el, attrs) {
                    var defaultState
                        , defaultStateParams;

                    el.click(function () {
                        var stateName
                            , stateParams;

                        if ($rootScope.previousState.name) {
                            stateName = $rootScope.previousState.name;
                            stateParams = $rootScope.previousState.params;
                        }
                        else {
                            stateName = defaultState;
                            stateParams = defaultStateParams;
                        }

                        if (stateName)
                            $state.go(stateName, stateParams);
                    });

                    attrs.$observe('defaultState', function () {
                        defaultState = attrs.defaultState;
                    });
                    attrs.$observe('defaultStateParams', function () {
                        defaultStateParams = $parse(attrs.defaultStateParams)(scope);
                    });

                    $rootScope.$watch('previousState', function (val) {
                        el.attr('disabled', !val.name && !defaultState);
                    });
                }
            };
        }]);