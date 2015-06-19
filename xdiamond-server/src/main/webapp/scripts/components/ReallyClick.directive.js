/**
 * Created by hengyunabc on 15-6-8.
 */

'use strict';
//from https://gist.github.com/asafge/7430497
angular.module('xdiamondApp')
    .directive('ngReallyClick', [function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                element.bind('click', function () {
                    var message = attrs.ngReallyMessage || "Are you sure ?";
                    if (message && confirm(message)) {
                        scope.$apply(attrs.ngReallyClick);
                    }
                });
            }
        }
    }]);

//TODO 用modal的方案当canel时，会留下一个div，貌似是和toaster 冲突了
//.directive('ngReallyClick', ['$modal',
//    function($modal) {
//
//        var ModalInstanceCtrl = function($scope, $modalInstance) {
//            $scope.ok = function() {
//                $modalInstance.close();
//            };
//
//            $scope.cancel = function() {
//                $modalInstance.dismiss('cancel');
//            };
//        };
//
//        return {
//            restrict: 'A',
//            scope: {
//                ngReallyClick:"&"
//            },
//            link: function(scope, element, attrs) {
//                element.bind('click', function() {
//                    var message = attrs.ngReallyMessage || "Are you sure ?";
//
//                    var modalHtml = '<div class="modal-body">' + message + '</div>';
//                    modalHtml += '<div class="modal-footer"><button class="btn btn-primary" ng-click="ok()">OK</button><button class="btn btn-warning" ng-click="cancel()">Cancel</button></div>';
//
//                    var modalInstance = $modal.open({
//                        template: modalHtml,
//                        controller: ModalInstanceCtrl
//                    });
//
//                    modalInstance.result.then(function() {
//                        scope.ngReallyClick();
//                    }, function() {
//                        //Modal dismissed
//                    });
//
//                });
//
//            }
//        }
//    }
//]);