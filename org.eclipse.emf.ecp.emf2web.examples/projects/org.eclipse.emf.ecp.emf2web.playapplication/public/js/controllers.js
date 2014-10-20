'use strict';

/* Controllers */

var qbFormsControllers = angular.module('myApp.controllers', []);

qbFormsControllers
    .controller('FormCtrl', ['$scope', 'GetData', 'SendData', '$routeParams',
        function($scope, Data, SendData, $routeParams) {

            //Fetch data from service and bind it to elements
            Data.getFormData($routeParams.type, $routeParams.id).then(function(data) {
                $scope.elements = data.layoutTree;
                $scope.id = data.id;
                $scope.bindings = data.bindings;
            });
            $scope.opened = false;


            $scope.openDate = function($event, element) {
                $event.preventDefault();
                $event.stopPropagation();

                element.isOpen = true;
            };

            $scope.sendData = function() {
                var data = {};

                var bindingsKeys = Object.keys($scope.bindings);

                for (var i = 0; i < bindingsKeys.length; i++) {
                    var key = bindingsKeys[i];
                    if($scope.bindings[key] != null){
                        data[key] = $scope.bindings[key];
                    }
                }
                
                SendData.sendData($routeParams.type, $scope.id, data);
            };

            $scope.validateNumber = function(value, element) {
                if (isNaN(value)) {
                    element.alerts = [];
                    var alert = {
                        type: 'danger',
                        msg: 'Must be a valid number!'
                    };
                    element.alerts.push(alert);
                    return false;
                }
                element.alerts = [];
                return true;
            };

            $scope.validateInteger = function(value, element) {
                if (isNaN(value) || (value !== "" && !(/^\d+$/.test(value)))) {
                    element.alerts = [];
                    var alert = {
                        type: 'danger',
                        msg: 'Must be a valid integer!'
                    };
                    element.alerts.push(alert);
                    return false;
                }
                element.alerts = [];
                return true;
            };
        }
    ]);


qbFormsControllers
    .controller('ListCtrl', ['$scope', 'GetData', '$routeParams',
        function($scope, Data, $routeParams) {
            //Fetch data from service and bind it to elements
            Data.getRawInstanceData($routeParams.type).then(function(data) {
                $scope.elements = data;
                $scope.type = $routeParams.type;
            });
        }
    ]);
