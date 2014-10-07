'use strict';

angular.module('myApp', [
    'ngRoute',
    'ui.bootstrap',
    'ui.validate',
    'myApp.filters',
    'myApp.dataServices',
    'myApp.directives',
    'myApp.controllers'
]).
config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.when('/:type', {
            templateUrl: '/assets/list.html',
            controller: 'ListCtrl'
        });

        $routeProvider.when('/:type/:id', {
            templateUrl: '/assets/form.html',
            controller: 'FormCtrl'
        });
        
        $routeProvider.otherwise({
            redirectTo: '/:type'
        });
    }
]);
