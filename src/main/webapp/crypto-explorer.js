/*
 * Copyright 2009-2012 Marcelo Morales
 *
 * Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License. under the License.
 */

//noinspection JSUnusedGlobalSymbols
function security($scope, $http) {
    $http.get('security/services/list').success(function (data) {
        $scope.serviceList = data;
    });

    $scope.selectService = function (service) {
        $http.get('security/services/describe/' + service.name).success(function (data) {
            $scope.selectedService = data;
            $scope.selectedService.service = service;
        });
        $scope.currentElement = undefined;
    };

    $scope.selectElement = function (element) {
        $scope.currentElement = element
    };

    $scope.findSecureRandoms = function (ssr) {
        $http.post('security/SecureRandom/' + ssr.algorithm + '/' + ssr.provider).success(function (data) {
            $scope.randoms = data;
            $scope.keystoreContents = undefined;
        });
    };

    $scope.listKeystore = function (ssr, data) {
        if (!data) {
            data = {};
        }
        $http.post('security/KeyStore/' + ssr.algorithm + '/' + ssr.provider + '/list', data).
            success(function (data) {
                $scope.keystoreContents = data;
                if ($scope.keystoreContents && $scope.keystoreContents.toString() == '') {
                    if (!$scope.feedback) {
                        $scope.feedback = [];
                    }
                    $scope.feedback.push("Sorry! No certificates there ;)");
                }
            }).
            error(function(data, status, headers, config) {
                $scope.keystoreContents = undefined;
                if (!$scope.feedback) {
                    $scope.feedback = [];
                }
                $scope.feedback.push(data + ' (status = ' + status + ')');
                // alert(data + status + headers + config);
            });
    };

    $scope.close = function(feedbackMessage) {
        $scope.feedback = undefined
    };
}
